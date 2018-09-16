/*
 * Created on Jan 19, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.utils.parameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;

public class SimilarityParameterHandler extends ParameterHandler<Object[]>
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(SimilarityParameterHandler.class);

  ACTRParameterHandler               _actrParameterHandler;

  public SimilarityParameterHandler(ACTRParameterHandler handler)
  {
    _actrParameterHandler = handler;
  }

  public Object[] coerce(String value, ACTRParameterHandler actrHandler)
  {
    try
    {
      /*
       * strip {}
       */
      String stripped = value.substring(value.indexOf("(") + 1, value
          .lastIndexOf(")"));

      String[] split = stripped.split(" ");

      /*
       * first should be chunk, second a number
       */
      IChunk chunk = (IChunk) actrHandler.coerce(split[0].trim());
      Number strength = numberInstance().coerce(split[1].trim());

      return new Object[] { chunk, strength.doubleValue() };
    }
    catch (Exception e)
    {
      throw new  ParameterException("Could not extract similarity info from " + value, e);
    }
  }

  /**
   * first object must be the model, second the chunk third is whatever was
   * passed
   */
  @Override
  public Object[] coerce(String value)
  {
    if (_actrParameterHandler == null)
      throw new ParameterException(
          "Cannot coerce "
              + value
              + " into a similarity without additional information, use coerce(String, ACTRParameterHandler) instead");
    return coerce(value, _actrParameterHandler);
  }

  /**
   * object[] values had better be link[]
   * 
   * @param values
   * @return
   */
  public String toString(Object[] value)
  {
    StringBuilder sb = new StringBuilder("(");
    sb.append(((IChunk) value[0]).getSymbolicChunk().getName()).append(" ");
    sb.append(value[1]).append(")");
    return sb.toString();
  }
}
