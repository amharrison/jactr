package org.jactr.core.module.procedural.six;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.IProductionInstantiator;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.bindings.VariableBindingsFactory;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.production.six.ISubsymbolicProduction6;
import org.jactr.core.utils.collections.FastCollectionFactory;
import org.jactr.core.utils.collections.FastListFactory;

/**
 * delegate task to actually do the instantiation and evaluation of the
 * instantiations. This is delegated out to make parallization easier since this
 * chunk of code takes the longest, in general.
 * 
 * @author harrison
 */
public class InstantiationTask implements Callable<Collection<IInstantiation>>
{
  /**
   * Logger definition
   */
  static private final transient Log    LOGGER = LogFactory
                                                   .getLog(InstantiationTask.class);

  private final List<IProduction>       _productionsToInstantiate;

  private final IProductionInstantiator _instantiator;

  private final IRandomModule           _randomModule;

  private final IModel                  _model;

  private final double                  _expectedUtilityNoise;

  public InstantiationTask(Collection<IProduction> productions,
      IProductionInstantiator instantiator, IModel model, IRandomModule random,
      double utilityNoise)
  {
    _productionsToInstantiate = FastListFactory.newInstance();
    _productionsToInstantiate.addAll(productions);
    _instantiator = instantiator;
    _randomModule = random;
    _expectedUtilityNoise = utilityNoise;
    _model = model;
  }

  public Collection<IInstantiation> call() throws Exception
  {
    List<IInstantiation> keepers = new ArrayList<IInstantiation>();
    boolean debugEnabled = LOGGER.isDebugEnabled();
    boolean hasLoggers = Logger.hasLoggers(_model); // this can be relatively
                                                    // costly to repeat

    @SuppressWarnings("unchecked")
    List<VariableBindings> provisionalBindings = FastListFactory
        .newInstance();

    StringBuilder message = new StringBuilder();
    if (debugEnabled)
      LOGGER.debug(String.format("Attempting to instantiatie %s",
          _productionsToInstantiate));

    for (IProduction production : _productionsToInstantiate)
      try
      {
        provisionalBindings.clear(); // clear the recycled container

        computeProvisionalBindings(production, provisionalBindings);

        if (debugEnabled) LOGGER.debug("Instantiating " + production);

        Collection<IInstantiation> instantiations = _instantiator.instantiate(
            production, provisionalBindings);


        for (IInstantiation instantiation : instantiations)
        {
          double noise = _randomModule.logisticNoise(_expectedUtilityNoise);
          ISubsymbolicProduction6 p = (ISubsymbolicProduction6) instantiation
              .getSubsymbolicProduction();
          double utility = p.getExpectedUtility();

          if (Double.isNaN(utility)) utility = p.getUtility();

          if (debugEnabled)
            LOGGER.debug(production + " utility: " + utility + " noise:"
                + noise + " expected utility: " + (utility + noise));

          p.setExpectedUtility(utility + noise);

          if (debugEnabled || hasLoggers)
          {
            message.delete(0, message.length());
            message.append("Instantiated ").append(production)
                .append(" expected utility ");
            message.append(utility + noise).append(" (").append(noise)
                .append(" noise)");

            String msg = message.toString();
            if (debugEnabled) LOGGER.debug(msg);
            if (hasLoggers) Logger.log(_model, Logger.Stream.PROCEDURAL, msg);
          }

          keepers.add(instantiation);
        }
      }
      catch (CannotInstantiateException cie)
      {
        if (debugEnabled || hasLoggers)
        {
          String msg = cie.getMessage();
          if (debugEnabled) LOGGER.debug(msg);
          if (hasLoggers) Logger.log(_model, Logger.Stream.PROCEDURAL, msg);
        }
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Could not instanitate %s ", production), e);
      }

    /*
     * before recycling provisionalBinding collection, let's recycle the
     * bindings
     */
    for (VariableBindings bindings : provisionalBindings)
      VariableBindingsFactory.recycle(bindings);

    FastListFactory.recycle(provisionalBindings);
    FastListFactory.recycle(_productionsToInstantiate);

    return keepers;
  }

