package org.jactr.core.production;

/*
 * default logging
 */
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * storage for variable bindings plus where they originated. If the binding
 * originated with a buffer (i.e., =retrieval) {@link #getSource(String)} will
 * return the buffer. If it is a slot container (i.e., symbolic chunk, muscle
 * state, status buffer) it will be that
 * 
 * @author harrison
 */
public class VariableBindings
{
  /**
   * Logger definition
   */
  static private final transient Log  LOGGER    = LogFactory
                                                    .getLog(VariableBindings.class);

  /**
   * keyed on name [value, originalSlot, container]
   */
  private final Map<String, Object[]> _bindings = new TreeMap<String, Object[]>();

  public VariableBindings()
  {
    this(Collections.EMPTY_MAP);
  }

  public VariableBindings(Map<String, Object> sourceBindings)
  {
    for (Map.Entry<String, Object> entry : sourceBindings.entrySet())
      bind(entry.getKey(), entry.getValue());
  }

  @Override
  public VariableBindings clone()
  {
    VariableBindings rtn = new VariableBindings();
    rtn._bindings.putAll(_bindings);

    return rtn;
  }

  public void clear()
  {
    _bindings.clear();
  }

  public void copy(VariableBindings bindings)
  {
    _bindings.putAll(bindings._bindings);
  }

  public Set<String> getVariables()
  {
    return Collections.unmodifiableSet(_bindings.keySet());
  }

  public void bind(String variableName, Object value, Object variableSource)
  {
    _bindings.put(variableName.toLowerCase(), new Object[] { value,
        variableSource });
  }

  public void bind(String variableName, Object value)
  {
    bind(variableName, value, null);
  }

  public void unbind(String variableName)
  {
    _bindings.remove(variableName.toLowerCase());
  }

  public boolean isBound(String variableName)
  {
    return _bindings.containsKey(variableName.toLowerCase());
  }

  public Object get(String variableName)
  {
    Object[] rtn = _bindings.get(variableName.toLowerCase());
    if (rtn != null) return rtn[0];
    return null;
  }

  public Object getSource(String variableName)
  {
    Object[] rtn = _bindings.get(variableName.toLowerCase());
    if (rtn != null) return rtn[1];
    return null;
  }

}
