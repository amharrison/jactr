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

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.entry.Main;

/**
 * @author developer
 */
public class MockExpBridgeTest extends TestCase
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(MockExpBridgeTest.class);

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    Main main = new Main();
    main.createRuntime(getClass().getClassLoader().getResource(
        "org/jactr/tools/bridge/environment.xml"));
  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  public void test() throws Exception
  {
    IController controller = ACTRRuntime.getRuntime().getController();
    controller.start().get();
    controller.complete().get();

    Thread.sleep(10000);
  }
}
