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

import java.util.Collection;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.slot.ISlot;

/**
 * AutoSimilarityHandler that computes the similarity between any two chunks
 * based on average of the similarities between their slot values. This is
 * similar, if not identical to Raluca's proposal. 0. if they are the same,
 * return maxsim 1. if the sim between two chunks is already defined, return it
 * 2. if they are of the same chunk type, or derivable chunk types - compare the
 * slot values based on the smaller of the two chunks 3. if they are not related
 * chunk types - return maxDiff 4. if they are both numbers - return the diff
 * scaled by maxDiff/maxSim 5. if they are strings.. well, screw them
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class AutoSimilarityHandler implements
    SimilarityHandler
{

  /**
   * Description of the Field
   */
  protected int _depth;

  /**
   * Constructor for the AutoSimilarityHandler object
   */
  public AutoSimilarityHandler()
  {
    this(-1);
  }

  /**
   * Constructor for the AutoSimilarityHandler object
   * 
   * @param depth
   *          Description of the Parameter
   */
  public AutoSimilarityHandler(int depth)
  {
    _depth = depth;
  }

  /**
   * Description of the Method
   * 
   * @param one
   *          Description of the Parameter
   * @param two
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public boolean handles(Object one, Object two)
  {
    return true;
  }

  /*
   * compute the similarty between one and two scaled to fit maxDiff and maxSim
   * This is done by recursively descending to depth checking slot values..
   */
  /**
   * Description of the Method
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
      double maxSim)
  {
    return computeSimilarity(one, two, maxDiff, maxSim, _depth);
  }

  /**
   * Description of the Method
   * 
   * @param one
   *          Description of the Parameter
   * @param two
   *          Description of the Parameter
   * @param maxDiff
   *          Description of the Parameter
   * @param maxSim
   *          Description of the Parameter
   * @param depth
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  protected double computeSimilarity(Object one, Object two, double maxDiff,
      double maxSim, int depth)
  {
    if (one == two)
    {
      return maxSim;
    }
    //identical
    else if (depth == 0)
    {
      return maxDiff;
    }
    //we've drilled alway down
    else if ((one == null || two != null) && (one != null || two == null))
    {
      return maxDiff;
    }
    else if (one instanceof Number)
    {
      //two numbers??

      if (!(two instanceof Number)) { return maxDiff; }
      //nope, just one
      int first = ((Number) one).intValue();
      int second = ((Number) two).intValue();
      double scale = 1 - (Math.max(second, first) - Math.min(second, first))
          / Math.max(second, first);
      return scale * (maxSim - maxDiff) + maxDiff;
    }
    else if (one instanceof String)
    {
      if (!(two instanceof String)) { return maxDiff; }
      // String first = (String) one;
      // String second = (String) two;
      //how do we numerically compare two strings?
      return maxDiff;
    }
    else if (one instanceof IChunk)
    {
      if (!(two instanceof IChunk)) { return maxDiff; }
      
      
      /*
       * both chunks must have ISubsymbolicChunk5
       */
      IChunk c1 = (IChunk) one;
      IChunk c2 = (IChunk) two;
      
      if( ! (c1.getSubsymbolicChunk() instanceof ISubsymbolicChunk5) ||
          ! (c2.getSubsymbolicChunk() instanceof ISubsymbolicChunk5))
        return maxDiff;
      
      double lastSim = ((ISubsymbolicChunk5)c1.getSubsymbolicChunk()).getSimilarity(
          c2);
      
      //both are chunks...
      //if the sim is already defined...return it
      if (!Double.isNaN(lastSim))
        return lastSim;

      ISymbolicChunk first = ((IChunk) one).getSymbolicChunk();
      ISymbolicChunk second = ((IChunk) two).getSymbolicChunk();

      //same chunk types?
      if (!first.isA(second.getChunkType())
          && !second.isA(first.getChunkType())) { return maxDiff; }

      Collection<? extends ISlot> fSlots = first.getSlots();
      Collection<? extends ISlot> sSlots = second.getSlots();
      Collection<? extends ISlot> slots = null;
      //slot matches?
      int diffSlots = fSlots.size() - sSlots.size();
      //so who ever has the fewest slots is the basis of comparison
      if (diffSlots < 0)
      {
        slots = fSlots;
        diffSlots = Math.abs(diffSlots);
      }
      else
      {
        slots = sSlots;
        second = first;
      }
      double sim = maxDiff * diffSlots;
      for(ISlot slot : slots)
      {
        try
        {
          Object sOne = slot.getValue();
          Object sTwo = second.getSlot(slot.getName()).getValue();
          sim += computeSimilarity(sOne, sTwo, maxDiff, maxSim, --depth);
        }
        catch (Exception e)
        {
          sim += maxDiff;
          //slot error
        }
      }
      sim /= (slots.size() + diffSlots);
      return sim;
    }
    else
    {
      return maxDiff;
    }
  }
}

