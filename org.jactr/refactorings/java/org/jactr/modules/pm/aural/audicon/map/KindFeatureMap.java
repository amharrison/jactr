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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMapListener;

/**
 * @author developer
 */
public class KindFeatureMap implements IAuralFeatureMap<String[]>
{
  /**
   * logger definition
   */
  static private final Log              LOGGER = LogFactory
                                                   .getLog(KindFeatureMap.class);

  private IAuralPropertyHandler         _propertyHandler;

  private Map<String, Set<IIdentifier>> _kindMap;

  private Map<IIdentifier, Set<String>> _identifierKindMap;

  private IAuralModule                  _module;

  public KindFeatureMap(IAuralModule module)
  {
    _module = module;
    _propertyHandler = new DefaultAuralPropertyHandler();
    _kindMap = new TreeMap<String, Set<IIdentifier>>();
    _identifierKindMap = new HashMap<IIdentifier, Set<String>>();
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#clear()
   */
  public void clear()
  {
    _kindMap.clear();
    _identifierKindMap.clear();
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#dispose()
   */
  public void dispose()
  {
    clear();
  }

  protected Collection<String> getKinds(IAfferentObject object)
  {
    String[] kinds = new String[0];
    try
    {
      kinds = _propertyHandler.getTypes(object);
    }
    catch (UnknownPropertyNameException e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No kinds associated with " + object.getIdentifier(), e);
    }

    return Arrays.asList(kinds);
  }

  protected String toKindString(Object object)
  {
    if (object instanceof IChunkType)
      return ((IChunkType) object).getSymbolicChunkType().getName();

    if (object instanceof IChunk)
      return ((IChunk) object).getSymbolicChunk().getName();

    if (object != null) return object.toString();

    return null;
  }

  protected Object toKindObject(String kind)
  {
    IDeclarativeModule decM = _module.getModel().getDeclarativeModule();
    try
    {
      IChunkType ct = decM.getChunkType(kind).get();
      if (ct != null) return ct;

      IChunk c = decM.getChunk(kind).get();
      if (c != null) return c;

      return kind;
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Could not get chunk or chunktype for " + kind, e);
      return kind;
    }
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#fillSlotValues(ChunkTypeRequest,
   *      org.commonreality.identifier.IIdentifier,
   *      IChunk, ChunkTypeRequest)
   */
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk, ChunkTypeRequest originalSearchRequest)
  {
    Collection<String> kinds = _identifierKindMap.get(identifier);
    if (kinds == null) return;

    for (String kind : kinds)
    {
      Object kindObject = toKindObject(kind);
      boolean matched = true;

      for (IConditionalSlot slot : originalSearchRequest.getConditionalSlots())
        if (slot.getName().equalsIgnoreCase(IAuralModule.KIND_SLOT))
          matched &= slot.matchesCondition(kindObject);

      if (matched)
      {
        mutableRequest
            .addSlot(new BasicSlot(IAuralModule.KIND_SLOT, kindObject));
        return;
      }
    }
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
      if (cSlot.getName().equalsIgnoreCase(IAuralModule.KIND_SLOT))
      {
        Object value = toKindString(cSlot.getValue());
        Collection<IIdentifier> eval = Collections.EMPTY_LIST;
        switch (cSlot.getCondition())
        {
          case IConditionalSlot.EQUALS:
            if (value != null) eval = equals(value.toString());
            break;
          case IConditionalSlot.NOT_EQUALS:
            if (value != null)
              eval = not(cSlot.getValue().toString());
            else
              eval = all();
            break;
          default:
            if (LOGGER.isWarnEnabled())
              LOGGER.warn(getClass().getSimpleName() +
                  " can only handle equals and not equals");
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
    for (Set<IIdentifier> ids : _kindMap.values())
      identifiers.addAll(ids);
    return identifiers;
  }

  protected Collection<IIdentifier> not(String kind)
  {
    kind = kind.toLowerCase();
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    for (Map.Entry<String, Set<IIdentifier>> entry : _kindMap.entrySet())
      if (!entry.getKey().equalsIgnoreCase(kind))
        identifiers.addAll(entry.getValue());
    return identifiers;
  }

  protected Collection<IIdentifier> equals(String kind)
  {
    kind = kind.toLowerCase();
    Set<IIdentifier> identifiers = _kindMap.get(kind);
    if (identifiers == null) identifiers = Collections.EMPTY_SET;
    return identifiers;
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#isInterestedIn(ChunkTypeRequest)
   */
  public boolean isInterestedIn(ChunkTypeRequest request)
  {
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equals(IAuralModule.KIND_SLOT)) return true;
    return false;
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectAdded(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectAdded(IAfferentObject object)
  {
    add(object.getIdentifier(), getKinds(object));
  }

  protected void add(IIdentifier identifier, Collection<String> kinds)
  {
    for (String kind : kinds)
    {
      kind = kind.toLowerCase();
      Set<IIdentifier> identifiers = _kindMap.get(kind);
      if (identifiers == null)
      {
        identifiers = new HashSet<IIdentifier>();
        _kindMap.put(kind, identifiers);
      }
      identifiers.add(identifier);
    }

    Set<String> objectKinds = _identifierKindMap.get(identifier);
    if (objectKinds == null)
    {
      objectKinds = new TreeSet<String>();
      _identifierKindMap.put(identifier, objectKinds);
    }
    objectKinds.addAll(kinds);
  }

  protected void remove(IIdentifier identifier)
  {
    Set<String> kinds = _identifierKindMap.remove(identifier);
    if (kinds == null) return;

    for (String kind : kinds)
    {
      kind = kind.toLowerCase();
      Set<IIdentifier> identifiers = _kindMap.get(kind);
      if (identifiers != null)
        if (identifiers.remove(identifier))
          if (identifiers.size() == 0) _kindMap.remove(kind);
    }
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectRemoved(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectRemoved(IAfferentObject object)
  {
    // noop
    remove(object.getIdentifier());
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectUpdated(org.commonreality.object.IAfferentObject,
   *      org.commonreality.object.delta.IObjectDelta)
   */
  public void afferentObjectUpdated(IAfferentObject object, IObjectDelta delta)
  {
    remove(object.getIdentifier());
    add(object.getIdentifier(), getKinds(object));
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#isInterestedIn(org.commonreality.object.IAfferentObject)
   */
  public boolean isInterestedIn(IAfferentObject object)
  {
    return _identifierKindMap.containsKey(object.getIdentifier()) ||
        _propertyHandler.hasModality(object) &&
        _propertyHandler.isAudible(object) &&
        _propertyHandler.hasProperty(IAuralPropertyHandler.TYPE, object);
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

  public String[] getInformation(IIdentifier identifier)
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
