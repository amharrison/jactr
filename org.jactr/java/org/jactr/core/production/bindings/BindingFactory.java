package org.jactr.core.production.bindings;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class BindingFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BindingFactory.class);

  static private RecyclableFactory<Object[]> _factory = new AbstractThreadLocalRecyclableFactory<Object[]>(
                                                          100) {

                                                        @Override
                                                        protected void cleanUp(
                                                            Object[] obj)
                                                        {
                                                          obj[0] = null;
                                                          obj[1] = null;
                                                        }

                                                        @Override
                                                        protected Object[] instantiate(
                                                            Object... params)
                                                        {
                                                          return new Object[2];
                                                        }

                                                        @Override
                                                        protected void release(
                                                            Object[] obj)
                                                        {

                                                        }

                                                      };

  static public Object[] newInstance(Object variable, Object variableSource)
  {
    Object[] rtn = _factory.newInstance();
    rtn[0] = variable;
    rtn[1] = variableSource;
    return rtn;
  }

  static public void recycle(Object[] set)
  {
    _factory.recycle(set);
  }

}
