package org.jactr.tools.itr.fit;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;

@Deprecated
public abstract class AbstractModelFitAnalysis implements
    IIterativeRunListener, IParameterized
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER         = LogFactory
                                                  .getLog(AbstractModelFitAnalysis.class);

  static final public String   CLUSTERS       = "Clusters";

  static final public String   FILENAME       = "FileName";

  static final public String   CLUSTER_PREFIX = "Prefix";

  List<String>                 _clusters;

  String                       _prefix        = "";

  PrintWriter                  _output;

  int                          _clusterSize;

  ModelFitStatistics           _modelFit;

  int                          _total;

  public AbstractModelFitAnalysis()
  {
    _clusters = new ArrayList<String>();

    _output = new PrintWriter(System.out);
  }

  protected PrintWriter getOutput()
  {
    return _output;
  }

  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException
  {


  }

  public void postRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    String clusterName = _clusters.get(Math.min(_clusters.size() - 1,
        (currentRunIndex - 1) / _clusterSize));
    collectData(currentRunIndex, _prefix + clusterName, models, _modelFit);

    int remaining = totalRuns - currentRunIndex;
    if (remaining == 0 ||
        remaining >= _clusterSize && currentRunIndex % _clusterSize == 0)
      finishCurrentCluster(currentRunIndex);
  }

  private void finishCurrentCluster(int currentRunIndex)
  {
    String clusterName = _prefix +
        _clusters.get(Math.min(_clusters.size() - 1, (currentRunIndex - 1) /
            _clusterSize));

    collectData(currentRunIndex, clusterName, _modelFit);
//    outputStatistics(_modelFit, getOutput());

    _modelFit = new ModelFitStatistics();
  }

  public void preBuild(int currentRunIndex, int totalRuns,
      Collection<CommonTree> modelDescriptors) throws TerminateIterativeRunException
  {
    // TODO Auto-generated method stub

  }

  public void preRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    // TODO Auto-generated method stub

  }

  public void start(int totalRuns) throws TerminateIterativeRunException
  {
    if (_clusters.size() == 0) _clusters.add("ALL");
    _clusterSize = totalRuns / _clusters.size();

    _modelFit = new ModelFitStatistics();
    _total = totalRuns;

    outputHeader(getOutput());
    getOutput().flush();
  }

  public void stop()
  {
    outputFooter(getOutput());
    getOutput().flush();
  }

  /**
   * called after each iteration
   * 
   * @param models
   */
  abstract protected void collectData(int runIndex, String clusterName,
      Collection<IModel> models, ModelFitStatistics fit);

  /**
   * called at the end of each cluster
   * 
   * @param runIndex
   * @param fit
   */
  abstract protected void collectData(int runIndex, String clusterName,
      ModelFitStatistics fit);

  abstract protected void outputStatistics(ModelFitStatistics fit,
      PrintWriter out);

  abstract protected void outputHeader(PrintWriter out);

  abstract protected void outputFooter(PrintWriter out);

  public String getParameter(String key)
  {
    if (CLUSTERS.equalsIgnoreCase(key)) return _clusters.toString();
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Collections.singleton(CLUSTERS);
  }

  public void setParameter(String key, String value)
  {
    if (CLUSTERS.equalsIgnoreCase(key))
    {
      String[] clusters = value.split(",");
      for (String cluster : clusters)
      {
        cluster = cluster.trim();
        if (cluster.length() != 0) _clusters.add(cluster);
      }
    }
    else if (CLUSTER_PREFIX.equalsIgnoreCase(key))
      _prefix = value;
    else if (FILENAME.equalsIgnoreCase(key))
      try
      {
        if ("out".equalsIgnoreCase(value))
          _output = new PrintWriter(System.out);
        else if ("err".equalsIgnoreCase(value))
          _output = new PrintWriter(System.err);
        else
        {
          File fp = new File(value);
          fp.getParentFile().mkdirs();
          _output = new PrintWriter(new FileWriter(fp, true));
        }
      }
      catch (Exception e)
      {
        if (LOGGER.isErrorEnabled())
          LOGGER.error("Could not set output to " + value, e);
        _output = new PrintWriter(System.out);
      }
  }
}
