package org.jactr.modules.pm.visual.memory.impl.filter;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.common.memory.filter.AbstractIndexFilter;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;
import org.jactr.modules.pm.visual.IVisualModule;

public class ValueVisualLocationFilter extends
    AbstractIndexFilter<Object>
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER = LogFactory
                                                  .getLog(ValueVisualLocationFilter.class);

  private Collection<IConditionalSlot> _conditionals;

  public ValueVisualLocationFilter()
  {
    _conditionals = Collections.EMPTY_LIST;
  }
  
  protected ValueVisualLocationFilter(Collection<IConditionalSlot> conditionals)
  {
    _conditionals = conditionals;
  }

  @Override
  protected Object compute(ChunkTypeRequest request)
  {
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equals(IVisualModule.VALUE_SLOT)
          && cSlot.getCondition() == IConditionalSlot.EQUALS)
        return cSlot.getValue();

    return null;
  }

  public boolean accept(ChunkTypeRequest template)
  {
    Object value = get(template);

    for (IConditionalSlot cSlot : _conditionals)
      if (!cSlot.matchesCondition(value)) return false;

    return true;
  }

  public Comparator<ChunkTypeRequest> getComparator()
  {
    return null;
  }

  public IIndexFilter instantiate(ChunkTypeRequest request)
  {
    List<IConditionalSlot> conditionals = FastListFactory.newInstance();

    int index = 0;
    for (IConditionalSlot cSlot : request.getConditionalSlots())
    {
      if (conditionals.size() == 0) index++;

      if (cSlot.getName().equals(IVisualModule.VALUE_SLOT))
        conditionals.add(cSlot);
    }

    if (conditionals.size() == 0)
    {
      FastListFactory.recycle(conditionals);
      return null;
    }

    ValueVisualLocationFilter rtn = new ValueVisualLocationFilter(conditionals);
    rtn.setWeight(index);
    rtn.setPerceptualMemory(getPerceptualMemory());

    return rtn;
  }

  @Override
  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub
    
  }

}
