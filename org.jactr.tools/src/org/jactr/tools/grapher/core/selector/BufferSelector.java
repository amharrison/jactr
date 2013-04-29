package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BufferSelector extends ClassNamedParameterSelector
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BufferSelector.class);

  public BufferSelector(String regex)
  {
    super(regex);
  }
}
