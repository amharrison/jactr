package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.visual.Color;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.modules.pm.visual.IVisualModule;

public class ColorFeatureMap extends AbstractVisualFeatureMap<Color[]>
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER = LogFactory
                                                  .getLog(ColorFeatureMap.class);

  private Map<Color, Set<IIdentifier>> _valueMap;

  private Map<IIdentifier, Color[]>    _currentValues;

  private ColorChunkCache              _cache;

  public ColorFeatureMap(IModel model)
  {
    super(IVisualModule.COLOR_SLOT, IVisualPropertyHandler.COLOR);
    _valueMap = new HashMap<Color, Set<IIdentifier>>();
    _currentValues = new HashMap<IIdentifier, Color[]>();
    _cache = new ColorChunkCache(model);
  }

  public ColorChunkCache getColorChunkCache()
  {
    return _cache;
  }

  @Override
  protected void addInformation(IIdentifier identifier, Color[] data)
  {
    for (Color color : data)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(identifier + " has color " + color);
      Set<IIdentifier> identifiers = _valueMap.get(color);
      if (identifiers == null)
      {
        identifiers = FastSetFactory.newInstance();
        _valueMap.put(color, identifiers);
      }
      identifiers.add(identifier);
    }
    _currentValues.put(identifier, data);
  }

  @Override
  protected void clearInternal()
  {
    _valueMap.clear();
    _currentValues.clear();
  }

  @Override
  protected Color[] getCurrentValue(IIdentifier identifier)
  {
    return _currentValues.get(identifier);
  }

  @Override
  protected Color[] extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      return getHandler().getColors(afferentObject);
    }
    catch (UnknownPropertyNameException unknown)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(
            "Could not find coloring info for "
                + afferentObject.getIdentifier(), unknown);
      return new Color[0];
    }
  }

  @Override
  protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results)
  {
    boolean firstInsertion = true;
    String slotName = getRelevantSlotName();
    Set<IIdentifier> tmp = FastSetFactory.newInstance();
    for (IConditionalSlot slot : request.getConditionalSlots())
      if (slot.getName().equalsIgnoreCase(slotName))
      {
        Object slotValue = slot.getValue();
        Color value = null;

        if (slotValue instanceof IChunk || slotValue == null)
          value = _cache.getColor((IChunk) slotValue);
        else if (LOGGER.isWarnEnabled())
          LOGGER.warn(String
              .format("color feature map requires null or color chunk values"));

          tmp.clear();

          switch (slot.getCondition())
          {
            case IConditionalSlot.NOT_EQUALS:
              not(value, tmp);
              break;
            case IConditionalSlot.EQUALS:
              equals(value, tmp);
              break;
            default:
              LOGGER.warn("only not and equals are available for color");
              break;
          }

          if (firstInsertion)
          {
            firstInsertion = false;
            results.addAll(tmp);
          }
          else
            results.retainAll(tmp);
        }

    FastSetFactory.recycle(tmp);
  }

  protected void not(Color color, Set<IIdentifier> container)
  {
    for (Map.Entry<Color, Set<IIdentifier>> entry : _valueMap.entrySet())
      if (color == null && entry.getKey() != null
          || !color.equals(entry.getKey())) container.addAll(entry.getValue());
  }

  protected void equals(Color color, Set<IIdentifier> container)
  {
    Set<IIdentifier> tmp = _valueMap.get(color);
    if (tmp != null) container.addAll(tmp);
  }

  @Override
  protected Color[] removeInformation(IIdentifier identifier)
  {
    Color[] colors = _currentValues.remove(identifier);

    if (colors != null) for (Color color : colors)
    {
      Set<IIdentifier> identifiers = _valueMap.get(color);
      if (identifiers != null)
      {
        identifiers.remove(identifier);
        if (identifiers.size() == 0)
        {
          _valueMap.remove(color);
          FastSetFactory.recycle(identifiers);
        }
      }
    }

    return colors;
  }

  @Override
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk,
      ChunkTypeRequest originalSearchRequest)
  {
    /*
     * we require the original search pattern since there can be many colors..
     */
    Color[] colors = _currentValues.get(identifier);

    if (colors != null)
      for (Color color : colors)
        if (colorMatchesPattern(color, originalSearchRequest))
        {
          IChunk colorChunk = _cache.getColorChunk(color);
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("setting color to " + colorChunk + " describing "
                + color);
          // assign the value and return
          mutableRequest.addSlot(new BasicSlot(IVisualModule.COLOR_SLOT,
              colorChunk));
          return;
        }
    if (LOGGER.isDebugEnabled()) LOGGER.debug("no color to set");
  }

  protected boolean colorMatchesPattern(Color color, ChunkTypeRequest request)
  {
    for (IConditionalSlot slot : request.getConditionalSlots())
      if (slot.getName().equalsIgnoreCase(IVisualModule.COLOR_SLOT))
      {
        Object slotValue = slot.getValue();
        if (slotValue instanceof IChunk || slotValue == null)
        {
          Color value = _cache.getColor((IChunk) slotValue);
          switch (slot.getCondition())
          {
            case IConditionalSlot.NOT_EQUALS:
              if (color.equals(value)) return false;
              break;

            case IConditionalSlot.EQUALS:
              if (!color.equals(value)) return false;

            default:
              LOGGER.warn("only not and equals are available for color");
              break;
          }
        }
      }

    return true;
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub

  }

}
