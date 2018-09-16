/*
 * Created on Mar 21, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.util.TreeMap;

import org.jactr.core.module.imaginal.IImaginalModule;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class ImaginalModuleParticipant extends BasicASTParticipant
{
  public ImaginalModuleParticipant()
  {
    super("org/jactr/io/include/imaginal.jactr");
    setInstallableClass(DefaultImaginalModule6.class);
    TreeMap<String, String> parameters = new TreeMap<String,String>();
    parameters.put(IImaginalModule.IMAGINAL_ADD_DELAY_PARAM, "0.2");
    parameters.put(IImaginalModule.IMAGINAL_MODIFY_DELAY_PARAM,"0.2");
    parameters.put(IImaginalModule.IMAGINAL_RANDOMIZE_DELAY_PARAM, "false");
    setParameterMap(parameters);
  }
}
