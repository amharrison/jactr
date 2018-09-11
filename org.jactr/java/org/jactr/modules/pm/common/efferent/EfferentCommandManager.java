package org.jactr.modules.pm.common.efferent;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.efferent.event.IEfferentCommandListener;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.net.message.command.object.IObjectCommand;
import org.commonreality.net.message.request.object.ObjectCommandRequest;
import org.commonreality.net.message.request.object.ObjectDataRequest;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.IMutableObject;
import org.commonreality.object.delta.DeltaTracker;
import org.commonreality.object.delta.FullObjectDelta;
import org.commonreality.object.delta.IObjectDelta;
import org.commonreality.object.identifier.ISensoryIdentifier;
import org.commonreality.object.manager.event.IObjectEvent;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.collections.FastSetFactory;

/**
 * abstract class used to manage specific efferent commands.Extending this class
 * will give you access to callbacks for the state changes of the commands. This
 * manager allows you to create and submit new commands (
 * {@link #newCommand(IEfferentObject, Object...)} request changes (
 * {@link #request(DeltaTracker, org.commonreality.efferent.IEfferentCommand.RequestedState)}
 * , execute and abort ({@link #execute(DeltaTracker)}
 * {@link #abort(DeltaTracker)}), and upon completion (typically called from
 * {@link #commandCompleted(IEfferentCommand)} and
 * {@link #commandAborted(IEfferentCommand)}), the ability to remove the
 * command.
 * 
 * @author harrison
 * @param <C>
 */
public abstract class EfferentCommandManager<C extends IEfferentCommand>
{
  /**
   * Logger definition
   */
  static private final transient Log                          LOGGER             = LogFactory
                                                                                     .getLog(EfferentCommandManager.class);

  private IAgent                                              _agent;

  /**
   * commands that have been accepted
   */
  final private Map<IIdentifier, IndividualCommandManager<C>> _managedCommands;

  /**
   * internally requested aborts
   */
  final private Set<IIdentifier>                              _requestedAbortions;

  final private Set<IIdentifier>                              _currentExecutions;

  /**
   * most recent request
   */
  final private Map<IIdentifier, FutureCommand>               _futureCommands;

  /**
   * automatically delete completed commands
   */
  private boolean                                             _autoDeleteEnabled = false;

  final private ReentrantReadWriteLock                        _lock              = new ReentrantReadWriteLock();

  final private IEfferentCommandListener                      _listener;

  final private Executor                                      _executor;

