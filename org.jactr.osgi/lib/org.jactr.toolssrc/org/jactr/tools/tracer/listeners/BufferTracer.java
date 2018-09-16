package org.jactr.tools.tracer.listeners;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.tracer.transformer.buffer.BulkBufferEvent;

/**
 * buffer tracer that monitors the buffers and contents for any changes. The AST
 * for each (dirty) buffer is sent out at conflict resolution and after the
 * cycle has elapsed. <br/>
 * As currently implemented, we mark buffers as dirty inline, but then flush
 * them on the background thread. This opens the possibility to mismatches due
 * to a late flush. It would probably be better to mark them inline, package
 * them inline, but dispatch the message on the background thread (hopefully
 * ensurely in order receipt)
 * 
 * @author harrison
 */
public class BufferTracer extends BaseTraceListener
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BufferTracer.class);

  private Map<IModel, Set<String>>   _dirtyBuffers;

  private IProceduralModuleListener  _proceduralListener;

  private IModelListener             _modelListener;

  private IChunkListener             _chunkListener;

  private IActivationBufferListener  _bufferListener;

  private Executor                   _executor;

  public BufferTracer()
  {
    _dirtyBuffers = new HashMap<IModel, Set<String>>();

    _modelListener = new ModelListenerAdaptor() {
      @Override
      public void cycleStopped(ModelEvent me)
      {
        flush(me.getSource(), me.getSimulationTime(), true);
      }
    };

    _proceduralListener = new ProceduralModuleListenerAdaptor() {
      @Override
      public void conflictSetAssembled(ProceduralModuleEvent pme)
      {
        flush(pme.getSource().getModel(), pme.getSimulationTime(), false);
      }
    };

    _chunkListener = new ChunkListenerAdaptor() {
      @Override
      public void slotChanged(ChunkEvent event)
      {
        IChunk chunk = event.getSource();
        for (IActivationBuffer buffer : BufferUtilities.getContainingBuffers(
            chunk, true))
          markDirty(buffer);
      }
    };

    /*
     * to track status changes and content changes (where we add and remove the
     * chunk listener)
     */
    _bufferListener = new ActivationBufferListenerAdaptor() {
      @Override
      public void statusSlotChanged(ActivationBufferEvent abe)
      {
        markDirty(abe.getSource());
      }

      @Override
      public void sourceChunkAdded(ActivationBufferEvent abe)
      {
        markDirty(abe.getSource());
        for (IChunk chunk : abe.getSourceChunks())
          chunk.addListener(_chunkListener, null);
      }

      @Override
      public void sourceChunkRemoved(ActivationBufferEvent abe)
      {
        markDirty(abe.getSource());
        detachListener(abe);
      }

      @Override
      public void sourceChunksCleared(ActivationBufferEvent abe)
      {
        markDirty(abe.getSource());
        detachListener(abe);
      }

      private void detachListener(ActivationBufferEvent abe)
      {
        for (IChunk chunk : abe.getSourceChunks())
          chunk.removeListener(_chunkListener);
      }
    };

  }

  public void install(IModel model, Executor executor)
  {
    _executor = executor;

    /*
     * we flush asynch, but mark dirty synchronously.
     */
    model.addListener(_modelListener, _executor);
    model.getProceduralModule().addListener(_proceduralListener, _executor);

    TreeSet<String> buffers = new TreeSet<String>();

    for (IActivationBuffer buffer : model.getActivationBuffers())
    {
      buffer.addListener(_bufferListener, null);
      buffers.add(buffer.getName());

      for (IChunk chunk : buffer.getSourceChunks())
        chunk.addListener(_chunkListener, null);
    }

    _dirtyBuffers.put(model, buffers);
  }

  public void uninstall(IModel model)
  {
    _dirtyBuffers.remove(model);

    model.removeListener(_modelListener);

    model.getProceduralModule().removeListener(_proceduralListener);

    for (IActivationBuffer buffer : model.getActivationBuffers())
    {
      buffer.removeListener(_bufferListener);
      for (IChunk chunk : buffer.getSourceChunks())
        chunk.removeListener(_chunkListener);
    }
  }

  protected void markDirty(IActivationBuffer buffer)
  {
    synchronized (_dirtyBuffers)
    {
      _dirtyBuffers.get(buffer.getModel()).add(buffer.getName());
    }
  }

  protected void flush(IModel model, double time, boolean isEndOfCycle)
  {
    Set<String> buffers = FastSetFactory.newInstance();

    synchronized (_dirtyBuffers)
    {
      buffers.addAll(_dirtyBuffers.get(model));
      _dirtyBuffers.get(model).clear();
    }

    ASTSupport support = new ASTSupport();
    CommonTree buffersAST = support.createTree(JACTRBuilder.BUFFERS, "buffers");

    /*
     * extract the ASTs and send them out. Normal buffer AST does not have slots
     * or chunks (fully defined)
     */
    for (String bufferName : buffers)
    {
      IActivationBuffer buffer = model.getActivationBuffer(bufferName);
      CommonTree bufferAST = ASTResolver.toAST(buffer);

      /*
       * we replace the content of CHUNKS with actual chunk ASTs
       */
      CommonTree chunksAST = ASTSupport.getFirstDescendantWithType(bufferAST,
          JACTRBuilder.CHUNKS);
      while (chunksAST.getChildCount() > 0)
        chunksAST.deleteChild(0);

      for (IChunk chunk : buffer.getSourceChunks())
      {
        // resolve, leaving out parameters
        CommonTree chunkAST = ASTResolver.toAST(chunk, true);
        chunksAST.addChild(chunkAST);
        // strip paraemters node entirely
        chunkAST.deleteChild(3);
      }

      /*
       * to be nice, lets strip the parameters (name chunks parameters)
       */
      bufferAST.deleteChild(2);

      /*
       * now the slots, if applicable
       */
      ISlotContainer slotContainer = buffer
          .getAdapter(ISlotContainer.class);
      if (slotContainer != null)
      {
        CommonTree slotsAST = support.createSlotsTree();
        bufferAST.addChild(slotsAST);

        ASTResolver.setSlots(slotsAST, slotContainer);
      }

      /*
       * add to the bulk buffer event
       */
      buffersAST.addChild(bufferAST);
    }

    // and send it out
    if (buffers.size() > 0)
      sink(new BulkBufferEvent(model.getName(), time, buffersAST, isEndOfCycle));

    FastSetFactory.recycle(buffers);
  }
}
