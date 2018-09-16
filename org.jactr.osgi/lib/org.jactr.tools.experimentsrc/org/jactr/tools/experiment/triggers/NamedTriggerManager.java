package org.jactr.tools.experiment.triggers;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.experiment.impl.IVariableContext;

public class NamedTriggerManager
{
  /**
   * Logger definition
   */
  static private final transient Log            LOGGER    = LogFactory
                                                              .getLog(NamedTriggerManager.class);

  private Map<String, Collection<NamedTrigger>> _triggers = new TreeMap<String, Collection<NamedTrigger>>();

  synchronized public void add(NamedTrigger trigger)
  {
    String name = trigger.getName().toLowerCase();
    Collection<NamedTrigger> triggers = _triggers.get(name);
    if (triggers == null)
    {
      triggers = new ArrayList<NamedTrigger>();
      _triggers.put(name, triggers);
    }
    triggers.add(trigger);
  }

  synchronized public void remove(NamedTrigger trigger)
  {
    String name = trigger.getName().toLowerCase();
    Collection<NamedTrigger> triggers = _triggers.get(name);
    if (triggers != null)
    {
      triggers.remove(trigger);
      if (triggers.size() == 0) _triggers.remove(name);
    }
  }

  public boolean fire(String triggerName, IVariableContext context)
  {
    triggerName = triggerName.toLowerCase();
    List<NamedTrigger> triggers = FastListFactory.newInstance();

    synchronized (this)
    {
      if (_triggers.containsKey(triggerName))
        triggers.addAll(_triggers.get(triggerName));
    }

    try
    {
      if (triggers == null) return false;

      boolean anyFired = false;
      for (NamedTrigger trigger : triggers)
        {
         trigger.fire(context);
         anyFired = true;
        }

      return anyFired;
    }
    finally
    {
      FastListFactory.recycle(triggers);
    }
  }

}
