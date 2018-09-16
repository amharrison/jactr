package org.jactr.modules.pm.vocal;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.vocal.VocalConstants;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.manager.IEfferentObjectManager;
import org.commonreality.object.manager.event.IEfferentListener;
import org.commonreality.object.manager.event.IObjectEvent;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.AbstractPerceptualModule;
import org.jactr.modules.pm.vocal.buffer.IVocalActivationBuffer;
import org.jactr.modules.pm.vocal.buffer.six.DefaultVocalActivationBuffer6;
import org.jactr.modules.pm.vocal.delegate.VocalCommandManager;
import org.jactr.modules.pm.vocal.delegate.VocalManagementDelegate;
import org.jactr.modules.pm.vocal.event.IVocalModuleListener;
import org.jactr.modules.pm.vocal.event.VocalModuleEvent;

/**
 * abstract implementation that
 * 
 * @author harrison
 */
public class AbstractVocalModule extends AbstractPerceptualModule implements
    IVocalModule
{

  /**
   * Logger definition
   */
  static private final transient Log                                    LOGGER                = LogFactory
                                                                                                  .getLog(AbstractVocalModule.class);

  private String                                                        _preparedVocalization = null;

  private IVocalPreparationTimeEquation                                 _preparationTimeEquation;

  private IVocalExecutionTimeEquation                                   _executionTimeEquation;

  private IVocalProcessingTimeEquation                                  _processingTimeEquation;

  private IVocalActivationBuffer                                        _vocalBuffer;

  private IChunkType                                                    _speakChunkType;

  private IChunkType                                                    _subvocalizeChunkType;

  /**
   * this is the object that provides the vocalizations in common reality
   */
  private IEfferentObject                                               _vocalizationSource;

  // private ProcessVocalizationDelegate _processDelegate;
  //
  // private PrepareVocalizationDelegate _prepareDelegate;
  //
  // private ExecuteVocalizationDelegate _executeDelegate;

  private ExecutionTimeResolution                                       _resolution           = ExecutionTimeResolution.MINIMUM;

  private VocalManagementDelegate                                       _vocalManager;

  final private ACTREventDispatcher<IVocalModule, IVocalModuleListener> _dispatcher           = new ACTREventDispatcher<IVocalModule, IVocalModuleListener>();

  public AbstractVocalModule()
  {
    super("vocal");
    /*
     * we listen to ourselves for the prepared vocalization
     */
    addListener(new IVocalModuleListener() {

      public void vocalSystemReset(VocalModuleEvent event)
      {
        _preparedVocalization = null;
      }

      public void vocalizationCompleted(VocalModuleEvent event)
      {
      }

      public void vocalizationPrepared(VocalModuleEvent event)
      {
        _preparedVocalization = event.getVocalization().getText();
      }

      public void vocalizationStarted(VocalModuleEvent event)
      {

      }

    }, ExecutorServices.INLINE_EXECUTOR);
  }

  protected void setPreparationTimeEquation(
      IVocalPreparationTimeEquation equation)
  {
    _preparationTimeEquation = equation;
  }

  protected void setExecutionTimeEquation(IVocalExecutionTimeEquation equation)
  {
    _executionTimeEquation = equation;
  }

  protected void setProcessingTimeEquation(IVocalProcessingTimeEquation equation)
  {
    _processingTimeEquation = equation;
  }

  public String getPreparedVocalization()
  {
    return _preparedVocalization;
  }

  public IVocalPreparationTimeEquation getPreparationTimeEquation()
  {
    return _preparationTimeEquation;
  }

  public IVocalExecutionTimeEquation getExecutionTimeEquation()
  {
    return _executionTimeEquation;
  }

  public IVocalProcessingTimeEquation getProcessingTimeEquation()
  {
    return _processingTimeEquation;
  }

  public IVocalActivationBuffer getVocalBuffer()
  {
    return _vocalBuffer;
  }

  public IChunkType getSpeakChunkType()
  {
    return _speakChunkType;
  }

  public IChunkType getSubvocalizeChunkType()
  {
    return _subvocalizeChunkType;
  }

  public void reset()
  {
    _vocalBuffer.clear();
    if (_vocalManager != null) _vocalManager.clear();

    if (hasListeners()) dispatch(new VocalModuleEvent(this));
  }

  // /**
  // * actually prepare the text (controlled by the buffer)
  // *
  // * @param text
  // */
  // public void prepare(ChunkTypeRequest request, String text,
  // boolean isVocalization)
  // {
  // if (_prepareDelegate == null)
  // throw new IllegalModuleStateException(
  // "Cannot prepare until connected to CommonReality");
  //
  // _prepareDelegate.process(request, text, isVocalization);
  // }
  //
  // public void execute(IIdentifier commandId, boolean isVocalization)
  // {
  // if (_processDelegate == null)
  // throw new IllegalModuleStateException(
  // "Cannot process until connected to CommonReality");
  // if (_executeDelegate == null)
  // throw new IllegalModuleStateException(
  // "Cannot execute until connected to CommonReality");
  //
  // _processDelegate.process(null, commandId, isVocalization);
  //
  // if (isVocalization)
  // _executeDelegate.process(null, commandId, isVocalization);
  // }

  protected IVocalActivationBuffer createVocalBuffer()
  {
    return new DefaultVocalActivationBuffer6(this);
  }

  @Override
  protected Collection<IActivationBuffer> createBuffers()
  {
    _vocalBuffer = createVocalBuffer();
    return Collections.singleton((IActivationBuffer) _vocalBuffer);
  }

  /**
   * when we connect, we set up listeners for efferent objects and efferent
   * commands
   */
  @Override
  protected void connectToCommonReality()
  {
    super.connectToCommonReality();
    /*
     * first we snag the agent that corresponds to the model. this is the
     * gateway into common reality
     */
    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(getModel());



    /**
     * we are interested in acquiring and monitoring any efferent that is marked
     * VocalConstants#CAN_VOCALIZE. but first we should check to see if it has
     * already arrived
     */
    IEfferentObjectManager eom = agent.getEfferentObjectManager();

    IEfferentListener effListener = new IEfferentListener() {

      public void objectsAdded(IObjectEvent<IEfferentObject, ?> addEvent)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("objectsAdded : ");
        for (IEfferentObject object : addEvent.getObjects())
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("got new efferent object " + object.getIdentifier());
          if (object.hasProperty(VocalConstants.CAN_VOCALIZE)
              && (Boolean) object.getProperty(VocalConstants.CAN_VOCALIZE))
          {
            if (_vocalizationSource != null)
              if (LOGGER.isWarnEnabled())
                LOGGER.warn("Vocalization source is already defined "
                    + _vocalizationSource.getIdentifier());

            _vocalizationSource = object;

            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Set vocalization source " + object.getIdentifier());
            // no need to do any further processing
            break;
          }
        }

      }

      public void objectsRemoved(IObjectEvent<IEfferentObject, ?> removeEvent)
      {
        for (IEfferentObject object : removeEvent.getObjects())
          if (object.equals(_vocalizationSource))
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Removed vocalization source "
                  + object.getIdentifier());
            _vocalizationSource = null;
          }
      }

      public void objectsUpdated(IObjectEvent<IEfferentObject, ?> updateEvent)
      {
        // ignore
      }
    };

    /*
     * we attach the listener on the common reality executor that is shared by
     * all modules listening to CR
     */
    eom.addListener(effListener, getCommonRealityExecutor());

    Collection<IIdentifier> ids = eom.getIdentifiers();

    if (LOGGER.isDebugEnabled()) LOGGER.debug("current efferents : " + ids);

    for (IIdentifier id : ids)
    {
      IEfferentObject efferent = eom.get(id);
      if (efferent.hasProperty(VocalConstants.CAN_VOCALIZE)
          && (Boolean) efferent.getProperty(VocalConstants.CAN_VOCALIZE))
      {
        _vocalizationSource = efferent;
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Set vocalization source " + efferent.getIdentifier());
        break;
      }
    }

    // IProceduralModule pm = getModel().getProceduralModule();
    // _prepareDelegate = new PrepareVocalizationDelegate(this, pm
    // .getDefaultProductionFiringTime(), getErrorChunk());
    // _processDelegate = new ProcessVocalizationDelegate(this, pm
    // .getDefaultProductionFiringTime(), getErrorChunk());
    // _executeDelegate = new ExecuteVocalizationDelegate(this, pm
    // .getDefaultProductionFiringTime(), getErrorChunk());
    VocalCommandManager manager = new VocalCommandManager(this);
    _vocalManager = new VocalManagementDelegate(this, manager);

    manager.install(agent);
  }

  @Override
  protected void disconnectFromCommonReality()
  {
    super.disconnectFromCommonReality();
    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(getModel());
    _vocalManager.getManager().uninstall(agent);
    /*
     * we dont bother unregistering the listener since the agent will be
     * destroyed entirely
     */
    _vocalizationSource = null;
  }

  @Override
  public void initialize()
  {
    super.initialize();

    _speakChunkType = getNamedChunkType(SPEAK_CHUNK_TYPE);
    _subvocalizeChunkType = getNamedChunkType(SUBVOCALIZE_CHUNK_TYPE);
  }

  public IEfferentObject getVocalizationSource()
  {
    return _vocalizationSource;
  }

  public ExecutionTimeResolution getExecutionTimeResolution()
  {
    return _resolution;
  }

  public void setExecutionTimeResolution(ExecutionTimeResolution resolution)
  {
    _resolution = resolution;
  }

  public Future<VocalizationCommand> execute(VocalizationCommand command)
  {
    if (_vocalManager == null)
      throw new IllegalStateException(
          "Cannot execute vocalization unless connected to running model");

    return _vocalManager.execute(command);
  }

  public Future<VocalizationCommand> prepare(IRequest request,
      double estimatedDuration)
  {
    if (_vocalManager == null)
      throw new IllegalStateException(
          "Cannot prepare vocalization unless connected to running model");

    return _vocalManager.prepare(request, estimatedDuration);
  }

  public void addListener(IVocalModuleListener listener, Executor executor)
  {
    _dispatcher.addListener(listener, executor);
  }

  public void dispatch(VocalModuleEvent event)
  {
    _dispatcher.fire(event);
  }

  public boolean hasListeners()
  {
    return _dispatcher.hasListeners();
  }

  public void removeListener(IVocalModuleListener listener)
  {
    _dispatcher.removeListener(listener);
  }

}
