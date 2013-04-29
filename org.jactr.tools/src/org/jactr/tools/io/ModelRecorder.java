/*
 * Created on Mar 13, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.tools.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.io.resolver.ASTResolver;

/**
 * A simple instrument that saves the state of the model before it starts and
 * after it stops.
 * 
 * @author developer
 */
public class ModelRecorder implements IInstrument, IParameterized
{
  /**
   * logger definition
   */
  static private final Log   LOGGER                   = LogFactory
                                                          .getLog(ModelRecorder.class);

  static final public String SAVE_AS_PARAM            = "SaveAsExtension";

  static final public String START_PARAM              = "StartDirectory";

  static final public String STOP_PARAM               = "StopDirectory";

  static final public String TRIM_CONTRIBUTIONS_PARAM = "TrimModuleContributions";

  private String             _saveAsExtension         = "jactr";

  private String             _startDirectory          = "start";

  private String             _stopDirectory           = "stop";

  private boolean            _trimModuleContributions = true;

  /**
   * IInstruments should always have a zero arg constructor
   */
  public ModelRecorder()
  {
  }

  /**
   * @see org.jactr.instrument.IInstrument#initialize()
   */
  public void initialize()
  {
    // NoOp
  }

  /**
   * @see org.jactr.instrument.IInstrument#install(org.jactr.core.model.IModel)
   */
  public void install(IModel model)
  {
    model.addListener(new ModelListenerAdaptor() {

      @Override
      public void modelStarted(ModelEvent me)
      {
        saveModel(me.getSource(), _startDirectory, _saveAsExtension,
            _trimModuleContributions);
      }

      @Override
      public void modelStopped(ModelEvent me)
      {
        saveModel(me.getSource(), _stopDirectory, _saveAsExtension,
            _trimModuleContributions);
      }
    }, ExecutorServices.INLINE_EXECUTOR);
  }

  /**
   * @see org.jactr.instrument.IInstrument#uninstall(org.jactr.core.model.IModel)
   */
  public void uninstall(IModel model)
  {
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (SAVE_AS_PARAM.equalsIgnoreCase(key)) return _saveAsExtension;
    if (START_PARAM.equalsIgnoreCase(key)) return _startDirectory;
    if (STOP_PARAM.equalsIgnoreCase(key)) return _stopDirectory;
    if (TRIM_CONTRIBUTIONS_PARAM.equalsIgnoreCase(key))
      return "" + _trimModuleContributions;
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    return Arrays.asList(new String[] { SAVE_AS_PARAM, START_PARAM, STOP_PARAM,
        TRIM_CONTRIBUTIONS_PARAM });
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * this chunk of code will permit the ModelRecorder to be configured when the
   * environment.xml file is loaded
   * 
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (SAVE_AS_PARAM.equalsIgnoreCase(key))
      _saveAsExtension = value;
    else if (START_PARAM.equalsIgnoreCase(key))
      _startDirectory = value;
    else if (STOP_PARAM.equalsIgnoreCase(key))
      _stopDirectory = value;
    else if (TRIM_CONTRIBUTIONS_PARAM.equalsIgnoreCase(key))
      _trimModuleContributions = Boolean.parseBoolean(value);
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue what to do with " + key + "=" + value);
  }

  /*
   * here is where we actually save the model
   */
  static public void saveModel(IModel model, String directory,
      String extension, boolean trim)
  {

    ICodeGenerator generator = CodeGeneratorFactory.getCodeGenerator(extension);

    if (generator == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("Could not find a code generator for " + extension);
      return;
    }

    String destination = null;
    try
    {
      /*
       * first things first, transform the model
       */
      CommonTree modelDescriptor = ASTResolver.toAST(model, true);
      
      /*
       * ok, now let's create the directory
       */
      File root = new File(ACTRRuntime.getRuntime().getWorkingDirectory(),
          directory);
      destination = root.getCanonicalPath();

      if (!root.exists()) root.mkdirs();

      File toBeWritten = new File(root, model.getName() + "." + extension);
      destination = toBeWritten.getCanonicalPath();

      PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
          toBeWritten)));

      /*
       * we dump the full model
       */
      for (StringBuilder line : generator.generate(modelDescriptor, trim))
        pw.println(line.toString());

      pw.flush();
      pw.close();
    }
    catch (Exception ioe)
    {
      LOGGER
          .error("Could not write " + model + " to " + destination + " ", ioe);
    }
  }

}
