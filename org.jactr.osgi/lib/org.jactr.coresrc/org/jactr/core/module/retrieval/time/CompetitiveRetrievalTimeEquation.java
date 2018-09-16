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

package org.jactr.core.module.retrieval.time;

import java.util.Collection;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * Competitive retrieval time equation from ACT-R 5.0
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class CompetitiveRetrievalTimeEquation implements IRetrievalTimeEquation
{



  private IRetrievalModule4 _retrievalModule;
  
  public CompetitiveRetrievalTimeEquation(IRetrievalModule4 module)
  {
    _retrievalModule = module;
  }
  
  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   * @param chunk
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public double computeRetrievalTime(IChunk chunk)
  {
    double threshold = _retrievalModule.getRetrievalThreshold();
    double scalor = _retrievalModule.getLatencyFactor();
    ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();
    ISymbolicChunk sc = chunk.getSymbolicChunk();
    double activation = ssc.getActivation();
    Collection<IChunk> otherChunks = sc.getChunkType().getSymbolicChunkType()
        .getChunks();
    double wj = 1.0;
    double sigmaActivation = 0.0;
    activation = Math.exp(activation);
    //transform to e^A

    if (activation >= threshold)
     //remove the self
    sigmaActivation -= activation;

    for(IChunk oChunk : otherChunks)
    {
      double oa = oChunk.getSubsymbolicChunk().getActivation();
      //System.out.println(otherChunks[i]+" "+oa);
      //LOGGER.debug(_parentChunk+" - Other chunk "+otherChunks[i]+" activation
      // "+oa);
      sigmaActivation += Math.exp(oa * wj);
    }

    if (!Double.isNaN(threshold) && !Double.isInfinite(threshold)) sigmaActivation += Math.exp(threshold);

    //System.out.println("S: "+scalor+" A:"+sigmaActivation+" a:"+activation+"
    // T:"+threshold);
    double time = scalor * sigmaActivation / activation;
    //LOGGER.debug("Total Activation "+sigmaActivation+" LF "+scalor+" self act
    // "+activation+" time "+time);
    return time;
  }

  public double computeRetrievalTime(IChunk retrievedChunk,
      ChunkTypeRequest retrievalRequest)
  {
    return computeRetrievalTime(retrievedChunk);
  }
}