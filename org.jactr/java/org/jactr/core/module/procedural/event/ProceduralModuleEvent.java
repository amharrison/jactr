/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.procedural.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * event describing the most common events that occur within the procedural
 * module.
 * 
 * @author harrison
 */
public class ProceduralModuleEvent extends
    AbstractACTREvent<IProceduralModule, IProceduralModuleListener> implements
    IParameterEvent<IProceduralModule, IProceduralModuleListener>
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(ProceduralModuleEvent.class);

  static public enum Type {
    PRODUCTION_CREATED, PRODUCTION_ADDED, PRODUCTIONS_MERGED, CONFLICT_SET_ASSEMBLED, PRODUCTION_WILL_FIRE, PRODUCTION_FIRED, PARAMETER_CHANGED
  };

  protected Type                    _type;

  protected Collection<IProduction> _productions;

  protected String                  _parameterName;

  protected Object                  _oldValue;

  protected Object                  _newValue;

  protected ProceduralModuleEvent(IProceduralModule source)
  {
    super(source);
    setSimulationTime(ACTRRuntime.getRuntime().getClock(source.getModel())
        .getTime());
    _productions = Collections.EMPTY_LIST;
  }

  public ProceduralModuleEvent(IProceduralModule source, Type type,
      IProduction production)
  {
    this(source);
    _type = type;
    _productions = new ArrayList<IProduction>(1);
    _productions.add(production);
  }

  public ProceduralModuleEvent(IProceduralModule source, Type type,
      Collection<? extends IProduction> productions)
  {
    this(source);
    _type = type;
    _productions = new ArrayList<IProduction>(productions);
  }

  public ProceduralModuleEvent(IProceduralModule source, String parameterName,
      Object oldValue, Object newValue)
  {
    this(source);
    _type = Type.PARAMETER_CHANGED;
    _parameterName = parameterName;
    _oldValue = oldValue;
    _newValue = newValue;
  }

  public Type getType()
  {
    return _type;
  }

  /**
   * the production(s) for create, add, merge, change and parameter events. the
   * {@link IInstantiation}(s) for conflict set, will fire and fire events.
   * 
   * @return
   */
  public Collection<IProduction> getProductions()
  {
    return Collections.unmodifiableCollection(_productions);
  }

  /**
   * the production for create, add, merge and change events. the
   * {@link IInstantiation} for will fire and fired events.
   * 
   * @return
   */
  public IProduction getProduction()
  {
    return _productions.iterator().next();
  }

  @Override
  public void fire(final IProceduralModuleListener listener)
  {
    switch (this.getType())
    {
      case PRODUCTION_ADDED:
        listener.productionAdded(this);
        break;
      case PRODUCTION_CREATED:
        listener.productionCreated(this);
        break;
      case PRODUCTIONS_MERGED:
        listener.productionsMerged(this);
        break;
      case PRODUCTION_WILL_FIRE:
        listener.productionWillFire(this);
        break;
      case CONFLICT_SET_ASSEMBLED:
        listener.conflictSetAssembled(this);
        break;
      case PRODUCTION_FIRED:
        listener.productionFired(this);
        break;
      case PARAMETER_CHANGED:
        listener.parameterChanged(this);
        break;
      default:
        LOGGER.warn("No clue what to do with type " + getType());
    }
  }

  public Object getNewParameterValue()
  {
    return _newValue;
  }

  public Object getOldParameterValue()
  {
    return _oldValue;
  }

  public String getParameterName()
  {
    return _parameterName;
  }
}
