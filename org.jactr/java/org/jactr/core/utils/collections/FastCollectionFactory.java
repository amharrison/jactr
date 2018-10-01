package org.jactr.core.utils.collections;

import java.util.Collection;

import org.eclipse.collections.impl.factory.Lists;
import org.jactr.core.utils.recyclable.CollectionPooledObjectFactory;
import org.jactr.core.utils.recyclable.PooledRecycableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastCollectionFactory
{
  static private RecyclableFactory<Collection<?>> _factory = new PooledRecycableFactory<Collection<?>>(
      new CollectionPooledObjectFactory<>(Lists.mutable::empty));

  @SuppressWarnings("unchecked")
  static public <T> Collection<T> newInstance()
  {
    return (Collection<T>) _factory.newInstance();
  }

  static public <T> void recycle(Collection<T> set)
  {
    _factory.recycle(set);
  }
}
