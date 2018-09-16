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
package org.jactr.tools.tracer.transformer.declarative;

import java.io.Serializable;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.tools.async.message.IMessage;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;
/**
 * @author developer
 *
 */
public class TransformedDeclarativeModuleEvent extends AbstractTransformedEvent
    implements IMessage, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1747334896625017333L;


  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(TransformedDeclarativeModuleEvent.class);

  
  private DeclarativeModuleEvent.Type _type;
  /**
   * @param source
   * @param systemTime
   * @param simulationTime
   * @param ast
   */
  public TransformedDeclarativeModuleEvent(String source, long systemTime,
      double simulationTime, DeclarativeModuleEvent.Type type, CommonTree ast)
  {
    super(source, source, systemTime, simulationTime, ast);
    _type = type;
  }

  public DeclarativeModuleEvent.Type getType()
  {
    return _type;
  }
}


