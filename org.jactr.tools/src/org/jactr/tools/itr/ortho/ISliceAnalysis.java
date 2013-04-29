package org.jactr.tools.itr.ortho;

import java.util.Map;

/**
 * container for the analysis of a particular {@link ISlice}. Images, detailed
 * reports, and fit statistics can be associated with this analysis. As well as
 * a serializable result for further analysis with respect to the other analyses.
 * 
 * @author harrison
 *
 */
public interface ISliceAnalysis
{
  /**
   * associated slice
   * @return
   */
  public ISlice getSlice();
  
  /**
   * the result of the the analysis. If you want to examine different analyses with respect
   * to each other, assign a result and use the {@link ISliceIntegrator}
   * @return
   */
  public Object getResult();
  
  /**
   * add common fit statistics
   * @param label
   * @param rmse
   * @param rsquare
   * @param n
   * @param flag
   */
  public void addFitStatistics(String label, double rmse, double rsquare, long n, boolean flag);
  
  /**
   * general fit statistics
   * @param label
   * @param stats
   * @param flag
   */
  public void addFitStatistics(String label, Map<String, String> stats, boolean flag);
  
  public Map<String, Map<String,String>> getFitStatistics();
  
  public boolean isFlagged();
  
  /**
   * where all analysis files should be written this is relative to
   * the working directory of the batch execution
   */
  public String getWorkingDirectory();
  
  /**
   * provide the path to an analysis generated image relative to
   * the working directory.
   * labels must be unique
   * @param label
   * @param workingRelativePath
   */
  public void addImage(String label, String workingRelativePath);
  
  public Map<String, String> getImages();
  
  /**
   * provide the path to an analysis generated file, typically
   * containing additional details
   * labels must be unique
   * @param label
   * @param workingRelativePath
   */
  public void addDetail(String label, String workingRelativePath);
  
  public Map<String, String> getDetails();
  
  public Map<String, String> getModels();

  public void setNotes(String notes);

  public String getNotes();
}
