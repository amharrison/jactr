package org.jactr.tools.deadlock;

/*
 * default logging
 */
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.management.MBeanServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.impl.ClockInterrogator;
import org.commonreality.util.LockUtilities;

public class DeadLockUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DeadLockUtilities.class);

  /**
   * Attempt to dump the heap to file
   * 
   * @param fileName
   * @param live
   */
  static public void dumpHeap(String fileName, boolean live)
  {
    // initialize hotspot diagnostic MBean
    try
    {
      Class clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
      Method m = clazz.getMethod("dumpHeap", String.class, boolean.class);
      m.invoke(getHotspotMBean(), fileName, live);
    }
    catch (RuntimeException re)
    {
      LOGGER.error("", re);
    }
    catch (Exception exp)
    {
      LOGGER.error("", exp);
    }
  }

  // get the hotspot diagnostic MBean from the
  // platform MBean server
  static private Object getHotspotMBean()
  {
    try
    {
      String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
      Class clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      Object bean = ManagementFactory.newPlatformMXBeanProxy(server,
          HOTSPOT_BEAN_NAME, clazz);
      return bean;
    }
    catch (RuntimeException re)
    {
      throw re;
    }
    catch (Exception exp)
    {
      throw new RuntimeException(exp);
    }
  }

  /**
   * dump all the threads, their info, and potential deadlocks.
   * 
   * @param output
   */
  public static void dumpThreads(String output)
  {

    try
    {
      PrintWriter pw = new PrintWriter(new FileWriter(output));

      final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
      final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(
          threadMXBean.getAllThreadIds(), 100);
      for (ThreadInfo threadInfo : threadInfos)
      {
        pw.println();
        pw.println(threadInfo.getThreadName() + " ID:"
            + threadInfo.getThreadId());
        Thread.State state = threadInfo.getThreadState();
        pw.print("   java.lang.Thread.State: ");
        pw.println(state);

        pw.println(String.format("LockedMonitors : %s",
            Arrays.toString(threadInfo.getLockedMonitors())));
        pw.println(String.format("LockedSynchs : %s",
            Arrays.toString(threadInfo.getLockedSynchronizers())));
        pw.println(String.format("LockInfo : %s", threadInfo.getLockInfo()));
        pw.println(String.format("LockName: %s  LockOwner: %s [%d]",
            threadInfo.getLockName(), threadInfo.getLockOwnerName(),
            threadInfo.getLockOwnerId()));

        pw.println();

        StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
        for (final StackTraceElement stackTraceElement : stackTraceElements)
        {
          pw.print("   ");
          pw.println(stackTraceElement);
        }
      }

      pw.println();
      long[] dlt = threadMXBean.findDeadlockedThreads();
      long[] mdlt = threadMXBean.findMonitorDeadlockedThreads();
      pw.println(String.format("Deadlocked : %s", Arrays.toString(dlt)));
      pw.println(String.format("MonitorDeadlocked : %s", Arrays.toString(mdlt)));

      if (dlt == null && mdlt == null)
        pw.println("Note: The lack of any deadlocked threads suggests a failed time update, perhaps from a simulation participant.");

      pw.println();
      pw.println("Clock information: ");
      pw.println(ClockInterrogator.getAllClockDetails());

      pw.println();
      pw.println(LockUtilities.getLockInfo());

      pw.flush();
      pw.close();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to get thread dump ", e);
    }
  }

  /**
   * interrupts all the threads that we know of. This is horrible and will
   * produce catastrophic errors, but can be useful in knowning where threads
   * may have been locked.
   */
  static public void interruptKnownThreads()
  {

  }
}
