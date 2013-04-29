package org.jactr.core.module.procedural.map.instance;

/*
 * default logging
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.procedural.map.template.BufferInstantiationMapTemplate;
import org.jactr.core.module.procedural.map.template.GeneralInstantiationMapTemplate;
import org.jactr.core.production.IProduction;

public class BufferInstantiationMap extends AbstractInstantiationMap<IActivationBuffer>
{

  /**
   * Logger definition
   */
  static private final transient Log       LOGGER           = LogFactory
                                                                .getLog(BufferInstantiationMap.class);

  static private IChunk                    QUERY_KEY        = null;

  private Map<IChunk, GeneralInstantiationMap> _contentInstances;

  private IActivationBufferListener        _listener;

  private volatile boolean                 _contentsChanged = true;

  protected BufferInstantiationMap(IActivationBuffer root,
      BufferInstantiationMapTemplate template, IInstaniationMap parent)
  {
    super(root, template, parent);
    _contentInstances = new HashMap<IChunk, GeneralInstantiationMap>();

    if (root instanceof IStatusBuffer)
      _contentInstances.put(QUERY_KEY, (GeneralInstantiationMap) template
          .getContentTemplate(BufferInstantiationMapTemplate.QUERY_KEY).instantiate(root,
              this));

    _listener = new ActivationBufferListenerAdaptor() {
      @Override
      public void sourceChunkAdded(ActivationBufferEvent event)
      {
        changed();
      }

      @Override
      public void sourceChunkRemoved(ActivationBufferEvent event)
      {
        changed();
      }

      @Override
      public void sourceChunksCleared(ActivationBufferEvent event)
      {
        changed();
      }
    };
  }

  protected void changed()
  {
    _contentsChanged = true;
  }

  protected boolean contentsHaveChanged()
  {
    return _contentsChanged;
  }

  protected void processContents(IActivationBuffer buffer)
  {
    if(!contentsHaveChanged()) return;
    
    _contentsChanged = false;
    
    BufferInstantiationMapTemplate template = (BufferInstantiationMapTemplate) getTemplate();
    Collection<IChunk> sourceChunks = buffer.getSourceChunks();

    /*
     * check for removals
     */
    Set<IChunk> keySet = null;
    synchronized (_contentInstances)
    {
      keySet = new HashSet<IChunk>(_contentInstances.keySet());
    }
    keySet.remove(QUERY_KEY);
    for (IChunk currentKey : keySet)
      if (!sourceChunks.contains(currentKey))
      {
        /*
         * we need to remove it and possibly detach
         */
        GeneralInstantiationMap instance = null;
        synchronized (_contentInstances)
        {
          instance = _contentInstances.remove(currentKey);
        }

        // if (!isActivated() && instance.isActivated()) instance.deactivate();
        if (isActivated() && instance.isActivated()) instance.deactivate();
      }

    /*
     * now we add..
     */
    for (IChunk chunk : sourceChunks)
      if (!keySet.contains(chunk))
      {
        IChunkType chunkType = chunk.getSymbolicChunk().getChunkType();
        GeneralInstantiationMapTemplate gTemplate = null;
        // this chunktype might be a derivative of the one
        // that we have a template for..
        while ((gTemplate = template.getContentTemplate(chunkType)) == null
            && chunkType != null)
          chunkType = chunkType.getSymbolicChunkType().getParent();

        GeneralInstantiationMap instance = null;
        if (gTemplate != null)
          instance = (GeneralInstantiationMap) gTemplate.instantiate(chunk, this);

        if (instance != null)
        {
          synchronized (_contentInstances)
          {
            _contentInstances.put(chunk, instance);
          }
          if (isActivated()) instance.activate();
        }
        else if (LOGGER.isWarnEnabled())
          LOGGER.warn("No GeneralRTETemplate found for "
              + chunk.getSymbolicChunk().getChunkType());
      }
    
  }

  public boolean add(IProduction production)
  {

    return false;
  }

  public int getSize()
  {

    return 0;
  }

  public void remove(IProduction production)
  {

  }

  @Override
  public void activate()
  {
    super.activate();
    getRoot().addListener(_listener, ExecutorServices.INLINE_EXECUTOR);
  }

  @Override
  public void deactivate()
  {
    super.deactivate();
    getRoot().removeListener(_listener);
  }

  public Set<IProduction> getFailedProductions()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Set<IProduction> getTestableProductions()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
