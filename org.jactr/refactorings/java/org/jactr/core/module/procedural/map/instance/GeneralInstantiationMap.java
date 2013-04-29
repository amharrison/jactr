package org.jactr.core.module.procedural.map.instance;

/*
 * default logging
 */
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.procedural.map.template.GeneralInstantiationMapTemplate;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;

public class GeneralInstantiationMap extends AbstractInstantiationMap<Object>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(GeneralInstantiationMap.class);

  /**
   * will be {@link IChunkListener} if root is chunk, or
   * {@link IActivationBufferListener} if it is a buffer (for status)
   */
  private Object                     _listener;

  /**
   * productions that make reference to status slots that aren't immediately
   * obvious (IStatusBuffer.getSlots() doesn't contain the names) - this is fine
   * as some status buffers might be scoped (i.e. motor)
   */
  final private Set<IProduction>              _unknownStatusSlotProductions;
  final private Map<String, Set<IProduction>> _productions;

  public GeneralInstantiationMap(Object root, GeneralInstantiationMapTemplate template,
      IInstaniationMap parent)
  {
    super(root, template, parent);
    _unknownStatusSlotProductions = new TreeSet<IProduction>();
    _productions = template.getRETEMapping();
  }
  
  private Set<IProduction> getProductions(String slotName,
      boolean createIfMissing)
  {
    slotName = slotName.toLowerCase();
    synchronized (_productions)
    {
      Set<IProduction> prods = _productions.get(slotName);
      if (prods == null && createIfMissing)
      {
        prods = new TreeSet<IProduction>();
        _productions.put(slotName, prods);
      }
      return prods;
    }
  }
  
  private void remove(String slotName)
  {
    slotName = slotName.toLowerCase();
    synchronized (_productions)
    {
      _productions.remove(slotName);
    }
  }

  public boolean add(IProduction production, ICondition condition)
  {
    if (!(condition instanceof ISlotContainer)) return false;

    FastList<ISlot> container = FastList.newInstance();
    boolean added = false;
    for (ISlot slot : ((ISlotContainer) condition).getSlots(container))
    {
      String name = slot.getName().toLowerCase();
      Set<IProduction> productions = getProductions(name, true);

      productions.add(production);
      added = true;
    }

    FastList.recycle(container);

    return added;
  }

  public void remove(IProduction production, ICondition condition)
  {
    if (!(condition instanceof ISlotContainer)) return;

    FastList<ISlot> container = FastList.newInstance();
    for (ISlot slot : ((ISlotContainer) condition).getSlots(container))
    {
      String name = slot.getName().toLowerCase();
      Set<IProduction> productions = getProductions(name, false);
      if (productions != null)
      {
        productions.remove(production);
        if (productions.size() == 0) remove(name);
      }
    }

    FastList.recycle(container);
  }

  public boolean add(IProduction production)
  {
    throw new UnsupportedOperationException(
        "use add(IProduction, ICondition) instead");
  }

  public void remove(IProduction production)
  {
    throw new UnsupportedOperationException(
        "use remove(IProduction, ICondition) instead");
  }

  
  public int getSize()
  {
    synchronized (_productions)
    {
      return _productions.size();
    }
  }

  

  @Override
  public void activate()
  {
    super.activate();
    Object root = getRoot();
    if (root instanceof IChunk)
    {
      IChunkListener listener = new ChunkListenerAdaptor(){

        @Override
        public void slotChanged(ChunkEvent event)
        {
          String slotName = event.getSlotName().toLowerCase();
          Set<IProduction> productions = getProductions(slotName, false);
          if(productions!=null)
            dirty(productions);
        }
      };
      
      _listener = listener;
      ((IChunk)root).addListener(listener, ExecutorServices.INLINE_EXECUTOR);
    }
    else if (root instanceof IStatusBuffer)
    {
      IActivationBufferListener listener = new ActivationBufferListenerAdaptor(){

   
        @Override
        public void statusSlotChanged(ActivationBufferEvent abe)
        {
          String slotName = abe.getSlotName().toLowerCase();
          Set<IProduction> productions = getProductions(slotName, false);
          if(productions!=null)
            dirty(productions);
        }
        
      };
      

      _listener = listener;
      ((IActivationBuffer)root).addListener(listener, ExecutorServices.INLINE_EXECUTOR);
    }
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to activate instance for " + root);
  }

  @Override
  public void deactivate()
  {
    super.deactivate();
    Object root = getRoot();
    if (root instanceof IChunk)
      ((IChunk)root).removeListener((IChunkListener)_listener);
    else if (root instanceof IStatusBuffer)
      ((IActivationBuffer)root).removeListener((IActivationBufferListener)_listener);
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to deactivate instance for " + root);

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
