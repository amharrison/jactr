package org.jactr.core.utils.collections;

/*
 * default logging
 */
import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastMapFactory
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER   = LogFactory
                                                         .getLog(FastMapFactory.class);

  static private RecyclableFactory<FastMap> _factory = new AbstractThreadLocalRecyclableFactory<FastMap>() {

                                                       @SuppressWarnings({
      "unchecked", "rawtypes"                         })
                                                       @Override
                                                       protected void cleanUp(
                                                           FastMap obj)
                                                       {
                                                         obj.clear();
                                                       }

                                                       @Override
                                                       protected FastMap instantiate(
                                                           Object... params)
                                                       {
                                                         return FastMap
                                                             .newInstance();
                                                       }

                                                       @Override
                                                       protected void release(
                                                           FastMap map)
                                                       {
                                                         FastMap.recycle(map);
                                                       }

                                                     };

  static public FastMap newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(FastMap set)
  {
    _factory.recycle(set);
  }
}
