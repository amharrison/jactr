package org.jactr.extensions.search.performance;

/*
 * default logging
 */
import java.util.Comparator;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.module.declarative.search.local.DefaultSearchSystem;
import org.jactr.core.module.declarative.search.local.ISearchDelegate;
import org.jactr.core.production.request.ChunkTypeRequest;

public class ProfiledSearchDelegate implements ISearchDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
                                                           .getLog(ProfiledSearchDelegate.class);

  private final ISearchDelegate      _delegate;

  private SummaryStatistics          _summaryStatistcs = new SummaryStatistics();

  private DescriptiveStatistics      _descriptiveStatistics = new DescriptiveStatistics(
                                                                1000);

  public ProfiledSearchDelegate(ISearchDelegate delegate)
  {
    _delegate = delegate;
  }

  @Override
  public SortedSet<IChunk> find(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter,
      DefaultSearchSystem searchSystem)
  {
    long start = System.nanoTime();

    SortedSet<IChunk> rtn = _delegate.find(pattern, sortRule, filter,
        searchSystem);

    long delta = System.nanoTime() - start;
    double millis = delta / 1000000.0;

    _summaryStatistcs.addValue(millis);
    _descriptiveStatistics.addValue(millis);

    return rtn;
  }

  public StatisticalSummary getSummary()
  {
    return _summaryStatistcs;
  }

  public StatisticalSummary getSlidingSummary()
  {
    return _descriptiveStatistics;
  }

}
