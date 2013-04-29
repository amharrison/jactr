/*
 * Created on Oct 24, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.production.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.runtime.ACTRRuntime;

public class ProductionEvent extends
    AbstractACTREvent<IProduction, IProductionListener> implements
    IParameterEvent<IProduction, IProductionListener>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(ProductionEvent.class);

  static public enum Type {
    ENCODED, FIRED, INSTANTIATED, CONDITION_ADDED, CONDITION_REMOVED, ACTION_ADDED, ACTION_REMOVED, PARAMETER_CHANGED
  };

  protected Type   _type;

  protected String _parameterName;

  protected Object _newValue;

  protected Object _oldValue;

  protected ProductionEvent(IProduction source, Type type)
  {
    super(source, ACTRRuntime.getRuntime().getClock(source.getModel())
        .getTime());
    _type = type;
  }

  public ProductionEvent(IProduction source)
  {
    this(source, Type.ENCODED);
  }

  public ProductionEvent(IProduction source, Type type,
      IInstantiation instantiation)
  {
    this(source, type);
    _newValue = instantiation;
  }

  public ProductionEvent(IProduction source, Type type, ICondition condition)
  {
    this(source, type);
    _newValue = condition;
  }

  public ProductionEvent(IProduction source, Type type, IAction action)
  {
    this(source, type);
    _newValue = action;
  }

  public ProductionEvent(IProduction source, String parameterName,
      Object newValue, Object oldValue)
  {
    this(source, Type.PARAMETER_CHANGED);
    _parameterName = parameterName;
    _oldValue = oldValue;
    _newValue = newValue;
  }

  public Type getType()
  {
    return _type;
  }

  @Override
  public void fire(IProductionListener listener)
  {
    switch (getType())
    {
      case ENCODED:
        listener.productionEncoded(this);
        break;
      case FIRED:
        listener.productionFired(this);
        break;
      case INSTANTIATED:
        listener.productionInstantiated(this);
        break;
      case PARAMETER_CHANGED:
        listener.parameterChanged(this);
        break;
      case CONDITION_ADDED:
        listener.conditionAdded(this);
        break;
      case CONDITION_REMOVED:
        listener.conditionRemoved(this);
        break;
      case ACTION_ADDED:
        listener.actionAdded(this);
        break;
      case ACTION_REMOVED:
        listener.actionRemoved(this);
        break;
      default:
        LOGGER.warn("no clue what to do with event type " + this.getType());
    }
  }

  public IInstantiation getInstantiation()
  {
    return (IInstantiation) _newValue;
  }

  public ICondition getCondition()
  {
    return (ICondition) _newValue;
  }

  public IAction getAction()
  {
    return (IAction) _newValue;
  }

  public String getParameterName()
  {
    return _parameterName;
  }

  public Object getOldParameterValue()
  {
    return _oldValue;
  }

  public Object getNewParameterValue()
  {
    return _newValue;
  }

}
