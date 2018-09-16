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
package org.jactr.core.module.declarative.search.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.declarative.search.map.AbstractTypeValueMap;
import org.jactr.core.module.declarative.search.map.DefaultValueMap;
import org.jactr.core.module.declarative.search.map.ITypeValueMap;
import org.jactr.core.module.declarative.search.map.IValueMap;

public class ChunkTypeValueMap<I> extends AbstractTypeValueMap<IChunk, I>
    implements ITypeValueMap<IChunk, I>
{
  static public final Log            LOGGER    = LogFactory
                                                   .getLog(ChunkTypeValueMap.class);

  private DefaultValueMap<IChunk, I> _valueMap = new DefaultValueMap<IChunk, I>();

  public IValueMap<IChunk, I> getValueMap()
  {
    return _valueMap;
  }

  public boolean isValueRelevant(Object value)
  {
    return value instanceof IChunk;
  }

  @Override
  public IChunk asKeyType(Object value)
  {
    if (value instanceof IChunk)
    {
      IChunk chunk = (IChunk) value;
      if (!chunk.isEncoded())
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String
                  .format(
                      "Indexable slot value %s has not been encoded. If it is scheduled to be encoded, no worries. But it if is never encoded, retrievals using it may fail",
                      chunk));
      /**
       * we can fix this, when needed, by adding a listener to unencoded chunks.
       * On encode, we reindex. On merge, we merge the collections, etc. The one
       * problem is that we may not be able to get at the original chunk key..
       * we also need to listen for chunk/type remove to update the index
       * correctly.
       */
      return chunk;
    }

    return null;
  }

}
