/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.procedural.six;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.procedural.IConflictSetAssembler;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.IProductionInstantiator;
import org.jactr.core.module.procedural.IProductionSelector;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.module.random.six.DefaultRandomModule;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.action.IBufferAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.condition.AbstractBufferCondition;
import org.jactr.core.production.condition.ChunkCondition;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.VariableCondition;
import org.jactr.core.production.six.DefaultProduction6;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * default procedural module. It provides extensibility by using
 * {@link IProductionInstantiator}, {@link IProductionSelector}, and
 * {@link IConflictSetAssembler} <b>strict harvesting</b> is implemented by
 * {@link #addProductionInternal(IProduction)}. The {@link IProduction}'s
 * {@link ICondition}s are checked. If they are {@link IBufferCondition}, it
 * checks to see if {@link IActivationBuffer#isStrictHarvestingEnabled()} and if
 * so, ensures that there is an {@link IBufferAction} for that buffer as well,
 * if not, an {@link RemoveAction} is added (and therefore the production
 * explicitly removes the chunk).
 * 
 * @see http://jactr.org/node/132
 * @author harrison
 */
public class DefaultProceduralModule6 extends AbstractModule implements
    IProceduralModule6, IParameterized
{

  /**
   * logger definition
   */
  static public final Log                                                   LOGGER                               = LogFactory
                                                                                                                     .getLog(DefaultProceduralModule6.class);

  private ACTREventDispatcher<IProceduralModule, IProceduralModuleListener> _eventDispatcher;

  protected Map<String, IProduction>                                        _allProductionsByName;

  // protected Map<IChunkType, Collection<IProduction>>
  // _allProductionsByChunkType;

  // protected Map<String, Collection<IProduction>> _ambiguousProductions;

  protected ReentrantReadWriteLock                                          _readWriteLock;

  protected double                                                          _productionFiringTime                = 0.05;                                     // 50ms

  protected long                                                            _productionsFired                    = 0;

  protected boolean                                                         _breakExpectedUtilityTiesRandomly    = true;

  protected double                                                          _expectedUtilityNoise;

  protected ProductionUtilityComparator                                     _comparator;

  protected IRandomModule                                                   _randomModule;

  private IProductionSelector                                               _selector;

  private IProductionInstantiator                                           _instantiator;

  private IConflictSetAssembler                                             _conflictSetAssembler;

  private boolean                                                           _parallelizeInstantiationsEnabled    = false;

  static public final String                                                ENABLE_PARALLEL_INSTANTIATIONS_PARAM = "EnableParallelInstantiations";

  // private Collection<Map<String, Object>> _provisionalBindings;

  public DefaultProceduralModule6()
  {
    super("procedural");
    _allProductionsByName = new TreeMap<String, IProduction>();
    // _allProductionsByChunkType = new HashMap<IChunkType,
    // Collection<IProduction>>();
    // _ambiguousProductions = new HashMap<String, Collection<IProduction>>();
    _readWriteLock = new ReentrantReadWriteLock();
    _eventDispatcher = new ACTREventDispatcher<IProceduralModule, IProceduralModuleListener>();

    setProductionSelector(new DefaultProductionSelector());

    setProductionInstantiator(new DefaultProductionInstantiator());

    setConflictSetAssembler(new DefaultConflictSetAssembler(false));

    _comparator = new ProductionUtilityComparator() {
      @Override
      public int compare(IProduction one, IProduction two)
      {
        int rtn = super.compare(one, two);
        if (rtn != 0) return rtn;

        /*
         * we only break random ties if the two instantiations (which these
         * actually are) are not of the same production
         */
        if (_breakExpectedUtilityTiesRandomly
            && !((IInstantiation) one).getProduction().equals(
                ((IInstantiation) two).getProduction()))
          return _randomModule.randomBoolean() ? 1 : -1;

        // otherwise we maintain insertion order
        return 0;
      }
    };
  }

  public void setProductionSelector(IProductionSelector selector)
  {
    if (_selector != null) _selector.setProceduralModule(null);
    _selector = selector;
    _selector.setProceduralModule(this);
  }

  public IProductionSelector getProductionSelector()
  {
    return _selector;
  }

  public void setProductionInstantiator(IProductionInstantiator instantiator)
  {
    if (_instantiator != null) _instantiator.setProceduralModule(null);
    _instantiator = instantiator;
    _instantiator.setProceduralModule(this);
  }

  public IProductionInstantiator getProductionInstantiator()
  {
    return _instantiator;
  }

  public void setConflictSetAssembler(IConflictSetAssembler assembler)
  {
    if (_conflictSetAssembler != null)
      _conflictSetAssembler.setProceduralModule(null);
    _conflictSetAssembler = assembler;
    _conflictSetAssembler.setProceduralModule(this);
  }

  public IConflictSetAssembler getConflictSetAssembler()
  {
    return _conflictSetAssembler;
  }

  public void addListener(IProceduralModuleListener listener, Executor executor)
  {
    _eventDispatcher.addListener(listener, executor);
  }

  public void removeListener(IProceduralModuleListener listener)
  {
    _eventDispatcher.removeListener(listener);
  }

  @Override
  public void dispose()
  {
    super.dispose();
    try
    {
      _readWriteLock.writeLock().lock();
      // actually dispose of the productions
      for (IProduction production : _allProductionsByName.values())
        production.dispose();

      // _ambiguousProductions.clear();
      // _ambiguousProductions = null;
      //
      // _allProductionsByChunkType.clear();
      // _allProductionsByChunkType = null;

      _allProductionsByName.clear();
      // _allProductionsByName = null;

      _eventDispatcher.clear();
      // _eventDispatcher = null;
    }
    finally
    {
      _readWriteLock.writeLock().unlock();
    }
  }

  protected void fireProductionCreated(IProduction production)
  {
    _eventDispatcher.fire(new ProceduralModuleEvent(this,
        ProceduralModuleEvent.Type.PRODUCTION_CREATED, production));
  }

  protected void fireProductionAdded(IProduction production)
  {
    _eventDispatcher.fire(new ProceduralModuleEvent(this,
        ProceduralModuleEvent.Type.PRODUCTION_ADDED, production));
    if (Logger.hasLoggers(getModel()))
      Logger.log(getModel(), Logger.Stream.PROCEDURAL, "Encoded " + production);
  }

  protected void fireProductionWillFire(IInstantiation instantiation)
  {
    _eventDispatcher.fire(new ProceduralModuleEvent(this,
        ProceduralModuleEvent.Type.PRODUCTION_WILL_FIRE, instantiation));
    if (Logger.hasLoggers(getModel()))
      Logger.log(getModel(), Logger.Stream.PROCEDURAL, "Can fire "
          + instantiation);
  }

  protected void fireProductionFired(IInstantiation instantiation)
  {
    _eventDispatcher.fire(new ProceduralModuleEvent(this,
        ProceduralModuleEvent.Type.PRODUCTION_FIRED, instantiation));
    if (Logger.hasLoggers(getModel()))
      Logger
          .log(getModel(), Logger.Stream.PROCEDURAL, "Fired " + instantiation);
  }

  protected void fireProductionsMerged(IProduction original,
      IProduction duplicate)
  {
    ArrayList<IProduction> prods = new ArrayList<IProduction>();
    prods.add(original);
    prods.add(duplicate);
    _eventDispatcher.fire(new ProceduralModuleEvent(this,
        ProceduralModuleEvent.Type.PRODUCTIONS_MERGED, prods));
  }

  protected void fireConflictSetAssembled(Collection<IInstantiation> instances)
  {
    _eventDispatcher.fire(new ProceduralModuleEvent(this,
        ProceduralModuleEvent.Type.CONFLICT_SET_ASSEMBLED, instances));
    if (Logger.hasLoggers(getModel()))
      Logger.log(getModel(), Logger.Stream.PROCEDURAL, "Conflict Set "
          + instances);
  }

  public void setParallelInstantiationsEnabled(boolean enable)
  {
    _parallelizeInstantiationsEnabled = enable;
  }

  public boolean isParallelInstantiationsEnabled()
  {
    return _parallelizeInstantiationsEnabled;
  }

  protected IProduction addProductionInternal(IProduction production)
  {
    ISymbolicProduction symProd = production.getSymbolicProduction();

    // /*
    // * we need all the chunktypes that this production matches against this
    // info
    // * is used to accelerate conflict set assembly
    // */
    // Set<IChunkType> candidateChunkTypes = new HashSet<IChunkType>();
    /*
     * in some cases where no chunktype can be infered, we just snag the buffer
     * name
     */
    Set<String> bufferNames = new HashSet<String>();
    Set<String> ambiguousBufferNames = new HashSet<String>();
    for (ICondition condition : symProd.getConditions())
      if (condition instanceof ChunkTypeCondition)
      {
        ChunkTypeCondition ctc = (ChunkTypeCondition) condition;
        ctc.getChunkType();

        bufferNames.add(ctc.getBufferName());

        // if (chunkType != null) candidateChunkTypes.add(chunkType);
      }
      else if (condition instanceof ChunkCondition)
      {
        ChunkCondition cc = (ChunkCondition) condition;
        cc.getChunk().getSymbolicChunk().getChunkType();

        bufferNames.add(cc.getBufferName());

        // if (chunkType != null) candidateChunkTypes.add(chunkType);
      }
      else if (condition instanceof AbstractBufferCondition)
      {
        String bufferName = ((AbstractBufferCondition) condition)
            .getBufferName();

        if (condition instanceof VariableCondition)
          bufferNames.add(bufferName);

        /*
         * this will catch all queries and variable conditions. These are
         * production conditions from which we can't immediately determine the
         * chunktype of the buffer contents
         */
        ambiguousBufferNames.add(bufferName);
      }

    /*
     * ok, to support strict harvesting, which as far as I can tell is just a
     * stylistic implementation. I cannot see an architectural reason for it. It
     * just looks like a convenience for modelers so that they don't have to
     * manually invoke remove actions.. so, I zip through the buffers, for those
     * that have strict harvesting enabled, I check the actions of the
     * production. If it contains an action on that buffer, we leave it alone.
     * If not, we add a remove, thereby enforcing strict harvesting
     */
    Collection<IAction> actions = new ArrayList<IAction>(symProd.getActions());
    IModel model = getModel();
    for (String bufferName : bufferNames)
    {
      IActivationBuffer buffer = model.getActivationBuffer(bufferName);
      boolean actionFound = false;
      if (buffer.isStrictHarvestingEnabled())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Strict harvesting enabled for " + bufferName
              + ", checking actions of " + symProd.getName());

        for (IAction action : actions)
          if (action instanceof IBufferAction
              && ((IBufferAction) action).getBufferName().equals(bufferName))
          {
            /*
             * if there is a modify or a remove, we don't have to do anything.
             */
            if (action instanceof RemoveAction
                || action instanceof ModifyAction
                || action instanceof AddAction) actionFound = true;
            break;
          }

        if (!actionFound)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER
                .debug(bufferName
                    + " requires strict harvest but "
                    + symProd.getName()
                    + " doesn't operate on the buffer after the match. Adding a remove");

          symProd.addAction(new RemoveAction(bufferName));
        }
      }
    }

    /*
     * figure out all the children of the chunktypes.. since any production that
     * could be fired for chunkTypeA could fire for chunkTypeB if chunkTypeB is
     * a child (derived from) of chunkTypeA
     */
    // Set<IChunkType> chunkTypesToProcess = new HashSet<IChunkType>(
    // candidateChunkTypes);
    // for (IChunkType chunkType : candidateChunkTypes)
    // chunkTypesToProcess
    // .addAll(chunkType.getSymbolicChunkType().getChildren());

    _readWriteLock.writeLock().lock();

    /*
     * make sure the name is unique
     */
    String productionName = getSafeName(symProd.getName(),
        _allProductionsByName);
    symProd.setName(productionName);

    production.encode();

    /*
     * add it to the name map
     */
    _allProductionsByName.put(productionName.toLowerCase(), production);

    // /*
    // * add it to the chunktype maps
    // */
    // for (IChunkType chunkType : chunkTypesToProcess)
    // {
    // Collection<IProduction> productions = _allProductionsByChunkType
    // .get(chunkType);
    // if (productions == null)
    // {
    // productions = new ArrayList<IProduction>();
    // _allProductionsByChunkType.put(chunkType, productions);
    // }
    // productions.add(production);
    // }
    //
    // /*
    // * now for the ambiguous conditions
    // */
    // for (String bufferName : ambiguousBufferNames)
    // {
    // Collection<IProduction> productions = _ambiguousProductions
    // .get(bufferName);
    // if (productions == null)
    // {
    // productions = new ArrayList<IProduction>();
    // _ambiguousProductions.put(bufferName, productions);
    // }
    // productions.add(production);
    // }

    _readWriteLock.writeLock().unlock();

    fireProductionAdded(production);

    return production;
  }

  public CompletableFuture<IProduction> addProduction(
      final IProduction production)
  {
    Callable<IProduction> callable = new Callable<IProduction>() {

      public IProduction call() throws Exception
      {
        return addProductionInternal(production);
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  protected IProduction removeProductionInternal(IProduction production)
  {
    ISymbolicProduction symProd = production.getSymbolicProduction();

    /*
     * we need all the chunktypes that this production matches against this info
     * is used to accelerate conflict set assembly
     */
    // Set<IChunkType> candidateChunkTypes = new HashSet<IChunkType>();
    /*
     * in some cases where no chunktype can be infered, we just snag the buffer
     * name
     */
    Set<String> bufferNames = new HashSet<String>();
    // Set<String> ambiguousBufferNames = new HashSet<String>();
    for (ICondition condition : symProd.getConditions())
      if (condition instanceof ChunkTypeCondition)
      {
        ChunkTypeCondition ctc = (ChunkTypeCondition) condition;
        ctc.getChunkType();

        bufferNames.add(ctc.getBufferName());

        // if (chunkType != null) candidateChunkTypes.add(chunkType);
      }
      else if (condition instanceof ChunkCondition)
      {
        ChunkCondition cc = (ChunkCondition) condition;
        cc.getChunk().getSymbolicChunk().getChunkType();

        bufferNames.add(cc.getBufferName());

        // if (chunkType != null) candidateChunkTypes.add(chunkType);
      }
      else if (condition instanceof AbstractBufferCondition)
      {
        String bufferName = ((AbstractBufferCondition) condition)
            .getBufferName();

        if (condition instanceof VariableCondition)
          bufferNames.add(bufferName);

        /*
         * this will catch all queries and variable conditions. These are
         * production conditions from which we can't immediately determine the
         * chunktype of the buffer contents
         */
        // ambiguousBufferNames.add(bufferName);
      }

    /*
     * figure out all the children of the chunktypes.. since any production that
     * could be fired for chunkTypeA could fire for chunkTypeB if chunkTypeB is
     * a child (derived from) of chunkTypeA
     */
    // Set<IChunkType> chunkTypesToProcess = new HashSet<IChunkType>(
    // candidateChunkTypes);
    // for (IChunkType chunkType : candidateChunkTypes)
    // chunkTypesToProcess
    // .addAll(chunkType.getSymbolicChunkType().getChildren());

    _readWriteLock.writeLock().lock();

    /*
     * make sure the name is unique
     */
    String productionName = symProd.getName();

    /*
     * add it to the name map
     */
    _allProductionsByName.remove(productionName.toLowerCase());

    // /*
    // * add it to the chunktype maps
    // */
    // for (IChunkType chunkType : chunkTypesToProcess)
    // {
    // Collection<IProduction> productions = _allProductionsByChunkType
    // .get(chunkType);
    // if (productions != null) productions.remove(production);
    // }

    // /*
    // * now for the ambiguous conditions
    // */
    // for (String bufferName : ambiguousBufferNames)
    // {
    // Collection<IProduction> productions = _ambiguousProductions
    // .get(bufferName);
    // if (productions != null) productions.remove(production);
    // }

    _readWriteLock.writeLock().unlock();

    return production;
  }

  public Future<IProduction> removeProduction(final IProduction production)
  {
    Callable<IProduction> callable = new Callable<IProduction>() {

      public IProduction call() throws Exception
      {
        return removeProductionInternal(production);
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  protected IProduction createProductionInternal(String name)
  {
    IModel model = getModel();

    IProduction production = new DefaultProduction6(model);
    production.getSymbolicProduction().setName(
        getSafeName(name, _allProductionsByName));

    fireProductionCreated(production);
    return production;
  }

  public CompletableFuture<IProduction> createProduction(final String name)
  {
    Callable<IProduction> callable = new Callable<IProduction>() {

      public IProduction call() throws Exception
      {
        return createProductionInternal(name);
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  protected Collection<IInstantiation> getConflictSetInternal(
      Collection<IActivationBuffer> buffers)
  {
    IModel model = getModel();
    Set<IProduction> productions = new TreeSet<IProduction>(
        new ProductionNameComparator());

    _conflictSetAssembler.getConflictSet(productions);

    /*
     * we now have every production that could conceivably fire, and some extra
     * ones too now we must zip through them trying to instantiate
     */
    if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
    {
      StringBuilder sb = new StringBuilder("Considering ");
      sb.append(productions.size()).append(" productions for conflict set");
      String message = sb.toString();
      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
      Logger.log(model, Logger.Stream.PROCEDURAL, message);
    }

    Collection<IInstantiation> keepers = createAndSortInstantiations(productions);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Final conflict set " + keepers);

    fireConflictSetAssembled(keepers);
    return keepers;
  }

  /**
   * iterates over productions, attempting to instantiate each. Those that can
   * be instantiated will be sorted by utility and returned. If
   * 
   * @param productions
   * @return
   */
  protected Collection<IInstantiation> createAndSortInstantiations(
      Collection<IProduction> productions)
  {
    // internal for now, should be exposed as a back parameter
    int minimumBlockSize = 20;

    List<IInstantiation> keepers = new ArrayList<IInstantiation>();

    // some minimum work level, below which we keep it single core
    if (_parallelizeInstantiationsEnabled && productions.size() > 4)
    {
      /*
       * we split the productions among the threads in the pool and dispatch
       */
      ExecutorCompletionService<Collection<IInstantiation>> ecs = new ExecutorCompletionService<Collection<IInstantiation>>(
          ExecutorServices.getExecutor(ExecutorServices.POOL));
      int size = productions.size();
      int tasks = size / minimumBlockSize;
      tasks += size % minimumBlockSize == 0 ? 0 : 1;

      List<IProduction> working = FastListFactory.newInstance();
      working.addAll(productions);

      for (int i = 0; i < tasks; i++)
      {
        int start = i * minimumBlockSize;
        int end = Math.min(i * minimumBlockSize + minimumBlockSize, size);
        InstantiationTask it = new InstantiationTask(
            working.subList(start, end), getProductionInstantiator(),
            getModel(), _randomModule, getExpectedUtilityNoise());

        ecs.submit(it);
      }

      /*
       * everyone is churning away.. let's harvest and sort as we go.
       */
      for (int i = 0; i < tasks; i++)
        try
        {
          Collection<IInstantiation> result = ecs.take().get();
          keepers.addAll(result);
        }
        catch (Exception e)
        {
          LOGGER.error("Failed to instantiatie productions in parallel: ", e);
        }

      FastListFactory.recycle(working);
    }
    else
      try
      {
        InstantiationTask it = new InstantiationTask(productions,
            getProductionInstantiator(), getModel(), _randomModule,
            getExpectedUtilityNoise());
        keepers.addAll(it.call());
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to instantiatie productions in serial: ", e);
      }

    Collections.sort(keepers, _comparator);
    return keepers;
  }

  public CompletableFuture<Collection<IInstantiation>> getConflictSet(
      final Collection<IActivationBuffer> buffers)
  {
    Callable<Collection<IInstantiation>> callable = new Callable<Collection<IInstantiation>>() {

      public Collection<IInstantiation> call() throws Exception
      {
        return getConflictSetInternal(buffers);
      }

    };

    return delayedFuture(callable, getExecutor());
  }

  protected IProduction getProductionInternal(String name)
  {
    _readWriteLock.readLock().lock();
    IProduction rtn = _allProductionsByName.get(name.toLowerCase());
    _readWriteLock.readLock().unlock();
    return rtn;
  }

  public CompletableFuture<IProduction> getProduction(final String name)
  {
    Callable<IProduction> callable = new Callable<IProduction>() {

      public IProduction call()
      {
        return getProductionInternal(name);
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  protected IInstantiation selectInstantiationInternal(
      Collection<IInstantiation> instantiations)
  {
    return _selector.select(instantiations);
  }

  public CompletableFuture<IInstantiation> selectInstantiation(
      final Collection<IInstantiation> instantiations)
  {
    Callable<IInstantiation> callable = new Callable<IInstantiation>() {

      public IInstantiation call() throws Exception
      {
        return selectInstantiationInternal(instantiations);
      }

    };

    return delayedFuture(callable, getExecutor());
  }

  protected Double fireProductionInternal(IInstantiation instantiation,
      double firingTime)
  {
    try
    {
      IModel model = getModel();

      fireProductionWillFire(instantiation);

      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Firing " + instantiation);
        Logger.log(model, Logger.Stream.PROCEDURAL, "Firing " + instantiation);
      }

      setNumberOfProductionsFired(_productionsFired + 1);

      instantiation.fire(firingTime);

      fireProductionFired(instantiation);
    }
    catch (ModelTerminatedException mte)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Model has terminated naturally");
      return Double.NaN;
    }
    return instantiation.getActionLatency();
  }

  public CompletableFuture<Double> fireProduction(
      final IInstantiation instantiation, final double firingTime)
  {
    Callable<Double> callable = new Callable<Double>() {
      public Double call()
      {
        return fireProductionInternal(instantiation, firingTime);
      }
    };

    return delayedFuture(callable, getExecutor());
  }

  public double getDefaultProductionFiringTime()
  {
    return _productionFiringTime;
  }

  public void setDefaultProductionFiringTime(double firingTime)
  {
    double old = _productionFiringTime;
    _productionFiringTime = firingTime;
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new ProceduralModuleEvent(this,
          DEFAULT_PRODUCTION_FIRING_TIME, old, firingTime));
  }

  @Override
  public void initialize()
  {
    // noop
    /*
     * snag the random module
     */
    _randomModule = (IRandomModule) getModel().getModule(IRandomModule.class);
    if (_randomModule == null)
      _randomModule = DefaultRandomModule.getInstance();

    /**
     * attach a little something to the buffers so we know when to clear
     * provisional bindings
     */
    for (IActivationBuffer buffer : getModel().getActivationBuffers())
      buffer.addListener(new IActivationBufferListener() {

        public void chunkMatched(ActivationBufferEvent abe)
        {
          // noop

        }

        public void requestAccepted(ActivationBufferEvent abe)
        {
          // noop

        }

        public void sourceChunkAdded(ActivationBufferEvent abe)
        {
          // _provisionalBindings = null;
        }

        public void sourceChunkRemoved(ActivationBufferEvent abe)
        {
          // _provisionalBindings = null;

        }

        public void sourceChunksCleared(ActivationBufferEvent abe)
        {
          // _provisionalBindings = null;

        }

        public void statusSlotChanged(ActivationBufferEvent abe)
        {
          // TODO Auto-generated method stub

        }

        public void parameterChanged(IParameterEvent pe)
        {
          // TODO Auto-generated method stub

        }

      }, null); // inline
  }

  public double getExpectedUtilityNoise()
  {
    return _expectedUtilityNoise;
  }

  public long getNumberOfProductionsFired()
  {
    return _productionsFired;
  }

  public void setExpectedUtilityNoise(double noise)
  {
    double old = _expectedUtilityNoise;
    _expectedUtilityNoise = noise;
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new ProceduralModuleEvent(this,
          EXPECTED_UTILITY_NOISE, old, noise));
  }

  public void setNumberOfProductionsFired(long fired)
  {
    long old = _productionsFired;
    _productionsFired = fired;
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new ProceduralModuleEvent(this,
          NUMBER_OF_PRODUCTIONS_FIRED, old, fired));
  }

  protected Collection<IProduction> getProductionsInternal()
  {
    try
    {
      _readWriteLock.readLock().lock();
      return new ArrayList<IProduction>(_allProductionsByName.values());
    }
    finally
    {
      _readWriteLock.readLock().unlock();
    }
  }

  public CompletableFuture<Collection<IProduction>> getProductions()
  {
    return delayedFuture(new Callable<Collection<IProduction>>() {

      public Collection<IProduction> call() throws Exception
      {
        return getProductionsInternal();
      }

    }, getExecutor());
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (NUMBER_OF_PRODUCTIONS_FIRED.equalsIgnoreCase(key))
      return "" + getNumberOfProductionsFired();
    if (EXPECTED_UTILITY_NOISE.equalsIgnoreCase(key))
      return "" + getExpectedUtilityNoise();
    if (DEFAULT_PRODUCTION_FIRING_TIME.equalsIgnoreCase(key))
      return "" + getDefaultProductionFiringTime();
    if (ENABLE_PARALLEL_INSTANTIATIONS_PARAM.equalsIgnoreCase(key))
      return "" + isParallelInstantiationsEnabled();
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>();
    rtn.add(EXPECTED_UTILITY_NOISE);
    rtn.add(DEFAULT_PRODUCTION_FIRING_TIME);
    rtn.add(NUMBER_OF_PRODUCTIONS_FIRED);
    rtn.add(ENABLE_PARALLEL_INSTANTIATIONS_PARAM);
    return rtn;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (NUMBER_OF_PRODUCTIONS_FIRED.equalsIgnoreCase(key))
      setNumberOfProductionsFired(ParameterHandler.numberInstance()
          .coerce(value).longValue());
    else if (EXPECTED_UTILITY_NOISE.equalsIgnoreCase(key))
      setExpectedUtilityNoise(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (DEFAULT_PRODUCTION_FIRING_TIME.equalsIgnoreCase(key))
      setDefaultProductionFiringTime(ParameterHandler.numberInstance()
          .coerce(value).doubleValue());
    else if (ENABLE_PARALLEL_INSTANTIATIONS_PARAM.equalsIgnoreCase(key))
      setParallelInstantiationsEnabled(ParameterHandler.booleanInstance()
          .coerce(value).booleanValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format(
          "%s doesn't recognize %s. Available parameters : %s", getClass()
              .getSimpleName(), key, getSetableParameters()));

  }

  // /**
  // * in order to handle the iterative nature of the instantiation process in
  // * addition to the possibility for multiple sources chunks, provisional
  // * bindings must be created for all the chunk permutations. Ugh.
  // */
  // private Collection<VariableBindings> computeProvisionalBindings(
  // IProduction production)
  // {
  // IModel model = getModel();
  // Collection<VariableBindings> returnedProvisionalBindings = new
  // ArrayList<VariableBindings>();
  // Collection<VariableBindings> provisionalBindings = new
  // ArrayList<VariableBindings>();
  //
  // VariableBindings initialBinding = new VariableBindings();
  // initialBinding.bind("=model", model);
  // provisionalBindings.add(initialBinding);
  //
  // /*
  // * with all the buffers this production should match against, we snag their
  // * sources
  // */
  // for (ICondition condition : production.getSymbolicProduction()
  // .getConditions())
  // if (condition instanceof IBufferCondition
  // && !(condition instanceof QueryCondition))
  // {
  // IActivationBuffer buffer = model
  // .getActivationBuffer(((IBufferCondition) condition).getBufferName());
  //
  // Collection<IChunk> sourceChunks = buffer.getSourceChunks();
  //
  // if (sourceChunks.size() == 0) continue;
  //
  // Map<IChunk, Collection<VariableBindings>> keyedProvisionalBindings = new
  // HashMap<IChunk, Collection<VariableBindings>>();
  //
  // /*
  // * if there are more than one source chunk, we need to duplicate all the
  // * provisional bindings, add the binding for the source chunk and then
  // * merge the duplicates back into the provisional set
  // */
  // for (IChunk source : sourceChunks)
  // {
  // Collection<VariableBindings> bindings = provisionalBindings;
  // // more than one, duplicate.
  // if (keyedProvisionalBindings.size() != 0)
  // bindings = copyBindings(provisionalBindings);
  //
  // // add binding to all bindings
  // for (VariableBindings binding : bindings)
  // binding.bind("=" + buffer.getName(), source, buffer);
  //
  // // store
  // keyedProvisionalBindings.put(source, bindings);
  // }
  //
  // /*
  // * merge these provisionals back into the full set. if there was only
  // * one source chunk, it was already added to the provisional binding, so
  // * we ignore it. If multi, we add all
  // */
  // for (Collection<VariableBindings> bindings : keyedProvisionalBindings
  // .values())
  // if (bindings != provisionalBindings)
  // provisionalBindings.addAll(bindings);
  // }
  //
  // returnedProvisionalBindings.addAll(provisionalBindings);
  //
  // return returnedProvisionalBindings;
  // }
  //
  // private Collection<VariableBindings> copyBindings(
  // Collection<VariableBindings> src)
  // {
  // Collection<VariableBindings> rtn = new ArrayList<VariableBindings>(
  // src.size());
  // for (VariableBindings map : src)
  // rtn.add(map.clone());
  // return rtn;
  // }

  public void reset()
  {
    // noop
  }
}
