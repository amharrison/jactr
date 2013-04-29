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
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

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
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.event.IProductionListener;
import org.jactr.core.production.event.ProductionEvent;

public abstract class AbstractProduction implements IProduction
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
      Collection<Map<String, Object>> provisionalBindings)
      throws CannotInstantiateException
  {
    if (!isEncoded())
      throw new CannotInstantiateException(
          "Cannot instantiate an unencoded production");

    ISymbolicProduction sp = getSymbolicProduction();
    IModel m = getModel();

    Collection<IInstantiation> instantiations = Collections.EMPTY_LIST;
    CannotMatchException exception = null;
    int totalIterations = 0;

    FastSet<String> missingBuffers = FastSet.newInstance();

    for (IActivationBuffer buffer : m.getActivationBuffers())
      missingBuffers.add("=" + buffer.getName());

    Collection<ICondition> originals = sp.getConditions();

    for (ICondition condition : originals)
      if (condition instanceof IBufferCondition)
        missingBuffers.remove("="
            + ((IBufferCondition) condition).getBufferName());

    /*
     * missingBuffers now contains the variable names bound to buffers that this
     * production should not be able to see so we will remove them from the
     * copied bindings
     */

    // Collection<Map<String, Object>> attemptedMappings = new
    // ArrayList<Map<String, Object>>(
    // provisionalBindings.size());
    FastList<ICondition> cloned = FastList.newInstance();
    // Collection<ICondition> cloned = new
    // ArrayList<ICondition>(originals.size());
    for (Map<String, Object> variableBindings : provisionalBindings)
    {
      FastMap<String, Object> tmpBindings = FastMap.newInstance();
      try
      {
        tmpBindings.putAll(variableBindings);
        tmpBindings.put("=production", this);

        for (String missing : missingBuffers)
          tmpBindings.remove(missing);

        /*
         * now we check to see if this particular mapping has already been
         * attempted, if so, skip.
         */
        // boolean alreadyAttempted = false;
        // for (Map<String, Object> attempted : attemptedMappings)
        // if (attempted.equals(variableBindings))
        // {
        // alreadyAttempted = true;
        // break;
        // }
        //
        // if (alreadyAttempted) continue;
        //
        // attemptedMappings.add(new TreeMap<String, Object>(variableBindings));
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Attempting resolution of " + sp.getName()
              + " with provisional binding: " + tmpBindings);
        /*
         * first we need to duplicate the conditions
         */
        cloned.clear();

        /*
         * clone them, they may throw CMEs if they can determine that binding is
         * impossible.
         */
        for (ICondition original : originals)
          cloned.add(original.clone(m, tmpBindings));

        /*
         * Now we iteratively zip through the conditions, keeping track of the
         * number of unresolved bindings, if the total reaches 0, we are golden
         * and can fully instantiate. If the total fails to decrease after any
         * iteration, it is stuck, and will fail. OR a CME can be thrown.
         */
        int lastTotalUnresolved = Integer.MAX_VALUE;
        int iterations = 0;
        while (lastTotalUnresolved > 0)
        {
          totalIterations++;
          iterations++;
          int totalUnresolved = 0;

          for (ICondition condition : cloned)
            totalUnresolved += condition.bind(m, tmpBindings, true);

          /*
           * no change.. we turn off iterative and attempt to resolve again. we
           * know it will fail, but we're doing this to get the CME
           */
          if (totalUnresolved == lastTotalUnresolved)
            for (ICondition condition : cloned)
              condition.bind(m, tmpBindings, false);

          lastTotalUnresolved = totalUnresolved;
        }

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Instantiated " + sp.getName() + " after " + iterations
              + " iterations, bindings : " + tmpBindings);

        /*
         * we can instantiate
         */
        if (instantiations.size() == 0)
          instantiations = new ArrayList<IInstantiation>(provisionalBindings
              .size());

        IInstantiation instance = createInstantiation(this, cloned,
            new TreeMap<String, Object>(tmpBindings));

        instantiations.add(instance);
      }
      catch (CannotMatchException cme)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Could not instantiate " + sp.getName() + " after "
              + totalIterations + " iterations with binding: " + tmpBindings
              + " ", cme);
        exception = cme;
      }
      finally
      {
        FastMap.recycle(tmpBindings);
      }
    }

    /*
     * clean up before exit..
     */
    FastList.recycle(cloned);
    FastSet.recycle(missingBuffers);

    if (instantiations.size() == 0)
      throw new CannotInstantiateException(sp.getName()
          + " couldn't instantiate because : " + exception.getMessage() + ". ["
          + provisionalBindings.size() + "/" + totalIterations + "]");

    // event
    if (hasListeners())
      for (IInstantiation instance : instantiations)
        dispatch(new ProductionEvent(this, ProductionEvent.Type.INSTANTIATED,
            instance));

    return instantiations;
  }

  /**
   * return a new instantiation
   * 
   * @param parent
   * @param boundConditions
   * @param bindings
   * @return
   */
  abstract protected IInstantiation createInstantiation(
      AbstractProduction parent, Collection<ICondition> boundConditions,
      Map<String, Object> bindings) throws CannotInstantiateException;

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
}
