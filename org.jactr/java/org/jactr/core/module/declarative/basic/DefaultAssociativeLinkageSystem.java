package org.jactr.core.module.declarative.basic;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.DefaultAssociativeLinkEquation;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.associative.IAssociativeLinkContainer;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.four.learning.DeclarativeModuleListener;
import org.jactr.core.utils.parameter.ACTRParameterProcessor;
import org.jactr.core.utils.parameter.LinkParameterHandler;
import org.jactr.core.utils.parameter.LinkParameterProcessor;

/**
 * creates {@link Link4} links, but does not install any code to
 * add/remove/learn the links
 * 
 * @author harrison
 */
public class DefaultAssociativeLinkageSystem implements
    IAssociativeLinkageSystem
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultAssociativeLinkageSystem.class);

  private IAssociativeLinkEquation   _equation;

  public DefaultAssociativeLinkageSystem()
  {
    setAssociativeLinkEquation(new DefaultAssociativeLinkEquation());
  }

  /**
   * creates the default link. If you want to use a different type of Link,
   * override this first.
   */
  public IAssociativeLink createLink(IChunk iChunk, IChunk jChunk)
  {
    Link4 link = new Link4(jChunk, iChunk);
    link.setAssociativeLinkEquation(_equation);
    return link;
  }

  public IAssociativeLinkEquation getAssociativeLinkEquation()
  {
    return _equation;
  }

  public void setAssociativeLinkEquation(IAssociativeLinkEquation equation)
  {
    _equation = equation;
  }

  public void install(IModel model)
  {

  }

  public void uninstall(IModel model)
  {

  }

  /**
   * associative linkers will often require chunk listeners to perform their
   * job. This null interface is provided so that hooks can be more generally
   * written to attach the linker's listener to chunks that are created. See
   * {@link DeclarativeModuleListener}
   * 
   * @return
   */
  public IChunkListener getChunkListener()
  {
    return null;
  }

  public LinkParameterHandler getParameterHandler()
  {
    return new LinkParameterHandler();
  }

  public LinkParameterProcessor getParameterProcessor(final IChunk sourceChunk)
  {
    return new LinkParameterProcessor(ISubsymbolicChunk4.LINKS, l -> {
      addLink(l);
    },
        () -> {
          IAssociativeLinkContainer container = sourceChunk
              .getAdapter(IAssociativeLinkContainer.class);
          Collection<IAssociativeLink> lC = new ArrayList<IAssociativeLink>();
          container.getOutboundLinks(sourceChunk, lC);
          if (lC.size() > 0)
            return lC.iterator().next();
          else
            return null;
        }, new ACTRParameterProcessor("bsName", null, null,
            sourceChunk.getModel()), sourceChunk);
  }

  /**
   * here we clean up the associative links. This is called during disposal of a
   * chunk, which can occur for temporary (during runtime), as well as encoded
   * chunks (usually after runtime)
   */
  public void chunkWillBeDisposed(IChunk chunk)
  {
    IAssociativeLinkContainer container = chunk
        .getAdapter(IAssociativeLinkContainer.class);

    // remove the associative links.
    FastList<IAssociativeLink> links = FastList.newInstance();
    // where chunk is J
    container.getOutboundLinks(links);

    for (IAssociativeLink link : links)
      detachLink(link);

    // where chunk is I
    links.clear();
    container.getInboundLinks(links);

    for (IAssociativeLink link : links)
      detachLink(link);

    FastList.recycle(links);

  }

  protected void detachLink(IAssociativeLink link)
  {
    IChunk iChunk = link.getIChunk();
    IChunk jChunk = link.getJChunk();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Detaching link %s", link));


    if (!iChunk.hasBeenDisposed())
      iChunk.getAdapter(IAssociativeLinkContainer.class).removeLink(link);
    ;


    if (!jChunk.hasBeenDisposed())
      jChunk.getAdapter(IAssociativeLinkContainer.class).removeLink(link);
    ;
  }

  /*
   * copy links from source, remapping all instances of source to destination,
   * then add the links to destination (and associated chunks) (non-Javadoc)
   * @see
   * org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem
   * #copyAndRemapLinks(org.jactr.core.chunk.IChunk,
   * org.jactr.core.chunk.IChunk)
   */
  public void copyAndRemapLinks(IChunk source, IChunk destination,
      boolean copySourceIs, boolean copySourceJs)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Copying and remapping links from %s to %s",
          source, destination));

    // zip through all of sources links

    IAssociativeLinkContainer srcCont = source.getAdapter(IAssociativeLinkContainer.class);
    IAssociativeLinkContainer destCont = destination.getAdapter(IAssociativeLinkContainer.class);

    if (srcCont != null && destCont != null)
    {
      // remove the associative links.
      FastList<IAssociativeLink> links = FastList.newInstance();
      // j links, that is these links spread activation from source
      if (copySourceJs)
      {
        srcCont.getOutboundLinks(links);

        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String
                  .format(
                      "Copying and remapping %d j Links (those that spread activation from destination)",
                      links.size()));

        for (IAssociativeLink link : links)
          remapAndInstall(source, destination, link);
      }

      if (copySourceIs)
      {
        links.clear();

        // i links, that is, these links spread activation to source
        srcCont.getInboundLinks(links);

        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String
                  .format(
                      "Copying and remapping %d i Links (those that spread activation into destination)",
                      links.size()));

        for (IAssociativeLink link : links)
          remapAndInstall(source, destination, link);
      }

      FastList.recycle(links);
    }
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn(String
          .format("Both source and destination must be ISubsymbolicChunk4"));
  }

  protected void remapAndInstall(IChunk source, IChunk dest,
      IAssociativeLink link)
  {
    // source can be i,j or both (for self-links)
    boolean sourceIsI = link.getIChunk().equals(source);
    boolean sourceIsJ = link.getJChunk().equals(source);

    Link4 newLink = (Link4) createLink(sourceIsI ? dest : link.getIChunk(),
        sourceIsJ ? dest : link.getJChunk());
    Link4 oldLink = (Link4) link;

    newLink.setCount(oldLink.getCount());
    newLink.setFNICJ(oldLink.getFNICJ());
    newLink.setRStrength(oldLink.getRStrength());
    newLink.setStrength(oldLink.getStrength());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Remapped link from %s to %s, adding",
          oldLink, newLink));

    // now add to both dest and the other side of the link
    dest.getAdapter(IAssociativeLinkContainer.class).addLink(newLink);

    if (sourceIsI && sourceIsJ)
      return;
    else if (sourceIsI)
      oldLink.getJChunk().getAdapter(IAssociativeLinkContainer.class)
          .addLink(newLink);
    else if (sourceIsJ)
      oldLink.getIChunk().getAdapter(IAssociativeLinkContainer.class)
          .addLink(newLink);

  }

  public void addLink(IAssociativeLink link)
  {
    IChunk sourceJ = link.getJChunk();
    IChunk targetI = link.getIChunk();

    /**
     * add the link to both, unless they are the same
     */
    sourceJ.getAdapter(IAssociativeLinkContainer.class).addLink(link);

    if (sourceJ != targetI)
      targetI.getAdapter(IAssociativeLinkContainer.class).addLink(link);
  }

  public void removeLink(IAssociativeLink link)
  {
    IChunk sourceJ = link.getJChunk();
    IChunk targetI = link.getIChunk();

    sourceJ.getAdapter(IAssociativeLinkContainer.class).removeLink(link);
    targetI.getAdapter(IAssociativeLinkContainer.class).removeLink(link);
  }

}
