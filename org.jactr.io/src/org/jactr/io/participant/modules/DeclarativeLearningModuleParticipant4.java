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

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.module.declarative.five.IDeclarativeModule5;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.declarative.four.learning.DefaultDeclarativeLearningModule4;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class DeclarativeLearningModuleParticipant4 extends BasicASTParticipant
{
  public DeclarativeLearningModuleParticipant4()
  {
    super((URL)null);
    setInstallableClass(DefaultDeclarativeLearningModule4.class);

    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IDeclarativeLearningModule4.BASE_LEVEL_LEARNING_RATE, "0.5");
    parameters.put(IDeclarativeLearningModule4.ASSOCIATIVE_LEARNING_RATE, "1");
    parameters.put(IDeclarativeLearningModule4.OPTIMIZED_LEARNING, "10");
    setParameterMap(parameters);
  }
}
