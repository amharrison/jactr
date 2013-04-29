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
package org.jactr.tools.tracer.transformer.model;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IACTREvent;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.tracer.transformer.IEventTransformer;
import org.jactr.tools.tracer.transformer.ITransformedEvent;
/**
 * @author developer
 *
 */
public class ModelEventTransformer implements IEventTransformer
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ModelEventTransformer.class);

  /** 
   * @see org.jactr.tools.tracer.transformer.IEventTransformer#transform(IACTREvent)
   */
  public ITransformedEvent transform(IACTREvent actrEvent)
  {
    ModelEvent event = (ModelEvent) actrEvent;
    
    String source = event.getSource().getName();
    long system = event.getSystemTime();
    double time = event.getSimulationTime();
    ModelEvent.Type type = event.getType();
    CommonTree payload = null;
    
    switch(type)
    {
      case BUFFER_INSTALLED : payload = ASTResolver.toAST(event.getBuffer()); break;
      case EXTENSION_INSTALLED : payload = ASTResolver.toAST(event.getExtension()); break;
      case MODULE_INSTALLED : payload = ASTResolver.toAST(event.getModule());break;
//      case INSTRUMENT_INSTALLED : payload = ASTResolver.toAST(event.getInstrument());break;
      default:
        if (LOGGER.isDebugEnabled()) LOGGER.debug("No payload transform available for "+type);
    }
    
    return new TransformedModelEvent(source, system, time, type, payload);
  }

}


