package org.jactr.modules.pm.vocal.delegate;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.efferent.IEfferentCommandTemplate;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.commonreality.modalities.vocal.VocalizationCommandTemplate;
import org.commonreality.object.IEfferentObject;
import org.jactr.modules.pm.common.efferent.EfferentCommandManager;
import org.jactr.modules.pm.vocal.IVocalModule;
import org.jactr.modules.pm.vocal.event.VocalModuleEvent;

public class VocalCommandManager extends EfferentCommandManager<VocalizationCommand>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VocalCommandManager.class);


  private final IVocalModule _module;
  private volatile boolean _isPreparing = false;
  private boolean _firedStart = false;

  public VocalCommandManager(IVocalModule module)
  {
    super(module.getCommonRealityExecutor());
    _module = module;
    setAutoDeleteEnabled(true);
  }
  
  public boolean isPreparing()
  {
    return _isPreparing;
  }
 

  @Override
  protected void commandAccepted(VocalizationCommand command)
  {
    _isPreparing = false;
    if (LOGGER.isDebugEnabled()) LOGGER.debug("vocalization of "+command.getText()+" accepted");
    if(_module.hasListeners())
      _module.dispatch(new VocalModuleEvent(_module, VocalModuleEvent.Type.PREPARED, command));
  }

  @Override
  protected void commandRejected(VocalizationCommand command)
  {
    _isPreparing = false;
    if (LOGGER.isDebugEnabled()) LOGGER.debug("vocalization of "+command.getText()+" rejected");
  }

  @Override
  protected void commandRunning(VocalizationCommand command)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("vocalization of "+command.getText()+" running");
    
    if(!_firedStart)
      if(_module.hasListeners())
        _module.dispatch(new VocalModuleEvent(_module, VocalModuleEvent.Type.STARTED, command));
    
    _firedStart = true;
  }
  
  @Override
  protected void commandAborted(VocalizationCommand command,
      boolean wasRequested)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("vocalization of "+command.getText()+" aborted");
    
    if(_firedStart)
      if(_module.hasListeners())
        _module.dispatch(new VocalModuleEvent(_module, VocalModuleEvent.Type.COMPLETED, command));
    
    _firedStart = false;
    
  }
  
  @Override
  protected void commandCompleted(VocalizationCommand command)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("vocalization of "+command.getText()+" completed");
    
    if(_firedStart)
      if(_module.hasListeners())
        _module.dispatch(new VocalModuleEvent(_module, VocalModuleEvent.Type.COMPLETED, command));
    
    _firedStart = false;
  }

  @Override
  protected VocalizationCommand createCommand(IEfferentObject vocalizationSource,
      Object... parameters)
  {
    for (IEfferentCommandTemplate<?> template : vocalizationSource
        .getCommandTemplates())
      if (template instanceof VocalizationCommandTemplate)
        try
        {
          VocalizationCommand command = ((VocalizationCommandTemplate) template)
              .instantiate(getAgent(), vocalizationSource);
          
          command.setText((String) parameters[0]);
          command.setProperty(IEfferentCommand.ESTIMATED_DURATION,
              parameters[1]);
          
          _isPreparing = true;
          
          return command;
        }
        catch (Exception e)
        {
          throw new RuntimeException("Could not create vocalization command ",
              e);
        }
        

    throw new RuntimeException("No vocalization command template found");
  }

}
