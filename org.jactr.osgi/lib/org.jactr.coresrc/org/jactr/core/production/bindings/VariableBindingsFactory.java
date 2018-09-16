package org.jactr.core.production.bindings;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.utils.recyclable.AbstractThreadLocalRecyclableFactory;
import org.jactr.core.utils.recyclable.RecyclableFactory;

public class VariableBindingsFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VariableBindingsFactory.class);

  static private RecyclableFactory<VariableBindings> _factory = new AbstractThreadLocalRecyclableFactory<VariableBindings>(
                                                                  20) {

                                                                @Override
                                                                protected void cleanUp(
                                                                    VariableBindings obj)
                                                                {
                                                                  obj.clear();
                                                                }

                                                                @Override
                                                                protected VariableBindings instantiate(
                                                                    Object... params)
                                                                {
                                                                  // explicitly
                                                                  // recycle the
                                                                  // bindings
                                                                  return new VariableBindings(
                                                                      true);
                                                                }

                                                                @Override
                                                                protected void release(
                                                                    VariableBindings obj)
                                                                {
                                                                  obj.clear();
                                                                }

                                                              };

  static public VariableBindings newInstance()
  {
    return _factory.newInstance();
  }

  static public void recycle(VariableBindings set)
  {
    _factory.recycle(set);
  }
}
