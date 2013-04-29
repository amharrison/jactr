/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
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

package org.jactr.core.module.retrieval.time;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;

/**
 * 4.0 retrieval time equation
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class DefaultRetrievalTimeEquation implements IRetrievalTimeEquation
{

  private static transient Log LOGGER  = LogFactory
                                           .getLog(DefaultRetrievalTimeEquation.class
                                               .getName());

  IRetrievalModule4            _retrievalModule;

  IChunk                       _errorChunk;

  boolean                      _warned = false;

  public DefaultRetrievalTimeEquation(IRetrievalModule4 module)
  {
    _retrievalModule = module;
  }

  /**
   * Description of the Method
   * 
   * @param model
   *            Description of the Parameter
   * @param chunk
   *            Description of the Parameter
   * @return Description of the Return Value
   */
  public double computeRetrievalTime(IChunk chunk)
  {
    double latencyFactor = _retrievalModule.getLatencyFactor();
    double latencyExponent = _retrievalModule.getLatencyExponent();
    double threshold = _retrievalModule.getRetrievalThreshold();
    double retrievalTime = 0;

    if (_errorChunk == null)
      _errorChunk = _retrievalModule.getModel().getDeclarativeModule()
          .getErrorChunk();

    if (chunk.equals(_errorChunk))
    {
      /*
       * if the retrieval threshold is inf, the time at which the retrieval will
       * fail will be inf as well, so we need to return something a little less
       * dangerous - so we use the error chunk activation to compute the
       * retrieval time.
       */
      if (Double.isInfinite(threshold) || Double.isNaN(threshold))
      {
        retrievalTime = latencyFactor *
            Math.exp(-_errorChunk.getSubsymbolicChunk().getActivation());
        if (!_warned)
        {
          StringBuilder msg = new StringBuilder(
              "since retrieval threshold is not active, retrieval errors can take an infinite time. Returning in ");
          msg.append(retrievalTime).append("s instead.");

          if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
          msg.insert(0, "Warning ");
          if (Logger.hasLoggers(_retrievalModule.getModel()))
            Logger.log(_retrievalModule.getModel(), Logger.Stream.RETRIEVAL,
                msg.toString());
          _warned = true;
        }
      }
      else
        // otherwise, we fail using the normal math
        retrievalTime = latencyFactor * Math.exp(-threshold);
    }
    else
    {
      /*
       * everything is fine, calculate the retrieval time bsed on the chunks
       * activation
       */
      double activation = chunk.getSubsymbolicChunk().getActivation();

      retrievalTime = latencyFactor * Math.exp(-latencyExponent * activation);
    }

    return retrievalTime;
  }
}