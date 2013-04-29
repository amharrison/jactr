/*
 * Created on Mar 7, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.tracer.listeners;

import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.tools.tracer.transformer.procedural.ProceduralModuleEventTransformer;

/**
 * @author developer
 */
public class ProceduralModuleTracer extends BaseTraceListener implements
    ITraceListener, IProceduralModuleListener
{
  /**
   * logger definition
   */
  static private final Log LOGGER             = LogFactory
                                                  .getLog(ProceduralModuleTracer.class);

  /**
   * these ignore flags are in place temporarily until the IDE side of things
   * can handle the data. until then, it's just wasted bandwidth
   */
  private boolean          _ignoreConflictSet = false;

  private boolean          _ignoreAdd         = true;

  private boolean          _ignoreWillFire    = true;

  private boolean          _ignoreCreated     = true;

  private boolean          _ignoreFired       = false;

  public ProceduralModuleTracer()
  {
    setEventTransformer(new ProceduralModuleEventTransformer());
  }

  /**
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#conflictSetAssembled(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void conflictSetAssembled(ProceduralModuleEvent pme)
  {
    if (!_ignoreConflictSet) redirectEvent(pme);
  }

  /**
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionAdded(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionAdded(ProceduralModuleEvent pme)
  {
    if (!_ignoreAdd) redirectEvent(pme);
  }

  /**
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionWillFire(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionWillFire(ProceduralModuleEvent pme)
  {
    if (!_ignoreWillFire) redirectEvent(pme);
  }

  /**
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionCreated(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionCreated(ProceduralModuleEvent pme)
  {
    if (!_ignoreCreated) redirectEvent(pme);
  }

  /**
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionFired(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionFired(ProceduralModuleEvent pme)
  {
    if (!_ignoreFired) redirectEvent(pme);
  }

  /**
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionsMerged(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionsMerged(ProceduralModuleEvent pme)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Ignoring merge events");
  }

  /**
   * @see org.jactr.core.event.IParameterListener#parameterChanged(org.jactr.core.event.IParameterEvent)
   */
  public void parameterChanged(IParameterEvent pe)
  {
    // ignore for now
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Ignoring parameter events");
  }

  public void install(IModel model, Executor executor)
  {
    model.getProceduralModule().addListener(this, executor);
  }

  public void uninstall(IModel model)
  {
    model.getProceduralModule().removeListener(this);
  }

}
