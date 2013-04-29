package org.jactr.tools.shell;

/*
 * default logging
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bsh.EvalError;
import bsh.Interpreter;

public class DefaultManipulator implements IInterpreterManipulator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                 = LogFactory
                                                                .getLog(DefaultManipulator.class);

  private Collection<String>         _interpreterClassNames = new ArrayList<String>();
  
  public DefaultManipulator()
  {
    _interpreterClassNames.add("org.jactr.tools.shell.EclipseManipulator");
  }

  public void configure(Interpreter interpreter)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Starting server on 9999");
    try
    {
      interpreter.set("currentModel", null);
      URL commonURL = Controller.class.getClassLoader().getResource(
          "org/jactr/tools/shell/common.bsh");
      interpreter.set("commonURL", commonURL);
      interpreter.eval("source(commonURL);");
      interpreter.eval("importCommands(\"org.jactr.tools.shell.commands\");");
      interpreter.eval("server(9999);");
      interpreter.set("runState", "[?]");
    }
    catch (EvalError e)
    {
      /**
       * Error :
       */
      LOGGER.error("Could not configure default settings ", e);
    }
    
    configureOthers(interpreter);
  }
  
  
  protected void configureOthers(Interpreter interpreter)
  {
    for(String className : _interpreterClassNames)
    {
      IInterpreterManipulator manip = createManipulator(className);
      if(manip==null) continue;
      manip.configure(interpreter);
    }
  }
  
  private IInterpreterManipulator createManipulator(String className)
  {
    try
    {
      Class clazz = getClass().getClassLoader().loadClass(className);
      return (IInterpreterManipulator) clazz.newInstance();
    }
    catch(Exception e)
    {
      LOGGER.debug("Could not create "+className, e);
      return null;
    }
  }

}
