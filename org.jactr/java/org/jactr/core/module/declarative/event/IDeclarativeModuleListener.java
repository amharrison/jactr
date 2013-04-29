/*
 * Created on Aug 14, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.module.declarative.event;

import java.util.EventListener;

import org.jactr.core.event.IParameterListener;

public interface IDeclarativeModuleListener extends EventListener, IParameterListener
{
  
  /**
   * called when a chunktype is created, but not necessarily fully defined
   * @param dme
   */
  public void chunkTypeCreated(DeclarativeModuleEvent dme);
  
  /**
   * called when a chunk type is added to the model
   * @param dme
   */
  public void chunkTypeAdded(DeclarativeModuleEvent dme);
  
  /**
   * called when two chunktypes are merged
   * @param dme
   */
  public void chunkTypesMerged(DeclarativeModuleEvent dme);
  
  public void chunkCreated(DeclarativeModuleEvent dme);
  
  public void chunkAdded(DeclarativeModuleEvent dme);
  
  public void chunksMerged(DeclarativeModuleEvent dme);
  
  public void chunkDisposed(DeclarativeModuleEvent dme);
  
  public void chunkTypeDisposed(DeclarativeModuleEvent dme);
  
  
}


