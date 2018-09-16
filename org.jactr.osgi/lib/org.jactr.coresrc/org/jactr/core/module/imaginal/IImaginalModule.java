/*
 * Created on Feb 6, 2007
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
package org.jactr.core.module.imaginal;

import org.jactr.core.module.IModule;
/**
 * @author developer
 *
 */
public interface IImaginalModule extends IModule
{
  static public final String IMAGINAL_BUFFER = "imaginal";
  static public final String IMAGINAL_LOG = "IMAGINAL";
  
  static public final String IMAGINAL_ADD_DELAY_PARAM = "AddDelayTime";
  static public final String IMAGINAL_MODIFY_DELAY_PARAM = "ModifyDelayTime";
  static public final String IMAGINAL_RANDOMIZE_DELAY_PARAM = "RandomizeDelaysEnabled";
  
  /**
   * set the amount of time it takes to add a new chunk
   * to the imaginal buffer
   * @param addDelayTime
   */
  public void setAddDelayTime(double addDelayTime);
  
  public double getAddDelayTime();
  
  /**
   * set the amount of time it takes for modification requests
   * @param modDelayTime
   */
  public void setModifyDelayTime(double modDelayTime);
  
  public double getModifyDelayTime();
  
  public void setRandomizeDelaysEnabled(boolean enabled);
  
  public boolean isRandomizeDelaysEnabled();
}


