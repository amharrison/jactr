package org.jactr.modules.pm.visual.scene;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.object.manager.IAfferentObjectManager;
import org.commonreality.object.manager.event.IAfferentListener;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;
import org.jactr.modules.pm.visual.event.IVisualModuleListener;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;

/**
 * Provides scene change detection functionality without touching the core
 * distribution. This adds two new status slots to the visual module
 * scene-change and scene-change-value. scene-change-value is a proportion of
 * objects that have changed in the visual scene since the last time the
 * scene-change mechanism was reset (visual-onset-duration since last trigger,
 * or explicit reset with +visual> isa clear-scene-change).<br>
 * <br>
 * ?visual> scene-change =value will be true or false depending on whether or
 * not the scene-change-value is greater than SceneChangeThreshold (default
 * 0.25). <br>
 * <br>
 * This is all accomplished by attaching listeners to the visual module, and
 * either the individual {@link IVisualFeatureMap}s or {@link IAgent}'s
 * {@link IAfferentObjectManager}, depending upon whether accuracy or speed is
 * more important. Setting the AcceleratedDetectionEnabled to true will use the
 * faster ( {@link IAfferentListener} version).<br>
 * <br>
 * Injection of the chunktype clear-scene-change is handled by the
 * {@link SceneChangeParticipant} and the extension point in the bundle
 * manifest.
 * 
 * @author harrison
 */
