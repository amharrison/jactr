package org.jactr.core.module.declarative.associative;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.utils.IInstallable;
import org.jactr.core.utils.parameter.LinkParameterHandler;
import org.jactr.core.utils.parameter.LinkParameterProcessor;

/*
 * default logging
 */

/**
 * entry point for all associative link handling in the system. It is preferred
 * that <b>all</b> handling of associations be taken care of through here. This
 * would include chunk level merging of associative links. This is managed by
 * the {@link IDeclarativeModule}, via
 * {@link IDeclarativeModule#setAssociativeLinkageSystem(IAssociativeLinkageSystem)}
 * . However, the linkage system must be set before the declarative module
 * builds any chunks, typically via
 * {@link IModule#install(org.jactr.core.model.IModel)}. To call from
 * {@link IModule#initialize()} would be after the chunks have been created.
 */
public interface IAssociativeLinkageSystem extends IInstallable
{

  
  /**
   * create a new associative link spreading activation from j to i
   * @param iChunk
   * @param jChunk
   * @return
   */
  public IAssociativeLink createLink(IChunk iChunk, IChunk jChunk);
  
  
  /**
   * copy all the specified links from source, replacing all occurrences of
   * source with destination, and added to destination.
   * 
   * @param source
   * @param destination
   */
  public void copyAndRemapLinks(IChunk source, IChunk destination,
      boolean copyInboundLinks, boolean copyOutboundLinks);

  public IAssociativeLinkEquation getAssociativeLinkEquation();

  /**
   * @deprecated use {@link #getParameterProcessor(IChunk)} instead
   * @return
   */
  @Deprecated
  public LinkParameterHandler getParameterHandler();


  public LinkParameterProcessor getParameterProcessor(IChunk sourceChunk);

  /**
   * this chunk will be disposed, clean up its links correctly.
   * 
   * @param chunk
   */
  public void chunkWillBeDisposed(IChunk chunk);

  /**
   * add the link to the appropriate containers within the chunks. This is the
   * only "right" way to add a link to a chunk.
   * 
   * @param link
   */
  public void addLink(IAssociativeLink link);

  /**
   * remove the link from the appropriate contains within the chunk. This is the
   * only "right" way to remove a link to a chunk.
   * 
   * @param link
   */
  public void removeLink(IAssociativeLink link);
}
