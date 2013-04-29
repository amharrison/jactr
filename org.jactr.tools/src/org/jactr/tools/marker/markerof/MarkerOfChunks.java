package org.jactr.tools.marker.markerof;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.event.IChunkListener;

/**
 * A simple class that installs listeners on specific chunks so you know when
 * the chunk has been accessed.
 * 
 * @author harrison
 */
public class MarkerOfChunks implements IMarkerOf<IChunk>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(MarkerOfChunks.class);

  private IChunkListener             _listener = new ChunkListenerAdaptor() {
                                                 @Override
                                                 public void chunkAccessed(
                                                     ChunkEvent event)
                                                 {

                                                 }
                                               };

  public void install(IChunk element)
  {

  }

}
