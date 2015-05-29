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
package org.jactr.modules.pm.aural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.pm.AbstractPerceptualModule;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.aural.event.AuralModuleEvent;
import org.jactr.modules.pm.aural.event.IAuralModuleListener;
import org.jactr.modules.pm.aural.memory.IAuralMemory;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;

/**
 * abstract implementation of the aural module that takes care of most of the
 * details. It handles parameter, FINST management, and buffer stuffing. It is
 * up to the extenders to deal with the actual attending, search, buffer and
 * memory creation.
 * 
 * @author developer
 */
public abstract class AbstractAuralModule extends AbstractPerceptualModule
    implements IAuralModule, IParameterized
{

  static public final String                                      ENABLE_BUFFER_STUFF_PARAM    = "EnableBufferStuff";

  static public final String                                      AURAL_DECAY_TIME_PARAM       = "AuralDecayTime";

  static public final String                                      ENCODING_TIME_EQUATION_PARAM = "AuralEncodingTimeEquationClass";

  /**
   * logger definition
   */
  static private final Log                                        LOGGER                       = LogFactory
                                                                                                   .getLog(AbstractAuralModule.class);

  private IModelListener                                          _modelListener;

  private IAuralActivationBuffer                                  _auralBuffer;

  private IAuralLocationBuffer                                    _auralLocationBuffer;

  private IAuralMemory                                            _auralMemory;

  private Map<String, String>                                     _deferredParameters;

  private IChunkType                                              _soundChunkType;

  private IChunkType                                              _audioEventChunkType;

  private IChunkType                                              _clearChunkType;

  private IChunk                                                  _lowestChunk;

  private IChunk                                                  _highestChunk;

  private IChunk                                                  _externalChunk;

  private IChunk                                                  _internalChunk;

  private TreeMap<IChunkType, Double>                             _recodeTimes;

  private ACTREventDispatcher<IAuralModule, IAuralModuleListener> _listener;

  private boolean                                                 _isBufferStuffEnabled        = false;

  private double                                                  _decayTime                   = 3;

  private IAuralEncodingTimeEquation                              _encodingTime                = new DefaultAuralEncodingTimeEquation();

  /**
   * @param name
   */
  public AbstractAuralModule()
  {
    super("aural");
    _deferredParameters = new TreeMap<String, String>();
    _recodeTimes = new TreeMap<IChunkType, Double>();
    _listener = new ACTREventDispatcher<IAuralModule, IAuralModuleListener>();
  }

  abstract protected IAuralMemory createAuralMemory();

  abstract protected IAuralLocationBuffer createAuralLocationBuffer();

  abstract protected IAuralActivationBuffer createAuralBuffer(
      IAuralLocationBuffer locationBuffer);

  @Override
  protected Collection<IActivationBuffer> createBuffers()
  {
    _auralLocationBuffer = createAuralLocationBuffer();
    _auralBuffer = createAuralBuffer(_auralLocationBuffer);

    ArrayList<IActivationBuffer> rtn = new ArrayList<IActivationBuffer>();
    rtn.add(_auralLocationBuffer);
    rtn.add(_auralBuffer);

    return rtn;
  }

  public void addListener(IAuralModuleListener listener, Executor executor)
  {
    _listener.addListener(listener, executor);
  }

  public void removeListener(IAuralModuleListener listener)
  {
    _listener.removeListener(listener);
  }

  public boolean hasListeners()
  {
    return _listener.hasListeners();
  }

  public void dispatch(AuralModuleEvent event)
  {
    if (_listener.hasListeners()) _listener.fire(event);
  }

  /**
   * @see org.jactr.core.module.AbstractModule#initialize()
   */
  @Override
  public void initialize()
  {
    _modelListener = new ModelListenerAdaptor() {

      double _lastStuffAttempt = -1;

      @Override
      public void cycleStarted(ModelEvent me)
      {
        if (isBufferStuffEnabled())
        {
          double lastTime = getAuralMemory().getLastChangeTime();
          if (lastTime >= _lastStuffAttempt)
          {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("attempting to stuff");
            getAuralLocationBuffer().checkForBufferStuff();
            _lastStuffAttempt = me.getSimulationTime();
          }
          else if (LOGGER.isDebugEnabled())
            LOGGER.debug("audicon has not changed since " + _lastStuffAttempt
                + " now: " + me.getSimulationTime());
        }
      }

    };

    IModel model = getModel();

    model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

    IDeclarativeModule decM = model.getDeclarativeModule();
    if (decM == null)
      throw new IllegalModuleStateException(
          "declarative module is not available");

    _soundChunkType = getNamedChunkType(SOUND_CHUNK_TYPE);
    _audioEventChunkType = getNamedChunkType(AUDIO_EVENT_CHUNK_TYPE);
    _clearChunkType = getNamedChunkType(CLEAR_CHUNK_TYPE);

    _highestChunk = getNamedChunk(HIGHEST_CHUNK);
    _lowestChunk = getNamedChunk(LOWEST_CHUNK);

    _internalChunk = getNamedChunk(INTERNAL_CHUNK);
    _externalChunk = getNamedChunk(EXTERNAL_CHUNK);

    setRecodeTime(getNamedChunkType(TONE_CHUNK_TYPE), 0.5);
    setRecodeTime(getNamedChunkType(DIGIT_CHUNK_TYPE), 0.285);
    setRecodeTime(getNamedChunkType(SPEECH_CHUNK_TYPE), 1);
    setRecodeTime(getNamedChunkType(WORD_CHUNK_TYPE), 0.3);

    _auralMemory = createAuralMemory();

    applyParameters();

    /*
     * we also add a listener to ourselves to keep track of the finsts..
     */
    IAuralModuleListener listener = new IAuralModuleListener() {

      public void moduleReset(IPerceptualMemoryModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void perceptAttended(IPerceptualMemoryModuleEvent event)
      {
        /*
         * flag it as attended
         */
        IChunk auralChunk = event.getChunk();
        IIdentifier identifier = (IIdentifier) auralChunk
            .getMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
        if (identifier != null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("assigning finst for " + auralChunk);
          getAuralMemory().getFINSTFeatureMap().flagAsAttended(identifier,
              auralChunk, getAuralMemory().getFINSTSpan());
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("could not find identifier for " + auralChunk);

      }

      public void perceptIndexFound(IPerceptualMemoryModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void parameterChanged(IParameterEvent pe)
      {

      }

    };

    addListener(listener, ExecutorServices.INLINE_EXECUTOR);

    /*
     * flag as new..
     */
    IActivePerceptListener finstListener = new IActivePerceptListener() {

      public void newPercept(IIdentifier identifier, IChunk chunk)
      {
        // this is quick enough that we can process it in line
        // and it may be needed in the search right now..
        IFINSTFeatureMap finstMap = getAuralMemory().getFINSTFeatureMap();
        if (finstMap != null && !finstMap.isAttended(identifier))
          finstMap.flagAsNew(identifier, chunk, getAuralMemory()
              .getNewFINSTOnsetDuration());
      }

      public void reencoded(IIdentifier identifier, IChunk oldChunk,
          IChunk newChunk)
      {
        // noop

      }

      public void removed(IIdentifier identifier, IChunk chunk)
      {
        // noop
      }

      public void updated(IIdentifier identifier, IChunk chunk)
      {
        // noop
      }

    };

    _auralMemory.addListener(finstListener, ExecutorServices.INLINE_EXECUTOR);

    super.initialize();
  }

  @Override
  protected void connectToCommonReality()
  {
    super.connectToCommonReality();
    /*
     * attach the visual memory
     */
    _auralMemory.attach(ACTRRuntime.getRuntime().getConnector()
        .getAgent(getModel()));
  }

  @Override
  protected void disconnectFromCommonReality()
  {
    super.disconnectFromCommonReality();
    _auralMemory.detach();
  }

  @Override
  public void dispose()
  {

    getModel().removeListener(_modelListener);

    super.dispose();
  }

  public boolean isBufferStuffEnabled()
  {
    return _isBufferStuffEnabled;
  }

  public IAuralMemory getAuralMemory()
  {
    return _auralMemory;
  }

  public IPerceptualMemory getPerceptualMemory()
  {
    return _auralMemory;
  }

  /**
   * @see org.jactr.modules.pm.aural.IAuralModule#getAuralActivationBuffer()
   */
  public IAuralActivationBuffer getAuralActivationBuffer()
  {
    return _auralBuffer;
  }

  /**
   * @see org.jactr.modules.pm.aural.IAuralModule#getAuralLocationBuffer()
   */
  public IAuralLocationBuffer getAuralLocationBuffer()
  {
    return _auralLocationBuffer;
  }

  public IAuralEncodingTimeEquation getEncodingTimeEquation()
  {
    return _encodingTime;
  }

  public IChunkType getClearChunkType()
  {
    return _clearChunkType;
  }

  public IChunkType getSoundChunkType()
  {
    return _soundChunkType;
  }

  public IChunkType getAudioEventChunkType()
  {
    return _audioEventChunkType;
  }

  public IChunk getLowestChunk()
  {
    return _lowestChunk;
  }

  public IChunk getHighestChunk()
  {
    return _highestChunk;
  }

  public IChunk getInternalChunk()
  {
    return _internalChunk;
  }

  public IChunk getExternalChunk()
  {
    return _externalChunk;
  }

  public double getRecodeTime(IChunkType chunkType)
  {
    Double rtn = _recodeTimes.get(chunkType);
    if (rtn != null) return rtn;

    if (LOGGER.isWarnEnabled())
      LOGGER.warn("No recode time is defined for " + chunkType);
    return 0.5;
  }

  public void setRecodeTime(IChunkType chunkType, double time)
  {
    _recodeTimes.put(chunkType, time);
  }

  /**
   * @see org.jactr.modules.pm.aural.IAuralModule#getAuralDecayTime()
   */
  public double getAuralDecayTime()
  {
    return _decayTime;
  }

  /**
   * @see org.jactr.modules.pm.aural.IAuralModule#setAuralDecayTime(double)
   */
  public void setAuralDecayTime(double time)
  {
    _decayTime = time;
  }

  /**
   * apply the deferred parameters to the audicon
   */
  protected void applyParameters()
  {
    if (_auralMemory instanceof IParameterized)
      for (Map.Entry<String, String> entry : _deferredParameters.entrySet())
        ((IParameterized) _auralMemory).setParameter(entry.getKey(), entry
            .getValue());
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    TreeSet<String> params = new TreeSet<String>(super.getSetableParameters());
    params.add(AURAL_DECAY_TIME_PARAM);
    params.add(ENABLE_BUFFER_STUFF_PARAM);
    params.add(ENCODING_TIME_EQUATION_PARAM);
    params.add(IPerceptualMemory.FINST_DURATION_TIME_PARAM);
    params.add(IPerceptualMemory.NEW_FINST_ONSET_DURATION_TIME_PARAM);
    params.add(IPerceptualMemory.NUMBER_OF_FINSTS_PARAM);
    return params;
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (AURAL_DECAY_TIME_PARAM.equalsIgnoreCase(key))
      setAuralDecayTime(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (ENABLE_BUFFER_STUFF_PARAM.equalsIgnoreCase(key))
      _isBufferStuffEnabled = ParameterHandler.booleanInstance().coerce(value)
          .booleanValue();
    else if (ENCODING_TIME_EQUATION_PARAM.equalsIgnoreCase(key))
      try
      {
        Class clazz = getClass().getClassLoader().loadClass(value);
        _encodingTime = (IAuralEncodingTimeEquation) clazz.newInstance();
      }
      catch (Exception e)
      {
        LOGGER.error("Could not load class " + value
            + " using default encoding time equation ", e);
        _encodingTime = new IAuralEncodingTimeEquation() {

          public double computeEncodingTime(IChunk soundChunk,
              IAuralModule auralModule)
          {
            return getRecodeTime(soundChunk.getSymbolicChunk().getChunkType());
          }
        };
      }
    else if (STRICT_SYNCHRONIZATION_PARAM.equalsIgnoreCase(key))
      super.setParameter(key, value);
    else
    {
      _deferredParameters.put(key, value);
      if (_auralMemory != null) _auralMemory.setParameter(key, value);
    }
  }

  @Override
  public String getParameter(String key)
  {
    if (AURAL_DECAY_TIME_PARAM.equalsIgnoreCase(key))
      return "" + getAuralDecayTime();
    else if (ENABLE_BUFFER_STUFF_PARAM.equalsIgnoreCase(key))
      return "" + isBufferStuffEnabled();
    else if (ENCODING_TIME_EQUATION_PARAM.equalsIgnoreCase(key))
      return "" + _encodingTime.getClass().getName();
    else if (STRICT_SYNCHRONIZATION_PARAM.equalsIgnoreCase(key))
      return super.getParameter(key);
    else
      return _deferredParameters.get(key);
  }

}
