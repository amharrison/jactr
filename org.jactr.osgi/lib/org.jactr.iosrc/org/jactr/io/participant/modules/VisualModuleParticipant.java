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
import org.jactr.modules.pm.visual.AbstractVisualModule;
import org.jactr.modules.pm.visual.memory.IVisualMemory;
import org.jactr.modules.pm.visual.memory.impl.DefaultVisualMemory;
import org.jactr.modules.pm.visual.six.DefaultEncodingTimeEquation;
import org.jactr.modules.pm.visual.six.DefaultSearchTimeEquation;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;

/**
 * @author developer
 */
public class VisualModuleParticipant extends BasicASTParticipant
{
  public VisualModuleParticipant()
  {
    super(VisualModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/modules/pm/visual/visual.jactr"));
    setInstallableClass(DefaultVisualModule6.class);
    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IAsynchronousModule.STRICT_SYNCHRONIZATION_PARAM, "true");
    parameters.put(AbstractVisualModule.ENABLE_BUFFER_STUFF_PARAM, "false");
    parameters.put(IVisualMemory.VISUAL_FIELD_WIDTH_PARAM, "160");
    parameters.put(IVisualMemory.VISUAL_FIELD_HORIZONTAL_RESOLUTION_PARAM,
        "160");
    parameters.put(IVisualMemory.VISUAL_FIELD_VERTICAL_RESOLUTION_PARAM, "120");
    parameters.put(IVisualMemory.VISUAL_FIELD_HEIGHT_PARAM, "120");
    parameters.put(IVisualMemory.NEW_FINST_ONSET_DURATION_TIME_PARAM, "0.5");
    parameters.put(IVisualMemory.FINST_DURATION_TIME_PARAM, "3");
    parameters.put(IVisualMemory.NUMBER_OF_FINSTS_PARAM, "4");
    parameters.put(IVisualMemory.MOVEMENT_TOLERANCE_PARAM, "0.5");
    parameters.put(IVisualMemory.STICKY_ATTENTION_PARAM, "false");
    parameters.put(DefaultVisualMemory.VISUAL_PESISTENCE_DELAY_PARAM, "0");
    parameters.put(AbstractVisualModule.ENCODING_TIME_EQUATION_PARAM,
        DefaultEncodingTimeEquation.class.getName());
    parameters.put(AbstractVisualModule.SEARCHING_TIME_EQUATION_PARAM,
        DefaultSearchTimeEquation.class.getName());
    setParameterMap(parameters);
  }
}
