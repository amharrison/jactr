package org.jactr.modules.pm.aural.memory;

/*
 * default logging
 */
import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;

public class AuralUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AuralUtilities.class);

  
  static public PerceptualSearchResult getSearchResult(IChunk locationChunk,
      IAuralMemory auralMemory)
  {
    FastList<PerceptualSearchResult> results = FastList.newInstance();
    auralMemory.getRecentSearchResults(results);

    for (PerceptualSearchResult result : results)
      if (locationChunk==result.getLocation())
        return result;

    FastList.recycle(results);
    return null;
  }

  /**
   * return search result with identifier
   * 
   * @param perceptualIdentifier
   * @param auralMemory
   * @return
   */
  static public PerceptualSearchResult getSearchResult(
      IIdentifier perceptualIdentifier, IAuralMemory auralMemory)
  {
    FastList<PerceptualSearchResult> results = FastList.newInstance();
    auralMemory.getRecentSearchResults(results);

    for (PerceptualSearchResult result : results)
      if (result.getPerceptIdentifier().equals(perceptualIdentifier))
        return result;

    FastList.recycle(results);
    return null;
  }

  /**
   * @param searchResult
   * @param auralMemory
   * @return
   */
  static public IChunk getAuralEvent(PerceptualSearchResult searchResult,
      IAuralMemory auralMemory)
  {
    if (auralMemory.getLastSearchResult() != searchResult)
      return searchResult.getLocation();

    /*
     * need to snag the screen-pos for visual object
     */
    IChunk auralChunk = searchResult.getPercept();
    return (IChunk) auralChunk.getSymbolicChunk().getSlot(
        IAuralModule.EVENT_SLOT).getValue();
  }


}
