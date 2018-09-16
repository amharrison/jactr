package org.jactr.modules.pm.aural.memory.impl.map;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.utils.collections.FastCollectionFactory;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.modules.pm.aural.IAuralModule;

public class KindFeatureMap extends AbstractAuralFeatureMap<String[]>
{
  /**
   * Logger definition
   */
  static private final transient Log               LOGGER = LogFactory
                                                              .getLog(KindFeatureMap.class);

  private TreeMap<String, Collection<IIdentifier>> _kindMap;

  private Map<IIdentifier, String[]>               _currentKindMap;

  public KindFeatureMap()
  {
    super(IAuralModule.KIND_SLOT, IAuralPropertyHandler.TYPE);
    _kindMap = new TreeMap<String, Collection<IIdentifier>>();
    _currentKindMap = new HashMap<IIdentifier, String[]>();
  }

  @Override
  protected void addInformation(IIdentifier identifier, String[] data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding " + identifier + " has " + Arrays.toString(data));

    for (String kind : data)
    {
      Collection<IIdentifier> identifiers = _kindMap.get(kind);
      if (identifiers == null)
      {
        identifiers = FastCollectionFactory.newInstance();
        _kindMap.put(kind, identifiers);
      }
      identifiers.add(identifier);
    }
    _currentKindMap.put(identifier, data);
  }

  @Override
  protected String[] extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      return getHandler().getTypes(afferentObject);
    }
    catch (UnknownPropertyNameException e)
    {
      LOGGER.error("Could not extract type information from  "
          + afferentObject.getIdentifier(), e);
      return new String[0];
    }
  }

  @Override
  protected String[] getCurrentValue(IIdentifier identifier)
  {
    return _currentKindMap.get(identifier);
  }

  @Override
  protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results)
  {
    boolean firstInsertion = true;
    Set<IIdentifier> tmp = FastSetFactory.newInstance();

    for (IConditionalSlot slot : request.getConditionalSlots())
      if (slot.getName().equals(IAuralModule.KIND_SLOT))
      {
        tmp.clear();
        String kind = transformKind(slot.getValue());
        switch (slot.getCondition())
        {
          case IConditionalSlot.NOT_EQUALS:
            not(kind, tmp);
            break;
          default:
            equals(kind, tmp);
            break;
        }

        if (firstInsertion)
        {
          results.addAll(tmp);
          firstInsertion = false;
        }
        else
          results.retainAll(tmp);
      }

    FastSetFactory.recycle(tmp);
  }

  private void not(String kind, Set<IIdentifier> container)
  {
    for (String tmpKind : _kindMap.keySet())
      if (!tmpKind.equals(kind))
      {
        Collection<IIdentifier> ids = _kindMap.get(tmpKind);
        if (ids != null) container.addAll(ids);
      }
  }

  private void equals(String kind, Set<IIdentifier> container)
  {
    Collection<IIdentifier> ids = _kindMap.get(kind);
    if (ids != null) container.addAll(ids);
  }

  private String transformKind(Object value)
  {
    if (value instanceof IChunkType)
      return ((IChunkType) value).getSymbolicChunkType().getName();
    else if (value instanceof String)
      return (String) value;
    else
      return value.toString();
  }

  @Override
  protected String[] removeInformation(IIdentifier identifier)
  {
    String[] kinds = _currentKindMap.remove(identifier);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Removing " + identifier + " has " + Arrays.toString(kinds));

    if (kinds != null) for (String kind : kinds)
    {
      Collection<IIdentifier> identifiers = _kindMap.get(kind);
      if (identifiers != null)
      {
        identifiers.remove(identifier);
        if (identifiers.size() == 0)
        {
          _kindMap.remove(kind);
          FastCollectionFactory.recycle(identifiers);
        }
      }
    }

    return kinds;
  }

  @Override
  protected void clearInternal()
  {
    _kindMap.clear();
    _currentKindMap.clear();
  }

  @Override
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk,
      ChunkTypeRequest originalSearchRequest)
  {
    String[] kinds = _currentKindMap.get(identifier);

    if (kinds != null) for (String kind : kinds)
      try
      {
        // attempt to resolve it..
      IChunkType chunkType = encodedChunk.getModel().getDeclarativeModule()
          .getChunkType(kind).get();

      Object kindValue = kind;
      if (chunkType != null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Got chunktype " + chunkType + " for " + kind);
        kindValue = chunkType;
      }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug("No chunktype matching " + kind
            + " could be found, using string value");

      if (kindMatchesPattern(originalSearchRequest, kindValue))
      {
        mutableRequest
            .addSlot(new BasicSlot(IAuralModule.KIND_SLOT, kindValue));
        return;
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to get kind ", e);
    }

  }

  protected boolean kindMatchesPattern(ChunkTypeRequest pattern, Object kind)
  {
    for (IConditionalSlot slot : pattern.getConditionalSlots())
      if (slot.getName().equalsIgnoreCase(IAuralModule.KIND_SLOT))
        if (!slot.matchesCondition(kind)) return false;

    return true;
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub
    
  }

}
