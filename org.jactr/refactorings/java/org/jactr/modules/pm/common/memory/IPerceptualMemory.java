package org.jactr.modules.pm.common.memory;

/*
 * default logging
 */
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.modules.pm.IPerceptualModule;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

/**
 * general interface for a short-term perceptual store that supports searching
 * for "index" chunks that can then be used to direct attending and encoding to
 * actual percepts. This is the basis for both the aural and visual systems.
 * 
 * @author harrison
 */
public interface IPerceptualMemory
{
  /**
   * used for temporary tagging of location chunks
   */
  static public final String SEARCH_RESULT_IDENTIFIER_KEY = IPerceptualMemory.class
                                                              .getName()
                                                              + ".foundIdentifier";

  /**
   * called after CR has been connected, this attaches the perceptual memory to
   * CR
   * @param agent
   */
  public void attach(ACTRAgent agent);

  /**
   * detach from CR
   */
  public void detach();
  
  /**
   * have we been attached to CR?
   * @return
   */
  public boolean isAttached();
  
  
  /**
   * the number of updates from CR as of yet unprocessed
   * @return
   */
  public int getPendingUpdates();

  /**
   * simulation time of the last update
   * @return
   */
  public double getLastChangeTime();

  /**
   * owning module
   * @return
   */
  public IPerceptualModule getModule();

  public void addListener(IActivePerceptListener listener, Executor executor);

  public void removeListener(IActivePerceptListener listener);

  /**
   * attach feature map
   * @param featureMap
   */
  public void addFeatureMap(IFeatureMap featureMap);

  public void removeFeatureMap(IFeatureMap featureMap);

  /**
   * return all the feature maps in the provided container
   * @param container
   * @return
   */
  public Collection<IFeatureMap> getFeatureMaps(
      Collection<IFeatureMap> container);

  /**
   * return the finst-able feature map
   * @return
   */
  public IFINSTFeatureMap getFINSTFeatureMap();

  /**
   * add post processing search filter
   * @param filter
   */
  public void addFilter(IIndexFilter filter);

  public void removeFilter(IIndexFilter filter);

  public Collection<IIndexFilter> getFilters(Collection<IIndexFilter> container);

  /**
   * add encoder
   * @param encoder
   */
  public void addEncoder(IPerceptualEncoder encoder);

  public void removeEncoder(IPerceptualEncoder encoder);
  
  public Collection<IPerceptualEncoder> getEncoders(
      Collection<IPerceptualEncoder> container);

  /**
   * returns all the known encodings of identifier
   * 
   * @param identifier
   * @param container
   * @return
   */
  public Collection<IChunk> getEncodings(IIdentifier identifier,
      Collection<IChunk> container);

  /**
   * search perceptual memory for index chunks matching the request and tag the
   * location chunk with {@link #SEARCH_RESULT_IDENTIFIER_KEY}
   * 
   * @param request
   * @return
   */
  public Future<PerceptualSearchResult> search(ChunkTypeRequest request);
  
  public void getRecentSearchResults(List<PerceptualSearchResult> results);

  public PerceptualSearchResult getLastSearchResult();
  
}
