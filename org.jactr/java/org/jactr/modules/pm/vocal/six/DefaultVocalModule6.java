package org.jactr.modules.pm.vocal.six;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.parameter.NumericParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.pm.vocal.AbstractVocalModule;
import org.jactr.modules.pm.vocal.IVocalExecutionTimeEquation;
import org.jactr.modules.pm.vocal.IVocalModule;
import org.jactr.modules.pm.vocal.IVocalPreparationTimeEquation;
import org.jactr.modules.pm.vocal.IVocalProcessingTimeEquation;

public class DefaultVocalModule6 extends AbstractVocalModule
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                           = LogFactory
                                                                          .getLog(DefaultVocalModule6.class);

  static public final String         SYLLABLE_RATE_PARAM              = "SyllableRate";

  static public final String         CHARACTERS_PER_SYLLABLE_PARAM    = "CharactersPerSyllable";

  static public final String         EMPTY_PREPARATION_TIME_PARAM     = "EmptyPreparationTime";

  static public final String         DIFFERENT_PREPARATION_TIME_PARAM = "DifferentPreparationTime";

  static public final String         SAME_PREPARATION_TIME_PARAM      = "SamePreparationTime";

  static public final String         PROCESSING_TIME_PARAM            = "ProcessingTime";

  static public final String         EXECUTION_TIME_RESOLUTION_PARAM  = "ExecutionTimeResolution";

  private double                     _emptyPreparationTime            = 0.15;

  private double                     _differentPreparationTime        = 0.1;

  private double                     _samePreparationTime             = 0;

  private double                     _processingTime                  = 0.05;

  private double                     _syllableRate                    = 6.66;

  private double                     _charactersPerSyllable           = 3;

  public DefaultVocalModule6()
  {
    super();

    setProcessingTimeEquation(new IVocalProcessingTimeEquation() {

      public double compute(String text, IVocalModule module)
      {
        return _processingTime;
      }

    });

    /*
     * default preparation equation
     */
    setPreparationTimeEquation(new IVocalPreparationTimeEquation() {

      public double compute(String text, IVocalModule module)
      {
        String prep = module.getPreparedVocalization();

        if (prep == null) return _emptyPreparationTime;

        if (prep.equals(text)) return _samePreparationTime;

        return _differentPreparationTime;
      }

    });

    setExecutionTimeEquation(new IVocalExecutionTimeEquation() {

      public double compute(String text, IVocalModule module)
      {
        if (text.trim().length() == 0) return _processingTime;

        return text.length() / _charactersPerSyllable / _syllableRate;
      }

    });
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    ArrayList<String> parameters = new ArrayList<String>(super
        .getSetableParameters());
    parameters.add(CHARACTERS_PER_SYLLABLE_PARAM);
    parameters.add(SYLLABLE_RATE_PARAM);
    parameters.add(PROCESSING_TIME_PARAM);
    parameters.add(DIFFERENT_PREPARATION_TIME_PARAM);
    parameters.add(SAME_PREPARATION_TIME_PARAM);
    parameters.add(EMPTY_PREPARATION_TIME_PARAM);
    parameters.add(EXECUTION_TIME_RESOLUTION_PARAM);
    return parameters;
  }

  @Override
  public String getParameter(String key)
  {
    if (CHARACTERS_PER_SYLLABLE_PARAM.equalsIgnoreCase(key))
      return "" + getCharactersPerSyllable();
    if (SYLLABLE_RATE_PARAM.equalsIgnoreCase(key))
      return "" + getSyllableRate();
    if (PROCESSING_TIME_PARAM.equalsIgnoreCase(key))
      return "" + getProcessingTime();
    if (EMPTY_PREPARATION_TIME_PARAM.equalsIgnoreCase(key))
      return "" + getEmptyPreparationTime();
    if (SAME_PREPARATION_TIME_PARAM.equalsIgnoreCase(key))
      return "" + getSamePreparationTime();
    if (DIFFERENT_PREPARATION_TIME_PARAM.equalsIgnoreCase(key))
      return "" + getDifferentPreparationTime();
    if (EXECUTION_TIME_RESOLUTION_PARAM.equalsIgnoreCase(key))
      return "" + getExecutionTimeResolution();
    return super.getParameter(key);
  }

  @Override
  public void setParameter(String key, String value)
  {
    NumericParameterHandler nph = ParameterHandler.numberInstance();
    if (CHARACTERS_PER_SYLLABLE_PARAM.equalsIgnoreCase(key))
      setCharactersPerSyllable(nph.coerce(value).doubleValue());
    else if (SYLLABLE_RATE_PARAM.equalsIgnoreCase(key))
      setSyllableRate(nph.coerce(value).doubleValue());
    else if (PROCESSING_TIME_PARAM.equalsIgnoreCase(key))
      setProcessingTime(nph.coerce(value).doubleValue());
    else if (EMPTY_PREPARATION_TIME_PARAM.equalsIgnoreCase(key))
      setEmptyPreparationTime(nph.coerce(value).doubleValue());
    else if (SAME_PREPARATION_TIME_PARAM.equalsIgnoreCase(key))
      setSamePreparationTime(nph.coerce(value).doubleValue());
    else if (DIFFERENT_PREPARATION_TIME_PARAM.equalsIgnoreCase(key))
      setDifferentPreparationTime(nph.coerce(value).doubleValue());
    else if (EXECUTION_TIME_RESOLUTION_PARAM.equalsIgnoreCase(key))
      setExecutionTimeResolution(ExecutionTimeResolution.valueOf(value
          .toUpperCase()));
    else
      super.setParameter(key, value);
  }

  public double getEmptyPreparationTime()
  {
    return _emptyPreparationTime;
  }

  public void setEmptyPreparationTime(double emptyPreparationTime)
  {
    _emptyPreparationTime = emptyPreparationTime;
  }

  public double getDifferentPreparationTime()
  {
    return _differentPreparationTime;
  }

  public void setDifferentPreparationTime(double differentPreparationTime)
  {
    _differentPreparationTime = differentPreparationTime;
  }

  public double getSamePreparationTime()
  {
    return _samePreparationTime;
  }

  public void setSamePreparationTime(double samePreparationTime)
  {
    _samePreparationTime = samePreparationTime;
  }

  public double getProcessingTime()
  {
    return _processingTime;
  }

  public void setProcessingTime(double processingTime)
  {
    _processingTime = processingTime;
  }

  public double getSyllableRate()
  {
    return _syllableRate;
  }

  public void setSyllableRate(double syllableRate)
  {
    _syllableRate = syllableRate;
  }

  public double getCharactersPerSyllable()
  {
    return _charactersPerSyllable;
  }

  public void setCharactersPerSyllable(double charactersPerSyllable)
  {
    _charactersPerSyllable = charactersPerSyllable;
  }

}
