package org.jactr.tools.itr;

/*
 * default logging
 */
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.itr.ortho.ISlice;
import org.jactr.tools.itr.ortho.ISliceListener;

/**
 * saves models of terminal runs and restores them to the run directory so that
 * longitudinal runs can be bootstrapped from prior runs
 * 
 * @author harrison
 */
public class LongitudinalParameterSetModifier extends ParameterSetModifier
    implements ISliceListener
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER         = LogFactory
                                                        .getLog(LongitudinalParameterSetModifier.class);

  static private String              TEMP_DIRECTORY = ".longitudinal";

  private Map<String, String>        _nameToFile    = new TreeMap<String, String>();

  private List<Collection<File>>     _priorModels   = new LinkedList<Collection<File>>();

  /**
   * called by the parser.. should probably just be a parameter for simplicities
   * sake.
   * 
   * @param modelName
   * @param modelFile
   */
  public void associate(String modelName, String modelFile)
  {
    _nameToFile.put(modelName.toLowerCase(), modelFile);
  }

  /**
   * delete the models from working dir..
   */
  public void deleteModels()
  {
    /*
     * delete all models in working directory..
     */
    File root = new File(System.getProperty("user.dir"));
    for (String path : _nameToFile.values())
    {
      File modelInRoot = new File(root, path);
      if (modelInRoot.exists()) delete(modelInRoot);
    }
  }

  /**
   * serializes the models to the temp directory
   * 
   * @param models
   */
  public void copyModels(Collection<IModel> models, long iteration)
  {
    ICodeGenerator generator = CodeGeneratorFactory.getCodeGenerator("jactr");
    Collection<File> serialized = new ArrayList<File>(_nameToFile.size());
    for (IModel model : models)
      try
      {
        String modelName = model.getName().toLowerCase();
        String modelFile = _nameToFile.get(modelName);
        if (modelFile == null)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("Could not find binding for " + modelName);
          continue;
        }
        CommonTree modelDescriptor = ASTResolver.toAST(model, true);
        File root = new File(TEMP_DIRECTORY, String.format("%07d", iteration));
        root.mkdirs();

        File fp = new File(root, modelFile);
        if (!fp.getParentFile().equals(root)) fp.getParentFile().mkdirs();

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Copying %s to %s", model, fp));
        PrintWriter pw = new PrintWriter(new FileWriter(fp));
        for (StringBuilder line : generator.generate(modelDescriptor, true))
          pw.println(line);
        pw.close();

        /**
         * if the file is nested, store the parent, not the file itself
         */
        if (!fp.getParentFile().equals(root))
          serialized.add(fp.getParentFile());
        else
          serialized.add(fp);
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to generate file ", e);
      }

    if (serialized.size() == 0)
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn("No models were saved, potential longitudinal configuration error. Check your modelName=modelFile bindings.");

    _priorModels.add(serialized);
  }

  @Override
  public void setParameter(CommonTree modelDescriptor, int parameterValueIndex)
  {
    super.setParameter(modelDescriptor, parameterValueIndex);

  }

  /**
   * is this the first slice in a longitudinal run?
   * 
   * @param parameterValue
   * @return
   */
  public boolean isFirstSlice(String parameterValue)
  {
    return getParameterValues().indexOf(parameterValue) == 0;
  }

  public boolean isLastSlice(String parameterValue)
  {
    List<String> parameterValues = getParameterValues();
    return parameterValues.indexOf(parameterValue) == parameterValues.size() - 1;
  }

  /**
   * called before loading, we get a chance to copy models in..
   */
  public void startSlice(ISlice slice)
  {
    String stage = slice.getProperty(getParameterDisplayName()).toString();
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Starting slice %s", stage));
    /*
     * let's just make sure we have a clean slate..
     */
    if (isFirstSlice(stage))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "First longitudinal stage %s, cleaning (%s)", stage, slice));
      deleteModels();
    }
  }

  public void startIteration(ISlice slice, long iteration,
      Collection<IModel> models)
  {

  }

  public void stopIteration(ISlice slice, long iteration,
      Collection<IModel> models)
  {
    String stage = slice.getProperty(getParameterDisplayName()).toString();
    if (!isFirstSlice(stage))
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Cleaning working dir");
      deleteModels();
    }
    /*
     * and copy
     */
    copyModels(models, iteration - slice.getFirstIteration());

    /*
     * we copy in the models for the next iteration within this slice.. if this
     * is the last iteration, we'll leave the coping for the next slice to
     * stopSlice()
     */
    if (!isFirstSlice(stage) && slice.getLastIteration() != iteration)
    {
      long offsetIteration = iteration - slice.getFirstIteration() + 1;
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "Need to copy files from %07d for stage %s (%s)", offsetIteration,
            stage, slice));

      moveFiles(offsetIteration);
    }
  }

  private void moveFiles(long offsetIteration)
  {
    Collection<File> files = _priorModels.remove(0);
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Moving %s to root for iteration %d", files,
          offsetIteration));

    for (File file : files)
    {
      File dest = new File(file.getName());
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Moving %s to %s", file, dest));
      if (!file.renameTo(dest))
        LOGGER.error(String.format("Failed to move %s to %s", file, dest));
    }
  }

  public void stopSlice(ISlice slice)
  {
    String stage = slice.getProperty(getParameterDisplayName()).toString();
    if (isLastSlice(stage))
    {
      /*
       * clean out the temp dir..
       */
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Was final stage slice, deleting temp");
      delete(new File(TEMP_DIRECTORY));
    }
    else
    {
      /*
       * we just finished the last iteration of a slice, but there are more to
       * come and since we can't get in before the models are loaded, we move
       * the files here, w/ an index of 0
       */
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("slice completed, moving longitudinal models for start of next stage");
      moveFiles(0);
    }
  }

  private void delete(File root)
  {
    File[] children = root.listFiles();
    if (children != null) for (File child : children)
      if (!child.isDirectory())
        try
        {
          if (LOGGER.isDebugEnabled()) LOGGER.debug("Deleting " + child);
          child.delete();
        }
        catch (Exception e)
        {
          LOGGER.error("Could not delete " + child, e);
        }
      else
        delete(child);

    root.delete();
  }
}
