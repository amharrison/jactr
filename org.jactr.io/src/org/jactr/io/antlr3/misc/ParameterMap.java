/*
 * Created on May 31, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.misc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.five.IDeclarativeModule5;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.module.procedural.IProceduralLearningModule;
import org.jactr.core.module.procedural.four.IProceduralModule4;
import org.jactr.core.module.procedural.four.learning.IProceduralLearningModule4;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;
import org.jactr.core.production.ISubsymbolicProduction;
import org.jactr.core.production.six.ISubsymbolicProduction6;

public class ParameterMap
{
  /**
   * logger definition
   */
  static public final Log            LOGGER       = LogFactory
                                                      .getLog(ParameterMap.class);

  static private Map<String, Pair>   _lispToJactr = new HashMap<String, Pair>();

  static private Map<String, String> _jactrToLisp = new HashMap<String, String>();

  static
  {
    // chunk parameters
    addParameter(":activation", ISubsymbolicChunk.ACTIVATION, null);
    addParameter(":base-level", ISubsymbolicChunk.BASE_LEVEL_ACTIVATION, null);
    addParameter(":source-spread", ISubsymbolicChunk.SPREADING_ACTIVATION, null);
    addParameter(":source", ISubsymbolicChunk.SOURCE_ACTIVATION, null);
    addParameter(":sjis", ISubsymbolicChunk4.LINKS, null);
    addParameter(":similarities", ISubsymbolicChunk5.SIMILARITIES, null);
    addParameter(":references", ISubsymbolicChunk.REFERENCE_TIMES, null);
    addParameter(":permanent-noise", null, null); // no equivalent just yet
    addParameter(":name", null, null); // symbolic side not subsym
    // applies to productions as well
    addParameter(":creation-time", ISubsymbolicChunk.CREATION_TIME, null);
    addParameter(":creation-cycle", ISubsymbolicChunk4.CREATION_CYCLE, null);
    addParameter(":needed", ISubsymbolicChunk.TIMES_NEEDED, null);
    addParameter(":contexts", ISubsymbolicChunk.TIMES_IN_CONTEXT, null);

    // model parameters
    // addParameter(":era", AbstractModel5.RATIONAL_ANALYSIS);
    // addParameter(":esc", AbstractModel5.SUBSYMBOLIC_COMPUTATION);
    // addParameter(":g", AbstractModel5.G);
    // addParameter(":egs", AbstractModel5.EXPECTED_GAIN_NOISE);
    // addParameter(":er", AbstractModel5.RANDOMNESS);
    // addParameter(":ga", AbstractModel5.W);
    addParameter(":blc", IDeclarativeModule4.BASE_LEVEL_CONSTANT,
        IDeclarativeModule4.class);
    addParameter(":ans", IDeclarativeModule4.ACTIVATION_NOISE,
        IDeclarativeModule4.class);
    addParameter(":pas", IDeclarativeModule4.PERMANENT_ACTIVATION_NOISE,
        IDeclarativeModule4.class);
    addParameter(":as", null, null);
    addParameter(":lf", IRetrievalModule4.LATENCY_FACTOR,
        IRetrievalModule4.class);
    addParameter(":le", null, null);
    addParameter(":dat", IProceduralModule4.DEFAULT_PRODUCTION_FIRING_TIME,
        IProceduralModule4.class);
    addParameter(":pm", IDeclarativeModule4.PARTIAL_MATCHING,
        IDeclarativeModule4.class);
    addParameter(":mp", IDeclarativeModule5.MISMATCH_PENALTY,
        IDeclarativeModule5.class);
    addParameter(":rt", IRetrievalModule.RETRIEVAL_THRESHOLD,
        IRetrievalModule.class);
    addParameter(":ol", IDeclarativeLearningModule4.OPTIMIZED_LEARNING,
        IDeclarativeLearningModule4.class);
    addParameter(":bll", IDeclarativeLearningModule4.BASE_LEVEL_LEARNING_RATE,
        IDeclarativeLearningModule4.class);
    addParameter(":al", IDeclarativeLearningModule4.ASSOCIATIVE_LEARNING_RATE,
        IDeclarativeLearningModule4.class);
    addParameter(":sl", null, null);
    addParameter(":pl", IProceduralLearningModule.PARAMETER_LEARNING_RATE,
        IProceduralLearningModule4.class);
    addParameter(":ea", null, null);
    addParameter(":time", null, null);

    // production parameters
    // addParameter(":chance", ISubsymbolicProduction.CHANCE);
    addParameter(":effort", ISubsymbolicProduction.FIRING_TIME, null);
    // _parameterMapping.put(":strength", null);
    // handled above by chunk
    // addParameter(":creation-time", "CreationTime");
    // addParameter(":creation-cycle", "CreationCycle");
    // addParameter(":references", "ReferenceTimes");
    // addParameter(":value", ISubsymbolicProduction.VALUE);
    // _parameterMapping.put(":q",null);
    addParameter(":a", ISubsymbolicProduction.FIRING_TIME, null);
    // _parameterMapping.put(":r",null);
    // _parameterMapping.put(":b",null);
    addParameter(":utility", ISubsymbolicProduction6.EXPECTED_UTILITY_PARAM, null);
    addParameter(":u", ISubsymbolicProduction6.UTILITY_PARAM, null);
//    addParameter(":failures", ISubsymbolicProduction.FAILURE_TIMES, null);
//    addParameter(":efforts", ISubsymbolicProduction.EFFORT_TIMES, null);
  }

  static public void addParameter(String lispParameter, String jactrParameter,
      Class< ? extends IModule> moduleClass)
  {
    if (lispParameter != null)
      _lispToJactr.put(lispParameter, new Pair(moduleClass, jactrParameter));
    if (jactrParameter != null)
      _jactrToLisp.put(jactrParameter, lispParameter);
  }

  static public Pair getJACTRParameter(String lispName)
  {
    return _lispToJactr.get(lispName);
  }

  static public String getJACTRParameterName(String lispName)
  {
    if (_lispToJactr.containsKey(lispName))
      return _lispToJactr.get(lispName).getParameterName();
    return lispName;
  }

  static public String getLispParameterName(String jactrName)
  {
    if (_jactrToLisp.containsKey(jactrName))
      return _jactrToLisp.get(jactrName);
    return jactrName;
  }

  static public class Pair
  {
    private Class< ? extends IModule> _moduleClass;

    private String                    _parameterName;

    public Pair(Class< ? extends IModule> moduleClass, String parameterName)
    {
      _moduleClass = moduleClass;
      _parameterName = parameterName;
    }

    public String getParameterName()
    {
      return _parameterName;
    }

    public Class< ? extends IModule> getModuleClass()
    {
      return _moduleClass;
    }
  }

}
