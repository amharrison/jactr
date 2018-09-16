/*
 * Created on Jan 21, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.utils.parameter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ParameterHandler<T> implements IParameterHandler<T>
{

  @SuppressWarnings("unchecked")
  static private Map<Class, IParameterHandler> _handlerMap = Collections.synchronizedMap(new HashMap<Class, IParameterHandler>());
  

  static public NumericParameterHandler numberInstance()
  {
    return (NumericParameterHandler) instance(NumericParameterHandler.class);
  }
  

  static public ClassNameParameterHandler classInstance()
  {
    return (ClassNameParameterHandler) instance(ClassNameParameterHandler.class);
  }

  static public BooleanParameterHandler booleanInstance()
  {
    return (BooleanParameterHandler) instance(BooleanParameterHandler.class);
  }

  @SuppressWarnings("unchecked")
  static public IParameterHandler instance(Class type)
  {
    IParameterHandler handler = null;
    synchronized (_handlerMap)
    {
      handler = _handlerMap.get(type);
      if (handler == null)
          try
          {
            handler = (IParameterHandler) type.newInstance();
            _handlerMap.put(type, handler);
          }
          catch (Exception e)
          {
            throw new RuntimeException("Could not instantiate handler for "
                + type.getName(), e);
          }
    }
    return handler;
  }

  /** 
   * @see org.jactr.core.utils.parameter.IParameterHandler#coerce(String)
   */
  abstract public T coerce(String value);
  
  public String toString(T value)
  {
    if(value!=null)
      return value.toString();
    return null;
  }
}