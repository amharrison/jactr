package org.jactr.tools.itr.ortho;

import java.util.Collection;

/**
 * called when all the slices have been collected gives a chance to perform
 * cross-cutting analyses
 * @author harrison
 *
 */
public interface ISliceIntegrator
{
  /**
   * notification that a slice has been completed. the slices may 
   * not necessarily arrive in order
   * @param analysis
   */
  public void completed(ISliceAnalysis analysis);
  
  /**
   * called when all the slices are complete
   * @param analyses
   */
  public void integrate(Collection<ISliceAnalysis> analyses);
}
