package org.jactr.tools.instantiation;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.instrument.IInstrument;

@Deprecated
public class InstantiationTracker implements IInstrument
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(InstantiationTracker.class);

  public void initialize()
  {
    // TODO Auto-generated method stub

  }

  public void install(IModel model)
  {
    // TODO Auto-generated method stub

  }

  public void uninstall(IModel model)
  {
    // TODO Auto-generated method stub

  }

}
