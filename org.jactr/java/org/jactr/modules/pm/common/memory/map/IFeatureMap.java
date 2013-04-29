/*
 * Created on Jun 26, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.common.memory.map;

import java.util.Set;
import java.util.concurrent.Executor;

import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.modules.pm.common.afferent.IAfferentObjectListener;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

/**
 * @author developer
 */
public interface IFeatureMap<T> extends IAfferentObjectListener
{

  /**
   * return all the identifiers of all possible objects that can exist at the
   * visual location defined by this search pattern if nothing is available, and
   * empty collection is to be returned
   * 
   * @param container
   */
  public void getCandidateRealObjects(ChunkTypeRequest request,
      Set<IIdentifier> container);

  /**
   * returns the feature value for this object
   * @param identifier
   * @return
   */
  public T getInformation(IIdentifier identifier);

  /**
   * fill the slot values accordingly for this identifier at the mutablePattern
   * location this is then used to further specify the actual  location
   * chunk that will be returned
   * 
   * @param mutableRequest
   *          the pseudo visual location to be setup
   * @param identifier
   * @param encodedChunk
   *          TODO
   * @param originalSearchRequest
   */
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk,
      ChunkTypeRequest originalSearchRequest);

  public void clear();

  public void dispose();

  public boolean isInterestedIn(ChunkTypeRequest request);

  public void addListener(IFeatureMapListener listener, Executor executor);

  public void removeListener(IFeatureMapListener listener);
  
  public void setPerceptualMemory(IPerceptualMemory memory);
  
  public IPerceptualMemory getPerceptualMemory();
  
  /**
   * provides an opportunity to normalize any varialbes. will only
   * be called if {@link #isInterestedIn(ChunkTypeRequest)}
   * @param request
   */
  public void normalizeRequest(ChunkTypeRequest request);
}
