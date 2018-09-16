/*
 * Created on Oct 25, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.declarative.four.associative;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * we use a proceduralmodule listener to track the chunks that are accessed in
 * service of firing a production. We listen to two methods:
 * productionWillFire() to snag all the chunks that the production requires to
 * fire, and productionFired() to call the various parameter update methods on
 * the chunks
 * 
 * @author developer
 */
public class ProceduralModuleListener extends ProceduralModuleListenerAdaptor
{
  /**
   * logger definition
   */
  static private final Log                  LOGGER     = LogFactory
                                                           .getLog(ProceduralModuleListener.class);

  static private final String               BOUND_GOAL = "="
                                                           + IActivationBuffer.GOAL;

  /*
   * all the chunks that a production matches against, keyed on buffer name
   */
  private final SortedMap<String, IChunk>   _matchedChunks;

  private final IDeclarativeLearningModule4 _learningModule;

  public ProceduralModuleListener(IDeclarativeLearningModule4 learning)
  {
    _matchedChunks = new TreeMap<String, IChunk>();
    _learningModule = learning;
  }

  /**
   * snag the chunks that the production that is about to fire is matched
   * against
   * 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionWillFire(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  @Override
  public void productionWillFire(ProceduralModuleEvent pme)
  {
    if (!_learningModule.isAssociativeLearningEnabled()) return;

    /*
     * snag all the chunks that will be matched against
     */
    _matchedChunks.clear();
    IInstantiation instantiation = (IInstantiation) pme.getProduction();
    for (ICondition condition : instantiation.getSymbolicProduction()
        .getConditions())
      if (condition instanceof IBufferCondition)
      {
        IBufferCondition bufferCondition = (IBufferCondition) condition;

        Object boundVariable = instantiation.getVariableBindings().get(
            "=" + bufferCondition.getBufferName());
        if (boundVariable instanceof IChunk)
          _matchedChunks.put(bufferCondition.getBufferName(),
              (IChunk) boundVariable);
      }
  }

  /**
   * adjust the parameter values for the chunks that enabled this production to
   * fire
   * 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionFired(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  @Override
  public void productionFired(ProceduralModuleEvent pme)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adjusting chunk parameter values");

    if (_learningModule.isAssociativeLearningEnabled())
      updateStatistics(_matchedChunks);
  }

  public void updateStatistics(SortedMap<String, IChunk> matchedChunks)
  {
    Collection<ISlot> slots = FastCollectionFactory.newInstance();
    for (Map.Entry<String, IChunk> matchedEntry : matchedChunks.entrySet())
    {
      IChunk matchedChunk = matchedEntry.getValue();
      /*
       * all chunks that were matched were needed..
       */
      ISubsymbolicChunk4 sscMatched = matchedChunk
          .getAdapter(ISubsymbolicChunk4.class);

      sscMatched.incrementTimesNeeded(1);

      /**
       * and in 4.0 the goal defines the context
       */
      if (matchedEntry.getKey().equalsIgnoreCase(BOUND_GOAL))
      {
        /*
         * the contents of the chunk define the context. Ideally we'd only use
         * those slots that were matched in the production. but for now, we'll
         * use everybody
         */
        slots.clear();
        for (ISlot slot : matchedChunk.getSymbolicChunk().getSlots(slots))
          if (slot.getValue() instanceof IChunk)
          {
            IChunk jChunk = (IChunk) slot.getValue();
            ISubsymbolicChunk4 sscJ = jChunk
                .getAdapter(ISubsymbolicChunk4.class);
            sscJ.incrementTimesInContext(1);

            /*
             * if J is in the context and there is a link between J and another
             * matched Chunk (i), then we need to strengthen that association
             * directly.
             */
            for (Map.Entry<String, IChunk> iEntry : matchedChunks.tailMap(
                matchedEntry.getKey()).entrySet())
            {
              IChunk iChunk = iEntry.getValue();
              // skip ourselves
              if (iChunk.equals(matchedChunk)) continue;

              // Link4 link = (Link4) sscJ.getIAssociation(iChunk);
              Link4 link = (Link4) ChunkListener.getAssociativeLink(jChunk,
                  iChunk, true);

              /*
               * the link exists. It might not.. should we create one here?
               */
              if (link != null)
              {
                if (LOGGER.isDebugEnabled())
                  LOGGER.debug(String.format("Incrementing F(Ni|Cj) for %s",
                      link));

                link.incrementFNICJ();
              }
            }
          }
      }

    }

    FastCollectionFactory.recycle(slots);
  }

}
