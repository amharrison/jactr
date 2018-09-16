package org.jactr.modules.pm.common.memory.map;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.modules.pm.IPerceptualModule;

public abstract class AbstractSortedFeatureMap<T> extends AbstractFeatureMap<T>
{

  /**
   * Logger definition
   */
  static private final transient Log     LOGGER = LogFactory
                                                    .getLog(AbstractSortedFeatureMap.class);

  private SortedMap<T, Set<IIdentifier>> _valueMap;

  private Map<IIdentifier, T>            _currentValues;

  public AbstractSortedFeatureMap(String requestSlotName, String crPropertyName)
  {
    super(requestSlotName, crPropertyName);
    _valueMap = new TreeMap<T, Set<IIdentifier>>();
    _currentValues = new HashMap<IIdentifier, T>();
  }

  @Override
  protected void clearInternal()
  {
    _currentValues.clear();
    _valueMap.clear();
  }

  @Override
  protected T getCurrentValue(IIdentifier identifier)
  {
    return _currentValues.get(identifier);
  }

  protected void not(T value, Set<IIdentifier> container)
  {
    for (Map.Entry<T, Set<IIdentifier>> entry : _valueMap.entrySet())
      if (!value.equals(entry.getKey())) container.addAll(entry.getValue());
  }

  protected void equals(T value, Set<IIdentifier> container)
  {
    Set<IIdentifier> tmp = _valueMap.get(value);
    if (tmp != null) container.addAll(tmp);
  }

  protected void lessThan(T value, Set<IIdentifier> container)
  {
    for (Set<IIdentifier> ids : _valueMap.headMap(value).values())
      container.addAll(ids);
  }

  protected void greaterThan(T value, Set<IIdentifier> container)
  {
    for (Map.Entry<T, Set<IIdentifier>> entry : _valueMap.tailMap(value)
        .entrySet())
      if (!value.equals(entry.getKey())) container.addAll(entry.getValue());
  }

  /**
   * resolves lowest, highest
   * 
   * @param request
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#normalizeRequest(org.jactr.core.production.request.ChunkTypeRequest)
   */
  public void normalizeRequest(ChunkTypeRequest request)
  {
    String relevantSlot = getRelevantSlotName();
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equalsIgnoreCase(relevantSlot))
      {
        Object slotValue = cSlot.getValue();
        String valueChunkName = null;
        if (slotValue instanceof IChunk)
          valueChunkName = ((IChunk) slotValue).getSymbolicChunk().getName();

        try
        {
          if (IPerceptualModule.LOWEST_CHUNK.equalsIgnoreCase(valueChunkName))
          {
            T value = _valueMap.firstKey();
            if (value != null)
              cSlot.setValue(value);
            else if (LOGGER.isDebugEnabled())
              LOGGER.debug("No values in " + this + " to resolve "
                  + valueChunkName);
          }
          else if (IPerceptualModule.HIGHEST_CHUNK
              .equalsIgnoreCase(valueChunkName))
          {
            T value = _valueMap.lastKey();
            if (value != null)
              cSlot.setValue(value);
            else if (LOGGER.isDebugEnabled())
              LOGGER.debug("No values in " + this + " to resolve "
                  + valueChunkName);
          }
        }
        catch (NoSuchElementException nsef)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("No value in " + this + " to resolve "
                + valueChunkName);
        }
      }
  }

  /**
   * converts a slot value to the appropriate type
   * 
   * @param slot
   * @return
   */
  abstract protected T toData(ISlot slot);

  /**
   * tests to be sure the value of the slot is a valid type
   * 
   * @param slot
   * @return
   */
  abstract protected boolean isValidValue(ISlot slot);

  @Override
  protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results)
  {
    boolean firstInsertion = true;
    String slotName = getRelevantSlotName();
    Set<IIdentifier> tmp = FastSetFactory.newInstance();
    for (IConditionalSlot slot : request.getConditionalSlots())
      if (slot.getName().equalsIgnoreCase(slotName))
        if (isValidValue(slot))
        {
          tmp.clear();
          T value = toData(slot);
          switch (slot.getCondition())
          {
            case IConditionalSlot.LESS_THAN_EQUALS:
              equals(value, tmp);
            case IConditionalSlot.LESS_THAN:
              lessThan(value, tmp);
              break;
            case IConditionalSlot.GREATER_THAN_EQUALS:
              equals(value, tmp);
            case IConditionalSlot.GREATER_THAN:
              greaterThan(value, tmp);
              break;
            case IConditionalSlot.NOT_EQUALS:
              not(value, tmp);
              break;
            case IConditionalSlot.WITHIN:
              LOGGER.warn("within is not implemented");
            default:
              equals(value, tmp);
              break;
          }

          if (firstInsertion)
            results.addAll(tmp);
          else
            results.retainAll(tmp);

          firstInsertion = false;

          if (results.size() == 0)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(this + " No possible results, returning early");
            break;
          }
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug(this + " " + slot + " value is not naturally ordered");

    FastSetFactory.recycle(tmp);
  }

  @Override
  protected void addInformation(IIdentifier identifier, T data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Adding " + identifier + " has " + data);

    Set<IIdentifier> identifiers = _valueMap.get(data);
    if (identifiers == null)
    {
      identifiers = FastSetFactory.newInstance();
      _valueMap.put(data, identifiers);
    }
    identifiers.add(identifier);
    _currentValues.put(identifier, data);
  }

  @Override
  protected T removeInformation(IIdentifier identifier)
  {
    T value = _currentValues.get(identifier);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Removing " + identifier + " has " + value);

    if (value != null)
    {
      Set<IIdentifier> identifiers = _valueMap.get(value);
      if (identifiers != null)
      {
        identifiers.remove(identifier);
        if (identifiers.size() == 0)
        {
          _valueMap.remove(value);
          FastSetFactory.recycle(identifiers);
        }
      }
    }

    return value;
  }

  // public void fillSlotValues(ChunkTypeRequest mutableRequest,
  // IIdentifier identifier, IChunk encodedChunk,
  // ChunkTypeRequest originalSearchRequest)
  // {
  // String slotName = getRelevantSlotName();
  // if (slotName == null) return;
  //
  // T value = getCurrentValue(identifier);
  //
  // if (value != null)
  // mutableRequest.addSlot(new BasicSlot(slotName, value));
  // else if (LOGGER.isWarnEnabled())
  // LOGGER.warn("No " + slotName + " information for " + identifier);
  // }

  @Override
  public String toString()
  {
    return getClass().getSimpleName();
  }
}
