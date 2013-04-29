/*
 * Created on Nov 25, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.retrieval.buffer;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.delegate.DefaultDelegatedRequestableBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.five.learning.CompilableContext;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * default retrieval buffer
 * 
 * @author developer
 */
public class DefaultRetrievalBuffer6 extends DefaultDelegatedRequestableBuffer6
    implements ICompilableBuffer
{
  /**
   * logger definition
   */
  static private final Log           LOGGER = LogFactory
                                                .getLog(DefaultRetrievalBuffer6.class);

  // protected boolean _ignoreStatus = true;
  //
  // protected double _minimumRetrievalTime = 0.05;
  //
  // protected StartRetrieval _currentRetrieval = null;
  //
  // protected IChunk _sourceChunk;
  protected RetrievalRequestDelegate _retrievalDelegate;

  public DefaultRetrievalBuffer6(String name, IRetrievalModule module)
  {
    super(name, module);

    // addRequestDelegate(new RetrievalRequestDelegate(module, this));
    _retrievalDelegate = new RetrievalRequestDelegate(module);
    _retrievalDelegate.setAsynchronous(true);
    addRequestDelegate(_retrievalDelegate);
  }

  protected Collection<IChunk> clearInternal()
  {
    _retrievalDelegate.clear();
    return super.clearInternal();
  }

  // protected void setCurrentRetrieval(StartRetrieval retrieval)
  // {
  // _currentRetrieval = retrieval;
  // }
  //
  // /**
  // * is there a retrieval that is currently in the works
  // *
  // * @return
  // */
  // protected boolean isRetrievalPending()
  // {
  // return (_currentRetrieval != null || isStateBusy());
  // }

  /**
   * this lets the retrieval buffer contain the original chunk and not its copy
   * 
   * @return false
   * @see org.jactr.core.buffer.six.AbstractActivationBuffer6#copyChunkOnInsertion()
   */
  @Override
  protected boolean shouldCopyOnInsertion(IChunk chunk)
  {
    return false;
  }



  // private void log(String message)
  // {
  // IModel model = getModel();
  // if(Logger.hasLoggers(model))
  // Logger.log(model, Logger.Stream.RETRIEVAL, message);
  // }

  // /**
  // * make the actual retrieval request.
  // * @param source
  // * @return
  // * @see
  //org.jactr.core.buffer.six.AbstractRequestableBuffer6#addChunkPatternInternal
  // (org.jactr.core.production.condition.ChunkPattern)
  // */
  // @Override
  // protected boolean addChunkPatternInternal(final ChunkPattern source)
  // {
  // if (isRetrievalPending())
  // {
  // log("A retrieval is currently pending");
  //      
  // if (!_ignoreStatus)
  // {
  // log("Aborting retrieval request to allow current retrieval to complete.");
  // return false;
  // }
  //
  // log("Ignoring pending retrieval and stumbling through.");
  //
  // _currentRetrieval.abort();
  // }
  //
  // /*
  // * clear out the previous retrieval
  // */
  // removeSourceChunk(getSourceChunk());
  //
  // double now = ACTRRuntime.getRuntime().getClock(getModel()).getTime();
  // Future<IChunk> result = ((IRetrievalModule) getModule())
  // .retrieveChunk(source);
  // StartRetrieval start = new StartRetrieval(this, source, result, now, now
  // + _minimumRetrievalTime);
  //
  // // queue up this event
  // getModel().getTimedEventQueue().enqueue(start);
  //
  // setCurrentRetrieval(start);
  //
  // /*
  // * set our status as busy..
  // */
  // setStateChunk(getBusyChunk());
  //
  // return true;
  // }

  // private boolean indexedRetrievalsEnabled()
  // {
  // IRetrievalModule module = (IRetrievalModule) getModule();
  // if (module instanceof DefaultRetrievalModule6)
  // return ((DefaultRetrievalModule6) module).isIndexedRetrievalEnabled();
  // return false;
  // }
  //
  // /**
  // * convert the chunk to a chunk pattern. if ignoreNullValues is true, any
  // slot
  // * with a null value will be omitted from the chunk pattern
  // *
  // * @param chunk
  // * @param ignoreNullValues
  // * @return
  // */
  // protected ChunkPattern convertToPattern(IChunk chunk, boolean
  // ignoreNullValues)
  // {
  // ChunkPattern pattern = new
  // ChunkPattern(chunk.getSymbolicChunk().getChunkType());
  //    
  // for(ISlot slot : chunk.getSymbolicChunk().getSlots())
  // if(slot.getValue()!=null || !ignoreNullValues)
  // pattern.addSlot(slot);
  //    
  // return pattern;
  // }
  //
  // /**
  // * if indexed retrievals are permitted by the retrieval module, this will
  // just
  // * route the chunk directly into the buffer, performing an end run around
  // the
  // * actual retrieval process.<br>
  // * If they arent enabled, the chunk will be converted into a
  // * {@link ChunkPattern} and passed on to
  // * {@link #request(IRequest)} to perform the actual search.<br>
  // *
  // * @param chunk
  // * @return the added chunk, or null if routed to
  // * {@link #request(IRequest)}
  // * @see
  //org.jactr.core.buffer.six.AbstractActivationBuffer6#addSourceChunk(org.jactr
  // .core.chunk.IChunk)
  // */
  // @Override
  // public IChunk addSourceChunk(IChunk chunk)
  // {
  // if (chunk == null) return superAddSourceChunk(chunk);
  //    
  // String msg = "";
  // IChunk rtn = null;
  //
  // if (indexedRetrievalsEnabled())
  // {
  // msg = "Performing indexed retrieval of "+chunk;
  //      
  // rtn = superAddSourceChunk(chunk);
  // }
  // else
  // {
  // msg = "Indexed retrievals not enabled, creating search pattern defined by
  // "+chunk;
  // ChunkPattern pattern = convertToPattern(chunk, false);
  // request(pattern);
  // }
  //    
  // log(msg);
  //    
  // if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
  //    
  // return rtn;
  // }
  //
  // /**
  // * called by {@link #retrievalCompleted(ChunkPattern, IChunk)}, this routes
  // * directly to the superclass's {@link #addSourceChunk(IChunk)}, allowing us
  // * to circumvent the indexed retrieval check that our version of add
  // performs
  // *
  // * @param chunk
  // */
  // private IChunk superAddSourceChunk(IChunk chunk)
  // {
  // return super.addSourceChunk(chunk);
  // }

  // /**
  // * called at the completion of the retrieval instead of directly calling
  // * {@link #addSourceChunk(IChunk)}. This allows us to disambiguate the
  // result
  // * with respect to the possible indexed retrieval.<br>
  // * This will do some basic checking and then add the chunk to the buffer.
  // *
  // * @param pattern
  // * @param chunk
  // */
  // public void retrievalCompleted(ChunkPattern pattern, IChunk chunk)
  // {
  // setCurrentRetrieval(null);
  // setStateChunk(getFreeChunk());
  // superAddSourceChunk(chunk);
  // }

  // /**
  // * chunkToInsert is a copy of what was passed iff it has been encoded
  // *
  // * @see
  // org.jactr.core.buffer.AbstractActivationBuffer#addSourceChunkInternal(org.
  // jactr.core.chunk.IChunk)
  // */
  // @Override
  // protected IChunk addSourceChunkInternal(IChunk chunkToInsert)
  // {
  // IChunk currentSource = getSourceChunk();
  // IChunk errorChunk = getErrorChunk();
  //
  // /*
  // * did something go wrong? set the states..
  // */
  // if (errorChunk.equals(chunkToInsert))
  // {
  // if (currentSource != null) removeSourceChunk(currentSource);
  // setStateChunk(errorChunk);
  //
  // chunkToInsert = null;
  // }
  //
  // /*
  // * all is good, let's set the chunk
  // */
  // if (chunkToInsert != null)
  // {
  // if (currentSource != null) removeSourceChunk(currentSource);
  //
  // setStateChunk(getFreeChunk());
  // setBufferChunk(getFullChunk());
  // setSourceChunkInternal(chunkToInsert);
  // }
  //
  // return chunkToInsert;
  // }
  //
  // @Override
  // protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  // {
  // setSourceChunkInternal(null);
  // setBufferChunk(getEmptyChunk());
  // setStateChunk(getFreeChunk());
  // return true;
  // }

  // /**
  // * @see
  // org.jactr.core.buffer.AbstractActivationBuffer#getSourceChunkInternal()
  // */
  // @Override
  // protected IChunk getSourceChunkInternal()
  // {
  // return _sourceChunk;
  // }
  //
  // /**
  // * @see
  // org.jactr.core.buffer.AbstractActivationBuffer#getSourceChunksInternal()
  // */
  // @Override
  // protected Collection<IChunk> getSourceChunksInternal()
  // {
  // if (_sourceChunk == null) return Collections.EMPTY_LIST;
  // return Collections.singleton(_sourceChunk);
  // }

  protected void setSourceChunkInternal(IChunk sourceChunk)
  {
    super.setSourceChunkInternal(sourceChunk);
    if (sourceChunk != null)
    {
      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.RETRIEVAL, "Retrieved " + sourceChunk);
    }
  }

  
  // @Override
  // public boolean willAcceptChunkPattern(ChunkPattern pattern)
  // {
  // return true;
  // }

  public ICompilableContext getCompilableContext()
  {
    return new CompilableContext(false, false, true);
  }

  // public boolean isImmediate()
  // {
  // return false;
  // }
  //
  // public boolean isPatternProcessingImmediate()
  // {
  // return true;
  // }

}
