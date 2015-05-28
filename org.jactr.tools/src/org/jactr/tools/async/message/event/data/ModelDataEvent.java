/*
 * Created on Feb 21, 2007
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
package org.jactr.tools.async.message.event.data;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.async.message.ast.BaseASTMessage;
import org.jactr.tools.async.message.event.IEvent;
/**
 * @author developer
 *
 */
public class ModelDataEvent extends BaseASTMessage implements IEvent,
    Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 4339250385783986279L;

  /**
   logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(ModelDataEvent.class);

  String _modelName;
  
  public ModelDataEvent(IModel model)
  {
    super(ASTResolver.toAST(model, true));
    compressAST();
    _modelName = model.getName();
  }
  
  
  
  public String getModelName()
  {
    return _modelName;
  }

}


