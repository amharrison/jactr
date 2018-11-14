package org.jactr.core.utils.collections;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.collections.collection.CompositeCollection;
import org.jactr.core.utils.recyclable.CollectionPooledObjectFactory;
import org.jactr.core.utils.recyclable.PooledRecycableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class CompositeCollectionFactory
{

  static private RecyclableFactory<CompositeCollection> _factory = new PooledRecycableFactory<CompositeCollection>(
      new CollectionPooledObjectFactory<CompositeCollection>(
          CompositeCollection::new,
          CompositeCollectionFactory::clear));

  static public CompositeCollection newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(CompositeCollection set)
  {
    _factory.recycle(set);
  }

  @SuppressWarnings("rawtypes")
  static private void clear(CompositeCollection set)
  {
    for (Object composite : set.getCollections())
      set.removeComposited((Collection) composite);
  }
}
