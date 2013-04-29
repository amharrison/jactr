package org.jactr.tools.itr.ortho.impl;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.tools.itr.ortho.ISlice;
import org.jactr.tools.itr.ortho.ISliceAnalysis;

public class SliceAnalysis implements ISliceAnalysis
{

  static final private String                    FLAGGED_KEY  = "flag";

  static final private String                    RMSE_KEY     = "RMSE";

  static final private String                    R_SQUARE_KEY = "RSquared";

  static final private String                    N_KEY        = "N";

  final private ISlice                           _slice;

  private Object                                 _result;

  private boolean                                _flagged;

  private String                                 _notes;

  final private Map<String, String>              _models;

  final private Map<String, Map<String, String>> _fitStatMap;

  final private String                             _workingDirectory;

  final private Map<String, String>              _images;

  final private Map<String, String>              _details;

  public SliceAnalysis(ISlice slice, String workingDirectory)
  {
    _slice = slice;
    _models = new TreeMap<String, String>();
    _images = new TreeMap<String, String>();
    _details = new LinkedHashMap<String, String>();
    _workingDirectory = workingDirectory;
    _fitStatMap = new TreeMap<String, Map<String, String>>();
  }

  public void addFitStatistics(String label, double rmse, double rsquare,
      long n, boolean flagged)
  {
    Map<String, String> stats = new TreeMap<String, String>();
    NumberFormat format = NumberFormat.getNumberInstance();
    format.setMaximumFractionDigits(3);
    
    stats.put(RMSE_KEY, format.format(rmse));
    stats.put(R_SQUARE_KEY, format.format(rsquare));
    stats.put(N_KEY, Long.toString(n));
    addFitStatistics(label, stats, flagged);
  }

  public void addFitStatistics(String label, Map<String, String> stats,
      boolean flagged)
  {
    Map<String, String> current = _fitStatMap.get(label);
    if (current == null)
    {
      current = new TreeMap<String, String>(stats);
      _fitStatMap.put(label, current);
    }
    else
      current.putAll(stats);

    current.put(FLAGGED_KEY, Boolean.toString(flagged));

    if (flagged) setFlagEnabled(true);
  }
  
  public Map<String, Map<String,String>> getFitStatistics()
  {
    return Collections.unmodifiableMap(_fitStatMap);
  }

  public void setResult(Object result)
  {
    _result = result;
  }

  public Object getResult()
  {
    return _result;
  }

  public ISlice getSlice()
  {
    return _slice;
  }

  public boolean isFlagged()
  {
    return _flagged;
  }

  public void setFlagEnabled(boolean flag)
  {
    _flagged = flag;
  }
  
  public Map<String, String> getModels()
  {
    return Collections.unmodifiableMap(_models);
  }

  public void addModel(String modelName, String sliceRelativePath)
  {
    _models.put(modelName, sliceRelativePath);
  }

  public String getWorkingDirectory()
  {
    return _workingDirectory;
  }
  
  public Map<String, String> getImages()
  {
    return Collections.unmodifiableMap(_images);
  }

  public void addImage(String label, String workingRelativePath)
  {
    _images.put(label, workingRelativePath);
  }

  public void addDetail(String label, String workingRelativePath)
  {
    _details.put(label, workingRelativePath);
  }
  
  public Map<String, String> getDetails()
  {
    return Collections.unmodifiableMap(_details);
  }

  public void write(PrintWriter pw)
  {
    NumberFormat format = NumberFormat.getNumberInstance();
    format.setMaximumFractionDigits(3);
    pw.println(" <slice id=\"" + _slice.getId() + "\" flag=\"" + _flagged
        + "\" n=\"" + (_slice.getLastIteration() - _slice.getFirstIteration())
        + "\">");
    pw.println("  <models>");
    for (Map.Entry<String, String> model : _models.entrySet())
      pw.println("   <model name=\"" + model.getKey() + "\" url=\""
          + model.getValue() + "\"/>");
    pw.println("  </models>");
    pw.println("  <parameters>");
    for (Map.Entry<String, Object> parameter : _slice.getParameterValues()
        .entrySet())
      pw.println("   <parameter name=\"" + parameter.getKey() + "\" value=\""
          + parameter.getValue() + "\"/>");
    pw.println("  </parameters>");
    pw.println("  <fits>");

    StringBuilder tmp = new StringBuilder();

    for (Map.Entry<String, Map<String, String>> fit : _fitStatMap.entrySet())
    {
      if (tmp.length() > 0) tmp.delete(0, tmp.length());
      tmp.append("   <fit label=\"");
      tmp.append(fit.getKey());
      tmp.append("\" ");
      for(Map.Entry<String, String> stat : fit.getValue().entrySet())
        tmp.append(stat.getKey()).append("=\"").append(stat.getValue()).append("\" ");
      tmp.append("/>");
      pw.println(tmp.toString());
    }
    
    pw.println("  </fits>");

    writeDetails(pw);
    /*
     * we also want to generate the images.xml if images were provided
     */
    if (_images.size() != 0) writeImages(pw);

    String notes = getNotes();
    if (notes != null)
    {
      pw.println("  <notes><![CDATA[");
      pw.println(notes);
      pw.println("  ]]></notes>");
    }

    pw.println(" </slice>");

  }

  private void writeDetails(PrintWriter pw)
  {
    pw.println("  <details>");
    for (Map.Entry<String, String> entry : _details.entrySet())
      pw.println("   <detail label=\"" + entry.getKey() + "\" url=\""
          + entry.getValue() + "\"/>");
    pw.println("  </details>");
  }

  private void writeImages(PrintWriter pw)
  {
    pw.println("  <images>");
    for (Map.Entry<String, String> entry : _images.entrySet())
      pw.println("   <image label=\"" + entry.getKey() + "\" url=\""
          + entry.getValue() + "\"/>");
    pw.println("  </images>");
  }

  public String getNotes()
  {
    return _notes;
  }

  public void setNotes(String notes)
  {
    _notes = notes;

  }

}
