/*
 * Created on Mar 7, 2007
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
package org.jactr.tools.tracer.transformer.procedural;

import java.io.Serializable;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;
/**
 * @author developer
 *
 */
public class TransformedProceduralEvent extends AbstractTransformedEvent implements Serializable
{
  
  
  /**
   * 
   */
  private static final long serialVersionUID = -5862795028052594101L;
  private ProceduralModuleEvent.Type _type;
  
  /**
   * @param source
   * @param systemTime
   * @param simulationTime
   * @param ast
   */
  public TransformedProceduralEvent(String modelName, long systemTime, double simulationTime, ProceduralModuleEvent.Type type, CommonTree ast)
  {
    super(modelName, modelName, systemTime, simulationTime, ast);
    _type = type;
  }
  
  public ProceduralModuleEvent.Type getType()
  {
    return _type;
  }

}


