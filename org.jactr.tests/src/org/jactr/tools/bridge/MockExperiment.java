/*
 * Created on Apr 12, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.tools.bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.RealTimeClock;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * @author developer
 */
public class MockExperiment implements Runnable
{
  /**
   * logger definition
   */
  static private final Log LOGGER        = LogFactory
                                             .getLog(MockExperiment.class);

  static public final int  TRIALS        = 20;

  private List<Object[]>   _trialStimuli = new ArrayList<Object[]>();

  private int              _trialIndex   = -1;

  private IClock           _timer;

  public MockExperiment()
  {
    this(false);
  }

  public MockExperiment(boolean isSimulated)
  {
    setupResponses(TRIALS);
    if (isSimulated)
      _timer = ACTRRuntime.getRuntime().getClock(null);
    else
      _timer = new RealTimeClock();
  }

  protected void setupResponses(int trials)
  {
    ArrayList<String> responses = new ArrayList<String>();
    responses.addAll(Arrays.asList(new String[] { "a", "b", "c", "d", "e", "f",
        "g", "h", "i", "j" }));

    ArrayList<Double> delays = new ArrayList<Double>();
    delays.add(1.0);
    delays.add(1.5);
    delays.add(2.0);

    while (trials > 0)
    {
      Collections.shuffle(responses);
      Collections.shuffle(delays);
      /*
       * not the best random assigned, but this is an example.
       */
      for (String response : responses)
        for (double delay : delays)
        {
          _trialStimuli.add(new Object[] { delay, response });
          trials--;
        }
    }

    Collections.shuffle(_trialStimuli);
  }

  public void run()
  {
    for (Object[] trial : _trialStimuli)
    {
      _trialIndex++;
      double showAfter = (Double) trial[0];
      String keyToPress = (String) trial[1];

      /*
       * no delay on the first prompt
       */
      if (_trialIndex == 0) showAfter = 0;

      prompt(showAfter, keyToPress);
    }
  }

  protected int getNumberOfTrials()
  {
    return _trialStimuli.size();
  }

  protected int getTrialIndex()
  {
    return _trialIndex;
  }

  protected List<Object[]> getTrials()
  {
    return Collections.unmodifiableList(_trialStimuli);
  }

  /*
   * display the stimuli
   */
  protected void prompt(final double showAfterSeconds, final String keyToPress)
  {
    try
    {
      /*
       * block until showAfterSeconds have elapsed
       */
      double waitUntil = _timer.getTime() + showAfterSeconds;
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting until " + waitUntil);
      double start = _timer.waitForTime(waitUntil);

      /*
       * this will block until the "user" enters some text and presses enter
       */
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Prompting @ " + start);
      String response = JOptionPane.showInputDialog("Enter " + keyToPress);

      double now = _timer.getTime();

      log(now, response, now - start);
    }
    catch (InterruptedException e)
    {
      LOGGER.error("Exception ", e);
    }
  }

  protected void log(double now, String response, double delta)
  {
    System.err.println("User reponded " + response + " at " + now + " after " +
        delta + "s");
  }

  static public void main(String[] argv)
  {
    new MockExperiment().run();
  }
}
