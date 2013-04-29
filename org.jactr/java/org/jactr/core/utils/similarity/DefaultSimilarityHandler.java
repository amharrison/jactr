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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.slot.ISlot;

/**
 * The DefaultSimilarityHandler handles basic similarity computations and
 * permits the attaching of custom handlers
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class DefaultSimilarityHandler implements SimilarityHandler
{

  /**
   * Description of the Field
   */
  protected List<SimilarityHandler> _handlers;

  /**
   * Constructor for the DefaultSimilarityHandler object
   */
  public DefaultSimilarityHandler()
  {
    _handlers = new ArrayList<SimilarityHandler>();
    addHandler(this);
  }

  /**
   * Adds a feature to the Handler attribute of the DefaultSimilarityHandler
   * object
   * 
   * @param sm
   *            The feature to be added to the Handler attribute
   */
  public void addHandler(SimilarityHandler sm)
  {
    _handlers.add(0, sm);
  }

  /**
   * Description of the Method
   * 
   * @param sm
   *            Description of the Parameter
   */
  public void removeHandler(SimilarityHandler sm)
  {
    _handlers.remove(sm);
  }

  /**
   * Gets the similarity attribute of the DefaultSimilarityHandler object
   * 
   * @param one
   *            Description of the Parameter
   * @param two
   *            Description of the Parameter
   * @param maxDiff
   *            Description of the Parameter
   * @param maxSim
   *            Description of the Parameter
   * @return The similarity value
   */
  public double getSimilarity(Object one, Object two, double maxDiff,
      double maxSim)
  {
    for (SimilarityHandler sm : _handlers)
      if (sm.handles(one, two))
      {
        // if the handler can do it, delegate

        double sim = sm.computeSimilarity(one, two, maxDiff, maxSim);
        return sim;
      }
    // otherwise return maxDiff
    return maxDiff;
  }

  /**
   * can this handler compute a similarity for these two chunks
   * 
   * @param one
   *            Description of the Parameter
   * @param two
   *            Description of the Parameter
   * @return true if it can compute the sim
   */
  public boolean handles(Object one, Object two)
  {
    return true;
  }

  /**
   * compute the similarty between one and two scaled to fit maxDiff and maxSim
   * 
   * @param one
   *            Description of the Parameter
   * @param two
   *            Description of the Parameter
   * @param maxDiff
   *            Description of the Parameter
   * @param maxSim
   *            Description of the Parameter
   * @return maxSim if they are equal maxDiff if not
   */
  public double computeSimilarity(Object one, Object two, double maxDiff,
      double maxSim)
  {
    if (!(one instanceof IChunk) && !(two instanceof IChunk))
    {
      return maxDiff;
    }

    /*
     * both chunks must have ISubsymbolicChunk5
     */
    IChunk c1 = (IChunk) one;
    IChunk c2 = (IChunk) two;

    if (!(c1.getSubsymbolicChunk() instanceof ISubsymbolicChunk5)
        || !(c2.getSubsymbolicChunk() instanceof ISubsymbolicChunk5))
      return maxDiff;

    double lastSim = ((ISubsymbolicChunk5) c1.getSubsymbolicChunk())
        .getSimilarity(c2);
    if (!Double.isNaN(lastSim))
    {
      return lastSim;
    }

    Collection<? extends ISlot> oneSlots = ((IChunk) one).getSymbolicChunk()
        .getSlots();
    ISlot tmpSlot = null;
    ISymbolicChunk twoSC = ((IChunk) two).getSymbolicChunk();
    double sim = maxSim;
    for (ISlot slot : oneSlots)
    {
      if ((tmpSlot = twoSC.getSlot(slot.getName())) != null)
      {
        if (!tmpSlot.equalValues(slot))
        {
          sim = maxDiff;
          break;
        }
      }
      else
      {
        sim = maxDiff;
      }
    }

    setSimilarity((IChunk) one, (IChunk) two, sim);
    // the only way we get this far is if all is good..
    return sim;
  }

  /**
   * Sets the similarity attribute of the DefaultSimilarityHandler class
   * 
   * @param one
   *            The new similarity value
   * @param two
   *            The new similarity value
   * @param sim
   *            The new similarity value
   */
  public void setSimilarity(IChunk one, IChunk two, double sim)
  {
    Double similarity = new Double(sim);

    if (one.getSubsymbolicChunk() instanceof ISubsymbolicChunk5)
      ((ISubsymbolicChunk5) one.getSubsymbolicChunk()).setSimilarity(two,
          similarity);
    if (two.getSubsymbolicChunk() instanceof ISubsymbolicChunk5)
      ((ISubsymbolicChunk5) two.getSubsymbolicChunk()).setSimilarity(one,
          similarity);
  }

}
