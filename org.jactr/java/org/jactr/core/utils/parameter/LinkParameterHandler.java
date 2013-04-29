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
import org.jactr.core.chunk.four.Link4;
import org.jactr.core.chunk.link.IAssociativeLink;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;

/**
 * basic parameter handler that returns {@link Link4} based on one of two string patterns:
 * "(iChunkName count strength)" or "(iChunkName count strength FNiCj)" 
 * @author harrison
 *
 */
public class LinkParameterHandler extends ParameterHandler<IAssociativeLink>
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(LinkParameterHandler.class);

  IChunk                             _jChunk;

  ACTRParameterHandler               _actrParameterHandler;

  public LinkParameterHandler()
  {

  }

  public LinkParameterHandler(IChunk jChunk, ACTRParameterHandler handler)
  {
    setDependents(jChunk, handler);
  }

  public void setDependents(IChunk jChunk, ACTRParameterHandler handler)
  {
    _jChunk = jChunk;
    _actrParameterHandler = handler;
  }

  public IAssociativeLink coerce(String value, IChunk jChunk,
      ACTRParameterHandler actrHandler)
  {
    /*
     * strip {}
     */
    String stripped = value.substring(value.indexOf("(") + 1, value
        .lastIndexOf(")"));

    IAssociativeLinkageSystem linkageSystem = jChunk.getModel()
        .getDeclarativeModule().getAssociativeLinkageSystem();
    if (linkageSystem == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn("No IAssociativeLinkageSystem is installed, ignoring associative links");
      return null;
    }

    String[] split = stripped.split(" ");

    try
    {
      // /*
      // * first should be chunk, second a number
      // */
      IChunk iChunk = (IChunk) actrHandler.coerce(split[0].trim());
      if (iChunk == null)
        throw new NullPointerException(String.format(
            "Could not find chunk %s in declarative memory", split[0]));

      int count = (int) Double.parseDouble(split[1].trim());
      double strength = Double.parseDouble(split[2].trim());

      double fnicj = 0;
      if (split.length > 3) fnicj = Double.parseDouble(split[3]);

      Link4 link = (Link4) linkageSystem.createLink(iChunk, jChunk);
      link.setCount(count);
      link.setFNICJ(fnicj);
      link.setStrength(strength);

      return link;
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException(String.format(
          "Failed to detailed link from %s because %s", stripped, e
              .getMessage()), e);
    }
  }

  /**
   * first object must be the model, second the chunk third is whatever was
   * passed
   */
  @Override
  public IAssociativeLink coerce(String value)
  {
    if (_jChunk == null || _actrParameterHandler == null)
      throw new ParameterException(
          "Cannot coerce "
              + value
              + " into a link without additional information, use coerce(String, IChunk, ACTRParameterHandler) instead");
    return coerce(value, _jChunk, _actrParameterHandler);
  }

  /**
   * object[] values had better be link[]
   * 
   * @param values
   * @return
   */
  @Override
  public String toString(IAssociativeLink value)
  {
    /*
     * it is possible to have links to disposed or unencoded chunks (for a short
     * period, they will be cleaned up in time), this just makes sure they don't
     * make it out to file
     */
    if (value.getIChunk().hasBeenDisposed() || !value.getIChunk().isEncoded())
      return "";

    StringBuilder sb = new StringBuilder("(");
    sb.append(value.getIChunk().getSymbolicChunk().getName()).append(" ");

    sb.append(getLinkParameters(value));

    sb.append(")");
    return sb.toString();
  }
  
  /**
   * return the string rep of the links parameters, i.e., "count strength fnicj"
   * @param link
   * @return
   */
  protected String getLinkParameters(IAssociativeLink link)
  {
    return String.format("%d %.2f %.2f", ((Link4)link).getCount(), link.getStrength(), ((Link4)link).getFNICJ());
  }
}
