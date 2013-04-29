package org.jactr.tools.itr.fit;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.regression.SimpleRegression;

public class FitStatistics
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER = LogFactory.getLog(FitStatistics.class);

  private double _rmse;

  private double _rSquare;

  private long   _n;

  private double _chiSquare;

  public FitStatistics(Set<String> comparisonPoints, Map<String, Double> model,
      Map<String, Double> data, boolean allowMissing)
  {
    ArrayList<double[]> values = new ArrayList<double[]>(
        comparisonPoints.size());
    for (String label : comparisonPoints)
    {
      Double modelValue = model.get(label);
      Double dataValue = data.get(label);
      if (modelValue == null || Double.isNaN(modelValue) || dataValue == null
          || Double.isNaN(dataValue))
        if (!allowMissing)
          throw new IllegalArgumentException(
              "Both model and data must contain values for " + label);
        else
          continue;

      values.add(new double[] { modelValue, dataValue });
    }

    compute(values.toArray());
  }

  /**
   * @param array
   *          of pairs of points [[modelData, observedData]....]
   */
  public FitStatistics(double[][] comparisonPoints)
  {
    int i = 0;
    for (double[] pair : comparisonPoints)
    {
      if (pair.length != 2)
        throw new IllegalArgumentException("Comparisonpoints[" + i
            + "] should be 2 long, not " + pair.length);
      i++;
    }
    compute(comparisonPoints);
  }

  private void compute(Object[] values)
  {
    SimpleRegression regression = new SimpleRegression();
    double sse = 0;
    double chiSquare = 0;
    long n = 0;
    for (Object val : values)
    {
      double[] value = (double[]) val;
      regression.addData(value[0], value[1]);

      double squareDiff = Math.pow(value[0] - value[1], 2);

      double chiPartial = squareDiff / value[1];
      chiSquare += chiPartial;

      n++;
      sse += squareDiff;
    }

    /*
     * cant use regression.getMeanSquaredError() as that is in comparison to the
     * regression line
     */
    _rmse = Math.sqrt(sse / n);
    _rSquare = regression.getRSquare();
    _n = regression.getN();
    _chiSquare = chiSquare;
  }

  public long getN()
  {
    return _n;
  }

  public double getRMSE()
  {
    return _rmse;
  }

  public double getRSquared()
  {
    return _rSquare;
  }

  public double getChiSquare()
  {
    return _chiSquare;
  }

  /**
   * compute bayesian information criterion using sample size, chiSquare, and
   * number of free parameters
   * 
   * @see http://en.wikipedia.org/wiki/Bayesian_information_criterion
   * @return
   */
  public double computeBIC(int freeParameters)
  {
    return getChiSquare() + freeParameters * Math.log(getN());
  }

  /**
   * compute Akaike information criterion using chiSq and free parameters
   * 
   * @param freeParameters
   * @see http://en.wikipedia.org/wiki/Akaike_information_criterion
   * @return
   */
  public double computeAIC(int freeParameters)
  {
    return getChiSquare() + 2 * freeParameters;
  }

  /**
   * compute the corrected AIC, taking into account N. This is best for small N
   * or large # freeParameters.
   * 
   * @param freeParameters
   * @see http://en.wikipedia.org/wiki/Akaike_information_criterion
   * @return
   */
  public double computeAICc(int freeParameters)
  {
    double numerator = 2 * freeParameters * (freeParameters + 1);
    double denom = getN() - freeParameters - 1;
    if (denom < 0)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format(
            "Free Parameters (%d) greater than data to fit (%d)",
            freeParameters, getN()));
      return Double.NaN;
    }
    return computeAIC(freeParameters) + numerator / denom;
  }
}
