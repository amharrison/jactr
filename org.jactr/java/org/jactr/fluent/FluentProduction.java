package org.jactr.fluent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISymbolicProduction;
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

  private Collection<ICondition>     _conditions = new ArrayList<>();

  private Collection<IAction>        _actions    = new ArrayList<>();

  private IProduction                _currentProduction;

  private FluentProduction(IModel model)
  {
    _model = model;
  }

  static public FluentProduction from(IModel model)
  {
    return new FluentProduction(model);
  }

  /**
   * name of the production
   * 
   * @param name
   * @return
   */
  public FluentProduction named(String name)
  {
    _productionName = name;
    _currentProduction = null;

    return this;
  }

  /**
   * add a new condition, probably built from {@link FluentCondition}
   * 
   * @param condition
   * @return
   */
  public FluentProduction condition(ICondition condition)
  {
    _conditions.add(condition);
    _currentProduction = null;
    return this;
  }

  /**
   * add a new action, probably built from {@link FluentAction}
   * 
   * @param action
   * @return
   */
  public FluentProduction action(IAction action)
  {
    _actions.add(action);
    _currentProduction = null;
    return this;
  }

  /**
   * build and encode the production
   * 
   * @return
   */
  public IProduction encode()
  {
    try
    {
      _currentProduction = _model.getProceduralModule()
          .createProduction(_productionName).get();

      ISymbolicProduction sp = _currentProduction.getSymbolicProduction();
      _conditions.forEach(sp::addCondition);
      _actions.forEach(sp::addAction);

      return _model.getProceduralModule().addProduction(_currentProduction)
          .get();
    }
    catch (InterruptedException | ExecutionException e)
    {
      throw new RuntimeException(e);
    }
  }




}
