package org.jactr.modules.pm.aural.memory.impl.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

public class AbstractAuralEncoder implements IPerceptualEncoder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractAuralEncoder.class);
  
  

  public IChunk encode(IAfferentObject afferentObject, IPerceptualMemory memory)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean isDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isInterestedIn(IAfferentObject afferentObject)
  {
    // TODO Auto-generated method stub
    return false;
  }

  public IChunk update(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
