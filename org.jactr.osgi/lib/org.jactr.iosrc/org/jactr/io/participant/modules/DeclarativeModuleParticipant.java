/*
 * Created on Mar 16, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.participant.modules;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.module.declarative.basic.DefaultDeclarativeModule;
import org.jactr.core.module.declarative.basic.chunk.DefaultChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.DefaultSubsymbolicChunkFactory5;
import org.jactr.core.module.declarative.basic.chunk.DefaultSymbolicChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.NoOpChunkConfigurator;
import org.jactr.core.module.declarative.basic.chunk.NoOpChunkNamer;
import org.jactr.core.module.declarative.basic.type.DefaultChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.DefaultSubsymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.DefaultSymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.NoOpChunkTypeConfigurator;
import org.jactr.core.module.declarative.basic.type.NoOpChunkTypeNamer;
import org.jactr.core.module.declarative.five.IDeclarativeModule5;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class DeclarativeModuleParticipant extends BasicASTParticipant
{
  public DeclarativeModuleParticipant()
  {
    super(DeclarativeModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/io/include/declarative.jactr"));
    setInstallableClass(DefaultDeclarativeModule6.class);

    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IDeclarativeModule4.PARTIAL_MATCHING, "false");
    parameters.put(IDeclarativeModule4.ACTIVATION_NOISE, "0");
    parameters.put(IDeclarativeModule4.PERMANENT_ACTIVATION_NOISE, "0");
    parameters.put(IDeclarativeModule4.BASE_LEVEL_CONSTANT, "0");
    parameters.put(IDeclarativeModule5.MISMATCH_PENALTY, "1");
    parameters.put(IDeclarativeModule5.MAXIMUM_SIMILARITY, "0");
    parameters.put(IDeclarativeModule5.MAXIMUM_DIFFERENCE, "-1");

    parameters.put(DefaultDeclarativeModule.CHUNK_FACTORY_PARAM,
        DefaultChunkFactory.class.getName());
    parameters.put(DefaultDeclarativeModule.SYMBOLIC_CHUNK_FACTORY_PARAM,
        DefaultSymbolicChunkFactory.class.getName());
    parameters.put(DefaultDeclarativeModule.SUBSYMBOLIC_CHUNK_FACTORY_PARAM,
        DefaultSubsymbolicChunkFactory5.class.getName());
    parameters.put(DefaultDeclarativeModule.CHUNK_NAMER_PARAM,
        NoOpChunkNamer.class.getName());
    parameters.put(DefaultDeclarativeModule.CHUNK_CONFIGURATOR_PARAM,
        NoOpChunkConfigurator.class.getName());

    parameters.put(DefaultDeclarativeModule.CHUNK_TYPE_FACTORY_PARAM,
        DefaultChunkTypeFactory.class.getName());
    parameters.put(DefaultDeclarativeModule.SYMBOLIC_CHUNK_TYPE_FACTORY_PARAM,
        DefaultSymbolicChunkTypeFactory.class.getName());
    parameters.put(
        DefaultDeclarativeModule.SUBSYMBOLIC_CHUNK_TYPE_FACTORY_PARAM,
        DefaultSubsymbolicChunkTypeFactory.class.getName());
    parameters.put(DefaultDeclarativeModule.CHUNK_TYPE_NAMER_PARAM,
        NoOpChunkTypeNamer.class.getName());
    parameters.put(DefaultDeclarativeModule.CHUNK_TYPE_CONFIGURATOR_PARAM,
        NoOpChunkTypeConfigurator.class.getName());

    setParameterMap(parameters);
  }
}
