/*
 * Created on Apr 12, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.iterative.message;

import java.io.Serializable;

import org.jactr.tools.async.message.BaseMessage;

/**
 * @author developer
 */
public class StatusMessage extends BaseMessage implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = -8802789743347290301L;

  private boolean _isStart;

  private long    _when;

  private int     _currentIndex;

  private int     _totalRuns;

  public StatusMessage(boolean isStart, int currentIndex, int totalRuns)
  {
    super();
    _isStart = isStart;
    _when = System.currentTimeMillis();
    _totalRuns = totalRuns;
    _currentIndex = currentIndex;
  }

  public long getWhen()
  {
    return _when;
  }

  public boolean isStart()
  {
    return _isStart;
  }

  public boolean isStop()
  {
    return !_isStart;
  }

  public int getIteration()
  {
    return _currentIndex;
  }

  public int getTotalIterations()
  {
    return _totalRuns;
  }
}
