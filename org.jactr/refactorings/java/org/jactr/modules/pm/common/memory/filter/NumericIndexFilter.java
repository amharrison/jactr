package org.jactr.modules.pm.common.memory.filter;

/*
 * default logging
 */
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;

public class NumericIndexFilter extends AbstractIndexFilter<Double>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NumericIndexFilter.class);

  private final String               _slotName;

  private final boolean              _sortAscending;

  public NumericIndexFilter(String slotName, boolean ascending)
  {
    _slotName = slotName;
    _sortAscending = ascending;
  }

  @Override
  protected Double compute(ChunkTypeRequest request)
  {
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getCondition() == IConditionalSlot.EQUALS
          && _slotName.equals(cSlot.getName()))
      {
        Object value = cSlot.getValue();
        if (value instanceof Number) return ((Number) value).doubleValue();
      }

    return null;
  }

  public boolean accept(ChunkTypeRequest template)
  {
    Double value = get(template);

    return value != null;
  }

  public Comparator<ChunkTypeRequest> getComparator()
  {
    return new Comparator<ChunkTypeRequest>() {
      public int compare(ChunkTypeRequest o1, ChunkTypeRequest o2)
      {
        if (o1 == o2) return 0;

        Double v1 = get(o1);
        Double v2 = get(o2);

        int rtn = 0;

        if (v1 < v2) rtn = -1;
        if (v1 > v2) rtn = 1;

        if (!_sortAscending) rtn *= -1;

        return rtn;
      }
    };
  }

  public IIndexFilter instantiate(ChunkTypeRequest request)
  {
    int weight = -1;
    int count = 0;
    for (IConditionalSlot cSlot : request.getConditionalSlots())
    {
      ++count;
      if (_slotName.equals(cSlot.getName()))
      {
        weight = count;
        break;
      }
    }

    if (weight == -1) return null;

    NumericIndexFilter instance = new NumericIndexFilter(_slotName,
        _sortAscending);
    instance.setWeight(weight);
    instance.setPerceptualMemory(getPerceptualMemory());

    return instance;
  }

}
