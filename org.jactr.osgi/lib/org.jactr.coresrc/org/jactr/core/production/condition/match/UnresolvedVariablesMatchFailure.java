package org.jactr.core.production.condition.match;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.IUniqueSlotContainer;

public class UnresolvedVariablesMatchFailure extends AbstractMatchFailure
{
  /**
   * Logger definition
   */
  static private final transient Log         LOGGER = LogFactory
                                                        .getLog(UnresolvedVariablesMatchFailure.class);

  private final Collection<IConditionalSlot> _unresolved;

  private final Set<String>                  _possible;

  private IUniqueSlotContainer               _container;

  public UnresolvedVariablesMatchFailure(ICondition condition,
      Collection<IConditionalSlot> unresolved, Set<String> possible,
      IUniqueSlotContainer container)
  {
    super(condition);
    _unresolved = new ArrayList<IConditionalSlot>(unresolved);
    _possible = new TreeSet<String>(possible);
    _container = container;
  }

  public UnresolvedVariablesMatchFailure(
      Collection<IConditionalSlot> unresolved, Set<String> possible,
      IUniqueSlotContainer container)
  {
    this(null, unresolved, possible, container);
  }

  public Collection<IConditionalSlot> getUnresolvedSlots()
  {
    return _unresolved;
  }

  public Set<String> getPossibleVariables()
  {
    return _possible;
  }

  public IUniqueSlotContainer getContainer()
  {
    return _container;
  }

  @Override
  public String toString()
  {
    return String.format("%d unresolved conditions %s. Possible : %s",
        _unresolved.size(), _unresolved, _possible);
  }
}
