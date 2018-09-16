package org.jactr.tools.itr.ortho.exec;

/*
 * default logging
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.tools.itr.ortho.ISliceAnalysis;
import org.jactr.tools.itr.ortho.ISliceAnalyzer;

/**
 * default slice analyzer that has a single parameter AnalysisScript which will
 * be executed when the analysis is requested. This allows aribitrary analysis
 * tools to be used. <br>
 * <br>
 * There are special variables that can be expanded on the command line:
 * ${parameters} will expand to a space separated list of key=value pairs for
 * the parameters manipulated. ${start} and ${stop} expand to the iteration
 * indexes for this slice. ${directories} expands to a space separated list of
 * the model execution directories (relative to the root working directory).
 * ${workingDir} expands to the location of the prefered working directory for
 * this particular analysis (typically: {root}/report/{sliceNumber}). <br>
 * <br>
 * This analyzer can also parse limited output from the analysis script. One
 * item per line, the analyzer looks for the following tokens:<br>
 * FIT: label key1 value1 key2 value2<br>
 * DETAIL: label workingDirRelativeFileLocation<br>
 * IMAGE: label workingDirRelativeFileLocation<br>
 * <br>
 * DETAIL: and IMAGE: allow you to return the label and location of images or
 * additional information files. FIT: allows you to return a label for the fit
 * of a specific metric, followed by the actual statistic names and values. It
 * also looks for a specific key of 'flag' and value 'true' or 'false', which it
 * uses to highlight the particular slice.
 * 
 * @author harrison
 */
public class ExecAnalyzer implements ISliceAnalyzer, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                   = LogFactory
                                                                  .getLog(ExecAnalyzer.class);

  static public final String         SCRIPT                   = "AnalysisScript";

  static public final String         PARAMETERS_OPTION        = "${parameters}";

  static private final String        ESC_PARAMETERS           = "\\$\\{parameters\\}";

  static public final String         START_INDEX_OPTION       = "${start}";

  static private final String        ESC_START                = "\\$\\{start\\}";

  static public final String         STOP_INDEX_OPTION        = "${stop}";

  static private final String        ESC_STOP                 = "\\$\\{stop\\}";

  static public final String         DIRECTORIES_OPTION       = "${directories}";

  static private final String        ESC_DIRECTORIES          = "\\$\\{directories\\}";

  static public final String         WORKING_DIRECTORY_OPTION = "${workingDir}";

  static private final String        ESC_WORKING              = "\\$\\{workingDir\\}";

  static public final String         FIT_KEY                  = "FIT:";

  static public final String         DETAIL_KEY               = "DETAIL:";

  static public final String         IMAGE_KEY                = "IMAGE:";

  private String                     _script;

  public ExecAnalyzer()
  {

  }

  public ExecAnalyzer(String script)
  {
    _script = script;
  }

  public Object analyze(ISliceAnalysis sliceAnalysis)
  {
    try
    {
      Process analysisProc = assembleProcess(sliceAnalysis, _script);
      int returnValue = analysisProc.waitFor();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Process returned %d", returnValue));

      /*
       * parse output
       */
      parseOutput(analysisProc.getInputStream(), sliceAnalysis);
    }
    catch (IOException e)
    {
      LOGGER.error("ExecAnalyzer.analyze threw IOException : ", e);
    }
    catch (InterruptedException e)
    {
      LOGGER.error("ExecAnalyzer.analyze threw InterruptedException : ", e);
    }

    return null;
  }

  private void parseOutput(InputStream inputStream, ISliceAnalysis analysis)
      throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        inputStream));
    String line = null;
    while ((line = reader.readLine()) != null)
      try
      {
        line = line.trim();
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Processing output:%s", line));
        if (line.startsWith(FIT_KEY))
        {
          String[] parts = line.split(" ");
          String label = parts[1];
          boolean flag = false;
          Map<String, String> values = new TreeMap<String, String>();
          for (int i = 2; i < parts.length - 1; i += 2)
          {
            values.put(parts[i], parts[i + 1]);
            if (parts[i].equals("flag"))
              flag = Boolean.parseBoolean(parts[i + 1]);
          }
          analysis.addFitStatistics(label, values, flag);
        }
        else if (line.startsWith(DETAIL_KEY))
        {
          String[] parts = line.split(" ");
          analysis.addDetail(parts[1], parts[2]);
        }
        else if (line.startsWith(IMAGE_KEY))
        {
          String[] parts = line.split(" ");
          analysis.addImage(parts[1], parts[2]);
        }
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to process line: " + line, e);
      }
  }

  private Process assembleProcess(ISliceAnalysis analysis, String template)
      throws IOException
  {
    if (template.indexOf(PARAMETERS_OPTION) != -1)
    {
      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, Object> entry : analysis.getSlice()
          .getParameterValues().entrySet())
        sb.append(entry.getKey()).append("=").append(entry.getValue()).append(
            " ");
      template = template.replaceAll(ESC_PARAMETERS, sb.toString());
    }
    if (template.indexOf(START_INDEX_OPTION) != -1)
      template = template.replaceAll(ESC_START, ""
          + analysis.getSlice().getFirstIteration());
    if (template.indexOf(STOP_INDEX_OPTION) != -1)
      template = template.replaceAll(ESC_STOP, ""
          + analysis.getSlice().getLastIteration());
    if (template.indexOf(DIRECTORIES_OPTION) != -1)
    {
      StringBuilder sb = new StringBuilder();
      for (String dir : analysis.getSlice().getWorkingDirectories())
        sb.append(dir).append(" ");
      template = template.replaceAll(ESC_DIRECTORIES, sb.toString());
    }

    if (template.indexOf(WORKING_DIRECTORY_OPTION) != -1)
      template = template.replaceAll(ESC_WORKING, analysis
          .getWorkingDirectory());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Invoking %s", template));

    ProcessBuilder builder = new ProcessBuilder(template.split(" "));
    builder.redirectErrorStream(true);

    return builder.start();
  }

  public String getParameter(String key)
  {

    return null;
  }

  public Collection<String> getPossibleParameters()
  {

    return null;
  }

  public Collection<String> getSetableParameters()
  {

    return null;
  }

  public void setParameter(String key, String value)
  {
    if (SCRIPT.equalsIgnoreCase(key)) _script = value;
  }

}
