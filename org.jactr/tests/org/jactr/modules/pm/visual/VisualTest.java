package org.jactr.modules.pm.visual;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.fluent.RealityConfigurator;
import org.commonreality.net.message.credentials.ICredentials;
import org.commonreality.net.message.credentials.PlainTextCredentials;
import org.commonreality.reality.CommonReality;
import org.commonreality.sensors.ISensor;
import org.commonreality.sensors.xml2.XMLSensor;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.reality.connector.CommonRealityConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.utils.StringUtilities;
import org.jactr.test.ExecutionTester;
import org.junit.Test;

public class VisualTest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(VisualTest.class);

  protected void interrogateCR()
  {
    for (IAgent agent : CommonReality.getAgents())
      System.err.println(agent);
    for (ISensor sensor : CommonReality.getSensors())
      System.err.println(sensor);
  }

  protected void commonReality(String modelName) throws Exception
  {
    interrogateCR();

    try
    {
    ICredentials a = new PlainTextCredentials("xml", "123"),
        b = new PlainTextCredentials("model", "123");

      RealityConfigurator config = new RealityConfigurator().credentials(a)
        .credentials(b);

    // model agent, linked through modelName
      config.agent(new ACTRAgent()).client().local().connectAt("997")
        .configure().credentials(b)
        .configure(Collections.singletonMap("ACTRAgent.ModelName", modelName));

    // xml sensor to provide stimuli
      config.sensor(new XMLSensor()).client().local().connectAt("997")
        .configure().credentials(a).configure(Collections.singletonMap(
              "XMLSensor.DataURI",
              "org/jactr/modules/pm/visual/sensorData.xml"));

      Runnable startup = (Runnable) config.server().local().connectAt("997")
          .configure()
        .configure(Collections.emptyMap());

    startup.run();
    }
    catch (Exception e)
    {
      LOGGER.error(e);
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  protected void run(IModel model) throws Exception
  {
    commonReality(model.getName());

    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    runtime.setController(new DefaultController());
    runtime.setConnector(new CommonRealityConnector());

    runtime.addModel(model);

    IController controller = runtime.getController();

    controller.start().get();
    controller.waitForCompletion().get();

    runtime.removeModel(model);
  }

  protected void cleanup(ExecutionTester tester, IModel model, boolean dispose)
  {
    RealityConfigurator.shutdownRunnable(true).run();

    model.uninstall(tester);
    if (dispose) model.dispose();

    try
    {
      // give all the other threads a chance to cleanup
      Thread.sleep(5000);
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("VisualTest.cleanup threw InterruptedException : ", e);
    }
  }

  protected ExecutionTester setup(IModel model, String[] validSequence,
      boolean fullLogging)
  {
    ExecutionTester tester = new ExecutionTester() {

      @Override
      public void verifyModelState(IModel model, IInstantiation instantiation)
      {
        if (LOGGER.isDebugEnabled())
        {
          IActivationBuffer buffer = model
              .getActivationBuffer(IActivationBuffer.GOAL);
          LOGGER.debug("Goal buffer contents");
          for (IChunk chunk : buffer.getSourceChunks())
            LOGGER.debug("\t" + StringUtilities.toString(chunk) + "\n");
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Instantiation : " + instantiation.getProduction()
                .getSymbolicProduction().getName());
        }
      }
    };

    tester.setProductionSequence(Arrays.asList(validSequence));
    model.install(tester);

    if (fullLogging)
    {
      DefaultModelLogger dml = new DefaultModelLogger();
      dml.setParameter("all", "err");
      model.install(dml);
    }

    return tester;
  }

  @Test
  public void testVisualAttending() throws Throwable
  {
    IModel model = new FluentVisual().get();

    String[] productionSequence = { "search-kind", "search-succeeded",
        "encoding-succeeded", "search-less-than", "search-succeeded",
        "encoding-succeeded", "search-greater-than", "search-succeeded",
        "encoding-succeeded", "search-color", "search-succeeded",
        "encoding-succeeded", "search-size", "search-succeeded",
        "encoding-succeeded", "search-size-succeeded" };

    String[] failures = { "search-failed", "search-match-failed",
        "encoding-failed", "encoding-match-failed" };

    ExecutionTester tester = setup(model, productionSequence, false);

    tester.setFailedProductions(Arrays.asList(failures));

    run(model);
    cleanup(tester, model, true);

    for (Throwable thrown : tester.getExceptions())
      fail(thrown.getMessage());

  }

}
