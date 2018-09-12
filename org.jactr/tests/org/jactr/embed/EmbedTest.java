package org.jactr.embed;

/*
 * default logging
 */
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.models.FluentSemantic;
import org.jactr.core.reality.connector.IConnector;
import org.jactr.core.reality.connector.LocalConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmbedTest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EmbedTest.class);

  @Before
  public void setUp() throws Exception
  {
  }

  @After
  public void tearDown() throws Exception
  {
  }

  /**
   * install the handlers for your defaults. org.jactr.modules is handled
   * automatically, but any additional modules, extensions, language parsers or
   * compilers would need to be installed
   * 
   * @throws Exception
   */
  protected void setUpBuiderDefaults(RuntimeBuilder builder) throws Exception
  {

  }

  protected RuntimeBuilder setUpRuntime(IConnector runtimeConnector)
      throws Exception
  {
    RuntimeBuilder builder = RuntimeBuilder.newBuilder();

    setUpBuiderDefaults(builder);

    /*
     * the IConnector is the basic glue between the jactr runtime and the other
     * simulation it is participating in. Perceptionless runs can use the
     * LocalConnector, P/M usually requires CommonRealityConnector or the
     * EmbedConnector
     */
    builder.with(runtimeConnector);

    return builder;
  }

  protected IController buildRuntime(boolean controlledExecution,
      RuntimeBuilder builder)
  {
    IController controller = null;

    /*
     * These are the two terminal RuntimeBuilder methods. From this point on the
     * runtime is fully configured, but not running. debugBuild gives us the
     * DebugController which has support for breakpoints: declarative,
     * procedural, temporal & cycle based
     */
    if (controlledExecution)
      controller = builder.debugBuild();
    else
      controller = builder.build();

    return controller;
  }

  /**
   * dispose of model resources and remove
   */
  protected void cleanUpModels()
  {
    ArrayList<IModel> models = new ArrayList<IModel>(ACTRRuntime.getRuntime()
        .getModels());

    models.forEach(m -> {
      // remove
        ACTRRuntime.getRuntime().removeModel(m);
        // dispose
        try
        {
          m.dispose();
        }
        catch (Exception e)
        {
          LOGGER.error("Failed to dispose of model ", e);
        }
      });
  }

  /**
   * clean up after ourselves
   */
  protected void cleanUpRuntime()
  {
    cleanUpModels();

    /**
     * null out.
     */
//    ACTRRuntime runtime = ACTRRuntime.getRuntime();
//    runtime.setApplicationData(null);
//    runtime.setOnStart(null);
//    runtime.setOnStop(null);
//    runtime.setController(null);
//    runtime.setConnector(null);

  }

  @Test
  public void test() throws Exception
  {
    IConnector connector = new LocalConnector();
    boolean useDebugController = false;

    RuntimeBuilder builder = setUpRuntime(connector);

    /*
     * here's is where you'd add any model sources, or existing models. Model
     * sources will be parsed, compiled and built after the runtime has been
     * configured (so all code injection & compilers work) but before the
     * controller is returned
     */
    // builder.addModel(existingModel);
    builder.addModel(new FluentSemantic());

    /*
     * if you have any application data that you'd like globally visible to all
     * your models, here's the time to provide it. This merely redirects to
     * ACTRRuntime
     * .getRuntime().setApplicationData(applicationDataSupplier.get()) when
     * built.
     */
    // builder.setApplicationData(applicationDataSupplier);

    /*
     * any code you want run right before or after the runtime starts
     */
    // builder.onRuntimeStart(onStart).onRuntimeStop(onStop);

    IController controller = buildRuntime(useDebugController, builder);

    /*
     * after this point builder cannot be reused
     */

    controller.start().get();

    /*
     * to wait until the runtime actually starts: result.get();
     */

    controller.waitForCompletion().get();

    /*
     * upon runtime termination, the connector will be closed and disposed of
     */

    cleanUpRuntime();
  }

}
