package org.jactr.core.chunk.link;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.utils.parameter.ParameterHandler;

public class AbstractAssociativeLink implements IAssociativeLink
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractAssociativeLink.class);

  private final IChunk               _iChunk;

  private final IChunk               _jChunk;

  private double                     _strength;

  public AbstractAssociativeLink(IChunk jChunk, IChunk iChunk, double strength)
  {
    _iChunk = iChunk;
    _jChunk = jChunk;
    _strength = strength;
  }

  public IChunk getIChunk()
  {
    return _iChunk;
  }

  public IChunk getJChunk()
  {
    return _jChunk;
  }

  public double getStrength()
  {
    return _strength;
  }

  public void setStrength(double strength)
  {
    _strength = strength;
  }

  public void copy(IAssociativeLink link) throws IllegalArgumentException
  {
    if (!_jChunk.equals(link.getJChunk()) || !_iChunk.equals(link.getIChunk()))
      throw new IllegalArgumentException("Both i and j chunks must match to copy link parameters");
    
    setStrength(link.getStrength());
  }

  public String getParameter(String key)
  {
    if(STRENGTH_PARAM.equalsIgnoreCase(key))
      return Double.toString(getStrength());
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Collections.singleton(STRENGTH_PARAM);
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void setParameter(String key, String value)
  {
    if(STRENGTH_PARAM.equalsIgnoreCase(key))
      setStrength(ParameterHandler.numberInstance().coerce(value).doubleValue());
    else
      if (LOGGER.isWarnEnabled()) LOGGER.warn(String.format("No clue how to set %s=%s",key,value));
  }

}
