package org.jactr.extensions.cached.procedural.invalidators;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.IProduction;
import org.jactr.extensions.cached.procedural.internal.InstantiationCache;
import org.jactr.extensions.cached.procedural.internal.ListenerHub;

/**
 * an invalidator that fires on any content change to a buffer.
 * @author harrison
 *
 */
public class BufferInvalidator extends AbstractInvalidator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BufferInvalidator.class);
  
  private final String _bufferName;
  
  public BufferInvalidator(InstantiationCache cache, IProduction production, String bufferName)
  {
    super(cache,production);
    _bufferName = bufferName.toLowerCase();
  }

  public void register(ListenerHub hub)
  {
    hub.getBufferListener(_bufferName).register(this);
  }

  public void unregister(ListenerHub hub)
  {
    hub.getBufferListener(_bufferName).unregister(this);
  }

}
