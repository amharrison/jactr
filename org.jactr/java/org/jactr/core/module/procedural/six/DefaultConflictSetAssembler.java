package org.jactr.core.module.procedural.six;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.event.ChunkTypeEvent;
import org.jactr.core.chunktype.event.ChunkTypeListenerAdaptor;
import org.jactr.core.chunktype.event.IChunkTypeListener;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.event.DeclarativeModuleListenerAdaptor;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.procedural.IConflictSetAssembler;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.utils.collections.FastSetFactory;

/**
 * monitors the procedural module for new productions. All productions are
 * sorted by the most specific characteristic used for conflict set assembly.
 * Typically this is the chunktype of one of the conditions. If there are no
 * chunktype matches, it drops down to the buffer (for queries), and finally if
 * that doesn't work, the production is always considered for matching. <br/>
 * <br/>
 * full indexing (of all conditions) is not strictly necessary since all
 * conditions must match for firing, but performing full indexing can be useful
 * if subsets of productions can change.
 * 
 * @author harrison
 */
public class DefaultConflictSetAssembler implements IConflictSetAssembler
{
  /**
   * Logger definition
   */
  static private final transient Log                     LOGGER                       = LogFactory
                                                                                          .getLog(DefaultConflictSetAssembler.class);

  private IProceduralModule                              _module;

  private boolean                                        _useFullIndexing             = false;

  /**
   * keyed by buffer name (null for ambiguous), then chunktype (null for query
   * or ambiguous) to get the set
   */
  private Map<String, Map<IChunkType, Set<IProduction>>> _productionMap;

  private IProceduralModuleListener                      _listener                    = new ProceduralModuleListenerAdaptor() {
                                                                                        @Override
                                                                                        public void productionAdded(
                                                                                            ProceduralModuleEvent pme)
                                                                                        {
                                                                                          index(pme
                                                                                              .getProduction());
                                                                                        }
                                                                                      };

  private ReentrantReadWriteLock                         _lock                        = new ReentrantReadWriteLock();

  /**
   * used to detect new (extended or refined) chunktypes that may require
   * reindexing
   */
  private IChunkTypeListener                             _chunkTypeListener           = new ChunkTypeListenerAdaptor() {
                                                                                        @Override
                                                                                        public void childAdded(
                                                                                            ChunkTypeEvent cte)
                                                                                        {
                                                                                          /*
                                                                                           * reindex
                                                                                           */
                                                                                          Set<IProduction> candidates = FastSetFactory
                                                                                              .newInstance();
                                                                                          for (IActivationBuffer buffer : getProceduralModule()
                                                                                              .getModel()
                                                                                              .getActivationBuffers())
                                                                                          {
                                                                                            candidates
                                                                                                .clear();
                                                                                            String bufferName = buffer
                                                                                                .getName()
                                                                                                .toLowerCase();
                                                                                            getPossibleProductions(
                                                                                                bufferName,
                                                                                                cte.getSource(),
                                                                                                candidates);

                                                                                            for (IProduction production : candidates)
                                                                                              add(bufferName,
                                                                                                  cte.getChild(),
                                                                                                  production);
                                                                                          }

                                                                                          FastSetFactory
                                                                                              .recycle(candidates);
                                                                                        }
                                                                                      };

  /**
   * so we know when a new chunktype is encoded, allowing us to attach our
   * listener
   */
  private IDeclarativeModuleListener                     _declarativeListener         = new DeclarativeModuleListenerAdaptor() {
                                                                                        @Override
                                                                                        public void chunkTypeAdded(
                                                                                            DeclarativeModuleEvent dme)
                                                                                        {
                                                                                          // inline
                                                                                          dme.getChunkType()
                                                                                              .addListener(
                                                                                                  _chunkTypeListener,
                                                                                                  null);
                                                                                        }
                                                                                      };

  private boolean                                        _declarativeListenerAttached = false;

  public DefaultConflictSetAssembler(boolean useFullIndexing)
  {
    _productionMap = new HashMap<String, Map<IChunkType, Set<IProduction>>>();
    _useFullIndexing = useFullIndexing;
  }

  public void setProceduralModule(IProceduralModule module)
  {
    if (_module != null && module == null)
    {
      // remove listeneer
      _module.removeListener(_listener);
      _module.getModel().getDeclarativeModule()
          .removeListener(_declarativeListener);
      clear();
    }

    _module = module;

    if (_module != null) try
    {
      _module.addListener(_listener, null); // inline listening
      for (IProduction production : module.getProductions().get())
        index(production);
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to preprocess existing productions ", e);
    }
  }

  protected Set<IProduction> createSet()
  {
    return FastSetFactory.newInstance();
  }

  @SuppressWarnings("rawtypes")
  protected void reclaimSet(Set<IProduction> productions)
  {
    FastSetFactory.recycle(productions);
  }

