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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.module.declarative.associative.IAssociativeLinkContainer;
import org.jactr.core.module.declarative.basic.chunk.IChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.ISubsymbolicChunkFactory;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * Chunk listener that handles associative links. This does three things. 1) it
 * creates (or removes) associative links between this chunk (i) and any chunks
 * it has as slot values (j). 2) it handles the associative links when a chunk
 * is merged. 3) updates the statistics used for Sji calculation on merging
 * 
 * @author developer
 */
public class ChunkListener extends ChunkListenerAdaptor
{
  /**
   * logger definition
   */
  static private final Log                       LOGGER           = LogFactory
                                                                      .getLog(ChunkListener.class);


  private final DefaultAssociativeLinkageSystem4 _linkageSystem;

  public ChunkListener(DefaultAssociativeLinkageSystem4 linkageSystem)
  {
    _linkageSystem = linkageSystem;
  }

  @Override
  public void chunkEncoded(ChunkEvent ce)
  {
    // remove listener
    ce.getSource().removeListener(this);
  }

  /**
   * handles the updating of associative links, The updating is done here since
   * the listener is removed after encoding, so the master chunk will not have
   * this listener attached
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
      LOGGER.debug(String.format("** Start Merge ** Merging %s into %s", self,
          master));

    IChunk selfCopiedFrom = (IChunk) self
        .getMetaData(IChunkFactory.COPIED_FROM_KEY);
    Object sscCopied = self
        .getMetaData(ISubsymbolicChunkFactory.SUBSYMBOLICS_COPIED_KEY);

    boolean subsymbolicsCopied = sscCopied != null ? (Boolean) sscCopied
        : false;

    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug(String.format("Master %s(N:%.2f, C:%.2f)", master, master
          .getSubsymbolicChunk().getTimesNeeded(), master.getSubsymbolicChunk()
          .getTimesInContext()));
      LOGGER.debug(String.format("Mergee %s(N:%.2f, C:%.2f)", self, self
          .getSubsymbolicChunk().getTimesNeeded(), self.getSubsymbolicChunk()
          .getTimesInContext()));
    }

    if (selfCopiedFrom != null && selfCopiedFrom.equals(master)
        && subsymbolicsCopied)
      // in this case, self has the most recent links including those from
      // master, so we can just
      // replace master's links with self's links (after juggling IDs)
      copyMergingIntoMaster(master, self);
    else
      /*
       * if self was copied from a different chunk or is a new blank chunk, we
       * have to merge the two..
       */
      newMergingIntoMaster(master, self);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("** End Merge **"));
  }

  /**
   * merge copy's self link with master's. We also check to see if master and
   * copy are linked to each other. If they are, merging would result in
   * overcompensation of FNiCj by the FNiCj of the master-copy link. This
   * returns the corrections necessary [copyI-masterJ, masterI-copyJ]. We need
   * both correction values since the rules of association are not specified at
   * this level. If links are cooccuring, then both values should be the same,
   * but if links are created or strengthened unidirectionally, we need both
   * values.
   * 
   * @param master
   * @param copy
   * @param absorb
   *          if true, copy's self-link values will override masters
   * @return
   */
  protected double[] processSelfLinks(IChunk master, IChunk copy, boolean absorb)
  {
    double[] correction = new double[2];

    master.getAdapter(IAssociativeLinkContainer.class);
    copy.getAdapter(IAssociativeLinkContainer.class);



    // link between copy-master
    Link4 link = (Link4) getAssociativeLink(copy, master, false);
    if (link != null)
    {
      correction[0] = link.getFNICJ();
      // remove so that they don't become redundant self-links
      _linkageSystem.removeLink(link);
    }

    // link between master-copy
    link = (Link4) getAssociativeLink(copy, master, true);
    if (link != null)
    {
      correction[1] = link.getFNICJ();
      // remove so that they don't become redundant self-links
      _linkageSystem.removeLink(link);
    }

    /*
     * self won't actually have a self link since it isn't created until
     * encoding. but lets just make sure
     */
    link = (Link4) getAssociativeLink(copy, copy, true);

    if (link != null)
    {
      // master selflink
      Link4 mLink = (Link4) getAssociativeLink(master, master, false);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "%s has a self link %s. %s into master self link %s", copy, link,
            absorb ? "Absorbing" : "Merging", mLink));

      if (absorb) // if copy was a literally subsymbolic copy, it will
        // have the original values of mLink, so we just swap them entirely
        mLink.setFNICJ(link.getFNICJ());
      else
        mLink.setFNICJ(mLink.getFNICJ() + link.getFNICJ());

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Master self link now : %s", mLink));

      // and remove so we don't double process
      _linkageSystem.removeLink(link);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("FNiCj corrections i-j [%.2f] j-i[%.2f]",
          correction[0], correction[1]));

    return correction;
  }

  /**
   * @param master
   * @param copy
   * @param absorbLinks
   *          true if copies links should override master's values. false if
   *          they are to be merged instead
   * @param processInboundLinks
   *          true to process the links where copy and master are the i, false
   *          if they are the j
   */
  protected void processLinks(IChunk master, IChunk copy, boolean absorbLinks,
      boolean processInboundLinks, double fnicjCorrection)
  {

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s merging into %s, processing %sLinks",
          copy, master, processInboundLinks ? "j" : "i"));

    ISubsymbolicChunk4 masterSSC = master.getSubsymbolicChunk().getAdapter(
        ISubsymbolicChunk4.class);

    IAssociativeLinkContainer cCont = copy
        .getAdapter(IAssociativeLinkContainer.class);
    master.getAdapter(IAssociativeLinkContainer.class);

    Collection<IAssociativeLink> links = FastCollectionFactory.newInstance();
    if (processInboundLinks)
      cCont.getInboundLinks(links);
    else
      cCont.getOutboundLinks(links);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Testing %d links", links.size()));

    for (IAssociativeLink link : links)
    {

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Testing link %s", link));

      Link4 oldLink = (Link4) link;

      /**
       * otherChunk is the other side of the link. If we are processing jLinks
       * (i.e., copy is the iChunk), the otherChunk is link.getJChunk();
       */
      IChunk otherChunk = processInboundLinks ? oldLink.getJChunk() : oldLink
          .getIChunk();

      /*
       * skip the self-links
       */
      if (copy.equals(otherChunk) || master.equals(otherChunk)) continue;

      /*
       * regardless of what we do with this, we need to remove it from the
       * otherChunk so that otherChunk doesn't link back to the merged chunk.
       */
      ISubsymbolicChunk4 otherSSC = otherChunk
          .getAdapter(ISubsymbolicChunk4.class);

      double fNiCj = oldLink.getFNICJ();
      int count = oldLink.getCount();

      /*
       * further traces support that this correction code is actually creating a
       * problem that was inadvertantly corrected downstream. This is a test
       * change - commenting out the correct, until confirmation (AMH 6/30/16)
       */

      // if (fnicjCorrection > 0)
      // {
      // /**
      // *
      // */
      // fNiCj = Math.max(0, fNiCj - fnicjCorrection);
      // if (LOGGER.isDebugEnabled())
      // LOGGER.debug(String.format("Original FNiCj(%.2f) corrected (%.2f)",
      // oldLink.getFNICJ(), fNiCj));
      // }

      /*
       * now we process the link, either merging it or absorbing its values
       */
      Link4 masterLink = (Link4) getAssociativeLink(master, otherChunk,
          !processInboundLinks); // we need to flip this so that I/Js match
      if (masterLink != null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String
                  .format(
                      "Master(N:%.2f, C:%.2f) is already linked to %s(N:%.2f, C:%.2f) via %s",
                      masterSSC.getTimesNeeded(),
                      masterSSC.getTimesInContext(), otherChunk,
                      otherSSC.getTimesNeeded(), otherSSC.getTimesInContext(),
                      masterLink));

        if (absorbLinks)
        {
          // copy's values supercede master's

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format(
                "Absorbing copy's link values %s to master %s", oldLink,
                masterLink));

          masterLink.setCount(count);
          masterLink.setFNICJ(fNiCj);
        }
        else
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format(
                "Merging copy's link values %s into master %s", oldLink,
                masterLink));

          // merge the values of the links
          masterLink.setCount(Math.max(count, masterLink.getCount()));
          masterLink.setFNICJ(masterLink.getFNICJ() + fNiCj);
        }

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Updating link %s", masterLink));
      }
      else
      {
        Link4 newLink = null;
        if (processInboundLinks)
          newLink = (Link4) _linkageSystem.createLink(master, otherChunk);
        else
          newLink = (Link4) _linkageSystem.createLink(otherChunk, master);

        newLink.setCount(count);
        newLink.setFNICJ(fNiCj);

        // no longer make these calls directly
        // otherSSC.addLink(newLink);
        // masterSSC.addLink(newLink);

        _linkageSystem.addLink(newLink);

        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String
                  .format(
                      "Master was not linked to %s, but copy was %s. Created new link %s",
                      otherChunk, oldLink, newLink));
      }

      /*
       * after all is said and done, remove the old link. we mess with the count
       * to be sure we remove the link entirely.
       */
      oldLink.setCount(1);

      _linkageSystem.removeLink(oldLink);
      // copySSC.removeLink(oldLink);
      // otherSSC.removeLink(oldLink);
    }

    FastCollectionFactory.recycle(links);
  }

  /**
   * Return a single associative link that existing between containingChunk and
   * referenceChunk. If getOutbound is true, containingChunk is j &
   * referenceChunk is I. If false, containingChunk is i & reference chunk is j
   * 
   * @param containingChunk
   * @param referenceChunk
   * @param getOutbound
   * @return
   */
  static public IAssociativeLink getAssociativeLink(IChunk containingChunk,
      IChunk referenceChunk, boolean getOutbound)
  {
    Collection<IAssociativeLink> links = FastCollectionFactory.newInstance();
    try
    {
      IAssociativeLinkContainer alc = containingChunk
          .getAdapter(IAssociativeLinkContainer.class);
      if (getOutbound)
        alc.getOutboundLinks(referenceChunk, links);
      else
        alc.getInboundLinks(referenceChunk, links);

      if (links.size() == 0) return null;
      return links.iterator().next();
    }
    finally
    {
      FastCollectionFactory.recycle(links);
    }
  }

  /**
   * if a copy of master and master are to be merged, and copy has master's
   * subsymbolics, then master can simply assume the same values and links.
   * 
   * @param master
   * @param copy
   */
  protected void copyMergingIntoMaster(IChunk master, IChunk copy)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s, a copy of %s is being merged back in.",
          copy, master));

    ISubsymbolicChunk4 selfSSC = copy.getAdapter(ISubsymbolicChunk4.class);
    ISubsymbolicChunk4 masterSSC = master.getAdapter(ISubsymbolicChunk4.class);

    /**
     * Sji stats
     */
    masterSSC.setTimesInContext(selfSSC.getTimesInContext());
    masterSSC.setTimesNeeded(selfSSC.getTimesNeeded());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Master %s(N:%.2f, C:%.2f)", master, master
          .getSubsymbolicChunk().getTimesNeeded(), master.getSubsymbolicChunk()
          .getTimesInContext()));

    double[] correction = processSelfLinks(master, copy, true);

    // process master-J
    processLinks(master, copy, true, true, correction[0]);
    // process I-master
    processLinks(master, copy, true, false, correction[1]);
  }

  /**
   * a new chunk merging into an existing one requires that the new links be
   * updated and absorbed into master, while addressing links between master and
   * identical (new).
   * 
   * @param master
   * @param identical
   */
  protected void newMergingIntoMaster(IChunk master, IChunk identical)
  {

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s is now identical to %s. Merging.",
          identical, master));

    ISubsymbolicChunk4 selfSSC = identical.getAdapter(ISubsymbolicChunk4.class);
    ISubsymbolicChunk4 masterSSC = master.getAdapter(ISubsymbolicChunk4.class);

    /**
     * Sji stats
     */

    masterSSC.setTimesInContext(masterSSC.getTimesInContext()
        + selfSSC.getTimesInContext());
    masterSSC.setTimesNeeded(masterSSC.getTimesNeeded()
        + selfSSC.getTimesNeeded());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Master %s(N:%.2f, C:%.2f)", master, master
          .getSubsymbolicChunk().getTimesNeeded(), master.getSubsymbolicChunk()
          .getTimesInContext()));

    double[] correction = processSelfLinks(master, identical, false);
    // process master-J
    processLinks(master, identical, false, true, correction[0]);
    // process I-master
    processLinks(master, identical, false, false, correction[1]);
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

    if (oldValue instanceof IChunk)
      _linkageSystem.linkSlotValue(iChunk, (IChunk) oldValue, true);

    if (newValue instanceof IChunk)
      _linkageSystem.linkSlotValue(iChunk, (IChunk) newValue, false);
  }

}
