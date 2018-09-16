/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.tracer.transformer.model;

import java.io.Serializable;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;

/**
 * @author developer
 */
public class TransformedModelEvent extends AbstractTransformedEvent implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -7699342223139814445L;

  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(TransformedModelEvent.class);

  private ModelEvent.Type _type;
  /**
   * @param source
   * @param systemTime
   * @param simulationTime
   * @param ast
   */
  public TransformedModelEvent(String source, long systemTime,
      double simulationTime, ModelEvent.Type type, CommonTree ast)
  {
    super(source, source, systemTime, simulationTime, ast);
    _type = type;
  }
  
  public ModelEvent.Type getType(){
    return _type;
  }

}
