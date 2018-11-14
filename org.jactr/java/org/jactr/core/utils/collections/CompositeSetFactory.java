package org.jactr.core.utils.collections;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.collections.set.CompositeSet;
import org.jactr.core.utils.recyclable.CollectionPooledObjectFactory;
import org.jactr.core.utils.recyclable.PooledRecycableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class CompositeSetFactory
{

  static private RecyclableFactory<CompositeSet>       _factory = new PooledRecycableFactory<CompositeSet>(
      new CollectionPooledObjectFactory<CompositeSet>(CompositeSet::new,
          CompositeSetFactory::clear));

  static public CompositeSet newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(CompositeSet set)
  {
    _factory.recycle(set);
  }

  @SuppressWarnings("rawtypes")
  static private void clear(CompositeSet set)
  {
    for (Object composite : set.getCollections())
      set.removeComposited((Collection) composite);
  }
}
