package org.jactr.core.module.declarative.search.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.logging.IMessageBuilder;
import org.jactr.core.logging.impl.MessageBuilderFactory;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * Basic filter that removes candidates based on their activation values, and
 * can log the information back to the runtime logs. This filter is not sharable
 * (internal state info) nor thread safe.
 * 
 * @author harrison
 */
public class PartialMatchActivationFilter implements ILoggedChunkFilter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                = LogFactory
                                                               .getLog(PartialMatchActivationFilter.class);

  private final double               _activationThreshold;

  private final ChunkTypeRequest     _request;

  private final boolean              _log;

  private double                     _highestActivationYet = Double.NEGATIVE_INFINITY;

  private IChunk                     _bestChunkYet         = null;

  private IMessageBuilder            _message;

  private ActivationPolicy           _activationPolicy;

  public PartialMatchActivationFilter(ActivationPolicy policy,
      ChunkTypeRequest request, double threshold, boolean logEvaluations)
  {
    _activationPolicy = policy;
    _request = request;
    _activationThreshold = threshold;
    _log = logEvaluations;
    if (_log) _message = MessageBuilderFactory.newInstance();
  }

  public PartialMatchActivationFilter(ChunkTypeRequest request, double threshold)
  {
    this(ActivationPolicy.SUMMATION, request, threshold, false);
  }

  public boolean accept(IChunk chunk)
  {

    int matches = _request.countMatches(chunk, new VariableBindings());
    if (matches < 1)
    {
      if (_message != null)
        _message.append(String.format(
            "rejecting %s, there is no overlap with retrieval pattern %s\n",
            chunk, _request));

      return false;
    }

    ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();

    double referenceActivation = _activationPolicy.getActivation(chunk);
    double totalActivation = ssc.getActivation();
    double discountedActivation = totalActivation;
    double base = ssc.getBaseLevelActivation();
    double spread = ssc.getSpreadingActivation();

    ISubsymbolicChunk5 ssc5 = ssc.getAdapter(ISubsymbolicChunk5.class);

    if (ssc5 != null) discountedActivation = ssc5.getActivation(_request);

    double discount = totalActivation - discountedActivation;

    // now with the discount, we can correctly due the referenceAc
    referenceActivation -= discount;

    boolean acceptChunk = referenceActivation >= _activationThreshold;
    boolean newBest = false;

    if (referenceActivation > _highestActivationYet)
    {
      _bestChunkYet = chunk;
      _highestActivationYet = referenceActivation;
      newBest = true;

    }

    if (_message != null)
    {
      String msg = null;
      if (newBest)
        msg = String.format(
            "%s is best candidate yet (%.2f=%.2f+%.2f [%.2f discount])\n",
            _bestChunkYet, discountedActivation, base, spread, discount);
      else
        msg = String
            .format(
                "%s doesn't have the highest activation (%.2f=%.2f+%.2f [%.2f discount])\n",
                chunk, discountedActivation, base, spread, discount);

      synchronized (_message)
      {
        _message.append(msg);
      }
    }

    return acceptChunk;
  }

  public IMessageBuilder getMessageBuilder()
  {
    return _message;
  }

}
