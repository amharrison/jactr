package org.jactr.core.module.declarative.search.filter;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;

public class SlotFilter implements IChunkFilter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SlotFilter.class);

  private final IConditionalSlot     _slot;

  public SlotFilter(IConditionalSlot cSlot)
  {
    _slot = cSlot;
  }

  @Override
  public boolean accept(IChunk chunk)
  {
    try
    {
      ISlot chunkSlot = chunk.getSymbolicChunk().getSlot(_slot.getName());
      return _slot.matchesCondition(chunkSlot.getValue());
    }
    catch (Exception e)
    {
      LOGGER.error(e);

      return false;
    }
  }

}
