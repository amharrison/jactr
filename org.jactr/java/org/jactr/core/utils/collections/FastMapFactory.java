package org.jactr.core.utils.collections;

import java.util.Map;

import org.eclipse.collections.impl.factory.Maps;
import org.jactr.core.utils.recyclable.MapPooledObjectFactory;
import org.jactr.core.utils.recyclable.PooledRecycableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastMapFactory
{
  static private RecyclableFactory<Map<?, ?>> _factory = new PooledRecycableFactory<Map<?, ?>>(
      new MapPooledObjectFactory<>(Maps.mutable::empty));

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
