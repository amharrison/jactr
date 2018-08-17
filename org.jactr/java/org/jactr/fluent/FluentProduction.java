package org.jactr.fluent;

import java.util.concurrent.ExecutionException;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.condition.ICondition;

/**
 * fluent production builder.
 * 
 * @author harrison
 */
public class FluentProduction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(FluentProduction.class);

  private IModel                     _model;

  private String                     _productionName;

  private IProduction                _currentProduction;

  private FluentProduction(IModel model)
  {
    _model = model;
  }

  static public FluentProduction from(IModel model)
  {
    return new FluentProduction(model);
  }

  public FluentProduction named(String name)
  {
    _productionName = name;
    try
    {
      _currentProduction = _model.getProceduralModule()
          .createProduction(_productionName).get();
    }
    catch (InterruptedException | ExecutionException e)
    {
      throw new RuntimeException(e);
    }
    return this;
  }

  public FluentProduction condition(ICondition condition)
  {
    _currentProduction.getSymbolicProduction().addCondition(condition);
    return this;
  }

  public FluentProduction action(IAction action)
  {
    _currentProduction.getSymbolicProduction().addAction(action);
    return this;
  }

  public IProduction get()
  {
    return _currentProduction;
  }

  public IProduction encode()
  {
    try
    {
      return _model.getProceduralModule().addProduction(_currentProduction)
          .get();
    }
    catch (InterruptedException | ExecutionException e)
    {
      throw new RuntimeException(e);
    }
  }




}
