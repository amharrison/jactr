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
package org.jactr.core.chunk.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.module.declarative.four.learning.IBaseLevelActivationEquation;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.CollectionParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.core.utils.references.IOptimizedReferences;
import org.jactr.core.utils.references.IReferences;

public abstract class AbstractSubsymbolicChunk implements ISubsymbolicChunk
{
  /**
   * logger definition
   */
  static private final Log               LOGGER                         = LogFactory
                                                                            .getLog(AbstractSubsymbolicChunk.class);

  final protected IChunk                 _parentChunk;

  protected IReferences                  _referenceList;

  protected double                       _creationTime;                                                             // encoding

  // time

  protected double                       _baseLevelActivation;

  protected double                       _spreadingActivation;

  protected double                       _sourceActivation;

  protected double                       _totalActivation;

  protected int                          _timesInContext;

  protected int                          _timesNeeded;

  protected double                       _lastActivationComputationTime = -1;

  protected IBaseLevelActivationEquation _baseLevelActiationEquation;

  public AbstractSubsymbolicChunk(IChunk parent)
  {
    _parentChunk = parent;
    try
    {
      _referenceList = IReferences.Factory.get().newInstance();

      /*
       * can we set the optimization?
       */
      IDeclarativeLearningModule4 dlm = (IDeclarativeLearningModule4) parent
          .getModel().getModule(IDeclarativeLearningModule4.class);

      if (dlm != null)
        _baseLevelActiationEquation = dlm.getBaseLevelActivationEquation();

      if (dlm != null && _referenceList instanceof IOptimizedReferences)
        ((IOptimizedReferences) _referenceList).setOptimizationLevel(dlm
            .getOptimizationLevel());
    }
    catch (Exception e)
    {
      throw new IllegalChunkStateException("Could not create references ", e);
    }
  }

  protected Lock readLock()
  {
    return _parentChunk.getReadLock();
  }

  protected Lock writeLock()
  {
    return _parentChunk.getWriteLock();
  }

  public void accessed(double time)
  {
    try
    {
      writeLock().lock();
      _referenceList.addReferenceTime(ACTRRuntime.getRuntime().getClock(
          _parentChunk.getModel()).getTime());
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasListeners())
      _parentChunk.dispatch(new ChunkEvent(_parentChunk,
          ChunkEvent.Type.ACCESSED));
  }

  public void dispose()
  {
    try
    {
      writeLock().lock();
      _referenceList.clear();
    }
    finally
    {
      writeLock().unlock();
    }
  }

  /**
   * non-locking since this will set creation time
   * 
   * @see org.jactr.core.chunk.ISubsymbolicChunk#encode(double)
   */
  public void encode(double when)
  {
    if (_parentChunk.isEncoded())
      throw new IllegalChunkStateException("Chunk has already been encoded");

    setCreationTime(ACTRRuntime.getRuntime().getClock(_parentChunk.getModel())
        .getTime());
  }

