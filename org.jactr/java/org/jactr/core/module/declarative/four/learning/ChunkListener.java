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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.module.declarative.basic.chunk.IChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.ISubsymbolicChunkFactory;
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
  static private final Log LOGGER = LogFactory.getLog(ChunkListener.class);

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

    // remove the listener
    ce.getSource().removeListener(this);
  }

  /**
   * references times. should also handle similarities, but that is not
   * implemented yet.<br>
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

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Merging %s into %s", self, master));

    master.getModel().getDeclarativeModule();

    ISubsymbolicChunk4 selfSSC = (ISubsymbolicChunk4) self
        .getSubsymbolicChunk().getAdapter(ISubsymbolicChunk4.class);
    ISubsymbolicChunk4 masterSSC = (ISubsymbolicChunk4) master
        .getSubsymbolicChunk().getAdapter(ISubsymbolicChunk4.class);

    // update the references for master
    masterSSC.accessed(event.getSimulationTime());

    IChunk selfCopiedFrom = (IChunk) self
        .getMetaData(IChunkFactory.COPIED_FROM_KEY);
    Object sscCopied = self
        .getMetaData(ISubsymbolicChunkFactory.SUBSYMBOLICS_COPIED_KEY);

    boolean subsymbolicsCopied = sscCopied != null ? (Boolean) sscCopied
        : false;

    boolean copiedFromMaster = selfCopiedFrom != null
        && selfCopiedFrom.equals(master) && subsymbolicsCopied;

    /*
     * and refs
     */
    if (!copiedFromMaster)
    {
      IReferences refs = masterSSC.getReferences();
      for (double refTime : selfSSC.getReferences().getTimes())
        refs.addReferenceTime(refTime);
    }

    // remove listener
    self.removeListener(this);

    /*
     * associatve links are now handled by ..associative.ChunkListener
     */
    //
    //
    // masterSSC.setTimesInContext(masterSSC.getTimesInContext()
    // + selfSSC.getTimesInContext());
    // masterSSC.setTimesNeeded(masterSSC.getTimesNeeded()
    // + selfSSC.getTimesNeeded());
    //
    //
    // /**
    // * Chunki contains Chunkj.
    // */
    //
    // // self is the j chunk
    // Collection<IAssociativeLink> links = selfSSC.getIAssociations(null);
    //
    // for (IAssociativeLink iLink : links)
    // {
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug(String.format("Testing old iLink %s", iLink));
    //
    // IChunk iChunk = iLink.getIChunk();
    // // skip self link
    // if (iChunk.equals(self)) continue;
    //
    // Link4 masterLink = (Link4) masterSSC.getIAssociation(iChunk);
    // if (masterLink != null)
    // {
    // /**
    // * need to merge the links
    // */
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug(master + " already linked to " + iChunk + ", merging");
    // masterLink.setCount(Math.max(masterLink.getCount(), ((Link4) iLink)
    // .getCount()));
    // masterLink.setStrength(Math.max(masterLink.getStrength(), iLink
    // .getStrength()));
    // }
    // else
    // {
    // /**
    // * add the link to master
    // */
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug(String.format(
    // "%s not already linked to %s, linking. %s is linked to %s",
    // master, iChunk, masterSSC.getIAssociations(null)));
    //
    // Link4 newLink = (Link4) decM.createLink(iChunk, master);
    // newLink.setCount(((Link4) iLink).getCount());
    // newLink.setStrength(iLink.getStrength());
    //
    // masterSSC.addLink(newLink);
    // ((ISubsymbolicChunk4) iChunk.getSubsymbolicChunk()).addLink(newLink);
    // }
    //
    // /*
    // * reduce the count so that we are sure we're removing the link
    // */
    // ((Link4) iLink).setCount(1);
    // selfSSC.removeLink(iLink);
    // ((ISubsymbolicChunk4) iChunk.getSubsymbolicChunk()).removeLink(iLink);
    // }
    //
    // links.clear();
    // links = selfSSC.getJAssociations(links);
    //
    // for (IAssociativeLink jLink : links)
    // {
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug(String.format("Testing old jLink %s", jLink));
    //
    // IChunk jChunk = jLink.getJChunk();
    // // skip self link
    // if (jChunk.equals(self)) continue;
    // Link4 masterLink = (Link4) masterSSC.getJAssociation(jChunk);
    // if (masterLink != null)
    // {
    // /**
    // * need to merge the links
    // */
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug(jChunk + " already linked to " + master + ", merging");
    // masterLink.setCount(Math.max(masterLink.getCount(), ((Link4) jLink)
    // .getCount()));
    // masterLink.setStrength(Math.max(masterLink.getStrength(), jLink
    // .getStrength()));
    //
    // }
    // else
    // {
    // /**
    // * add the link to master
    // */
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug(String.format(
    // "%s not already linked to %s, linking. %s is linked to %s",
    // jChunk, master, master, masterSSC.getJAssociations(null)));
    //
    // Link4 newLink = (Link4) decM.createLink(master, jChunk);
    // newLink.setCount(((Link4) jLink).getCount());
    // newLink.setStrength(jLink.getStrength());
    //
    // masterSSC.addLink(newLink);
    // ((ISubsymbolicChunk4) jChunk.getSubsymbolicChunk()).addLink(newLink);
    // }
    //
    // /*
    // * reduce the count so that we are sure we're removing the link
    // */
    // ((Link4) jLink).setCount(1);
    // selfSSC.removeLink(jLink);
    // ((ISubsymbolicChunk4) jChunk.getSubsymbolicChunk()).removeLink(jLink);
    // }
  }

  /**
   * also handled by the associative.ChunkListener
   */
  // @Override
  // public void slotChanged(ChunkEvent ce)
  // {
  // IChunk iChunk = ce.getSource();
  // Object oldValue = ce.getOldSlotValue();
  // Object newValue = ce.getNewSlotValue();
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(iChunk + "." + ce.getSlotName() + "=" + newValue + " (was "
  // + oldValue + ")");
  //
  // /*
  // * we can only do this if ISubsymbolicChunk is ISubsymbolicChunk4
  // */
  // if (!(iChunk.getSubsymbolicChunk() instanceof ISubsymbolicChunk4))
  // {
  // if (LOGGER.isWarnEnabled())
  // LOGGER
  // .warn("Can only adjust associative links if the chunk's subsymbolic is derived from ISubsymbolicChunk4");
  // return;
  // }
  //
  // /*
  // * I chunk is the owner that references J
  // */
  // ISubsymbolicChunk4 sscI = (ISubsymbolicChunk4)
  // iChunk.getSubsymbolicChunk();
  //
  // /*
  // * first the old chunk value..
  // */
  // if (oldValue instanceof IChunk)
  // {
  // IChunk jChunk = (IChunk) oldValue;
  // if (jChunk.getSubsymbolicChunk() instanceof ISubsymbolicChunk4)
  // {
  // ISubsymbolicChunk4 sscJ = (ISubsymbolicChunk4) jChunk
  // .getSubsymbolicChunk();
  //
  // IAssociativeLink sJI = sscJ.getIAssociation(iChunk);
  //
  // if (sJI != null)
  // {
  // ((Link4) sJI).decrement();
  // if (((Link4) sJI).getCount() == 0)
  // {
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Removing link between " + iChunk + " and " + jChunk
  // + " : " + sJI);
  // sscI.removeLink(sJI);
  // sscJ.removeLink(sJI);
  // }
  // else if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Multiple links established between " + iChunk
  // + " and " + jChunk + " decrementing : " + sJI);
  // }
  // }
  // else if (LOGGER.isWarnEnabled())
  // LOGGER.warn("old value " + jChunk
  // + " doesn't have a ISubsymbolicChunk4, nothing to be done");
  // }
  //
  // /*
  // * now for the new one
  // */
  // if (newValue instanceof IChunk)
  // {
  // IChunk jChunk = (IChunk) newValue;
  // if (jChunk.getSubsymbolicChunk() instanceof ISubsymbolicChunk4)
  // {
  // ISubsymbolicChunk4 sscJ = (ISubsymbolicChunk4) jChunk
  // .getSubsymbolicChunk();
  //
  // IAssociativeLink sJI = sscJ.getIAssociation(iChunk);
  // /*
  // * is this a new linkage?
  // */
  // if (sJI == null)
  // {
  // sJI = jChunk.getModel().getDeclarativeModule().createLink(iChunk,
  // jChunk);
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Adding link between " + iChunk + " and " + jChunk
  // + " : " + sJI);
  // sscI.addLink(sJI);
  // sscJ.addLink(sJI);
  // }
  // else
  // {
  // ((Link4) sJI).increment(); // not new, but we need to increment the
  // // link
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Link already established between " + iChunk + " and "
  // + jChunk + " incrementing : " + sJI);
  // }
  // }
  // else if (LOGGER.isWarnEnabled())
  // LOGGER.warn("new value " + jChunk
  // + " doesn't have a ISubsymbolicChunk4, nothing to be done");
  // }
  // }
}
