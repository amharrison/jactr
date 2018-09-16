package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.IProduction;

public class ProductionSelector extends AbstractNameSelector<IProduction>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionSelector.class);
  
  
  public ProductionSelector(String regex)
  {
    super(regex);
  }

  @Override
  protected String getName(IProduction element)
  {
    return element.getSymbolicProduction().getName();
  }


  public void add(ISelector selector)
  {

  }

}
