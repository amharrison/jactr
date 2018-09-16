package org.jactr.core.buffer.delegate;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.production.request.SlotBasedRequest;

/**
 * silently consumes {@link SlotBasedRequest}s but does not forward them to the
 * owning module
 * 
 * @author harrison
 */
public class IgnoreSlotRequestDelegate implements IRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(IgnoreSlotRequestDelegate.class);

  
  public boolean request(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    return (request instanceof SlotBasedRequest);
  }


  public boolean willAccept(IRequest request)
  {
    return request instanceof SlotBasedRequest;
  }

  public void clear()
  {
    //noop
  }
}
