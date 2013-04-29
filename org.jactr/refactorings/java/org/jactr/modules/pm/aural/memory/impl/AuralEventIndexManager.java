package org.jactr.modules.pm.aural.memory.impl;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.impl.IIndexManager;

/**
 * @author harrison
 */
public class AuralEventIndexManager implements IIndexManager
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AuralEventIndexManager.class);

  final private IAuralModule         _module;

  public AuralEventIndexManager(IAuralModule module)
  {
    _module = module;
  }

  public IChunk getIndexChunk(IChunk encodedChunk)
  {
    return null;
  }

  public IChunk getAuralEvent(IAfferentObject auralEvent)
  {
    return null;
  }
}