  /**
   * in order to handle the iterative nature of the instantiation process in
   * addition to the possibility for multiple sources chunks, provisional
   * bindings must be created for all the chunk permutations. Ugh.
   */
  private Collection<VariableBindings> computeProvisionalBindings(
      IProduction production, Collection<VariableBindings> bindingsContainer)
  {

    @SuppressWarnings("unchecked")
    Collection<VariableBindings> provisionalBindings = FastCollectionFactory
        .newInstance();

    VariableBindings initialBinding = VariableBindingsFactory.newInstance();
    initialBinding.bind("=model", _model);
    provisionalBindings.add(initialBinding);


        /*
     * reusbale map collection.
     */
    Map<IChunk, Collection<VariableBindings>> keyedProvisionalBindings = new HashMap<IChunk, Collection<VariableBindings>>();
    /*
     * with all the buffers this production should match against, we snag their
     * source chunks, these will be the values bound to =bufferName. for each
     * source chunk in a given buffer, we have to create an additional set of
     * provisional bindings
     */
    Collection<IChunk> sourceChunks = FastCollectionFactory.newInstance();
    for (ICondition condition : production.getSymbolicProduction()
        .getConditions())
      if (condition instanceof IBufferCondition
          && !(condition instanceof QueryCondition))
      {
        IActivationBuffer buffer = _model
            .getActivationBuffer(((IBufferCondition) condition).getBufferName());

        sourceChunks.clear();
        buffer.getSourceChunks(sourceChunks);

        /*
         * nothing there? nothing to bind.
         */
        if (sourceChunks.size() == 0) continue;

        /*
         * if there are more than one source chunk, we need to duplicate all the
         * provisional bindings, add the binding for the source chunk and then
         * merge the duplicates back into the provisional set
         */
        for (IChunk source : sourceChunks)
        {
          Collection<VariableBindings> bindings = provisionalBindings;
          // we've already processed at least 1 source chunk
          // we need to copy ALL the existing bindings, since we are doing
          // full permutations. As we do so, we bind =bufferName to source
          if (keyedProvisionalBindings.size() != 0)
            bindings = copyAndBinding(provisionalBindings, buffer, source);
          else
            // otherwise, we just add the binding (bindings.size =1)
            for (VariableBindings binding : bindings)
              binding.bind("=" + buffer.getName(), source, buffer);

          // store
          keyedProvisionalBindings.put(source, bindings);
        }

        /*
         * merge these provisionals back into the full set. if there was only
         * one source chunk, it was already added to the provisional binding, so
         * we ignore it. If multi, we add all
         */
        for (Collection<VariableBindings> bindings : keyedProvisionalBindings
            .values())
          if (bindings != provisionalBindings)
          {
            /*
             * since there was more than one source chunk, we grab those
             * bindings, and then free up the collection backing it.
             */
            provisionalBindings.addAll(bindings);
            FastCollectionFactory.recycle(bindings);
          }
        keyedProvisionalBindings.clear(); // cleanup for reuse
      }

    bindingsContainer.addAll(provisionalBindings);

    FastCollectionFactory.recycle(sourceChunks);
    FastCollectionFactory.recycle(provisionalBindings);

    return bindingsContainer;
  }

  /**
   * we make a full copy of this collection, deeply including the bindings, and
   * while we are at it, we will add this binding
   * 
   * @param src
   * @return
   */
  private Collection<VariableBindings> copyAndBinding(
      Collection<VariableBindings> src, IActivationBuffer buffer, IChunk source)
  {
    @SuppressWarnings("unchecked")
    Collection<VariableBindings> rtn = FastCollectionFactory.newInstance();

    for (VariableBindings map : src)
    {
      VariableBindings clone = VariableBindingsFactory.newInstance();
      clone.copy(map);
      clone.bind("=" + buffer.getName(), clone, source);
      rtn.add(clone);
    }
    return rtn;
  }

}
