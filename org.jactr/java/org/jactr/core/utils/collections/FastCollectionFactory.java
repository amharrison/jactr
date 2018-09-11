package org.jactr.core.utils.collections;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.impl.factory.Lists;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastCollectionFactory
{
  /**
   * Logger definition
   */
  static private final transient Log             LOGGER   = LogFactory
                                                              .getLog(FastCollectionFactory.class);

  static private RecyclableFactory<Collection<?>> _factory = new AbstractThreadLocalRecyclableFactory<Collection<?>>() {

                                                            @SuppressWarnings({
      "unchecked", "rawtypes"                              })
                                                            @Override
                                                            protected void cleanUp(
                                                                 Collection<?> obj)
                                                            {
                                                                obj.clear();
                                                            }

                                                            @Override
                                                             protected Collection<?> instantiate(
                                                                Object... params)
                                                            {
                                                               return Lists.mutable
                                                                   .empty();
                                                            }

                                                              @Override
                                                              protected void release(
                                                                 Collection<?> obj)
                                                              {

                                                              }

                                                          };

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
