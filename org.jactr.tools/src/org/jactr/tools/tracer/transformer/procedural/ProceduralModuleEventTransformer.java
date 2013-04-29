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
package org.jactr.tools.tracer.transformer.procedural;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IACTREvent;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.production.IProduction;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.tracer.transformer.IEventTransformer;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

/**
 * @author developer
 */
public class ProceduralModuleEventTransformer implements IEventTransformer
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ProceduralModuleEventTransformer.class);

  /**
   * @see org.jactr.tools.tracer.transformer.IEventTransformer#transform(org.jactr.core.event.IACTREvent)
   */
  public ITransformedEvent transform(IACTREvent actrEvent)
  {
    ProceduralModuleEvent pme = (ProceduralModuleEvent) actrEvent;

    ProceduralModuleEvent.Type type = pme.getType();
    String modelName = pme.getSource().getModel().getName();
    long actualTime = pme.getSystemTime();
    double simTime = pme.getSimulationTime();
    CommonTree data = null;

    /*
     * figure out what data should be..
     */
    switch (type)
    {
      case PARAMETER_CHANGED:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("We are ignoring parameter events");
        break;
      case PRODUCTION_CREATED:
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Ignoring production creation");
        break;
      case PRODUCTION_FIRED:
      case PRODUCTION_WILL_FIRE:
      case PRODUCTION_ADDED:
        data = ASTResolver.toAST(pme.getProduction());
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Transformed production " + pme.getProduction()
              + " into " + data);
        break;
      case PRODUCTIONS_MERGED:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("We are ignoring production merges");
        break;
      /*
       * snag the instantiated productions and wrap them in a procedural memory
       * node
       */
      case CONFLICT_SET_ASSEMBLED:
        ASTSupport support = new ASTSupport();
        data = support.createTree(JACTRBuilder.PROCEDURAL_MEMORY,
            "conflict set");
        for (IProduction instantiation : pme.getProductions())
          data.addChild(ASTResolver.toAST(instantiation));
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("conflict set " + pme.getProductions()
              + " transformed into " + data);
        break;
    }

    return new TransformedProceduralEvent(modelName, actualTime, simTime, type,
        data);
  }

}
