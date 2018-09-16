package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ModuleSelector extends ClassNamedParameterSelector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModuleSelector.class);

  public ModuleSelector(String regex)
  {
    super(regex);
    // TODO Auto-generated constructor stub
  }

}
