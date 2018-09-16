/*
 * Created on Feb 22, 2007
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
package org.jactr.tools.tracer.transformer;

import java.io.Serializable;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.async.message.ast.BaseASTMessage;
/**
 * @author developer
 *
 */
public class AbstractTransformedEvent extends BaseASTMessage implements
    ITransformedEvent, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 8564388993537455160L;


  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(AbstractTransformedEvent.class);

  
  private double _simulationTime;
  private long _systemTime;
  private String _sourceName;
  private String _modelName;
  
  
  public AbstractTransformedEvent(String modelName, String source, long systemTime, double simulationTime, CommonTree ast)
  {
    super(ast);
    // compressAST();
    _modelName = modelName;
    _sourceName = source;
    _simulationTime = simulationTime;
    _systemTime = systemTime;
  }
  
  /**
   * @see org.jactr.tools.tracer.transformer.ITransformedEvent#getSimulationTime()
   */
  public double getSimulationTime()
  {
    return _simulationTime;
  }

  /**
   * @see org.jactr.tools.tracer.transformer.ITransformedEvent#getSource()
   */
  public String getSource()
  {
    return _sourceName;
  }
  
  public String getModelName()
  {
    return _modelName;
  }

  /**
   * @see org.jactr.tools.tracer.transformer.ITransformedEvent#getSystemTime()
   */
  public long getSystemTime()
  {
    return _systemTime;
  }

}


