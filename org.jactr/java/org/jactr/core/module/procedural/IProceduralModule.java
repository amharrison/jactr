/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.procedural;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.module.IModule;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;

/**
 * specialized module for accessing procedural memory
 * 
 * @author developer
 */
public interface IProceduralModule extends IModule
{
  
  static public final String DEFAULT_PRODUCTION_FIRING_TIME = "DefaultProductionFiringTime";

  static public final String NUMBER_OF_PRODUCTIONS_FIRED    = "NumberOfProductionsFired";
  
  
  /**
   * create a production, likely backed by the factory
   * 
   * @param name
   * @return
   */
  public CompletableFuture<IProduction> createProduction(String name);

  /**
   * add this production to procedural memory and check for duplicates
   * 
   * @param production
   * @return
   */
  public CompletableFuture<IProduction> addProduction(IProduction production);

  /**
   * return the named production
   * 
   * @param name
   * @return
   */
  public CompletableFuture<IProduction> getProduction(String name);
  
  /**
   * return all the productions
   * @return
   */
  public CompletableFuture<Collection<IProduction>> getProductions();

  /**
   * find the set of production instantiations that can fire based on the state
   * of the buffers and fire the notification events
   * 
   * @param buffers
   * @return
   */
  public CompletableFuture<Collection<IInstantiation>> getConflictSet(
      Collection<IActivationBuffer> buffers);

  /**
   * actually executed the production returning the amount of time it took to
   * fire this production and fire notification events. This should catch
   * and ModelTerminatedException and return Double.NaN if it catches it
   * 
   * @param instantiation
   * @param firingTime TODO
   * @return
   */
  public CompletableFuture<Double> fireProduction(IInstantiation instantiation,
      double firingTime);

  public CompletableFuture<IInstantiation> selectInstantiation(
      Collection<IInstantiation> instantiations);
  
  public void addListener(IProceduralModuleListener listener, Executor executor);

  public void removeListener(IProceduralModuleListener listener);
  
  /**
   * the default production firing time, aka default action time
   * 
   * @return
   */
  public double getDefaultProductionFiringTime();

  public void setDefaultProductionFiringTime(double firingTime);
  
  /**
   * @return
   */
  public long getNumberOfProductionsFired();

  public void setNumberOfProductionsFired(long fired);
  
  public void setProductionSelector(IProductionSelector selector);

  public IProductionSelector getProductionSelector();

  public void setProductionInstantiator(IProductionInstantiator instantiator);

  public IProductionInstantiator getProductionInstantiator();

  public void setConflictSetAssembler(IConflictSetAssembler assembler);

  public IConflictSetAssembler getConflictSetAssembler();
}
