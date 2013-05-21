package org.jactr.core.utils.collections;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.collections.set.CompositeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class CompositeSetFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(CompositeSetFactory.class);

  static private RecyclableFactory<CompositeSet> _factory = new AbstractThreadLocalRecyclableFactory<CompositeSet>() {

                                                                        @Override
                                                                        protected void cleanUp(
                                                                            CompositeSet obj)
                                                                        {
                                                                          for (Object set : obj
                                                                              .getCollections())
                                                                            obj.removeComposited((Collection) set);
                                                                        }

                                                                        @Override
                                                                               protected CompositeSet instantiate(
                                                                                   Object... params)
                                                                        {
                                                                          return new CompositeSet();
                                                                        }

                                                                      };


  static public CompositeSet newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(CompositeSet set)
  {
    _factory.recycle(set);
  }
}
