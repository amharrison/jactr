package org.jactr.tools.async.shadow.handlers;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.transform.IMessageTransfromer;
import org.jactr.tools.async.message.BulkMessage;

/**
 * converts bulk messages into individuals
 * 
 * @author harrison
 */
public class BulkMessageTransformer implements IMessageTransfromer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BulkMessageTransformer.class);

  public BulkMessageTransformer()
  {
  }

  @Override
  public Collection<?> messageReceived(Object message)
  {
    if (message instanceof BulkMessage)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Received bulk message, splitting"));
      BulkMessage bm = (BulkMessage) message;
      return bm.getMessages();
    }
    else
      return Collections.singleton(message);
  }

}
