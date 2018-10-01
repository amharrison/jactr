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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;

public class DefaultReferences implements IOptimizedReferences
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(DefaultReferences.class);

  private MutableDoubleList _arrayOfDoubles;

  private int              _nextInsertionPoint;

  private long             _referenceCount;

  private int              _optimizationLevel;

  /**
   * no optimization
   */
  public DefaultReferences()
  {
    this(10);
  }

  public DefaultReferences(int optimization)
  {
    setOptimizationLevel(optimization);
    // it optimizatilevel is massive, we don't actually want to create that
    // whole mess of storage unnecssairly
    _arrayOfDoubles = DoubleLists.mutable.empty();
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
        LOGGER.debug("overwriting reference time @ " + insertionPoint + " : "
            + time);
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

  synchronized public double getEarliestReferenceTime()
  {
    if (_arrayOfDoubles.size() != 0)
      if (_nextInsertionPoint >= _arrayOfDoubles.size())
        return _arrayOfDoubles.get(0);
      else
        return _arrayOfDoubles.get(_nextInsertionPoint);
    return Double.NaN;
  }

  synchronized public double getLastReferenceTime()
  {
    int lastIndex = _nextInsertionPoint;
    if (_arrayOfDoubles.size() != 0)
    {
      if (lastIndex == 0) lastIndex += _arrayOfDoubles.size();
      return _arrayOfDoubles.get(lastIndex - 1);
    }
    return Double.NaN;
  }

  synchronized public long getNumberOfReferences()
  {
    return _referenceCount;
  }

  synchronized public double[] getRelativeTimes(double referenceTime,
      double[] container)
  {
    int size = _arrayOfDoubles.size();
    double[] rtn = getTimes(container);
    // the container may be larger
    for (int i = 0; i < size; i++)
      rtn[i] = referenceTime - rtn[i];
    return rtn;
  }

  /**
   * these may not be returned in order
   * 
   * @see org.jactr.core.utils.references.IReferences#getTimes()
   */
  synchronized public double[] getTimes(double[] container)
  {
    // empty or too small
    if (container == null || container.length < _arrayOfDoubles.size())
      return _arrayOfDoubles.toArray();

    double[] tmp = _arrayOfDoubles.toArray();
    System.arraycopy(tmp, 0, container, 0, tmp.length);

    return container;
  }

  synchronized public void removeReferenceTime(double time)
  {
    _referenceCount--;
    if (_arrayOfDoubles.remove(time)) _nextInsertionPoint--;
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
      double[] values = getTimes((double[]) null);
      long currentCount = getNumberOfReferences();
      int lastPosition = _nextInsertionPoint - 1;

      /*
       * clear and add them back in reverse
       */
      clear();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("reclaiming times lastPos:" + lastPosition + " values:"
            + values.length + " count:" + currentCount);

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

  @Override
  synchronized public int getNumberOfRecentReferences()
  {
    return _arrayOfDoubles.size();
  }

  @Override
  public Collection<Double> getTimes(Collection<Double> container)
  {
    if (container == null) container = Lists.mutable.empty();
    final Collection<Double> fContainer = container;
    _arrayOfDoubles.forEach((d) -> {
      fContainer.add(d);
    });
    return container;
  }

}
