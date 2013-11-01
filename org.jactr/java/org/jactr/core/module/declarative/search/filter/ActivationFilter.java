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

  public ActivationFilter(double threshold, boolean logEvaluations)
  {
    _activationThreshold = threshold;
    _log = logEvaluations;
    if (_log) _message = new TextBuilder();
  }

  public ActivationFilter(double threshold)
  {
    this(threshold, false);
  }

  public boolean accept(IChunk chunk)
  {
    ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();
    double totalActivation = ssc.getActivation();
    double base = ssc.getBaseLevelActivation();
    double spread = ssc.getSpreadingActivation();

    boolean acceptChunk = totalActivation >= _activationThreshold;

    if (totalActivation > _highestActivationYet)
    {
      _bestChunkYet = chunk;
      _highestActivationYet = totalActivation;

      if (_message != null)
        _message.append(String.format(
            "%s is best candidate yet (%.2f=%.2f+%.2f)\n", _bestChunkYet,
            totalActivation, base, spread));
    }
    else if (_message != null)
      _message.append(String.format(
          "%s doesn't have the highest activation (%.2f=%.2f+%.2f)\n", chunk,
          totalActivation, base, spread));

    return acceptChunk;
  }

  public TextBuilder getMessageBuilder()
  {
    return _message;
  }

}
