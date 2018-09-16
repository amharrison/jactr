package org.jactr.modules.pm.common.efferent;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.efferent.IEfferentCommand.ActualState;
import org.commonreality.identifier.IIdentifier;

public class IndividualCommandManager<C extends IEfferentCommand>
{
  /**
   * Logger definition
   */
  static private final transient Log      LOGGER = LogFactory
                                                     .getLog(IndividualCommandManager.class);

  final private IIdentifier               _identifier;

  final private EfferentCommandManager<C> _globalManager;

  private ActualState                     _lastKnownState;


  public IndividualCommandManager(EfferentCommandManager<C> manager,
      IIdentifier command)
  {
    _globalManager = manager;
    _identifier = command;
    _lastKnownState = ActualState.UNKNOWN;
  }

  public C getCommand()
  {
    return (C) _globalManager.getAgent().getEfferentCommandManager().get(
        _identifier);
  }

  public IIdentifier getIdentifier()
  {
    return _identifier;
  }

  public ActualState getState()
  {
    return _lastKnownState;
  }

  public boolean canAbort()
  {
    return _lastKnownState == ActualState.RUNNING;
  }

  public boolean canExecute()
  {
    return _lastKnownState == ActualState.ACCEPTED;
  }

  public boolean hasCompleted()
  {
    return _lastKnownState == ActualState.COMPLETED;
  }

  public boolean isRunning()
  {
    return _lastKnownState == ActualState.RUNNING;
  }

  public boolean hasAborted()
  {
    return _lastKnownState == ActualState.ABORTED;
  }

  public boolean isDone()
  {
    return hasAborted() || hasCompleted();
  }

  /**
   * update the states, and return true if some signalling (outside the locks)
   * is necessary.
   * 
   * @return
   */
  public boolean update()
  {
    IEfferentCommand command = _globalManager.getAgent()
        .getEfferentCommandManager().get(_identifier);

    if (command == null)
      throw new IllegalStateException(_identifier
          + " has no associated command");
    return update(command);
  }

  public boolean update(IEfferentCommand command)
  {
    ActualState currentState = command.getActualState();
    // no change
    if (currentState == getState()) return false;
    _lastKnownState = currentState;
    return true;
  }

}
