package org.jactr.core.utils.collections;

import java.util.List;

import org.eclipse.collections.impl.factory.Lists;
import org.jactr.core.utils.recyclable.CollectionPooledObjectFactory;
import org.jactr.core.utils.recyclable.PooledRecycableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastListFactory
{

  static private RecyclableFactory<List<?>> _factory = new PooledRecycableFactory<List<?>>(
      new CollectionPooledObjectFactory<>(Lists.mutable::empty));

  @SuppressWarnings("unchecked")
  static public <T> List<T> newInstance()
  {
    return (List<T>) _factory.newInstance();
  }

  static public <T> void recycle(List<T> set)
  {
    _factory.recycle(set);
  }
}
