package org.jactr.modules.pm.vocal.delegate;

/*
 * default logging
 */
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.delta.DeltaTracker;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.vocal.AbstractVocalModule;
import org.jactr.modules.pm.vocal.IVocalModule;

public class VocalManagementDelegate
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VocalManagementDelegate.class);

  final private VocalCommandManager  _manager;

  final private AbstractVocalModule  _module;

  public VocalManagementDelegate(AbstractVocalModule module,
      VocalCommandManager manager)
  {
    _module = module;
    _manager = manager;
  }

  public IVocalModule getModule()
  {
    return _module;
  }
  
  public VocalCommandManager getManager()
  {
    return _manager;
  }

  public void clear()
  {
    IModel model = getModule().getModel();
    if(_manager.isExecuting())
      if(Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, "Warning, clearing with active vocalization.");
    
    _manager.clear();
  }

  protected Future<VocalizationCommand> error(final String message)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
    if (Logger.hasLoggers(_module.getModel()))
      Logger.log(_module.getModel(), Logger.Stream.VOCAL, message);

    FutureTask<VocalizationCommand> future = new FutureTask<VocalizationCommand>(
        new Runnable() {
          public void run()
          {
            throw new IllegalStateException(message);
          }
        }, null);
    future.run();
    return future;
  }

  public Future<VocalizationCommand> prepare(IRequest request,
      double estimatedDuration)
  {
    ChunkTypeRequest ctRequest = (ChunkTypeRequest) request;
    String text = "";


    for (IConditionalSlot cSlot : ctRequest.getConditionalSlots())
      if (cSlot.getName().equals(IVocalModule.STRING_SLOT)
          && cSlot.getCondition() == IConditionalSlot.EQUALS
          && cSlot.getValue() instanceof String)
      {
        text = (String) cSlot.getValue();
        break;
      }

    if (_manager.isPreparing())
      return error("Cannot prepare vocalization of " + text
          + ", still preparing previous vocalization.");

    IEfferentObject vocalizationSource = _module.getVocalizationSource();

    if (vocalizationSource == null)
      return error("No vocalization source defined. I'm mute");

    return _manager.newCommand(vocalizationSource, text, estimatedDuration);
  }

  public Future<VocalizationCommand> execute(IEfferentCommand command)
  {
    if (_manager.isExecuting())
      return error("Still speaking previous vocalization. Ignoring request.");

    if (_module.getVocalizationSource() == null)
      return error("No vocalization source. I'm mute");

    VocalizationCommand vCommand = (VocalizationCommand) command;

    DeltaTracker<VocalizationCommand> tracker = new DeltaTracker<VocalizationCommand>(
        vCommand);
    tracker.setProperty(IEfferentCommand.REQUESTED_START_TIME, ACTRRuntime
        .getRuntime().getClock(_module.getModel()).getTime());

    // ensure that we have a duration, no matter what
    if (Math.abs(vCommand.getEstimatedDuration()) <= 0.001)
      tracker.setProperty(IEfferentCommand.ESTIMATED_DURATION, 0.1);


    return _manager.execute(tracker);
  }

}
