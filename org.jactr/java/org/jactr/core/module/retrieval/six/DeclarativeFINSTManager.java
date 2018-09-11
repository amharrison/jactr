package org.jactr.core.module.retrieval.six;

import java.util.List;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.module.retrieval.event.IRetrievalModuleListener;
import org.jactr.core.module.retrieval.event.RetrievalModuleEvent;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.utils.collections.FastListFactory;

public class DeclarativeFINSTManager
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                 = LogFactory
                                                                .getLog(DeclarativeFINSTManager.class);

  static public final String         NUMBER_OF_FINSTS_PARAM = "NumberOfFINSTs";

  static public final String         FINST_DURATION_PARAM   = "FINSTDurationTime";

  private double                     _finstDurationTime     = 3;

  private int                        _numberOfFINSTs        = 4;

  /**
   * keys on the retrieved chunk w/ a timed event value that handles the
   * expiration of the item.
   */
  private LRUMap                     _recentlyRetrieved     = new LRUMap(
                                                                _numberOfFINSTs);

  private IRetrievalModuleListener   _retrievalListener;

  private IChunk                     _errorChunk;

  public DeclarativeFINSTManager(IRetrievalModule retrievalModule)
  {
    _retrievalListener = new IRetrievalModuleListener() {

      public void retrievalInitiated(RetrievalModuleEvent rme)
      {

      }

      public void retrievalCompleted(RetrievalModuleEvent rme)
      {
        IChunk retrieved = rme.getChunk();
        if (!_errorChunk.equals(retrieved))
          addRecentRetrieval(retrieved, rme.getSimulationTime());
      }
    };

    retrievalModule.addListener(_retrievalListener,
        ExecutorServices.INLINE_EXECUTOR);
  }

  protected void setErrorChunk(IChunk errorChunk)
  {
    _errorChunk = errorChunk;
  }

  public void setFINSTDuration(double duration)
  {
    _finstDurationTime = duration;
  }

  public double getFINSTDuration()
  {
    return _finstDurationTime;
  }

  public void setNumberOfFINSTs(int number)
  {
    _numberOfFINSTs = number;
    if (_numberOfFINSTs != _recentlyRetrieved.maxSize()) synchronized (this)
    {
      LRUMap old = _recentlyRetrieved;
      _recentlyRetrieved = new LRUMap(_numberOfFINSTs);
      _recentlyRetrieved.putAll(old);
    }
  }

  public int getNumberOfFINSTs()
  {
    return _numberOfFINSTs;
  }

  public void addRecentRetrieval(IChunk retrievedChunk, double when)
  {
    // just in case
    removeRecentRetrieval(retrievedChunk);

    ITimedEvent expirationEvent = new FINSTExpirationTimedEvent(when, when
        + _finstDurationTime, retrievedChunk);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(
          "New retrieval of %s will be marked as old @ %.2f", retrievedChunk,
          expirationEvent.getEndTime()));

    synchronized (this)
    {
      _recentlyRetrieved.put(retrievedChunk, expirationEvent);
    }

    retrievedChunk.getModel().getTimedEventQueue().enqueue(expirationEvent);
  }

  public void removeRecentRetrieval(IChunk retrievedChunk)
  {
    ITimedEvent expirationEvent = null;
    synchronized (this)
    {
      expirationEvent = (ITimedEvent) _recentlyRetrieved.remove(retrievedChunk);
    }

    if (expirationEvent != null && !expirationEvent.hasAborted()
        && !expirationEvent.hasFired()) expirationEvent.abort();
  }

  public void clearRecentRetrievals()
  {
    /*
     * abort the timed events
     */

    List<ITimedEvent> expirationEvents = FastListFactory.newInstance();
    synchronized (this)
    {
      expirationEvents.addAll(_recentlyRetrieved.values());
      _recentlyRetrieved.clear();
    }

    for (ITimedEvent event : expirationEvents)
      if (!event.hasAborted() && !event.hasFired()) event.abort();

    FastListFactory.recycle(expirationEvents);
  }

  synchronized public boolean hasBeenRetrieved(IChunk chunk)
  {
    return _recentlyRetrieved.containsKey(chunk);
  }

  private class FINSTExpirationTimedEvent extends AbstractTimedEvent
  {
    final private IChunk _retrievedChunk;

    public FINSTExpirationTimedEvent(double start, double end,
        IChunk retrievedChunk)
    {
      super(start, end);
      _retrievedChunk = retrievedChunk;
    }

    @Override
    public void fire(double currentTime)
    {
      super.fire(currentTime);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Removing dec-finst for %s @ %.2f",
            _retrievedChunk, currentTime));

      removeRecentRetrieval(_retrievedChunk);
    }

    @Override
    public void abort()
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Aborting expiration event for %s",
            _retrievedChunk));
      super.abort();
    }
  }
}
