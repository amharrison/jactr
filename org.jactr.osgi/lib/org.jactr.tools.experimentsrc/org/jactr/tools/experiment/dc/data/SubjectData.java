package org.jactr.tools.experiment.dc.data;

/*
 * default logging
 */
import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * base class for the data collected for a given subject. The generic T
 * represents the actual data payload, that you, as the modeler/experimenter
 * must decide upon and provide with a factor method provided to the experiment
 * configuration.
 * 
 * @author harrison
 * @param <T>
 */
public class SubjectData<T>
{
  /**
   * Logger definition
   */
  static private final transient Log    LOGGER = LogFactory
                                                   .getLog(SubjectData.class);

  final private T                       _data;

  final private String                  _subjectId;

  final private ISubjectDataProvider<T> _provider;

  final private File                    _dataDir;

  public SubjectData(String subjectId, File dataDir,
      ISubjectDataProvider<T> provider, T data)
  {
    _subjectId = subjectId;
    _data = data;
    _provider = provider;
    _dataDir = dataDir;
  }

  public String getSubjectId()
  {
    return _subjectId;
  }

  public T getData()
  {
    return _data;
  }
  
  public File getDataDirectory()
  {
    return _dataDir;
  }

  public void save() throws IOException
  {
    _dataDir.mkdirs();
    _provider.saveData(this);
  }

}
