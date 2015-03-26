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
  static private final transient Log             LOGGER    = LogFactory
                                                               .getLog(DefaultAdaptable.class);

  final private Map<Class<?>, IAdaptableFactory> _adapters = new HashMap<Class<?>, IAdaptableFactory>();

  private Map<Class<?>, Object>                  _hardCache;                                            // =
                                                                                                         // new
                                                                                                         // HashMap<Class<?>,
                                                                                                         // Object>();

  private Map<Class<?>, SoftReference<?>>        _softCache;                                            // =
                                                                                                         // new
                                                                                                         // HashMap<Class<?>,
                                                                                                         // SoftReference<?>>();

  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Class<T> adapterClass)
  {
    if (adapterClass.isAssignableFrom(getClass())) return (T) this;

    // check our hard cache
    Object adapter = null;
    if (_hardCache != null) adapter = _hardCache.get(adapterClass);

    if (adapter == null)
    {
      SoftReference<?> reference = null;
      if (_softCache != null) reference = _softCache.get(adapterClass);
      if (reference != null) adapter = reference.get();
    }

    /*
     * nothing was cached, let's dig deeper.
     */
    IAdaptableFactory factory = _adapters.get(adapterClass);
    if (adapter == null && factory != null)
    {
      adapter = factory.adapt(this);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Created %s for %s from %s", adapter,
            adapterClass.getSimpleName(), factory.getClass().getSimpleName()));

      if (factory.shouldCache())
      {
        if (_hardCache == null) _hardCache = new HashMap<Class<?>, Object>();

        _hardCache.put(adapterClass, adapter);
      }
      else if (factory.shouldSoftCache())
      {
        if (_softCache == null)
          _softCache = new HashMap<Class<?>, SoftReference<?>>();
        _softCache.put(adapterClass, new SoftReference<Object>(adapter));
      }
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Returning %s.%d for %s adaptation of %s.%d",
          adapter, adapter != null ? adapter.hashCode() : 0,
          adapterClass.getSimpleName(), this, hashCode()));

    return (T) adapter;
  }

  /**
   * replaces the this adapters and caches with those from adaptable. This is
   * used during representation merging. i.e., a merged chunk would adopt the
   * master's adaptable contents
   * 
   * @param adaptable
   */
  protected void adopt(DefaultAdaptable adaptable)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s.%d Adopting adaptables from %s.%d", this,
          hashCode(), adaptable, adaptable.hashCode()));

    _adapters.clear();
    if (_hardCache != null) _hardCache.clear();
    if (_softCache != null) _softCache.clear();
    _adapters.putAll(adaptable._adapters);
    if (adaptable._hardCache != null)
    {
      if (_hardCache == null) _hardCache = new HashMap<Class<?>, Object>();
      _hardCache.putAll(adaptable._hardCache);
    }
    if (adaptable._softCache != null)
    {
      if (_softCache == null)
        _softCache = new HashMap<Class<?>, SoftReference<?>>();

      _softCache.putAll(adaptable._softCache);
    }
  }

  public void addAdapterFactory(IAdaptableFactory factory, Class<?>[] forClasses)
  {
    for (Class<?> c : forClasses)
    {
      IAdaptableFactory prior = _adapters.put(c, factory);
      if (prior != factory)
        LOGGER.debug(String.format("Replacing factory for %s [%s] with [%s]",
            c, prior, factory));
    }
  }

  public void removeAdapterFactory(IAdaptableFactory factory,
      Class<?>[] forClasses)
  {
    for (Class<?> c : forClasses)
    {
      IAdaptableFactory prior = _adapters.remove(c);
      if (prior != factory)
        LOGGER.debug(String.format(
            "Removed factory for %s [%s] was not the expected [%s]", c, prior,
            factory));
    }
  }

}
