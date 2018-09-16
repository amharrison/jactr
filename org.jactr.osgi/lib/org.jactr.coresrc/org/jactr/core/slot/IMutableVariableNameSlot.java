package org.jactr.core.slot;

/*
 * default logging
 */

/**
 * interface for a variable named slot that can have the name
 * resolved.
 * @author harrison
 *
 */
public interface IMutableVariableNameSlot extends IVariableNameSlot
{

  /**
   * set the name of the slot, fully resolving it. if the name
   * has already been resolved (i.e. {@link #isVariableName()} returns
   * false), the name is not changed.
   * 
   * @param name
   */
  public void setName(String name);
}
