package org.jactr.core.concurrent;

/*
 * default logging
 */
import java.util.WeakHashMap;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneralThreadFactory implements ThreadFactory
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER           = LogFactory
                                                            .getLog(GeneralThreadFactory.class);

  private int                          _count           = 0;

  private String                       _nameTemplate;

  private ThreadGroup                  _group;

  private WeakHashMap<Thread, Boolean> _membershipMap;

  /**
   * thread groups are currently disabled because in order to dispose of them
   * correctly, all their threads need to have exited, however, the disposal
   * will usually be called from one of those threads, hence it will always
   * throw an exception.
   */
  private boolean                      _useThreadGroups = false;

  public GeneralThreadFactory(String nameTemplate)
  {
    this(nameTemplate, null);
  }

  public GeneralThreadFactory(String nameTemplate, ThreadGroup parentGroup)
  {
    _nameTemplate = nameTemplate;

    if (_useThreadGroups) if (parentGroup != null)
      _group = new ThreadGroup(parentGroup, _nameTemplate);
    else
      _group = new ThreadGroup(_nameTemplate);

    _membershipMap = new WeakHashMap<Thread, Boolean>();
  }

  /**
   * destroy the thread group, if any exists
   */
  public void dispose()
  {
    if (_group != null)
      try
      {
        _group.destroy();
        _group = null;
      }
      catch (Exception e)
      {
        LOGGER.error(
            "Could not destory thread group " + _group.getName() + " ", e);
      }
  }

  public ThreadGroup getThreadGroup()
  {
    return _group;
  }

  public Thread newThread(final Runnable r)
  {
    Thread t = null;

    Runnable defensive = new Runnable() {
      public void run()
      {
        try
        {
          r.run();
        }
        catch (Throwable thrown)
        {
          LOGGER.error("Uncaught exception on "
              + Thread.currentThread().getName() + ", while executing "+r+"("+r.getClass().getName()+") : " + thrown.getMessage()
              + " ", thrown);
        }
      }
    };

    if (_group != null)
      t = new Thread(_group, defensive);
    else
      t = new Thread(defensive);

    t.setName(_nameTemplate + "-" + (++_count));

    t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

      public void uncaughtException(Thread t, Throwable e)
      {
        LOGGER.error("Uncaught exception on " + t + " : ", e);
      }

    });

    _membershipMap.put(t, Boolean.TRUE);
    return t;
  }

  public boolean isMember(Thread thread)
  {
    return Boolean.TRUE.equals(_membershipMap.get(thread));
  }

  public boolean isMember()
  {
    return isMember(Thread.currentThread());
  }
}
