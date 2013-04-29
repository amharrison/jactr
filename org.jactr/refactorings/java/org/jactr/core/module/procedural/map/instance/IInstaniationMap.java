package org.jactr.core.module.procedural.map.instance;

import java.util.Set;

import org.jactr.core.module.procedural.map.template.IInstantiationMapTemplate;
import org.jactr.core.production.IProduction;

/*
 * default logging
 */

public interface IInstaniationMap<T>
{
  
  public IInstantiationMapTemplate<T> getTemplate();
  
  public IInstaniationMap getParent();
  /**
   * 
   * @param production
   * @return true if this map is accepting the production
   */
  public boolean add(IProduction production);
  
  public void remove(IProduction production);
  
  public int getSize();
  
  public void activate();
  
  public void deactivate();
  
  public boolean isActivated();
  
  public ProductionTable getProductionTable();
  
  /**
   * the set of relevant productions who's instantiability is not know, 
   * @return
   */
  public Set<IProduction> getTestableProductions();
  
  /**
   * a set of the relevant, but uninstantiable productions 
   * @return
   */
  public Set<IProduction> getFailedProductions();
}