  public double getActivation()
  {
    refreshActivationValues();
    try
    {
      readLock().lock();
      return _totalActivation;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public double getBaseLevelActivation()
  {
    refreshActivationValues();

    try
    {
      readLock().lock();
      return _baseLevelActivation;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public double getCreationTime()
  {
    try
    {
      readLock().lock();
      return _creationTime;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public IReferences getReferences()
  {
    return _referenceList;
  }

  public double getSourceActivation()
  {
    try
    {
      readLock().lock();
      return _sourceActivation;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public double getSpreadingActivation()
  {
    refreshActivationValues();
    try
    {
      readLock().lock();
      return _spreadingActivation;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public int getTimesInContext()
  {
    try
    {
      readLock().lock();
      return _timesInContext;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public int getTimesNeeded()
  {
    try
    {
      readLock().lock();
      return _timesNeeded;
    }
    finally
    {
      readLock().unlock();
    }
  }

  public void incrementTimesInContext()
  {
    setTimesInContext(getTimesInContext() + 1);
  }

  public void incrementTimesNeeded()
  {
    setTimesNeeded(getTimesNeeded() + 1);
  }

  public void setActivation(double act)
  {
    double old = 0;
    try
    {
      writeLock().lock();
      old = _totalActivation;
      _totalActivation = act;
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), ACTIVATION, old, act));
  }

  public void setBaseLevelActivation(double base)
  {
    double old = 0;

    try
    {
      writeLock().lock();
      old = _baseLevelActivation;
      _baseLevelActivation = base;
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), BASE_LEVEL_ACTIVATION,
          old, base));
  }

  public void setCreationTime(double time)
  {
    if (_parentChunk.isEncoded())
      throw new IllegalChunkStateException(
          "Creation time cannot be changed after encoding");

    double old = 0;
    try
    {
      writeLock().lock();
      old = _creationTime;
      _creationTime = time;
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), CREATION_TIME, old,
          time));
  }

  public void setSourceActivation(double source)
  {
    double old = 0;
    try
    {
      writeLock().lock();
      old = _sourceActivation;
      _sourceActivation = source;
    }
    finally
    {
      writeLock().unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), SOURCE_ACTIVATION, old,
          source));
  }

  public void setSpreadingActivation(double spread)
  {
    double old = 0;
    try
    {
      writeLock().lock();
      old = _spreadingActivation;
      _spreadingActivation = spread;

    }
    finally
    {
      writeLock().unlock();
    }
    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), SPREADING_ACTIVATION,
          old, spread));

  }

  public void setTimesInContext(int context)
  {
    int old = 0;
    try
    {
      writeLock().lock();
      old = _timesInContext;
      _timesInContext = context;

    }
    finally
    {
      writeLock().unlock();
    }
    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), TIMES_IN_CONTEXT, old,
          context));
  }

  public void setTimesNeeded(int needed)
  {
    int old = 0;
    try
    {
      writeLock().lock();
      old = _timesNeeded;
      _timesNeeded = needed;

    }
    finally
    {
      writeLock().unlock();
    }
    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
          .getClock(_parentChunk.getModel()).getTime(), TIMES_NEEDED, old,
          needed));
  }

  public String getParameter(String key)
  {
    String rtn = null;
    if (CREATION_TIME.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance().toString(getCreationTime());
    else if (TIMES_NEEDED.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance().toString(getTimesNeeded());
    else if (TIMES_IN_CONTEXT.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance().toString(getTimesInContext());
    else if (REFERENCE_COUNT.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance().toString(
          getReferences().getNumberOfReferences());
    else if (REFERENCE_TIMES.equalsIgnoreCase(key))
    {
      double[] times = getReferences().getTimes();
      // make sure they are sorted
      Arrays.sort(times);

      Collection<Number> nTimes = new ArrayList<Number>(times.length);
      for (double time : times)
        nTimes.add(time);

      // make sure they are sorted

      CollectionParameterHandler<Number> aph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());
      return aph.toString(nTimes);
    }
    else if (BASE_LEVEL_ACTIVATION.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance()
          .toString(getBaseLevelActivation());
    else if (SPREADING_ACTIVATION.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance()
          .toString(getSpreadingActivation());
    else if (SOURCE_ACTIVATION.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance().toString(getSourceActivation());
    else if (ACTIVATION.equalsIgnoreCase(key))
      rtn = ParameterHandler.numberInstance().toString(getActivation());

    return rtn;
  }

  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> params = new ArrayList<String>();

    params.add(CREATION_TIME);
    params.add(TIMES_NEEDED);
    params.add(TIMES_IN_CONTEXT);
    params.add(REFERENCE_COUNT);
    params.add(REFERENCE_TIMES);
    params.add(BASE_LEVEL_ACTIVATION);
    params.add(SPREADING_ACTIVATION);
    params.add(SOURCE_ACTIVATION);
    params.add(ACTIVATION);
    return params;
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void setParameter(String key, String value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Attempting to set " + key + " to " + value);

    if (CREATION_TIME.equalsIgnoreCase(key))
      setCreationTime(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (TIMES_NEEDED.equalsIgnoreCase(key))
      setTimesNeeded(ParameterHandler.numberInstance().coerce(value).intValue());
    else if (TIMES_IN_CONTEXT.equalsIgnoreCase(key))
      setTimesInContext(ParameterHandler.numberInstance().coerce(value)
          .intValue());
    else if (REFERENCE_COUNT.equalsIgnoreCase(key))
    {
      long referenceCount = ParameterHandler.numberInstance().coerce(value)
          .longValue();
      IReferences references = getReferences();

      double[] oldTimes = references.getTimes();
      long oldCount = references.getNumberOfReferences();

      references.clear();

      /*
       * create referenceCount references from creation time to now
       */
      double min = getCreationTime();
      double step = (ACTRRuntime.getRuntime().getClock(
          getParentChunk().getModel()).getTime() - min)
          / referenceCount;
      for (int i = 0; i < referenceCount; i++)
        references.addReferenceTime(getCreationTime() + (i * step));

      _lastActivationComputationTime = -1;

      if (_parentChunk.hasParameterListeners())
      {
        _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
            .getClock(_parentChunk.getModel()).getTime(), REFERENCE_COUNT,
            oldCount, referenceCount));
        _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
            .getClock(_parentChunk.getModel()).getTime(), REFERENCE_TIMES,
            oldTimes, references.getTimes()));
      }
    }
    else if (REFERENCE_TIMES.equalsIgnoreCase(key))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Attempting to set reference times with " + value);

      CollectionParameterHandler<Number> cph = new CollectionParameterHandler<Number>(
          ParameterHandler.numberInstance());

      Collection<Number> times = cph.coerce(value);

      // let's make sure they are sorted..
      TreeSet<Double> refTimes = new TreeSet<Double>();
      for (Number time : times)
        refTimes.add(time.doubleValue());

      IReferences references = getReferences();
      double[] oldTimes = references.getTimes();

      /*
       * if count was previously set, we need to maintain it..
       */
      references.setNumberOfReferences(Math.max(0, references
          .getNumberOfReferences()
          - refTimes.size()));

      /*
       * now we'll add these times
       */
      for (Double time : refTimes)
        references.addReferenceTime(time);

      _lastActivationComputationTime = -1;

      if (_parentChunk.hasParameterListeners())
        _parentChunk.dispatch(new ParameterEvent(this, ACTRRuntime.getRuntime()
            .getClock(_parentChunk.getModel()).getTime(), REFERENCE_TIMES,
            oldTimes, references.getTimes()));
    }
    else if (BASE_LEVEL_ACTIVATION.equalsIgnoreCase(key))
      setBaseLevelActivation(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (SPREADING_ACTIVATION.equalsIgnoreCase(key))
      setSpreadingActivation(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (SOURCE_ACTIVATION.equalsIgnoreCase(key))
      setSourceActivation(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (ACTIVATION.equalsIgnoreCase(key))
      setActivation(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
  }

  protected void refreshActivationValues()
  {
    double now = ACTRRuntime.getRuntime().getClock(_parentChunk.getModel())
        .getTime();

    if (now > _lastActivationComputationTime
        && _baseLevelActiationEquation != null)
    {
      _lastActivationComputationTime = now;
      calculateValues();
    }
  }

  private void calculateValues()
  {
    setBaseLevelActivation(computeBaseLevelActivation());
    setSpreadingActivation(computeSpreadingActivation());
    setActivation(getBaseLevelActivation() + getSpreadingActivation());
  }

  private double computeBaseLevelActivation()
  {
    if (_baseLevelActiationEquation == null) return _baseLevelActivation;

    return _baseLevelActiationEquation.computeBaseLevelActivation(_parentChunk
        .getModel(), _parentChunk);
  }

  abstract protected double computeSpreadingActivation();

  protected IChunk getParentChunk()
  {
    return _parentChunk;
  }
}
