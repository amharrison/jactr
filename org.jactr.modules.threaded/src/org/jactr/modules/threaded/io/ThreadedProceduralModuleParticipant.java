package org.jactr.modules.threaded.io;

import org.jactr.io.participant.modules.ProceduralModuleParticipant;
import org.jactr.modules.threaded.procedural.DefaultThreadedProceduralModule6;

public class ThreadedProceduralModuleParticipant extends
    ProceduralModuleParticipant
{

  public ThreadedProceduralModuleParticipant()
  {
    super();
    setInstallableClass(DefaultThreadedProceduralModule6.class);
  }

}
