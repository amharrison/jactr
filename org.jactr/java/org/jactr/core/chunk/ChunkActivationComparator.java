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

package org.jactr.core.chunk;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.production.request.ChunkTypeRequest;

/*
 * sorts descending based on current activation
 */
/*
 * sorts by activation, descending
 */
/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class ChunkActivationComparator implements java.util.Comparator<IChunk>
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkActivationComparator.class);

  ChunkTypeRequest                       _pattern;

  /*
   * 1 one <two 0 one=two -1 one>two
   */
  /**
   * Description of the Method
   * 
   * @param one
   *            Description of the Parameter
   * @param two
   *            Description of the Parameter
   * @return Description of the Return Value
   */
  public int compare(IChunk one, IChunk two)
  {
    /*
     * exact same chunk
     */
    if (one == two || one != null && one.equals(two)) return 0;

    ISubsymbolicChunk sc1 = one.getSubsymbolicChunk();
    ISubsymbolicChunk sc2 = two.getSubsymbolicChunk();

    double base1 = sc1.getActivation();
    double base2 = sc2.getActivation();

    if (_pattern != null)
      try
      {
        base1 = ((ISubsymbolicChunk5) sc1).getActivation(_pattern);
        base2 = ((ISubsymbolicChunk5) sc2).getActivation(_pattern);
      }
      catch (ClassCastException cce)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Someone isnt a subsymbolicchunk5");
      }
    // System.out.println(one+": "+base1+" <=> "+two+": "+base2);

    /*
     * this will eliminate chunks of the same activation, but possibly different
     * chunks - dumb mistake
     */
    // if (base1 == base2)
    // {
    // return 0;
    // }
    // else
    if (base1 < base2) return 1;
    if (base1 == base2) /*
                         * this is necessary because this comparator is often
                         * used in a set situation
                         * 
                         * @see DefaultSlotSearchSupport.findCandidates(). If we
                         *      just return -1 all the time when the activations
                         *      are equal, then searching for a specific chunk
                         *      in the set might abort early. This was the case
                         *      when candidateList.removeAll(cullSet) was being
                         *      called in findCandidates and cullSet was not
                         *      actually removed.
                         */
    // sort by something deterministic - name..
      return one.getSymbolicChunk().getName().compareTo(
          two.getSymbolicChunk().getName());
    return -1;
  }

  /**
   * Sets the pattern attribute of the IChunkActivationComparator object
   * 
   * @param p
   *            The new pattern value
   */
  public void setChunkTypeRequest(ChunkTypeRequest p)
  {
    _pattern = p;
  }

  public Comparator<IChunk> reversed()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
