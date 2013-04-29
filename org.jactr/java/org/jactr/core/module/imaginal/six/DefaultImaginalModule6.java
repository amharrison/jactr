/*
 * Created on Feb 6, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.imaginal.six;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.imaginal.IImaginalModule;
import org.jactr.core.module.imaginal.six.buffer.DefaultImaginalBuffer;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * DefaultImaginalModule6 is the default implementation of the imaginal system.
 * It provides the imaginal buffer, and has parameters to manipulate the amount
 * of time add and modify requests take.
 * 
 * @see http://jactr.org/node/135
 * @author developer
 */
public class DefaultImaginalModule6 extends AbstractModule implements
    IImaginalModule, IParameterized
{
  /**
   * logger definition
   */
  static private final Log  LOGGER           = LogFactory
                                                 .getLog(DefaultImaginalModule6.class);

  private IActivationBuffer _imaginalBuffer;

  private boolean           _randomizeDelays = false;

  private double            _addDelay        = 0.2;

  private double            _modifyDelay     = 0.2;

  public DefaultImaginalModule6()
  {
    super(IMAGINAL_BUFFER);
  }

  /**
   * @see org.jactr.core.module.AbstractModule#initialize()
   */
  @Override
  public void initialize()
  {

  }

  @Override
  public void dispose()
  {
    super.dispose();
    _imaginalBuffer.dispose();
    _imaginalBuffer = null;
  }

  protected @Override
  Collection<IActivationBuffer> createBuffers()
  {
    _imaginalBuffer = new DefaultImaginalBuffer(this);
    ArrayList<IActivationBuffer> buffs = new ArrayList<IActivationBuffer>();
    buffs.add(_imaginalBuffer);
    return buffs;
  }

  public double getAddDelayTime()
  {
    return _addDelay;
  }

  public double getModifyDelayTime()
  {
    return _modifyDelay;
  }

  public boolean isRandomizeDelaysEnabled()
  {
    return _randomizeDelays;
  }

  public void setAddDelayTime(double addDelayTime)
  {
    _addDelay = addDelayTime;
    // TODO implement parameter event firing
  }

  public void setModifyDelayTime(double modDelayTime)
  {
    _modifyDelay = modDelayTime;

  }

  public void setRandomizeDelaysEnabled(boolean enabled)
  {
    _randomizeDelays = enabled;
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(new String[] { IMAGINAL_ADD_DELAY_PARAM,
        IMAGINAL_MODIFY_DELAY_PARAM, IMAGINAL_RANDOMIZE_DELAY_PARAM });
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public String getParameter(String key)
  {
    if (IMAGINAL_ADD_DELAY_PARAM.equalsIgnoreCase(key))
      return "" + getAddDelayTime();
    if (IMAGINAL_MODIFY_DELAY_PARAM.equalsIgnoreCase(key))
      return "" + getModifyDelayTime();
    if (IMAGINAL_RANDOMIZE_DELAY_PARAM.equalsIgnoreCase(key))
      return "" + isRandomizeDelaysEnabled();
    return null;
  }

  public void setParameter(String key, String value)
  {
    if (IMAGINAL_ADD_DELAY_PARAM.equalsIgnoreCase(key))
      setAddDelayTime(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (IMAGINAL_MODIFY_DELAY_PARAM.equalsIgnoreCase(key))
      setModifyDelayTime(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (IMAGINAL_RANDOMIZE_DELAY_PARAM.equalsIgnoreCase(key))
      setRandomizeDelaysEnabled(ParameterHandler.booleanInstance()
          .coerce(value).booleanValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format(
          "%s doesn't recognize %s. Available parameters : %s", getClass()
              .getSimpleName(), key, getSetableParameters()));
  }

  public void reset()
  {
    _imaginalBuffer.clear();
  }
}
