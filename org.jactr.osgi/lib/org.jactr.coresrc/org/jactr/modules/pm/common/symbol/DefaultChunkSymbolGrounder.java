package org.jactr.modules.pm.common.symbol;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.modalities.visual.DefaultVisualPropertyHandler;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.ChunkActivationComparator;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.search.filter.AcceptAllFilter;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.IPerceptualModule;

public class DefaultChunkSymbolGrounder implements ISymbolGrounder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                      = LogFactory
                                                                     .getLog(DefaultChunkSymbolGrounder.class);

  static private final String        PERCEPTUAL_SYMBOL_CHUNKTYPE = "perceptual-symbol";

  static private final String        TOKEN_SLOT                  = "token";

  private IVisualPropertyHandler     _visualPropertyHandler      = new DefaultVisualPropertyHandler();

  private IAuralPropertyHandler      _auralPropertyHandler       = new DefaultAuralPropertyHandler();

  private Map<String, IChunk>        _tokenToConcept             = new TreeMap<String, IChunk>();

  private IChunkType                 _symbolChunkType;

  private ChunkActivationComparator  _activationSorter           = new ChunkActivationComparator();

  public Object getSymbolForPercept(IAfferentObject percept,
      IPerceptualModule perceivingModule, IDeclarativeModule declarativeModule)
  {
    return getSymbolForString(getToken(percept), declarativeModule);
  }
  
  public Object getSymbolForString(String symbolString, IDeclarativeModule declarativeModule) {
    IChunk conceptSymbol = getConcept(symbolString);
    /*
     * try to find it in DM
     */
    
    if (conceptSymbol == null)
      try
      {
        conceptSymbol = findConcept(symbolString, declarativeModule);

        if (conceptSymbol != null) addConcept(symbolString, conceptSymbol);
      }
      catch (Exception e)
      {
        LOGGER.error(String
            .format("Failed to search DM for existing grounded symbol %s",
                symbolString), e);
      }

    if (conceptSymbol == null)
      try
      {
        conceptSymbol = createConcept(symbolString, declarativeModule);
        conceptSymbol = encodeConcept(conceptSymbol, declarativeModule);

        addConcept(symbolString, conceptSymbol);
      }
      catch (Exception e)
      {
        LOGGER
            .error(
                String
                    .format(
                        "Failed to create and encode grounded symbol %s. Returning string symbol instead.",
                        symbolString), e);
        return symbolString;
      }

    return conceptSymbol;
  }

  private IChunk getConcept(String symbolName)
  {
    IChunk symbol = _tokenToConcept.get(symbolName);
    return symbol;
  }

  private void addConcept(String symbolName, IChunk symbolChunk)
  {
    _tokenToConcept.put(symbolName, symbolChunk);
  }

  private IChunk findConcept(String symbol, IDeclarativeModule declarativeModule)
      throws Exception
  {
    ChunkTypeRequest request = new ChunkTypeRequest(
        getSymbolChunkType(declarativeModule));
    request.addSlot(new DefaultConditionalSlot(TOKEN_SLOT,
        IConditionalSlot.EQUALS, symbol));

    Collection<IChunk> candidates = declarativeModule.findExactMatches(request,
        _activationSorter, new AcceptAllFilter()).get();


    if (candidates.size() == 0)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Found no existing symbols with token=%s",
            symbol));
      return null;
    }
    else
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Found %d existing symbols with token=%s",
            candidates.size(), symbol));

      return candidates.iterator().next();
    }
  }

  private IChunk createConcept(String symbol,
      IDeclarativeModule declarativeModule) throws Exception
  {
    IChunk symbolChunk = declarativeModule.createChunk(
        getSymbolChunkType(declarativeModule), symbol).get();

    ((IMutableSlot) symbolChunk.getSymbolicChunk().getSlot(TOKEN_SLOT))
        .setValue(symbol);

    return symbolChunk;
  }

  private IChunk encodeConcept(IChunk symbolConcept,
      IDeclarativeModule declarativeModule) throws Exception
  {
    symbolConcept = declarativeModule.addChunk(symbolConcept).get();

    return symbolConcept;
  }

  private IChunkType getSymbolChunkType(IDeclarativeModule declarativeModule)
      throws Exception
  {
    if (_symbolChunkType == null)
      _symbolChunkType = declarativeModule.getChunkType(
          PERCEPTUAL_SYMBOL_CHUNKTYPE).get();
    return _symbolChunkType;
  }

  private String getToken(IAfferentObject percept)
  {
    String symbol = null;

    if (_visualPropertyHandler.hasModality(percept))
    {
      if (_visualPropertyHandler.hasProperty(IVisualPropertyHandler.TEXT,
          percept))
        symbol = _visualPropertyHandler.getText(percept);
      else if (_visualPropertyHandler.hasProperty(IVisualPropertyHandler.TOKEN,
          percept)) symbol = _visualPropertyHandler.getToken(percept);
    }
    else if (_auralPropertyHandler.hasModality(percept))
      if (_auralPropertyHandler.hasProperty(IAuralPropertyHandler.TOKEN,
          percept)) symbol = _auralPropertyHandler.getToken(percept);

    /*
     * catch all
     */
    if (symbol == null)
    {
      symbol = percept.getIdentifier().getName();
      if (symbol == null) symbol = percept.getIdentifier().toString();
    }

    return symbol; // toLower cases problems since jactr is case respecting
  }
}
