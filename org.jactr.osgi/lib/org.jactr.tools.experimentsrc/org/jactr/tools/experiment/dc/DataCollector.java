package org.jactr.tools.experiment.dc;

/*
 * default logging
 */
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.tools.experiment.dc.data.ISubjectDataProvider;
import org.jactr.tools.experiment.dc.data.SubjectData;

/**
 * static singleton entry point for getting {@link SubjectData}
 * 
 * @author harrison
 */
public class DataCollector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DataCollector.class);

  static private DataCollector       _default;

  static public void set(DataCollector collector)
  {
    _default = collector;
  }

  static public DataCollector get()
  {
    return _default;
  }

  /**
   * create a timestamp subjectId
   * 
   * @return
   */
  static public String createSubjectId(long systemTime)
  {
    return String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", new Date(
        systemTime));
  }

  final private Map<String, SubjectData<?>> _subjectData;

  private ISubjectDataProvider<?>           _dataProvider;

  private File                              _dataPath;

  public DataCollector(ISubjectDataProvider<?> provider)
  {
    _subjectData = new TreeMap<String, SubjectData<?>>();
    setDataProvider(provider);
  }

  public void setDataProvider(ISubjectDataProvider<?> provider)
  {
    _dataProvider = provider;
  }

  public void setDataDirectory(File dir)
  {
    _dataPath = dir;
  }

  /**
   * @param model
   * @return
   */
  static public String getSubjectId(IModel model)
  {
    synchronized (model)
    {
      String modelId = (String) model.getMetaData("dataCollector.subjectId");
      if (modelId == null)
      {
        modelId = createSubjectId(model.hashCode() + System.currentTimeMillis());
        model.setMetaData("dataCollector.subjectId", modelId);
      }
      return modelId;
    }
  }

  /**
   * Get or create the subject data for this model. If the model has
   * {@link IModel#getMetaData(String)} "dataCollector.subjectId", it will use
   * that string for the subject Id. Otherwise, one will be created (and the
   * metadata set). Just call {@link SubjectData#getSubjectId()} to see what it
   * is.
   * 
   * @param model
   * @param createIfMissing
   * @return
   */
  synchronized public SubjectData<?> getSubjectData(IModel model,
      boolean createIfMissing)
  {
    String modelId = getSubjectId(model);
    return getSubjectData(modelId, createIfMissing);
  }

  /**
   * not thread safe.
   * 
   * @param subjectId
   * @param createIfMissing
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  synchronized public SubjectData<?> getSubjectData(String subjectId,
      boolean createIfMissing)
  {
    SubjectData<?> data = _subjectData.get(subjectId);
    if (data == null && createIfMissing)
    {
      data = new SubjectData(subjectId, new File(_dataPath, subjectId),
          _dataProvider, _dataProvider.newData());
      _dataProvider.newSubject(data);
      _subjectData.put(subjectId, data);
    }
    return data;
  }

  synchronized public SubjectData<?> removeSubjectData(String subjectId)
  {
    return _subjectData.remove(subjectId);
  }

  synchronized public void clear()
  {
    _subjectData.clear();
  }

}
