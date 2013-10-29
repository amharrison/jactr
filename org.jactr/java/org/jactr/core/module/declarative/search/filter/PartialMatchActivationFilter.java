package org.jactr.core.module.declarative.search.filter;

/*
 * default logging
 */
import javolution.text.TextBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
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

  private TextBuilder                _message;

  public PartialMatchActivationFilter(ChunkTypeRequest request,
      double threshold, boolean logEvaluations)
  {
    _request = request;
    _activationThreshold = threshold;
    _log = logEvaluations;
    if (_log) _message = new TextBuilder();
  }

  public PartialMatchActivationFilter(ChunkTypeRequest request, double threshold)
  {
    this(request, threshold, false);
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

    double totalActivation = ssc.getActivation();
    double tmpAct = totalActivation;
    double base = ssc.getBaseLevelActivation();
    double spread = ssc.getSpreadingActivation();

    ISubsymbolicChunk5 ssc5 = (ISubsymbolicChunk5) ssc
        .getAdapter(ISubsymbolicChunk5.class);

    if (ssc5 != null) tmpAct = ssc5.getActivation(_request);

    double discount = totalActivation - tmpAct;

    boolean acceptChunk = tmpAct >= _activationThreshold;

    if (totalActivation > _highestActivationYet)
    {
      _bestChunkYet = chunk;
      _highestActivationYet = totalActivation;

      if (_message != null)
        _message.append(String.format(
            "%s is best candidate yet (%.2f=%.2f+%.2f [%.2f discount])\n",
            _bestChunkYet, tmpAct, base, spread, discount));
    }
    else if (_message != null)
      _message
          .append(String
              .format(
                  "%s doesn't have the highest activation (%.2f=%.2f+%.2f [%.2f discount])\n",
                  chunk, tmpAct, base, spread, discount));

    return acceptChunk;
  }

  public TextBuilder getMessageBuilder()
  {
    return _message;
  }

}
