package org.jactr.tools.marker.markerof;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.marker.IMarker;
import org.jactr.tools.marker.impl.DefaultMarker;

/**
 * listener for buffers that marks state changes and contents
 * 
 * @author harrison
 */
public class MarkerOfBuffers implements IMarkerOf<IActivationBuffer>
{
  /**
   * Logger definition
   */
  static private final transient Log      LOGGER          = LogFactory
                                                              .getLog(MarkerOfBuffers.class);

  private Map<IActivationBuffer, IMarker> _stateMarkers   = new HashMap<IActivationBuffer, IMarker>();

  private Map<IActivationBuffer, IMarker> _contentMarkers = new HashMap<IActivationBuffer, IMarker>();

  private IActivationBufferListener       _bufferListener = new ActivationBufferListenerAdaptor() {
                                                            @Override
                                                            public void statusSlotChanged(
                                                                ActivationBufferEvent abe)
                                                            {
                                                              if (abe
                                                                  .getSlotName()
                                                                  .equals(
                                                                      "state"))
                                                                stateHasChanged(
                                                                    abe.getSource(),
                                                                    abe.getNewSlotValue(),
                                                                    abe.getSimulationTime());
                                                            }

                                                            @Override
                                                            public void sourceChunkAdded(
                                                                ActivationBufferEvent abe)
                                                            {
                                                              updateContents(
                                                                  abe.getSource(),
                                                                  abe.getSimulationTime());
                                                            }

                                                            @Override
                                                            public void sourceChunkRemoved(
                                                                ActivationBufferEvent abe)
                                                            {
                                                              updateContents(
                                                                  abe.getSource(),
                                                                  abe.getSimulationTime());
                                                            }

                                                            @Override
                                                            public void sourceChunksCleared(
                                                                ActivationBufferEvent abe)
                                                            {
                                                              updateContents(
                                                                  abe.getSource(),
                                                                  abe.getSimulationTime());
                                                            }
                                                          };

  public void install(IActivationBuffer element)
  {
    _stateMarkers.put(element, null);
    _contentMarkers.put(element, null);

    // inline
    element.addListener(_bufferListener, null);
  }

  protected void stateHasChanged(IActivationBuffer buffer, Object stateValue,
      double when)
  {
    IMarker marker = _stateMarkers.remove(buffer);

    if (marker != null) marker.close(when);

    marker = new DefaultMarker(buffer.getModel(), String.format("%s.%s",
        buffer.getName(), stateValue), "buffer.state");

    _stateMarkers.put(buffer, marker);
    marker.open(when);
  }

  protected void updateContents(IActivationBuffer buffer, double when)
  {
    IMarker marker = _contentMarkers.remove(buffer);
    if (marker != null) marker.close(when);

    List<IChunk> sourceChunks = FastListFactory.newInstance();
    buffer.getSourceChunks(sourceChunks);

    if (sourceChunks.size() != 0)
    {
      marker = new DefaultMarker(buffer.getModel(), String.format("%s.%d",
          buffer.getName(), sourceChunks.size()), "buffer.content");

      StringBuilder sb = new StringBuilder();
      for (IChunk chunk : sourceChunks)
      {
        if (sb.length() != 0) sb.append(", ");
        sb.append(chunk);
      }

      ((DefaultMarker) marker).setDescription(sb.toString());

      _contentMarkers.put(buffer, marker);
      marker.open(when);
    }

    FastListFactory.recycle(sourceChunks);
  }

}
