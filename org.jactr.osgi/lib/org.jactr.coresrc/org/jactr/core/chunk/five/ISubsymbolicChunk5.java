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
package org.jactr.core.chunk.five;

import java.util.Collection;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
public interface ISubsymbolicChunk5 extends ISubsymbolicChunk
{
  static public final String SIMILARITIES = "Similarities";
  
  /**
   * The similarity between two chunks can either be computed by the similarity
   * computer or it can be set directly by the modeler. This sets the similarity
   * between this chunk and c to value.
   * 
   * @param c
   *          The new similarity value
   * @param value
   *          The new similarity value
   */
  public void setSimilarity(IChunk c, double value);

  /**
   * @param c
   *          Description of the Parameter
   * @return Double.NaN if no similarity is defined
   */
  public double getSimilarity(IChunk c);

  /**
   * @param container TODO
   * @return an array of [[IChunk, Double],[IChunk, Double],....]
   */
  public Collection<Object[]> getSimilarities(Collection<Object[]> container);
  
  
  public double getSimilarityActivation();
  
  public void setSimilarityActivation(double similarity);
  
  /**
   * Return the activation given this pattern. This is for retrieving activation
   * when partial matching is enabled. Mismatches will result in a decrement in
   * activation as a function of similarity.
   * 
   * @param retr
   *          Description of the Parameter
   * @return The activation value
   */
  public double getActivation(ChunkTypeRequest retr);
}


