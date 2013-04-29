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
package org.jactr.modules.temporal.io;

import java.util.Map;
import java.util.TreeMap;

import org.jactr.io.participant.impl.BasicASTParticipant;
import org.jactr.modules.temporal.six.DefaultTemporalModule6;

/**
 * This snippet of code is called by the io tools when parsing detects that
 * the {@link DefaultTemporalModule6} is being imported. It explicitly imports the contents
 * of temporal.jactr and sets some default parameter values. <br>
 * <br>
 * This class is dynamically resolved by the astparticipant extension point in plugin.xml
 * @author developer
 */
public class TemporalModuleParticipant extends BasicASTParticipant
{
  public TemporalModuleParticipant()
  {
    super(TemporalModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/modules/temporal/temporal.jactr"));
    setInstallableClass(DefaultTemporalModule6.class);
    
    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(DefaultTemporalModule6.TIME_MULTIPLIER_PARAM, "1.1");
    parameters.put(DefaultTemporalModule6.TIME_NOISE_PARAM, "0.015");
    parameters.put(DefaultTemporalModule6.TIME_START_PARAM, "0.011");
    
    setParameterMap(parameters);
  }
}
