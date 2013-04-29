package org.jactr.modules.threaded.io;

import org.jactr.io.participant.impl.BasicASTParticipant;
import org.jactr.modules.threaded.goal.DefaultThreadedGoalModule6;

public class ThreadedGoalModuleParticipant extends BasicASTParticipant
{

  public ThreadedGoalModuleParticipant()
  {
    super(ThreadedGoalModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/modules/threaded/io/goal.jactr"));
    setInstallableClass(DefaultThreadedGoalModule6.class);
  }
}
