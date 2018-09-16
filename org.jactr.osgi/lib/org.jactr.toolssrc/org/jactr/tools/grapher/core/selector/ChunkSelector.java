package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.event.IParameterListener;
import org.jactr.core.module.declarative.associative.IAssociativeLinkContainer;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.grapher.core.container.IProbeContainer;

public class ChunkSelector extends AbstractNameSelector<IChunk>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkSelector.class);

  private Collection<LinkSelector>   _linkSelectors;

  private IParameterListener         _pListener;

  private IChunkListener             _cListener;

  private Set<IAssociativeLink>      _installed;

  public ChunkSelector(String regex)
  {
    super(regex);
    _installed = new HashSet<IAssociativeLink>();
    _linkSelectors = new ArrayList<LinkSelector>();
    _pListener = new IParameterListener() {

      public void parameterChanged(IParameterEvent pe)
      {
        if (((ISubsymbolicChunk) pe.getSource()).getParentChunk().isEncoded())
          if (pe.getParameterName().equalsIgnoreCase(ISubsymbolicChunk4.LINKS))
            checkLinks(((ISubsymbolicChunk) pe.getSource()).getParentChunk());
      }

    };

    _cListener = new ChunkListenerAdaptor() {
      @Override
      public void chunkEncoded(ChunkEvent event)
      {
        checkLinks(event.getSource());
      }

    };

  }

  @Override
  protected String getName(IChunk element)
  {
    return element.getSymbolicChunk().getName();
  }

  public void add(ISelector selector)
  {
    if (selector instanceof LinkSelector)
      _linkSelectors.add((LinkSelector) selector);
  }

  @Override
  public IProbeContainer install(IChunk element, IProbeContainer container)
  {
    IProbeContainer rtnContainer = super.install(element, container);
    Executor executor = ExecutorServices.INLINE_EXECUTOR;
    element.addListener(_pListener, executor);
    element.addListener(_cListener, executor);

    if (element.isEncoded()) checkLinks(element);

    return rtnContainer;
  }

  private void checkLinks(IChunk chunk)
  {
    List<IAssociativeLink> links = FastListFactory.newInstance();

    IAssociativeLinkContainer alc = chunk
        .getAdapter(IAssociativeLinkContainer.class);
    alc.getOutboundLinks(links);

    for (IAssociativeLink link : links)
      checkLink(link, getProbeContainer(chunk));

    FastListFactory.recycle(links);
  }

  /**
   * this will be called anytime new links are added or removed. Which could
   * result in duplicate installs, so we track who we've installed already
   * 
   * @param link
   * @param container
   */
  private void checkLink(IAssociativeLink link, IProbeContainer container)
  {
    for (LinkSelector selector : _linkSelectors)
      if (selector.matches(link) && !_installed.contains(link))
      {
        selector.install(link, container);
        _installed.add(link);
      }
  }

}
