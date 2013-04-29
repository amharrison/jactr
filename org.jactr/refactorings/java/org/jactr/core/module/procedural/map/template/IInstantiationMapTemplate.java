package org.jactr.core.module.procedural.map.template;

/*
 * default logging
 */
import java.util.Set;

import org.jactr.core.module.procedural.map.instance.IInstaniationMap;
import org.jactr.core.production.IProduction;

/**
 * basic RETE-equse map interface
 * @author harrison
 *
 */
public interface IInstantiationMapTemplate<T>
{

  public T getRoot();
  
  /**
   * 
   * @param production
   * @return true if this map is accepting the production
   */
  public boolean add(IProduction production);
  
  public void remove(IProduction production);
  
  public int getSize();
  
  public Set<IProduction> get(Set<IProduction> container);
  
  
  public IInstaniationMap<T> instantiate(Object... params);
}
