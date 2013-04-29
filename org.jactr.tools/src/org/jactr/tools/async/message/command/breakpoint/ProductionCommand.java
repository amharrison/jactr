package org.jactr.tools.async.message.command.breakpoint;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.async.message.BaseMessage;

public class ProductionCommand extends BaseMessage implements
    IProductionCommand
{
  /**
   * 
   */
  private static final long serialVersionUID = -8787924694951419236L;
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionCommand.class);

  private final Action _action;
  private final String _modelName;
  private final String _productionName;
  
  public ProductionCommand(String model, String production, Action action)
  {
    _action = action;
    _modelName = model;
    _productionName = production;
  }
  
  
  public Action getAction()
  {
    return _action;
  }

  public String getModelName()
  {
    return _modelName;
  }

  public String getProductionName()
  {
    return _productionName;
  }

}
