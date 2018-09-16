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

import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.concurrent.ModelCycleExecutor;
import org.jactr.core.logging.Logger;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.modules.pm.aural.AbstractAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.aural.buffer.six.DefaultAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.six.DefaultAuralLocationBuffer;
import org.jactr.modules.pm.aural.delegate.AuralAttendingDelegate;
import org.jactr.modules.pm.aural.delegate.AuralSearchDelegate;
import org.jactr.modules.pm.aural.event.AuralModuleEvent;
import org.jactr.modules.pm.aural.memory.IAuralMemory;
import org.jactr.modules.pm.aural.memory.impl.DefaultAuralMemory;
import org.jactr.modules.pm.aural.memory.impl.DefaultPerceptListener;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;

/**
 * @author developer
 */
public class DefaultAuralModule6 extends AbstractAuralModule
{
  /**
   * logger definition
   */
  static private final Log       LOGGER = LogFactory
                                            .getLog(DefaultAuralModule6.class);

  private IActivePerceptListener _perceptListener;

  private AuralAttendingDelegate _attendingDelegate;

  private AuralSearchDelegate    _searchDelegate;

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#createAudicon()
   */

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#createAuralBuffer(org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer)
   */
  @Override
  protected IAuralActivationBuffer createAuralBuffer(
      IAuralLocationBuffer locationBuffer)
  {
    return new DefaultAuralActivationBuffer(this, locationBuffer);
  }

  /**
   * @see org.jactr.modules.pm.aural.AbstractAuralModule#createAuralLocationBuffer()
   */
  @Override
  protected IAuralLocationBuffer createAuralLocationBuffer()
  {
    return new DefaultAuralLocationBuffer(this);
  }

  @Override
  public void initialize()
  {
    super.initialize();
    getModel();

    _attendingDelegate = new AuralAttendingDelegate(this);
    _searchDelegate = new AuralSearchDelegate(this);
  }

  @Override
  protected IAuralMemory createAuralMemory()
  {
    _perceptListener = new DefaultPerceptListener(this);

    IAuralMemory memory = new DefaultAuralMemory(this, _perceptListener);
    // do most of the processing at the top of the cycle
    memory.addListener(_perceptListener, new ModelCycleExecutor(getModel(),
        ModelCycleExecutor.When.BEFORE));

    return memory;
  }

  public Future<IChunk> attendTo(PerceptualSearchResult result,
      double requestTime)
  {
    return _attendingDelegate.process((result != null ? result.getRequest()
        : null), requestTime, result);
  }

  public Future<PerceptualSearchResult> search(ChunkTypeRequest request,
      double requestTime, boolean isStuffRequest)
  {
    return _searchDelegate.process(request, requestTime, isStuffRequest);
  }

  public void reset(boolean resetFINSTs)
  {
    if (resetFINSTs) getAuralMemory().getFINSTFeatureMap().reset();

    getAuralLocationBuffer().clear();
    getAuralActivationBuffer().clear();

    if (Logger.hasLoggers(getModel()))
      Logger.log(getModel(), Logger.Stream.AURAL, "Reset aural");

    if (hasListeners())
      dispatch(new AuralModuleEvent(this, AuralModuleEvent.Type.RESET));
  }

  public void reset()
  {
    reset(false);
  }

}
