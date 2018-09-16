package org.jactr.tools.change;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.event.DeclarativeModuleListenerAdaptor;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.io.resolver.ASTResolver;

/**
 * general change tracker that logs chunk changes and production instantiations.
 * It outputs them to the {@link Logger}, using a custom stream label "CHANGE"
 * 
 * @author harrison
 */
public class ChangeTracker implements IInstrument, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log          LOGGER               = LogFactory
                                                                       .getLog(ChangeTracker.class);

  static public final String                  CHANGE_STREAM        = "CHANGE";

  static public final String                  FORMAT_PARAM         = "Format";

  static public final String                  TRACK_NEW_CHUNKS     = "TrackNewChunks";

  static public final String                  TRACK_ACTIVE_CHUNKS  = "TrackActiveChunks";

  static public final String                  TRACK_INSTANTIATIONS = "TrackInstantiations";

  /**
   * listen to the model for cycle start/stop
   */
  private IModelListener                      _modelListener;

  /**
   * listen to the procedural module for the production to fire
   */
  private IProceduralModuleListener           _proceduralListener;

  /**
   * for new chunks
   */
  private IDeclarativeModuleListener          _declarativeListener;

  /**
   * to catch the add/remove of chunks to buffers
   */
  private IActivationBufferListener           _bufferListener;

  /**
   * so we know when chunks in buffers change
   */
  private IChunkListener                      _chunkListener;

  /**
   * keep track of all the chunks that have changed. We will use these at the
   * start/end of the cycle to generate ASTs
   */
  private Map<IModel, Set<IChunk>>            _changedChunks;

  /**
   * all the asts that need to be output
   */
  private Map<IModel, Collection<CommonTree>> _astsToWrite;

  private ICodeGenerator                      _codeGenerator;

  private String                              _format;

  private boolean                             _trackNewChunks      = false;

  private boolean                             _trackActiveChunks   = false;

  private boolean                             _trackInstantitions  = false;

  public ChangeTracker()
  {
    _changedChunks = new HashMap<IModel, Set<IChunk>>();
    _astsToWrite = new HashMap<IModel, Collection<CommonTree>>();

    _modelListener = new ModelListenerAdaptor() {
      @Override
      public void cycleStarted(ModelEvent me)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Cycle started, generate");
        generateASTs(me.getSource());
      }

      @Override
      public void cycleStopped(ModelEvent me)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Cycle stopped, generate and flush");
        generateASTs(me.getSource());
        flush(me.getSource(), false);
      }
    };

    _declarativeListener = new DeclarativeModuleListenerAdaptor() {
      @Override
      public void chunkAdded(DeclarativeModuleEvent dme)
      {
        IChunk chunk = dme.getChunk();

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Chunk added to DM " + chunk);

        synchronized (_changedChunks)
        {
          _changedChunks.get(dme.getSource().getModel()).add(chunk);
        }
      }
      @Override
      public void chunksMerged(DeclarativeModuleEvent event)
      {
        /*
         * the original
         */
        IChunk chunk = event.getChunk();
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Merged into " + chunk);

        synchronized (_changedChunks)
        {
          _changedChunks.get(event.getSource().getModel()).add(chunk);
        }
      }
    };

    _proceduralListener = new ProceduralModuleListenerAdaptor() {
      @Override
      public void productionWillFire(ProceduralModuleEvent pme)
      {
        /*
         * we have to generate this now since it is an instantiation
         */
        generateAST(pme.getSource().getModel(), pme.getProduction());
      }
    };

    _bufferListener = new ActivationBufferListenerAdaptor() {
      @Override
      public void sourceChunkAdded(ActivationBufferEvent abe)
      {
        for (IChunk chunk : abe.getSourceChunks())
          chunk.addListener(_chunkListener, ExecutorServices.INLINE_EXECUTOR);
      }

      @Override
      public void sourceChunkRemoved(ActivationBufferEvent abe)
      {
        sourceChunksCleared(abe);
      }

      @Override
      public void sourceChunksCleared(ActivationBufferEvent abe)
      {
        for (IChunk chunk : abe.getSourceChunks())
          chunk.removeListener(_chunkListener);
      }
    };

    _chunkListener = new ChunkListenerAdaptor() {
      @Override
      public void slotChanged(ChunkEvent ce)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(ce.getSource() + " has changed");
        synchronized (_changedChunks)
        {
          _changedChunks.get(ce.getSource().getModel()).add(ce.getSource());
        }
      }
    };
  }

  public void initialize()
  {
    // TODO Auto-generated method stub

  }

  public void install(IModel model)
  {
    if (_codeGenerator == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("No code generator was defined. must set " + FORMAT_PARAM
            + " parameter");
      return;
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Attaching to " + model);

    Set<IChunk> changedChunks = new HashSet<IChunk>();
    Collection<CommonTree> asts = new ArrayList<CommonTree>();

    synchronized (_astsToWrite)
    {
      _astsToWrite.put(model, asts);
    }

    synchronized (_changedChunks)
    {
      _changedChunks.put(model, changedChunks);
    }

    model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

    if (_trackInstantitions)
      model.getProceduralModule().addListener(_proceduralListener,
          ExecutorServices.INLINE_EXECUTOR);

    if (_trackNewChunks)
      model.getDeclarativeModule().addListener(_declarativeListener,
          ExecutorServices.INLINE_EXECUTOR);

    if (_trackActiveChunks)
      for (IActivationBuffer buffer : model.getActivationBuffers())
        buffer.addListener(_bufferListener, ExecutorServices.INLINE_EXECUTOR);
  }

  public void uninstall(IModel model)
  {
    if (_codeGenerator == null) return;

    model.removeListener(_modelListener);

    model.getProceduralModule().removeListener(_proceduralListener);

    for (IActivationBuffer buffer : model.getActivationBuffers())
      buffer.removeListener(_bufferListener);

    /**
     * queue up a dump request..
     */
    flush(model, true);
  }

  /**
   * generate the ast of the to-be-fire instantaition
   * 
   * @param model
   * @param production
   */
  protected void generateAST(IModel model, IProduction production)
  {
    Collection<CommonTree> asts = null;
    synchronized (_astsToWrite)
    {
      asts = _astsToWrite.get(model);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Generating ast for " + production);

    asts.add(ASTResolver.toAST(production));
  }

  /**
   * use the list of changed chunks to generate asts for them
   * 
   * @param model
   */
  protected void generateASTs(IModel model)
  {
    Collection<CommonTree> asts = null;
    synchronized (_astsToWrite)
    {
      asts = _astsToWrite.get(model);
    }


    List<IChunk> changedChunks = FastListFactory.newInstance();
    synchronized (_changedChunks)
    {
      changedChunks.addAll(_changedChunks.get(model));
      _changedChunks.get(model).clear();
    }
    
    for (IChunk chunk : changedChunks)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("generating ast for " + chunk);
      asts.add(ASTResolver.toAST(chunk, false));
    }
    
    FastListFactory.recycle(changedChunks);
  }

  /**
   * generate the code for the asts and then send them to the logger on the
   * background thread
   * 
   * @param model
   * @param cleanUp
   */
  protected void flush( IModel model,  boolean cleanUp)
  {
    List<CommonTree> list = FastListFactory.newInstance();
    
    synchronized (_astsToWrite)
    {
      list.addAll(_astsToWrite.get(model));
      _astsToWrite.get(model).clear();
    }

    StringBuilder sb = new StringBuilder();
    //fast, destructive iterator where processing order does not matter
    for (CommonTree ast : list)
    {
      sb.delete(0, sb.length());
      
      for (StringBuilder line : _codeGenerator.generate(ast, true))
        sb.append(line).append("\n");
      sb.append("\n");
      
      Logger.log(model, CHANGE_STREAM, sb.toString());
    }

    FastListFactory.recycle(list);


    if (cleanUp)
    {
      synchronized (_astsToWrite)
      {
        _astsToWrite.remove(model);
      }
      synchronized (_changedChunks)
      {
        _changedChunks.remove(model);
      }
    }
  }

  public String getParameter(String key)
  {
    if (FORMAT_PARAM.equalsIgnoreCase(key)) return _format;
    if (TRACK_ACTIVE_CHUNKS.equalsIgnoreCase(key))
      return "" + _trackActiveChunks;
    if (TRACK_NEW_CHUNKS.equalsIgnoreCase(key)) return "" + _trackNewChunks;
    if (TRACK_INSTANTIATIONS.equalsIgnoreCase(key))
      return "" + _trackInstantitions;
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(FORMAT_PARAM, TRACK_ACTIVE_CHUNKS, TRACK_NEW_CHUNKS,
        TRACK_INSTANTIATIONS);
  }

  public void setParameter(String key, String value)
  {
    if (FORMAT_PARAM.equalsIgnoreCase(key))
    {
      _codeGenerator = CodeGeneratorFactory.getCodeGenerator(value);
      _format = value;
    }
    else if (TRACK_ACTIVE_CHUNKS.equalsIgnoreCase(key))
      _trackActiveChunks = Boolean.parseBoolean(value);
    else if (TRACK_NEW_CHUNKS.equalsIgnoreCase(key))
      _trackNewChunks = Boolean.parseBoolean(value);
    else if (TRACK_INSTANTIATIONS.equalsIgnoreCase(key))
      _trackInstantitions = Boolean.parseBoolean(value);
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to set " + key + " = " + value);

  }
}
