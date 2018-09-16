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

public class CSVDataLogger implements IDataLogger
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(CSVDataLogger.class);

  private IExperiment                _experiment;

  private PrintWriter                _writer;

  private String                     _path;

  static private int                 _iterations;

  public CSVDataLogger()
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
      LOGGER.error("CSVDataCollector.setPath threw IOException : ", e);
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
    LOGGER
        .debug("open tag not fully supported by CSV writer, trying as best as I can.");
    write(tagName, false, false, attributes, context);
  }

  public void close(String tagName)
  {
    LOGGER
        .debug("open tag not fully supported by CSV writer, trying as best as I can.");
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
    StringBuilder sb = new StringBuilder();

    /*
     * we require the line="attrName,attrName"
     */

    String line = attributes.get("line");
    if (line == null)
    {
      LOGGER.debug("Found no line attribute, cannot remap");
      return;
    }

    String[] orderedAttrs = line.trim().split(",");

    VariableResolver resolver = _experiment.getVariableResolver();

    for (String attr : orderedAttrs)
    {
      attr = attr.trim();
      String attrValue = attributes.get(attr);

      if (attrValue != null)
        attrValue = resolver.resolve(attrValue, context).toString();
      else
        attrValue = attr;

      sb.append(attrValue).append(",");
    }

    // delete the last one
    int len = sb.length();
    if (len > 0) sb.delete(len - 1, len);

    PrintWriter writer = getWriter();
    writer.println(sb.toString());
    writer.flush();
  }

}
