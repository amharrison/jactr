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
import org.jactr.modules.pm.aural.AbstractAuralModule;
import org.jactr.modules.pm.aural.DefaultAuralEncodingTimeEquation;
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

/**
 * @author developer
 */
public class AuralModuleParticipant extends BasicASTParticipant
{
  public AuralModuleParticipant()
  {
    super(AuralModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/modules/pm/aural/aural.jactr"));
    setInstallableClass(DefaultAuralModule6.class);
    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IAsynchronousModule.STRICT_SYNCHRONIZATION_PARAM, "true");
    parameters.put(AbstractAuralModule.ENABLE_BUFFER_STUFF_PARAM, "true");
    parameters.put(AbstractAuralModule.AURAL_DECAY_TIME_PARAM,"3");
    parameters.put(AbstractAuralModule.ENCODING_TIME_EQUATION_PARAM,
        DefaultAuralEncodingTimeEquation.class.getName());
    parameters
        .put(IPerceptualMemory.NEW_FINST_ONSET_DURATION_TIME_PARAM, "0.5");
    parameters.put(IPerceptualMemory.FINST_DURATION_TIME_PARAM, "3");
    parameters.put(IPerceptualMemory.NUMBER_OF_FINSTS_PARAM, "4");
    setParameterMap(parameters);
  }
}
