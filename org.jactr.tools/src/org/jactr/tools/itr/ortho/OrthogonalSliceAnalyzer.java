package org.jactr.tools.itr.ortho;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.tools.itr.IParameterModifier;
import org.jactr.tools.itr.IParameterSetModifier;
import org.jactr.tools.itr.LongitudinalParameterSetModifier;
import org.jactr.tools.itr.ortho.impl.Slice;
import org.jactr.tools.itr.ortho.impl.SliceAnalysis;
import org.w3c.dom.Document;

public class OrthogonalSliceAnalyzer implements IIterativeRunListener,
    IParameterized
{
  /**
   * Logger definition
   */
  static private transient Log                  LOGGER          = LogFactory
                                                                    .getLog(OrthogonalSliceAnalyzer.class);

  static public final String                    URI             = "ConfigURL";

  final private Collection<ISliceAnalyzer>      _analyzers;

  final private Collection<ISliceIntegrator>    _integrators;

  final private Map<String, IParameterModifier> _parameterModifiers;

  final private Collection<SliceAnalysis>       _analyses;

  final private Collection<SliceAnalysis>       _completedAnalyses;

  final private Collection<ISliceListener>      _sliceListeners = new ArrayList<ISliceListener>();

  private File                                  _reportRoot;

  private PrintWriter                           _report;

  private String                                _reportName     = "OrthoSpaceSearch";

  private LongitudinalParameterSetModifier      _longitudinalModifier;

  public OrthogonalSliceAnalyzer()
  {
    _analyzers = new ArrayList<ISliceAnalyzer>();
    _parameterModifiers = new LinkedHashMap<String, IParameterModifier>();
    _analyses = new ArrayList<SliceAnalysis>();
    _integrators = new ArrayList<ISliceIntegrator>();
    _completedAnalyses = new ArrayList<SliceAnalysis>();
  }

  public void add(ISliceListener listener)
  {
    _sliceListeners.add(listener);
  }

  public void add(ISliceAnalyzer analyzer)
  {
    _analyzers.add(analyzer);
    if (analyzer instanceof ISliceListener) add((ISliceListener) analyzer);
  }

  public void add(ISliceIntegrator integrator)
  {
    _integrators.add(integrator);
    if (integrator instanceof ISliceListener) add((ISliceListener) integrator);
  }

  public void add(IParameterModifier pModifier)
  {
    if (pModifier instanceof LongitudinalParameterSetModifier)
      // we'll add it last at start
      _longitudinalModifier = (LongitudinalParameterSetModifier) pModifier;
    else
      _parameterModifiers.put(pModifier.getParameterDisplayName(), pModifier);

    if (pModifier instanceof ISliceListener) add((ISliceListener) pModifier);
  }

  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException
  {

  }

  public void postRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {

    ISlice slice = getSlice(currentRunIndex);

    for (ISliceListener listener : _sliceListeners)
      listener.stopIteration(slice, currentRunIndex, models);

    /*
     * should we run the analysis?
     */
    if (currentRunIndex == slice.getLastIteration())
    {
      for (ISliceListener listener : _sliceListeners)
        listener.stopSlice(slice);

      LOGGER.debug("Finished " + slice);
      runAnalyzers(currentRunIndex);
    }

  }

  public void preBuild(int currentRunIndex, int totalRuns,
      Collection<CommonTree> modelDescriptors) throws TerminateIterativeRunException
  {
    /*
     * set the parameters
     */
    ISlice slice = getSlice(currentRunIndex);

    /**
     * if no longitudinal, just set all the parameters. if there is a
     * longitudinal, only set the population parameters if longitudinal is
     * first..
     */
    if (_longitudinalModifier == null)
      for (Map.Entry<String, Object> parameter : slice.getParameterValues()
          .entrySet())
        for (CommonTree modelDescriptor : modelDescriptors)
        {
          IParameterModifier pm = _parameterModifiers.get(parameter.getKey());
          if (pm == null) continue;
          pm.setParameter(modelDescriptor, parameter.getValue().toString());
        }
    else
    {

      Map<String, Object> parameters = slice.getParameterValues();
      for (Map.Entry<String, Object> parameter : parameters.entrySet())
        if (parameter.getKey().equals(
            _longitudinalModifier.getParameterDisplayName())
            || _longitudinalModifier.isFirstSlice(parameters.get(
                _longitudinalModifier.getParameterDisplayName()).toString()))
          for (CommonTree modelDescriptor : modelDescriptors)
          {
            IParameterModifier pm = _parameterModifiers.get(parameter.getKey());
            if (pm == null) continue;
            pm.setParameter(modelDescriptor, parameter.getValue().toString());
          }
    }

    /*
     * should we dump the models too?
     */
    if (slice.getFirstIteration() == currentRunIndex)
    {
      SliceAnalysis analysis = (SliceAnalysis) getSliceAnalysis(currentRunIndex);
      File sliceDir = new File(_reportRoot, "" + slice.getId());
      File modelsDir = new File(sliceDir, "models");
      modelsDir.mkdirs();

      ICodeGenerator generator = CodeGeneratorFactory.getCodeGenerator("jactr");
      for (CommonTree model : modelDescriptors)
        try
        {
          String modelName = ASTSupport.getName(model);
          File fp = File.createTempFile("model-", ".jactr", modelsDir);
          PrintWriter pw = new PrintWriter(new FileWriter(fp));
          for (StringBuilder line : generator.generate(model, true))
            pw.println(line);
          pw.close();
          analysis
              .addModel(modelName, modelsDir.getName() + "/" + fp.getName());
        }
        catch (IOException ioe)
        {

        }
    }
  }

  private void runAnalyzers(int currentRun)
  {
    ISliceAnalysis analysis = getSliceAnalysis(currentRun);

    _completedAnalyses.add((SliceAnalysis) analysis);

    for (ISliceAnalyzer analyzer : _analyzers)
      analyzer.analyze(analysis);

    write();

    /*
     * notify integrators
     */
    for (ISliceIntegrator integrator : _integrators)
      integrator.completed(analysis);
  }

  private void write()
  {
    try
    {
      _report = new PrintWriter(new FileWriter(new File(_reportRoot,
          "report.orthoxml")));
    }
    catch (IOException e)
    {
      throw new IllegalStateException("Cannot save report.xml ", e);
    }

    /*
     * dump the header
     */
    _report.print("<analyses date=\"");
    _report.print(DateFormat.getDateTimeInstance(DateFormat.SHORT,
        DateFormat.LONG).format(new Date()));
    _report.print("\" name=\"");
    _report.print(_reportName);
    _report.println("\">");
    _report.println("");

    /*
     * dump all the completed analyses
     */
    for (SliceAnalysis analysis : _completedAnalyses)
    {
      analysis.write(_report);
      _report.println();
    }

    /*
     * dump the rest of the file
     */
    _report.println("</analyses>");
    _report.close();
  }

  public void preRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    /*
     * current run working directory, during iterative runs, this is a sub
     * directory of user dir.
     */
    File workingDir = ACTRRuntime.getRuntime().getWorkingDirectory();

    Slice slice = (Slice) getSlice(currentRunIndex);
    slice.addWorkingDirectory(workingDir.getName());

    for (ISliceListener listener : _sliceListeners)
      listener.startIteration(slice, currentRunIndex, models);
  }

  /**
   * return the slice for this run iteration
   * 
   * @param currentRun
   * @return
   */
  private ISlice getSlice(int currentRun)
  {
    return getSliceAnalysis(currentRun).getSlice();
  }

  private ISliceAnalysis getSliceAnalysis(int currentRun)
  {
    for (SliceAnalysis analysis : _analyses)
    {
      ISlice slice = analysis.getSlice();
      if (slice.getFirstIteration() <= currentRun
          && currentRun <= slice.getLastIteration()) return analysis;
    }
    throw new IllegalStateException("No slice associated with " + currentRun);
  }

  public void start(int totalRuns) throws TerminateIterativeRunException
  {
    if (_longitudinalModifier != null)
      _parameterModifiers.put(_longitudinalModifier.getParameterDisplayName(),
          _longitudinalModifier);

    long totalSize = 1;
    Map<String, Long> pRanges = new LinkedHashMap<String, Long>();
    for (IParameterModifier modifier : _parameterModifiers.values())
    {
      long size = modifier.getParameterValues().size();
      pRanges.put(modifier.getParameterDisplayName(), size);
      totalSize *= size;
    }

    long averageBlockSize = totalRuns / totalSize;

    if (averageBlockSize == 0)
      throw new RuntimeException(
          "Insufficient trials to explore parameter space, need at least "
              + totalSize);

    _reportRoot = new File(System.getProperty("user.dir"), "report");
    _reportRoot.mkdirs();

    /*
     * we precreate all the slices..
     */
    long blockCount = 0;
    for (long i = 1; i <= totalRuns; i += averageBlockSize)
    {
      blockCount++;
      long last = Math.min(i + averageBlockSize - 1, totalRuns);
      if (totalRuns - last < averageBlockSize) last = totalRuns;

      Slice slice = new Slice(blockCount, i, last);

      /*
       * compute the indicies into the parameter space
       */
      Map<String, Long> currentIndicies = computeIndicies(blockCount - 1,
          pRanges);

      /*
       * set the parameter values for that slice
       */
      for (Map.Entry<String, Long> entry : currentIndicies.entrySet())
      {
        IParameterModifier parameterModifier = _parameterModifiers.get(entry
            .getKey());
        int parameterValueIndex = entry.getValue().intValue();

        slice.setProperty(entry.getKey(), parameterModifier
            .getParameterValues().get(parameterValueIndex));

        if (parameterModifier instanceof IParameterSetModifier)
        {
          Map<String, String> nestedParametres = ((IParameterSetModifier) parameterModifier)
              .getNestedParameterValues(parameterValueIndex);
          for (Map.Entry<String, String> pV : nestedParametres.entrySet())
            slice.setProperty(pV.getKey(), pV.getValue());
        }
      }

      LOGGER.debug("Created " + slice);

      // File analysisRoot = new File(_reportRoot, "" + blockCount);

      SliceAnalysis analysis = new SliceAnalysis(slice, "report/" + blockCount);
      _analyses.add(analysis);

      if (last == totalRuns) i = totalRuns;
    }

  }

  public void stop()
  {
    _longitudinalModifier = null;
    _analyzers.clear();
    _parameterModifiers.clear();

    /*
     * and notify the integrators
     */
    Collection<ISliceAnalysis> analyses = Collections
        .unmodifiableCollection(new ArrayList<ISliceAnalysis>(_analyses));
    for (ISliceIntegrator integrator : _integrators)
      integrator.integrate(analyses);

    _analyses.clear();
    _integrators.clear();
    _completedAnalyses.clear();
  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Collections.singleton(URI);
  }

  public Collection<String> getSetableParameters()
  {
    return Collections.singleton(URI);
  }

  public void setParameter(String key, String value)
  {
    if (URI.equalsIgnoreCase(key))
      try
      {
        URI uri = null;
        URL resource = getClass().getClassLoader().getResource(value);

        if (resource != null)
          uri = resource.toURI();
        else
        {
          uri = new URI(value);
          if (!uri.isAbsolute())
            uri = new File(System.getProperty("user.dir")).toURI().resolve(
                value);
        }

        Document doc = Parser.load(uri);
        for (ISliceAnalyzer analyzer : Parser.buildAnalyzers(doc))
          add(analyzer);

        for (IParameterModifier modifier : Parser.buildModifiers(doc))
          add(modifier);

        for (ISliceIntegrator integrator : Parser.buildIntegrators(doc))
          add(integrator);

        _reportName = doc.getDocumentElement().getAttribute("name");
      }
      catch (Exception e)
      {
        throw new RuntimeException("Could not configure from " + value, e);
      }
  }

  static private Map<String, Long> computeIndicies(long linearOffset,
      Map<String, Long> dimensions)
  {
    LinkedHashMap<String, Long> indicies = new LinkedHashMap<String, Long>();

    int size = 1;
    /*
     * first get the total linear size
     */
    for (Long dim : dimensions.values())
      size *= dim;

    Iterator<Map.Entry<String, Long>> iterator = dimensions.entrySet()
        .iterator();
    while (iterator.hasNext())
    {
      Map.Entry<String, Long> entry = iterator.next();
      size /= entry.getValue();

      long index = linearOffset / size;

      index = Math.min(index, entry.getValue() - 1);

      linearOffset -= index * size;

      indicies.put(entry.getKey(), index);
    }

    return Collections.unmodifiableMap(indicies);
  }

  public void preLoad(int currentRunIndex, int totalRuns) throws TerminateIterativeRunException
  {
    ISlice slice = getSlice(currentRunIndex);

    if (slice.getFirstIteration() == currentRunIndex)
    {
      LOGGER.debug("Starting " + slice);
      for (ISliceListener listener : _sliceListeners)
        listener.startSlice(slice);
    }
  }

}
