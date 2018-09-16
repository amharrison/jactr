package org.jactr.modules.pm.aural.memory.impl;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.logging.IMessageBuilder;
import org.jactr.core.logging.Logger;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.memory.IAuralMemory;
import org.jactr.modules.pm.aural.memory.impl.encoder.DigitAuralEncoder;
import org.jactr.modules.pm.aural.memory.impl.encoder.SpeechAuralEncoder;
import org.jactr.modules.pm.aural.memory.impl.encoder.ToneAuralEncoder;
import org.jactr.modules.pm.aural.memory.impl.encoder.WordAuralEncoder;
import org.jactr.modules.pm.aural.memory.impl.filter.AttendedAudioEventFilter;
import org.jactr.modules.pm.aural.memory.impl.map.AudibleFeatureMap;
import org.jactr.modules.pm.aural.memory.impl.map.DurationFeatureMap;
import org.jactr.modules.pm.aural.memory.impl.map.FINSTAuralFeatureMap;
import org.jactr.modules.pm.aural.memory.impl.map.KindFeatureMap;
import org.jactr.modules.pm.aural.memory.impl.map.OffsetFeatureMap;
import org.jactr.modules.pm.aural.memory.impl.map.OnsetFeatureMap;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;
import org.jactr.modules.pm.common.memory.filter.NumericIndexFilter;
import org.jactr.modules.pm.common.memory.impl.AbstractPerceptualMemory;
import org.jactr.modules.pm.common.memory.impl.DelayableAfferentObjectListener;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

