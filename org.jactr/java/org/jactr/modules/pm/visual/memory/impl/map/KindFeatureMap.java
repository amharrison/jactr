package org.jactr.modules.pm.visual.memory.impl.map;

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
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.utils.collections.FastCollectionFactory;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.modules.pm.visual.IVisualModule;

public class KindFeatureMap extends AbstractVisualFeatureMap<String[]>
{
  /**
   * Logger definition
   */
  static private final transient Log                   LOGGER = LogFactory
                                                                  .getLog(KindFeatureMap.class);

  private TreeMap<String, Collection<IIdentifier>>     _kindMap;

  private HashMap<IChunkType, Collection<IIdentifier>> _ctKindMap;

  private Map<IIdentifier, String[]>                   _currentKindMap;

  private Map<IIdentifier, Set<IChunkType>>            _currentCTKindMap;

  public KindFeatureMap()
  {
    super(IVisualModule.KIND_SLOT, IVisualPropertyHandler.TYPE);
    _kindMap = new TreeMap<String, Collection<IIdentifier>>();
    _currentKindMap = new HashMap<IIdentifier, String[]>();
    _ctKindMap = new HashMap<IChunkType, Collection<IIdentifier>>();
    _currentCTKindMap = new HashMap<IIdentifier, Set<IChunkType>>();
  }

  @Override
  protected void addInformation(IIdentifier identifier, String[] data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding " + identifier + " has " + Arrays.toString(data));

    Set<IChunkType> ctKinds = FastSetFactory.newInstance();

    for (String kind : data)
    {
      Collection<IIdentifier> identifiers = _kindMap.get(kind);
      if (identifiers == null)
      {
        identifiers = FastCollectionFactory.newInstance();
        _kindMap.put(kind, identifiers);
      }
      identifiers.add(identifier);

      IChunkType ct = transformKind(kind);
      if (ct != null)
      {
        ctKinds.add(ct);
        identifiers = _ctKindMap.get(ct);
        if (identifiers == null)
        {
          identifiers = FastCollectionFactory.newInstance();
          _ctKindMap.put(ct, identifiers);
        }
        identifiers.add(identifier);
      }
    }
    _currentKindMap.put(identifier, data);
    _currentCTKindMap.put(identifier, ctKinds);
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
      if (slot.getName().equals(IVisualModule.KIND_SLOT))
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

    IChunkType actualKind = transformKind(kind);
    if (actualKind != null) for (Map.Entry<IChunkType, Collection<IIdentifier>> entry : _ctKindMap
        .entrySet())
      if(!entry.getKey().isA(actualKind))
        container.addAll(entry.getValue());
  }

  private void equals(String kind, Set<IIdentifier> container)
  {
    Collection<IIdentifier> ids = _kindMap.get(kind);
    if (ids != null) container.addAll(ids);

    IChunkType actualKind = transformKind(kind);
    if (actualKind != null) for (Map.Entry<IChunkType, Collection<IIdentifier>> entry : _ctKindMap
        .entrySet())
      if (entry.getKey().isA(actualKind)) container.addAll(entry.getValue());
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

  private IChunkType transformKind(String chunkTypeName)
  {
    try
    {
      return getPerceptualMemory().getModule().getModel()
          .getDeclarativeModule().getChunkType(chunkTypeName).get();
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Failed to get chunktype " + chunkTypeName, e);
      return null;
    }
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

    Set<IChunkType> cts = _currentCTKindMap.remove(identifier);
    if (cts != null) for (IChunkType kind : cts)
    {
      Collection<IIdentifier> identifiers = _ctKindMap.get(kind);
      if (identifiers != null)
      {
        identifiers.remove(identifier);
        if (identifiers.size() == 0)
        {
          _ctKindMap.remove(kind);
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
            .addSlot(new BasicSlot(IVisualModule.KIND_SLOT, kindValue));
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
      if (slot.getName().equalsIgnoreCase(IVisualModule.KIND_SLOT))
        if (!slot.matchesCondition(kind)) return false;

    return true;
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub

  }

}