public class SceneChangeExtension implements IExtension
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                       = LogFactory
                                                                      .getLog(SceneChangeExtension.class);

  static final public String         CLEAR_CHUNK_TYPE             = "clear-scene-change";

  static final public String         SCENE_CHANGED_SLOT           = "scene-change";

  static final public String         SCENE_CHANGED_VALUE_SLOT     = "scene-change-value";

  static final public String         SCENE_CHANGE_THRESHOLD_PARAM = "SceneChangeThreshold";

  static final public String         ACCELERATED_DETECTION_PARAM  = "AcceleratedDetectionEnabled";

  private IVisualModule              _visualModule;

  private IModel                     _model;

  private SceneChangeListener        _sceneChangeListener;

  private double                     _changeThreshold             = 0.25;

  private boolean                    _useAfferentObjectListener   = false;

  private ITimedEvent                _resetTimedEvent;

  private IMutableSlot               _sceneChangeFlagSlot;

  private IMutableSlot               _sceneChangeValueSlot;

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return "SceneChange";
  }

  public void install(IModel model)
  {
    if (_model != null)
      throw new IllegalStateException("Can only be installed for one model");

    _model = model;

    _visualModule = (IVisualModule) _model.getModule(IVisualModule.class);

    if (_visualModule == null)
      throw new IllegalStateException(
          "IVisualModule must be installed for this extension");

    /*
     * when the visual system is reset explicitly, we reset the scene change too
     */
    _visualModule.addListener(new IVisualModuleListener() {

      public void trackedObjectMoved(VisualModuleEvent event)
      {
        // noop

      }

      public void trackingObjectStarted(VisualModuleEvent event)
      {
        // noop

      }

      public void trackingObjectStopped(VisualModuleEvent event)
      {
        // noop

      }

      public void parameterChanged(IParameterEvent pe)
      {
        // noop

      }

      public void moduleReset(IPerceptualMemoryModuleEvent event)
      {
        reset();

      }

      public void perceptAttended(IPerceptualMemoryModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void perceptIndexFound(IPerceptualMemoryModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

    }, ExecutorServices.INLINE_EXECUTOR);

    /*
     * we need to be sure the clear-scene-change chunktype is installed.
     */
    IChunkType clearChunkType = null;
    try
    {
      clearChunkType = _model.getDeclarativeModule().getChunkType(
          CLEAR_CHUNK_TYPE).get();

      if (clearChunkType == null) throw new NullPointerException();
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Could not get reference to "
          + CLEAR_CHUNK_TYPE + " chunktype.", e);
    }

    _sceneChangeListener = new SceneChangeListener();

    /*
     * we can use the feature map listener in one of two ways..
     */
    if (!_useAfferentObjectListener)
    {
      /*
       * we attach the listener to all the feature maps and handle the events
       * inline with the visual processing
       */
      List<IFeatureMap> featureMaps = FastListFactory.newInstance();
      _visualModule.getVisualMemory().getFeatureMaps(featureMaps);

      for (IFeatureMap featureMap : featureMaps)
        featureMap.addListener(_sceneChangeListener,
            ExecutorServices.INLINE_EXECUTOR);

      FastListFactory.recycle(featureMaps);
    }

    /*
     * but regardless, we need a model listener so that we can check for changes
     * at the top of each cycle. If we are using the afferent object listener,
     * we will also install it on connect
     */
    IModelListener installer = new ModelListenerAdaptor() {

      /**
       * at the top of each cycle, we check for scene change
       * 
       * @param event
       * @see org.jactr.core.model.event.ModelListenerAdaptor#cycleStarted(org.jactr.core.model.event.ModelEvent)
       */
      @Override
      public void cycleStarted(ModelEvent event)
      {
        checkForChange();
      }

      @Override
      public void modelConnected(ModelEvent event)
      {
        if (_useAfferentObjectListener)
        {
          /**
           * in order to listen to afferent objects, we need to attach to
           * IAgent, which isn't available until after the model has connected
           * to common reality.. so, we need to defer install/uninstall until a
           * specific model event
           */
          IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(
              _model);
          /*
           * attach the listener to the afferent object manager. we use the CR
           * executor because the object manager operates on the IO thread,
           * which we should never sit on.
           */
          agent.getAfferentObjectManager().addListener(_sceneChangeListener,
              _visualModule.getCommonRealityExecutor());
        }
      }

      @Override
      public void modelDisconnected(ModelEvent event)
      {
        // no need to remove the feature listener as the agent will be disposed

        // and remove ourselves
        _model.removeListener(this);
      }
    };

    // install the listener and we need to be notified immediately
    _model.addListener(installer, ExecutorServices.INLINE_EXECUTOR);

    /*
     * now we need to add a status slot for scene-change so that queries will
     * work
     */
    IVisualActivationBuffer buffer = _visualModule.getVisualActivationBuffer();
    buffer.addSlot(new BasicSlot(SCENE_CHANGED_SLOT, false));
    buffer.addSlot(new BasicSlot(SCENE_CHANGED_VALUE_SLOT, 0.0));

    /*
     * addSlot should probably return the actual slot added, but instead we have
     * to do this.. we are snagging the persistent slots so that we don't have
     * to query the buffer during updates
     */
    _sceneChangeFlagSlot = (IMutableSlot) buffer.getSlot(SCENE_CHANGED_SLOT);
    _sceneChangeValueSlot = (IMutableSlot) buffer
        .getSlot(SCENE_CHANGED_VALUE_SLOT);

    /*
     * now we need to add a request delegate to deal with the explicit reset
     */
    AbstractRequestDelegate delegate = new AbstractRequestDelegate(
        clearChunkType) {

      @Override
      protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
      {
        // Noop
        return null;
      }

      @Override
      protected void finishRequest(IRequest request, IActivationBuffer buffer,
          Object startValue)
      {
        reset();
      }

      @Override
      protected boolean isValid(IRequest request, IActivationBuffer buffer)
          throws IllegalArgumentException
      {
        return true;
      }

    };

    buffer.addRequestDelegate(delegate);

    /*
     * finally, we need to insert a listener so that
     */
  }

  public void uninstall(IModel model)
  {
    if (_model != model) return;

    /*
     * if we didn't install like this, it doesn't matter, removing a
     * non-existant listener doesn't do anything.
     */
    List<IFeatureMap> featureMaps = FastListFactory.newInstance();
    _visualModule.getVisualMemory().getFeatureMaps(featureMaps);
    
    for (IFeatureMap featureMap : featureMaps)
      featureMap.removeListener(_sceneChangeListener);

    FastListFactory.recycle(featureMaps);
    
    _model = null;
    _visualModule = null;
    _sceneChangeListener = null;
  }

  /**
   * @throws Exception
   * @see org.jactr.core.utils.IInitializable#initialize()
   */
  public void initialize() throws Exception
  {
    reset();
  }

  /**
   * check to see if we should set the scene-change flag
   */
  protected void checkForChange()
  {
    double changeRatio = _sceneChangeListener.check();
    _sceneChangeValueSlot.setValue(changeRatio);

    /*
     * if the ratio exceeds threshold, and hasn't already, set the flag
     */
    if (_resetTimedEvent == null && changeRatio >= getSceneChangeThreshold())
    {
      _sceneChangeFlagSlot.setValue(Boolean.TRUE);

      /*
       * queue up the reset. We want to reset the scene-changed flag after
       * visual onset duration.
       */
      double now = ACTRRuntime.getRuntime().getClock(_model).getTime();
      double resetTime = _visualModule.getVisualMemory().getNewFINSTOnsetDuration();
      _resetTimedEvent = new AbstractTimedEvent(now, now + resetTime) {
        @Override
        public void fire(double currentTime)
        {
          super.fire(currentTime);
          clearFlag();
        }

        @Override
        public void abort()
        {
          super.abort();
          clearFlag();
        }
      };

      // queue it up
      _model.getTimedEventQueue().enqueue(_resetTimedEvent);
    }
  }

  private void clearFlag()
  {
    _sceneChangeFlagSlot.setValue(Boolean.FALSE);
    _sceneChangeValueSlot.setValue(0.0);
    _sceneChangeListener.reset();
    _resetTimedEvent = null;
  }

  /**
   * explicitly reset the scene-change flag, regardless of whether or not its
   * been triggered
   */
  protected void reset()
  {
    if (_resetTimedEvent != null && !_resetTimedEvent.hasAborted()
        && !_resetTimedEvent.hasFired()) _resetTimedEvent.abort();

    clearFlag();
  }

  public double getSceneChangeThreshold()
  {
    return _changeThreshold;
  }

  public void setSceneChangeThreshold(double threshold)
  {
    _changeThreshold = threshold;
  }

  public String getParameter(String key)
  {
    if (ACCELERATED_DETECTION_PARAM.equalsIgnoreCase(key))
      return "" + _useAfferentObjectListener;
    if (SCENE_CHANGE_THRESHOLD_PARAM.equalsIgnoreCase(key))
      return "" + getSceneChangeThreshold();

    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Arrays.asList(SCENE_CHANGE_THRESHOLD_PARAM,
        ACCELERATED_DETECTION_PARAM);
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void setParameter(String key, String value)
  {
    if (ACCELERATED_DETECTION_PARAM.equalsIgnoreCase(key))
      _useAfferentObjectListener = ParameterHandler.booleanInstance().coerce(
          value);
    else if (SCENE_CHANGE_THRESHOLD_PARAM.equalsIgnoreCase(key))
      setSceneChangeThreshold(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
  }
}
