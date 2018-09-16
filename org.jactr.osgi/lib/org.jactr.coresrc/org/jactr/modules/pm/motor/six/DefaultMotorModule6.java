package org.jactr.modules.pm.motor.six;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.buffer.IMotorActivationBuffer;
import org.jactr.modules.pm.motor.buffer.six.DefaultMotorActivationBuffer6;
import org.jactr.modules.pm.motor.command.IMotorTimeEquation;

public class DefaultMotorModule6 extends AbstractMotorModule
{
  /**
   * Logger definition
   */
  public static final transient Log LOGGER = LogFactory
                                                .getLog(DefaultMotorModule6.class);

  private IMotorActivationBuffer     _buffer;

  public DefaultMotorModule6()
  {
    this("motor");
  }

  public DefaultMotorModule6(String name)
  {
    super(name);
  }

  /**
   * create the {@link IMotorActivationBuffer}
   * 
   * @see org.jactr.core.module.AbstractModule#createBuffers()
   */
  protected Collection<IActivationBuffer> createBuffers()
  {
    _buffer = new DefaultMotorActivationBuffer6(IActivationBuffer.MOTOR, this);
    return Collections.singleton((IActivationBuffer) _buffer);
  }

  /**
   * @see IMotorModule#getBuffer()
   */
  public IMotorActivationBuffer getBuffer()
  {
    return _buffer;
  }

  /**
   * initialize by installing the {@link IEfferentObjectTranslator},
   * {@link IEfferentCommandTranslator}, the preparation and processing
   * {@link IMotorTimeEquation}s
   * 
   * @see org.jactr.modules.pm.motor.AbstractMotorModule#initialize()
   */
  public void initialize()
  {
    super.initialize();

    setPreparationTimeEquation(new DefaultPreparationTimeEquation());
    setProcessingTimeEquation(new DefaultProcessingTimeEquation());
  }

}
