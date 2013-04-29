/*
 * Created on Dec 5, 2006
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
package org.jactr.modules.pm;

import java.util.concurrent.Executor;

import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.modules.pm.common.symbol.ISymbolGrounder;
/**
 * @author developer
 *
 */
public interface IPerceptualModule extends IAsynchronousModule
{
  
  static public final String HIGHEST_CHUNK                    = "highest";

  static public final String LOWEST_CHUNK                     = "lowest";

  static public final String LESS_THAN_CURRENT_CHUNK          = "less-than-current";

  static public final String GREATER_THAN_CURRENT_CHUNK       = "greater-than-current";

  /**
   * return the executor that should shared by all common reality listeners
   * this is typically only valid once the model has started to run
   * @return
   */
  public Executor getCommonRealityExecutor();
  
  /**
   * returns the shared symbol grounder. In order to do cross-modal linkages
   * correctly all modules should use the same grounder.
   * 
   * @return
   */
  public ISymbolGrounder getSymbolGrounder();

  public void setSymbolGrounder(ISymbolGrounder grounder);
}


