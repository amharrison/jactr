package org.jactr.tools.itr.pspace;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;
import org.jactr.tools.itr.IParameterModifier;
import org.jactr.tools.itr.analysis.IAnalyzer;
import org.w3c.dom.Document;

@Deprecated
public class OrthogonalParameterSpaceSearcher implements IIterativeRunListener,
    IParameterized
{
  /**
   * Logger definition
   */
  static private transient Log                  LOGGER = LogFactory
                                                           .getLog(OrthogonalParameterSpaceSearcher.class);

  static public final String                    URI    = "ConfigURL";

  private Collection<IAnalyzer>                 _analyzers;

  private SortedMap<String, IParameterModifier> _parameterModifiers;

  private SortedMap<String, Integer>            _parameterRanges;

  private SortedMap<String, Integer>            _currentIndicies;

  private SortedMap<String, String>             _currentValues;

  private Map<IAnalyzer, Object[]>              _analysisResults;

  private int                                   _totalSize;

  private int                                   _averageBlockSize;

  private int                                   _currentOffset;

  
  /**
   * return all the linear offsets for all the values of parameterName given the other indicies
   * @param parameterName
   * @param otherIndicies
   * @param dimensions
   * @return
   */
  static public Collection<Integer> computeLinearOffsets(String parameterName, SortedMap<String, Integer> otherIndicies, SortedMap<String, Integer> dimensions)
  {
    ArrayList<Integer> offsets = new ArrayList<Integer>(dimensions.get(parameterName));
    
    TreeMap<String, Integer> indicies = new TreeMap<String, Integer>(otherIndicies);
    for(int i=0;i<dimensions.get(parameterName);i++)
    {
      indicies.put(parameterName, i);
      offsets.add(computeLinearOffset(indicies, dimensions));
    }
    
    return offsets;
  }
  
  static public int computeLinearOffset(SortedMap<String, Integer> indicies,
      SortedMap<String, Integer> dimensions)
  {
    int index = 0;
    Iterator<Map.Entry<String, Integer>> iterator = dimensions.entrySet()
        .iterator();
    int multiplier = 1;
    while (iterator.hasNext())
    {
      Map.Entry<String, Integer> entry = iterator.next();

      index += multiplier * indicies.get(entry.getKey());

      multiplier *= dimensions.get(entry.getKey());
    }

    index = Math.min(index, multiplier - 1);
    return index;
  }
  
  static public SortedMap<String, Integer> computeIndicies(int linearOffset,
      SortedMap<String, Integer> dimensions)
  {
    TreeMap<String, Integer> indicies = new TreeMap<String, Integer>();

    /*
     * we reverse it because the algorithim works backwards
     */
    TreeMap<String, Integer> reversedRanges = new TreeMap<String, Integer>(
        Collections.reverseOrder());
    reversedRanges.putAll(dimensions);
    
    int size = 1;
    /*
     * first get the total linear size
     */
    for(Integer dim : reversedRanges.values())
      size  *= dim;
    

    Iterator<Map.Entry<String, Integer>> iterator = reversedRanges.entrySet()
        .iterator();
    while (iterator.hasNext())
    {
      Map.Entry<String, Integer> entry = iterator.next();
      size /= entry.getValue();

      int index = linearOffset / size;

      index = Math.min(index, entry.getValue() - 1);

      linearOffset -= index * size;

      indicies.put(entry.getKey(), index);
    }

    return Collections.unmodifiableSortedMap(indicies);
  }
  
  

  public OrthogonalParameterSpaceSearcher()
  {
    _analyzers = new ArrayList<IAnalyzer>();
    _parameterModifiers = new TreeMap<String, IParameterModifier>();
  }

  public void add(IAnalyzer analyzer)
  {
    _analyzers.add(analyzer);
  }

  public void add(IParameterModifier pModifier)
  {
    _parameterModifiers.put(pModifier.getParameterName(), pModifier);
  }

  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException
  {

  }

  public void postRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    for (IAnalyzer analyzer : _analyzers)
      analyzer.collectData(currentRunIndex, models);
  }

  public void preBuild(int currentRunIndex, int totalRuns,
      Collection<CommonTree> modelDescriptors) throws TerminateIterativeRunException
  {
    SortedMap<String, Integer> currentIndicies = computeIndicies(
        currentRunIndex, totalRuns);
    boolean changed = false;

    if (_currentIndicies == null)
      changed = true;
    else
      /*
       * are the new indicies different from the previous?
       */
      for (Map.Entry<String, Integer> entry : _currentIndicies.entrySet())
        if (entry.getValue() != currentIndicies.get(entry.getKey()))
        {
          changed = true;
          break;
        }

    if (changed)
    {
      if (_currentIndicies != null) stopCollection();

      _currentIndicies = currentIndicies;

      _currentOffset = computeLinearOffset(_currentIndicies, _parameterRanges);

      /*
       * stash the parameter values..
       */
      TreeMap<String, String> parameterValues = new TreeMap<String, String>();
      for (Map.Entry<String, Integer> entry : _currentIndicies.entrySet())
        parameterValues.put(entry.getKey(), _parameterModifiers.get(
            entry.getKey()).getParameterValues().get(entry.getValue()));

      _currentValues = Collections.unmodifiableSortedMap(parameterValues);

      /*
       * signal the start of the current run
       */
      for (IAnalyzer analyzer : _analyzers)
        analyzer.startCollection(currentRunIndex, _currentIndicies,
            _currentValues);
    }

    /*
     * modify the parameters of the models
     */
    for (Map.Entry<String, Integer> entry : _currentIndicies.entrySet())
      for (CommonTree modelDesc : modelDescriptors)
        _parameterModifiers.get(entry.getKey()).setParameter(modelDesc,
            entry.getValue());
  }

  /**
   * tell the analyzers to stop collecting for this block and store
   * the analysis results
   */
  protected void stopCollection()
  {
    for (IAnalyzer analyzer : _analyzers)
    {
      Object rtn = analyzer.stopCollection(_currentIndicies, _currentValues);
      _analysisResults.get(analyzer)[_currentOffset] = rtn;
    }
  }

  public void preRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {

  }

  /**
   * takes the current run index (1..N) and returns a map of parameter name
   * indicies
   * 
   * @param currentRun
   * @param totalRuns
   * @return
   */
  protected SortedMap<String, Integer> computeIndicies(int currentRun,
      int totalRuns)
  {
    currentRun--; // 1 indexed
    currentRun = Math.min(currentRun / _averageBlockSize, _totalSize);

    return computeIndicies(currentRun, _parameterRanges);
  }

  public void start(int totalRuns) throws TerminateIterativeRunException
  {
    _totalSize = 1;
    TreeMap<String, Integer> pRanges = new TreeMap<String, Integer>();
    for (IParameterModifier modifier : _parameterModifiers.values())
    {
      int size = modifier.getParameterValues().size();
      pRanges.put(modifier.getParameterName(), size);
      _totalSize *= size;
    }

    _averageBlockSize = totalRuns / _totalSize;
    _parameterRanges = Collections.unmodifiableSortedMap(pRanges);

    if (_averageBlockSize == 0)
      throw new RuntimeException(
          "Insufficient trials to explore parameter space, need at least " +
              _totalSize);

    /*
     * tell the analyzers what the parameter space looks like
     */
    _analysisResults = new HashMap<IAnalyzer, Object[]>(_analyzers.size());
    for (IAnalyzer analyzer : _analyzers)
    {
      _analysisResults.put(analyzer, new Object[_totalSize]);
      analyzer.start(_parameterRanges);
    }
  }

  public void stop()
  {
    stopCollection();

    SortedMap<String, List<String>> pValues = new TreeMap<String, List<String>>();
    for (IParameterModifier modifier : _parameterModifiers.values())
      pValues.put(modifier.getParameterName(), Collections
          .unmodifiableList(modifier.getParameterValues()));
    pValues = Collections.unmodifiableSortedMap(pValues);

    for (IAnalyzer analyzer : _analyzers)
      analyzer.stop(_analysisResults.get(analyzer), pValues);
    
    _analysisResults.clear();
    _parameterModifiers.clear();
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

        Document doc = PSpaceParser.load(uri);
        for (IAnalyzer analyzer : PSpaceParser.buildAnalyzers(doc))
          add(analyzer);

        for (IParameterModifier modifier : PSpaceParser.buildModifiers(doc))
          add(modifier);
      }
      catch (Exception e)
      {
        throw new RuntimeException("Could not configure from " + value, e);
      }
  }

  public void preLoad(int currentRunIndex, int totalRuns) throws TerminateIterativeRunException
  {
    // TODO Auto-generated method stub

  }

}
