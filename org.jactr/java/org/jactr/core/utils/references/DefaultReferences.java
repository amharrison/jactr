/*
 * Created on Nov 16, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.utils.references;

import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultReferences implements IOptimizedReferences
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(DefaultReferences.class);

  private ArrayDoubleList  _arrayOfDoubles;

  private int              _nextInsertionPoint;

  private long             _referenceCount;

  private int              _optimizationLevel;

  /**
   * no optimization
   */
  public DefaultReferences()
  {
    this(0);
  }

  public DefaultReferences(int optimization)
  {
    setOptimizationLevel(optimization);
    _arrayOfDoubles = new ArrayDoubleList(Math.min(getOptimizationLevel(), 10));
  }

  synchronized public void addReferenceTime(double time)
  {
    // will always be valide
    int insertionPoint = _nextInsertionPoint;

    _referenceCount++;

    if (insertionPoint < _arrayOfDoubles.size())
    {
      _arrayOfDoubles.set(insertionPoint, time);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("overwriting reference time @ " + insertionPoint + " : " +
            time);
    }
    else
    {
      _arrayOfDoubles.add(time);
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("adding reference time @ " + insertionPoint + " : " + time);
    }

    /*
     * calcuate the next insertion point.. normally this will jsut be an
     * increment
     */
    _nextInsertionPoint++;

    /*
     * but, if we exceed the optimization level, we drop back to zero so that
     * the next add will overwrite the oldest
     */
    if (_nextInsertionPoint >= _optimizationLevel)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("next insertion point exceeds or equals optimization level, zeroing");
      _nextInsertionPoint = 0;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("next insertion point will be @ " + _nextInsertionPoint);
  }

  synchronized public void clear()
  {
    _referenceCount = 0;
    _arrayOfDoubles.clear();
    _nextInsertionPoint = 0;
  }

  synchronized public double getLastReferenceTime()
  {
    if (_arrayOfDoubles.size() != 0)
      return _arrayOfDoubles.get(Math.max(_nextInsertionPoint - 1, 0));
    return Double.NaN;
  }

  synchronized public long getNumberOfReferences()
  {
    return _referenceCount;
  }

  synchronized public double[] getRelativeTimes(double referenceTime)
  {
    double[] rtn = getTimes();
    for (int i = 0; i < rtn.length; i++)
      rtn[i] = referenceTime - rtn[i];
    return rtn;
  }

  /**
   * these may not be returned in order
   * 
   * @see org.jactr.core.utils.references.IReferences#getTimes()
   */
  synchronized public double[] getTimes()
  {
    return _arrayOfDoubles.toArray();
  }

  synchronized public void removeReferenceTime(double time)
  {
    _referenceCount--;
    if (_arrayOfDoubles.removeElement(time)) _nextInsertionPoint--;
    if (_nextInsertionPoint < 0)
      _nextInsertionPoint = _arrayOfDoubles.size() - 1;
  }

  synchronized public void setNumberOfReferences(long references)
  {
    _referenceCount = references;
  }

  synchronized public int getOptimizationLevel()
  {
    return _optimizationLevel;
  }

  final synchronized public void setOptimizationLevel(int level)
  {
    if (level == 0) level = Integer.MAX_VALUE;

    int oldLevel = _optimizationLevel;
    _optimizationLevel = level;

    // do some tweaking..
    if (level < oldLevel)
    {
      /*
       * we've got some contracting to do.. snag the values and the current
       * insertion point
       */
      double[] values = getTimes();
      long currentCount = getNumberOfReferences();
      int lastPosition = _nextInsertionPoint - 1;

      /*
       * clear and add them back in reverse
       */
      clear();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("reclaiming times lastPos:" + lastPosition + " values:" +
            values.length + " count:" + currentCount);

      /*
       * add oldest to newest
       */
      for (int i = Math.max(0, lastPosition); i < values.length; i++)
        addReferenceTime(values[i]);

      for (int i = 0; i < lastPosition && i < values.length; i++)
        addReferenceTime(values[i]);

      _referenceCount = currentCount;
    }
  }

}
