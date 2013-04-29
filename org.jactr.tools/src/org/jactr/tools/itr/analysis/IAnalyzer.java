package org.jactr.tools.itr.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.jactr.core.model.IModel;
import org.jactr.tools.itr.fit.ModelFitStatistics;

/**
 * interface for a pluggable model run analysis tool. This is used
 * in conjunction with ***** in order to collect model fit data.
 * @author harrison
 *
 */
@Deprecated
public interface IAnalyzer
{
  /**
   * called before any runs start.
   * @param dimensions a map of parameter names and the number of values that will be searched
   */
  public void start(SortedMap<String, Integer> dimensions);
  
  /**
   * all of the runs have completed. this is your primary point of output. all the results
   * from {@link #stopCollection(SortedMap, SortedMap)} are returned as well as all
   * the possible parameter values
   * @param analysisResults
   * @param parameterValues
   */
  public void stop(Object[] analysisResults, SortedMap<String, List<String>> parameterValues);

  /**
   * called at the start of any given cluster of iterative runs, marking the start
   * of a new block that should be used in the calculation of model fit
   * @param iteration current iteration
   * @param totalIteration how many iterations will be used in this cluster
   * @param stats model fit statistic to use
   * @param parameterValues the current set of parameter values
   */
  public void startCollection(int iteration, SortedMap<String,Integer> parameterIndicies, SortedMap<String, String> parameterValues);
  
  /**
   * 
   * @param iteration
   * @param stats
   * @param models
   */
  public void collectData(int iteration, Collection<IModel> models);
  
  /**
   * called at the end of the analysis block. This method should run whatever analyses are necessary.
   * While outputing at this stage is possible, typically you will return the results of your
   * analysis for output at {@link #stop(Object[], SortedMap)}
   * @param parameterIndicies
   * @param parameterValues
   * @return result of the analysis
   */
  public Object stopCollection(SortedMap<String,Integer> parameterIndicies, SortedMap<String, String> parameterValues);
}
