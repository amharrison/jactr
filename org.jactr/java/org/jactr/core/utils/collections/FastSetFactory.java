package org.jactr.core.utils.collections;

/*
 * default logging
 */
import javolution.util.FastSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastSetFactory
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER   = LogFactory
                                                         .getLog(FastSetFactory.class);

  static private RecyclableFactory<FastSet> _factory = new AbstractThreadLocalRecyclableFactory<FastSet>() {

                                                       @SuppressWarnings({
      "unchecked", "rawtypes"                         })
                                                       @Override
                                                       protected void cleanUp(
                                                           FastSet obj)
                                                       {
                                                         obj.clear();
                                                       }

                                                       @Override
                                                       protected FastSet instantiate(
                                                           Object... params)
                                                       {
                                                         return FastSet
                                                             .newInstance();
                                                       }

                                                       @Override
                                                       protected void release(
                                                           FastSet obj)
                                                       {
                                                         FastSet.recycle(obj);
                                                       }

                                                     };

  static public FastSet newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(FastSet set)
  {
    _factory.recycle(set);
  }
}
