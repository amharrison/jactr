package org.jactr.embed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.IConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.DebugController;

/**
 * The start of a runtime builder to better support embedding. If running
 * outside of the Eclipse environment and are using externally contributed
 * modules, you must ensure that classpaths are accessible. This is not
 * reusable, and assumes no one else is attempting to conifugre the runtime
 * 
 * @author harrison
 */
public class RuntimeBuilder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(RuntimeBuilder.class);



  private Collection<Supplier<IModel>> _modelProviders;

  private Supplier<Object>           _applicationDataSupplier;

  private IConnector                 _connector;

  private Runnable                   _onStart;

  private Runnable                   _onStop;

  static public RuntimeBuilder newBuilder()
  {
    return new RuntimeBuilder();
  }

  protected RuntimeBuilder()
  {
    _modelProviders = new ArrayList<>();
  }

  public RuntimeBuilder addModel(Supplier<IModel> modelSupplier)
  {
    _modelProviders.add(modelSupplier);
    return this;
  }

  /**
   * add the model to the runtime to be built. This model must have all it's
   * instruments installed already.
   * 
   * @param model
   * @return
   */
  public RuntimeBuilder addModel(IModel model)
  {
    addModel(() -> {
      return model;
    });
    return this;
  }



  public RuntimeBuilder setApplicationData(Supplier<Object> supplier)
  {
    _applicationDataSupplier = supplier;
    return this;
  }

  public RuntimeBuilder onRuntimeStart(Runnable onStart)
  {
    _onStart = onStart;
    return this;
  }

  public RuntimeBuilder onRuntimeStop(Runnable onStop)
  {
    _onStop = onStop;
    return this;
  }

  /**
   * @param connector
   * @return
   */
  public RuntimeBuilder with(IConnector connector)
  {
    _connector = connector;
    return this;
  }



  /**
   * Terminal build operator that will construct and configure the runtime. It
   * returns the controller that should be used to manage the runtime.
   * 
   * @return
   */
  public IController build()
  {
    buildInternal();
    return defaultController();
  }

  /**
   * Alternatitve terminal build operator that constructs the runtime, returning
   * a debug controller for finer grained runtime control.
   * 
   * @return
   */
  public IController debugBuild()
  {
    buildInternal();
    return debugController();
  }

  protected DefaultController defaultController()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    DefaultController controller = new DefaultController();
    runtime.setController(controller);
    return controller;
  }

  protected DebugController debugController()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    DebugController controller = new DebugController();
    runtime.setController(controller);
    return controller;
  }

  protected void buildInternal()
  {
    resetRuntime();
    bootstrap();
    initialize();
  }

  protected void resetRuntime()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    IController controller = runtime.getController();
    if (controller != null)
    {
      if (controller.isRunning()) try
      {
        controller.terminate().get();
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to terminate runtime, forcing forward ", e);
      }
      runtime.setController(null);
    }

    /*
     * it is also the job of the controller to notify the IConnector to
     * disconnect, cleanup, etc. So we don't do anything for the IConnector
     * other than null it out
     */
    runtime.setConnector(null);

    /*
     * zero our application data
     */
    runtime.setApplicationData(null);

    Collection<IModel> models = runtime.getModels();

    /*
     * Clean up any lingering models.
     */
    models.forEach((m) -> {
      runtime.removeModel(m);
      m.dispose();
    });

  }

  protected void bootstrap()
  {
    if (_applicationDataSupplier != null)
      ACTRRuntime.getRuntime().setApplicationData(
          _applicationDataSupplier.get());
  }

  protected void initialize()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();

    runtime.setConnector(_connector);

    for (Supplier<IModel> model : _modelProviders)
      runtime.addModel(model.get());

    runtime.setOnStart(_onStart);
    runtime.setOnStop(_onStop);
  }



}
