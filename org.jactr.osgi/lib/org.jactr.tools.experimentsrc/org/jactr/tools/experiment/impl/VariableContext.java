package org.jactr.tools.experiment.impl;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VariableContext implements IVariableContext
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VariableContext.class);

  private final IVariableContext     _parent;

  private final Map<String, Object>  _values;

  public VariableContext()
  {
    this(null);
  }

  public VariableContext(IVariableContext parent)
  {
    _parent = parent;
    _values = new TreeMap<String, Object>();
  }

  public Object get(String variableName)
  {
    if (_values.containsKey(variableName))
      return _values.get(variableName);
    else if (_parent != null) return _parent.get(variableName);

    return null;
  }

  public void getAll(Map<String, Object> container)
  {
    container.putAll(_values);
    if (_parent != null) _parent.getAll(container);
  }

  public IVariableContext getParent()
  {
    return _parent;
  }

  public boolean isSet(String variableName)
  {
    if (_values.containsKey(variableName)) return true;
    if (_parent != null) return _parent.isSet(variableName);

    return false;
  }

  public void set(String variableName, Object value)
  {
    _values.put(variableName, value);
  }
  
  public IVariableContext duplicate()
  {
    VariableContext duplicate = new VariableContext();
    getAll(duplicate._values);
    return duplicate;
  }

}
