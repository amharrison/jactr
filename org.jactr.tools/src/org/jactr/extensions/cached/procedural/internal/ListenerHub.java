package org.jactr.extensions.cached.procedural.internal;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.slot.INotifyingSlotContainer;
import org.jactr.extensions.cached.procedural.listeners.BufferListener;
import org.jactr.extensions.cached.procedural.listeners.SlotListener;

/**
 * central point for the listeners used to track the buffers and the chunk
 * contents.
 * 
 * @author harrison
 */
public class ListenerHub
{
  /**
   * Logger definition
   */
  static private final transient Log                       LOGGER = LogFactory
                                                                      .getLog(ListenerHub.class);

  private final Map<String, BufferListener>                _bufferListeners;

  private final Map<INotifyingSlotContainer, SlotListener> _slotListeners;

  private final IModelListener                             _modelListener;

  private final IModel                                     _model;

  private final ReentrantReadWriteLock                     _lock  = new ReentrantReadWriteLock();

  public ListenerHub(IModel model)
  {
    _model = model;
    _modelListener = new ModelListenerAdaptor() {

      @Override
      public void bufferInstalled(ModelEvent me)
      {
        _bufferListeners.put(me.getBuffer().getName().toLowerCase(),
            new BufferListener(me.getBuffer()));
      }
      /*
       * at the end of each cycle, check the slot listeners for any that are
       * unused, and clear (non-Javadoc)
       * @see
       * org.jactr.core.model.event.ModelListenerAdaptor#cycleStopped(org.jactr
       * .core.model.event.ModelEvent)
       */
      @Override
      public void cycleStopped(ModelEvent me)
      {
        checkForEmptyListeners();
      }
    };

    _bufferListeners = new TreeMap<String, BufferListener>();
    _slotListeners = new HashMap<INotifyingSlotContainer, SlotListener>();

    /**
     * buffer listeners are permanent installations
     */
    for (IActivationBuffer buffer : model.getActivationBuffers())
      _bufferListeners.put(buffer.getName().toLowerCase(), new BufferListener(
          buffer));

    _model.addListener(_modelListener, null);
  }

  public void dispose()
  {
    _model.removeListener(_modelListener);

    try
    {
      _lock.writeLock().lock();
      // detach everyone
      for (BufferListener listener : _bufferListeners.values())
        listener.dispose();
      _bufferListeners.clear();

      for (SlotListener listener : _slotListeners.values())
        listener.dispose();
      _slotListeners.clear();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  public BufferListener getBufferListener(String bufferName)
  {
    try
    {
      _lock.readLock().lock();
      return _bufferListeners.get(bufferName.toLowerCase());
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public SlotListener getSlotListener(INotifyingSlotContainer container)
  {
    try
    {
      _lock.writeLock().lock();
      SlotListener sl = _slotListeners.get(container);
      if (sl == null)
      {
        
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("No slot listener registered for %s, doing so now", container));
        
        sl = new SlotListener(container);
        _slotListeners.put(container, sl);
      }
      return sl;
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  /**
   * find any unused slot listeerns and remove.
   */
  protected void checkForEmptyListeners()
  {
    try
    {
      _lock.writeLock().lock();

      Iterator<SlotListener> listeners = _slotListeners.values().iterator();
      while (listeners.hasNext())
      {
        SlotListener listener = listeners.next();
        if (listener.isEmpty())
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format(
                "Slot listener for %s is empty, removing",
                listener.getContainer()));

          // do cleanup outside of lock?
          listener.dispose();
          listeners.remove();
        }
      }
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }
}
