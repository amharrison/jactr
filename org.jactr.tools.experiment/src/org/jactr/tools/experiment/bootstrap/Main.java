package org.jactr.tools.experiment.bootstrap;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;

public class Main
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory.getLog(Main.class);

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    IExperiment experiment = StartModelExperiments.loadExperiment(args[0], null);
    experiment.start();
  }

}
