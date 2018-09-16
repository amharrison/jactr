package org.jactr.core.utils.collections;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.impl.factory.Maps;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastMapFactory
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER   = LogFactory
                                                         .getLog(FastMapFactory.class);

  static private RecyclableFactory<Map<?, ?>> _factory = new AbstractThreadLocalRecyclableFactory<Map<?, ?>>() {

                                                       @SuppressWarnings({
      "unchecked", "rawtypes"                         })
                                                       @Override
                                                       protected void cleanUp(
                                                             Map<?, ?> obj)
                                                       {
                                                         obj.clear();
                                                       }

                                                       @Override
                                                         protected Map<?, ?> instantiate(
                                                           Object... params)
                                                       {
                                                           return Maps.mutable
                                                               .empty();
                                                       }

                                                       @Override
                                                       protected void release(
                                                             Map<?, ?> map)
                                                       {

                                                       }

                                                     };

  @SuppressWarnings("unchecked")
  static public <K, V> Map<K, V> newInstance()
  {
    return (Map<K, V>) _factory.newInstance();
  }

  static public <K, V> void recycle(Map<K, V> set)
  {
    _factory.recycle(set);
  }
}
