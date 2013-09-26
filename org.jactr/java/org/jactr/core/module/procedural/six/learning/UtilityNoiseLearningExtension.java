package org.jactr.core.module.procedural.six.learning;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.six.IProceduralModule6;
import org.jactr.core.module.procedural.six.learning.event.IProceduralLearningModule6Listener;
import org.jactr.core.module.procedural.six.learning.event.ProceduralLearningEvent;
import org.jactr.core.utils.parameter.ParameterHandler;

public class UtilityNoiseLearningExtension implements IExtension,
    IProceduralLearningModule6Listener
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                            = LogFactory
                                                                           .getLog(UtilityNoiseLearningExtension.class);

  static public final String         UTILITY_NOISE_LEARNING_RATE_PARAM = "UtilityNoiseLearningRate";

  static public final String         UTILITY_NOISE_SCALOR_PARAM        = "UtilityLearningScalor";

  static public final String         LONG_TERM_RATE_PARAM              = "LongTermRewardLearningRate";

  static public final String         SHORT_TERM_RATE_PARAM             = "ShortTermRewardLearningRate";

  /**
   * alpha sr
   */
  protected double                   _shortTermLearningRate            = 0.008;

  /**
   * alpha lr
   */
  protected double                   _longTermLearningRate             = 0.001;

  /**
   * sr
   */
  private double                     _shortTermEstimate                = 0;

  /**
   * lr
   */
  private double                     _longTermEstimate                 = 0;

  /**
   * alpha s NaN for disabled
   */
  protected double                   _noiseLearningRate                = Double.NaN;

  /**
   * 
   */
  protected double                   _noiseLearningScalor              = 4;

  private IModel                     _model;

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return getClass().getSimpleName();
  }

  public void install(IModel model)
  {
    _model = model;

  }

  public void uninstall(IModel model)
  {
    IProceduralLearningModule6 plm = (IProceduralLearningModule6) getModel()
        .getModule(IProceduralLearningModule6.class);

    if (plm != null) plm.removeListener(this);

    _model = null;
  }

  public String getParameter(String key)
  {
    if (UTILITY_NOISE_LEARNING_RATE_PARAM.equalsIgnoreCase(key))
      return "" + _noiseLearningRate;
    else if (UTILITY_NOISE_SCALOR_PARAM.equalsIgnoreCase(key))
      return "" + _noiseLearningScalor;
    else if (LONG_TERM_RATE_PARAM.equalsIgnoreCase(key))
      return "" + _longTermLearningRate;
    else if (SHORT_TERM_RATE_PARAM.equalsIgnoreCase(key))
      return "" + _shortTermLearningRate;
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays
        .asList(UTILITY_NOISE_LEARNING_RATE_PARAM, UTILITY_NOISE_SCALOR_PARAM,
            LONG_TERM_RATE_PARAM, SHORT_TERM_RATE_PARAM);
  }

  public void setParameter(String key, String value)
  {
    if (UTILITY_NOISE_LEARNING_RATE_PARAM.equalsIgnoreCase(key))
      _noiseLearningRate = ParameterHandler.numberInstance().coerce(value)
          .doubleValue();
    else if (UTILITY_NOISE_SCALOR_PARAM.equalsIgnoreCase(key))
      _noiseLearningScalor = ParameterHandler.numberInstance().coerce(value)
          .doubleValue();
    else if (LONG_TERM_RATE_PARAM.equalsIgnoreCase(key))
      _longTermLearningRate = ParameterHandler.numberInstance().coerce(value)
          .doubleValue();
    else if (SHORT_TERM_RATE_PARAM.equalsIgnoreCase(key))
      _shortTermLearningRate = ParameterHandler.numberInstance().coerce(value)
          .doubleValue();
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to set " + key + "=" + value);
  }

  public void initialize() throws Exception
  {
    IProceduralLearningModule6 plm = (IProceduralLearningModule6) getModel()
        .getModule(IProceduralLearningModule6.class);

    if (plm == null)
      throw new IllegalStateException("Cannot install " + getName() + " w/o "
          + IProceduralLearningModule6.class.getName());

    plm.addListener(this, ExecutorServices.INLINE_EXECUTOR);
  }

  public boolean isAdaptiveUtilityNoiseEnabled()
  {
    return !Double.isNaN(_noiseLearningRate);
  }

  private void adjustUtilityNoise(double reward)
  {
    if (Double.isNaN(reward)) reward = 0;

    _shortTermEstimate = _shortTermEstimate + _shortTermLearningRate
        * (reward - _shortTermEstimate);

    _longTermEstimate = _longTermEstimate + _longTermLearningRate
        * (reward - _longTermEstimate);

    double f = Math.max(1, _noiseLearningScalor
        * (_longTermEstimate - _shortTermEstimate));
    double oldNoise = ((IProceduralModule6) getModel().getProceduralModule())
        .getExpectedUtilityNoise();
    double newNoise = Math.max(0, oldNoise + _noiseLearningRate
        * (f - oldNoise));

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("New noise : " + newNoise + " SR : " + _shortTermEstimate
          + " LR : " + _longTermEstimate + " old oise : " + oldNoise + " F : "
          + f);

    ((IProceduralModule6) getModel().getProceduralModule())
        .setExpectedUtilityNoise(newNoise);
  }

  public void rewarded(ProceduralLearningEvent event)
  {
    if (isAdaptiveUtilityNoiseEnabled()) adjustUtilityNoise(event.getReward());
  }

  public void startReward(ProceduralLearningEvent event)
  {
    // TODO Auto-generated method stub

  }

  public void stopReward(ProceduralLearningEvent event)
  {
    // TODO Auto-generated method stub

  }

}
