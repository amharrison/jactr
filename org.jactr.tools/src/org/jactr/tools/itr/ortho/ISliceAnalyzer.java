package org.jactr.tools.itr.ortho;

/*
 * default logging
 */

/**
 * code that is responsible for analyzing a particular slice
 * 
 * @author harrison
 */
public interface ISliceAnalyzer
{
  /**
   * analyze the specified slice and return some value. This value
   * is entirely modeler dependent, however it should likely be
   * serializable so that the results can be collected and transmitted
   * back when this finally supports distributed execution
   * @param slice
   * @return
   */
  public Object analyze(ISliceAnalysis sliceAnalysis);
}
