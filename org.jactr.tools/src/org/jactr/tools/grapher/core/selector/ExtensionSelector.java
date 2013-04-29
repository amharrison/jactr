package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExtensionSelector extends ClassNamedParameterSelector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ExtensionSelector.class);

  public ExtensionSelector(String regex)
  {
    super(regex);
    // TODO Auto-generated constructor stub
  }

}
