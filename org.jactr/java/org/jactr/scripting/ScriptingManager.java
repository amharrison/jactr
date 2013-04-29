package org.jactr.scripting;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.scripting.javascript.JavascriptFactory;

public class ScriptingManager
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ScriptingManager.class);

  static private final Collection<IScriptableFactory> _factories = new ArrayList<IScriptableFactory>();

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

  static public IScriptableFactory getFactory(String languageName)
  {
    for (IScriptableFactory factory : _factories)
      if (factory.supports(languageName)) return factory;
    return null;
  }
}
