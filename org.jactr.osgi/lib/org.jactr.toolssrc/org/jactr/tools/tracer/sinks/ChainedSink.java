package org.jactr.tools.tracer.sinks;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

public class ChainedSink implements ITraceSink, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChainedSink.class);

  private Collection<ITraceSink>     _sinks = FastListFactory.newInstance();


  public void add(ITraceSink sink)
  {
    _sinks.add(sink);
  }

  public Collection<ITraceSink> getSinks(Collection<ITraceSink> container)
  {
    if (container == null)
      container = new ArrayList<ITraceSink>(_sinks.size());
    container.addAll(_sinks);
    return container;
  }

  public void add(ITransformedEvent event)
  {
    if (event == null)
    {
      LOGGER.error("null message received ", new NullPointerException());
      return;
    }
    for (ITraceSink sink : _sinks)
      sink.add(event);
  }

  public void flush() throws Exception
  {
    for (ITraceSink sink : _sinks)
      sink.flush();
  }

  public boolean isOpen()
  {
    for(ITraceSink sink : _sinks)
      if(sink.isOpen()) return true;

    return false;
  }

  public String getParameter(String key)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    // TODO Auto-generated method stub
    return Collections.EMPTY_LIST;
  }

  public Collection<String> getSetableParameters()
  {
    // TODO Auto-generated method stub
    return Collections.EMPTY_LIST;
  }

  public void setParameter(String key, String value)
  {
    for (ITraceSink sink : _sinks)
      if (sink instanceof IParameterized)
        ((IParameterized) sink).setParameter(key, value);

  }

}
