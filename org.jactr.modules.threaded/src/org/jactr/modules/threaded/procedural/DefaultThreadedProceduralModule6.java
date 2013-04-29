package org.jactr.modules.threaded.procedural;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.IProductionSelector;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;

/**
 * We merely replace the {@link IProductionSelector} to bias those productions
 * that don't reference the previousGoal. If none is found, the default behavior
 * of taking the first instantiation is used.
 * 
 * @author harrison
 */
public class DefaultThreadedProceduralModule6 extends DefaultProceduralModule6
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER = LogFactory
                                          .getLog(DefaultThreadedProceduralModule6.class);


  public DefaultThreadedProceduralModule6()
  {
    setProductionSelector(new AlternatingProductionSelector());
    setProductionInstantiator(new CullingProductionInstantiator());
  }

  // @Override
  // protected Collection<IInstantiation> getConflictSetInternal(
  // Collection<IActivationBuffer> buffers)
  // {
  // buffers = new ArrayList<IActivationBuffer>(buffers);
  //
  // IModel model = getModel();
  // IActivationBuffer goalBuffer = model
  // .getActivationBuffer(IActivationBuffer.GOAL);
  // buffers.remove(goalBuffer);
  //
  // /*
  // * first snag the common set - all those productions that could fire based
  // * on the buffers other than goal
  // */
  // Set<IProduction> commonCandidates = new HashSet<IProduction>();
  // for (IActivationBuffer buffer : buffers)
  // for (IChunk sourceChunk : buffer.getSourceChunks())
  // getPossibleProductions(buffer.getName(), sourceChunk, commonCandidates);
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("All possible productions : " + commonCandidates);
  //
  // /*
  // * all the candidate productions given the goal
  // */
  // Map<IChunk, Collection<IInstantiation>> instantiationsMap = new
  // HashMap<IChunk, Collection<IInstantiation>>();
  //
  // for (IChunk goalChunk : goalBuffer.getSourceChunks())
  // {
  // Set<IProduction> candidates = new HashSet<IProduction>();
  // getPossibleProductions(goalBuffer.getName(), goalChunk, candidates);
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Possible productions for " + goalChunk + " : "
  // + candidates);
  //
  // /*
  // * let's remove the possible rejects
  // */
  // commonCandidates.removeAll(candidates);
  //
  // Collection<IInstantiation> instantiations =
  // createAndSortInstantiations(candidates);
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Instantiations for goal : " + goalChunk + " : "
  // + instantiations);
  //
  // instantiationsMap.put(goalChunk, instantiations);
  // }
  //
  // /*
  // * final list..
  // */
  // ArrayList<IInstantiation> instances = new ArrayList<IInstantiation>();
  // Collection<IInstantiation> previousGoalSet = instantiationsMap
  // .remove(_previousGoal);
  // _previousGoal = null;
  //
  // for (Map.Entry<IChunk, Collection<IInstantiation>> insts :
  // instantiationsMap
  // .entrySet())
  // {
  // instances.addAll(insts.getValue());
  // if (_previousGoal == null) _previousGoal = insts.getKey();
  // }
  //
  // if (previousGoalSet != null) instances.addAll(previousGoalSet);
  //
  // /*
  // * ok, so now the instances list contains all the instantiable productions
  // * based on the goals. But what about those that dont depend on goals? Now
  // * they come in.. what this means, though, is that reflexes are considered
  // * less important, which is probably not correct..
  // */
  // instances.addAll(createAndSortInstantiations(commonCandidates));
  //
  // if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
  // {
  // String msg = "Conflict set " + instances;
  //
  // LOGGER.debug(msg);
  // Logger.log(model, Logger.Stream.PROCEDURAL, msg);
  // }
  //
  // return instances;
  // }
  //
  // private void getPossibleProductions(String bufferName, IChunk chunk,
  // Set<IProduction> candidates)
  // {
  // IChunkType chunkType = chunk.getSymbolicChunk().getChunkType();
  // try
  // {
  // _readWriteLock.readLock().lock();
  //
  // Collection<IProduction> possible = null;
  //
  // possible = _allProductionsByChunkType.get(chunkType);
  // if (possible != null)
  // {
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(chunkType + " productions : " + possible);
  // candidates.addAll(possible);
  // }
  //
  // /*
  // * plus any ambiguous productions
  // */
  // possible = _ambiguousProductions.get(bufferName.toLowerCase());
  // if (possible != null)
  // {
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(bufferName + " ambiguous productions : " + possible);
  // candidates.addAll(possible);
  // }
  // }
  // finally
  // {
  // _readWriteLock.readLock().unlock();
  // }
  // }
}
