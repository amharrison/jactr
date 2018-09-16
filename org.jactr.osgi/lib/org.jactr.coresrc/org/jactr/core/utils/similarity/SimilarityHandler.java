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

package org.jactr.core.utils.similarity;

/**
 * code for computing the similarity between two chunks.. They should implement
 * a caching mechanism so that hard coded similarities will nto be overwritten.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface SimilarityHandler
{

  /**
   * can this handler compute a similarity for these two chunks
   * 
   * @param one
   *          Description of the Parameter
   * @param two
   *          Description of the Parameter
   * @return true if it can compute the sim
   */
  public boolean handles(Object one, Object two);

  /**
   * compute the similarty between one and two scaled to fit maxDiff and maxSim
   * 
   * @param one
   *          Description of the Parameter
   * @param two
   *          Description of the Parameter
   * @param maxDiff
   *          Description of the Parameter
   * @param maxSim
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public double computeSimilarity(Object one, Object two, double maxDiff,
      double maxSim);

}

