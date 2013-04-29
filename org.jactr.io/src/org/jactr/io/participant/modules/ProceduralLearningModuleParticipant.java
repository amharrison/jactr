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

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.module.procedural.IProceduralLearningModule;
import org.jactr.core.module.procedural.five.learning.IProceduralLearningModule5;
import org.jactr.core.module.procedural.six.learning.DefaultExpectedUtilityEquation;
import org.jactr.core.module.procedural.six.learning.DefaultProceduralLearningModule6;
import org.jactr.core.module.procedural.six.learning.IProceduralLearningModule6;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class ProceduralLearningModuleParticipant extends BasicASTParticipant
{
  public ProceduralLearningModuleParticipant()
  {
    super((URL) null);
    setInstallableClass(DefaultProceduralLearningModule6.class);
    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IProceduralLearningModule.PARAMETER_LEARNING_RATE, "0.2");
    parameters.put(IProceduralLearningModule6.EXPECTED_UTILITY_EQUATION_PARAM,
        DefaultExpectedUtilityEquation.class.getName());
    parameters.put(DefaultProceduralLearningModule6.INCLUDE_BUFFERS_PARAM,
        "goal, retrieval, imaginal");
    parameters.put(IProceduralLearningModule5.PRODUCTION_COMPILATION_PARAM,
        "false");
    parameters.put(IProceduralLearningModule.OPTIMIZED_LEARNING, "10");
    setParameterMap(parameters);
  }
}
