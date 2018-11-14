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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.DefaultAdaptable;
import org.jactr.core.utils.IAdaptableFactory;
import org.jactr.core.utils.parameter.CollectionParameterProcessor;
import org.jactr.core.utils.parameter.DoubleParameterProcessor;
import org.jactr.core.utils.parameter.LongParameterProcessor;
import org.jactr.core.utils.parameter.ParameterHelper;
import org.jactr.core.utils.references.IReferences;

public abstract class AbstractSubsymbolicChunk extends DefaultAdaptable
    implements ISubsymbolicChunk
{
  /**
   * logger definition
   */
  static private final Log                   LOGGER                         = LogFactory
      .getLog(AbstractSubsymbolicChunk.class);

  protected IChunk                           _parentChunk;

  protected IReferences                      _referenceList;

  protected double                           _creationTime;                                                           // encoding

  // time

  protected double                           _baseLevelActivation           = Double.NaN;

  protected double                           _spreadingActivation;

  protected double                           _randomActivation;

  protected Map<IActivationBuffer, Double>   _sourceActivation;

  protected double                           _totalActivation;

  protected double                           _timesInContext;

  protected double                           _timesNeeded;

  protected double                           _lastActivationComputationTime = -1;

  private Collection<IActivationParticipant> _activationParticipants        = new ArrayList<IActivationParticipant>();

  protected ParameterHelper                  _parameterHelper               = new ParameterHelper();

  public AbstractSubsymbolicChunk()
  {
    // factory? really. this should be changed
    _referenceList = IReferences.Factory.get().newInstance();
    initializeParameters();

    /**
     * install adapter handler for parameterHelper
     */
    addAdapterFactory(new IAdaptableFactory() {

      @Override
      public boolean shouldSoftCache()
      {
        return false;
      }

      @Override
      public boolean shouldCache()
      {
        return true;
      }

      @Override
      public <T> T adapt(Object sourceObject)
      {
        return (T) ((AbstractSubsymbolicChunk) sourceObject)
            .getParameterHelper();
      }
    }, new Class[] { ParameterHelper.class });
  }

  public ParameterHelper getParameterHelper()
  {
    return _parameterHelper;
  }

  public void bind(IChunk wrapper)
  {
    _parentChunk = wrapper;
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
    Lock l = writeLock();
    try
    {
      l.lock();
      _referenceList.addReferenceTime(time);
    }
    finally
    {
      l.unlock();
    }

    if (_parentChunk.hasListeners()) _parentChunk
        .dispatch(new ChunkEvent(_parentChunk, ChunkEvent.Type.ACCESSED));
  }

  public void dispose()
  {
    _referenceList.clear();
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

    setCreationTime(when);
  }

  public void addActivationParticipant(IActivationParticipant participant)
  {
    _activationParticipants.add(participant);
  }

  public void removeActivationParticipant(IActivationParticipant participant)
  {
    _activationParticipants.remove(participant);
  }

  public Collection<IActivationParticipant> getActivationParticipants(
      Collection<IActivationParticipant> container)
  {
    if (container == null) container = new ArrayList<IActivationParticipant>();
    container.addAll(_activationParticipants);
    return container;
  }

  public double getActivation()
  {
    Lock l = readLock();
    refreshActivationValues();
    try
    {
      l.lock();
      return _totalActivation;
    }
    finally
    {
      l.unlock();
    }
  }

  public double getBaseLevelActivation()
  {
    Lock l = readLock();
    refreshActivationValues();

    try
    {
      l.lock();
      return _baseLevelActivation;
    }
    finally
    {
      l.unlock();
    }
  }

  public double getRandomActivation()
  {
    Lock l = readLock();
    refreshActivationValues();

    try
    {
      l.lock();
      return _randomActivation;
    }
    finally
    {
      l.unlock();
    }
  }

  public double getCreationTime()
  {
    Lock l = readLock();
    try
    {
      l.lock();
      return _creationTime;
    }
    finally
    {
      l.unlock();
    }
  }

  public IReferences getReferences()
  {
    return _referenceList;
  }

  public double getSourceActivation()
  {
    Lock l = readLock();
    try
    {
      l.lock();
      if (_sourceActivation == null) return 0;

      double source = 0;
      for (Double act : _sourceActivation.values())
        source += act;

      return source;
    }
    finally
    {
      l.unlock();
    }
  }

  public double getSourceActivation(IActivationBuffer buffer)
  {
    Lock l = readLock();
    try
    {
      l.lock();
      if (_sourceActivation == null) return 0;

      if (_sourceActivation.containsKey(buffer))
        return _sourceActivation.get(buffer);
      else
        return 0;
    }
    finally
    {
      l.unlock();
    }
  }

  public double getSpreadingActivation()
  {
    Lock l = readLock();
    refreshActivationValues();
    try
    {
      l.lock();
      return _spreadingActivation;
    }
    finally
    {
      l.unlock();
    }
  }

  public double getTimesInContext()
  {
    Lock l = readLock();
    try
    {
      l.lock();
      return _timesInContext;
    }
    finally
    {
      l.unlock();
    }
  }

  public double getTimesNeeded()
  {
    Lock l = readLock();
    try
    {
      l.lock();
      return _timesNeeded;
    }
    finally
    {
      l.unlock();
    }
  }

  public void incrementTimesInContext(double value)
  {
    setTimesInContext(getTimesInContext() + value);
  }

  public void incrementTimesNeeded(double value)
  {
    setTimesNeeded(getTimesNeeded() + value);
  }

  public void setActivation(double act)
  {
    double old = 0;
    Lock l = writeLock();
    try
    {
      l.lock();
      old = _totalActivation;
      _totalActivation = act;
    }
    finally
    {
      l.unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          ACTIVATION, old, act));
  }

  public void setBaseLevelActivation(double base)
  {
    double old = 0;
    Lock l = writeLock();
    try
    {
      l.lock();
      old = _baseLevelActivation;
      _baseLevelActivation = base;
    }
    finally
    {
      l.unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          BASE_LEVEL_ACTIVATION, old, base));
  }

  public void setRandomActivation(double random)
  {

    Lock l = writeLock();
    try
    {
      l.lock();
      _randomActivation = random;
    }
    finally
    {
      l.unlock();
    }
  }

  public void setCreationTime(double time)
  {
    // if (_parentChunk.isEncoded())
    // throw new IllegalChunkStateException(
    // "Creation time cannot be changed after encoding");
    Lock l = writeLock();
    double old = 0;
    try
    {
      l.lock();
      old = _creationTime;
      _creationTime = time;
    }
    finally
    {
      l.unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          CREATION_TIME, old, time));
  }

  public void setSourceActivation(IActivationBuffer sourceBuffer, double source)
  {
    double old = getSourceActivation();

    if (sourceBuffer == null) return;
    // noop

    Lock l = writeLock();
    try
    {
      l.lock();

      // this is a removal, we should see if we can dispose of the map.
      if (source == 0 && _sourceActivation != null)
      {
        _sourceActivation.remove(sourceBuffer);
        if (_sourceActivation.size() == 0) _sourceActivation = null;
      }
      else if (source != 0)
      {
        if (_sourceActivation == null)
          _sourceActivation = new HashMap<IActivationBuffer, Double>();
        _sourceActivation.put(sourceBuffer, source);
      }
    }
    finally
    {
      l.unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          SOURCE_ACTIVATION, old, source));
  }

  public void setSpreadingActivation(double spread)
  {
    double old = 0;
    Lock l = writeLock();
    try
    {
      l.lock();
      old = _spreadingActivation;
      _spreadingActivation = spread;

    }
    finally
    {
      l.unlock();
    }
    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          SPREADING_ACTIVATION, old, spread));

  }

  public void setTimesInContext(double context)
  {
    double old = 0;
    Lock l = writeLock();
    try
    {
      l.lock();
      old = _timesInContext;
      _timesInContext = context;

    }
    finally
    {
      l.unlock();
    }

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          TIMES_IN_CONTEXT, old, context));
  }

  public void setTimesNeeded(double needed)
  {
    Lock l = writeLock();
    double old = 0;
    try
    {
      l.lock();
      old = _timesNeeded;
      _timesNeeded = needed;

    }
    finally
    {
      l.unlock();
    }
    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          TIMES_NEEDED, old, needed));
  }

  protected void initializeParameters()
  {
    // parameter help hooks.

    _parameterHelper.addProcessor(new DoubleParameterProcessor(CREATION_TIME,
        this::setCreationTime, this::getCreationTime));
    _parameterHelper.addProcessor(new DoubleParameterProcessor(TIMES_NEEDED,
        this::setTimesNeeded, this::getTimesNeeded));
    _parameterHelper.addProcessor(new DoubleParameterProcessor(TIMES_IN_CONTEXT,
        this::setTimesInContext, this::getTimesInContext));
    _parameterHelper
        .addProcessor(new DoubleParameterProcessor(BASE_LEVEL_ACTIVATION,
            this::setBaseLevelActivation, this::getBaseLevelActivation));
    _parameterHelper
        .addProcessor(new DoubleParameterProcessor(SPREADING_ACTIVATION,
            this::setSpreadingActivation, this::getSpreadingActivation));
    _parameterHelper.addProcessor(new DoubleParameterProcessor(
        SOURCE_ACTIVATION, null, this::getSourceActivation));
    _parameterHelper.addProcessor(new DoubleParameterProcessor(ACTIVATION,
        this::setActivation, this::getActivation));

    _parameterHelper.addProcessor(new LongParameterProcessor(REFERENCE_COUNT,
        this::createInitialReferenceCount,
        _referenceList::getNumberOfReferences));

    _parameterHelper.addProcessor(new CollectionParameterProcessor<Double>(
        REFERENCE_TIMES, this::setInitialReferenceTimes, () -> {
          return _referenceList.getTimes(Lists.mutable.empty());
        }, new DoubleParameterProcessor(null, null, null), true));
  }

  /**
   * called when REFERENCE_COUNT is set via parameter. This clears the
   * references and adds initialCount references.
   * 
   * @param initialCount
   */
  protected void createInitialReferenceCount(long initialCount)
  {
    IReferences references = getReferences();

    double[] oldTimes = references.getTimes();
    long oldCount = references.getNumberOfReferences();

    references.clear();

    /*
     * create referenceCount references from creation time to now
     */
    double min = getCreationTime();
    double step = (getParentChunk().getModel().getAge() - min) / initialCount;
    for (int i = 0; i < initialCount; i++)
      references.addReferenceTime(getCreationTime() + i * step);

    _lastActivationComputationTime = -1;

    if (_parentChunk.hasParameterListeners())
    {
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          REFERENCE_COUNT, oldCount, initialCount));
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          REFERENCE_TIMES, oldTimes, references.getTimes()));
    }
  }

  protected void setInitialReferenceTimes(Collection<Double> referenceTimes)
  {
    IReferences references = getReferences();
    double[] oldTimes = references.getTimes();

    /*
     * roll back the reference count
     */
    references.setNumberOfReferences(Math.max(0,
        references.getNumberOfReferences() - referenceTimes.size()));

    /*
     * add the new times
     */
    referenceTimes.forEach(references::addReferenceTime);

    _lastActivationComputationTime = -1;

    if (_parentChunk.hasParameterListeners())
      _parentChunk.dispatch(new ParameterEvent(this,
          ACTRRuntime.getRuntime().getClock(_parentChunk.getModel()).getTime(),
          REFERENCE_TIMES, oldTimes, references.getTimes()));
  }

  public String getParameter(String key)
  {
    return _parameterHelper.getParameter(key);

//    String rtn = null;
//    // short-term fix until everything is migrated to ParameterProcessor/Helper
//
//    if (CREATION_TIME.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance().toString(getCreationTime());
//    else if (TIMES_NEEDED.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance().toString(getTimesNeeded());
//    else if (TIMES_IN_CONTEXT.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance().toString(getTimesInContext());
//    else if (REFERENCE_COUNT.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance()
//          .toString(getReferences().getNumberOfReferences());
//    else if (REFERENCE_TIMES.equalsIgnoreCase(key))
//    {
//      double[] times = getReferences().getTimes((double[]) null);
//      // make sure they are sorted
//      Arrays.sort(times);
//
//      Collection<Number> nTimes = new ArrayList<Number>(times.length);
//      for (double time : times)
//        nTimes.add(time);
//
//      // make sure they are sorted
//
//      CollectionParameterHandler<Number> aph = new CollectionParameterHandler<Number>(
//          ParameterHandler.numberInstance());
//      return aph.toString(nTimes);
//    }
//    else if (BASE_LEVEL_ACTIVATION.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance()
//          .toString(getBaseLevelActivation());
//    else if (SPREADING_ACTIVATION.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance()
//          .toString(getSpreadingActivation());
//    else if (SOURCE_ACTIVATION.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance().toString(getSourceActivation());
//    else if (ACTIVATION.equalsIgnoreCase(key))
//      rtn = ParameterHandler.numberInstance().toString(getActivation());
//    else
//      rtn = _unknownParameters.get(key);
//
//    return rtn;
  }

  public Collection<String> getPossibleParameters()
  {
//    Collection<String> setable = getSetableParameters();
//    setable.add(SOURCE_ACTIVATION);
//    // setable.add(SPREADING_ACTIVATION);
//    return setable;
    return _parameterHelper.getParameterNames(Sets.mutable.empty());
  }

  public Collection<String> getSetableParameters()
  {
//    ArrayList<String> params = new ArrayList<String>();
//
//    params.addAll(_unknownParameters.keySet());
//    params.add(CREATION_TIME);
//    params.add(TIMES_NEEDED);
//    params.add(TIMES_IN_CONTEXT);
//    params.add(REFERENCE_COUNT);
//    params.add(REFERENCE_TIMES);
//    params.add(BASE_LEVEL_ACTIVATION);
//    params.add(SPREADING_ACTIVATION);
//    // params.add(SOURCE_ACTIVATION);
//    params.add(ACTIVATION);
//    return params;
    return _parameterHelper.getSetableParameterNames(Sets.mutable.empty());
  }

  public void setParameter(String key, String value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Attempting to set " + key + " to " + value);

//    if (CREATION_TIME.equalsIgnoreCase(key))
//      setCreationTime(
//          ParameterHandler.numberInstance().coerce(value).doubleValue());
//    else if (TIMES_NEEDED.equalsIgnoreCase(key))
//      setTimesNeeded(
//          ParameterHandler.numberInstance().coerce(value).doubleValue());
//    else if (TIMES_IN_CONTEXT.equalsIgnoreCase(key))
//      setTimesInContext(
//          ParameterHandler.numberInstance().coerce(value).doubleValue());
//    else if (REFERENCE_COUNT.equalsIgnoreCase(key))
//    {
//      long referenceCount = ParameterHandler.numberInstance().coerce(value)
//          .longValue();
//      IReferences references = getReferences();
//
//      double[] oldTimes = references.getTimes();
//      long oldCount = references.getNumberOfReferences();
//
//      references.clear();
//
//      /*
//       * create referenceCount references from creation time to now
//       */
//      double min = getCreationTime();
//      double step = (getParentChunk().getModel().getAge() - min)
//          / referenceCount;
//      for (int i = 0; i < referenceCount; i++)
//        references.addReferenceTime(getCreationTime() + i * step);
//
//      _lastActivationComputationTime = -1;
//
//      if (_parentChunk.hasParameterListeners())
//      {
//        _parentChunk
//            .dispatch(
//                new ParameterEvent(
//                    this, ACTRRuntime.getRuntime()
//                        .getClock(_parentChunk.getModel()).getTime(),
//                    REFERENCE_COUNT, oldCount, referenceCount));
//        _parentChunk.dispatch(new ParameterEvent(this,
//            ACTRRuntime.getRuntime().getClock(_parentChunk.getModel())
//                .getTime(),
//            REFERENCE_TIMES, oldTimes, references.getTimes()));
//      }
//    }
//    else if (REFERENCE_TIMES.equalsIgnoreCase(key))
//    {
//      if (LOGGER.isDebugEnabled())
//        LOGGER.debug("Attempting to set reference times with " + value);
//
//      CollectionParameterHandler<Number> cph = new CollectionParameterHandler<Number>(
//          ParameterHandler.numberInstance());
//
//      Collection<Number> times = cph.coerce(value);
//
//      // let's make sure they are sorted..
//      TreeSet<Double> refTimes = new TreeSet<Double>();
//      for (Number time : times)
//        refTimes.add(time.doubleValue());
//
//      IReferences references = getReferences();
//      double[] oldTimes = references.getTimes();
//
//      /*
//       * if count was previously set, we need to maintain it..
//       */
//      references.setNumberOfReferences(
//          Math.max(0, references.getNumberOfReferences() - refTimes.size()));
//
//      /*
//       * now we'll add these times
//       */
//      for (Double time : refTimes)
//        references.addReferenceTime(time);
//
//      _lastActivationComputationTime = -1;
//
//      if (_parentChunk.hasParameterListeners())
//        _parentChunk.dispatch(new ParameterEvent(this,
//            ACTRRuntime.getRuntime().getClock(_parentChunk.getModel())
//                .getTime(),
//            REFERENCE_TIMES, oldTimes, references.getTimes()));
//    }
//    else if (BASE_LEVEL_ACTIVATION.equalsIgnoreCase(key))
//      setBaseLevelActivation(
//          ParameterHandler.numberInstance().coerce(value).doubleValue());
//    else if (SPREADING_ACTIVATION.equalsIgnoreCase(key))
//      setSpreadingActivation(
//          ParameterHandler.numberInstance().coerce(value).doubleValue());
//    else if (SOURCE_ACTIVATION.equalsIgnoreCase(key))
//    {
//      if (!_setSourceWarned)
//      {
//        _setSourceWarned = true;
//        if (LOGGER.isWarnEnabled()) LOGGER.warn(String.format(
//            "Setting source activation directly via setParameter no longer supported. "));
//      }
//      setSourceActivation(null,
//          ParameterHandler.numberInstance().coerce(value).doubleValue());
//    }
//    else if (ACTIVATION.equalsIgnoreCase(key))
//      setActivation(
//          ParameterHandler.numberInstance().coerce(value).doubleValue());
//    else
//    {
//      /*
//       * an unknown parameter. We store and fire an event. This allows others to
//       * add parameters with no hassle.
//       */
//      String oldValue = _unknownParameters.put(key, value);
//      if (_parentChunk.hasParameterListeners()) _parentChunk
//          .dispatch(
//              new ParameterEvent(
//                  this, ACTRRuntime.getRuntime()
//                      .getClock(_parentChunk.getModel()).getTime(),
//                  key, oldValue, value));
//    }
    _parameterHelper.setParameter(key, value);
  }

  protected void refreshActivationValues()
  {
    double now = getParentChunk().getModel().getAge();

    if (now > _lastActivationComputationTime)
    {
      _lastActivationComputationTime = now;
      calculateValues();
    }
  }

  protected void calculateValues()
  {
    IModel model = getParentChunk().getModel();
    IChunk self = getParentChunk();

    double total = 0;
    for (IActivationParticipant actCalc : _activationParticipants)
    {
      double act = actCalc.computeAndSetActivation(self, model);
      total += Double.isNaN(act) || Double.isInfinite(act) ? 0 : act;
      if (LOGGER.isDebugEnabled()) LOGGER
          .debug(String.format("%s %s = %.2f", self, actCalc.getName(), act));
    }

    setActivation(total);
  }

  // abstract protected double computeBaseLevelActivation();

  /**
   * return the spreading activation value
   * 
   * @return
   */
  // abstract protected double computeSpreadingActivation();

  // abstract protected double computeRandomActivation();

  public IChunk getParentChunk()
  {
    return _parentChunk;
  }
}
