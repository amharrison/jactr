/*
 * Created on May 9, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.queue.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Crazy prioritized queue (bad name.. whatever) that had to be implemented for
 * one reason: java.util.PriorityQueue does not maintain insertion order of
 * items when they have the same priority. jACT-R needs to maintain the order so
 * that timed events added by instantiations are fired in the order they are
 * queued.<br>
 * <br>
 * This has three pieces, IPrioritizer<T> which takes a T and returns a double
 * value. When you add an item, its priority is used to determine which
 * collection, in a sorted map of collections, it will be stored in.<br>
 * <br>
 * You call Collection<T> remove(double) to remove all items from the queue
 * that have priorities less than or equal to the value.<br>
 * <b>Note</b>: this is not thread safe
 * 
 * @author developer
 */
public class PrioritizedQueue<T>
{
  /**
   * logger definition
   */
  static private final Log                 LOGGER = LogFactory
                                                      .getLog(PrioritizedQueue.class);

  private IPrioritizer<T>                  _prioritizer;

  private SortedMap<Double, Collection<T>> _backingMap;

  private int                              _size;

  public PrioritizedQueue(IPrioritizer<T> prioritizer)
  {
    _prioritizer = prioritizer;
    _backingMap = new TreeMap<Double, Collection<T>>();
  }
  
  synchronized public void clear()
  {
    _backingMap.clear();
  }

  synchronized public void add(T item)
  {
    double priority = _prioritizer.getPriority(item);
    if (Double.isNaN(priority))
      throw new IllegalArgumentException("Invalid priority of " + item +
          ", must not be NaN");

    Collection<T> itemsOfSamePriority = _backingMap.get(priority);
    if (itemsOfSamePriority == null)
    {
      itemsOfSamePriority = new ArrayList<T>();
      _backingMap.put(priority, itemsOfSamePriority);
    }
    itemsOfSamePriority.add(item);
    _size++;
  }
  
  synchronized public boolean remove(T item)
  {
    double priority = _prioritizer.getPriority(item);
    if (Double.isNaN(priority))
      throw new IllegalArgumentException("Invalid priority of " + item +
          ", must not be NaN");

    Collection<T> itemsOfSamePriority = _backingMap.get(priority);
    if (itemsOfSamePriority == null) return false;
    
    itemsOfSamePriority.remove(item);
    _size--;
    return true;
  }

  /**
   * return all the items of less than or equal to upThrough
   * 
   * @param upThrough
   * @return
   */
  synchronized public void remove(double upThrough, Collection<T> removedEvents)
  {
    if (_backingMap.isEmpty()) return;

    double firstKey = _backingMap.firstKey();

    if (firstKey > upThrough) return;
    
    int originalSize = removedEvents.size();

    if (firstKey != upThrough)
    {
      /*
       * snag collections from lowest to upToPriority(exclusive)
       */
      Iterator<Map.Entry<Double, Collection<T>>> itr = _backingMap.subMap(
          firstKey, upThrough).entrySet().iterator();
      while (itr.hasNext())
      {
        removedEvents.addAll(itr.next().getValue());
        itr.remove();
      }
    }

    /*
     * that took care of everyone from low to priority(exclusive) - now we need
     * priority
     */
    Collection<T> atPriority = _backingMap.remove(upThrough);
    if (atPriority != null) removedEvents.addAll(atPriority);

    _size -= (removedEvents.size()-originalSize);
  }

  /**
   * return all the elements in order of priority (and insertion if priorities
   * are the same)
   * 
   * @return
   */
  synchronized public Collection<T> get()
  {
    ArrayList<T> rtn = new ArrayList<T>();

    if (_backingMap.isEmpty()) return rtn;
    for (Map.Entry<Double, Collection<T>> entry : _backingMap.entrySet())
      rtn.addAll(entry.getValue());

    return rtn;
  }

  /**
   * first key, or NaN if empty
   * 
   * @return
   */
  synchronized public double getFirstPriority()
  {
    if (!_backingMap.isEmpty()) return _backingMap.firstKey();
    return Double.NaN;
  }

  /**
   * first key after afterPriority, or NaN
   * 
   * @param afterPriority
   * @return
   */
  synchronized public double getFirstPriorityAfter(double afterPriority)
  {
    SortedMap<Double, Collection<T>> submap = _backingMap.subMap(_backingMap
        .firstKey(), afterPriority);
    if (!submap.isEmpty()) return submap.firstKey();
    return Double.NaN;
  }

  synchronized public boolean isEmpty()
  {
    return _backingMap.isEmpty();
  }

  public int getSize()
  {
    return _size;
  }
}
