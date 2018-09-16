package org.jactr.tools.experiment;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import org.jactr.tools.experiment.impl.IVariableContext;

/*
 * default logging
 */

public interface IDataLogger
{

  public void setExperiment(IExperiment experiment);

  public void setPath(String relativePath, String fileName);

  public String getPath();

  public PrintWriter getWriter();

  public void open(String tagName, Map<String, String> attributes,
      IVariableContext context);

  public void close(String tagName);

  public void simple(String tagName, Map<String, String> attributes,
      IVariableContext context);
}
