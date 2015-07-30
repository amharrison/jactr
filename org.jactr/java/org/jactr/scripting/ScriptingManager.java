package org.jactr.scripting;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.scripting.javascript.JavascriptFactory;

/**
 * not thread safe.
 * 
 * @author harrison
 */
public class ScriptingManager
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ScriptingManager.class);

  static private final Collection<IScriptableFactory> _factories = new ArrayList<IScriptableFactory>();

  static private final Collection<IScriptConfigurator> _configurators = new ArrayList<IScriptConfigurator>();

  static
  {
    install(new JavascriptFactory());
  }

  static public void install(IScriptableFactory factory)
  {
    for (IScriptableFactory existing : _factories)
      if (existing.getClass() == factory.getClass()) return;

    _factories.add(factory);
  }

  static public void uninstall(IScriptableFactory factory)
  {
    _factories.remove(factory);
  }

  static public void install(IScriptConfigurator config)
  {
    for (IScriptConfigurator conf : _configurators)
      if (conf == config) return;
    _configurators.add(config);
  }

  static public void uninstall(IScriptConfigurator config)
  {
    _configurators.remove(config);
  }

  static public IScriptableFactory getFactory(String languageName)
  {
    for (IScriptableFactory factory : _factories)
      if (factory.supports(languageName)) return factory;
    return null;
  }

  /**
   * create a new instance of the script support class. A new instance is used
   * for every invokation of scriptable actions/conditions
   * 
   * @return
   */
  static public ScriptSupport newScriptSupport(IScriptableFactory factory,
      IModel model, VariableBindings bindings)
  {
    ScriptSupport ss = new ScriptSupport(factory, model, bindings);

    return ss;
  }

  static public ScriptSupport newScriptSupport(IScriptableFactory factory,
      IInstantiation instantiation)
  {
    ScriptSupport ss = new ScriptSupport(factory, instantiation);

    return ss;
  }

  /**
   * called by actual script execution code before execution, but after
   * ScriptSupport has been installed
   * 
   * @param factory
   * @param model
   * @param support
   */
  static public void configureScripting(IScriptableFactory factory,
      IModel model, ScriptSupport support, Object scope)
  {
    for (IScriptConfigurator conf : _configurators)
      conf.configure(factory, model, support, scope);
  }
}
