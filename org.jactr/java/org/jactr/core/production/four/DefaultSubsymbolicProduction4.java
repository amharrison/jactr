/*
 * Created on Nov 20, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.production.four;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.four.IProceduralModule4;
import org.jactr.core.module.procedural.four.learning.IProceduralLearningModule4;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.basic.BasicSubsymbolicProduction;
import org.jactr.core.production.event.ProductionEvent;
import org.jactr.core.utils.parameter.CollectionParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.core.utils.references.IOptimizedReferences;
import org.jactr.core.utils.references.IReferences;

public class DefaultSubsymbolicProduction4 extends BasicSubsymbolicProduction
    implements ISubsymbolicProduction4
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(DefaultSubsymbolicProduction4.class);

  protected int            _creationCycle;

  protected int            _priorFailures;

  protected int            _priorSuccesses;

  protected double         _priorEfforts;

  protected double         _expectedGain;

  protected double         _gainNoise;

  protected double         _p     = 1;

  protected double         _c     = 0.05;

  protected IReferences    _successes;

  protected IReferences    _failures;

  protected IReferences    _efforts;

  public DefaultSubsymbolicProduction4(IProduction parent, IModel model)
  {
    super(parent, model);
    _successes = IReferences.Factory.get().newInstance();
    _failures = IReferences.Factory.get().newInstance();
    _efforts = IReferences.Factory.get().newInstance();
  }

  @Override
  public void dispose()
  {
    _successes.clear();
    _successes = null;
    _failures.clear();
    _failures = null;
    _efforts.clear();
    _efforts = null;
    super.dispose();
  }

  @Override
  protected void setDefaultParameters()
  {
    super.setDefaultParameters();
    setP(1.0);
    setPriorSuccesses(1);
    setPriorFailures(0);

    IProceduralModule pm = _parentProduction.getModel().getProceduralModule();
    if (pm != null && pm instanceof IProceduralModule4)
    {
      setFiringTime(pm.getDefaultProductionFiringTime());
      setC(pm.getDefaultProductionFiringTime());
      setPriorEfforts(pm.getDefaultProductionFiringTime());
    }
    else
      setC(0.05);

    /*
     * can we optimize?
     */
    IProceduralLearningModule4 plm = (IProceduralLearningModule4) _parentProduction
        .getModel().getModule(IProceduralLearningModule4.class);
    if (plm != null)
      synchronized (this)
      {
        _costEquation = plm.getCostEquation();
        _probabilityEquation = plm.getProbabilityEquation();

        if (_successes instanceof IOptimizedReferences)
          ((IOptimizedReferences) _successes).setOptimizationLevel(plm
              .getOptimizationLevel());
        if (_failures instanceof IOptimizedReferences)
          ((IOptimizedReferences) _failures).setOptimizationLevel(plm
              .getOptimizationLevel());
        if (_efforts instanceof IOptimizedReferences)
          ((IOptimizedReferences) _efforts).setOptimizationLevel(plm
              .getOptimizationLevel());
      }
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = new ArrayList<String>(Arrays.asList(new String[] {
        P, C, GAIN, SUCCESS, PRIOR_SUCCESSES, SUCCESS_COUNT, SUCCESS_TIMES,
        FAILURE, PRIOR_FAILURES, FAILURE_COUNT, FAILURE_TIMES, PRIOR_EFFORTS,
        EFFORT_COUNT, EFFORT_TIMES, CREATION_CYCLE }));
    rtn.addAll(super.getSetableParameters());
    return rtn;
  }

  public int getCreationCycle()
  {
    return _creationCycle;
  }

  public void setCreationCycle(int i)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(_parentProduction + " Setting creation cycle = " + i);
    int oldValue = _creationCycle;
    int newValue = i;

    _creationCycle = i;
    _parameterMap.put(CREATION_CYCLE, newValue);
    _lastFiringTime = Double.NEGATIVE_INFINITY;

    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          CREATION_CYCLE, oldValue, newValue));
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (CREATION_CYCLE.equals(key))
      setCreationCycle(ParameterHandler.numberInstance().coerce(value)
          .intValue());
    else if (SUCCESS.equals(key))
      ((ISymbolicProduction4) _parentProduction.getSymbolicProduction())
          .setSuccessful(ParameterHandler.booleanInstance().coerce(value)
              .booleanValue());
    else if (FAILURE.equals(key))
      ((ISymbolicProduction4) _parentProduction.getSymbolicProduction())
          .setFailure(ParameterHandler.booleanInstance().coerce(value)
              .booleanValue());
    else if (P.equalsIgnoreCase(key))
      setP(ParameterHandler.numberInstance().coerce(value).doubleValue());
    else if (C.equalsIgnoreCase(key))
      setC(ParameterHandler.numberInstance().coerce(value).doubleValue());
    else if (PRIOR_EFFORTS.equalsIgnoreCase(key))
      setPriorEfforts(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (PRIOR_SUCCESSES.equalsIgnoreCase(key))
      setPriorSuccesses(ParameterHandler.numberInstance().coerce(value)
          .intValue());
    else if (PRIOR_FAILURES.equalsIgnoreCase(key))
      setPriorFailures(ParameterHandler.numberInstance().coerce(value)
          .intValue());
    else if (FAILURE_TIMES.equalsIgnoreCase(key))
    {
      CollectionParameterHandler<Number> cph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());
      setReferenceTimes(cph.coerce(value), _failures, FAILURE_TIMES);
    }
    else if (FAILURE_COUNT.equalsIgnoreCase(key))
      setReferenceCount(ParameterHandler.numberInstance().coerce(value)
          .longValue(), _failures, FAILURE_COUNT, FAILURE_TIMES);
    else if (SUCCESS_TIMES.equalsIgnoreCase(key))
    {
      CollectionParameterHandler<Number> cph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());
      setReferenceTimes(cph.coerce(value), _successes, SUCCESS_TIMES);
    }
    else if (SUCCESS_COUNT.equalsIgnoreCase(key))
      setReferenceCount(ParameterHandler.numberInstance().coerce(value)
          .longValue(), _successes, SUCCESS_COUNT, SUCCESS_TIMES);
    else if (EFFORT_TIMES.equalsIgnoreCase(key))
    {
      CollectionParameterHandler<Number> cph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());
      setReferenceTimes(cph.coerce(value), _efforts, EFFORT_TIMES);
    }
    else if (EFFORT_COUNT.equalsIgnoreCase(key))
      setReferenceCount(ParameterHandler.numberInstance().coerce(value)
          .longValue(), _efforts, EFFORT_COUNT, EFFORT_TIMES);
    else
      super.setParameter(key, value);
  }

  @Override
  public String getParameter(String key)
  {
    String rtn = null;
    if (CREATION_CYCLE.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getCreationCycle());
    else if (SUCCESS.equals(key))
      rtn = ParameterHandler.booleanInstance().toString(
          ((ISymbolicProduction4) _parentProduction.getSymbolicProduction())
              .isSuccessful());
    else if (FAILURE.equals(key))
      rtn = ParameterHandler.booleanInstance().toString(
          ((ISymbolicProduction4) _parentProduction.getSymbolicProduction())
              .isFailure());
    else if (PRIOR_EFFORTS.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getPriorEfforts());
    else if (PRIOR_SUCCESSES.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getPriorSuccesses());
    else if (PRIOR_FAILURES.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getPriorFailures());
    else if (GAIN.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getExpectedGain());
    else if (P.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getP());
    else if (C.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getC());
    else if (FAILURE_COUNT.equals(key))
      rtn = ParameterHandler.numberInstance().toString(
          _failures.getNumberOfReferences());
    else if (SUCCESS_COUNT.equals(key))
      rtn = ParameterHandler.numberInstance().toString(
          _successes.getNumberOfReferences());
    else if (EFFORT_COUNT.equals(key))
      rtn = ParameterHandler.numberInstance().toString(
          _efforts.getNumberOfReferences());
    else if (FAILURE_TIMES.equals(key))
    {
      Collection<Number> nTimes = new ArrayList<Number>();
      for (double time : _failures.getTimes())
        nTimes.add(new Double(time));

      CollectionParameterHandler<Number> aph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());
      rtn = aph.toString(nTimes);
    }
    else if (SUCCESS_TIMES.equals(key))
    {
      Collection<Number> nTimes = new ArrayList<Number>();
      for (double time : _successes.getTimes())
        nTimes.add(new Double(time));

      CollectionParameterHandler<Number> aph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());
      rtn = aph.toString(nTimes);
    }
    else if (EFFORT_TIMES.equals(key))
    {
      Collection<Number> nTimes = new ArrayList<Number>();
      for (double time : _efforts.getTimes())
        nTimes.add(new Double(time));

      CollectionParameterHandler<Number> aph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());
      rtn = aph.toString(nTimes);
    }
    else
      rtn = super.getParameter(key);
    return rtn;
  }

  public int getPriorSuccesses()
  {
    return _priorSuccesses;
  }

  public void setPriorSuccesses(int suc)
  {
    int oldValue = _priorSuccesses;
    _priorSuccesses = suc;
    _lastFiringTime = Double.NEGATIVE_INFINITY;
    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          PRIOR_SUCCESSES, oldValue, suc));
  }

  public int getPriorFailures()
  {
    return _priorFailures;
  }

  public void setPriorFailures(int suc)
  {
    int oldValue = _priorFailures;
    _priorFailures = suc;
    _lastFiringTime = Double.NEGATIVE_INFINITY;

    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          PRIOR_FAILURES, oldValue, suc));
  }

  public double getPriorEfforts()
  {
    return _priorEfforts;
  }

  public void setPriorEfforts(double eff)
  {
    double oldValue = _priorEfforts;
    _priorEfforts = eff;
    _lastFiringTime = Double.NEGATIVE_INFINITY;
    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          PRIOR_EFFORTS, oldValue, eff));
  }

  public IReferences getSuccesses()
  {
    return _successes;
  }

  public IReferences getFailures()
  {
    return _failures;
  }

  public IReferences getEfforts()
  {
    return _efforts;
  }

  /**
   * returns the expected gain based on the goal buffer's G value (ick)
   */
  public double getExpectedGain()
  {
    if (partialsAreDirty()) computePartials();

    computeExpectedGain(_parentProduction.getModel().getActivationBuffer(
        IActivationBuffer.GOAL).getG());
    double gain = _expectedGain + _gainNoise;
    return gain;
  }

  public synchronized double getP()
  {
    if (partialsAreDirty()) computePartials();

    return _p;
  }

  public synchronized double getC()
  {
    if (partialsAreDirty()) computePartials();

    return _c;
  }

  public void setC(double c)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(_parentProduction + " Setting c = " + c);
    double oldValue = _c;
    double newValue = c;

    _c = c;
    _parameterMap.put(C, newValue);
    _lastFiringTime = Double.NEGATIVE_INFINITY;

    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction, C,
          oldValue, newValue));
  }

  public void setP(double p)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(_parentProduction + " Setting p = " + p);
    double newValue = p;
    double oldValue = _p;
    _p = p;
    _parameterMap.put(P, newValue);
    _lastFiringTime = Double.NEGATIVE_INFINITY;
    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction, P,
          oldValue, newValue));
  }

  /**
   * partials must be computed before this
   */
  protected void computeExpectedGain(double g)
  {
    _gainNoise = ((IProceduralModule4) _parentProduction.getModel()
        .getProceduralModule()).getExpectedGainNoise();

    _expectedGain = _p * g - _c;
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(_parentProduction + " expected gain " + _expectedGain
          + " noise " + _gainNoise + " from g " + g);
  }

  /**
   * compute P & C based on the relevant equations
   */
  protected synchronized void computePartials()
  {
    IModel parentModel = _parentProduction.getModel();
    if (_probabilityEquation != null)
    {
      double p = _probabilityEquation.computeProbability(parentModel,
          _parentProduction);
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Computed p to be " + p);
      setP(p);
    }
    else
      setP(_p);

    if (_costEquation != null)
    {
      double c = _costEquation.computeCost(parentModel, _parentProduction);
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Computed c to be " + c);
      setC(c);
    }
    else
      setC(_c);

    _lastFiringTime = parentModel.getAge();
  }

  /**
   * check the _lastFiring time to determine if we need to recalculate the
   * partials..
   * 
   * @return
   */
  protected boolean partialsAreDirty()
  {
    return _lastFiringTime < _parentProduction.getModel().getAge();
  }

  protected void setReferenceCount(long referenceCount, IReferences references,
      String countParameterName, String timesParameterName)
  {
    double[] oldTimes = references.getTimes();
    long oldCount = references.getNumberOfReferences();
    references.clear();

    double min = getCreationTime();
    double delta = (_parentProduction.getModel().getAge() - min)
        / referenceCount;

    for (int i = 0; i < referenceCount; i++)
      references.addReferenceTime(min + i * delta);

    _lastFiringTime = Double.NEGATIVE_INFINITY;

    if (_parentProduction.hasListeners())
    {
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          countParameterName, references.getNumberOfReferences(), oldCount));
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          timesParameterName, references.getTimes(), oldTimes));
    }
  }

  protected void setReferenceTimes(Collection<Number> times,
      IReferences references, String parameterName)
  {
    double[] oldTimes = references.getTimes();

    /*
     * we do not clear here since count may have been set and this reference
     * list may be optimized
     */

    TreeSet<Double> refTimes = new TreeSet<Double>();
    for (Number time : times)
      refTimes.add(time.doubleValue());

    /*
     * adjust the reference count so that it will be the same after we add these
     * references
     */
    references.setNumberOfReferences(Math.max(0, references
        .getNumberOfReferences()
        - refTimes.size()));

    for (Double time : refTimes)
      references.addReferenceTime(time);

    _lastFiringTime = Double.NEGATIVE_INFINITY;

    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          parameterName, references.getTimes(), oldTimes));
  }
}
