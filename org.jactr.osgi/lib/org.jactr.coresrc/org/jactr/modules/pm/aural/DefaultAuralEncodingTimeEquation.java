package org.jactr.modules.pm.aural;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;

public final class DefaultAuralEncodingTimeEquation implements
    IAuralEncodingTimeEquation
{


  public double computeEncodingTime(
       IChunk soundChunk,
       IAuralModule auralModule)
   {
    if (auralModule instanceof AbstractAuralModule)
      return ((AbstractAuralModule) auralModule).getRecodeTime(soundChunk
         .getSymbolicChunk()
         .getChunkType());
    else
      return 0.1;
   }
}