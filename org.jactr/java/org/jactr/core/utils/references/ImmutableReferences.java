/*
 * Created on Nov 20, 2006
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
package org.jactr.core.utils.references;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class ImmutableReferences implements IReferences
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ImmutableReferences.class);

  IReferences _references;
  
  public ImmutableReferences(IReferences references)
  {
    _references = references;
  }
  
  public void addReferenceTime(double time)
  {
    //noop
  }

  public void clear()
  {
    //noop
  }

  public double getLastReferenceTime()
  {
    return _references.getLastReferenceTime();
  }

  public long getNumberOfReferences()
  {
    return _references.getNumberOfReferences();
  }

  public double[] getRelativeTimes(double referenceTime, double[] container)
  {
    return _references.getRelativeTimes(referenceTime, container);
  }

  public double[] getTimes(double[] container)
  {
    return _references.getTimes(container);
  }

  public void removeReferenceTime(double time)
  {
    //noop
  }

  public void setNumberOfReferences(long references)
  {
    //noop
  }

  @Override
  public double getEarliestReferenceTime()
  {
    return _references.getEarliestReferenceTime();
  }



  @Override
  public void setOptimizationLevel(int level)
  {

  }

  @Override
  public int getOptimizationLevel()
  {
    return _references.getOptimizationLevel();
  }

  @Override
  public int getNumberOfRecentReferences()
  {
    return _references.getNumberOfRecentReferences();
  }

  @Override
  public Collection<Double> getTimes(Collection<Double> container)
  {
    return _references.getTimes(container);
  }

}


