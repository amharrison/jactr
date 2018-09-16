package org.jactr.tools.utility;

/*
 * default logging
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.six.ISubsymbolicProduction6;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;

/**
 * tracks the expected utility of a set of productions over time
 * 
 * @author harrison
 */
public class UtilityTracker implements IInstrument, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                     = LogFactory
                                                                    .getLog(UtilityTracker.class);

  static public final String         FILE_NAME_PARAM            = "FileName";

  static public final String         PATTERN_PARAM              = "Pattern";

  static public final String         TRACK_INSTANTIATIONS_PARAM = "TrackInstantiations";

  private Map<IProduction, Integer>  _productionColumnIndices;

  private Collection<Pattern>        _productionNamePatterns;

  private IProceduralModuleListener  _prodListener;

  private ArrayList<Double>          _utilities;

  private File                       _currentFile;

  private String                     _fileName;

  private PrintWriter                _outputStream;

  private IModel                     _attachedTo;

  /**
   * log the production's utility (sans noise) or the instantiation's utility
   * (with noise)
   */
  private boolean                    _logInstantiationUtility   = true;

  public UtilityTracker()
  {
    _productionColumnIndices = new HashMap<IProduction, Integer>();
    _productionNamePatterns = new ArrayList<Pattern>();
    _utilities = new ArrayList<Double>();
    _utilities.add(null); // for time

    _prodListener = new ProceduralModuleListenerAdaptor() {

      private final SortedMap<String, IProduction> _sorter = new TreeMap<String, IProduction>();

      public void conflictSetAssembled(ProceduralModuleEvent pme)
      {
        for (IProduction instantiation : pme.getProductions())
        {
          if (!_logInstantiationUtility)
            instantiation = ((IInstantiation) instantiation).getProduction();

          if (isTracked(instantiation) || matchesPattern(instantiation))
            _sorter.put(instantiation.getSymbolicProduction().getName(),
                instantiation);
        }

        /**
         * we use the sorter to maintain a consistent (alphabetical) order of
         * the productions as they are added, otherwise they will be initially
         * tracked in their initial utility order
         */
        if (_sorter.size() != 0)
        {
          for (IProduction production : _sorter.values())
          {
            if (!isTracked(production)) addTrackedProduction(production);
            logUtility(production);
          }

          flushUtility(pme.getSimulationTime());
          _sorter.clear();
        }
      }
    };
  }

  private boolean isTracked(IProduction production)
  {
    if (production instanceof IInstantiation)
      production = ((IInstantiation) production).getProduction();

    return _productionColumnIndices.containsKey(production);
  }

  private boolean matchesPattern(IProduction production)
  {
    for (Pattern pattern : _productionNamePatterns)
      if (pattern.matcher(production.getSymbolicProduction().getName())
          .matches()) return true;
    return false;
  }

  private void addTrackedProduction(IProduction production)
  {
    if (production instanceof IInstantiation)
      production = ((IInstantiation) production).getProduction();

    _productionColumnIndices.put(production, _utilities.size());
    _utilities.add(null);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding production " + production + " tracking "
          + (_utilities.size() - 1));
  }

  private void logUtility(IProduction production)
  {
    double utility = ((ISubsymbolicProduction6) production
        .getSubsymbolicProduction()).getExpectedUtility();

    /**
     * perfectly legit if there has been no learning and we're tracking the
     * production and not the instantiation
     */
    if (Double.isNaN(utility))
      utility = ((ISubsymbolicProduction6) production
          .getSubsymbolicProduction()).getUtility();

    /*
     * make sure we get the column index right..
     */
    if (production instanceof IInstantiation)
      production = ((IInstantiation) production).getProduction();

    _utilities.set(_productionColumnIndices.get(production), utility);
  }

  private void flushUtility(double when)
  {
    _utilities.set(0, when);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < _utilities.size(); i++)
    {
      Double out = _utilities.set(i, null);

      sb.append((out != null ? out : ""));
      sb.append("\t");
    }
    _outputStream.println(sb.toString());
  }

  public void initialize()
  {
    // TODO Auto-generated method stub

  }

  synchronized public void install(IModel model)
  {
    if (_attachedTo != null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("UtilityTracker is already attached to " + _attachedTo
            + ". Can only be attched to one model.");
      return;
    }

    model.getProceduralModule().addListener(_prodListener,
        ExecutorServices.INLINE_EXECUTOR);

    try
    {
      _currentFile = File.createTempFile(_fileName, ".tmp");
      _currentFile.deleteOnExit();
      _outputStream = new PrintWriter(new BufferedWriter(new FileWriter(
          _currentFile)));
    }
    catch (IOException e)
    {
      throw new IllegalStateException("Could not create temp file", e);
    }

    _attachedTo = model;
  }

  synchronized public void uninstall(IModel model)
  {
    if(_attachedTo!=model)
      return;
    
    _attachedTo = null;
    model.getProceduralModule().removeListener(_prodListener);

    /*
     * now that we are done, merge the file with a header file
     */
    _outputStream.flush();
    _outputStream.close();

    try
    {
      _outputStream = new PrintWriter(new BufferedWriter(new FileWriter(
          new File(ACTRRuntime.getRuntime().getWorkingDirectory(), _fileName))));

      /*
       * output the header
       */
      ArrayList<String> header = new ArrayList<String>();
      header.add("Time");
      for (int i = 0; i < _productionColumnIndices.size(); i++)
        header.add("");

      for (Map.Entry<IProduction, Integer> entry : _productionColumnIndices
          .entrySet())
        header.set(entry.getValue(), entry.getKey().getSymbolicProduction()
            .getName());

      for (String itm : header)
      {
        _outputStream.print(itm);
        _outputStream.print("\t");
      }
      _outputStream.println();

      BufferedReader reader = new BufferedReader(new FileReader(_currentFile));
      while (reader.ready())
        _outputStream.println(reader.readLine());

      reader.close();
      _outputStream.flush();
      _outputStream.close();

      _currentFile.delete();
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Could not generate file output file ", e);
    }
  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(FILE_NAME_PARAM, PATTERN_PARAM,
        TRACK_INSTANTIATIONS_PARAM);
  }

  public void setParameter(String key, String value)
  {
    if (FILE_NAME_PARAM.equalsIgnoreCase(key))
      _fileName = value;
    else if (PATTERN_PARAM.equalsIgnoreCase(key))
      _productionNamePatterns.add(Pattern.compile(value));
    else if (TRACK_INSTANTIATIONS_PARAM.equalsIgnoreCase(key))
      _logInstantiationUtility = Boolean.parseBoolean(value);

  }

}
