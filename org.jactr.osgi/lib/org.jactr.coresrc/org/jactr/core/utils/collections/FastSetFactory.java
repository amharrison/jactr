package org.jactr.core.utils.collections;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.impl.factory.Sets;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastSetFactory
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER   = LogFactory
                                                         .getLog(FastSetFactory.class);

  static private RecyclableFactory<Set<?>> _factory = new AbstractThreadLocalRecyclableFactory<Set<?>>() {

                                                       @SuppressWarnings({
      "unchecked", "rawtypes"                         })
                                                       @Override
                                                       protected void cleanUp(
                                                          Set<?> obj)
                                                       {
                                                         obj.clear();
                                                       }

                                                       @Override
                                                      protected Set<?> instantiate(
                                                           Object... params)
                                                       {
                                                        return Sets.mutable
                                                            .empty();
                                                       }

                                                       @Override
                                                       protected void release(
                                                          Set<?> obj)
                                                       {

                                                       }

                                                     };

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
