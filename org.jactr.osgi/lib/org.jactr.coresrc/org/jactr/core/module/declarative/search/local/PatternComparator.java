package org.jactr.core.module.declarative.search.local;

/*
 * default logging
 */
import java.util.Comparator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.slot.ISlot;

public class PatternComparator implements Comparator<ISlot>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(PatternComparator.class);

  private final Map<ISlot, Long>     _sizeMap;

  public PatternComparator(Map<ISlot, Long> sizeMap)
  {
    _sizeMap = sizeMap;
  }

  public int compare(ISlot arg0, ISlot arg1)
  {
    Long size0 = _sizeMap.get(arg0);
    Long size1 = _sizeMap.get(arg1);

    return size0.compareTo(size1);
  }

}
