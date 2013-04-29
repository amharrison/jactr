/*
 * Created on Aug 11, 2005 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.goal.six;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.goal.IGoalModule;
import org.jactr.core.module.goal.six.buffer.DefaultGoalBuffer6;

/**
 * handles the goal and imaginal buffers
 * 
 * @author developer
 */
public class DefaultGoalModule6 extends AbstractModule implements IGoalModule
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultGoalModule6.class);

  private IActivationBuffer          _goalBuffer;

  public DefaultGoalModule6()
  {
    super("goal");
  }

  protected @Override
  Collection<IActivationBuffer> createBuffers()
  {
    IActivationBuffer goalBuffer = new DefaultGoalBuffer6(
        IActivationBuffer.GOAL, this);
    setGoalBuffer(goalBuffer);
    ArrayList<IActivationBuffer> buffs = new ArrayList<IActivationBuffer>();
    buffs.add(goalBuffer);
    return buffs;
  }

  protected void setGoalBuffer(IActivationBuffer buffer)
  {
    _goalBuffer = buffer;
  }

  @Override
  public void initialize()
  {
  }

  @Override
  public void dispose()
  {
    super.dispose();
    _goalBuffer.dispose();
    _goalBuffer = null;
  }

  public void reset()
  {
    _goalBuffer.clear();
  }
}
