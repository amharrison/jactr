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
package org.jactr.core.module.declarative.four.learning;

import org.jactr.core.module.declarative.four.IBaseLevelActivationEquation;
import org.jactr.core.module.learning.ILearningModule;


public interface IDeclarativeLearningModule4 extends ILearningModule
{

  static public final String BASE_LEVEL_LEARNING_RATE = "BaseLevelLearningRate";
  static public final String ASSOCIATIVE_LEARNING_RATE = "AssociativeLearningRate";
  static public final String OPTIMIZED_LEARNING = "OptimizedLearning";
  
  /**
   * 
   * @return
   */
  public boolean isBaseLevelLearningEnabled();
  
  
  /**
   * 
   * @return
   */
  public double getBaseLevelLearning();
  
  
  /**
   * 
   * @return
   */
  public boolean isAssociativeLearningEnabled();
  
  
  /**
   * 
   * @return
   */
  public double getAssociativeLearning();
  
  
  public int getOptimizationLevel();
  
  
  public IBaseLevelActivationEquation getBaseLevelActivationEquation();
  
}


