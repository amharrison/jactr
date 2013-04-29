package org.jactr.core.buffer.six;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.ICapacityBuffer;
import org.jactr.core.buffer.IllegalActivationBufferStateException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.runtime.ACTRRuntime;

public abstract class AbstractCapacityBuffer6 extends
    AbstractRequestableBuffer6 implements ICapacityBuffer
{

  /**
   * Logger definition
   */
  static private transient Log            LOGGER       = LogFactory
                                                           .getLog(AbstractCapacityBuffer6.class);

  private boolean                         _initialized = false;

  protected EjectionPolicy                _ejectionPolicy;

  /*
   * we store the source chunks by their match/insert times
   */
  final private SortedMap<Double, IChunk> _sourceChunks;

  /*
   * but we need them to be bidirectional so that we can remove the key
   * correctly
   */
  final private Map<IChunk, Double>       _times;

  public AbstractCapacityBuffer6(String name, IModule module)
  {
    super(name, module);
    _sourceChunks = new TreeMap<Double, IChunk>();
    _times = new HashMap<IChunk, Double>();
  }

  public void initialize()
  {
    super.initialize();
    _initialized = true;
  }

  public void dispose()
  {
    super.dispose();
    try
    {
      getLock().writeLock().lock();
      _sourceChunks.clear();
      _times.clear();
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  /**
   * return true if the capacity has been reached and someone needs to be
   * removed
   * 
   * @return
   */
  abstract protected boolean isCapacityReached();

  /**
   * return true if a chunk of this type can be added to the buffer
   * 
   * @return
   */
  abstract protected boolean isValidChunkType(IChunkType chunkType);

  /**
   * returns the actual backing map used. This should only be used with great
   * caution and be sure when you do so you are wrapped within at least a
   * readlock
   * 
   * @return
   */
  protected Map<Double, IChunk> getTimesAndChunks()
  {
    return _sourceChunks;
  }

  protected Map<IChunk, Double> getChunksAndTimes()
  {
    return _times;
  }

  /**
   * must only be called within the write lock. will remove chunks until
   * capacity is no longer reached (i.e. there is room for one more chunk) -
   * this is called by addSourceChunkInternal
   */
  private void ensureCapacity()
  {
    IModel model = getModel();
    StringBuilder sb = null;
    while (isCapacityReached())
    {
      if (sb == null)
        sb = new StringBuilder();
      else
        sb.delete(0, sb.length());

      /*
       * remove the first..
       */
      IChunk toRemove = getSourceChunk();
      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        sb.append(toRemove.toString());
        sb.append(" is being removed because capacity has been reached");
        String msg = sb.toString();

        LOGGER.debug(msg);
        Logger.log(model, Logger.Stream.BUFFER, msg);
      }
      removeSourceChunk(toRemove);
    }
  }

  @Override
  protected IChunk addSourceChunkInternal(IChunk chunkToInsert)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("attempting to insert " + chunkToInsert);

    /*
     * ok, something will be changing..
     */
    IChunk errorChunk = getErrorChunk();

    /*
     * did something go wrong? set the states..
     */
    if (errorChunk.equals(chunkToInsert)
        || !isValidChunkType(chunkToInsert.getSymbolicChunk().getChunkType()))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getName() + " : " + chunkToInsert
            + " was error or invalid of chunk type");
      setStateChunk(errorChunk);
      chunkToInsert = null;
    }

    /*
     * all is good, let's set the chunk
     */
    if (chunkToInsert != null)
    {
      /*
       * are we bumping up against the maximum capacity?
       */
      ensureCapacity();

      double now = ACTRRuntime.getRuntime().getClock(getModel()).getTime();

      double time = Double.MIN_VALUE;

      /*
       * if LRA or LRU, time will be now, if modified, we set it as minimum
       * double value (can't be negative inf because of potential value
       * collisions, see below)
       */
      switch (getEjectionPolicy())
      {
        case LeastRecentlyAdded:
        case LeastRecentlyUsed:
        case MostRecentlyAdded:
        case MostRecentlyUsed:
          time = now;
          break;
      }

      /*
       * problem of more than one chunk inserted at the same time (could happen
       * if a production fires multiple adds)
       */
      while (_sourceChunks.containsKey(time)
          && !chunkToInsert.equals(_sourceChunks.get(time)))
        time += 0.0000001;

      _sourceChunks.put(time, chunkToInsert);
      _times.put(chunkToInsert, time);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getName() + " Source Chunks & Times " + _sourceChunks);

      IModel model = getModel();
      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        StringBuilder sb = new StringBuilder("Inserted ");
        sb.append(chunkToInsert).append(" into ").append(getName());
        String msg = sb.toString();

        LOGGER.debug(msg);
        Logger.log(model, Logger.Stream.BUFFER, msg);
      }

      chunkInserted(chunkToInsert);
    }

    return chunkToInsert;
  }

  /**
   * set the status slots after insertion
   * 
   * @param insertedChunk
   */
  protected void chunkInserted(IChunk insertedChunk)
  {
    if (isCapacityReached())
      setBufferChunk(getFullChunk());
    else
      setStatusSlotContent(IStatusBuffer.BUFFER_SLOT, _sourceChunks.size());

    setStateChunk(getFreeChunk());
  }

  /**
   * return the first source chunk. which will be returned depends upon ejection
   * order, the next to get the boot will be returned
   */
  protected IChunk getSourceChunkInternal()
  {
    if (_sourceChunks.size() == 0) return null;

    switch (getEjectionPolicy())
    {
      case LeastRecentlyAdded:
      case LeastRecentlyMatched:
      case LeastRecentlyUsed:
        return _sourceChunks.get(_sourceChunks.firstKey());
      case MostRecentlyAdded:
      case MostRecentlyMatched:
      case MostRecentlyUsed:
        return _sourceChunks.get(_sourceChunks.lastKey());
      default:
        throw new IllegalActivationBufferStateException(
            "EjectionPolicy is invalid");
    }
  }

  /**
   * return the contents of the buffer. The order of the chunks returned is
   * determined by the ejection policy, such that the chunk most likely to be
   * given the book is first.
   */
  @SuppressWarnings("unchecked")
  protected Collection<IChunk> getSourceChunksInternal()
  {
    /*
     * there is nothing to return..
     */
    if (_sourceChunks.size() == 0) return Collections.EMPTY_LIST;

    List<IChunk> rtn = new ArrayList<IChunk>(_sourceChunks.values());

    switch (getEjectionPolicy())
    {
      case MostRecentlyAdded:
      case MostRecentlyMatched:
      case MostRecentlyUsed:
        Collections.reverse(rtn);
        break;
    }

    return rtn;
  }

  @Override
  protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("attempting to remove " + chunkToRemove + " from "
          + getName());

    if (_times.containsKey(chunkToRemove))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("removing and setting status slots");

      double time = _times.remove(chunkToRemove);
      _sourceChunks.remove(time);

      chunkRemoved(chunkToRemove);

      if (LOGGER.isDebugEnabled())
      {
        // TODO remove debugging code
        LOGGER.debug(getName() + " Source Chunks & Times " + _sourceChunks);
        for (IChunk chunk : _sourceChunks.values())
          if (chunk.isEncoded())
            LOGGER.debug("WARNING : " + chunk + " has already been encoded");
      }

      return true;
    }
    else
    {
      /*
       * TODO remove debugging code safety check while I debug this issue: in
       * some cases, the chunk (in the buffer) is being encoded and merged with
       * an existing chunk - now, the buffer should not contain any encoded
       * chunks, but somehow this one is still in here.. the problem is that the
       * merging process changes the hashcode which will mean that the above
       * check will fail.. this finds the problem and throws the appropriate
       * exception so we can stop the execution
       */
      /*
       * problem found: two buffers, with the same chunk, one removes and
       * encodes. solution: make all insertions copies
       */
      for (IChunk source : _times.keySet())
        if (source.equals(chunkToRemove))
          throw new IllegalStateException(
              "Mayday, hashcode has changed for encoded chunk " + chunkToRemove);
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("nothing to remove");
    return false;
  }

  protected void chunkRemoved(IChunk removedChunk)
  {
    if (_sourceChunks.size() == 0) setBufferChunk(getEmptyChunk());
  }

  /**
   * when a chunk is matched against, we modify its insertion time..
   */
  protected boolean matchedInternal(IChunk chunk)
  {

    if (!_times.containsKey(chunk))
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(chunk + " was matched, but is not in the buffer?");
      return false;
    }

    /*
     * if LRA, we've done all we need to
     */
    EjectionPolicy policy = getEjectionPolicy();

    if (policy == EjectionPolicy.LeastRecentlyAdded
        || policy == EjectionPolicy.MostRecentlyAdded) return true;

    /*
     * now we can tweak the access time, but only if the policy is LRU or LRM
     */
    double oldTime = _times.get(chunk);
    double newTime = ACTRRuntime.getRuntime().getClock(getModel()).getTime();

    _sourceChunks.remove(oldTime);
    /*
     * make sure there are no collisions
     */
    while (_sourceChunks.containsKey(newTime)
        && !chunk.equals(_sourceChunks.get(newTime)))
    {
      /*
       * problem of more than one chunk inserted at the same time (could happen
       * during path-integration)
       */
      newTime += 0.000001;
    }

    _times.put(chunk, newTime);
    _sourceChunks.put(newTime, chunk);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(getName() + " Source Chunks & Times " + _sourceChunks);
    return true;
  }

  public EjectionPolicy getEjectionPolicy()
  {
    try
    {
      getLock().readLock().lock();
      return _ejectionPolicy;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  public void setEjectionPolicy(EjectionPolicy policy)
  {
    if (_ejectionPolicy != null && _initialized)
      throw new IllegalActivationBufferStateException(
          "EjectionPolicy cannot be changed after buffer initialization");
    try
    {
      getLock().writeLock().lock();
      _ejectionPolicy = policy;
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    Collection<String> rtn = new ArrayList<String>(super
        .getPossibleParameters());
    rtn.add(EJECTION_POLICY_PARAM);
    return rtn;
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = new ArrayList<String>(super.getSetableParameters());
    rtn.add(EJECTION_POLICY_PARAM);
    return rtn;
  }

  @Override
  public String getParameter(String key)
  {
    if (EJECTION_POLICY_PARAM.equalsIgnoreCase(key))
      return "" + getEjectionPolicy();

    return super.getParameter(key);
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (EJECTION_POLICY_PARAM.equalsIgnoreCase(key))
      try
      {
        setEjectionPolicy(EjectionPolicy.valueOf(value));
      }
      catch (Exception e)
      {
        /**
         * Error : error
         */
        LOGGER.error("could not set policy to " + value
            + ", assuming LeastRecentlyMatched", e);
        setEjectionPolicy(EjectionPolicy.LeastRecentlyMatched);
      }
    else
      super.setParameter(key, value);
  }
}
