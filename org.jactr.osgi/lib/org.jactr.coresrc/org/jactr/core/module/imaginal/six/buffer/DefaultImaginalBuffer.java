package org.jactr.core.module.imaginal.six.buffer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.delegate.DefaultDelegatedRequestableBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.module.IModule;
import org.jactr.core.module.imaginal.IImaginalModule;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;

public class DefaultImaginalBuffer extends DefaultDelegatedRequestableBuffer6
    implements ICompilableBuffer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultImaginalBuffer.class);

  public DefaultImaginalBuffer(IModule module)
  {
    super(IImaginalModule.IMAGINAL_BUFFER, module);
    setStrictHarvestingEnabled(false);

    addRequestDelegate(new ImaginalAddChunkRequestDelegate());
    addRequestDelegate(new ImaginalAddChunkTypeRequestDelegate());
    addRequestDelegate(new ImaginalSlotRequestDelegate());
  }

  @Override
  protected void setSourceChunkInternal(IChunk sourceChunk)
  {
    if (sourceChunk != null && Logger.hasLoggers(getModel()))
      Logger.log(getModel(), IImaginalModule.IMAGINAL_LOG, "Imagining "
          + sourceChunk);
    
    super.setSourceChunkInternal(sourceChunk);
  }

  public ICompilableContext getCompilableContext()
  {
    return null;
  }

}
