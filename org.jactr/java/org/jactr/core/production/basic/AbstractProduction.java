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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISubsymbolicProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.production.IllegalProductionStateException;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.bindings.VariableBindingsFactory;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.event.IProductionListener;
import org.jactr.core.production.event.ProductionEvent;
import org.jactr.core.utils.DefaultAdaptable;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.collections.FastSetFactory;

public abstract class AbstractProduction extends DefaultAdaptable implements
    IProduction
{
  /**
   * logger definition
   */
  static private final Log                                        LOGGER = LogFactory
                                                                             .getLog(AbstractProduction.class);

  protected ACTREventDispatcher<IProduction, IProductionListener> _eventDispatcher;

  protected ISymbolicProduction                                   _symbolicProduction;

  protected ISubsymbolicProduction                                _subsymbolicProduction;

  protected IModel                                                _model;

  protected String                                                _comment;

  protected boolean                                               _encoded;

  public AbstractProduction(IModel model)
  {
    _eventDispatcher = new ACTREventDispatcher<IProduction, IProductionListener>();
    _model = model;
    _symbolicProduction = createSymbolicProduction(this, model);
    _subsymbolicProduction = createSubsymbolicProduction(this, model);
  }

  abstract protected ISymbolicProduction createSymbolicProduction(
      AbstractProduction production, IModel model);

  abstract protected ISubsymbolicProduction createSubsymbolicProduction(
      AbstractProduction production, IModel model);

  public void addListener(IProductionListener pl, Executor executor)
  {
    _eventDispatcher.addListener(pl, executor);
  }

  public void dispose()
  {
    _eventDispatcher.clear();
    if (getSymbolicProduction() != null) getSymbolicProduction().dispose();
    if (getSubsymbolicProduction() != null)
      getSubsymbolicProduction().dispose();
  }

  public IModel getModel()
  {
    return _model;
  }

  public ISubsymbolicProduction getSubsymbolicProduction()
  {
    return _subsymbolicProduction;
  }

  public ISymbolicProduction getSymbolicProduction()
  {
    return _symbolicProduction;
  }

  // public IInstantiation instantiate() throws CannotInstantiateException
  // {
  // if (!isEncoded())
  // throw new CannotInstantiateException(
  // "Cannot instantiate an unencoded production");
  //
  // HashMap<String, Object> bindings = new HashMap<String, Object>();
  // ISymbolicProduction sp = getSymbolicProduction();
  //
  // IModel m = getModel();
  //
  // bindings.put("=model", m);
  // bindings.put("=production", this);
  // Collection<ICondition> conditions = sp.getConditions();
  //
  // ArrayList<ICondition> boundConditions = new ArrayList<ICondition>(
  // conditions.size());
  //
  // if (LOGGER.isDebugEnabled()) LOGGER.debug("Instantiating " + this);
  //
  // for (ICondition condition : conditions)
  // try
  // {
  // condition = condition.bind(m, bindings);
  // boundConditions.add(condition);
  // }
  // catch (CannotMatchException cme)
  // {
  // // cleanup
  // for (ICondition bound : boundConditions)
  // bound.dispose();
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Cannot instantiate " + this
  // + ": Failed to match condition " + condition, cme);
  // throw new CannotInstantiateException(cme.getMessage(), cme);
  // }
  //
  // IInstantiation instance = createInstantiation(this, boundConditions,
  // bindings);
  //
  // // event
  // if (hasListeners())
  // dispatch(new ProductionEvent(this, ProductionEvent.Type.INSTANTIATED,
  // instance));
  //
  // return instance;
  // }

  public Collection<IInstantiation> instantiateAll(
      Collection<VariableBindings> provisionalBindings)
      throws CannotInstantiateException
  {
    if (!isEncoded())
      throw new CannotInstantiateException(this,
          "Cannot instantiate an unencoded production");

    // we call this enough in loop to warrant this
    boolean debugEnabled = LOGGER.isDebugEnabled();

    ISymbolicProduction sp = getSymbolicProduction();
    IModel m = getModel();

    Collection<IInstantiation> instantiations = Collections.EMPTY_LIST;
    List<CannotMatchException> exceptions = FastListFactory.newInstance();
    Set<String> missingBuffers = FastSetFactory.newInstance();
    List<ICondition> cloned = FastListFactory.newInstance();

    int totalIterations = 0;

    try
    {

      for (IActivationBuffer buffer : m.getActivationBuffers())
        missingBuffers.add("=" + buffer.getName());

      Collection<ICondition> originals = sp.getConditions();

      for (ICondition condition : originals)
        if (condition instanceof IBufferCondition)
          missingBuffers.remove("="
              + ((IBufferCondition) condition).getBufferName());

      /*
       * missingBuffers now contains the variable names bound to buffers that
       * this production should not be able to see so we will remove them from
       * the copied bindings
       */
      // we will recycle this binding
      VariableBindings tmpBindings = VariableBindingsFactory.newInstance();

      for (VariableBindings variableBindings : provisionalBindings)
       try
        {
          /*
           * we recycle the tmpBindings for efficiency purposes
           */
          tmpBindings.clear();
          tmpBindings.copy(variableBindings);
          
          tmpBindings.bind("=production", this);

          for (String missing : missingBuffers)
            tmpBindings.unbind(missing);


          if (debugEnabled)
            LOGGER.debug("Attempting resolution of " + sp.getName()
                + " with provisional binding: " + tmpBindings);
          /*
           * first we need to duplicate the conditions
           */
          cloned.clear();

          /*
           * clone them, they may throw CMEs if they can determine that binding
           * is impossible.
           */
          for (ICondition original : originals)
            cloned.add(original.clone(m, tmpBindings));

          /*
           * Now we iteratively zip through the conditions, keeping track of the
           * number of unresolved bindings, if the total reaches 0, we are
           * golden and can fully instantiate. If the total fails to decrease
           * after any iteration, it is stuck, and will fail. OR a CME can be
           * thrown.
           */
          int lastTotalUnresolved = Integer.MAX_VALUE;
          int iterations = 0;
          while (lastTotalUnresolved > 0)
          {
            totalIterations++;
            iterations++;
            int totalUnresolved = 0;
            for (ICondition condition : cloned)
            {
              if (debugEnabled)
                LOGGER.debug("binding condition " + condition.getClass());
              totalUnresolved += condition.bind(m, tmpBindings, true);
            }
            /*
             * no change.. we turn off iterative and attempt to resolve again.
             * we know it will fail, but we're doing this to get the CME
             */
            if (totalUnresolved == lastTotalUnresolved)
              for (ICondition condition : cloned)
                condition.bind(m, tmpBindings, false);

            lastTotalUnresolved = totalUnresolved;
          }

          if (debugEnabled)
            LOGGER.debug("Instantiated " + sp.getName() + " after "
                + iterations + " iterations, bindings : " + tmpBindings);

          /*
           * we can instantiate
           */
          if (instantiations.size() == 0)
            instantiations = new ArrayList<IInstantiation>(
                provisionalBindings.size());

          // an actual copy of the bindings, outside of recycling
          // since we can't trace where these will go.
          IInstantiation instance = createInstantiation(this, cloned,
              tmpBindings);

          instantiations.add(instance);
        }
        catch (CannotMatchException cme)
        {
          if (debugEnabled)
            LOGGER.debug("Could not instantiate " + sp.getName() + " after "
                + totalIterations + " iterations with binding: " + tmpBindings
                + " ", cme);

          /*
           * hold onto the CME for passing to CNI
           */
          exceptions.add(cme);
        }
        catch (CannotInstantiateException cie)
        {
          cie.setProduction(this);
          throw cie;
        }

      VariableBindingsFactory.recycle(tmpBindings);

      if (instantiations.size() == 0) throw new CannotInstantiateException(this, exceptions);

      // event
      if (hasListeners())
        for (IInstantiation instance : instantiations)
          dispatch(new ProductionEvent(this, ProductionEvent.Type.INSTANTIATED,
              instance));

      return instantiations;
    }
    finally
    {
      FastListFactory.recycle(cloned);
      FastSetFactory.recycle(missingBuffers);
      FastListFactory.recycle(exceptions);
    }
  }

  /**
   * return a new instantiation
   * 
   * @param parent
   * @param boundConditions
   * @param bindings
   *          will be copied.
   * @return
   */
  abstract protected IInstantiation createInstantiation(
      AbstractProduction parent, Collection<ICondition> boundConditions,
      VariableBindings bindings) throws CannotInstantiateException;

  @Override
  public String toString()
  {
    if (_symbolicProduction != null) return _symbolicProduction.getName();
    return super.toString();
  }

  public boolean hasListeners()
  {
    return _eventDispatcher.hasListeners();
  }

  public void dispatch(ProductionEvent pe)
  {
    _eventDispatcher.fire(pe);
  }

  public void removeListener(IProductionListener pl)
  {
    _eventDispatcher.removeListener(pl);
  }

  public int compareTo(IProduction arg0)
  {
    if (arg0 == this) return 0;
    // else lexical
    return getSymbolicProduction().getName().compareTo(
        arg0.getSymbolicProduction().getName());
  }

  public String getComment()
  {
    return _comment;
  }

  public void setComment(String comment)
  {
    _comment = comment;
  }

  public void encode()
  {
    if (isEncoded())
      throw new IllegalProductionStateException(
          "Cannot encoded an encoded production");

    getSymbolicProduction().encode();
    getSubsymbolicProduction().encode();
    _encoded = true;
  }

  public boolean isEncoded()
  {
    return _encoded;
  }

  @Override
  public Object getAdapter(Class adapterClass)
  {
    if (ISubsymbolicProduction.class.equals(adapterClass))
      return getSubsymbolicProduction();
    else if (ISymbolicProduction.class.equals(adapterClass))
      return getSymbolicProduction();
    else
      return super.getAdapter(adapterClass);
  }
}
