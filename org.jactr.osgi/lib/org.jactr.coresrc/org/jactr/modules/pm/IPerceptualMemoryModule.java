package org.jactr.modules.pm;

import java.util.concurrent.Future;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;

/*
 * default logging
 */

/**
 * a perceptual module that has a perceptual memory permitting the searching and
 * attending of perceptual information within it.
 */
public interface IPerceptualMemoryModule extends IPerceptualModule
{

  public IPerceptualMemory getPerceptualMemory();

  /**
   * search perceptual memory, returning some result in the future
   * 
   * @param request
   * @param requestTime
   * @param isStuffRequest
   * @return
   */
  public Future<PerceptualSearchResult> search(ChunkTypeRequest request,
      double requestTime,
      boolean isStuffRequest);

  /**
   * attend to some search result, potentially returning some attended
   * perceptual representation in the future.
   * 
   * @param audioEvent
   * @param requestTime
   * @return
   */
  public Future<IChunk> attendTo(PerceptualSearchResult searchResult,
      double requestTime);
}
