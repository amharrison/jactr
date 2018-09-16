package org.jactr.core.chunk.basic;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;

/*
 * default logging
 */

/**
 * snippet of code that allows one to add/remove/replace elements in the
 * activation calculations. It is assumed that activation sources are
 * independent. This is also a stateless class as it will be used for all chunks
 * 
 * @author harrison
 */
public interface IActivationParticipant
{

  /**
   * simple named identifier for logging purposes
   * 
   * @return
   */
  public String getName();

  /**
   * as the name implies, not only should this compute the activation, but also
   * set the appropriate internal member variable. The chunk's write lock will
   * have already been acquired.
   * 
   * @param chunk
   * @param model
   * @return
   */
  public double computeAndSetActivation(IChunk chunk, IModel model);
}