  public EfferentCommandManager(Executor executor)
  {
    _futureCommands = new HashMap<IIdentifier, FutureCommand>();
    _managedCommands = new HashMap<IIdentifier, IndividualCommandManager<C>>();
    _currentExecutions = new HashSet<IIdentifier>();
    _requestedAbortions = new HashSet<IIdentifier>();
    _executor = executor;

    _listener = new IEfferentCommandListener() {

      public void objectsAdded(IObjectEvent<IEfferentCommand, ?> addEvent)
      {
        List<IEfferentCommand> valid = FastListFactory.newInstance();
        try
        {
          _lock.readLock().lock();
          for (IEfferentCommand command : addEvent.getObjects())
            if (isInterestedIn(command)) valid.add(command);
        }
        finally
        {
          _lock.readLock().unlock();
        }

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Updating commands %s", valid));

        /*
         * fire the events outside of the lock
         */
        for (IEfferentCommand command : valid)
          update((C) command);

        FastListFactory.recycle(valid);
      }

      public void objectsRemoved(IObjectEvent<IEfferentCommand, ?> removeEvent)
      {
        List<C> valid = FastListFactory.newInstance();
        Set<C> abortions = FastSetFactory.newInstance();
        try
        {
          _lock.readLock().lock();
          for (IEfferentCommand command : removeEvent.getObjects())
            if (isInterestedIn(command))
            {
              if (_requestedAbortions.contains(command.getIdentifier()))
                abortions.add((C) command);
              valid.add((C) command);
            }
        }
        finally
        {
          _lock.readLock().unlock();
        }

        /*
         * signal out of lock
         */
        for (C command : valid)
        {
          update(command);
          setAndRelease(command, null);

          /*
           * signal any incomplete abortions..
           */
          if (abortions.contains(command))
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(String
                  .format("%s was aborted on request, delivering late message",
                      command));
            commandAborted(command, true);
          }

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Removed %s", command));
          commandRemoved(command);
        }

        /*
         * once more to remove
         */
        try
        {
          _lock.writeLock().lock();

          for (C command : valid)
          {
            IIdentifier id = command.getIdentifier();
            _currentExecutions.remove(id);
            _requestedAbortions.remove(id);
            _managedCommands.remove(id);
          }
        }
        finally
        {
          _lock.writeLock().unlock();
        }

        FastSetFactory.recycle(abortions);
        FastListFactory.recycle(valid);
      }

      public void objectsUpdated(IObjectEvent<IEfferentCommand, ?> updateEvent)
      {
        List<IEfferentCommand> valid = FastListFactory.newInstance();

        try
        {
          _lock.readLock().lock();
          for (IObjectDelta delta : updateEvent.getDeltas())
          {
            IIdentifier id = delta.getIdentifier();
            if (isInterestedIn(id)
                && delta.getChangedProperties().contains(
                    IEfferentCommand.ACTUAL_STATE))
            {
              IEfferentCommand command = _agent.getEfferentCommandManager()
                  .get(delta.getIdentifier());
              if (command != null) valid.add(command);
            }
          }
        }
        finally
        {
          _lock.readLock().unlock();
        }

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Updating commands %s", valid));

        for (IEfferentCommand command : valid)
          update((C) command);

        FastListFactory.recycle(valid);
      }

    };
  }

  protected ReentrantReadWriteLock getLock()
  {
    return _lock;
  }

  public void install(IAgent agent)
  {
    try
    {
      _lock.writeLock().lock();

      _agent = agent;
      _managedCommands.clear();
      _futureCommands.clear();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
    agent.getEfferentCommandManager().addListener(_listener, _executor);
  }


  public void uninstall(IAgent agent)
  {
    agent.getEfferentCommandManager().removeListener(_listener);
    try
    {
      _lock.writeLock().lock();

      _agent = null;
      _managedCommands.clear();
      _futureCommands.clear();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  protected IndividualCommandManager<C> getIndividualManager(
      IIdentifier commandId)
  {
    try
    {
      _lock.readLock().lock();
      return _managedCommands.get(commandId);
    }
    finally
    {
      _lock.readLock().unlock();
    }

  }

  public IAgent getAgent()
  {
    return _agent;
  }

  public IEfferentObject getMuscle(IIdentifier muscleId)
  {
    return _agent.getEfferentObjectManager().get(muscleId);
  }

  public C getCommand(IIdentifier commandId)
  {
    return (C) _agent.getEfferentCommandManager().get(commandId);
  }

  public boolean isAutoDeleteEnabled()
  {
    return _autoDeleteEnabled;
  }

  public void setAutoDeleteEnabled(boolean enabled)
  {
    _autoDeleteEnabled = enabled;
  }

  /**
   * explicitly request that executing commands are aborted, and issue a remove
   * for all the other commands. All pending futures will return an
   * IllegalStateException. No callbacks for active or pending commands will be
   * called after this.
   */
  public void clear()
  {
    Set<C> commands = FastSetFactory.newInstance();
    try
    {
      _lock.writeLock().lock();

      /*
       * abort executing commands
       */
      for (C command : getExecutingCommands(commands))
        if (!_requestedAbortions.contains(command.getIdentifier()))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format(
                "Requesting abort of executing command %s", command));
          DeltaTracker tracker = new DeltaTracker(command);
          abort(tracker);
        }

      /**
       * remove and set all the futures
       */
      IllegalStateException ise = new IllegalStateException(
          "Command is invalid since efferent command system has been cleared");

      for (IndividualCommandManager<C> manager : _managedCommands.values())
      {
        C command = manager.getCommand();
        if (command != null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Requesting removal of %s", command));
          remove(command);
          setAndRelease(command, ise);
        }
      }

    }
    finally
    {
      FastSetFactory.recycle(commands);

      _lock.writeLock().unlock();
    }
  }

  final public boolean isExecuting()
  {
    try
    {
      _lock.readLock().lock();
      return _currentExecutions.size() != 0;
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  /**
   * @param container
   * @return
   */
  final public Set<IIdentifier> getExecutingCommandIds(
      Set<IIdentifier> container)
  {
    if (container == null) container = new HashSet<IIdentifier>();
    try
    {
      _lock.readLock().lock();
      container.addAll(_currentExecutions);
    }
    finally
    {
      _lock.readLock().unlock();
    }
    return container;
  }

  /**
   * return all the commands that are currently executing
   * 
   * @param container
   * @return
   */
  final public Collection<C> getExecutingCommands(Collection<C> container)
  {
    if (container == null) container = new FastList<C>();
    try
    {
      _lock.readLock().lock();
      for (IIdentifier id : _currentExecutions)
      {
        IndividualCommandManager<C> manager = _managedCommands.get(id);
        if (manager != null && manager.isRunning())
          container.add(manager.getCommand());
      }
    }
    finally
    {
      _lock.readLock().unlock();
    }
    return container;
  }

  /**
   * creates and submits the command, w/o using the future..
   * 
   * @param object
   * @param parameters
   * @return
   */
  final protected C newCommandInternal(IEfferentObject object,
      Object... parameters)
  {
    IEfferentCommand tmpCommand = createCommand(object, parameters);
    IIdentifier id = tmpCommand.getIdentifier();

    try
    {
      _lock.writeLock().lock();
      _managedCommands.put(id, new IndividualCommandManager<C>(this, id));

      _agent.send(new ObjectDataRequest(_agent.getIdentifier(), object
          .getIdentifier().getSensor(), Collections
          .singleton(new FullObjectDelta(tmpCommand))));

      _agent.send(new ObjectCommandRequest(_agent.getIdentifier(), object
          .getIdentifier().getSensor(), IObjectCommand.Type.ADDED, Collections
          .singleton(id)));
    }
    catch (RuntimeException e)
    {
      _managedCommands.remove(id);
      tmpCommand = null;
      throw e;
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    return (C) tmpCommand;
  }

  /**
   * create and submit a new command. parameters will be passed on to
   * {@link #createCommand(IEfferentObject, Object...)} and can be used in
   * anyway desired. <br>
   * The future returned will block until an exception occurs, or the command is
   * accepted or rejected.
   * 
   * @param object
   * @param parameters
   * @return
   */
  final public Future<C> newCommand(IEfferentObject object,
      Object... parameters)
  {
    FutureCommand future = new FutureCommand();

    try
    {
      _lock.writeLock().lock();

      C command = newCommandInternal(object, parameters);
      if (command != null)
        _futureCommands.put(command.getIdentifier(), future);
      else
        future.setResult(null);
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    return future;
  }

  /**
   * request that the command be executed
   * 
   * @param commandChange
   * @return
   */
  public Future<C> execute(DeltaTracker<? extends IMutableObject> commandChange)
  {
    return request(commandChange, IEfferentCommand.RequestedState.START);
  }

  /**
   * request that the command be aborted
   * 
   * @param commandChange
   * @return
   */
  public Future<C> abort(DeltaTracker<? extends IMutableObject> commandChange)
  {
    _requestedAbortions.add(commandChange.getIdentifier());
    return request(commandChange, IEfferentCommand.RequestedState.ABORT);
  }

  protected Future<C> request(
      DeltaTracker<? extends IMutableObject> commandChange,
      IEfferentCommand.RequestedState requestedState)
  {
    IIdentifier id = commandChange.getIdentifier();
    FutureCommand future = new FutureCommand();

    commandChange.setProperty(IEfferentCommand.REQUESTED_STATE, requestedState);

    try
    {
      _lock.writeLock().lock();
      _futureCommands.put(id, future);

      send(commandChange);
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    return future;
  }

  protected void send(DeltaTracker<? extends IMutableObject> commandUpdate)
  {
    IIdentifier id = commandUpdate.getIdentifier();
    _agent.send(new ObjectDataRequest(_agent.getIdentifier(),
        ((ISensoryIdentifier) id).getSensor(), Collections
            .singleton(commandUpdate.getDelta())));

    _agent.send(new ObjectCommandRequest(_agent.getIdentifier(),
        ((ISensoryIdentifier) id).getSensor(), IObjectCommand.Type.UPDATED,
        Collections.singleton(id)));
  }

  /**
   * request removal
   * 
   * @param command
   */
  final public void remove(C command)
  {
    _agent.send(new ObjectCommandRequest(_agent.getIdentifier(), command
        .getIdentifier().getSensor(), IObjectCommand.Type.REMOVED, Collections
        .singleton((IIdentifier) command.getIdentifier())));
  }

  /**
   * will find the future associated with this command, set its return value,
   * release and remove it from the collection of futures
   * 
   * @param command
   */
  private void setAndRelease(C command, Throwable exception)
  {
    FutureCommand future = null;
    try
    {
      _lock.writeLock().lock();
      future = _futureCommands.remove(command.getIdentifier());
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (future != null) if (exception == null)
      future.setResult(command);
    else
      future.setException(exception);
  }

  protected boolean isInterestedIn(IEfferentCommand command)
  {
    return isInterestedIn(command.getIdentifier());
  }

  /**
   * tests the identifier to see if it is pending, future or managed
   * 
   * @param identifier
   * @return
   */
  private boolean isInterestedIn(IIdentifier identifier)
  {
    try
    {
      _lock.readLock().lock();
      return _managedCommands.containsKey(identifier);
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  private void update(C command)
  {
    boolean processed = true;
    boolean removalCandidate = false;
    boolean wasRequested = false;
    IIdentifier commandIdentifier = command.getIdentifier();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(commandIdentifier + " : " + command.getActualState());

    boolean shouldSignal = false;
    try
    {
      _lock.writeLock().lock();

      IndividualCommandManager<C> manager = _managedCommands
          .get(commandIdentifier);

      if (shouldSignal = manager.update(command))
        switch (command.getActualState())
        {
          case ACCEPTED:
            break;
          case REJECTED:
            break;
          case RUNNING:
            _currentExecutions.add(commandIdentifier);
            break;
          case ABORTED:
            wasRequested = _requestedAbortions.remove(commandIdentifier);
            _currentExecutions.remove(commandIdentifier);
            removalCandidate = true;
            break;
          case COMPLETED:
            _currentExecutions.remove(commandIdentifier);
            removalCandidate = true;
            break;
          case UNKNOWN:
            break;
          default:
            if (LOGGER.isWarnEnabled())
              LOGGER.warn("No clue how to handle command " + command + "."
                  + command.getActualState());
            processed = false;
            break;
        }
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (processed)
    {
      setAndRelease(command, null);
      /*
       * signal
       */
      if (shouldSignal) switch (command.getActualState())
      {
        case ACCEPTED:
          commandAccepted(command);
          break;
        case REJECTED:
          commandRejected(command);
          break;
        case RUNNING:
          commandRunning(command);
          break;
        case COMPLETED:
          commandCompleted(command);
          break;
        case ABORTED:
          commandAborted(command, wasRequested);
          break;
        case UNKNOWN:
          break;
      }
    }

    if (removalCandidate && isAutoDeleteEnabled()) remove(command);
  }

  /**
   * create the command using the provided efferent object, from which a
   * template should be extracted and instantiated
   * 
   * @param object
   * @return
   */
  abstract protected C createCommand(IEfferentObject object,
      Object... parameters);

  /**
   * call back executed on the CR executor
   * 
   * @param command
   */
  abstract protected void commandAccepted(C command);

  /**
   * call back executed on the CR executor
   * 
   * @param command
   */
  abstract protected void commandRejected(C command);

  /**
   * call back executed on the CR executor
   * 
   * @param command
   */
  abstract protected void commandRunning(C command);

  /**
   * call back executed on the CR executor
   * 
   * @param command
   */
  abstract protected void commandAborted(C command, boolean wasRequested);

  /**
   * call back executed on the CR executor
   * 
   * @param command
   */
  abstract protected void commandCompleted(C command);

  /**
   * call back executed on the CR executor
   * 
   * @param command
   */
  protected void commandRemoved(C command)
  {

  }

  private class FutureCommand extends FutureTask<C>
  {

    public FutureCommand()
    {
      super(new Runnable() {

        public void run()
        {

        }

      }, null);
    }

    public void setResult(C command)
    {
      set(command);
    }

    @Override
    public void setException(Throwable thrown)
    {
      super.setException(thrown);
    }
  }
}
