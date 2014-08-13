package org.jactr.core.module.declarative.associative;

import java.util.Collection;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.link.IAssociativeLink;

/*
 * default logging
 */

/**
 * container for associative links
 * 
 * @author harrison
 */
public interface IAssociativeLinkContainer
{

  /**
   * get all the outbound links
   * 
   * @param container
   */
  public void getOutboundLinks(Collection<IAssociativeLink> container);

  public long getNumberOfOutboundLinks();

  /**
   * all in bound links
   * 
   * @param container
   */
  public void getInboundLinks(Collection<IAssociativeLink> container);

  public long getNumberOfInboundLinks();

  /**
   * return all outbound links that connect to receiver. Normally, there is only
   * one, but this supports multiple.
   * 
   * @param receiver
   * @param container
   */
  public void getOutboundLinks(IChunk receiver,
      Collection<IAssociativeLink> container);

  /**
   * return all the links that we are getting
   * 
   * @param sender
   * @param container
   */
  public void getInboundLinks(IChunk sender,
      Collection<IAssociativeLink> container);

  public void addLink(IAssociativeLink link);

  public void removeLink(IAssociativeLink link);

}
