package org.jactr.core.slot;


/**
 * interface defining a slot who's name is a variable itself.
 * 
 * @author harrison
 *
 */
public interface IVariableNameSlot extends ISlot
{

  /**
   * does this slot's name correspond to an unresolved variable
   * @return
   */
  public boolean isVariableName();
}
