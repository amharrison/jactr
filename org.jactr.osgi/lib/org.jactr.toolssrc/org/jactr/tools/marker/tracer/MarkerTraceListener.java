package org.jactr.tools.marker.tracer;

/*
 * default logging
 */
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.marker.IMarkerListener;
import org.jactr.tools.marker.MarkerManager;
import org.jactr.tools.marker.impl.MarkerEvent;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.listeners.BaseTraceListener;
import org.jactr.tools.tracer.sinks.ChainedSink;
import org.jactr.tools.tracer.sinks.trace.ArchivalSink;

public class MarkerTraceListener extends BaseTraceListener
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(MarkerTraceListener.class);

  private final IMarkerListener      _markerListener;

  private MarkerIndex                _markerIndex;

  private final Set<IModel>          _installedModels = new HashSet<IModel>();

  public MarkerTraceListener()
  {

    _markerListener = new IMarkerListener() {

      public void markerOpened(MarkerEvent me)
      {
        redirectEvent(me);
        if (_markerIndex != null) _markerIndex.opened(me.getMarker());
      }

      public void markerClosed(MarkerEvent me)
      {
        redirectEvent(me);
        if (_markerIndex != null) _markerIndex.closed(me.getMarker());
      }

    };

    setEventTransformer(new MarkerEventTransformer());
  }

  public void install(IModel model, Executor executor)
  {
    MarkerManager.get().addListener(_markerListener, executor);
    _installedModels.add(model);
  }

  public void uninstall(IModel model)
  {
    /*
     * since we do a multi install, we need to make sure we only dispose of the
     * tool once it has been removed from all
     */
    if (_installedModels.remove(model) && _installedModels.size() == 0)
    {
      MarkerManager.get().removeListener(_markerListener);
      if (_markerIndex != null)
      {
        _markerIndex.dispose();
        _markerIndex = null;
      }
    }
  }

  @Override
  public void setTraceSink(ITraceSink sink)
  {
    if (_markerIndex != null)
    {
      _markerIndex.dispose();
      _markerIndex = null;
    }

    super.setTraceSink(sink);

    if (_markerIndex == null)
    {
      ArchivalSink as = checkForArchivalSink(sink);
      if (as != null) useIndexer(as);
    }
  }

  protected ArchivalSink checkForArchivalSink(ITraceSink sink)
  {
    if (sink instanceof ArchivalSink) return (ArchivalSink) sink;
    if (sink instanceof ChainedSink)
    {
      ChainedSink cSink = (ChainedSink) sink;
      List<ITraceSink> sinks = FastListFactory.newInstance();
      cSink.getSinks(sinks);

      try
      {
        for (ITraceSink tSink : sinks)
        {
          ArchivalSink as = checkForArchivalSink(tSink);
          if (as != null) return as;
        }
      }
      finally
      {
        FastListFactory.recycle(sinks);
      }
    }
    return null;
  }

  protected void useIndexer(ArchivalSink as)
  {
    _markerIndex = new MarkerIndex(as.getOutputDirectory());
  }

}
