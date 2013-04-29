/*
 * Created on Jul 10, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author developer
 */
public abstract class AbstractFactory<T> implements IFactory<T>
{
  
  final private int        _maximumPoolSize;

  final private List<T>    _pool;

  public AbstractFactory()
  {
    this(10, false);
  }

  public AbstractFactory(int maximumPoolSize)
  {
    this(maximumPoolSize, false);
  }

  public AbstractFactory(int maximumPoolSize, boolean precreate)
  {
    _maximumPoolSize = maximumPoolSize;
    _pool = new ArrayList<T>(_maximumPoolSize);
    if (precreate) for (int i = 0; i < _maximumPoolSize; i++)
      _pool.add(create());
  }

  /**
   * @see org.jactr.core.factory.IFactory#acquireInstance()
   */
  synchronized public T acquireInstance()
  {
    T rtn = null;

    if (_pool.size() != 0)
      rtn = _pool.remove(0);
    else
      rtn = create();

    return rtn;
  }

  synchronized public T acquireInstance(T template)
  {
    T rtn = acquireInstance();
    set(rtn, template);
    return rtn;
  }

  /**
   * @see org.jactr.core.factory.IFactory#releaseInstance(java.lang.Object)
   */
  synchronized public void releaseInstance(T t)
  {
    if (_pool.size() < _maximumPoolSize)
    {
      reset(t);
      _pool.add(t);
    }
    else
      destroy(t);
  }

  /**
   * create an instance of T.
   * 
   * @param template
   * @return
   */
  abstract protected T create();

  /**
   * reset an instance
   * 
   * @param t
   */
  abstract protected void reset(T t);

  /**
   * set the values of newInstance to those of template
   * 
   * @param newInstance
   * @param template
   */
  abstract protected void set(T newInstance, T template);

  abstract protected void destroy(T t);

}
