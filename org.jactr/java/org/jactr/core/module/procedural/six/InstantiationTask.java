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

import javolution.util.FastList;

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
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.production.six.ISubsymbolicProduction6;

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

  private final FastList<IProduction>   _productionsToInstantiate;

  private final IProductionInstantiator _instantiator;

  private final IRandomModule           _randomModule;

  private final double                  _expectedUtilityNoise;

  public InstantiationTask(Collection<IProduction> productions,
      IProductionInstantiator instantiator, IRandomModule random,
      double utilityNoise)
  {
    _productionsToInstantiate = FastList.newInstance();
    _productionsToInstantiate.addAll(productions);
    _instantiator = instantiator;
    _randomModule = random;
    _expectedUtilityNoise = utilityNoise;
  }

  public Collection<IInstantiation> call() throws Exception
  {
    IModel model = _randomModule.getModel();
    List<IInstantiation> keepers = new ArrayList<IInstantiation>();

    StringBuilder message = new StringBuilder();

    for (IProduction production : _productionsToInstantiate)
      /*
       * only consider those with sufficient utility
       */
      // double tmpGain =
      // production.getSubsymbolicProduction().getExpectedGain();
      // if (tmpGain >= _expectedGainThreshold)
      try
      {
        Collection<VariableBindings> provisionalBindings = computeProvisionalBindings(production);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Instantiating " + production);

        Collection<IInstantiation> instantiations = _instantiator.instantiate(
            production, provisionalBindings);

        for (IInstantiation instantiation : instantiations)
        {
          double noise = _randomModule.logisticNoise(_expectedUtilityNoise);
          ISubsymbolicProduction6 p = (ISubsymbolicProduction6) instantiation
              .getSubsymbolicProduction();
          double utility = p.getExpectedUtility();

          if (Double.isNaN(utility)) utility = p.getUtility();

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(production + " utility: " + utility + " noise:"
                + noise + " expected utility: " + (utility + noise));

          p.setExpectedUtility(utility + noise);

          if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
          {
            message.delete(0, message.length());
            message.append("Instantiated ").append(production)
                .append(" expected utility ");
            message.append(utility + noise).append(" (").append(noise)
                .append(" noise)");

            String msg = message.toString();
            if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
            if (Logger.hasLoggers(model))
              Logger.log(model, Logger.Stream.PROCEDURAL, msg);
          }

          keepers.add(instantiation);
        }
      }
      catch (CannotInstantiateException cie)
      {

        if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
        {
          String msg = cie.getMessage();
          LOGGER.debug(msg);
          Logger.log(model, Logger.Stream.PROCEDURAL, msg);
        }
      }

    FastList.recycle(_productionsToInstantiate);

    return keepers;
  }

  /**
   * in order to handle the iterative nature of the instantiation process in
   * addition to the possibility for multiple sources chunks, provisional
   * bindings must be created for all the chunk permutations. Ugh.
   */
  private Collection<VariableBindings> computeProvisionalBindings(
      IProduction production)
  {
    IModel model = _randomModule.getModel();
    Collection<VariableBindings> returnedProvisionalBindings = new ArrayList<VariableBindings>();
    Collection<VariableBindings> provisionalBindings = new ArrayList<VariableBindings>();

    VariableBindings initialBinding = new VariableBindings();
    initialBinding.bind("=model", model);
    provisionalBindings.add(initialBinding);

    /*
     * with all the buffers this production should match against, we snag their
     * sources
     */
    for (ICondition condition : production.getSymbolicProduction()
        .getConditions())
      if (condition instanceof IBufferCondition
          && !(condition instanceof QueryCondition))
      {
        IActivationBuffer buffer = model
            .getActivationBuffer(((IBufferCondition) condition).getBufferName());

        Collection<IChunk> sourceChunks = buffer.getSourceChunks();

        if (sourceChunks.size() == 0) continue;

        Map<IChunk, Collection<VariableBindings>> keyedProvisionalBindings = new HashMap<IChunk, Collection<VariableBindings>>();

        /*
         * if there are more than one source chunk, we need to duplicate all the
         * provisional bindings, add the binding for the source chunk and then
         * merge the duplicates back into the provisional set
         */
        for (IChunk source : sourceChunks)
        {
          Collection<VariableBindings> bindings = provisionalBindings;
          // more than one, duplicate.
          if (keyedProvisionalBindings.size() != 0)
            bindings = copyBindings(provisionalBindings);

          // add binding to all bindings
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
            provisionalBindings.addAll(bindings);
      }

    returnedProvisionalBindings.addAll(provisionalBindings);

    return returnedProvisionalBindings;
  }

  private Collection<VariableBindings> copyBindings(
      Collection<VariableBindings> src)
  {
    Collection<VariableBindings> rtn = new ArrayList<VariableBindings>(
        src.size());
    for (VariableBindings map : src)
      rtn.add(map.clone());
    return rtn;
  }

}
