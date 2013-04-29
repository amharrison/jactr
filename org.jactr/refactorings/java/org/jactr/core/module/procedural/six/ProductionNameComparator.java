package org.jactr.core.module.procedural.six;

/*
 * default logging
 */
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.IProduction;

public class ProductionNameComparator implements Comparator<IProduction>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionNameComparator.class);

  public int compare(IProduction o1, IProduction o2)
  {
    return o1.getSymbolicProduction().getName().compareTo(
        o2.getSymbolicProduction().getName());
  }

}
