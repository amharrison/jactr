package org.jactr.modules.pm.vocal.buffer.six;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6;
import org.jactr.modules.pm.vocal.AbstractVocalModule;
import org.jactr.modules.pm.vocal.IVocalModule;
import org.jactr.modules.pm.vocal.buffer.IVocalActivationBuffer;
import org.jactr.modules.pm.vocal.buffer.processor.ClearRequestDelegate;
import org.jactr.modules.pm.vocal.buffer.processor.SpeechRequestDelegate;

public class DefaultVocalActivationBuffer6 extends AbstractPMActivationBuffer6 implements IVocalActivationBuffer
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultVocalActivationBuffer6.class);

  public DefaultVocalActivationBuffer6(AbstractVocalModule module)
  {
    super(IActivationBuffer.VOCAL, module);
  }

  @Override
  public void initialize()
  {
    super.initialize();
  }

  @Override
  protected void grabReferences()
  {
    IDeclarativeModule dm = getModel().getDeclarativeModule();

    try
    {
      // handles imagined voices
      addRequestDelegate(new AddChunkRequestDelegate());
      
      /*
       * to support clear..
       */
      addRequestDelegate(new ClearRequestDelegate(dm.getChunkType(IVocalModule.CLEAR_CHUNK_TYPE).get()));
      
      AbstractVocalModule module = (AbstractVocalModule) getModule();
      
//      addRequestDelegate(new SpeechRequestDelegate(module.getSpeakChunkType(), true, module));
//      addRequestDelegate(new SpeechRequestDelegate(module.getSubvocalizeChunkType(), false, module));
      addRequestDelegate(new SpeechRequestDelegate(module, module.getSpeakChunkType()));
    }
    catch (Exception e)
    {

    }
    super.grabReferences();
  }

  @Override
  protected boolean isValidChunkType(IChunkType chunkType)
  {
    return false;
  }

}
