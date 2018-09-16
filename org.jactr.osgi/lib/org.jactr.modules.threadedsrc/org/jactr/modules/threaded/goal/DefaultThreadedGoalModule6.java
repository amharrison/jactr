package org.jactr.modules.threaded.goal;

import java.util.ArrayList;
import java.util.Collection;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.misc.ProxyActivationBuffer;
import org.jactr.core.module.goal.six.DefaultGoalModule6;
import org.jactr.modules.threaded.goal.buffer.DefaultCapacityGoalBuffer6;

public class DefaultThreadedGoalModule6 extends DefaultGoalModule6
{

  public DefaultThreadedGoalModule6()
  {
    
  }

 
  protected @Override
  Collection<IActivationBuffer> createBuffers()
  {
    IActivationBuffer goalBuffer = new DefaultCapacityGoalBuffer6(
        IActivationBuffer.GOAL, this);
    setGoalBuffer(goalBuffer);

    IActivationBuffer proxyBuffer = new ProxyActivationBuffer("other-goal",
        getModel(), this, goalBuffer);

    ArrayList<IActivationBuffer> buffs = new ArrayList<IActivationBuffer>();
    buffs.add(goalBuffer);
    buffs.add(proxyBuffer);
    return buffs;
  }
}
