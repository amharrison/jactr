package org.jactr.core.module.declarative.search.filter;

/*
 * default logging
 */
import javolution.text.TextBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;

/**
 * Basic filter that removes candidates based on their activation values, and
 * can log the information back to the runtime logs. This filter is not sharable
 * (internal state info) nor thread safe.
 * 
 * @author harrison
 */
public class ActivationFilter implements IChunkFilter, ILoggedChunkFilter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                = LogFactory
                                                               .getLog(ActivationFilter.class);

  private final double               _activationThreshold;

  private final boolean              _log;

  private double                     _highestActivationYet = Double.NEGATIVE_INFINITY;

  private IChunk                     _bestChunkYet         = null;

  private TextBuilder                _message;

  private ActivationPolicy           _activationPolicy;

  public ActivationFilter(ActivationPolicy policy, double threshold,
      boolean logEvaluations)
  {
    _activationThreshold = threshold;
    _log = logEvaluations;
    _activationPolicy = policy;
    if (_log) _message = new TextBuilder();
  }

  public ActivationFilter(double threshold)
  {
    this(ActivationPolicy.SUMMATION, threshold, false);
  }

  public boolean accept(IChunk chunk)
  {
    ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();

    double referenceActivation = _activationPolicy.getActivation(chunk);
    double totalActivation = ssc.getActivation();
    double base = ssc.getBaseLevelActivation();
    double spread = ssc.getSpreadingActivation();

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
      String message = null;
      if (newBest)
        message = String.format("%s.(%.2f=%.2f+%.2f) is best candidate yet \n",
            _bestChunkYet, totalActivation, base, spread);
      else
        message = String.format(
            "%s.(%.2f=%.2f+%.2f) doesn't have the highest activation \n",
            chunk, totalActivation, base, spread);

      /*
       * TextBuilder's aren't thread safe.
       */
      synchronized (_message)
      {
        _message.append(message);
      }
    }

    return acceptChunk;
  }

  public TextBuilder getMessageBuilder()
  {
    return _message;
  }

}
