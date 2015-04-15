package org.jactr.core.utils.collections;

/*
 * default logging
 */
import javolution.util.FastCollection;
import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class FastCollectionFactory
{
  /**
   * Logger definition
   */
  static private final transient Log             LOGGER   = LogFactory
                                                              .getLog(FastCollectionFactory.class);

  static private RecyclableFactory<FastCollection> _factory = new AbstractThreadLocalRecyclableFactory<FastCollection>() {

                                                            @SuppressWarnings({
      "unchecked", "rawtypes"                              })
                                                            @Override
                                                            protected void cleanUp(
                                                                  FastCollection obj)
                                                            {
                                                                obj.clear();
                                                            }

                                                            @Override
                                                              protected FastCollection instantiate(
                                                                Object... params)
                                                            {
                                                                return FastList
                                                                    .newInstance();
                                                            }

                                                              @Override
                                                              protected void release(
                                                                  FastCollection obj)
                                                              {
                                                                if (obj instanceof FastList)
                                                                  FastList
                                                                      .recycle((FastList) obj);
                                                              }

                                                          };

  static public FastCollection newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(FastCollection set)
  {
    _factory.recycle(set);
  }
}
