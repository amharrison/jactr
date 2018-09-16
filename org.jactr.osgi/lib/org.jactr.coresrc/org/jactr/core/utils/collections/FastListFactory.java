package org.jactr.core.utils.collections;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.impl.factory.Lists;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastListFactory
{
  /**
   * Logger definition
   */
  static private final transient Log             LOGGER   = LogFactory
                                                              .getLog(FastListFactory.class);

  static private RecyclableFactory<List<?>> _factory = new AbstractThreadLocalRecyclableFactory<List<?>>() {

                                                            @SuppressWarnings({
      "unchecked", "rawtypes"                              })
                                                            @Override
                                                            protected void cleanUp(
                                                           List<?> obj)
                                                            {
                                                                obj.clear();
                                                            }

                                                            @Override
                                                       protected List<?> instantiate(
                                                                Object... params)
                                                            {
                                                         return Lists.mutable
                                                             .empty();
                                                            }

                                                              @Override
                                                              protected void release(
                                                           List<?> obj)
                                                              {

                                                              }

                                                          };

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
