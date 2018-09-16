package org.jactr.tools.experiment.dc.data;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.DataBindingException;

/*
 * default logging
 */

/**
 * interface responsible for the creation, and persisting of experiment 
 * specific data.
 * @author harrison
 *
 */
public interface ISubjectDataProvider<T>
{
  public T newData();
  
  public void newSubject(SubjectData<?> data);
  
  /**
   * using the path from {@link SubjectData#getDataDirectory()}
   * @param data
   * @throws IOException
   */
  public void saveData(SubjectData<T> data) throws IOException;
}
