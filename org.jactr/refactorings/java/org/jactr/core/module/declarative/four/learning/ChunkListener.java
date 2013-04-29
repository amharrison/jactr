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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link;
import org.jactr.core.utils.references.IReferences;

/**
 * we just listen for slot value changes so that Links can be created correctly.
 * 
 * @author developer
 */
public class ChunkListener extends ChunkListenerAdaptor
{
  /**
   * logger definition
   */
  static private final Log          LOGGER = LogFactory
                                               .getLog(ChunkListener.class);


  public ChunkListener()
  {
  }

  @Override
  public void chunkEncoded(ChunkEvent ce)
  {
    /*
     * add that first reference time
     */
    ce.getSource().getSubsymbolicChunk().accessed(ce.getSimulationTime());
  }

  /**
   * handles the updating of associative links, references times, and context/needed
   * tallies. should also handle similarities, but that is not implemented yet and will
   * be by the six version of this.<br>
   * The updating is done here since the listener is removed after encoding, so
   * the master chunk will not have this listener attached
   * 
   * @param event
   * @see org.jactr.core.chunk.event.ChunkListenerAdaptor#mergingInto(org.jactr.core.chunk.event.ChunkEvent)
   */
  @Override
  public void mergingInto(ChunkEvent event)
  {
    IChunk self = event.getSource();
    IChunk master = event.getChunk();

    ISubsymbolicChunk4 selfSSC = (ISubsymbolicChunk4) self
        .getSubsymbolicChunk();
    ISubsymbolicChunk4 masterSSC = (ISubsymbolicChunk4) master
        .getSubsymbolicChunk();
    
    
    //update the references for master
    masterSSC.accessed(event.getSimulationTime());
    
    masterSSC.setTimesInContext(masterSSC.getTimesInContext() + selfSSC.getTimesInContext());
    masterSSC.setTimesNeeded(masterSSC.getTimesNeeded() + selfSSC.getTimesNeeded());
    
    /*
     * and refs
     */
    IReferences refs = masterSSC.getReferences();
    for(double refTime : selfSSC.getReferences().getTimes())
      refs.addReferenceTime(refTime);
    
    
    // will allocate
    Collection<Link> links = selfSSC.getIAssociations(null);

    for (Link iLink : links)
    {
      IChunk iChunk = iLink.getIChunk();
      Link masterLink = masterSSC.getIAssociation(iChunk);
      if (masterLink != null)
      {
        /**
         * need to merge the links
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(master + " already linked to " + iChunk + ", merging");
        masterLink.setCount(Math.max(masterLink.getCount(), iLink.getCount()));
        masterLink.setStrength(Math.max(masterLink.getStrength(), iLink
            .getStrength()));
      }
      else
      {
        /**
         * add the link to master
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(master + " not already linked to " + iChunk
              + ", linking.");
        Link newLink = new Link(master, iChunk, iLink.getCount(), iLink
            .getStrength());
        masterSSC.addLink(newLink);
        ((ISubsymbolicChunk4) iChunk.getSubsymbolicChunk()).addLink(newLink);
      }

      /*
       * reduce the count so that we are sure we're removing the link
       */
      iLink.setCount(1);
      selfSSC.removeLink(iLink);
      ((ISubsymbolicChunk4) iChunk.getSubsymbolicChunk()).removeLink(iLink);
    }

    links.clear();
    links = selfSSC.getJAssociations(links);

    for (Link jLink : links)
    {
      IChunk jChunk = jLink.getJChunk();
      Link masterLink = masterSSC.getIAssociation(jChunk);
      if (masterLink != null)
      {
        /**
         * need to merge the links
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(jChunk + " already linked to " + master + ", merging");
        masterLink.setCount(Math.max(masterLink.getCount(), jLink.getCount()));
        masterLink.setStrength(Math.max(masterLink.getStrength(), jLink
            .getStrength()));

      }
      else
      {
        /**
         * add the link to master
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(jChunk + " not already linked to " + master
              + ", linking.");
        Link newLink = new Link(jChunk, master, jLink.getCount(), jLink
            .getStrength());
        masterSSC.addLink(newLink);
        ((ISubsymbolicChunk4) jChunk.getSubsymbolicChunk()).addLink(newLink);
      }

      /*
       * reduce the count so that we are sure we're removing the link
       */
      jLink.setCount(1);
      selfSSC.removeLink(jLink);
      ((ISubsymbolicChunk4) jChunk.getSubsymbolicChunk()).removeLink(jLink);
    }
  }


  @Override
  public void slotChanged(ChunkEvent ce)
  {
    IChunk iChunk = ce.getSource();
    Object oldValue = ce.getOldSlotValue();
    Object newValue = ce.getNewSlotValue();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(iChunk + "." + ce.getSlotName() + "=" + newValue + " (was "
          + oldValue + ")");

    /*
     * we can only do this if ISubsymbolicChunk is ISubsymbolicChunk4
     */
    if (!(iChunk.getSubsymbolicChunk() instanceof ISubsymbolicChunk4))
    {
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn("Can only adjust associative links if the chunk's subsymbolic is derived from ISubsymbolicChunk4");
      return;
    }

    /*
     * I chunk is the owner that references J
     */
    ISubsymbolicChunk4 sscI = (ISubsymbolicChunk4) iChunk.getSubsymbolicChunk();

    /*
     * first the old chunk value..
     */
    if (oldValue instanceof IChunk)
    {
      IChunk jChunk = (IChunk) oldValue;
      if (jChunk.getSubsymbolicChunk() instanceof ISubsymbolicChunk4)
      {
        ISubsymbolicChunk4 sscJ = (ISubsymbolicChunk4) jChunk
            .getSubsymbolicChunk();

        Link sJI = sscJ.getIAssociation(iChunk);

        if (sJI != null)
        {
          sJI.decrement();
          if (sJI.getCount() == 0)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Removing link between " + iChunk + " and " + jChunk
                  + " : " + sJI);
            sscI.removeLink(sJI);
            sscJ.removeLink(sJI);
          }
          else if (LOGGER.isDebugEnabled())
            LOGGER.debug("Multiple links established between " + iChunk
                + " and " + jChunk + " decrementing : " + sJI);
        }
      }
      else if (LOGGER.isWarnEnabled())
        LOGGER.warn("old value " + jChunk
            + " doesn't have a ISubsymbolicChunk4, nothing to be done");
    }

    /*
     * now for the new one
     */
    if (newValue instanceof IChunk)
    {
      IChunk jChunk = (IChunk) newValue;
      if (jChunk.getSubsymbolicChunk() instanceof ISubsymbolicChunk4)
      {
        ISubsymbolicChunk4 sscJ = (ISubsymbolicChunk4) jChunk
            .getSubsymbolicChunk();

        Link sJI = sscJ.getIAssociation(iChunk);
        /*
         * is this a new linkage?
         */
        if (sJI == null)
        {
          sJI = new Link(jChunk, iChunk);
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Adding link between " + iChunk + " and " + jChunk
                + " : " + sJI);
          sscI.addLink(sJI);
          sscJ.addLink(sJI);
        }
        else
        {
          sJI.increment(); // not new, but we need to increment the link
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Link already established between " + iChunk + " and "
                + jChunk + " incrementing : " + sJI);
        }
      }
      else if (LOGGER.isWarnEnabled())
        LOGGER.warn("new value " + jChunk
            + " doesn't have a ISubsymbolicChunk4, nothing to be done");
    }
  }
}
