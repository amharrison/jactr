/*
 * Created on Oct 12, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.random.six;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * default random module
 * 
 * @see http://jactr.org/node/88
 * @see http://jactr.org/node/89
 * @author harrison
 */
public class DefaultRandomModule extends AbstractModule implements
    IRandomModule, IParameterized
{
  /**
   * logger definition
   */
  static private final Log     LOGGER   = LogFactory
                                            .getLog(DefaultRandomModule.class);

  static private IRandomModule _default = new DefaultRandomModule();

  static public IRandomModule getInstance()
  {
    return _default;
  }

  private long   _seed;

  private Random _random;

  private double _timeRandomizer = 3;

  public DefaultRandomModule()
  {
    super("random");
    setSeed(System.currentTimeMillis());
  }

  public Random getGenerator()
  {
    return _random;
  }

  public long getSeed()
  {
    return _seed;
  }

  public void setSeed(long seedValue)
  {
    _seed = seedValue;
    _random = new Random(_seed);
  }

  public double logisticNoise(double s)
  {
    double p = _random.nextDouble();
    return s * Math.log(p / (1.0 - p));
  }

  @Override
  public void initialize()
  {

  }

  public boolean randomBoolean()
  {
    return _random.nextBoolean();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (SEED_PARAM.equalsIgnoreCase(key)) return "" + getSeed();
    if (RANDOM_TIME_PARAM.equalsIgnoreCase(key))
      return "" + getTimeRandomizer();
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>();
    rtn.add(SEED_PARAM);
    rtn.add(RANDOM_TIME_PARAM);
    return rtn;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (SEED_PARAM.equalsIgnoreCase(key))
      try
      {
        setSeed(ParameterHandler.numberInstance().coerce(value).longValue());
      }
      catch (Exception e)
      {
        long now = System.currentTimeMillis();
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Failed to set seed, using %d ", now), e);
        setSeed(now);
      }
    else if (RANDOM_TIME_PARAM.equalsIgnoreCase(key))
      setTimeRandomizer(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format(
          "%s doesn't recognize %s. Available parameters : %s", getClass()
              .getSimpleName(), key, getSetableParameters()));
  }

  public double getTimeRandomizer()
  {
    return _timeRandomizer;
  }

  public double randomizedTime(double currentTime)
  {
    double lowerBound = currentTime * (_timeRandomizer - 1) / _timeRandomizer;
    double upperBound = currentTime * (_timeRandomizer + 1) / _timeRandomizer;

    return lowerBound + _random.nextDouble() * (upperBound - lowerBound);
  }

  public void setTimeRandomizer(double randomizer)
  {
    _timeRandomizer = randomizer;
  }

  public void reset()
  {
    // noop
  }
}
