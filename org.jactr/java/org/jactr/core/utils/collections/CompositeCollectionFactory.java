package org.jactr.core.utils.collections;

/*
 * default logging
 */
import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.collections.collection.CompositeCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class CompositeCollectionFactory
{
  /**
   * Logger definition
   */
  static private final transient Log             LOGGER   = LogFactory
                                                              .getLog(CompositeCollectionFactory.class);

  static private RecyclableFactory<CompositeCollection> _factory = new AbstractThreadLocalRecyclableFactory<CompositeCollection>() {

                                                            @SuppressWarnings({
      "unchecked", "rawtypes"                              })
                                                            @Override
                                                            protected void cleanUp(
                                                                       CompositeCollection obj)
                                                            {
                                                              for (Object set : obj
                                                                  .getCollections())
                                                              {
                                                                obj.removeComposited((Collection) set);
                                                                if (set instanceof ConcurrentSkipListSet)
                                                                  SkipListSetFactory
                                                                      .recycle((ConcurrentSkipListSet) set);
                                                              }
                                                            }

                                                            @Override
                                                                   protected CompositeCollection instantiate(
                                                                Object... params)
                                                            {
                                                                     return new CompositeCollection();
                                                            }

                                                                   @Override
                                                                   protected void release(
                                                                       CompositeCollection obj)
                                                                   {
                                                                     // noop

                                                                   }

                                                          };

  static public CompositeCollection newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(CompositeCollection set)
  {
    _factory.recycle(set);
  }
}
