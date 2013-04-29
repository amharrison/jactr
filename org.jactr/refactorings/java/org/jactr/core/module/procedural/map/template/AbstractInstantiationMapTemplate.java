package org.jactr.core.module.procedural.map.template;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.map.instance.IInstaniationMap;
import org.jactr.core.production.IProduction;

public abstract class AbstractInstantiationMapTemplate<T, K, M extends IInstantiationMapTemplate<K>>
    implements IInstantiationMapTemplate<T>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractInstantiationMapTemplate.class);

  final private T                    _root;

  final private Map<K, M>            _subMaps;

  final private Set<IProduction>     _relevant;

  protected AbstractInstantiationMapTemplate(T root)
  {
    _root = root;
    _subMaps = new HashMap<K, M>();
    _relevant = new HashSet<IProduction>();
    installInitialMaps(_root);
  }

  public T getRoot()
  {
    return _root;
  }

  public int getSize()
  {
    synchronized (_subMaps)
    {
      return _subMaps.size();
    }
  }

  public boolean add(IProduction production)
  {
    FastList<M> subMaps = getSubMaps();
    boolean added = false;
    for (M subMap : subMaps)
      added |= subMap.add(production);

    if (added) synchronized (_relevant)
    {
      _relevant.add(production);
    }

    FastList.recycle(subMaps);
    return added;
  }

  public void remove(IProduction production)
  {
    FastList<M> subMaps = getSubMaps();
    for (M subMap : subMaps)
    {
      subMap.remove(production);
      if (subMap.getSize() == 0) removeSubMap(getRoot(), subMap.getRoot());
    }

    synchronized (_relevant)
    {
      _relevant.remove(production);
    }

    FastList.recycle(subMaps);
  }

  public Set<IProduction> get(Set<IProduction> container)
  {
    if (container == null) container = new HashSet<IProduction>();
    synchronized(_relevant)
    {
      container.addAll(_relevant);
    }
    return container;
  }

  abstract public IInstaniationMap<T> instantiate(Object... params);

  /**
   * return all the possible current sub map keys for the given root
   * 
   * @param root
   * @return
   */
  abstract protected Set<K> getSubMapKeys(T root);

  /**
   * initialize with the current properties
   * 
   * @param root
   */
  protected void installInitialMaps(T root)
  {
    for (K key : getSubMapKeys(root))
      addSubMap(root, key);
  }

  protected M addSubMap(T root, K key)
  {
    M subMap = instantiateSubMap(root, key);
    if (subMap != null)
      synchronized (_subMaps)
      {
        if (_subMaps.containsKey(key))
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("Replacing previous content at " + key);
        _subMaps.put(key, subMap);
      }
    return subMap;
  }

  protected void removeSubMap(T root, K key)
  {
    synchronized (_subMaps)
    {
      _subMaps.remove(key);
    }
  }

  /**
   * instantiate the actual submap
   * 
   * @param root
   * @param key
   * @return
   */
  abstract protected M instantiateSubMap(T root, K key);

  protected FastList<M> getSubMaps()
  {
    FastList<M> maps = FastList.newInstance();
    synchronized (_subMaps)
    {
      maps.addAll(_subMaps.values());
    }
    return maps;
  }

  protected M getSubMap(K key, boolean createIfMissing)
  {
    synchronized (_subMaps)
    {
      M subMap = _subMaps.get(key);
      if (subMap == null && createIfMissing)
        subMap = addSubMap(getRoot(), key);
      return subMap;
    }
  }
}
