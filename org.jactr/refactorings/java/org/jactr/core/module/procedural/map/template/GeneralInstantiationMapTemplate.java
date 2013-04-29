package org.jactr.core.module.procedural.map.template;

/*
 * default logging
 */
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.map.instance.GeneralInstantiationMap;
import org.jactr.core.module.procedural.map.instance.IInstaniationMap;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;

public class GeneralInstantiationMapTemplate implements IInstantiationMapTemplate<Object>
{
  /**
   * Logger definition
   */
  static private final transient Log          LOGGER = LogFactory
                                                         .getLog(GeneralInstantiationMapTemplate.class);

  final private Object                        _key;

  final private Map<String, Set<IProduction>> _productions;

  public GeneralInstantiationMapTemplate(Object key)
  {
    _key = key;
    _productions = new TreeMap<String, Set<IProduction>>();
  }

  public Object getRoot()
  {
    return _key;
  }

  public Set<IProduction> get(Set<IProduction> container)
  {
    if (container == null) container = new HashSet<IProduction>();
    synchronized (_productions)
    {
      for (Set<IProduction> prods : _productions.values())
        container.addAll(prods);
    }
    return container;
  }

  /**
   * returns a mutable copy of the slotName to production mappings
   * 
   * @return
   */
  public Map<String, Set<IProduction>> getRETEMapping()
  {
    Map<String, Set<IProduction>> rtn = new TreeMap<String, Set<IProduction>>();
    synchronized (_productions)
    {
      for (String slotName : _productions.keySet())
        rtn.put(slotName, new TreeSet<IProduction>(_productions.get(slotName)));
    }
    return rtn;
  }

  private Set<IProduction> getProductions(String slotName,
      boolean createIfMissing)
  {
    slotName = slotName.toLowerCase();
    synchronized (_productions)
    {
      Set<IProduction> prods = _productions.get(slotName);
      if (prods == null && createIfMissing)
      {
        prods = new TreeSet<IProduction>();
        _productions.put(slotName, prods);
      }
      return prods;
    }
  }

  private void remove(String slotName)
  {
    slotName = slotName.toLowerCase();
    synchronized (_productions)
    {
      _productions.remove(slotName);
    }
  }

  public boolean add(IProduction production, ICondition condition)
  {
    if (!(condition instanceof ISlotContainer)) return false;

    FastList<ISlot> container = FastList.newInstance();
    boolean added = false;
    for (ISlot slot : ((ISlotContainer) condition).getSlots(container))
    {
      String name = slot.getName();
      Set<IProduction> productions = getProductions(name, true);

      productions.add(production);
      added = true;
    }

    FastList.recycle(container);

    return added;
  }

  public void remove(IProduction production, ICondition condition)
  {
    if (!(condition instanceof ISlotContainer)) return;

    FastList<ISlot> container = FastList.newInstance();
    for (ISlot slot : ((ISlotContainer) condition).getSlots(container))
    {
      String name = slot.getName();
      Set<IProduction> productions = getProductions(name, false);
      if (productions != null)
      {
        productions.remove(production);
        if (productions.size() == 0) remove(name);
      }
    }

    FastList.recycle(container);
  }

  public boolean add(IProduction production)
  {
    throw new UnsupportedOperationException(
        "use add(IProduction, ICondition) instead");
  }

  public void remove(IProduction production)
  {
    throw new UnsupportedOperationException(
        "use remove(IProduction, ICondition) instead");
  }

  public int getSize()
  {
      return _productions.size();
  }

  /**
   * @param params
   *          [root, parent]
   * @return
   * @see org.jactr.core.module.procedural.map.template.IInstantiationMapTemplate#instantiate(java.lang.Object[])
   */
  public IInstaniationMap<Object> instantiate(Object... params)
  {
    return new GeneralInstantiationMap(params[0], this, (IInstaniationMap) params[1]);
  }

}
