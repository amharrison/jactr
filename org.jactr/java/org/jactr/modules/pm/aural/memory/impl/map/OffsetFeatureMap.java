package org.jactr.modules.pm.aural.memory.impl.map;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.manager.event.IAfferentListener;
import org.commonreality.object.manager.event.IObjectEvent;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.modules.pm.IPerceptualModule;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.impl.INeedsAgent;

/**
 * consider using sorted but use supers. and override the defaults
 * 
 * @author harrison
 */
public class OffsetFeatureMap extends AbstractAuralFeatureMap<Double> implements
    INeedsAgent
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(OffsetFeatureMap.class);

  /**
   * used to record the actual end time of a sound
   */
  private IAfferentListener          _trueRemoveListener;

  private IAgent                     _agent;

  private Map<IIdentifier, Double>   _completedSounds;

  private Set<IIdentifier>           _activeSounds;

  public OffsetFeatureMap()
  {
    this(IAuralModule.OFFSET_SLOT);
  }

  public OffsetFeatureMap(String requestSlotName)
  {
    super(requestSlotName, null);
    _activeSounds = new HashSet<IIdentifier>();
    _completedSounds = new HashMap<IIdentifier, Double>();
    _trueRemoveListener = new IAfferentListener() {

      public void objectsAdded(IObjectEvent<IAfferentObject, ?> addEvent)
      {

      }

      public void objectsRemoved(IObjectEvent<IAfferentObject, ?> removeEvent)
      {
        double now = getCurrentTime();
        /*
         * record the time the sound actually stopped
         */
        try
        {
          getLock().lock();
          for (IAfferentObject object : removeEvent.getObjects())
          {
            IIdentifier id = object.getIdentifier();
            _completedSounds.put(id, now);
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Sound : " + id + " has actually ended at " + now);
          }
        }
        finally
        {
          getLock().unlock();
        }
      }

      public void objectsUpdated(IObjectEvent<IAfferentObject, ?> updateEvent)
      {

      }

    };
  }

  public void setAgent(IAgent agent)
  {
    if (agent == null)
    {
      if (_agent != null)
        _agent.getAfferentObjectManager().removeListener(_trueRemoveListener);
      _agent = null;
    }
    else
    {
      _agent = agent;
      /*
       * attach inline so that we are notified of removal before the perceptual
       * memory. this allows us to handle the perceptual delay w/ no collisions
       * when delay is 0.
       */
      _agent.getAfferentObjectManager().addListener(_trueRemoveListener,
          ExecutorServices.INLINE_EXECUTOR);
    }
  }
  
  protected IAgent getAgent()
  {
    return _agent;
  }
  
  protected double getCurrentTime()
  {
    return _agent.getClock().getTime();
  }

  @Override
  protected void addInformation(IIdentifier identifier, Double data)
  {
    _activeSounds.add(identifier);
  }

  @Override
  protected void clearInternal()
  {
    _activeSounds.clear();
    _completedSounds.clear();
  }

  @Override
  protected Double extractInformation(IAfferentObject afferentObject)
  {
    return _agent.getClock().getTime();
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
        if (slot.getValue() instanceof Number)
        {
          tmp.clear();
          double value = ((Number) slot.getValue()).doubleValue();
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
        else
          LOGGER.warn(this + " " + slot + " value is not a number");

    FastSetFactory.recycle(tmp);
  }

  protected void not(double time, Set<IIdentifier> results)
  {
    double now = getCurrentTime();
    if (now != time) results.addAll(_activeSounds);

    for (Map.Entry<IIdentifier, Double> entry : _completedSounds.entrySet())
      if (entry.getValue() != time) results.add(entry.getKey());
  }

  protected void equals(double time, Set<IIdentifier> results)
  {
    // ideally we should use a tolerance..
    double now = getCurrentTime();
    if (now == time) results.addAll(_activeSounds);

    for (Map.Entry<IIdentifier, Double> entry : _completedSounds.entrySet())
      if (entry.getValue() == time) results.add(entry.getKey());
  }

  protected void lessThan(double time, Set<IIdentifier> results)
  {
    double now = getCurrentTime();
    if (now < time) results.addAll(_activeSounds);

    for (Map.Entry<IIdentifier, Double> entry : _completedSounds.entrySet())
      if (entry.getValue() < time) results.add(entry.getKey());
  }

  protected void greaterThan(double time, Set<IIdentifier> results)
  {
    double now = getCurrentTime();
    if (now > time) results.addAll(_activeSounds);

    for (Map.Entry<IIdentifier, Double> entry : _completedSounds.entrySet())
      if (entry.getValue() > time) results.add(entry.getKey());
  }

  /**
   * returns current time or end time if available
   * 
   * @param identifier
   * @return
   * @see org.jactr.modules.pm.common.memory.map.AbstractFeatureMap#getCurrentValue(org.commonreality.identifier.IIdentifier)
   */
  @Override
  protected Double getCurrentValue(IIdentifier identifier)
  {
    Double completedTime = _completedSounds.get(identifier);
    if (completedTime == null) completedTime = getCurrentTime();

    return completedTime;
  }

  @Override
  protected Double removeInformation(IIdentifier identifier)
  {
    Double rtn = getCurrentValue(identifier);
    _activeSounds.remove(identifier);
    return rtn;
  }

  @Override
  protected void objectRemoved(IAfferentObject object, Double data)
  {
    super.objectRemoved(object, data);
    IIdentifier id = object.getIdentifier();
    if (_completedSounds.remove(id) != null && LOGGER.isDebugEnabled())
      LOGGER.debug("Sound: " + id + " has been removed from audicon");
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    for (IConditionalSlot cSlot : request.getConditionalSlots())
    {
      String name = cSlot.getName();
      if (IPerceptualModule.LOWEST_CHUNK.equalsIgnoreCase(name))
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(name+" filtering not implemented (yet)");
      }
      else
        if(IPerceptualModule.HIGHEST_CHUNK.equalsIgnoreCase(name)) if (LOGGER.isWarnEnabled())
          LOGGER.warn(name+" filtering not implemented (yet)");
    }
    
  }

}
