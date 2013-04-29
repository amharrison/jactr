/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.production;

import java.util.Collection;
import java.util.concurrent.Executor;

import org.jactr.core.model.IModel;
import org.jactr.core.production.event.IProductionListener;
import org.jactr.core.production.event.ProductionEvent;
import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.ICommentable;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created February 5, 2003
 */
public interface IProduction extends Comparable<IProduction>, ICommentable,
    IAdaptable
{

  public void addListener(IProductionListener pl, Executor executor);

  public void removeListener(IProductionListener pl);

  public boolean hasListeners();

  public void dispatch(ProductionEvent event);

  /**
   * Gets the symbolicProduction attribute of the IProduction object
   * 
   * @return The symbolicProduction value
   * @since
   */
  public ISymbolicProduction getSymbolicProduction();

  /**
   * Gets the subsymbolicProduction attribute of the IProduction object
   * 
   * @return The subsymbolicProduction value
   * @since
   */
  public ISubsymbolicProduction getSubsymbolicProduction();

  /**
   * attempts to instantiate this production given the current state of the
   * model as defined by the collection of buffers. returns an instantiation of
   * this production
   */
  public Collection<IInstantiation> instantiateAll(Collection<VariableBindings> provisionalBindings) throws CannotInstantiateException;

  /**
   * Description of the Method
   * 
   * @since
   */
  public void dispose();

  /**
   * get the model that this production is associated with
   * 
   * @return
   */
  public IModel getModel();

  public void encode();

  public boolean isEncoded();
}
