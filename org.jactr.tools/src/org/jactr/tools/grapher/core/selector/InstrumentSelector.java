package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InstrumentSelector extends ClassNamedParameterSelector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(InstrumentSelector.class);

  public InstrumentSelector(String regex)
  {
    super(regex);
  }

}
