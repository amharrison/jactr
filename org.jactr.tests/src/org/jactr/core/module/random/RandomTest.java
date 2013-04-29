package org.jactr.core.module.random;

/*
 * default logging
 */
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.jactr.core.module.random.six.DefaultRandomModule;

public class RandomTest extends TestCase
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RandomTest.class);

  public void testLogistic()
  {
    IRandomModule module = DefaultRandomModule.getInstance();

    SummaryStatistics summary = new SummaryStatistics();

    double s = 2;
    double variance = s * s * Math.PI * Math.PI / 3;

    for (int i = 0; i < 100000; i++)
      summary.addValue(module.logisticNoise(s));

    assertEquals(variance, summary.getVariance(), 0.1);
    assertEquals(0, summary.getMean(), 0.1);
  }
}
