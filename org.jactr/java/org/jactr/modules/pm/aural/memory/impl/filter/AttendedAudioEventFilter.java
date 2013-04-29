package org.jactr.modules.pm.aural.memory.impl.filter;

/*
 * default logging
 */
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.filter.AbstractIndexFilter;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;

public class AttendedAudioEventFilter extends
    AbstractIndexFilter<Boolean>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AttendedAudioEventFilter.class);

  @Override
  protected Boolean compute(ChunkTypeRequest request)
  {
    return null;
  }

  /**
   * if we've gotten this far, then always accept
   * @param template
   * @return
   * @see org.jactr.modules.pm.common.memory.filter.IIndexFilter#accept(org.jactr.core.production.request.ChunkTypeRequest)
   */
  public boolean accept(ChunkTypeRequest template)
  {
    return true;
  }

  /**
   * no comparator is used (how would you sort new, old, attended ?)
   * @return
   * @see org.jactr.modules.pm.common.memory.filter.IIndexFilter#getComparator()
   */
  public Comparator<ChunkTypeRequest> getComparator()
  {
    return null;
  }

  public IIndexFilter instantiate(ChunkTypeRequest request)
  {
    /*
     * no need for an instantiated copy
     */
    return this;
  }

  @Override
  public void normalizeRequest(ChunkTypeRequest request)
  {
    /**
     * +visual-location> isa visual-location
     *  :attended null is equivalent to :attended != true
     */
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equals(IAuralModule.ATTENDED_STATUS_SLOT)
          && cSlot.getCondition() == IConditionalSlot.EQUALS
          && cSlot.getValue() == null)
      {
        cSlot.setValue(Boolean.TRUE);
        cSlot.setCondition(IConditionalSlot.NOT_EQUALS);
      }
  }
}
