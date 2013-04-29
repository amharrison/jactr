/*
 * Created on Nov 30, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.procedural.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IParameterEvent;
/**
 * @author developer
 *
 */
public class ProceduralModuleListenerAdaptor implements
    IProceduralModuleListener
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ProceduralModuleListenerAdaptor.class);

  /** 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#conflictSetAssembled(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void conflictSetAssembled(ProceduralModuleEvent pme)
  {
  }

  /** 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionAdded(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionAdded(ProceduralModuleEvent pme)
  {

  }

  /** 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionWillFire(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionWillFire(ProceduralModuleEvent pme)
  {

  }

  /** 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionCreated(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionCreated(ProceduralModuleEvent pme)
  {

  }

  /** 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionFired(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionFired(ProceduralModuleEvent pme)
  {

  }

  /** 
   * @see org.jactr.core.module.procedural.event.IProceduralModuleListener#productionsMerged(org.jactr.core.module.procedural.event.ProceduralModuleEvent)
   */
  public void productionsMerged(ProceduralModuleEvent pme)
  {

  }

  /** 
   * @see org.jactr.core.event.IParameterListener#parameterChanged(org.jactr.core.event.IParameterEvent)
   */
  @SuppressWarnings("unchecked")
  public void parameterChanged(IParameterEvent pe)
  {

  }

}


