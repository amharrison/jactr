package org.jactr.extensions.search.performance;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.basic.DefaultDeclarativeModule;
import org.jactr.core.module.declarative.search.ISearchSystem;
import org.jactr.core.module.declarative.search.local.DefaultSearchSystem;
import org.jactr.core.module.declarative.search.local.ExactParallelSearchDelegate;
import org.jactr.core.module.declarative.search.local.ExactSingleThreadedSearchDelegate;
import org.jactr.core.module.declarative.search.local.PartialParallelSearchDelegate;
import org.jactr.core.module.declarative.search.local.PartialSingleThreadedSearchDelegate;
import org.jactr.core.utils.parameter.BooleanParameterProcessor;
import org.jactr.core.utils.parameter.ParameterHelper;

/**
 * turns on profiling of local search. Can also enable parallel searches via
 * EnableParallelSearch=true. However, the overhead cost is generally too
 * expensive for most models. Install this extension, run it and get a sense of
 * the average runtimes, turn on parallel to see if you get any improvements.
 * 
 * @author harrison
 */
public class DefaultSearchSystemOptimizer implements IExtension
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(DefaultSearchSystemOptimizer.class);

  private IModel                     _model;

  private DefaultSearchSystem        _searchSystem;

  private ParameterHelper            _parameters     = new ParameterHelper();

  private boolean                    _enableParallel = false;

  public DefaultSearchSystemOptimizer()
  {
    _parameters.addProcessor(new BooleanParameterProcessor(
        "EnableParallelSearch", this::setParallelEnabled,
        this::isParallelEnabled));

  }

  public boolean isParallelEnabled()
  {
    return _enableParallel;
  }

  public void setParallelEnabled(boolean parallel)
  {
    _enableParallel = parallel;
  }

  @Override
  public void setParameter(String key, String value)
  {
    _parameters.setParameter(key, value);
  }

  @Override
  public String getParameter(String key)
  {
    return _parameters.getParameter(key);
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    Set<String> rtn = new TreeSet<String>();
    _parameters.getParameterNames(rtn);
    return rtn;
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  @Override
  public void initialize() throws Exception
  {
    if (_searchSystem != null)
    {
      // install the profiler
      _searchSystem.setExactDelegate(new ProfiledSearchDelegate(
          _enableParallel ? new ExactParallelSearchDelegate()
              : new ExactSingleThreadedSearchDelegate()));

      _searchSystem.setPartialDelegate(new ProfiledSearchDelegate(
          _enableParallel ? new PartialParallelSearchDelegate()
              : new PartialSingleThreadedSearchDelegate()));
    }
  }

  @Override
  public void install(IModel model)
  {
    _model = model;
    IDeclarativeModule decM = _model.getDeclarativeModule();
    DefaultDeclarativeModule ddm = decM
        .getAdapter(DefaultDeclarativeModule.class);

    if (ddm == null)
      LOGGER.warn(String.format(
          "Could not install %s properly. No %s installed.", this.getClass()
              .getSimpleName(), DefaultDeclarativeModule.class.getName()));
    else
    {
      ISearchSystem sm = ddm.getSearchSystem();
      if (sm instanceof DefaultSearchSystem)
        _searchSystem = (DefaultSearchSystem) sm;
      else
        LOGGER
            .warn(String
                .format(
                    "Could not hook into DefaultSearchSystem, unknown search system %s",
                    sm));
    }
  }

  @Override
  public void uninstall(IModel model)
  {
    if (_searchSystem != null)
    {
      System.out.println("Search Performance Statistics");
      System.out.println(getStats(
          (ProfiledSearchDelegate) _searchSystem.getExactDelegate(), "Exact"));
      System.out.println(getStats(
          (ProfiledSearchDelegate) _searchSystem.getPartialDelegate(),
          "Partial"));
    }
  }

  private String getStats(ProfiledSearchDelegate delegate, String type)
  {
    StatisticalSummary stats = delegate.getSummary();
    StatisticalSummary slide = delegate.getSlidingSummary();

    return String
        .format(
            "%s Search: Total : %.4f (%.4f) [%.4f - %.4f] ms. Last1000 : %.4f (%.4f) [%.4f - %.4f] ms.",
            type, stats.getMean(), stats.getStandardDeviation(),
            stats.getMin(), stats.getMax(), slide.getMean(),
            slide.getStandardDeviation(), slide.getMin(), slide.getMax());
  }

  @Override
  public IModel getModel()
  {
    return _model;
  }

  @Override
  public String getName()
  {
    return "searchOptimizer";
  }

}
