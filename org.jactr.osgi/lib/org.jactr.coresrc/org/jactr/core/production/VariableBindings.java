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
import org.jactr.core.production.bindings.BindingFactory;

/**
 * storage for variable bindings plus where they originated. If the binding
 * originated with a buffer (i.e., =retrieval) {@link #getSource(String)} will
 * return the buffer. If it is a slot container (i.e., symbolic chunk, muscle
 * state, status buffer) it will be that <br/>
 * If this binding is to be used in an instantiated, it should not recycle
 * bindings.
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

  private boolean                     _recycleBindings = false;

  public VariableBindings()
  {
    this(false);
  }

  public VariableBindings(boolean recycleBindings)
  {
    _recycleBindings = recycleBindings;
  }


  /**
   * make a copy, without any recycling of bindings
   * 
   * @param bindings
   */
  public VariableBindings(VariableBindings bindings)
  {
    this(false);
    copy(bindings);
  }

  // @Override
  // public VariableBindings clone()
  // {
  // // VariableBindings rtn = new VariableBindings();
  // VariableBindings rtn = VariableBindingsFactory.newInstance();
  // rtn._bindings.putAll(_bindings);
  //
  // return rtn;
  // }

  public void clear()
  {
    if (_recycleBindings) for (Object[] binding : _bindings.values())
      BindingFactory.recycle(binding);

    _bindings.clear();
  }


  /**
   * copy the bindings from bindings into this.
   * 
   * @param bindings
   */
  public void copy(VariableBindings bindings)
  {
    /*
     * can't just putAll as that would mean multiple VariableBindings have the
     * same Object[] for recycling. Bad things happen.
     */
    for (Map.Entry<String, Object[]> binding : bindings._bindings.entrySet())
    {
      Object[] valueAndSrc = binding.getValue();
      bind(binding.getKey(), valueAndSrc[0], valueAndSrc[1]);
    }
  }

  public Set<String> getVariables()
  {
    return Collections.unmodifiableSet(_bindings.keySet());
  }

  public void bind(String variableName, Object value, Object variableSource)
  {

    Object[] binding = null;
    if (_recycleBindings)
      binding = BindingFactory.newInstance(value, variableSource);
    else
    {
      binding = new Object[2];
      binding[0] = value;
      binding[1] = variableSource;
    }

    Object[] oldBinding = _bindings.put(variableName.toLowerCase(), binding);

    if (oldBinding != null && _recycleBindings)
      BindingFactory.recycle(oldBinding);
  }

  public void bind(String variableName, Object value)
  {
    bind(variableName, value, null);
  }

  public void unbind(String variableName)
  {
    Object[] binding = _bindings.remove(variableName.toLowerCase());
    if (binding != null && _recycleBindings) BindingFactory.recycle(binding);
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
