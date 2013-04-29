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

import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;
import org.jactr.core.module.retrieval.six.DeclarativeFINSTManager;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class RetrievalModuleParticipant extends BasicASTParticipant
{
  public RetrievalModuleParticipant()
  {
    super(RetrievalModuleParticipant.class.getClassLoader().getResource(
        "org/jactr/io/include/retrieval.jactr"));
    setInstallableClass(DefaultRetrievalModule6.class);
    Map<String, String> parameters = new TreeMap<String, String>();
    parameters.put(IRetrievalModule.RETRIEVAL_THRESHOLD, "0");
    parameters.put(IRetrievalModule4.LATENCY_FACTOR, "1");
    // parameters.put(IRetrievalModule4.LATENCY_EXPONENT, "1");
    parameters.put(DefaultRetrievalModule6.INDEXED_RETRIEVALS_ENABLED_PARAM, "false");
    parameters.put(DeclarativeFINSTManager.FINST_DURATION_PARAM, "3.0");
    parameters.put(DeclarativeFINSTManager.NUMBER_OF_FINSTS_PARAM, "4");
    setParameterMap(parameters);
  }
}
