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

import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.io.participant.impl.BasicASTParticipant;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.command.translators.AbstractManualTranslator;
import org.jactr.modules.pm.motor.command.translators.PeckRecoilTranslator;
import org.jactr.modules.pm.motor.command.translators.PeckTranslator;
import org.jactr.modules.pm.motor.command.translators.PunchTranslator;
import org.jactr.modules.pm.motor.six.DefaultMotorModule6;

/**
 * @author developer
 */
public class MotorModuleParticipant extends BasicASTParticipant
{
  public MotorModuleParticipant()
  {
    super(MotorModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/modules/pm/motor/motor.jactr"));
    setInstallableClass(DefaultMotorModule6.class);
    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IAsynchronousModule.STRICT_SYNCHRONIZATION_PARAM, "true");
    parameters.put(AbstractMotorModule.ENABLE_PARALLEL_MUSCLES_PARAM, "false");
    parameters.put(AbstractManualTranslator.MINIMUM_FITTS_TIME,"0.1");
    parameters.put(AbstractManualTranslator.MINIMUM_MOVEMENT_TIME,"0.05");
    parameters.put(AbstractManualTranslator.PECK_FITTS_COEFFICIENT,"0.075");
    parameters.put(PunchTranslator.class.getName(), "true");
    parameters.put(PeckTranslator.class.getName(), "true");
    parameters.put(PeckRecoilTranslator.class.getName(), "true");
    
    setParameterMap(parameters);
  }
}
