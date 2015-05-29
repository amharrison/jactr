package org.jactr.embed;

/*
 * default logging
 */
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
import org.jactr.io.parser.ModelParserFactory;
import org.jactr.io.participant.ASTParticipantRegistry;
import org.jactr.scripting.ScriptingManager;

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

  private Collection<Runnable>       _parserInitializers;

  private Collection<Runnable>       _astInitializers;

  private Collection<Runnable>       _scriptInitializers;

  private Supplier<Object>           _applicationDataSupplier;

  private Collection<IModel>         _models;

  private IConnector                 _connector;

  static public RuntimeBuilder newBuilder()
  {
    return new RuntimeBuilder();
  }

  protected RuntimeBuilder()
  {
    _parserInitializers = new ArrayList<Runnable>();
    _astInitializers = new ArrayList<Runnable>();
    _scriptInitializers = new ArrayList<Runnable>();
    _models = new ArrayList<IModel>();
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
    _models.add(model);
    return this;
  }

  /**
   * If you need to add access to custom model parsers, it can be contributed at
   * this point. Accessible via the {@link ModelParserFactory} singleton
   * 
   * @param initializer
   * @return
   */
  public RuntimeBuilder addParserInitializer(Runnable initializer)
  {
    _parserInitializers.add(initializer);
    return this;
  }

  /**
   * If you need to add access to custome ast participants (used by modules &
   * extensions), it is contributed here. Accesible via the
   * {@link ASTParticipantRegistry} singelton
   * 
   * @param intializer
   * @return
   */
  public RuntimeBuilder addASTInitializer(Runnable intializer)
  {
    _astInitializers.add(intializer);
    return this;
  }

  /**
   * If you need to add access to custom scripting support. Accessible via the
   * {@link ScriptingManager} singleton
   * 
   * @param initializer
   * @return
   */
  public RuntimeBuilder addScriptInitializer(Runnable initializer)
  {
    _scriptInitializers.add(initializer);
    return this;
  }

  public RuntimeBuilder setApplicationData(Supplier<Object> supplier)
  {
    _applicationDataSupplier = supplier;
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

    runtime.setConnector(null);

    /*
     * zero our application data
     */
    runtime.setApplicationData(null);

    Collection<IModel> models = runtime.getModels();

    // cleanup any existing models
    models.forEach((m) -> {
      runtime.removeModel(m);
      m.dispose();
    });

  }

  protected void bootstrap()
  {
    /*
     * TODO defensive exception handling
     */
    for (Runnable init : _scriptInitializers)
      init.run();

    for (Runnable init : _astInitializers)
      init.run();

    for (Runnable init : _parserInitializers)
      init.run();

    if (_applicationDataSupplier != null)
      ACTRRuntime.getRuntime().setApplicationData(
          _applicationDataSupplier.get());
  }

  protected void initialize()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();


    runtime.setConnector(_connector);

    for (IModel model : _models)
      runtime.addModel(model);
  }



}
