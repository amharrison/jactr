/*
 * Created on Jun 26, 2007 Copyright (C) 2001-2007, Anthony Harrison
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
package org.jactr.modules.pm.aural.audicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.audicon.encoder.GenericSoundEncoder;
import org.jactr.modules.pm.aural.audicon.encoder.IAuralChunkEncoder;
import org.jactr.modules.pm.aural.audicon.map.FINSTFeatureMap;
import org.jactr.modules.pm.aural.audicon.map.KindFeatureMap;
import org.jactr.modules.pm.aural.audicon.map.LocationFeatureMap;
import org.jactr.modules.pm.aural.audicon.map.OffsetFeatureMap;
import org.jactr.modules.pm.aural.audicon.map.OnsetFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

/**
 * @author developer
 */
public class DefaultAudicon implements IAudicon
{

  /**
   * logger definition
   */
  static private final Log               LOGGER           = LogFactory
                                                              .getLog(DefaultAudicon.class);

  private Collection<IFeatureMap>        _featureMaps;

  private IFINSTFeatureMap               _finstFeatureMap;

  private Collection<IAuralChunkEncoder> _soundEncoders;

  protected int                          _audioEventIndex = 0;

  protected IAuralModule                 _module;

  protected double                       _lastChangeTime  = 0;

  protected Map<IIdentifier, IChunk>     _activeAudioEvents;

  protected IChunkListener               _chunkListener;

  public DefaultAudicon(IAuralModule module)
  {
    _module = module;
    _soundEncoders = new ArrayList<IAuralChunkEncoder>();
    _featureMaps = new ArrayList<IFeatureMap>();

    _activeAudioEvents = new HashMap<IIdentifier, IChunk>();

    _chunkListener = new ChunkListenerAdaptor() {
      public void chunkEncoded(ChunkEvent ce)
      {
//        IIdentifier identifier = (IIdentifier) ce.getSource().getMetaData(
//            IAfferentObjectEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
//
//        _activeAudioEvents.remove(identifier);
      }

      public void mergingInto(ChunkEvent ce)
      {
//        IIdentifier identifier = (IIdentifier) ce.getSource().getMetaData(
//            IAfferentObjectEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
//
//        _activeAudioEvents.remove(identifier);
      }
    };

    /*
     * add the finst feature map
     */
    _finstFeatureMap = new FINSTFeatureMap(module) {

      @Override
      public void afferentObjectAdded(IAfferentObject object)
      {
        super.afferentObjectAdded(object);
        /*
         * we create the chunks in advance..
         */
        try
        {
          IChunk audioEvent = _module.getModel().getDeclarativeModule()
              .createChunk(getModule().getAudioEventChunkType(),
                  "audioEvent-" + (++_audioEventIndex)).get();

          audioEvent.addListener(_chunkListener,
              ExecutorServices.INLINE_EXECUTOR);

          _activeAudioEvents.put(object.getIdentifier(), audioEvent);
        }
        catch (Exception e)
        {

        }
        _lastChangeTime = ACTRRuntime.getRuntime().getClock(_module.getModel())
            .getTime();
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(object.getIdentifier() + " added @ " + _lastChangeTime);
      }

      /*
       * the unavailability of a sound should not make a new buffer stuff search
       * possible..
       */
      // @Override
      // public void afferentObjectRemoved(IAfferentObject object)
      // {
      // super.afferentObjectRemoved(object);
      // _lastChangeTime = ACTRRuntime.getRuntime().getClock(_module.getModel())
      // .getTime();
      // if (LOGGER.isDebugEnabled())
      // LOGGER
      // .debug(object.getIdentifier() + " removed @ " + _lastChangeTime);
      // }
      @Override
      public void afferentObjectUpdated(IAfferentObject object,
          IObjectDelta delta)
      {
        super.afferentObjectUpdated(object, delta);
        _lastChangeTime = ACTRRuntime.getRuntime().getClock(_module.getModel())
            .getTime();
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(object.getIdentifier() + " updated @ " + _lastChangeTime);
      }

      @Override
      public void removeFeatureFor(IAfferentObject object)
      {
        super.removeFeatureFor(object);
        _activeAudioEvents.remove(object.getIdentifier());
      }
    };

    addFeatureMap(_finstFeatureMap);

    /*
     * kind feature map
     */
    addFeatureMap(new KindFeatureMap(module));

    /*
     * and the onset map
     */
    addFeatureMap(new OnsetFeatureMap(module));
    addFeatureMap(new OffsetFeatureMap(module));

    /*
     * and the location map which still does not support internal
     */
    addFeatureMap(new LocationFeatureMap(module));

    addEncoder(new GenericSoundEncoder(module, _finstFeatureMap));
  }

  public void addEncoder(IAuralChunkEncoder soundEncoder)
  {
    _soundEncoders.add(soundEncoder);
  }

  public void removeEncoder(IAuralChunkEncoder soundEncoder)
  {
    _soundEncoders.remove(soundEncoder);
  }

  public Collection<IAuralChunkEncoder> getEncoders()
  {
    return Collections.unmodifiableCollection(_soundEncoders);
  }

  public void addFeatureMap(IFeatureMap featureMap)
  {
    _featureMaps.add(featureMap);
  }

  public void removeFeatureMap(IFeatureMap featureMap)
  {
    _featureMaps.remove(featureMap);
  }

  public Collection<IFeatureMap> getFeatureMaps()
  {
    return Collections.unmodifiableCollection(_featureMaps);
  }

  public IFINSTFeatureMap getFINSTFeatureMap()
  {
    return _finstFeatureMap;
  }

  /**
   * @see org.jactr.modules.pm.aural.audicon.IAudicon#clear()
   */
  public void clear()
  {
    _activeAudioEvents.clear();
    _finstFeatureMap.reset();
  }

  /**
   * @see org.jactr.modules.pm.aural.audicon.IAudicon#dispose()
   */
  public void dispose()
  {
    _soundEncoders.clear();
    _featureMaps.clear();
  }

  public IChunk getAudioEventFor(IIdentifier identifier)
  {
    return _activeAudioEvents.get(identifier);
  }

  /**
   * @see org.jactr.modules.pm.aural.audicon.IAudicon#getModule()
   */
  public IAuralModule getModule()
  {
    return _module;
  }

  /**
   * @see org.jactr.modules.pm.aural.audicon.IAudicon#getLastChangeTime()
   */
  public double getLastChangeTime()
  {
    return _lastChangeTime;
  }

}
