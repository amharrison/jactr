/*
 * Created on Nov 30, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.runtime.controller.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.IProductionInstantiator;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.module.procedural.six.DefaultProductionInstantiator;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.debug.event.BreakpointEvent;
import org.jactr.core.runtime.controller.debug.event.IBreakpointListener;
import org.jactr.core.runtime.event.ACTRRuntimeAdapter;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;

/**
 * support production break points that block the model when the marked
 * productions are instantiated and selected for firing. it also supports a few
 * other break point types
 * 
 * @author developer
 */
public class DebugController extends DefaultController implements
    IDebugController
{
  /**
   * logger definition
   */
  static private final Log                                     LOGGER = LogFactory
                                                                          .getLog(DebugController.class);

  private Map<IModel, Map<BreakpointType, Collection<Object>>> _breakpoints;

  private Set<IProduction>                                     _disabledProductions;

  private ACTREventDispatcher<IModel, IBreakpointListener>     _breakpointListeners;

  private IProceduralModuleListener                            _proceduralListener;

  private IModelListener                                       _modelListener;

  private IACTRRuntimeListener                                 _runtimeListener;

  public DebugController()
  {
    super();
    _disabledProductions = new HashSet<IProduction>();
    _breakpoints = new HashMap<IModel, Map<BreakpointType, Collection<Object>>>();



    _proceduralListener = createProceduralListener();
    _breakpointListeners = new ACTREventDispatcher<IModel, IBreakpointListener>();
    _runtimeListener = new ACTRRuntimeAdapter() {

      /**
       * normally we would attach at start, but since break points can be added
       * before the model runs, we need to do it here.
       * 
       * @param event
       */
      public void modelAdded(ACTRRuntimeEvent event)
      {
        IModel model = event.getModel();

        model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);
        IProceduralModule procMod = model.getProceduralModule();
        procMod.addListener(getProceduralListener(),
            ExecutorServices.INLINE_EXECUTOR);

        final IProductionInstantiator fInstantiator = procMod
            .getProductionInstantiator();

        /**
         * we use a delegated instantiator to skip disabled productions.
         */
        procMod.setProductionInstantiator(new DefaultProductionInstantiator() {

          @Override
          public Collection<IInstantiation> instantiate(IProduction production,
              Collection<VariableBindings> provisionalBindings)
              throws CannotInstantiateException
          {
            if (_disabledProductions.contains(production))
              throw new CannotInstantiateException(production
                  .getSymbolicProduction().getName() + " has been disabled");
            try
            {
              return fInstantiator.instantiate(production, provisionalBindings);
            }
            catch (CannotInstantiateException cie)
            {
              throw cie;
            }
            catch (Exception e)
            {
              LOGGER.error(
                  "Could not instantiate " + production + " : "
                      + e.getMessage(), e);

              throw new CannotInstantiateException("Could not instantiate "
                  + production + " : " + e.getMessage(), e);
            }
          }

        });

        try
        {
          _lock.lock();
          _breakpoints.put(event.getModel(),
              new HashMap<BreakpointType, Collection<Object>>());
        }
        finally
        {
          _lock.unlock();
        }
      }

      public void modelRemoved(ACTRRuntimeEvent event)
      {
        IModel model = event.getModel();
        model.removeListener(_modelListener);
        model.getProceduralModule().removeListener(getProceduralListener());
        /*
         * remove the breakpoint info
         */
        try
        {
          _lock.lock();
          _breakpoints.remove(event.getModel());
        }
        finally
        {
          _lock.unlock();
        }
      }

    };

    _modelListener = new ModelListenerAdaptor() {
      @Override
      public void cycleStarted(ModelEvent event)
      {
        super.cycleStarted(event);
        IModel model = event.getSource();
        long cycle = model.getCycle();

        checkForBreakpoint(event.getSource(), BreakpointType.CYCLE, cycle);
        checkForBreakpoint(event.getSource(), BreakpointType.TIME,
            model.getAge());
      }
    };
  }

  /**
   * @see org.jactr.core.runtime.controller.debug.IDebugController#addListener(org.jactr.core.runtime.controller.debug.event.IBreakpointListener,
   *      java.util.concurrent.Executor)
   */
  public void addListener(IBreakpointListener listener, Executor executor)
  {
    _breakpointListeners.addListener(listener, executor);
  }

  /**
   * @see org.jactr.core.runtime.controller.debug.IDebugController#removeListener(org.jactr.core.runtime.controller.debug.event.IBreakpointListener)
   */
  public void removeListener(IBreakpointListener listener)
  {
    _breakpointListeners.removeListener(listener);
  }

  protected IProceduralModuleListener getProceduralListener()
  {
    return _proceduralListener;
  }

  protected IProceduralModuleListener createProceduralListener()
  {
    return new ProceduralModuleListenerAdaptor() {
      /**
       * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#conflictSetAssembled(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
       */
      @Override
      public void conflictSetAssembled(ProceduralModuleEvent pme)
      {
      }

      /**
       * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionAdded(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
       */
      @Override
      public void productionAdded(ProceduralModuleEvent pme)
      {

      }

      /**
       * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionWillFire(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
       */
      @Override
      public void productionWillFire(ProceduralModuleEvent pme)
      {
        IModel model = pme.getSource().getModel();
        IProduction production = pme.getProduction();
        checkForBreakpoint(model, BreakpointType.PRODUCTION, production);
      }

      /**
       * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionCreated(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
       */
      @Override
      public void productionCreated(ProceduralModuleEvent pme)
      {

      }

      /**
       * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionFired(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
       */
      @Override
      public void productionFired(ProceduralModuleEvent pme)
      {

      }

      /**
       * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionsMerged(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
       */
      @Override
      public void productionsMerged(ProceduralModuleEvent pme)
      {

      }
    };
  }

  @Override
  public void attach()
  {
    super.attach();
    ACTRRuntime.getRuntime().addListener(_runtimeListener,
        ExecutorServices.INLINE_EXECUTOR);
  }

  @Override
  public void detach()
  {
    super.detach();

    /*
     * we also need to make sure we remove our listeners
     */
    for (IModel model : _breakpoints.keySet())
      model.getProceduralModule().removeListener(getProceduralListener());

    clearBreakpoints();
  }

  /**
   * @see org.jactr.core.runtime.controller.debug.IDebugController#clearBreakpoints()
   */
  public void clearBreakpoints()
  {
    clearBreakpoints(null, null);
  }

  /**
   * @see org.jactr.core.runtime.controller.debug.IDebugController#clearBreakpoints(org.jactr.core.model.IModel,
   *      org.jactr.core.runtime.controller.debug.BreakpointType)
   */
  public void clearBreakpoints(IModel model, BreakpointType type)
  {
    try
    {
      _lock.lock();
      Collection<IModel> forModels = _breakpoints.keySet();
      if (model != null) forModels = Collections.singleton(model);

      for (IModel m : forModels)
      {
        Map<BreakpointType, Collection<Object>> maps = _breakpoints.get(model);
        Collection<BreakpointType> types = Arrays.asList(BreakpointType
            .values());
        if (type != null) types = Collections.singleton(type);

        if (maps != null)
        {
          for (BreakpointType t : types)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Clearing break points of " + t + " for " + m);
            Collection<Object> points = maps.get(t);
            if (points != null) points.clear();
          }
          maps.clear();
        }

      }
      _breakpoints.clear();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void setEnabled(IProduction production, boolean enabled)
  {
    if (enabled)
      _disabledProductions.remove(production);
    else
      _disabledProductions.add(production);
  }

  /**
   * @see org.jactr.core.runtime.controller.debug.IDebugController#addBreakpoint(org.jactr.core.model.IModel,
   *      org.jactr.core.runtime.controller.debug.BreakpointType,
   *      java.lang.Object)
   */
  public void addBreakpoint(IModel model, BreakpointType type, Object value)
  {
    try
    {
      _lock.lock();
      Map<BreakpointType, Collection<Object>> breakpoints = _breakpoints
          .get(model);
      /*
       * breakpoints wont be null..
       */
      Collection<Object> values = breakpoints.get(type);
      if (values == null)
      {
        values = new ArrayList<Object>();
        breakpoints.put(type, values);
      }

      values.add(value);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.debug.IDebugController#removeBreakpoint(org.jactr.core.model.IModel,
   *      org.jactr.core.runtime.controller.debug.BreakpointType,
   *      java.lang.Object)
   */
  public void removeBreakpoint(IModel model, BreakpointType type, Object value)
  {
    try
    {
      _lock.lock();
      Map<BreakpointType, Collection<Object>> breakpoints = _breakpoints
          .get(model);

      Collection<Object> values = breakpoints.get(type);
      if (values != null) values.remove(value);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.debug.IDebugController#isBreakpoint(org.jactr.core.model.IModel,
   *      org.jactr.core.runtime.controller.debug.BreakpointType,
   *      java.lang.Object)
   */
  public boolean isBreakpoint(IModel model, BreakpointType type, Object value)
  {
    try
    {
      _lock.lock();

      /*
       * the break object is an instantiation, we need to get the production it
       * was derived from
       */
      if (type == BreakpointType.PRODUCTION)
        if (value instanceof IInstantiation)
          value = ((IInstantiation) value).getProduction();

      /*
       * never break on exception this is temporary until a more optimal
       * solution can be found - basically, the issue is that if an exception
       * occurs during model execution, there is no way to recover gracefully,
       * so using it as a break point is silly.
       */
      if (type == BreakpointType.EXCEPTION) return false;

      Map<BreakpointType, Collection<Object>> breakpoints = _breakpoints
          .get(model);
      if (breakpoints == null) return false;

      Collection<Object> values = breakpoints.get(type);
      if (values == null) return false;

      boolean isBreakpoint = values.contains(value);

      /*
       * time and cycle behave differently. if value is >= any value they will
       * trigger..
       */
      if (!isBreakpoint) if (type == BreakpointType.CYCLE)
      {
        // values are numbers..
        for (Object obj : values)
          if (((Number) obj).intValue() < ((Number) value).intValue())
          {
            isBreakpoint = true;
            continue;
          }
      }
      else if (type == BreakpointType.TIME) for (Object obj : values)
        if (((Number) obj).doubleValue() < ((Number) value).doubleValue())
        {
          isBreakpoint = true;
          continue;
        }

      return isBreakpoint;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * will check to see if value represents a valid break point of type within
   * model and block after calling breakpointReached
   * 
   * @param model
   * @param type
   * @param value
   */
  protected void checkForBreakpoint(IModel model, BreakpointType type,
      Object value)
  {
    if (isBreakpoint(model, type, value))
      breakpointReached(model, type, value);
  }

  /**
   * actually suspend the model. this should only be called on the model thread
   * via an event listener that is attached as inline. we can independently
   * suspend or resume models, however, that doesn't make a whole lot of sense.
   * so, when we reach this point, we suspend EVERYONE. meaning, the model that
   * tripped this breakpoint will suspend right here, but everyone else will
   * suspend at the top of the next cycle, unless they also trip a breakpoint.
   * 
   * @param model
   * @param type
   * @param value
   */
  protected void breakpointReached(IModel model, BreakpointType type,
      Object value)
  {
    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("Breakpoint." + type.name() + " reached at " + value);
      if (value instanceof Throwable)
        LOGGER.error("Specifically, an exception was thrown ",
            (Throwable) value);
    }

    fireBreakpointReachedEvent(model, type, value);

    // signal to suspend everyone
    suspend();

    suspendLocally(model);
  }

  protected void fireBreakpointReachedEvent(IModel model, BreakpointType type,
      Object details)
  {
    _breakpointListeners.fire(new BreakpointEvent(model, type, details));
  }

}
