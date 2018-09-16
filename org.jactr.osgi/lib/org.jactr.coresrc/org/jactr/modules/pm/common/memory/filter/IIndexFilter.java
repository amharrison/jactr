package org.jactr.modules.pm.common.memory.filter;

/*
 * default logging
 */
import java.util.Comparator;

import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

/**
 * interface that is responsible for the second stage of a perceptual
 * index search. The first stage is handled by the {@link IFeatureMap} and
 * provides a list of candidates. the index filters then accept/reject and
 * provide the ability to sort the results using the comparator
 * 
 * @author harrison
 *
 */
public interface IIndexFilter
{

  /**
   * can be used to expand variables or special values
   * @param request
   */
  public void normalizeRequest(ChunkTypeRequest request);
  
  
  public void setPerceptualMemory(IPerceptualMemory memory);
  public IPerceptualMemory getPerceptualMemory();

  /**
   * these are self-templating. The master copy is installed and during the
   * search processing a copy is instantiated.
   * 
   * @param request
   * @return
   */
  public IIndexFilter instantiate(ChunkTypeRequest request);
  
  public int getWeight();
  
  public boolean accept(ChunkTypeRequest template);
  public Comparator<ChunkTypeRequest> getComparator();
}
