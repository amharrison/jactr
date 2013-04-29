package org.jactr.core.module.declarative.four;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.model.IModel;

/**
 * Noop base level act equation that passes back the chunk's base level if it is
 * defined, if not, it passes back base level constant
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
    double base = ((ISubsymbolicChunk4) c.getSubsymbolicChunk().getAdapter(
        ISubsymbolicChunk4.class))
        .getBaseLevelActivation();
    if (Double.isNaN(base)) base = _declarativeModule.getBaseLevelConstant();

    return base;
  }

}