  protected void clear()
  {
    try
    {
      _lock.writeLock().lock();

    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  public IProceduralModule getProceduralModule()
  {
    return _module;
  }

  public Set<IProduction> getConflictSet(Set<IProduction> container)
  {
    if (!_declarativeListenerAttached)
    {
      _module.getModel().getDeclarativeModule()
          .addListener(_declarativeListener, null);
      _declarativeListenerAttached = true;
    }

    try
    {
      _lock.readLock().lock();

      Set<IProduction> candidates = FastSetFactory.newInstance();
      for (IActivationBuffer buffer : getProceduralModule().getModel()
          .getActivationBuffers())
      {
        candidates.clear();

        String bufferName = buffer.getName().toLowerCase();

        /*
         * first, the buffer ambiguous
         */
        getPossibleProductions(bufferName, null, candidates);

        if (candidates.size() != 0)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("%s yielded %s", buffer, candidates));
          container.addAll(candidates);
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "%s buffer yielded no ambiguous buffer productions", buffer));

        // get the source contents
        for (IChunk chunk : buffer.getSourceChunks())
        {
          candidates.clear();

          IChunkType chunkType = chunk.getSymbolicChunk().getChunkType();
          getPossibleProductions(buffer.getName(), chunkType, candidates);

          if (candidates.size() != 0)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(String.format("Chunktype : %s in %s yielded %s",
                  chunkType, buffer, candidates));

            container.addAll(candidates);
          }
          else if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format(
                "%s in %s buffer yielded no candidate productions", chunk,
                buffer.getName()));
        }

      }

      // and the completely ambiguous set
      candidates.clear();
      getPossibleProductions(null, null, candidates);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Ambiguous productions %s", candidates));

      container.addAll(candidates);

      FastSetFactory.recycle(candidates);

      return container;
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public Set<IProduction> getPossibleProductions(String bufferName,
      IChunkType chunkType, Set<IProduction> container)
  {
    try
    {
      _lock.readLock().lock();

      Map<IChunkType, Set<IProduction>> tree = _productionMap.get(bufferName);
      if (tree != null)
      {
        Set<IProduction> productions = tree.get(chunkType);

        if (productions != null) container.addAll(productions);
      }
    }
    finally
    {
      _lock.readLock().unlock();
    }

    return container;
  }

  public Set<IProduction> getPossibleProductions(String bufferName,
      Set<IProduction> container)
  {
    return getPossibleProductions(bufferName, null, container);
  }

  public Set<IProduction> getAmbiguousProductions(Set<IProduction> container)
  {
    return getPossibleProductions(null, null, container);
  }

  protected void index(IProduction production)
  {
    indexInternal(production);
  }

  protected void unindex(IProduction production)
  {
    LOGGER.warn("unindexing is not implemented currently");
  }

  protected void indexInternal(IProduction production)
  {
    Map<String, Set<IChunkType>> map = new TreeMap<String, Set<IChunkType>>();
    Set<String> ambiguous = FastSetFactory.newInstance();
    int minimumSize = Integer.MAX_VALUE;

    for (ICondition condition : production.getSymbolicProduction()
        .getConditions())
      if (condition instanceof IBufferCondition)
      {
        String bufferName = ((IBufferCondition) condition).getBufferName();

        if (condition instanceof ChunkTypeCondition)
        {
          IChunkType ct = ((ChunkTypeCondition) condition).getChunkType();

          if (ct != null)
          {
            Set<IChunkType> chunkTypes = map.get(bufferName);
            if (chunkTypes == null)
            {
              chunkTypes = FastSetFactory.newInstance();
              map.put(bufferName, chunkTypes);
            }

            chunkTypes.add(ct);
            chunkTypes.addAll(ct.getSymbolicChunkType().getChildren());

            /*
             * the smallest will also be the most specific condition (in terms
             * of chunktype only)
             */
            if (minimumSize > chunkTypes.size())
              minimumSize = chunkTypes.size();
          }
          else
            ambiguous.add(bufferName);

        }
        else
          // ambiguous, use a null chunktype
          ambiguous.add(bufferName);
      }

    /*
     * a completely ambiguous production has no buffer conditions at all
     */
    if (ambiguous.size() == 0 && map.size() == 0)
      add(null, null, production);
    else if (_useFullIndexing)
    {
      /*
       * if we are indexing everyone, do so.
       */
      for (String buffer : ambiguous)
        add(buffer, null, production);

      for (Map.Entry<String, Set<IChunkType>> entry : map.entrySet())
      {
        for (IChunkType chunkType : entry.getValue())
          add(entry.getKey(), chunkType, production);

        FastSetFactory.recycle(entry.getValue());
      }
    }
    else // no chunktype info? damn, have to use the ambiguous
    if (map.size() == 0)
      for (String buffer : ambiguous)
        add(buffer, null, production);
    else
      /*
       * if we have chunktype info, use it.
       */
      for (Map.Entry<String, Set<IChunkType>> entry : map.entrySet())
      {
        if (entry.getValue().size() == minimumSize)
          for (IChunkType chunkType : entry.getValue())
            add(entry.getKey(), chunkType, production);

        FastSetFactory.recycle(entry.getValue());
      }

    FastSetFactory.recycle(ambiguous);
  }

  /**
   * add production to the tree based on buffer (null for completely ambiguous),
   * and chunktype (null for type ambiguous)
   * 
   * @param bufferName
   * @param chunkType
   * @param production
   */
  private void add(String bufferName, IChunkType chunkType,
      IProduction production)
  {
    try
    {
      _lock.writeLock().lock();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Indexing %s by %s & %s", production,
            bufferName, chunkType));

      Map<IChunkType, Set<IProduction>> tree = _productionMap.get(bufferName);
      if (tree == null)
      {
        tree = new HashMap<IChunkType, Set<IProduction>>();
        _productionMap.put(bufferName, tree);
      }

      Set<IProduction> productions = tree.get(chunkType);
      if (productions == null)
      {
        productions = createSet();
        tree.put(chunkType, productions);
      }

      productions.add(production);
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

}
