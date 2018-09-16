package org.jactr.tools.async.iterative.message;

/*
 * default logging
 */
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.async.message.BaseMessage;

public class DeadLockMessage extends BaseMessage implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -5181260550772423344L;
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DeadLockMessage.class);

}
