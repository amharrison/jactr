/*
 * Created on Oct 25, 2006
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
package org.jactr.core.module.declarative.four;

import org.jactr.core.module.declarative.IDeclarativeModule;

/**
 * 
 * @author developer
 *
 */
public interface IDeclarativeModule4 extends IDeclarativeModule
{
  static public final String PARTIAL_MATCHING = "EnablePartialMatching";
  static public final String ACTIVATION_NOISE = "ActivationNoise";

  static public final String PERMANENT_ACTIVATION_NOISE = "PermanentActivationNoise";
  static public final String BASE_LEVEL_CONSTANT = "BaseLevelConstant";
  
  
  
  public boolean isPartialMatchingEnabled();
  
  public void setPartialMatchingEnabled(boolean enable);
  
  public double getActivationNoise();
  
  public void setActivationNoise(double noise);
  
  public double getPermanentActivationNoise();
  
  public void setPermanentActivationNoise(double noise);
  
  public double getBaseLevelConstant();
  
  public void setBaseLevelConstant(double base);
  
  /**
   * return the activation penalty for mismatches this parameter only applies if
   * partial matching is enabled
   * 
   * @return The MismatchPenalty value
   * @since
   */
  public double getMismatchPenalty();

  /**
   * set the activation penalty for a mismatched slot value
   * 
   * @param p
   * @since
   */
  public void setMismatchPenalty(double p);
}


