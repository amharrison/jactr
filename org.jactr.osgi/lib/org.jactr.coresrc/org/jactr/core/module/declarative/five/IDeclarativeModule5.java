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
package org.jactr.core.module.declarative.five;

import org.jactr.core.module.declarative.IDeclarativeModule;


public interface IDeclarativeModule5 extends IDeclarativeModule
{
  
  static public final String MISMATCH_PENALTY = "MismatchPenalty";
  
  static public final String MAXIMUM_SIMILARITY = "MaximumSimilarity";
  static public final String MAXIMUM_DIFFERENCE = "MaximumDifference";

  public double getMismatchPenalty();
  
  public void setMismatchPenalty(double mismatch);
  
  public double getMaximumSimilarity();
  
  public void setMaximumSimilarity(double maxSim);
  
  public double getMaximumDifference();
  
  public void setMaximumDifference(double maxDiff);
  
  
  public double getSimilarity(Object one, Object two);
  
  public void setSimilarity(Object one, Object two, double sim);
}


