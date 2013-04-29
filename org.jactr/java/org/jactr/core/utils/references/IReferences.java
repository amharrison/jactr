/*
 * Created on Nov 16, 2006
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


/**
 * interface used to track the number and times of references of objects in a model
 * @author developer
 *
 */
public interface IReferences
{

  /**
   * get all the tracked absolute reference times. The order of the
   * times is not guaranteed
   * @return all the known absolute reference times
   */
  public double[] getTimes();
  
  /**
   * get all the reference times relative to referenceTime
   * @param referenceTime
   * @return
   */
  public double[] getRelativeTimes(double referenceTime);
  
  /**
   * get the number of references. this value need not equal
   * getTimes().size(), for instance getTimes() might just track a window
   * of values and not every reference.
   * @return
   */
  public long getNumberOfReferences();
  
  
  /**
   * get the last time there has been a reference
   * @return
   */
  public double getLastReferenceTime();
  
  
  /**
   * add a reference time and increment the reference count
   * @param time
   */
  public void addReferenceTime(double time);
  
  
  /**
   * remove a reference time and decrement the reference coutn
   * @param time
   */
  public void removeReferenceTime(double time);
  
  
  /**
   * clear it out
   *
   */
  public void clear();
  
  
  /**
   * 
   * @param references
   */
  public void setNumberOfReferences(long references);
  
  
  /**
   * factory for instantiating new references
   * @author developer
   *
   */
  static public class Factory
  {
    static private Factory _default = new Factory();
    
    
    static public void set(Factory factory)
    {
      _default = factory;
    }
    
    static public Factory get()
    {
      return _default;
    }
    
    public IReferences newInstance()
    {
      return new DefaultReferences();
    }
  }
}


