package org.jactr.modules.pm.common.memory.impl;

/*
 * default logging
 */
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.ISimulationObject;

/**
 * used as a proxy invocationHandler to override/append the type field of an
 * IAfferentObject
 * 
 * @author harrison
 */
public class AdaptableTypeInvocationHandler implements InvocationHandler
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AdaptableTypeInvocationHandler.class);

  private final ISimulationObject    _proxied;

  private final Set<String>          _typePropertyNames;

  public AdaptableTypeInvocationHandler(ISimulationObject toBeProxied,
      String... typePropertyNames)
  {
    _proxied = toBeProxied;
    _typePropertyNames = new TreeSet<String>();
    for (String propertyName : typePropertyNames)
      _typePropertyNames.add(propertyName);
  }

  /**
   * proxy {@link ISimulationObject#getProperty(String)},
   * {@link ISimulationObject#getPropertyMap()},
   * {@link ISimulationObject#getProperties()}, specifically looking for
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable
  {

    return null;
  }

}
