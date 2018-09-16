package org.jactr.core.module.procedural.six;

/*
 * default logging
 */
import org.jactr.core.module.procedural.IProceduralModule;

public interface IProceduralModule6 extends IProceduralModule
{

  static public final String EXPECTED_UTILITY_NOISE            = "ExpectedUtilityNoise";



  public double getExpectedUtilityNoise();

  public void setExpectedUtilityNoise(double noise);

}
