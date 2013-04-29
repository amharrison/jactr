/*
 * Created on Mar 16, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.participant.modules;

import org.jactr.core.module.goal.six.DefaultGoalModule6;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class GoalModuleParticipant extends BasicASTParticipant
{
  public GoalModuleParticipant()
  {
    super(GoalModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/io/include/goal.jactr"));
    setInstallableClass(DefaultGoalModule6.class);
  }
}
