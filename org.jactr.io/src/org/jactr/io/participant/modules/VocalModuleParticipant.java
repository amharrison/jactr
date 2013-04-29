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

import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.io.participant.impl.BasicASTParticipant;
import org.jactr.modules.pm.vocal.six.DefaultVocalModule6;

/**
 * @author developer
 */
public class VocalModuleParticipant extends BasicASTParticipant
{
  public VocalModuleParticipant()
  {
    super(VocalModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/modules/pm/vocal/vocal.jactr"));
    setInstallableClass(DefaultVocalModule6.class);
    
    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IAsynchronousModule.STRICT_SYNCHRONIZATION_PARAM, "true");
    parameters.put(DefaultVocalModule6.CHARACTERS_PER_SYLLABLE_PARAM, "3");
    parameters.put(DefaultVocalModule6.SYLLABLE_RATE_PARAM, "6.66");
    parameters.put(DefaultVocalModule6.PROCESSING_TIME_PARAM, "0.05");
    parameters.put(DefaultVocalModule6.EMPTY_PREPARATION_TIME_PARAM, "0.15");
    parameters.put(DefaultVocalModule6.DIFFERENT_PREPARATION_TIME_PARAM, "0.1");
    parameters.put(DefaultVocalModule6.SAME_PREPARATION_TIME_PARAM, "0");
    
    setParameterMap(parameters);
  }
}
