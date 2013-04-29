package org.jactr.tools.async.message.command.breakpoint;

/*
 * default logging
 */
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.async.message.command.ICommand;

public interface IProductionCommand extends ICommand, Serializable
{

  static public enum Action {ENABLE,DISABLE};
  
  public Action getAction();
  public String getModelName();
  public String getProductionName();
}