public class DefaultAuralMemory extends AbstractPerceptualMemory implements
    IAuralMemory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultAuralMemory.class);

  private IChunk                     _notAvailableChunk;


  public DefaultAuralMemory(IAuralModule module,
      IActivePerceptListener listener)
  {
    super(module, new AuralEventIndexManager(module));
    // addListener(listener, ExecutorServices.INLINE_EXECUTOR);
    
    addFilter(new NumericIndexFilter(IAuralModule.ONSET_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.OFFSET_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.PITCH_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.AZIMUTH_SLOT, true));
    addFilter(new NumericIndexFilter(IAuralModule.ELEVATION_SLOT, true));
    addFilter(new AttendedAudioEventFilter());
    
    addFeatureMap(new OnsetFeatureMap());
    addFeatureMap(new OffsetFeatureMap());
    addFeatureMap(new KindFeatureMap());
    addFeatureMap(new DurationFeatureMap());
    addFeatureMap(new AudibleFeatureMap());
    addFeatureMap(new FINSTAuralFeatureMap(module.getModel()));

    addEncoder(new SpeechAuralEncoder());
    addEncoder(new ToneAuralEncoder());
    addEncoder(new WordAuralEncoder());
    addEncoder(new DigitAuralEncoder());
  }


  public IAuralModule getAuralModule()
  {
    return (IAuralModule) getModule();
  }

  @Override
  public void attach(IAgent agent)
  {
    super.attach(agent);

    ((DelayableAfferentObjectListener) getAfferentObjectListener())
        .setPerceptualDelay(getAuralModule().getAuralDecayTime());

    ((AuralEventIndexManager) getIndexManager())
        .attach(getAfferentObjectListener());

    try
    {
      _notAvailableChunk = getModule().getModel().getDeclarativeModule()
          .getChunk(IStatusBuffer.ERROR_NO_LONGER_AVAILABLE_CHUNK).get();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not get no-longer available chunk ", e);
      _notAvailableChunk = getModule().getModel().getDeclarativeModule()
          .getErrorChunk();
    }
  }

  @Override
  public void detach()
  {
    ((AuralEventIndexManager) getIndexManager())
        .detach(getAfferentObjectListener());
    super.detach();
  }

  @Override
  protected void fillIndexChunk(IChunk indexChunk, IChunk encodedChunk,
      ChunkTypeRequest originalRequest, ChunkTypeRequest expandedRequest)
  {
    try
    {
      indexChunk.getWriteLock().lock();
      /*
       * now we just copy everything over, if possible
       */
      ISymbolicChunk sc = indexChunk.getSymbolicChunk();
      for (ISlot slot : expandedRequest.getSlots())
        try
        {
          Object value = slot.getValue();
          String name = slot.getName();
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Setting " + indexChunk + "." + name + "=" + value);
          ((IMutableSlot) sc.getSlot(name)).setValue(value);
        }
        catch (IllegalChunkStateException e)
        {
          // if the slot was a metaslot there will be an exception
        }
    }
    finally
    {
      indexChunk.getWriteLock().unlock();
    }

  }

  @Override
  protected boolean isAcceptable(IChunk encodedChunk,
      ChunkTypeRequest originalRequest)
  {
    return encodedChunk.getSymbolicChunk().getChunkType().isA(
        getAuralModule().getSoundChunkType());

  }
  
  @Override
  public void setParameter(String key, String value) {
	   if (key.indexOf('.') > 0)
	    {
	      /*
	       * might be a class
	       */
	      if (LOGGER.isDebugEnabled())
	        LOGGER.debug("Assuming " + key + " is a classname");
	      try
	      {
	        boolean shouldAdd = ParameterHandler.booleanInstance().coerce(value)
	            .booleanValue();
	        Class clazz = ParameterHandler.classInstance().coerce(key);

	        if (!shouldAdd)
	        {
	          for (IIndexFilter filter : getFilters(null))
	            if (filter.getClass().getName().equals(key)) removeFilter(filter);

	          for (IPerceptualEncoder encoder : getEncoders(null))
	            if (encoder.getClass().getName().equals(key))
	              removeEncoder(encoder);

	          for (IFeatureMap map : getFeatureMaps(null))
	            if (map.getClass().getName().equals(key)) removeFeatureMap(map);
	        }
	        else
	        {
	          Object instance = clazz.newInstance();

	          if (instance instanceof IIndexFilter)
	            addFilter((IIndexFilter) instance);
	          if (instance instanceof IFeatureMap)
	            addFeatureMap((IFeatureMap) instance);
	          if (instance instanceof IPerceptualEncoder)
	            addEncoder((IPerceptualEncoder) instance);
	        }
	      }
	      catch (Exception e)
	      {
	        if (LOGGER.isWarnEnabled())
	          LOGGER.warn("Could not process suspected class name " + key + " = "
	              + value, e);
	      }
	    }
	    else
	      super.setParameter(key, value);
  }

  @Override
  protected IChunk getRemovedErrorCodeChunk()
  {
    return _notAvailableChunk;
  }

  @Override
  protected PerceptualSearchResult select(
      Collection<PerceptualSearchResult> results)
  {
    IMessageBuilder mb = null;
    if (Logger.hasLoggers(getModule().getModel()))
    {
      mb = Logger.messageBuilder();
      IMessageBuilder fmb = mb;
      mb.append("Aural search candidates :");
      results.forEach(
          (psr) -> {
        fmb.append("[");
        fmb.append(psr.getPercept().getSymbolicChunk().getName());
        fmb.append(" @ ")
            .append(
                psr.getLocation().getSymbolicChunk().getName());
        fmb.append("] ");
          });

    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("All results : " + results);
    PerceptualSearchResult rtn = null;
    if (results.size() > 0)
    {
      // grab one at random
      Iterator<PerceptualSearchResult> itr = results.iterator();
      int which = (int) Math.floor(Math.random() * results.size());

      if (mb != null) mb.prepend(String.format("Selecting %d", which));

      for (int i = 0; i <= which && itr.hasNext(); i++)
        rtn = itr.next();
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Selected : [%s]=[%s]",
          rtn != null ? rtn.getLocation() : "null", rtn));

    if (mb != null)
      Logger.log(getModule().getModel(), Logger.Stream.AURAL, mb);
    return rtn;
  }

}
