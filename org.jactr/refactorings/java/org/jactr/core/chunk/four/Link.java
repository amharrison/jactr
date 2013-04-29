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

package org.jactr.core.chunk.four;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * A Link represents a subsymbolic associative link between two chunks J and I.
 * Spreading activation propogates from J to I. There are two ways to create a
 * Link: 1) From Chunkj to Chunki when one of Chunkj's slots contains Chunki 2)
 * From Chunkj to Chunki when Chunkj is in the goal buffer and Chunki has been
 * positively matched within any other buffer
 * 
 * @note : this is a point of divergence from ACT-R 5.0. 5.0's associative link
 *       contract is less than specific. In 4.0 condition 2) is different in
 *       that it would only be true for the retrieval buffer. Additionally, 5.0
 *       is attempting to move to an entirely similarity based system. We are
 *       still waiting for final word as to how this will be handled.
 * @author harrison
 * @created April 18, 2003
 */
public class Link
{

  private static transient Log LOGGER     = LogFactory.getLog(Link.class
                                              .getName());

  /**
   * Description of the Field
   */
  public IChunk                _jChunk;

  // j
  /**
   * Description of the Field
   */
  public IChunk                _iChunk;

  // i
  /**
   * Description of the Field
   */
  public int                   _count;

  // Number of times this link is used

  /**
   * Description of the Field
   */
  public double                _rStrength = 1;

  /**
   * Description of the Field
   */
  public double                _strength;

  /**
   * Description of the Field
   */
  public double                _fnicj;

  /**
   * Description of the Field
   */
  public double                _timeStamp;

  /**
   * The j chunk should contain the I chunk as a slot value.
   * 
   * @param j
   *            Description of the Parameter
   * @param i
   *            Description of the Parameter
   */
  public Link(IChunk j, IChunk i)
  {
    this(j, i, 1, Double.NaN);
  }

  /**
   * Constructor for the Link object
   * 
   * @param j
   *            Description of the Parameter
   * @param i
   *            Description of the Parameter
   * @param count
   *            Description of the Parameter
   * @param strength
   *            Description of the Parameter
   */
  public Link(IChunk j, IChunk i, int count, double strength)
  {
    _jChunk = j;
    _iChunk = i;
    _count = count;
    if (!Double.isNaN(strength))
      setStrength(strength);
    else
      resetStrength();
  }

  /**
   * used for copying the values from an existing link when a copy of j is made
   * 
   * @param j
   * @param i
   * @param link
   */
  public Link(IChunk newJ, Link link)
  {
    _jChunk = newJ;
    _iChunk = link._iChunk;
    _count = link._count;
    _fnicj = link._fnicj;
    _rStrength = link._rStrength;
    _strength = link._strength;
    _timeStamp = link._timeStamp;
  }

  /**
   * Return the containing chunk
   * 
   * @return The jChunk value
   */
  public IChunk getJChunk()
  {
    return _jChunk;
  }

  /**
   * Return the contained chunk
   * 
   * @return The iChunk value
   */
  public IChunk getIChunk()
  {
    return _iChunk;
  }

  /**
   * A link can actually represent multiple links between two chunks. This value
   * represents the number of links this one actually is.
   * 
   * @return The count value
   */
  public int getCount()
  {
    return _count;
  }

  /**
   * Gets the fNICJ attribute of the Link object
   * 
   * @return The fNICJ value
   */
  public double getFNICJ()
  {
    return _fnicj;
  }

  /**
   * Description of the Method
   */
  public void incrementFNICJ()
  {
    _fnicj += 1.0;
    dirty();
  }

  /**
   * Returns the R strength which is the prelog transformed strength value
   * 
   * @return The rStrength value
   */
  public double getRStrength()
  {
    if (isDirty()) computeStrength();
    return _rStrength;
  }

