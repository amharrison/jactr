package org.jactr.core.utils.collections;

import java.util.Set;

import org.eclipse.collections.impl.factory.Sets;
import org.jactr.core.utils.recyclable.CollectionPooledObjectFactory;
import org.jactr.core.utils.recyclable.PooledRecycableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastSetFactory
{
  static private RecyclableFactory<Set<?>> _factory = new PooledRecycableFactory<Set<?>>(
      new CollectionPooledObjectFactory<>(Sets.mutable::empty));

  @SuppressWarnings("unchecked")
  static public <T> Set<T> newInstance()
  {
    return (Set<T>) _factory.newInstance();
  }

  static public <T> void recycle(Set<T> set)
  {
    _factory.recycle(set);
  }
}
