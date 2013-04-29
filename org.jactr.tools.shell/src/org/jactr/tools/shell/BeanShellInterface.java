package org.jactr.tools.shell;

import org.jactr.core.model.IModel;
import org.jactr.instrument.IInstrument;

/*
 * default logging
 */

public class BeanShellInterface implements IInstrument
{

  public void initialize()
  {
    
  }

  final public void install(IModel model)
  {
    RuntimeListener.setEnabled(true);
  }

  final public void uninstall(IModel model)
  {
    RuntimeListener.setEnabled(false);
  }

}
