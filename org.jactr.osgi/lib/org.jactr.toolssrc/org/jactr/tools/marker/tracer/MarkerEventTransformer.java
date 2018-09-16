package org.jactr.tools.marker.tracer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IACTREvent;
import org.jactr.tools.marker.impl.MarkerEvent;
import org.jactr.tools.tracer.transformer.IEventTransformer;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

public class MarkerEventTransformer implements IEventTransformer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(MarkerEventTransformer.class);

  public ITransformedEvent transform(IACTREvent actrEvent)
  {
    return new MarkerTransformedEvent((MarkerEvent) actrEvent);
  }

}
