package org.jactr.modules.threaded.procedural;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.six.DefaultProductionInstantiator;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.VariableBindings;

/**
 * removes candidate bindings that include that same chunk in goal and
 * other-goal
 * 
 * @author harrison
 */
public class CullingProductionInstantiator extends
    DefaultProductionInstantiator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(CullingProductionInstantiator.class);

  @Override
  public Collection<IInstantiation> instantiate(IProduction production,
      Collection<VariableBindings> provisionalBindings)
      throws CannotInstantiateException
  {
    Iterator<VariableBindings> itr = provisionalBindings.iterator();
    while (itr.hasNext())
    {
      VariableBindings bindings = itr.next();
      if (bindings.isBound("=goal") && bindings.isBound("=other-goal")
          && bindings.get("=goal") == bindings.get("=other-goal"))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "Rejecting instantiation of %s with goal/other-goal match (%s)",
              production, bindings.get("=goal")));
        itr.remove();
      }
    }

    if (provisionalBindings.size() == 0)
      throw new CannotInstantiateException(
          "Only self-referential bindings found (=goal/=other-goal). Cannot instantiate.");

    return super.instantiate(production, provisionalBindings);
  }
}
