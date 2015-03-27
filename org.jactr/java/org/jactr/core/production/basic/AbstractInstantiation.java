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
package org.jactr.core.production.basic;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISubsymbolicProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.production.IllegalProductionStateException;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.production.event.ProductionEvent;

public abstract class AbstractInstantiation extends AbstractProduction
    implements IInstantiation
{
  /**
   * logger definition
   */
  static private final Log      LOGGER = LogFactory
                                           .getLog(AbstractInstantiation.class);

  protected AbstractProduction  _production;

  protected VariableBindings   _variableBindings;

  protected double              _firingTime;

  protected double              _executionTime;                                 // aka

  // action
  // latency

  /**
   * @param parent
   * @param boundConditions
   * @param variableBindings
   *          will be copied locally.
   * @throws CannotInstantiateException
   */
  public AbstractInstantiation(AbstractProduction parent,
      Collection<ICondition> boundConditions,
 VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    super(parent.getModel());
    _production = parent;
    _variableBindings = new VariableBindings(variableBindings); // ensure that
                                                                // this is a
                                                                // copy
    _variableBindings.bind("=instantiation", this);

    _symbolicProduction.setName(parent.getSymbolicProduction().getName());

    /*
     * add the conditions and actions..
     */
    for (ICondition condition : boundConditions)
      _symbolicProduction.addCondition(condition);

    for (IAction action : parent.getSymbolicProduction().getActions())
      _symbolicProduction.addAction(action.bind(_variableBindings));
  }

  public double getActionLatency()
  {
    return _executionTime;
  }

  public IProduction getProduction()
  {
    return _production;
  }

  public double getTimeFired()
  {
    return _firingTime;
  }

  public VariableBindings getVariableBindings()
  {
    return _variableBindings;
  }

  @Override
  public void dispose()
  {
    _symbolicProduction.dispose();
    _symbolicProduction = null;

    // _variableBindings.clear();
    _variableBindings = null;

    _eventDispatcher.clear();
    _eventDispatcher = null;

    _production = null;
  }

  @Override
  public IModel getModel()
  {
    return _production.getModel();
  }

  @Override
  public ISubsymbolicProduction getSubsymbolicProduction()
  {
    return _production.getSubsymbolicProduction();
  }

  @Override
  public int compareTo(IProduction arg0)
  {
    return -1;
  }

  @Override
  public String getComment()
  {
    return null;
  }

  @Override
  public void setComment(String comment)
  {
    // noop
  }

  /**
   * can't instantiate an instnatiation
   */
  @Override
  protected IInstantiation createInstantiation(AbstractProduction parent,
      Collection<ICondition> boundConditions, VariableBindings bindings)
      throws CannotInstantiateException
  {
    throw new CannotInstantiateException(this,
        "Cannot instantiate an instantiation");
  }

  public double fire(double firingTime)
  {
    ISymbolicProduction sp = getSymbolicProduction();
    IModel m = getModel();

    ModelTerminatedException mte = null;

    _firingTime = firingTime;

    // notify the buffers that any chunks matched against them
    // are being accessed
    notifyBuffers(_firingTime);

    double actionTime = getSubsymbolicProduction().getFiringTime();

    IProceduralModule pm = (IProceduralModule) m
        .getModule(IProceduralModule.class);

    if (pm != null)
      actionTime = Math.max(pm.getDefaultProductionFiringTime(), actionTime);

    for (IAction action : sp.getActions())
      try
      {
        actionTime = Math.max(action.fire(this, _firingTime), actionTime);
      }
      catch (ModelTerminatedException e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Model should be terminated ", mte);
        mte = e;
      }
      catch (Exception e)
      {
        LOGGER.error(this + " failed to fire " + action + " because of " + e
            + " bindings:" + _variableBindings, e);
        throw new IllegalProductionStateException("Failed for fire " + this
            + " because of an exception during action firing", e);
      }

    _executionTime = actionTime;

    // fire the event
    if (_production.hasListeners())
      _production.dispatch(new ProductionEvent(_production,
          ProductionEvent.Type.FIRED, this));

    // delayed termination so that everything finishes correctly.
    if (mte != null) throw mte;

    return _executionTime;
  }

  /**
   * Notify the buffer that the bound chunk has been accessed(matched)
   */
  protected void notifyBuffers(double now)
  {
    ISymbolicProduction sp = getSymbolicProduction();
    IModel m = getModel();

    // This is a 5.0 feature, with different activation channels/buffers,
    // or Goal Stacks if you will.
    for(ICondition condition : sp.getConditions())
      if (condition instanceof IBufferCondition
          && !(condition instanceof QueryCondition))
      {
        IBufferCondition bufferCondition = (IBufferCondition) condition;
        String bufferName = bufferCondition.getBufferName();

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(sp.getName() + "(" + now + ") : Accessing buffer "
              + bufferName + " and variable =" + bufferName);

        IActivationBuffer ac = m.getActivationBuffer(bufferName);

        IChunk chunk = (IChunk) _variableBindings.get("=" + bufferName);

        if (chunk != null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(sp.getName() + "(" + now + ") : Notifying "
                + ac.getName() + " that we've matched " + chunk);
          // notify the buffer that this chunk has been accessed by a firing
          // production
          ac.matched(chunk);
        }
      }
  }
}
