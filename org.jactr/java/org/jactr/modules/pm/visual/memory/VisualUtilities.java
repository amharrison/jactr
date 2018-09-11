package org.jactr.modules.pm.visual.memory;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.visual.IVisualModule;

public class VisualUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VisualUtilities.class);

  
  /**
   * return the search result, invalid or not
   * 
   * @param locationChunk
   * @param visualMemory
   * @return
   */
  static public PerceptualSearchResult getSearchResult(IChunk locationChunk, IVisualMemory visualMemory)
  {
    List<PerceptualSearchResult> results = FastListFactory.newInstance();
    visualMemory.getRecentSearchResults(results);

    for (PerceptualSearchResult result : results)
      if (locationChunk == result.getLocation())
        return result;

    FastListFactory.recycle(results);
    return null;
  }
   
  /**
   * return search result with identifier
   * @param perceptualIdentifier
   * @param visualMemory
   * @return
   */
  static public PerceptualSearchResult getSearchResult(
      IIdentifier perceptualIdentifier, IVisualMemory visualMemory)
  {
    List<PerceptualSearchResult> results = FastListFactory.newInstance();
    visualMemory.getRecentSearchResults(results);

    for (PerceptualSearchResult result : results)
      if (result.getPerceptIdentifier().equals(perceptualIdentifier))
        return result;

    FastListFactory.recycle(results);
    return null;
  }

  /**
   * returns {@link PerceptualSearchResult#getLocation()} if
   * {@link IVisualMemory#isStickyAttentionEnabled()} is false. Otherwise, it
   * attends to resolve to the current visual-location of the
   * {@link PerceptualSearchResult#getPercept()} if it is the latest visual
   * search
   * 
   * @param searchResult
   * @param visualMemory
   * @return
   */
  static public IChunk getVisualLocation(PerceptualSearchResult searchResult,
      IVisualMemory visualMemory)
  {
    if (!visualMemory.isStickyAttentionEnabled())
      return searchResult.getLocation();

    if (visualMemory.getLastSearchResult() != searchResult)
      return searchResult.getLocation();

    /*
     * need to snag the screen-pos for visual object
     */
    IChunk visualChunk = searchResult.getPercept();
    return (IChunk) visualChunk.getSymbolicChunk().getSlot(
        IVisualModule.SCREEN_POSITION_SLOT).getValue();
  }

  /**
   * returns false if !{@link IVisualMemory#isStickyAttentionEnabled()}. Else if
   * the last visual search percept identifier matches the provided
   * 
   * @param identifier
   * @param visualMemory
   * @return
   */
  static public boolean isCurrentlySticky(IIdentifier identifier,
      IVisualMemory visualMemory)
  {
    if (!visualMemory.isStickyAttentionEnabled()) return false;

    PerceptualSearchResult searchResult = visualMemory.getLastSearchResult();
    if (searchResult != null)
      return searchResult.getPerceptIdentifier().equals(identifier);

    return false;
  }

  /**
   * returns false if !{@link IVisualMemory#isStickyAttentionEnabled()}. Else
   * true if the perceptual chunk is in the associataed buffer
   * 
   * @param perceptualEncoding
   * @param visualMemory
   * @param buffer
   * @return
   */
  static public boolean isCurrentlySticky(IChunk perceptualEncoding,
      IVisualMemory visualMemory, IActivationBuffer buffer)
  {
    if (!visualMemory.isStickyAttentionEnabled()) return false;

    return BufferUtilities.getContainingBuffers(perceptualEncoding, true)
        .contains(buffer);
  }
}
