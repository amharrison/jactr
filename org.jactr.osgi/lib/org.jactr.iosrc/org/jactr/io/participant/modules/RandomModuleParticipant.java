/*
 * Created on Apr 15, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.net.URL;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.module.random.six.DefaultRandomModule;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class RandomModuleParticipant extends BasicASTParticipant
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(RandomModuleParticipant.class);

  public RandomModuleParticipant()
  {
    super((URL) null);
    setInstallableClass(DefaultRandomModule.class);
    TreeMap<String, String> parameters = new TreeMap<String, String>();
    // can't use this to set a random value since this is a singleton object
    parameters.put(IRandomModule.SEED_PARAM, "");
    parameters.put(IRandomModule.RANDOM_TIME_PARAM, "3");
    setParameterMap(parameters);
  }
}
