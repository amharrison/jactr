package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.parameter.IParameterized;

public class ClassNamedParameterSelector extends
    AbstractNameSelector<IParameterized>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ClassNamedParameterSelector.class);

  public ClassNamedParameterSelector(String regex)
  {
    super(regex);
  }

  @Override
  protected String getName(IParameterized element)
  {
    return element.getClass().getName();
  }

  public void add(ISelector selector)
  {

  }

}
