package org.jactr.tools.shell;

/*
 * default logging
 */
import java.net.URL;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bsh.EvalError;
import bsh.Interpreter;

public class Controller
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER               = LogFactory
                                                              .getLog(Controller.class);

  static private ReentrantLock       _sessionLock         = new ReentrantLock();

  static private Condition           _shouldBlockDisposal = _sessionLock
                                                              .newCondition();

  static private Interpreter         _interpreter;

  static Interpreter getInterpreter()
  {
    synchronized (Controller.class)
    {
      if (_interpreter == null) try
      {
        _interpreter = initializeInterpreter();
      }
      catch (EvalError e)
      {
        throw new IllegalStateException("Could not create interpreter ", e);
      }
      
      return _interpreter;
    }
  }

  static private Interpreter initializeInterpreter() throws EvalError
  {
    Interpreter inter = new Interpreter();
    DefaultManipulator manip = new DefaultManipulator();
    
    manip.configure(inter);
    return inter;
  }
  

  /**
   * quit the shell. what this actually does is release the lock from the
   * runtime thread allowing it to exit correctly
   */
  static public void quit()
  {
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Quitting shell");
    _sessionLock.lock();
    try
    {
      _shouldBlockDisposal.signalAll();
    }
    finally
    {
      _sessionLock.unlock();
    }
  }

  static void block()
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Blocking runtime until quit");
    _sessionLock.lock();
    try
    {
      _shouldBlockDisposal.await();
    }
    catch (InterruptedException e)
    {
      LOGGER.error("Controller.block threw InterruptedException : ", e);
    }
    finally
    {
      _sessionLock.unlock();
    }
  }

}
