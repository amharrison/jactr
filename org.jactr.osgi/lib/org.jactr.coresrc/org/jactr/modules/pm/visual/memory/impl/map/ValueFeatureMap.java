package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
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
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.modules.pm.visual.IVisualModule;

public class ValueFeatureMap extends AbstractVisualFeatureMap<String>
{
  /**
   * Logger definition
   */
  static private final transient Log    LOGGER = LogFactory
                                                   .getLog(ValueFeatureMap.class);

  private Map<String, Set<IIdentifier>> _byToken;

  private Map<IIdentifier, String>      _byIdenitifer;

  public ValueFeatureMap()
  {
    super(IVisualModule.VALUE_SLOT, IVisualPropertyHandler.TOKEN);
    _byToken = new TreeMap<String, Set<IIdentifier>>();
    _byIdenitifer = new HashMap<IIdentifier, String>();
  }

  private Set<IIdentifier> acquireSet()
  {
    return FastSetFactory.newInstance();
  }

  private void releaseSet(Set<IIdentifier> set)
  {
    FastSetFactory.recycle(set);
  }

  @Override
  protected void addInformation(IIdentifier identifier, String data)
  {
    _byIdenitifer.put(identifier, data);
    Set<IIdentifier> identifiers = _byToken.get(data);
    if (identifiers == null)
    {
      identifiers = acquireSet();
      _byToken.put(data, identifiers);
    }
    identifiers.add(identifier);
  }

  @Override
  protected String removeInformation(IIdentifier identifier)
  {
    String rtn = getCurrentValue(identifier);
    if (rtn != null)
    {
      _byIdenitifer.remove(identifier);
      Set<IIdentifier> identifiers = _byToken.get(rtn);
      if (identifiers != null)
      {
        identifiers.remove(identifier);
        if (identifiers.size() == 0)
        {
          _byToken.remove(rtn);
          releaseSet(identifiers);
        }
      }
    }
    return rtn;
  }

  @Override
  protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results)
  {
    boolean firstInsertion = true;
    Set<IIdentifier> tmp = FastSetFactory.newInstance();

    for (IConditionalSlot slot : request.getConditionalSlots())
      if (slot.getName().equals(IVisualModule.VALUE_SLOT))
      {
        tmp.clear();
        String value = transformValue(slot.getValue());
        switch (slot.getCondition())
        {
          case IConditionalSlot.NOT_EQUALS:
            not(value, tmp);
            break;
          default:
            equals(value, tmp);
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

  private void not(String value, Set<IIdentifier> tmp)
  {
    for (Map.Entry<String, Set<IIdentifier>> entry : _byToken.entrySet())
      if (entry.getKey() != null && !entry.getKey().equals(value)
          || entry.getKey() == null && value != null)
        tmp.addAll(entry.getValue());
  }

  private void equals(String value, Set<IIdentifier> tmp)
  {
    Set<IIdentifier> ids = _byToken.get(value);
    if(ids!=null)
      tmp.addAll(ids);
  }

  private String transformValue(Object value)
  {
    if (value == null) return null;
    if (value instanceof String) return (String) value;
    return value.toString();
  }

  @Override
  protected void clearInternal()
  {
    _byIdenitifer.clear();
    _byToken.clear();
  }

  @Override
  protected String extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      return getHandler().getToken(afferentObject);
    }
    catch (UnknownPropertyNameException e)
    {
      LOGGER.error("Could not extract token information from  "
          + afferentObject.getIdentifier(), e);
      return null;
    }
  }

  @Override
  protected String getCurrentValue(IIdentifier identifier)
  {
    return _byIdenitifer.get(identifier);
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub

  }

}
