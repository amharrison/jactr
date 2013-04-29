package org.jactr.tools.shell;

/*
 * default logging
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import bsh.EvalError;
import bsh.Interpreter;

public class EclipseManipulator implements IInterpreterManipulator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EclipseManipulator.class);

  public void configure(Interpreter interpreter)
  {
    source(interpreter);
    importCommands(interpreter);
  }

  private void importCommands(Interpreter interpreter)
  {
    for (IConfigurationElement config : getExtensionInfo(
        "org.jactr.tools.shell.commands", "package"))
    {
      String packageName = config.getAttribute("name");
      try
      {
        interpreter.eval("importCommands(\"" + packageName + "\")");
      }
      catch (EvalError e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug("Could not import commands from " + packageName + " ", e);
      }
    }
  }

  private void source(Interpreter interpreter)
  {
    for (IConfigurationElement config : getExtensionInfo(
        "org.jactr.tools.shell.commands", "source"))
    {
      String url = config.getAttribute("url");
      String resource = config.getAttribute("resource");
      URL actualURL = null;

      if (resource != null)
        actualURL = getClass().getClassLoader().getResource(resource);
      else if (url != null)
        try
        {
          actualURL = new URL(url);
        }
        catch (Exception e)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn(url + " is not a valid url ", e);
        }

      if (actualURL != null)
        try
        {
          interpreter.set("tmpURL", actualURL);
          interpreter.eval("source(tmpURL)");
          interpreter.unset("tmpURL");
        }
        catch (EvalError e)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Could not source " + url + " ", e);
        }
    }
  }

  private Collection<IConfigurationElement> getExtensionInfo(String extPoint,
      String type)
  {
    Collection<IConfigurationElement> rtn = new ArrayList<IConfigurationElement>();
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    
    //possible if running w/o eclipse launched fully
    if(registry==null) return rtn;
    
    IExtensionPoint exp = registry
        .getExtensionPoint("org.jactr.tools.shell.commands");
    
    if (exp == null) return rtn;

    for (IExtension extension : exp.getExtensions())
      for (IConfigurationElement config : extension.getConfigurationElements())
        if (config.getName().equals(type)) rtn.add(config);

    return rtn;
  }
}
