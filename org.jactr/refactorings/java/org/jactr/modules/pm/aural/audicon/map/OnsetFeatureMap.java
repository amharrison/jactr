/*
 * Created on Jun 27, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.aural.audicon.map;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMapListener;

/**
 * @author developer
 */
public class OnsetFeatureMap implements IAuralFeatureMap<Double>
{
  /**
   * logger definition
   */
  static private final Log                  LOGGER = LogFactory
                                                       .getLog(OnsetFeatureMap.class);

  private IAuralPropertyHandler             _propertyHandler;

  private TreeMap<Double, Set<IIdentifier>> _onsetMap;

  private Map<IIdentifier, Double>          _identifierOnsetMap;

  private IAuralModule                      _module;

  public OnsetFeatureMap(IAuralModule module)
  {
    _module = module;
    _propertyHandler = new DefaultAuralPropertyHandler();
    _onsetMap = new TreeMap<Double, Set<IIdentifier>>();
    _identifierOnsetMap = new HashMap<IIdentifier, Double>();
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#clear()
   */
  public void clear()
  {
    _onsetMap.clear();
    _identifierOnsetMap.clear();
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#dispose()
   */
  public void dispose()
  {
    clear();
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#fillSlotValues(ChunkTypeRequest,
   *      org.commonreality.identifier.IIdentifier,
   *      IChunk, ChunkTypeRequest)
   */
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk, ChunkTypeRequest originalSearchRequest)
  {
    Double then = _identifierOnsetMap.get(identifier);
    if (then == null) return;

    mutableRequest.addSlot(new BasicSlot(IAuralModule.ONSET_SLOT, then));
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#getCandidateRealObjects(ChunkTypeRequest, Set)
   */
  public void getCandidateRealObjects(
      ChunkTypeRequest request, Set<IIdentifier> container)
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();

    boolean firstIteration = true;
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equalsIgnoreCase(IAuralModule.ONSET_SLOT))
      {
        Object value = cSlot.getValue();

        if (_module.getLowestChunk().equals(value))
          value = _onsetMap.firstKey();
        else if (_module.getHighestChunk().equals(value))
          value = _onsetMap.lastKey();

        Number val = (Number) value;

        Collection<IIdentifier> eval = new HashSet<IIdentifier>();
        if (val == null)
        {
          if (IConditionalSlot.NOT_EQUALS == cSlot.getCondition())
            eval.addAll(all());
        }
        else
          switch (cSlot.getCondition())
          {
            case IConditionalSlot.EQUALS:
              eval.addAll(equal(val.doubleValue()));
              break;
            case IConditionalSlot.NOT_EQUALS:
              eval.addAll(not(val.doubleValue()));
              break;
            case IConditionalSlot.GREATER_THAN_EQUALS:
              eval.addAll(equal(val.doubleValue()));
            case IConditionalSlot.GREATER_THAN:
              eval.addAll(greaterThan(val.doubleValue()));
              break;
            case IConditionalSlot.LESS_THAN_EQUALS:
              eval.addAll(equal(val.doubleValue()));
            case IConditionalSlot.LESS_THAN:
              eval.addAll(lessThan(val.doubleValue()));
              break;
            default:
              if (LOGGER.isWarnEnabled())
                LOGGER.warn(getClass().getSimpleName() +
                    " can only handle =,!=,<,<=,>,>=");
              break;

          }

        if (eval.size() == 0) break;

        if (firstIteration)
        {
          identifiers.addAll(eval);
          firstIteration = false;
        }
        else
          identifiers.retainAll(eval);
      }
  }

  protected Collection<IIdentifier> all()
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Set<IIdentifier> ids : _onsetMap.values())
      identifiers.addAll(ids);
    return identifiers;
  }

  protected Collection<IIdentifier> not(Double when)
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Map.Entry<Double, Set<IIdentifier>> entry : _onsetMap.entrySet())
      if (!entry.getKey().equals(when)) identifiers.addAll(entry.getValue());
    return identifiers;
  }

  protected Collection<IIdentifier> equal(Double when)
  {
    Set<IIdentifier> identifiers = _onsetMap.get(when);
    if (identifiers == null) identifiers = Collections.EMPTY_SET;
    return identifiers;
  }

  protected Collection<IIdentifier> lessThan(Double when)
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Set<IIdentifier> ids : _onsetMap.headMap(when).values())
      identifiers.addAll(ids);
    return identifiers;
  }

  protected Collection<IIdentifier> greaterThan(Double when)
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Set<IIdentifier> ids : _onsetMap.tailMap(when).values())
      identifiers.addAll(ids);

    Set<IIdentifier> equals = _onsetMap.get(when);
    if (equals != null) identifiers.removeAll(equals);

    return identifiers;
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#isInterestedIn(ChunkTypeRequest)
   */
  public boolean isInterestedIn(ChunkTypeRequest request)
  {
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equals(IAuralModule.ONSET_SLOT)) return true;
    return false;
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectAdded(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectAdded(IAfferentObject object)
  {
    add(object.getIdentifier());
  }

  protected void add(IIdentifier identifier)
  {
    Double now = ACTRRuntime.getRuntime().getClock(_module.getModel())
        .getTime();

    Set<IIdentifier> identifiers = _onsetMap.get(now);
    if (identifiers == null)
    {
      identifiers = new HashSet<IIdentifier>();
      _onsetMap.put(now, identifiers);
    }
    identifiers.add(identifier);

    _identifierOnsetMap.put(identifier, now);
  }

  protected void remove(IIdentifier identifier)
  {
    Double then = _identifierOnsetMap.remove(identifier);
    if (then == null) return;

    Set<IIdentifier> identifiers = _onsetMap.get(then);
    if (identifiers != null)
      if (identifiers.remove(identifier))
        if (identifiers.size() == 0) _onsetMap.remove(then);
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectRemoved(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectRemoved(IAfferentObject object)
  {
    // noop
  }

  public void removeFeatureFor(IAfferentObject object)
  {
    remove(object.getIdentifier());
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectUpdated(org.commonreality.object.IAfferentObject,
   *      org.commonreality.object.delta.IObjectDelta)
   */
  public void afferentObjectUpdated(IAfferentObject object, IObjectDelta delta)
  {
    // noop
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#isInterestedIn(org.commonreality.object.IAfferentObject)
   */
  public boolean isInterestedIn(IAfferentObject object)
  {
    return _identifierOnsetMap.containsKey(object.getIdentifier()) ||
        _propertyHandler.hasModality(object) &&
        _propertyHandler.isAudible(object);
  }

  public void addListener(IFeatureMapListener listener, Executor executor)
  {
    // TODO Auto-generated method stub
    
  }

  public Double getInformation(IIdentifier identifier)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public IPerceptualMemory getPerceptualMemory()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public void removeListener(IFeatureMapListener listener)
  {
    // TODO Auto-generated method stub
    
  }

  public void setPerceptualMemory(IPerceptualMemory memory)
  {
    // TODO Auto-generated method stub
    
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub
    
  }

}
