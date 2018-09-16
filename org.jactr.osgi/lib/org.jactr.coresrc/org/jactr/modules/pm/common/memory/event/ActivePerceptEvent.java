package org.jactr.modules.pm.common.memory.event;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

public class ActivePerceptEvent extends
    AbstractACTREvent<IPerceptualMemory, IActivePerceptListener>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ActivePerceptEvent.class);

  static public enum Type {
    NEW, UPDATED, REMOVED, REENCODED
  };

  final private Type        _type;

  final private IIdentifier _perceptIdentifier;

  final private IChunk      _currentEncoding;

  private IChunk            _oldEncoding;

  public ActivePerceptEvent(IPerceptualMemory source, Type type,
      IIdentifier perceptIdentifier, IChunk currentEncoding)
  {
    super(source, ACTRRuntime.getRuntime().getClock(
        source.getModule().getModel()).getTime());
    _perceptIdentifier = perceptIdentifier;
    _type = type;
    _currentEncoding = currentEncoding;
  }

  /**
   * for reencode
   * 
   * @param source
   * @param perceptIdentifier
   * @param currentEncoding
   * @param oldEncoding
   */
  public ActivePerceptEvent(IPerceptualMemory source,
      IIdentifier perceptIdentifier, IChunk currentEncoding, IChunk oldEncoding)
  {
    this(source, Type.REENCODED, perceptIdentifier, currentEncoding);
    _oldEncoding = oldEncoding;
  }

  public IIdentifier getPerceptIdentifier()
  {
    return _perceptIdentifier;
  }

  public IChunk getCurrentEncoding()
  {
    return _currentEncoding;
  }

  public IChunk getOldEncoding()
  {
    return _oldEncoding;
  }

  public Type getType()
  {
    return _type;
  }

  @Override
  public void fire(IActivePerceptListener listener)
  {
    switch (getType())
    {
      case NEW:
        listener.newPercept(_perceptIdentifier, _currentEncoding);
        break;
      case UPDATED:
        listener.updated(_perceptIdentifier, _currentEncoding);
        break;
      case REMOVED:
        listener.removed(_perceptIdentifier, _currentEncoding);
        break;
      case REENCODED:
        listener.reencoded(_perceptIdentifier, _oldEncoding, _currentEncoding);
        break;
    }

  }

}
