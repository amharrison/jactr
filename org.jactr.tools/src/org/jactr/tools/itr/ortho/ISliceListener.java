package org.jactr.tools.itr.ortho;

import java.util.Collection;

import org.jactr.core.model.IModel;

/*
 * default logging
 */

public interface ISliceListener
{
  /**
   * called when a new slice is starting
   * @param slice
   */
  public void startSlice(ISlice slice);
  
  /**
   * called just before the models are to be run, but after
   * their parameters have been set
   * @param models
   */
  public void startIteration(ISlice slice, long iteration,
      Collection<IModel> models);
  
  /**
   * 
   * @param iteration
   * @param models
   */
  public void stopIteration(ISlice slice, long iteration,
      Collection<IModel> models);
  
  /**
   * called after a slice has completed
   * @param slice
   */
  public void stopSlice(ISlice slice);
}
