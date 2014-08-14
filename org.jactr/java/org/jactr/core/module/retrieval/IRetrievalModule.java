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
package org.jactr.core.module.retrieval;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.IModule;
import org.jactr.core.module.retrieval.event.IRetrievalModuleListener;
import org.jactr.core.module.retrieval.time.IRetrievalTimeEquation;
import org.jactr.core.production.request.ChunkTypeRequest;

public interface IRetrievalModule extends IModule
{
  static public final String RETRIEVAL_THRESHOLD = "RetrievalThreshold";
  
  
  public double getRetrievalThreshold();
  
  public void setRetrievalThreshold(double threshold);
  
  
  public CompletableFuture<IChunk> retrieveChunk(ChunkTypeRequest chunkRequest);
 
  
  public IRetrievalTimeEquation getRetrievalTimeEquation();

  public void addListener(IRetrievalModuleListener listener, Executor executor);

  public void removeListener(IRetrievalModuleListener listener);

  public void reset(boolean resetFinstsToo);
}


