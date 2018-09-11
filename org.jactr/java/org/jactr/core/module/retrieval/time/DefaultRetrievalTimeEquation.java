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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.logging.Logger;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.declarative.search.filter.ActivationPolicy;
import org.jactr.core.module.retrieval.buffer.RetrievalRequestDelegate;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastListFactory;

/**
 * 4.0 retrieval time equation. Added an override system parameter to deal with
 * -infinite/NaN retrieval threshold on error (which will take infinite time to
 * complete). By default, when an infinite time is calculated, we instead use
 * the activation of the error chunk. By enabling
 * "org.jactr.core.module.retrieval.time.AllowInfiniteRetrievalTime" to true
 * (i.e.,
 * -Dorg.jactr.core.module.retrieval.time.AllowInfiniteRetrievalTime=true),
 * retrieval times can be infinite.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class DefaultRetrievalTimeEquation implements IRetrievalTimeEquation
{

  private static transient Log LOGGER                      = LogFactory
                                                               .getLog(DefaultRetrievalTimeEquation.class
                                                                   .getName());

  IRetrievalModule4            _retrievalModule;

  IChunk                       _errorChunk;

  boolean                      _warned                     = false;

  static private boolean       _allowInfiniteRetrievalTime = false;

  static
  {
    try
    {
      _allowInfiniteRetrievalTime = Boolean
          .parseBoolean(System
              .getProperty("org.jactr.core.module.retrieval.time.AllowInfiniteRetrievalTime"));
    }
    catch (Exception e)
    {
      _allowInfiniteRetrievalTime = false;
    }

  }

  public DefaultRetrievalTimeEquation(IRetrievalModule4 module)
  {
    _retrievalModule = module;
  }

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   * @param chunk
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public double computeRetrievalTime(IChunk chunk)
  {
    return computeRetrievalTime(chunk, null);
  }

  public double computeRetrievalTime(IChunk retrievedChunk,
      ChunkTypeRequest retrievalRequest)
  {
    List<ISlot> slots = FastListFactory.newInstance();
    if(retrievalRequest != null)
    	retrievalRequest.getSlots(slots);
    double latencyFactor = _retrievalModule.getLatencyFactor();
    double latencyExponent = _retrievalModule.getLatencyExponent();
    double threshold = RetrievalRequestDelegate.getThreshold(_retrievalModule,
        slots);
    IDeclarativeModule decM = _retrievalModule.getModel()
        .getDeclarativeModule();

    ActivationPolicy policy = RetrievalRequestDelegate.getActivationPolicy(
        DefaultRetrievalModule6.RETRIEVAL_TIME_SLOT, slots);

    double retrievalTime = 0;

    if (_errorChunk == null) _errorChunk = decM.getErrorChunk();

    if (retrievedChunk.equals(_errorChunk))
    {
      /*
       * if the retrieval threshold is inf, the time at which the retrieval will
       * fail will be inf as well, so we need to return something a little less
       * dangerous - so we use the error chunk activation to compute the
       * retrieval time.
       */
      if (!_allowInfiniteRetrievalTime
          && (Double.isInfinite(threshold) || Double.isNaN(threshold)))
      {
        retrievalTime = latencyFactor
            * Math.exp(-policy.getActivation(_errorChunk));
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
      { // otherwise, we fail using the normal math
        if (Double.isNaN(threshold)) threshold = Double.POSITIVE_INFINITY;

        retrievalTime = latencyFactor * Math.exp(-threshold);

        if (Double.isInfinite(retrievalTime))
        {
          String message = String
              .format(
                  "Warning : retrieval of error will take %.2f seconds, because of lack of threshold",
                  retrievalTime);
          if (LOGGER.isWarnEnabled()) LOGGER.warn(message);

          if (Logger.hasLoggers(_retrievalModule.getModel()))
            Logger.log(_retrievalModule.getModel(), Logger.Stream.RETRIEVAL,
                message);
        }
      }
    }
    else
    {
      /*
       * everything is fine, calculate the retrieval time bsed on the chunks
       * activation.
       */
      double activation = policy.getActivation(retrievedChunk);

      /**
       * if partial matching is enabled and the chunk supports
       * getActivation(ChunkTypeRequest), then use the pattern to determine the
       * relative activation
       */
      IDeclarativeModule4 decM4 = decM
          .getAdapter(IDeclarativeModule4.class);
      boolean partialEnabled = false;
      if (decM4 != null)
        partialEnabled = RetrievalRequestDelegate.isPartialMatchEnabled(decM4,
            slots);

      if (partialEnabled && retrievalRequest != null)
      {
        ISubsymbolicChunk5 ssc = retrievedChunk
            .getSubsymbolicChunk().getAdapter(ISubsymbolicChunk5.class);
        if (ssc != null)
        {
          double total = ssc.getActivation();
          double discounted = ssc.getActivation(retrievalRequest);
          activation -= total - discounted;
        }
      }

      retrievalTime = latencyFactor * Math.exp(-latencyExponent * activation);
    }

    FastListFactory.recycle(slots);

    return retrievalTime;
  }
}