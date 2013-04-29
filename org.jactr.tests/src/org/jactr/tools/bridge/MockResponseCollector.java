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

import java.awt.Robot;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * @author developer
 */
public class MockResponseCollector extends ResponseCollector
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(MockResponseCollector.class);

  /**
   * robot lets me issue UI events..
   */
  protected Robot          _robot;

  public MockResponseCollector()
  {
    try
    {
      _robot = new Robot();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not create robot ", e);
      throw new RuntimeException("Could not create roboot", e);
    }
  }

  /**
   * @see org.jactr.tools.bridge.ResponseCollector#handleResponse(org.jactr.core.model.IModel,
   *      VariableBindings)
   */
  @Override
  public void handleResponse(final IModel model,
      VariableBindings variableBindings)
  {
    /*
     * we know that the component will snag the focus - so unless someone
     * changes the focus before we can respond - we're good
     */
    final String str = ((String) variableBindings.get("=str")).toUpperCase();
    double now = ACTRRuntime.getRuntime().getClock(model).getTime();

    ITimedEvent response = new AbstractTimedEvent(now, now + Math.random()
        + 0.05) {
      @Override
      public void fire(final double now)
      {
        try
        {
          SwingUtilities.invokeAndWait(new Runnable() {
            public void run()
            {
              KeyStroke stroke = KeyStroke.getKeyStroke(str);

              if (LOGGER.isDebugEnabled())
                LOGGER.debug("pressing " + stroke + " @ " + now);
              /*
               * press the key
               */
              _robot.keyPress(stroke.getKeyCode());
              _robot.keyRelease(stroke.getKeyCode());

              /*
               * and enter to dismiss the prompt
               */
              _robot.keyPress(KeyEvent.VK_ENTER);
              _robot.keyRelease(KeyEvent.VK_ENTER);
            }
          });

          // we also remove the current goal..
          IActivationBuffer goalBuffer = model
              .getActivationBuffer(IActivationBuffer.GOAL);
          goalBuffer.removeSourceChunk(goalBuffer.getSourceChunk());
        }
        catch (Exception e)
        {
          LOGGER.error("Could not wait for swing ", e);
        }
      }
    };

    model.getTimedEventQueue().enqueue(response);
  }
}
