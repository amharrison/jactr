package org.jactr.fluent;

import java.util.ArrayList;
import java.util.Collection;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.core.module.goal.six.DefaultGoalModule6;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.fluent.registry.FluentParticipantRegistry;
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.motor.six.DefaultMotorModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;
import org.jactr.modules.pm.vocal.six.DefaultVocalModule6;

/**
 * fluent model builder for API construction. After {@link #build()}ing, chunks,
 * types, productions can be added. Finally, the model must be
 * {@link IModel#initialize()} before execution.
 * 
 * @author harrison
 */
public class FluentModel
{
  /**
   * Logger definition
   */
  static private final transient Log           LOGGER   = LogFactory
      .getLog(FluentModel.class);

  private String                               _name;

  private Collection<Class<? extends IModule>> _modules = new ArrayList<>();

  /**
   * entry point for model building
   * 
   * @param name
   * @return
   */
  static public FluentModel named(String name)
  {
    FluentModel fm = new FluentModel();
    fm._name = name;
    return fm;
  }

  /**
   * define a module to be installed
   * 
   * @param moduleClass
   * @return
   */
  public FluentModel with(Class<? extends IModule> moduleClass)
  {
    _modules.add(moduleClass);
    return this;
  }

  /**
   * remove a module, useful if you use {@link #withCoreModules()} or
   * {@link #withPMModules()} but need to replace one of the modules.
   * 
   * @param moduleClass
   * @return
   */
  public FluentModel without(Class<? extends IModule> moduleClass)
  {
    _modules.remove(moduleClass);
    return this;
  }

  /**
   * install the core modules required for running, including: goal, retrieval,
   * imaginal procedural.
   * 
   * @return
   */
  public FluentModel withCoreModules()
  {
    with(DefaultDeclarativeModule6.class);
    with(DefaultProceduralModule6.class);
    with(DefaultGoalModule6.class);
    with(DefaultImaginalModule6.class);
    with(DefaultRetrievalModule6.class);

    return this;
  }

  /**
   * install the perceptual motor modules: visual, motor, aural, and vocal
   * 
   * @return
   */
  public FluentModel withPMModules()
  {
    with(DefaultVisualModule6.class);
    with(DefaultAuralModule6.class);
    with(DefaultVocalModule6.class);
    with(DefaultMotorModule6.class);

    return this;
  }

  /**
   * build the model. Now you can add chunks, types, productions. After which,
   * call {@link IModel#initialize()}
   * 
   * @return
   */
  public IModel build()
  {
    IModel model = new BasicModel(_name);
    _modules.forEach(mc -> {
      try
      {
        model.install(mc.newInstance());
        // inject content
        FluentParticipantRegistry.get().getParticipant(mc).ifPresent(c -> {
          c.accept(model);
        });
      }
      catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    });

    return model;
  }
}
