/*
 * Created on Jul 11, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
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
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.pm.AbstractPerceptualModule;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;
import org.jactr.modules.pm.visual.buffer.IVisualLocationBuffer;
import org.jactr.modules.pm.visual.event.IVisualModuleListener;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;
import org.jactr.modules.pm.visual.memory.IVisualMemory;
import org.jactr.modules.pm.visual.six.DefaultEncodingTimeEquation;
import org.jactr.modules.pm.visual.six.DefaultSearchTimeEquation;

/**
 * abstract impl that address most of the trivial details. Clients must provide
 * and configure the actual IVisualMemory used.
 * 
 * @see http://jactr.org/node/137
 * @author harrison
 */
public abstract class AbstractVisualModule extends AbstractPerceptualModule
    implements IVisualModule, IParameterized
{

  /**
   * Logger definition
   */

  static private final transient Log                                LOGGER                        = LogFactory
                                                                                                      .getLog(AbstractVisualModule.class);

  static final public String                                        ENCODING_TIME_EQUATION_PARAM  = "VisualEncodingTimeEquationClass";

  static final public String                                        SEARCHING_TIME_EQUATION_PARAM = "VisualSearchTimeEquationClass";

  static final public String                                        ENABLE_BUFFER_STUFF_PARAM     = "EnableVisualBufferStuff";

  static private Collection<String>                                 SETABLE_PARAMS;

  static
  {
    ArrayList<String> params = new ArrayList<String>();
    params.add(ENCODING_TIME_EQUATION_PARAM);
    params.add(SEARCHING_TIME_EQUATION_PARAM);
    params.add(IVisualMemory.VISUAL_FIELD_WIDTH_PARAM);
    params.add(IVisualMemory.VISUAL_FIELD_HORIZONTAL_RESOLUTION_PARAM);
    params.add(IVisualMemory.VISUAL_FIELD_HEIGHT_PARAM);
    params.add(IVisualMemory.VISUAL_FIELD_VERTICAL_RESOLUTION_PARAM);
    params.add(IPerceptualMemory.NUMBER_OF_FINSTS_PARAM);
    params.add(IPerceptualMemory.FINST_DURATION_TIME_PARAM);
    params.add(IPerceptualMemory.NEW_FINST_ONSET_DURATION_TIME_PARAM);
    params.add(IVisualMemory.MOVEMENT_TOLERANCE_PARAM);
    params.add(ENABLE_BUFFER_STUFF_PARAM);
    params.add(STRICT_SYNCHRONIZATION_PARAM);
    SETABLE_PARAMS = Collections.unmodifiableCollection(params);
  }

  private IVisualLocationBuffer                                     _visualLocationBuffer;

  private IVisualActivationBuffer                                   _visualActivationBuffer;

  private IVisualSearchTimeEquation                                 _searchTimeEquation;

  private IVisualEncodingTimeEquation                               _encodingTimeEquation;

  private IChunkType                                                _visualLocationChunkType;

  private IChunkType                                                _visualChunkType;

  private IChunk                                                    _highestChunk;

  private IChunk                                                    _lowestChunk;

  private IChunk                                                    _currentChunk;

  private IChunk                                                    _lessThanCurrentChunk;

  private IChunk                                                    _greaterThanCurrentChunk;

  private boolean                                                   _bufferStuffEnabled           = true;

  private Map<String, String>                                       _parameterMap;

  private ACTREventDispatcher<IVisualModule, IVisualModuleListener> _listener;

  private IModelListener                                            _modelListener;

  private IVisualMemory                                             _visualMemory;

  public AbstractVisualModule()
  {
    super("visual");
    _searchTimeEquation = new DefaultSearchTimeEquation();
    _encodingTimeEquation = new DefaultEncodingTimeEquation();
    _parameterMap = new LinkedHashMap<String, String>();
    _listener = new ACTREventDispatcher<IVisualModule, IVisualModuleListener>();
    setDefaultParameters();
  }

  @Override
  public void dispose()
  {
    _searchTimeEquation = null;
    _encodingTimeEquation = null;

    _parameterMap.clear();
    _parameterMap = null;

    _listener.clear();
    _listener = null;

    /*
     * and the buffers
     */
    _visualActivationBuffer.dispose();
    _visualActivationBuffer = null;
    _visualLocationBuffer.dispose();
    _visualLocationBuffer = null;

    // so that getModel() calls return valid values
    super.dispose();
  }

  protected void setDefaultParameters()
  {
    setParameter(IVisualMemory.VISUAL_FIELD_WIDTH_PARAM, "160");
    setParameter(IVisualMemory.VISUAL_FIELD_HEIGHT_PARAM, "120");
    setParameter(IVisualMemory.VISUAL_FIELD_HORIZONTAL_RESOLUTION_PARAM, "160");
    setParameter(IVisualMemory.VISUAL_FIELD_VERTICAL_RESOLUTION_PARAM, "120");
    setParameter(IVisualMemory.VISUAL_FIELD_HEIGHT_PARAM, "90");
    setParameter(IPerceptualMemory.NEW_FINST_ONSET_DURATION_TIME_PARAM, "0.5");
    setParameter(IPerceptualMemory.FINST_DURATION_TIME_PARAM, "3");
    setParameter(IPerceptualMemory.NUMBER_OF_FINSTS_PARAM, "4");
    setParameter(IVisualMemory.MOVEMENT_TOLERANCE_PARAM, "0.5");
    setParameter(ENCODING_TIME_EQUATION_PARAM,
        DefaultEncodingTimeEquation.class.getName());
    setParameter(SEARCHING_TIME_EQUATION_PARAM, DefaultSearchTimeEquation.class
        .getName());
  }

  public void addListener(IVisualModuleListener listener, Executor executor)
  {
    _listener.addListener(listener, executor);
  }

  public void removeListener(IVisualModuleListener listener)
  {
    _listener.removeListener(listener);
  }

  public boolean hasListeners()
  {
    return _listener.hasListeners();
  }

  public void dispatch(VisualModuleEvent event)
  {
    if (_listener.hasListeners()) _listener.fire(event);
  }

  /**
   * called during install process since models rely on only one status buffer,
   * the visual location buffer should use the visual activation buffer.
   * 
   * @return
   */
  abstract protected IVisualLocationBuffer createVisualLocationBuffer(
      IVisualActivationBuffer buffer);

  /**
   * called during install process
   * 
   * @return
   */
  abstract protected IVisualActivationBuffer createVisualActivationBuffer();

  abstract protected IVisualMemory createVisualMemory();

  public IVisualLocationBuffer getVisualLocationBuffer()
  {
    return _visualLocationBuffer;
  }

  public IVisualActivationBuffer getVisualActivationBuffer()
  {
    return _visualActivationBuffer;
  }

  @Override
  protected Collection<IActivationBuffer> createBuffers()
  {
    _visualActivationBuffer = createVisualActivationBuffer();
    _visualLocationBuffer = createVisualLocationBuffer(_visualActivationBuffer);

    ArrayList<IActivationBuffer> rtn = new ArrayList<IActivationBuffer>();
    rtn.add(_visualLocationBuffer);
    rtn.add(_visualActivationBuffer);

    return rtn;
  }

  public IChunk getLowestChunk()
  {
    if (_lowestChunk == null) _lowestChunk = getNamedChunk("lowest");
    return _lowestChunk;
  }

  public IChunk getHighestChunk()
  {
    if (_highestChunk == null) _highestChunk = getNamedChunk("highest");
    return _highestChunk;
  }

  public IChunk getLessThanCurrentChunk()
  {
    if (_lessThanCurrentChunk == null)
      _lessThanCurrentChunk = getNamedChunk("less-than-current");
    return _lessThanCurrentChunk;
  }

  public IChunk getGreaterThanCurrentChunk()
  {
    if (_greaterThanCurrentChunk == null)
      _greaterThanCurrentChunk = getNamedChunk("greater-than-current");
    return _greaterThanCurrentChunk;
  }

  public IChunk getCurrentChunk()
  {
    if (_currentChunk == null) _currentChunk = getNamedChunk("current");
    return _currentChunk;
  }

  public IVisualSearchTimeEquation getSearchTimeEquation()
  {
    return _searchTimeEquation;
  }

  public void setSearchTimeEquation(IVisualSearchTimeEquation equation)
  {
    _searchTimeEquation = equation;
  }

  public IVisualEncodingTimeEquation getEncodingTimeEquation()
  {
    return _encodingTimeEquation;
  }

  public void setEncodingTimeEquation(IVisualEncodingTimeEquation equation)
  {
    _encodingTimeEquation = equation;
  }

  public IVisualMemory getVisualMemory()
  {
    return _visualMemory;
  }

  public IPerceptualMemory getPerceptualMemory()
  {
    return getVisualMemory();
  }

  public IChunkType getVisualLocationChunkType()
  {
    if (_visualLocationChunkType == null)
      _visualLocationChunkType = getNamedChunkType(VISUAL_LOCATION_CHUNK_TYPE);
    return _visualLocationChunkType;
  }

  public IChunkType getVisualChunkType()
  {
    if (_visualChunkType == null)
      _visualChunkType = getNamedChunkType(VISUAL_CHUNK_TYPE);
    return _visualChunkType;
  }

  public void assignFINST(IChunk visualChunk)
  {
    if (visualChunk == null) return;

    // IIdentifier identifier = (IIdentifier) visualChunk
    // .getMetaData(IAfferentObjectEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
    IIdentifier identifier = (IIdentifier) visualChunk
        .getMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
    if (identifier != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("assigning finst for " + visualChunk);
      // _visicon.getVisualMap().getFINSTFeatureMap().flagAsAttended(identifier,
      // visualChunk, _visicon.getFINSTTimeSpan());
      getVisualMemory().getFINSTFeatureMap().flagAsAttended(identifier,
          visualChunk, getVisualMemory().getFINSTSpan());
    }
    else if (LOGGER.isDebugEnabled())
      LOGGER.debug("could not find identifier for " + visualChunk);
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (STRICT_SYNCHRONIZATION_PARAM.equalsIgnoreCase(key))
      super.setParameter(key, value);
    else
    {
      _parameterMap.put(key, value);
      if (_visualMemory != null) _visualMemory.setParameter(key, value);
    }
  }

  /**
   * return parameter value - null if not defined.
   * 
   * @param key
   *          Description of the Parameter
   * @return The parameter value
   */
  @Override
  public String getParameter(String key)
  {
    if (STRICT_SYNCHRONIZATION_PARAM.equalsIgnoreCase(key))
      return super.getParameter(key);
    if (ENCODING_TIME_EQUATION_PARAM.equalsIgnoreCase(key))
      return _encodingTimeEquation.getClass().getName();
    if (SEARCHING_TIME_EQUATION_PARAM.equalsIgnoreCase(key))
      return _searchTimeEquation.getClass().getName();
    return _parameterMap.get(key);
  }

  /**
   * Return list of all parameters that can be set.
   * 
   * @return The setableParameters value
   */
  @Override
  public Collection<String> getSetableParameters()
  {
    TreeSet<String> params = new TreeSet<String>(_parameterMap.keySet());
    params.addAll(SETABLE_PARAMS);
    params.addAll(super.getSetableParameters());
    return params;
  }

  /**
   * we apply the parameters after the visual map has been created so that we
   * can pass through some parameters
   */
  protected void applyParameters()
  {
    for (String key : _parameterMap.keySet())
    {
      String value = _parameterMap.get(key);
      if (ENABLE_BUFFER_STUFF_PARAM.equalsIgnoreCase(key))
        _bufferStuffEnabled = ParameterHandler.booleanInstance().coerce(value);
      else if (ENCODING_TIME_EQUATION_PARAM.equalsIgnoreCase(key))
        try
        {
          setEncodingTimeEquation((IVisualEncodingTimeEquation) ParameterHandler
              .classInstance().coerce(value).newInstance());
        }
        catch (Exception e)
        {
          LOGGER.error("Could not create visual encoding time equation "
              + value, e);
        }
      else if (SEARCHING_TIME_EQUATION_PARAM.equalsIgnoreCase(key))
        try
        {
          setSearchTimeEquation((IVisualSearchTimeEquation) ParameterHandler
              .classInstance().coerce(value).newInstance());
        }
        catch (Exception e)
        {
          /**
           * Error : error
           */
          LOGGER.error("Could not create visual search time equation " + value,
              e);
        }
      else
        _visualMemory.setParameter(key, value);
    }
  }

  @Override
  public void initialize()
  {
    super.initialize();
    _modelListener = new ModelListenerAdaptor() {
      double _lastCheckTime = -1;

      /**
       * called at the top of each cycle.. here is where we will perform any
       * buffer stuffing all pending timed events will have fired before we get
       * here..
       */
      @Override
      public void cycleStarted(ModelEvent event)
      {
        if (!_bufferStuffEnabled) return;

        if (getVisualMemory().getLastChangeTime() >= _lastCheckTime)
        {
          _visualLocationBuffer.checkForBufferStuff();
          _lastCheckTime = event.getSimulationTime();
        }
      }
    };

    getModel().addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

    _visualMemory = createVisualMemory();

    applyParameters();

    /*
     * we also add a listener to ourselves to keep track of the finsts..
     */
    IVisualModuleListener listener = new IVisualModuleListener() {

      public void moduleReset(IPerceptualMemoryModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void perceptAttended(IPerceptualMemoryModuleEvent event)
      {
        /*
         * flag it as attended
         */
        IChunk visualChunk = event.getChunk();

        /*
         * this is possible if the underlying percept is removed or invalidated
         * between encoding and the chunk actually making it into the buffer.
         */
        if (visualChunk.hasBeenDisposed()) return;

        IIdentifier identifier = (IIdentifier) visualChunk
            .getMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
        if (identifier != null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("assigning finst for " + visualChunk);
          // _visicon.getVisualMap().getFINSTFeatureMap().flagAsAttended(identifier,
          // visualChunk, _visicon.getFINSTTimeSpan());
          getVisualMemory().getFINSTFeatureMap().flagAsAttended(identifier,
              visualChunk, getVisualMemory().getFINSTSpan());
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("could not find identifier for " + visualChunk);

      }

      public void perceptIndexFound(IPerceptualMemoryModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void parameterChanged(IParameterEvent pe)
      {

      }

      public void trackedObjectMoved(VisualModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void trackingObjectStarted(VisualModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void trackingObjectStopped(VisualModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

    };

    addListener(listener, ExecutorServices.INLINE_EXECUTOR);

    IActivePerceptListener finstListener = new IActivePerceptListener() {

      public void newPercept(IIdentifier identifier, IChunk chunk)
      {
        // this is quick enough that we can process it in line
        // and it may be needed in the search right now..
        IFINSTFeatureMap finstMap = getVisualMemory().getFINSTFeatureMap();
        if (finstMap != null && !finstMap.isAttended(identifier))
          finstMap.flagAsNew(identifier, chunk, getVisualMemory()
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

    _visualMemory.addListener(finstListener, ExecutorServices.INLINE_EXECUTOR);

  }

  @Override
  protected void connectToCommonReality()
  {
    super.connectToCommonReality();
    /*
     * attach the visual memory
     */
    _visualMemory.attach(ACTRRuntime.getRuntime().getConnector()
        .getAgent(getModel()));
  }

  @Override
  protected void disconnectFromCommonReality()
  {
    super.disconnectFromCommonReality();
    _visualMemory.detach();
  }
}
