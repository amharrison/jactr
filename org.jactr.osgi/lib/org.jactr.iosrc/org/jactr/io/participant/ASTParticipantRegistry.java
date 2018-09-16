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
package org.jactr.io.participant;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.declarative.four.learning.DefaultDeclarativeLearningModule4;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.core.module.goal.six.DefaultGoalModule6;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.module.procedural.six.learning.DefaultProceduralLearningModule6;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.io.participant.modules.AuralModuleParticipant;
import org.jactr.io.participant.modules.DeclarativeLearningModuleParticipant4;
import org.jactr.io.participant.modules.DeclarativeModuleParticipant;
import org.jactr.io.participant.modules.GoalModuleParticipant;
import org.jactr.io.participant.modules.ImaginalModuleParticipant;
import org.jactr.io.participant.modules.MotorModuleParticipant;
import org.jactr.io.participant.modules.ProceduralLearningModuleParticipant;
import org.jactr.io.participant.modules.ProceduralModuleParticipant;
import org.jactr.io.participant.modules.RetrievalModuleParticipant;
import org.jactr.io.participant.modules.VisualModuleParticipant;
import org.jactr.io.participant.modules.VocalModuleParticipant;
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.motor.six.DefaultMotorModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;
import org.jactr.modules.pm.vocal.six.DefaultVocalModule6;

/**
 * Central point for finding IASTParticipants for a given class
 * 
 * @author developer
 */
public class ASTParticipantRegistry
{

  /**
   * Logger definition
   */

  static private final transient Log          LOGGER           = LogFactory
                                                                   .getLog(ASTParticipantRegistry.class);

  static private Map<String, IASTParticipant> _astParticipants = new HashMap<String, IASTParticipant>();

  static
  {
    /*
     * we do this just in case the activator is not called, which could happen
     * if this code is executed w/o the eclipse/osgi environment
     */
    addParticipant(DefaultDeclarativeModule6.class.getName(),
        new DeclarativeModuleParticipant());

    addParticipant(DefaultProceduralModule6.class.getName(),
        new ProceduralModuleParticipant());

    addParticipant(DefaultGoalModule6.class.getName(),
        new GoalModuleParticipant());

    addParticipant(DefaultImaginalModule6.class.getName(),
        new ImaginalModuleParticipant());

    addParticipant(DefaultRetrievalModule6.class.getName(),
        new RetrievalModuleParticipant());

    addParticipant(DefaultVisualModule6.class.getName(),
        new VisualModuleParticipant());

    addParticipant(DefaultAuralModule6.class.getName(),
        new AuralModuleParticipant());

    addParticipant(DefaultVocalModule6.class.getName(),
        new VocalModuleParticipant());

    addParticipant(DefaultMotorModule6.class.getName(),
        new MotorModuleParticipant());

    addParticipant(DefaultDeclarativeLearningModule4.class.getName(),
        new DeclarativeLearningModuleParticipant4());

    addParticipant(DefaultProceduralLearningModule6.class.getName(),
        new ProceduralLearningModuleParticipant());
  }

  static public void addParticipant(String moduleClass,
      IASTParticipant participant)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding participant " + participant + " for " + moduleClass);
    synchronized (_astParticipants)
    {
      _astParticipants.put(moduleClass, participant);
    }
  }

  static public IASTParticipant removeParticipant(String moduleClass)
  {
    synchronized (_astParticipants)
    {
      return _astParticipants.remove(moduleClass);
    }
  }

  static public IASTParticipant getParticipant(String moduleClass)
  {
    IASTParticipant participant = null;
    synchronized (_astParticipants)
    {
      participant = _astParticipants.get(moduleClass);
    }
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Returning " + participant + " for " + moduleClass);
    return participant;
  }

  static public boolean hasParticipant(String moduleClass)
  {
    return getParticipant(moduleClass) != null;
  }
}
