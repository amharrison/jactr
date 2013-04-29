package org.jactr.modules.pm.vocal.event;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.vocal.IVocalModule;

public class VocalModuleEvent extends AbstractACTREvent<IVocalModule, IVocalModuleListener>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VocalModuleEvent.class);
  
  public enum Type {PREPARED, STARTED, COMPLETED, RESET};
  
  final private Type _type;
  final private VocalizationCommand _vocalization;
  
  public VocalModuleEvent(IVocalModule source, Type type, VocalizationCommand vocalization)
  {
    super(source, ACTRRuntime.getRuntime().getClock(source.getModel()).getTime());
    _type = type;
    _vocalization = vocalization;
  }
  
  public VocalModuleEvent(IVocalModule source)
  {
    this(source, Type.RESET, null);
  }
  
  public Type getType()
  {
    return _type;
  }
  
  public VocalizationCommand getVocalization()
  {
    return _vocalization;
  }

  @Override
  public void fire(IVocalModuleListener listener)
  {
    switch(getType())
    {
      
    }
  }

}
