package org.jactr.core.utils.collections;

/*
 * default logging
 */
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;

public class ChunkNameComparator implements Comparator<IChunk>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkNameComparator.class);

  public int compare(IChunk o1, IChunk o2)
  {
    return o1.getSymbolicChunk().getName()
        .compareTo(o2.getSymbolicChunk().getName());
  }

}
