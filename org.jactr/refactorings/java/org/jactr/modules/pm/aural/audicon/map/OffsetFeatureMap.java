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
import java.util.Iterator;
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
public class OffsetFeatureMap implements IAuralFeatureMap<Double>
{
  /**
   * logger definition
   */
  static private final Log                  LOGGER = LogFactory
                                                       .getLog(OffsetFeatureMap.class);

  private IAuralPropertyHandler             _propertyHandler;

  private TreeMap<Double, Set<IIdentifier>> _offsetMap;

  private Map<IIdentifier, Double>          _activeSounds;

  private Map<IIdentifier, Double>          _identifierOffsetMap;

  private Map<IIdentifier, Double>          _removeAt;

  private IAuralModule                      _module;

  public OffsetFeatureMap(IAuralModule module)
  {
    _module = module;
    _propertyHandler = new DefaultAuralPropertyHandler();
    _activeSounds = new HashMap<IIdentifier, Double>();
    _offsetMap = new TreeMap<Double, Set<IIdentifier>>();
    _identifierOffsetMap = new HashMap<IIdentifier, Double>();
    _removeAt = new HashMap<IIdentifier, Double>();
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#clear()
   */
  public void clear()
  {
    _offsetMap.clear();
    _identifierOffsetMap.clear();
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
    Double then = _identifierOffsetMap.get(identifier);
    if (then == null) return;

    mutableRequest.addSlot(new BasicSlot(IAuralModule.OFFSET_SLOT, then));
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
      if (cSlot.getName().equalsIgnoreCase(IAuralModule.OFFSET_SLOT))
      {
        Object value = cSlot.getValue();

        if (_module.getLowestChunk().equals(value))
          value = _offsetMap.firstKey();
        else if (_module.getHighestChunk().equals(value))
          value = _offsetMap.lastKey();

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
    for (Set<IIdentifier> ids : _offsetMap.values())
      identifiers.addAll(ids);
    return identifiers;
  }

  protected Collection<IIdentifier> not(Double when)
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Map.Entry<Double, Set<IIdentifier>> entry : _offsetMap.entrySet())
      if (!entry.getKey().equals(when)) identifiers.addAll(entry.getValue());
    return identifiers;
  }

  protected Collection<IIdentifier> equal(Double when)
  {
    Set<IIdentifier> identifiers = _offsetMap.get(when);
    if (identifiers == null) identifiers = Collections.EMPTY_SET;
    return identifiers;
  }

  protected Collection<IIdentifier> lessThan(Double when)
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Set<IIdentifier> ids : _offsetMap.headMap(when).values())
      identifiers.addAll(ids);
    return identifiers;
  }

  protected Collection<IIdentifier> greaterThan(Double when)
  {
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Set<IIdentifier> ids : _offsetMap.tailMap(when).values())
      identifiers.addAll(ids);

    Set<IIdentifier> equals = _offsetMap.get(when);
    if (equals != null) identifiers.removeAll(equals);

    return identifiers;
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#isInterestedIn(ChunkTypeRequest)
   */
  public boolean isInterestedIn(ChunkTypeRequest request)
  {
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equals(IAuralModule.OFFSET_SLOT)) return true;
    return false;
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectAdded(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectAdded(IAfferentObject object)
  {
    _activeSounds.put(object.getIdentifier(), ACTRRuntime.getRuntime()
        .getClock(_module.getModel()).getTime());
  }

  protected void add(IIdentifier identifier)
  {
    Double then = _activeSounds.remove(identifier);
    if (then == null) return;

    double now = ACTRRuntime.getRuntime().getClock(_module.getModel())
        .getTime();
    Double duration = now - then;

    Set<IIdentifier> identifiers = _offsetMap.get(duration);
    if (identifiers == null)
    {
      identifiers = new HashSet<IIdentifier>();
      _offsetMap.put(duration, identifiers);
    }
    identifiers.add(identifier);

    _identifierOffsetMap.put(identifier, duration);

    _removeAt.put(identifier, now + _module.getAuralDecayTime());

    /*
     * and let's sweep through the list of sounds that need to be removed from
     * our cache..
     */
    Iterator<Map.Entry<IIdentifier, Double>> iterator = _removeAt.entrySet()
        .iterator();
    while (iterator.hasNext())
    {
      Map.Entry<IIdentifier, Double> removal = iterator.next();
      if (removal.getValue() < now)
      {
        remove(removal.getKey());
        iterator.remove();
      }
    }

  }

  protected void remove(IIdentifier identifier)
  {
    Double duration = _identifierOffsetMap.remove(identifier);
    if (duration == null) return;

    Set<IIdentifier> identifiers = _offsetMap.get(duration);
    if (identifiers != null)
      if (identifiers.remove(identifier))
        if (identifiers.size() == 0) _offsetMap.remove(duration);
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectRemoved(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectRemoved(IAfferentObject object)
  {
    add(object.getIdentifier());
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
    return _identifierOffsetMap.containsKey(object.getIdentifier()) ||
        _propertyHandler.hasModality(object);
  }

  /**
   * @see org.jactr.modules.pm.aural.audicon.map.IAuralFeatureMap#removeFeatureFor(org.commonreality.object.IAfferentObject)
   */
  public void removeFeatureFor(IAfferentObject object)
  {
    remove(object.getIdentifier());
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
