/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.procedural.event;

import java.util.EventListener;

import org.jactr.core.event.IParameterListener;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;

/**
 * listener for the major procedural module events. These are for listening, not
 * for intercepting and changing.
 * 
 * @author harrison
 */
public interface IProceduralModuleListener extends EventListener,
    IParameterListener
{

  /**
   * called when a production is added to the procedural module
   * 
   * @param pme
   */
  public void productionAdded(ProceduralModuleEvent pme);

  /**
   * called after the conflict set is assembled.
   * {@link ProceduralModuleEvent#getProductions()} actually contains the
   * {@link IInstantiation}s of the individual {@link IProduction}s.
   * 
   * @param pme
   */
  public void conflictSetAssembled(ProceduralModuleEvent pme);

  /**
   * called when a production has been selected to fire.
   * {@link ProceduralModuleEvent#getProduction()} actually returns the
   * {@link IInstantiation} of the {@link IProduction}.
   * 
   * @param pme
   */
  public void productionWillFire(ProceduralModuleEvent pme);

  /**
   * called after a production has been fired
   * {@link ProceduralModuleEvent#getProduction()} actually returns the
   * {@link IInstantiation} of the {@link IProduction}.
   * 
   * @param pme
   */
  public void productionFired(ProceduralModuleEvent pme);

  /**
   * called when a production has been created, but not necessarily configured
   * with all its symbolic/subsymbolic contents
   */
  public void productionCreated(ProceduralModuleEvent pme);

  /**
   * called when two productions have been merged into one. pme.getProductions()
   * will return handles for both the original (unchanged) and the mergie
   * (changed)
   */
  public void productionsMerged(ProceduralModuleEvent pme);
}
