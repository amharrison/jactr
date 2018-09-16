package org.jactr.modules.temporal.buffer;

/*
 * default logging
 */
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.buffer.delegate.DefaultDelegatedRequestableBuffer6;
import org.jactr.core.buffer.delegate.IRequestDelegate;
import org.jactr.core.module.IModule;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.modules.temporal.buffer.processor.ClearRequestDelegate;
import org.jactr.modules.temporal.buffer.processor.TimeRequestDelegate;

/**
 * default temporal buffer. This doesn't do much other than automatically install some
 * command handler (which handle {@link ChunkPattern}s). For a module/buffer as simple as
 * this one, this is overkill, but it serves as a good example for how you can extend
 * the behavior of modules/buffers by just adding new {@link IRequestDelegate}s
 * @author harrison
 *
 */
public class DefaultTemporalActivationBuffer extends
    DefaultDelegatedRequestableBuffer6
{

  public DefaultTemporalActivationBuffer(String name, IModule module)
  {
    super(name, module);
  }

  @Override
  public void initialize()
  {
    super.initialize();
  }

  @Override
  protected void grabReferences()
  {
    //expand encoded chunks
    addRequestDelegate(new AddChunkRequestDelegate());

    try
    {
      addRequestDelegate(new ClearRequestDelegate(getModel()
          .getDeclarativeModule().getChunkType("clear").get()));
      addRequestDelegate(new TimeRequestDelegate(getModel()
          .getDeclarativeModule().getChunkType("time").get()));
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not get required chunktypes ", e);
    }
    super.grabReferences();
  }

}
