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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.link.AbstractAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.chunk.link.IAssociativeLinkEquation;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * A Link represents a subsymbolic associative link between two chunks J and I.
 * Spreading activation propogates from J to I. There are two ways to create a
 * Link: 1) From Chunkj to Chunki when one of Chunki's slots contains Chunkj 2)
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
public class Link4 extends AbstractAssociativeLink
{

  static public final String       COUNT_PARAM = "Count";

  static public final String       FNICJ_PARAM = "FNiCj";

  private static transient Log     LOGGER      = LogFactory.getLog(Link4.class
                                                   .getName());

  /**
   * Number of times this link is used
   */
  private int                      _count;

  /**
   * prior strength
   */
  private double                   _rStrength  = 1;

  /**
   * count of the times that i was accessed while j was in context
   */
  private double                   _fnicj;

  /**
   * to permit lazy computation of strength
   */
  private double                   _timeStamp  = -1;

  private IAssociativeLinkEquation _linkEquation;

  /**
   * The j chunk should contain the I chunk as a slot value.
   */
  public Link4(IChunk j, IChunk i)
  {
    this(j, i, 1, Double.NaN);
  }

  /**
   * Constructor for the Link object
   * 
   * @param j
   *          Description of the Parameter
   * @param i
   *          Description of the Parameter
   * @param count
   *          Description of the Parameter
   * @param strength
   *          Description of the Parameter
   */
  public Link4(IChunk j, IChunk i, int count, double strength)
  {
    super(j, i, strength);
    _count = count;
  }

  public void setAssociativeLinkEquation(IAssociativeLinkEquation equation)
  {
    _linkEquation = equation;
  }

  public IAssociativeLinkEquation getAssociativeLinkEquation()
  {
    return _linkEquation;
  }

  /**
   * used for copying the values from an existing link when a copy of j is made
   * 
   * @param j
   * @param i
   * @param link
   */
  // public Link(IChunk newJ, Link link)
  // {
  // _jChunk = newJ;
  // _iChunk = link._iChunk;
  // _count = link._count;
  // _fnicj = link._fnicj;
  // _rStrength = link._rStrength;
  // _strength = link._strength;
  // _timeStamp = link._timeStamp;
  // }

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

  public void setFNICJ(double FNiCj)
  {
    _fnicj = FNiCj;
    dirty();
  }

  /**
   * Returns the R strength which is the prelog transformed strength value
   * 
   * @return The rStrength value
   */
  public double getRStrength()
  {
    return _rStrength;
  }

  public void setRStrength(double r)
  {
    _rStrength = r;
    dirty();
  }

  @Override
  public double getStrength()
  {
    return getStrength(true);
  }

  public double getStrength(boolean recompute)
  {
    if (recompute && isDirty()) computeStrength();
    return super.getStrength();
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Return Value
   */
  @Override
  public String toString()
  {
    return String.format("j:%s - i:%s", getJChunk(), getIChunk());
  }

  /**
   * Set the log transformed strength ? if strength learning is enabled in the
   * model, the strength value will not remain fixed.
   * 
   * @param s
   *          The new strength value
   */
  @Override
  public void setStrength(double s)
  {
    super.setStrength(s);
    _rStrength = Math.exp(s);
  }

  /**
   * Sets the count attribute of the Link object
   * 
   * @param count
   *          The new count value
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
  protected void computeStrength()
  {
    clean();
    double strength = 0;
    if (_linkEquation != null)
      strength = _linkEquation.computeLearnedStrength(this);

    if (Double.isNaN(strength)) strength = 0;

    setStrength(strength);
  }

  /**
   * Gets the dirty attribute of the Link object current will recalculate only
   * once per cycle.
   * 
   * @return The dirty value
   */
  public boolean isDirty()
  {
    return _timeStamp < 0 // getIChunk().getModel().getAge()
        || Double.isNaN(_rStrength);
  }

  /**
   * Description of the Method
   */
  public void dirty()
  {
    _timeStamp = -1;
  }

  /**
   * Description of the Method
   */
  protected void clean()
  {
    _timeStamp = getIChunk().getModel().getAge();
  }

  @Override
  public void copy(IAssociativeLink link) throws IllegalArgumentException
  {
    super.copy(link);
    if (link instanceof Link4)
    {
      Link4 l4 = (Link4) link;
      setRStrength(l4.getRStrength());
      setCount(l4.getCount());
      setFNICJ(l4.getFNICJ());
      l4.dirty();
    }
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>(3);
    rtn.addAll(super.getPossibleParameters());
    rtn.add(COUNT_PARAM);
    rtn.add(FNICJ_PARAM);
    return rtn;
  }

  @Override
  public String getParameter(String key)
  {
    if (COUNT_PARAM.equalsIgnoreCase(key))
      return Integer.toString(getCount());
    else if (FNICJ_PARAM.equalsIgnoreCase(key))
      return Double.toString(getFNICJ());
    else
      return super.getParameter(key);
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (COUNT_PARAM.equalsIgnoreCase(key))
      setCount(ParameterHandler.numberInstance().coerce(value).intValue());
    else if (FNICJ_PARAM.equalsIgnoreCase(key))
      setFNICJ(ParameterHandler.numberInstance().coerce(value).doubleValue());
    else
      super.setParameter(key, value);
  }

}
