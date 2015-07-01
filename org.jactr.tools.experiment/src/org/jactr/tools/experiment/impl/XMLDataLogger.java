package org.jactr.tools.experiment.impl;

/*
 * default logging
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;

public class XMLDataLogger implements IDataLogger
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(XMLDataLogger.class);

  private IExperiment                _experiment;

  private int                        _level = 0;

  private PrintWriter                _writer;

  private String                     _path;

  static private int                 _iterations;

  public XMLDataLogger()
  {
    _iterations++;
  }

  public void setExperiment(IExperiment experiment)
  {
    _experiment = experiment;
  }

  public void setPath(String relativePath, String fileName)
  {
    /*
     * model will already be defined
     */
    String subj = _experiment.getVariableResolver()
        .resolve("${SubjectId}", _experiment.getVariableContext()).toString();

    File fp = new File(relativePath, subj);
    fp.mkdirs();
    fp = new File(fp, fileName);

    _path = fp.getPath();

    try
    {
      _writer = new PrintWriter(new FileWriter(fp));
    }
    catch (IOException e)
    {
      LOGGER.error("XMLDataCollector.setPath threw IOException : ", e);
    }
  }

  public String getPath()
  {
    return _path;
  }

  synchronized public PrintWriter getWriter()
  {
    if (_writer == null) _writer = new PrintWriter(System.out);
    return _writer;
  }

  public void open(String tagName, Map<String, String> attributes,
      IVariableContext context)
  {
    write(tagName, false, false, attributes, context);
  }

  public void close(String tagName)
  {
    write(tagName, true, false, Collections.EMPTY_MAP,
        _experiment.getVariableContext());
  }

  public void simple(String tagName, Map<String, String> attributes,
      IVariableContext context)
  {
    write(tagName, false, true, attributes, context);
  }

  synchronized private void write(String tagName, boolean close,
      boolean simple, Map<String, String> attributes, IVariableContext context)
  {

    if (close) _level--;

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < _level; i++)
      sb.append(" ");
    if (close)
      sb.append("</");
    else
      sb.append("<");

    sb.append(tagName);

    if (!close)
    {
      VariableResolver resolver = _experiment.getVariableResolver();
      for (Map.Entry<String, String> entry : attributes.entrySet())
      {
        String attr = entry.getKey();
        String value = entry.getValue();
        value = resolver.resolve(value, context).toString();
        sb.append(" ").append(attr).append("=").append("\"").append(value)
            .append("\"");
      }
    }

    if (simple)
      sb.append("/>");
    else
      sb.append(">");

    PrintWriter writer = getWriter();
    writer.println(sb.toString());
    writer.flush();

    if (!simple && !close) _level++;
  }

}