  /**
   * return the log transformed strength.
   * 
   * @return The strength value
   */
  public double getStrength()
  {
    if (isDirty()) computeStrength();
    return _strength;
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Return Value
   */
  @Override
  public String toString()
  {
    return String.format("%s %f %s (%d, %f, %f)", _jChunk, getStrength(),
        _iChunk, _count, _fnicj, _rStrength);
  }

  /**
   * Set the log transformed strength ? if strength learning is enabled in the
   * model, the strength value will not remain fixed.
   * 
   * @param s
   *            The new strength value
   */
  public void setStrength(double s)
  {
    _strength = s;
    _rStrength = Math.exp(s);
    dirty();
  }

  /**
   * Sets the count attribute of the Link object
   * 
   * @param count
   *            The new count value
   */
  public void setCount(int count)
  {
    _count = count;
    dirty();
  }

  /**
   * increment the count number of links this Link represents
   * 
   * @returns the new count
   */
  public void increment()
  {
    _count++;
    dirty();
  }

  /**
   * decrement the count number of links this Link represents.
   * 
   * @return Description of the Return Value
   * @returns the new count.
   */
  public int decrement()
  {
    _count--;
    dirty();
    return _count;
  }

  /**
   * Description of the Method
   */
  public void resetStrength()
  {
    _rStrength = computeDefaultRStrength();
    _strength = Math.log(_rStrength);
    clean();
  }

  /**
   * Description of the Method
   */
  protected void computeStrength()
  {
    _rStrength = computeLearnedRStrength();

    _strength = Math.log(_rStrength);
    clean();
  }

  /**
   * Gets the dirty attribute of the Link object
   * 
   * @return The dirty value
   */
  protected boolean isDirty()
  {
    return _timeStamp < ACTRRuntime.getRuntime().getClock(_iChunk.getModel())
        .getTime()
        || Double.isNaN(_rStrength) || Double.isNaN(_strength);
  }

  /**
   * Description of the Method
   */
  protected void dirty()
  {
    _timeStamp = -1;
  }

  /**
   * Description of the Method
   */
  protected void clean()
  {
    _timeStamp = ACTRRuntime.getRuntime().getClock(_iChunk.getModel())
        .getTime();
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Return Value
   */
  protected double computeDefaultRStrength()
  {
    // self link
    if (_iChunk.equals(_jChunk)) return 1;

    int totalChunksInMemory = (int) _jChunk.getModel().getDeclarativeModule()
        .getNumberOfChunks();

    int fan = 0;
    if (_jChunk.getSubsymbolicChunk() instanceof ISubsymbolicChunk4)
      fan = ((ISubsymbolicChunk4) _jChunk.getSubsymbolicChunk())
          .getNumberOfIAssociations();

    double dRji = 0;
    if (fan > 0)
      dRji = (double) totalChunksInMemory / (double) fan;
    else if (fan == 0) dRji = 1.0;

    /*
     * number of duplicate references, scales the default r accordingly
     */
    if (_count > 0) dRji *= _count;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " defaultRji : " + dRji + "(" + Math.log(dRji)
          + ") fan : " + fan + " count : " + _count + " totalFacts : "
          + totalChunksInMemory);

    return dRji;
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Return Value
   */
  protected double computeLearnedRStrength()
  {
    if (_iChunk.equals(_jChunk)) return 1;

    IModel m = _iChunk.getModel();
    IDeclarativeLearningModule4 idlm = (IDeclarativeLearningModule4) m
        .getModule(IDeclarativeLearningModule4.class);

    if (idlm == null || idlm.isAssociativeLearningEnabled()) return _rStrength;

    ISubsymbolicChunk4 j = (ISubsymbolicChunk4) _jChunk.getSubsymbolicChunk();
    ISubsymbolicChunk4 i = (ISubsymbolicChunk4) _iChunk.getSubsymbolicChunk();

    double numerator = idlm.getAssociativeLearning() * _rStrength;
    double denom = idlm.getAssociativeLearning() * j.getTimesInContext();
    double fcEji = 1.0;
    if (i.getTimesNeeded() == 0)
      fcEji = j.getTimesInContext();
    else
      fcEji = getFNICJ() * (m.getCycle() - i.getCreationCycle())
          / i.getTimesNeeded();
    numerator += fcEji;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("numerator : " + numerator + " denom : " + denom
          + " fcEji : " + fcEji);

    return numerator / denom;
  }

  /**
   * Reset all the Link strengths in a given model.
   * 
   * @param m
   *            Description of the Parameter
   */
  public static void resetAllLinks(IModel m)
  {
    try
    {
      for (IChunk chunk : m.getDeclarativeModule().getChunks().get())
      {
        ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();
        if (ssc instanceof ISubsymbolicChunk4)
          for (Link link : ((ISubsymbolicChunk4) ssc).getIAssociations(null))
//            if (Math.abs(link.getStrength()) <= 0.0001)
            {
              if (LOGGER.isDebugEnabled())
                LOGGER.debug("Reseting strength of " + link);
              link.resetStrength();
            }
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Could not reset links because of an exception ", e);
    }
  }
}
