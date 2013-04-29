/*
 * Created on Aug 13, 2005 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.buffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * basic implementation of an activation buffer that handles the spreading of
 * activation to chunks. this is thread safe.
 * 
 * @author developer
 */
public abstract class AbstractActivationBuffer implements IActivationBuffer,
    IParameterized
{

  /**
   * Logger definition
   */

  static private final transient Log                                              LOGGER                   = LogFactory
                                                                                                               .getLog(AbstractActivationBuffer.class);

  final private IModel                                                            _model;

  final private IModule                                                           _module;

  final private Collection<IChunk>                                                _activatedChunks;

  private double                                                                  _activation              = 0;

  private double                                                                  _goalValue               = 0;

  private boolean                                                                 _strictHarvestingEnabled = true;

  final private String                                                            _name;

  final private ACTREventDispatcher<IActivationBuffer, IActivationBufferListener> _eventDispatcher;

  final private ReentrantReadWriteLock                                            _lock                    = new ReentrantReadWriteLock();

  /**
   * Comment for <code>ACTIVATION</code>
   */
  static public final String                                                      ACTIVATION_PARAM         = "Activation";

  /**
   * Comment for <code>GOAL_VALUE</code>
   */
  static public final String                                                      GOAL_VALUE_PARAM         = "G";

  static public final String                                                      STRICT_HARVESTING_PARAM  = "StrictHarvestingEnabled";

  static private final String[]                                                   SETTABLE                 = {
      ACTIVATION_PARAM, GOAL_VALUE_PARAM, STRICT_HARVESTING_PARAM                                         };

  static private final String[]                                                   GETTABLE                 = {
      ACTIVATION_PARAM, GOAL_VALUE_PARAM, STRICT_HARVESTING_PARAM                                         };

  public AbstractActivationBuffer(String name, IModel model, IModule module)
  {
    _name = name;
    _model = model;
    _module = module;
    _activatedChunks = new ArrayList<IChunk>();
    _eventDispatcher = new ACTREventDispatcher<IActivationBuffer, IActivationBufferListener>();
  }

  public void dispose()
  {
    try
    {
      getLock().writeLock().lock();
      _activatedChunks.clear();
      _eventDispatcher.clear();
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  protected ReentrantReadWriteLock getLock()
  {
    return _lock;
  }

  public void matched(IChunk chunk)
  {
    if (chunk == null) return;
    boolean signal = false;
    try
    {
      _lock.writeLock().lock();
      signal = matchedInternal(chunk);
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (signal)
      if (_eventDispatcher.hasListeners())
        _eventDispatcher.fire(new ActivationBufferEvent(this,
            ActivationBufferEvent.Type.CHUNK_MATCHED, chunk));
  }

  /**
   * return true if the matched event should be fired
   * 
   * @param chunk
   * @return
   */
  protected boolean matchedInternal(IChunk chunk)
  {
    return true;
  }

  final public void setActivation(double activation)
  {
    double oldAct = _activation;
    try
    {
      _lock.writeLock().lock();
      _activation = activation;
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new ActivationBufferEvent(this,
          ActivationBufferEvent.Type.PARAMETER_CHANGED, ACTIVATION_PARAM,
          oldAct, activation));
  }

  final public double getActivation()
  {
    try
    {
      _lock.readLock().lock();
      return _activation;
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  final public void setG(double g)
  {
    double oldValue = _goalValue;
    try
    {
      _lock.writeLock().lock();
      _goalValue = g;
    }
    finally
    {
      _lock.writeLock().unlock();
    }
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new ActivationBufferEvent(this,
          ActivationBufferEvent.Type.PARAMETER_CHANGED, GOAL_VALUE_PARAM,
          oldValue, g));
  }

  final public double getG()
  {
    try
    {
      _lock.readLock().lock();
      return _goalValue;
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  final public void setStrictHarvestingEnabled(boolean enabled)
  {
    try
    {
      getLock().writeLock().lock();
      _strictHarvestingEnabled = enabled;
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  final public boolean isStrictHarvestingEnabled()
  {
    try
    {
      getLock().readLock().lock();
      return _strictHarvestingEnabled;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  final protected ACTREventDispatcher<IActivationBuffer, IActivationBufferListener> getEventDispatcher()
  {
    return _eventDispatcher;
  }

  /**
   * will call removeSourceChunkInternal for all the chunks and then recalculate
   * activation
   * 
   * @see org.jactr.core.buffer.IActivationBuffer#clear()
   */
  public void clear()
  {
    Collection<IChunk> old = null;
    try
    {
      _lock.writeLock().lock();
      old = clearInternal();
    }
    catch (RuntimeException re)
    {
      LOGGER.error("exception while clearing buffer " + getName(), re);
      throw re;
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    // notify
    if (old.size() != 0)
    {
      if (_eventDispatcher.hasListeners())
        _eventDispatcher.fire(new ActivationBufferEvent(this, old));

      if (Logger.hasLoggers(getModel()))
        Logger.log(getModel(), Logger.Stream.BUFFER, "cleared " + getName());
    }
  }

  /**
   * called within the lock
   * 
   * @return the chunks that were removed
   */
  protected Collection<IChunk> clearInternal()
  {
    Collection<IChunk> cleared = getSourceChunks();

    for (IChunk chunk : cleared)
      if(removeSourceChunkInternal(chunk))
        BufferUtilities.unmarkContained(chunk, this);

    calculateAndSpreadSourceActivation();
    if (LOGGER.isDebugEnabled()) LOGGER.debug("cleared " + cleared);
    return cleared;
  }

  /**
   * spread activation to the contents, if any..
   */
  final protected void calculateAndSpreadSourceActivation()
  {
    try
    {
      _lock.writeLock().lock();
      clearActivatedChunks();
      _activatedChunks.addAll(spreadActivation());
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  /**
   * spread activation to all the chunks that are connected to the source chunk
   * 
   * @return a collection of all the chunks that have had their source
   *         activation incremented
   */
  final protected Collection<IChunk> spreadActivation()
  {
    /*
     * we use an array list which WILL permit duplicates. Why? shouldn't
     * something appearing N times in a source chunk get N times the source
     * activation?
     */
    Collection<IChunk> rtn = new ArrayList<IChunk>();
    Collection<Link> iLinks = new ArrayList<Link>();
    /*
     * we need to get each chunk that the source contains
     */
    for (IChunk sourceChunk : getSourceChunks())
    {
      ISubsymbolicChunk sourceChunkSub = sourceChunk.getSubsymbolicChunk();
      if (sourceChunkSub instanceof ISubsymbolicChunk4)
      {
        iLinks.clear();
        ((ISubsymbolicChunk4) sourceChunkSub).getIAssociations(iLinks);

        /*
         * this chunk is J, so we need I
         */
        for (Link iLink : iLinks)
          rtn.add(iLink.getIChunk());
      }

      if (rtn.size() != 0)
      {
        // now we have them all, let's apply the activation
        double sourceActivation = getActivation() / rtn.size();
        for (IChunk chunk : rtn)
        {
          // unlikely, but possible..
          if (chunk.hasBeenDisposed()) continue;
          ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();
          double activation = ssc.getSourceActivation() + sourceActivation;
          ssc.setSourceActivation(activation);
        }
      }
    }

    return rtn;
  }

  /**
   * 
   *
   */
  final private void clearActivatedChunks()
  {
    if (_activatedChunks.size() == 0) return;

    double sourceActivation = getActivation() / _activatedChunks.size();
    for (IChunk chunk : _activatedChunks)
    {
      // a chunk may have been disposed of by now...
      if (chunk.hasBeenDisposed()) continue;
      ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();
      double activation = ssc.getSourceActivation() - sourceActivation;
      ssc.setSourceActivation(activation);
    }
    _activatedChunks.clear();
  }

  /**
   * return the source chunk that was actually inserted into the buffer (i.e. a
   * copy of chunkToInsert if chunkToInsert has already been encoded).
   * 
   * @param chunkToInsert
   *          the chunk to be inserted, will never be null nor already in the
   *          buffer
   * @return actual chunk inserted. if null, no events are fired or activation
   *         spread
   */
  abstract protected IChunk addSourceChunkInternal(IChunk chunkToInsert);

  /**
   * add chunk to the buffer. if addSourceChunkInternal(IChunk) returns not
   * null, activation is spread and an event is fired
   * 
   * @see org.jactr.core.buffer.IActivationBuffer#addSourceChunk(org.jactr.core.chunk.IChunk)
   */
  public IChunk addSourceChunk(IChunk c)
  {
    if (c == null) return null;

    IChunk added = contains(c);
    if (added != null) return added;

    /*
     * moved event firing out of synchronization
     */
    try
    {
      _lock.writeLock().lock();
      added = addSourceChunkInternal(c);
      if (added != null)
        calculateAndSpreadSourceActivation();
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (added != null)
    {
      BufferUtilities.markContained(added, this, getActivation());
      
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("addSourceChunkInternal returned true, dispatching and calculating activation");
      if (_eventDispatcher.hasListeners())
        _eventDispatcher.fire(new ActivationBufferEvent(this,
            ActivationBufferEvent.Type.SOURCE_ADDED, added));

      if (Logger.hasLoggers(getModel()))
        Logger.log(getModel(), Logger.Stream.BUFFER, "Added " + added + " to "
            + getName());
    }

    return added;
  }

  /**
   * do the actual work of removing chunkToRemove from the buffer.
   * 
   * @param chunkToRemove
   *          chunk that is in the buffer, never null
   * @return true if activation is to be reset and an event fired
   */
  abstract protected boolean removeSourceChunkInternal(IChunk chunkToRemove);

  /**
   * remove the chunk from the buffer. if removeSourceChunkInternal(IChunk)
   * returns true activation is reset and event fired
   * 
   * @see org.jactr.core.buffer.IActivationBuffer#removeSourceChunk(org.jactr.core.chunk.IChunk)
   */
  public void removeSourceChunk(IChunk c)
  {
    if (c == null) return;

    boolean shouldRemove = false;

    /*
     * is the chunk actually in the buffer?
     */
    for (IChunk chunk : getSourceChunks())
      if (chunk.equals(c))
      {
        shouldRemove = true;
        break;
      }

    if (!shouldRemove) return;

    /*
     * remove firing from synch
     */
    try
    {
      _lock.writeLock().lock();
      shouldRemove = removeSourceChunkInternal(c);
      if (shouldRemove)
        calculateAndSpreadSourceActivation();
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (shouldRemove)
    {
      BufferUtilities.unmarkContained(c, this);
      
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("removeSourceChunkInternal returned true, dispatching and calculating activation");
      if (_eventDispatcher.hasListeners())
        _eventDispatcher.fire(new ActivationBufferEvent(this,
            ActivationBufferEvent.Type.SOURCE_REMOVED, c));

      if (Logger.hasLoggers(getModel()))
        Logger.log(getModel(), Logger.Stream.BUFFER, "Removed " + c + " from "
            + getName());
    }
  }

  final public IChunk getSourceChunk()
  {
    try
    {
      _lock.readLock().lock();
      return getSourceChunkInternal();
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  /**
   * return the source chunk from the internal backing store
   * 
   * @return
   */
  abstract protected IChunk getSourceChunkInternal();

  final public Collection<IChunk> getSourceChunks()
  {
    try
    {
      _lock.readLock().lock();
      return getSourceChunksInternal();
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  abstract protected Collection<IChunk> getSourceChunksInternal();

  public IChunk contains(IChunk c)
  {
    for (IChunk chunk : getSourceChunks())
      if (chunk.equalsSymbolic(c)) return chunk;
    return null;
  }

  public void addListener(IActivationBufferListener abl, Executor executor)
  {
    _eventDispatcher.addListener(abl, executor);
  }

  public void removeListener(IActivationBufferListener abl)
  {
    _eventDispatcher.removeListener(abl);
  }

  public String getName()
  {
    return _name;
  }

  public IModel getModel()
  {
    return _model;
  }

  public boolean handlesEncoding()
  {
    return false;
  }

  public IModule getModule()
  {
    return _module;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   * @return
   */
  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(SETTABLE);
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   * @return
   */
  public Collection<String> getPossibleParameters()
  {
    return Arrays.asList(GETTABLE);
  }

  /**
   * @param parameter
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   * @return
   */
  public String getParameter(String parameter)
  {
    String rtn = null;

    if (parameter.equalsIgnoreCase(ACTIVATION_PARAM))
      rtn = ParameterHandler.numberInstance().toString(getActivation());
    else if (parameter.equalsIgnoreCase(GOAL_VALUE_PARAM))
      rtn = ParameterHandler.numberInstance().toString(getG());
    else if (parameter.equalsIgnoreCase(STRICT_HARVESTING_PARAM))
      rtn = "" + isStrictHarvestingEnabled();

    return rtn;
  }

  /**
   * @param parameter
   * @param value
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      String)
   */
  public void setParameter(String parameter, String value)
  {
    if (parameter.equalsIgnoreCase(ACTIVATION_PARAM))
      setActivation(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (parameter.equalsIgnoreCase(GOAL_VALUE_PARAM))
      setG(ParameterHandler.numberInstance().coerce(value).doubleValue());
    else if (parameter.equalsIgnoreCase(STRICT_HARVESTING_PARAM))
      setStrictHarvestingEnabled(ParameterHandler.booleanInstance().coerce(
          value));
  }

  public void initialize()
  {
    // noop
  }

  public String toString()
  {
    return getName();
  }
}
