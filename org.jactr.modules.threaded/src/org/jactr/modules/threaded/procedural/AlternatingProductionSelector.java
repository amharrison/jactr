package org.jactr.modules.threaded.procedural;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.procedural.six.DefaultProductionSelector;
import org.jactr.core.production.IInstantiation;

/**
 * production selector for threaded cognition that strictly alternates between
 * different goals, whenever possible
 * 
 * @author harrison
 */
public class AlternatingProductionSelector extends DefaultProductionSelector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AlternatingProductionSelector.class);

  private IChunk                     _previousGoal;

  /**
   * we select the first instantiation if 1) there was no previous goal or 2)
   * there is no production that doesn't match the previousGoal. Otherwise, we
   * select the first instantiation that matches to something other than
   * previous goal
   */
  @Override
  public IInstantiation select(Collection<IInstantiation> instantiations)
  {
    IInstantiation instantiation = selectInstantiation(instantiations);
    _previousGoal = getGoal(instantiation);
    return instantiation;
  }

  protected IChunk getGoal(IInstantiation instantiation)
  {
    if (instantiation == null) return null;
    return (IChunk) instantiation.getVariableBindings().get("=goal");
  }

  protected IInstantiation selectInstantiation(
      Collection<IInstantiation> instantiations)
  {
    if (instantiations.size() == 0) return null;

    if (_previousGoal == null) return instantiations.iterator().next();

    /*
     * iterate through, looking for the first none goal related..
     */
    for (IInstantiation instantiation : instantiations)
    {
      IChunk goal = getGoal(instantiation);
      if (!_previousGoal.equals(goal)) return instantiation;
    }

    /*
     * none selected? default to first
     */
    return instantiations.iterator().next();
  }
}
