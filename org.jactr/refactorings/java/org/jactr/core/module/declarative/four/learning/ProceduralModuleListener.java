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
package org.jactr.core.module.declarative.four.learning;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.ISlot;

/**
 * we use a proceduralmodule listener to track the chunks that are accessed in
 * service of firing a production. We listen to two methods:
 * productionWillFire() to snag all the chunks that the production requires to
 * fire, and productionFired() to call the various parameter update methods on
 * the chunks
 * 
 * @author developer
 */
public class ProceduralModuleListener implements IProceduralModuleListener
{
  /**
   * logger definition
   */
  static private final Log    LOGGER           = LogFactory
                                                   .getLog(ProceduralModuleListener.class);

  /*
   * all the chunks that a production matches against, keyed on buffer name
   */
  private Map<String, IChunk> _matchedChunks;


  public ProceduralModuleListener(DefaultDeclarativeLearningModule4 learning)
  {
    _matchedChunks = new TreeMap<String, IChunk>();
  }

  public void conflictSetAssembled(ProceduralModuleEvent pme)
  {

  }

  public void productionAdded(ProceduralModuleEvent pme)
  {

  }

  /**
   * if goals are special, then only the goal chunk's slot values will have
   * their times in context incremented. if false, all buffer chunk slot values
   * will be incremented
   * 
   * @return
   */
  protected boolean goalsAreSpecial()
  {
    return true;
  }

  /**
   * if true, associative links will be established between the chunks in each
   * of the buffers (or just the goal buffer if {@link #goalsAreSpecial()} )
   * 
   * @return
   */
  protected boolean associateConcurrentChunks()
  {
    return true;
  }

  /**
   * snag the chunks that the production that is about to fire is matched
   * against
   * 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionWillFire(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionWillFire(ProceduralModuleEvent pme)
  {
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

  public void productionCreated(ProceduralModuleEvent pme)
  {
  }

  /**
   * adjust the parameter values for the chunks that enabled this production to
   * fire
   * 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionFired(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionFired(ProceduralModuleEvent pme)
  {

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adjusting chunk parameter values");

    /*
     * adjust timesInContext and timesNeeded
     */
    for (Map.Entry<String, IChunk> entry : _matchedChunks.entrySet())
    {
      String bufferName = entry.getKey();
      IChunk iChunk = entry.getValue();
      ISubsymbolicChunk4 sscI = (ISubsymbolicChunk4) iChunk
          .getSubsymbolicChunk();

      /*
       * everyone who was matched against, was needed
       */
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Incrementing times needed for " + iChunk);
      sscI.incrementTimesNeeded();

      /*
       * all the slots of the goal chunk get their timesInContext incremented.
       * arguably, this should apply to all the bound chunks in all the buffers
       */
      if (IActivationBuffer.GOAL.equals(bufferName) || !goalsAreSpecial())
      {
        for (ISlot slot : iChunk.getSymbolicChunk().getSlots())
          if (slot.getValue() instanceof IChunk)
          {
            IChunk jChunk = (IChunk) slot.getValue();

            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Incrementing times in context " + jChunk
                  + " value of " + iChunk + "." + slot.getName());

            jChunk.getSubsymbolicChunk().incrementTimesInContext();
          }

        /*
         * now we link the chunk to all the other chunks. This is where the
         * runtime context comes into play. we are linking all the other chunks
         * (J) to this one (I) so that they (J) can spread activation to I.
         */
        if (associateConcurrentChunks())
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Updating associative links for " + iChunk);
          for (IChunk jChunk : _matchedChunks.values())
          {
            if (iChunk.equals(jChunk)) continue; // ignore

            ISubsymbolicChunk4 sscJ = (ISubsymbolicChunk4) jChunk
                .getSubsymbolicChunk();

            Link sJI = sscI.getJAssociation(jChunk);
            if (sJI != null)
            {
              if (LOGGER.isDebugEnabled()) LOGGER.debug("Incrementing " + sJI);
              sJI.incrementFNICJ();
            }
            else
            {
              sJI = new Link(iChunk, jChunk);
              if (LOGGER.isDebugEnabled())
                LOGGER.debug("Adding new link " + sJI);
              sscI.addLink(sJI);
              sscJ.addLink(sJI);
            }
          }
        }

      }

    }
  }

  public void productionsMerged(ProceduralModuleEvent pme)
  {

  }

  @SuppressWarnings("unchecked")
  public void parameterChanged(IParameterEvent pe)
  {

  }

}
