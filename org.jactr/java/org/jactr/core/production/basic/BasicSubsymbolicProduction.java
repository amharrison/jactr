/*
 * Created on Nov 20, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.production.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.four.learning.ICostEquation;
import org.jactr.core.module.procedural.four.learning.IProbabilityEquation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISubsymbolicProduction;
import org.jactr.core.production.event.ProductionEvent;
import org.jactr.core.utils.DefaultAdaptable;
import org.jactr.core.utils.parameter.ParameterHandler;

public class BasicSubsymbolicProduction extends DefaultAdaptable implements
    ISubsymbolicProduction
{

  /**
   * Logger definition
   */

  protected static final transient Log LOGGER          = LogFactory
                                                           .getLog(BasicSubsymbolicProduction.class);

  protected double                     _creationTime;

  protected double                     _firingTime     = 0.05;

  protected IProduction                _parentProduction;

  /**
   * last firing time is used to flag that P,G,C calculations might need to be
   * redone. Setting it to NEGATIVE_INFINITY will force recalculation at the
   * next access
   */
  protected double                     _lastFiringTime = Double.NEGATIVE_INFINITY;

  protected Map<String, Object>        _parameterMap;

  protected ICostEquation              _costEquation;

  protected IProbabilityEquation       _probabilityEquation;

  protected Map<String, String>        _unknownParameters;

  public BasicSubsymbolicProduction(IProduction parent, IModel model)
  {
    _parentProduction = parent;
    _parameterMap = new TreeMap<String, Object>();

    setDefaultParameters();
  }

  /**
   * @since
   */
  public void dispose()
  {
    _probabilityEquation = null;
    _parameterMap.clear();
    _parameterMap = null;
    _parentProduction = null;
  }

  /**
   * Sets the defaultParameters attribute of the DefaultSubsymbolicProduction5
   * object
   * 
   * @since
   */
  protected void setDefaultParameters()
  {
    setCreationTime(0.0);
    _unknownParameters = new HashMap<String, String>();
  }

  public IProbabilityEquation getProbabilityEquation()
  {
    return _probabilityEquation;
  }

  /**
   * Gets the possibleParameters attribute of the DefaultSubsymbolicProduction5
   * object
   * 
   * @return The possibleParameters value
   * @since
   */
  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  /**
   * Gets the setableParameters attribute of the DefaultSubsymbolicProduction5
   * object
   * 
   * @return The setableParameters value
   * @since
   */
  public Collection<String> getSetableParameters()
  {
    ArrayList<String> params = new ArrayList<String>();
    Collections.addAll(params, FIRING_TIME, CREATION_TIME);
    params.addAll(_unknownParameters.keySet());

    return params;
  }

  /**
   * Sets the parameter attribute of the DefaultSubsymbolicProduction5 object
   * 
   * @param key
   *          The new parameter value
   * @param value
   *          The new parameter value
   * @since
   */
  public void setParameter(String key, String value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Trying to set " + key + " to " + value);

    if (CREATION_TIME.equalsIgnoreCase(key))
      setCreationTime(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (FIRING_TIME.equalsIgnoreCase(key))
      setFiringTime(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else
    {
      String oldValue = _unknownParameters.put(key, value);
      if (_parentProduction.hasListeners())
        _parentProduction.dispatch(new ProductionEvent(_parentProduction, key,
            oldValue, value));

      LOGGER.warn(".setParameter(" + key + ") not implemented.");
    }
  }

  /**
   * Gets the parameter attribute of the DefaultSubsymbolicProduction5 object
   * 
   * @param key
   *          Description of Parameter
   * @return The parameter value
   * @since
   */
  public String getParameter(String key)
  {
    String rtn = null;
    if (CREATION_TIME.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getCreationTime());
    else if (FIRING_TIME.equals(key))
      rtn = ParameterHandler.numberInstance().toString(getFiringTime());
    else
      rtn = _unknownParameters.get(key);

    return rtn;
  }

  /**
   * how long does this production take to fire (normally)
   */
  public double getFiringTime()
  {
    return _firingTime;
  }

  /**
   * Sets the effortTime attribute of the DefaultSubsymbolicProduction5 object
   * 
   * @param defAct
   *          The new effortTime value
   * @since
   */
  public void setFiringTime(double defAct)
  {
    double old = _firingTime;
    _firingTime = defAct;
    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          FIRING_TIME, defAct, old));
  }

  public double getCreationTime()
  {
    return _creationTime;
  }

  public void setCreationTime(double time)
  {
    double oldValue = _creationTime;
    _creationTime = time;
    _lastFiringTime = Double.NEGATIVE_INFINITY;
    if (_parentProduction.hasListeners())
      _parentProduction.dispatch(new ProductionEvent(_parentProduction,
          CREATION_TIME, oldValue, time));
  }

  public void encode()
  {
    // noop
  }

}
