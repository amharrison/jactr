package org.jactr.tools.experiment.impl;

import java.util.Map;

/*
 * default logging
 */

public interface IVariableContext
{

  public Object get(String variableName);
  
  public void set(String variableName, Object value);
  
  public boolean isSet(String variableName);
  
  public IVariableContext getParent();
  
  public void getAll(Map<String, Object> container);
  
  /**
   * make a deep copy of the context values. The returned copy
   * will have no parents, but have all the values
   * @return
   */
  public IVariableContext duplicate();
}
