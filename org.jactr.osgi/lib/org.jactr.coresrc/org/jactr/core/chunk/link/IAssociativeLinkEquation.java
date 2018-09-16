package org.jactr.core.chunk.link;

import org.jactr.core.model.IModel;

/*
 * default logging
 */

/**
 * equation code for the learning and setting of associative links
 * 
 * @author harrison
 */
public interface IAssociativeLinkEquation
{
  public double computeLearnedStrength(IAssociativeLink link);

  public double computeDefaultStrength(IAssociativeLink link);

  /**
   * reset all the associatve strengths in the model
   * 
   * @param model
   */
  public void resetStrengths(IModel model);
}
