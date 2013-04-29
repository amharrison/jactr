/*
 * Created on Jun 27, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.aural.six;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.aural.AbstractAuralModule;
import org.jactr.modules.pm.aural.audicon.DefaultAudicon;
import org.jactr.modules.pm.aural.audicon.IAudicon;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.aural.buffer.six.DefaultAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.six.DefaultAuralLocationBuffer;
import org.jactr.modules.pm.common.afferent.DefaultAfferentObjectListener;
import org.jactr.modules.pm.common.afferent.IAfferentObjectListener;

/**
 * @author developer
 */
public class DefaultAuralModule6 extends AbstractAuralModule
{
  /**
   * logger definition
   */
  static private final Log              LOGGER = LogFactory
                                                   .getLog(DefaultAuralModule6.class);

  private DefaultAfferentObjectListener _afferentListener;

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#connectToCommonReality()
   */
  @Override
  protected void connectToCommonReality()
  {
    super.connectToCommonReality();
    IAgent agentInterface = ACTRRuntime.getRuntime().getConnector().getAgent(
        getModel());

    _afferentListener = new DefaultAfferentObjectListener(agentInterface,
        getCommonRealityExecutor());

    /*
     * both the chunk encoders and the feature maps need to be aware of afferent
     * information. feature maps first.
     */
    for (IAfferentObjectListener listener : ((DefaultAudicon) getAudicon())
        .getFeatureMaps())
      _afferentListener.add(listener);

//    for (IAfferentObjectListener listener : ((DefaultAudicon) getAudicon())
//        .getEncoders())
//      _afferentListener.add(listener);

    /*
     * and all the work gets done on the common reality executor
     */
    agentInterface.getAfferentObjectManager().addListener(_afferentListener,
        ExecutorServices.INLINE_EXECUTOR);
  }

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#createAudicon()
   */
  @Override
  protected IAudicon createAudicon()
  {
    return new DefaultAudicon(this);
  }

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#createAuralBuffer(org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer)
   */
  @Override
  protected IAuralActivationBuffer createAuralBuffer(
      IAuralLocationBuffer locationBuffer)
  {
    return new DefaultAuralActivationBuffer(this);
  }

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#createAuralLocationBuffer()
   */
  @Override
  protected IAuralLocationBuffer createAuralLocationBuffer()
  {
    return new DefaultAuralLocationBuffer(this);
  }

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#disconnectFromCommonReality()
   */
  @Override
  protected void disconnectFromCommonReality()
  {
    super.disconnectFromCommonReality();
    IAgent agentInterface = ACTRRuntime.getRuntime().getConnector().getAgent(
        getModel());

    agentInterface.getAfferentObjectManager().removeListener(_afferentListener);
    _afferentListener = null;
  }

}
