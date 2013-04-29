package org.jactr.core.module.declarative.associative;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.utils.IInstallable;
import org.jactr.core.utils.parameter.LinkParameterHandler;

/*
 * default logging
 */

/**
 * entry point for all associative link handling in the system. It is preferred
 * that <b>all</b> handling of associations be taken care of through here. This
 * would include chunk level merging of associative links.
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
   * copy all the specified links from source, replacing all occurences of
   * source with destination, and added to destination.
   * 
   * @param copySourceIs
   *          copy those links where source is iChunk (i.e., these links spread
   *          to the source chunk)
   * @param copySourceJs
   *          copy those links where source is jChunk (i.e., these links spread
   *          to the source chunk)
   * @param source
   * @param destination
   */
  public void copyAndRemapLinks(IChunk source, IChunk destination,
      boolean copySourceIs, boolean copySourceJs);

  public IAssociativeLinkEquation getAssociativeLinkEquation();
  
  public LinkParameterHandler getParameterHandler();

  /**
   * this chunk will be disposed, clean up its links correctly.
   * 
   * @param chunk
   */
  public void chunkWillBeDisposed(IChunk chunk);
}
