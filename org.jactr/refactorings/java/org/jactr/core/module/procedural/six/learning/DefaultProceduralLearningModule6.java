/*
 * Created on Oct 25, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.procedural.six.learning;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.module.procedural.five.learning.IProductionCompiler;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.module.procedural.six.learning.event.IProceduralLearningModule6Listener;
import org.jactr.core.module.procedural.six.learning.event.ProceduralLearningEvent;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.six.ISubsymbolicProduction6;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * production learning is accomplished by listening to the procedural module for
 * firing events..<br>
 * <br>
 * 
 * @author developer
 */
public class DefaultProceduralLearningModule6 extends AbstractModule implements
    IProceduralLearningModule6, IParameterized
{
  /**
   * logger definition
   */
  static private final Log                                                                    LOGGER                        = LogFactory
                                                                                                                                .getLog(DefaultProceduralLearningModule6.class);

  protected boolean                                                                           _productionCompilationEnabled = false;

  protected double                                                                            _parameterLearningRate        = Double.NaN;

  protected int                                                                               _optimizationLevel            = 0;

  protected IExpectedUtilityEquation                                                          _utilityEquation;

  protected IProductionCompiler                                                               _productionCompiler;

  protected SortedMap<Double, IProduction>                                                    _firedProductions;

  /**
   * used to track new productions. Keyed on the first parent, if the first
   * parent is ever part of the conflict set, but one of its children isn't,
   * that child is invalid and should be removed
   */
  private Map<IProduction, IProduction>                                                       _kindergarden;

  private IProduction                                                                         _justFired;

  private IProduction                                                                         _oneBack;

  private IProceduralModule                                                                   _proceduralModule;

  private ACTREventDispatcher<IProceduralLearningModule6, IProceduralLearningModule6Listener> _dispatcher                   = new ACTREventDispatcher<IProceduralLearningModule6, IProceduralLearningModule6Listener>();

  public DefaultProceduralLearningModule6()
  {
    super("ProceduralLearningV6");
    _firedProductions = new TreeMap<Double, IProduction>();
    _kindergarden = new HashMap<IProduction, IProduction>();
  }

  public boolean isProductionCompilationEnabled()
  {
    return _productionCompilationEnabled && _productionCompiler != null;
  }

  public void setProductionCompilationEnabled(boolean enabled)
  {
    _productionCompilationEnabled = enabled;
  }

  public double getParameterLearning()
  {
    return _parameterLearningRate;
  }

  public boolean isParameterLearningEnabled()
  {
    return !Double.isNaN(_parameterLearningRate);
  }

  public void setParameterLearning(double rate)
  {
    _parameterLearningRate = rate;
  }

  public boolean isLearningEnabled()
  {
    return isParameterLearningEnabled();
  }

  @Override
  public void initialize()
  {
    setExpectedUtilityEquation(this.new DefaultExpectedUtilityEquation());

    _proceduralModule = getModel().getProceduralModule();
    _proceduralModule.addListener(new ProceduralModuleListenerAdaptor() {

      @Override
      public void conflictSetAssembled(ProceduralModuleEvent pme)
      {
        /**
         * check through the productions that might fire, if any are parents,
         * check to see if their children are represented too...
         */
        if (_kindergarden.size() == 0) return;

        HashSet<IProduction> masterList = new HashSet<IProduction>();
        for (IProduction production : pme.getProductions())
          masterList.add(((IInstantiation) production).getProduction());

        IModel model = getModel();

        for (IProduction production : masterList)
          if (_kindergarden.containsKey(production))
          {
            IProduction child = _kindergarden.get(production);

            if (child != null && !masterList.contains(child))
            {
              String msg = production
                  + " produced "
                  + child
                  + " who could not be instantiated with its parent. Bad children shall be killed!";

              if (Logger.hasLoggers(model))
                Logger.log(model, Logger.Stream.PROCEDURAL, msg);

              if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);

              ((DefaultProceduralModule6) model.getProceduralModule())
                  .removeProduction(child);
            }

            _kindergarden.remove(production);
          }
      }

      @Override
      public void productionFired(ProceduralModuleEvent pme)
      {
        IInstantiation instantiation = (IInstantiation) pme.getProduction();

        DefaultProceduralLearningModule6.this.productionFired(instantiation
            .getProduction(), pme.getSimulationTime());

        if (isProductionCompilationEnabled())
        {
          IProduction newProduction = getProductionCompiler().productionFired(
              instantiation, pme.getSource());
          if (newProduction != null)
          {
            /**
             * add the new production
             */
            pme.getSource().addProduction(newProduction);
            /**
             * hop back two productions to get its initial parent
             */
            if (_oneBack != null) _kindergarden.put(_oneBack, newProduction);
          }
        }
      }
    }, getExecutor());
  }

  protected void productionFired(IProduction production, double when)
  {
    _oneBack = _justFired;
    _justFired = production;

    if (isParameterLearningEnabled())
    {
      _firedProductions.put(when, production);

      /*
       * if the production has a reward value that is not nan..
       */
      double reward = ((ISubsymbolicProduction6) production
          .getSubsymbolicProduction()).getReward();

      if (!Double.isNaN(reward))
      {
        reward(reward);

        if (_dispatcher.hasListeners())
          _dispatcher
              .fire(new ProceduralLearningEvent(this, production, reward));
      }
    }
  }

  public IProductionCompiler getProductionCompiler()
  {
    return _productionCompiler;
  }

  public void setProductionCompiler(IProductionCompiler compiler)
  {
    _productionCompiler = compiler;
  }

  public void setExpectedUtilityEquation(IExpectedUtilityEquation equation)
  {
    _utilityEquation = equation;
  }

  public IExpectedUtilityEquation getExpectedUtilityEquation()
  {
    return _utilityEquation;
  }

  public int getOptimizationLevel()
  {
    return _optimizationLevel;
  }

  public void setOptimizationLevel(int level)
  {
    _optimizationLevel = level;
  }

  public void reward(double initialReward)
  {
    IModel model = getModel();
    boolean log = LOGGER.isDebugEnabled() || Logger.hasLoggers(model);

    if (log)
    {
      String msg = "Rewarding " + _firedProductions.size() + " productions by "
          + initialReward;
      LOGGER.debug(msg);
      Logger.log(model, Logger.Stream.PROCEDURAL, msg);
    }

    double now = ACTRRuntime.getRuntime().getClock(model).getTime();
    IExpectedUtilityEquation equation = getExpectedUtilityEquation();

    for (Map.Entry<Double, IProduction> entry : _firedProductions.entrySet())
    {
      double discountedReward = initialReward - (now - entry.getKey());
      IProduction p = entry.getValue();

      ISubsymbolicProduction6 ssp = (ISubsymbolicProduction6) p
          .getSubsymbolicProduction();

      double utility = equation.computeExpectedUtility(p, model,
          discountedReward);

      if (!(Double.isNaN(utility) || Double.isInfinite(utility)))
        ssp.setExpectedUtility(utility);

      if (log)
      {
        String msg = "Discounted reward for " + p + " to " + discountedReward
            + " for a learned utility of " + utility;
        LOGGER.debug(msg);
        Logger.log(model, Logger.Stream.PROCEDURAL, msg);
      }
    }

    _firedProductions.clear();
  }

  private class DefaultExpectedUtilityEquation implements
      IExpectedUtilityEquation
  {

    public double computeExpectedUtility(IProduction production, IModel model,
        double reward)
    {
      ISubsymbolicProduction6 ssp = (ISubsymbolicProduction6) production
          .getSubsymbolicProduction();

      double previousUtility = ssp.getExpectedUtility();

      if (Double.isNaN(previousUtility)) previousUtility = ssp.getUtility();

      double partial = 0;

      if (isParameterLearningEnabled()
          && !(Double.isNaN(reward) || Double.isInfinite(reward)))
        partial = getParameterLearning() * (reward - previousUtility);

      double utility = previousUtility + partial;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(production + ".expectedUtility=" + utility + " previous="
            + previousUtility + " partial=" + partial + " reward=" + reward
            + " rate=" + getParameterLearning());

      return utility;
    }
  }

  public String getParameter(String key)
  {
    if (PARAMETER_LEARNING_RATE.equalsIgnoreCase(key))
      return "" + getParameterLearning();
    else if (OPTIMIZED_LEARNING.equalsIgnoreCase(key))
      return "" + getOptimizationLevel();
    else if (PRODUCTION_COMPILATION_PARAM.equalsIgnoreCase(key))
      return "" + isProductionCompilationEnabled();
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(PARAMETER_LEARNING_RATE, OPTIMIZED_LEARNING,
        PRODUCTION_COMPILATION_PARAM);
  }

  public void setParameter(String key, String value)
  {
    if (PARAMETER_LEARNING_RATE.equalsIgnoreCase(key))
      setParameterLearning(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (OPTIMIZED_LEARNING.equalsIgnoreCase(key))
      setOptimizationLevel(ParameterHandler.numberInstance().coerce(value)
          .intValue());
    else if (PRODUCTION_COMPILATION_PARAM.equalsIgnoreCase(key))
      setProductionCompilationEnabled(ParameterHandler.booleanInstance()
          .coerce(value).booleanValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to set " + key + "=" + value);
  }

  public void addListener(IProceduralLearningModule6Listener listener,
      Executor executor)
  {
    _dispatcher.addListener(listener, executor);
  }

  public void removeListener(IProceduralLearningModule6Listener listener)
  {
    _dispatcher.removeListener(listener);
  }

  public void reset()
  {
    // noop
  }
}
