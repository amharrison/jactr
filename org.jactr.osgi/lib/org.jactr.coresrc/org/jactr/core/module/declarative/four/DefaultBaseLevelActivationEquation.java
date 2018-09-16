package org.jactr.core.module.declarative.four;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.basic.AbstractSubsymbolicChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.Logger.Stream;
import org.jactr.core.model.IModel;

/**
 * Noop base level act equation that passes back the chunk's base level if it is
 * defined, if not, it passes back base level constant. Assumes
 * AbstractSubsymbolicChunk
 * 
 * @author harrison
 */
public class DefaultBaseLevelActivationEquation implements
    IBaseLevelActivationEquation
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultBaseLevelActivationEquation.class);

  private IDeclarativeModule4        _declarativeModule;

  public DefaultBaseLevelActivationEquation(IDeclarativeModule4 decM)
  {
    _declarativeModule = decM;
  }

  public double computeBaseLevelActivation(IModel model, IChunk c)
  {
    double base = c.getSubsymbolicChunk().getBaseLevelActivation();
    if (Double.isNaN(base)) base = _declarativeModule.getBaseLevelConstant();

    if (Logger.hasLoggers(model))
      Logger.log(model, Stream.ACTIVATION,
          String.format("%s.base = %.2f", c, base));

    return base;
  }

  @Override
  public String getName()
  {
    return "base-static";
  }

  @Override
  public double computeAndSetActivation(IChunk chunk, IModel model)
  {
    double base = computeBaseLevelActivation(model, chunk);
    ((AbstractSubsymbolicChunk) chunk.getSubsymbolicChunk())
        .setBaseLevelActivation(base);
    return base;
  }

}
