/*
 * Created on Oct 12, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.declarative.search;

import java.util.Collection;
import java.util.Comparator;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * A search system that indexes and retrieves I(ndexable) based on P(attern)
 * @author developer
 *
 */
public interface ISearchSystem
{

  /**
   * index this object
   *
   */
  public void index(IChunk chunk);
  
  /**
   * remove this object from the index
   * 
   */
  public void unindex(IChunk chunk);
  
  public void update(IChunk chunk, String slotName, Object oldValue, Object newValue);
  
  /**
   * return all I that match this pattern exactly
   * @param pattern
   * @param sortRule may be null
   * @param filter TODO
   * @return
   */
  public Collection<IChunk> findExact(ChunkTypeRequest pattern, Comparator<IChunk> sortRule, IChunkFilter filter);
  
  /**
   * find all I that match this pattern somewhat
   * @param pattern
   * @param filter TODO
   * @return
   */
  public Collection<IChunk> findFuzzy(ChunkTypeRequest pattern, Comparator<IChunk> sortRule, IChunkFilter filter);
  
  public void clear();
  
//  public void addListener(ISearchListener listener, Executor executor);
//  
//  public void removeListener(ISearchListener listener);
//  
//  public boolean hasListeners();
}


