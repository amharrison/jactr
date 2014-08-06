package org.jactr.core.utils;

/*
 * default logging
 */
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * default impl of IAdaptable that will handle the object's class heirarchhy,
 * plus supports the addition of IAdaptableFactory(s) with hard, soft, and no
 * caching (create on each call) supported.
 * 
 * @author harrison
 */
public class DefaultAdaptable implements IAdaptable
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultAdaptable.class);

  final private Map<Class, IAdaptableFactory>     _adapters  = new HashMap<Class, IAdaptableFactory>();

  final private Map<Class, Object>                _hardCache = new HashMap<Class, Object>();

  final private Map<Class, SoftReference<Object>> _softCache = new HashMap<Class, SoftReference<Object>>();

  
  public <T> T getAdapter(Class<T> adapterClass)
  {
    if (adapterClass.isAssignableFrom(getClass())) return (T) this;
    
    // check our hard cache
    Object adapter = _hardCache.get(adapterClass);
    if (adapter != null) return (T) adapter;

    SoftReference reference = _softCache.get(adapterClass);
    if (reference != null) adapter = reference.get();

    if (adapter != null) return (T) adapter;

    /*
     * nothing was cached, let's dig deeper.
     */
    IAdaptableFactory factory = _adapters.get(adapterClass);
    if (factory != null)
    {
      adapter = factory.adapt(this);
      if (factory.shouldCache())
        _hardCache.put(adapterClass, adapter);
      else if (factory.shouldSoftCache())
        _softCache.put(adapterClass, new SoftReference(adapter));
    }
    

    return (T) adapter;
  }
  
  
  public void addAdapterFactory(IAdaptableFactory factory, Class[] forClasses)
  {
    for (Class c : forClasses)
    {
      IAdaptableFactory prior = _adapters.put(c, factory);
      if (prior != factory)
        LOGGER.debug(String.format("Replacing factory for %s [%s] with [%s]",
            c, prior, factory));
    }
  }

  public void removeAdapterFactory(IAdaptableFactory factory, Class[] forClasses)
  {
    for (Class c : forClasses)
    {
      IAdaptableFactory prior = _adapters.remove(c);
      if (prior != factory)
        LOGGER.debug(String.format(
            "Removed factory for %s [%s] was not the expected [%s]", c, prior,
            factory));
    }
  }

}
