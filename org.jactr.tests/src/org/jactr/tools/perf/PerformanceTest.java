/*
 * Created on Apr 13, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.perf;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.entry.iterative.IterativeMain;

/**
 * @author developer
 */
public class PerformanceTest extends TestCase
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(PerformanceTest.class);

  public void testSemantic1() throws Exception
  {
    System.setProperty("iterative-id", "1");
    run(getClass().getClassLoader().getResource(
        "org/jactr/tools/perf/semantic x 1.xml"));
  }

  public void testSemantic2() throws Exception
  {
    System.setProperty("iterative-id", "2");
    run(getClass().getClassLoader().getResource(
        "org/jactr/tools/perf/semantic x 2.xml"));
  }
  
  public void testVisual1() throws Exception
  {
    System.setProperty("iterative-id", "1");
    run(getClass().getClassLoader().getResource(
        "org/jactr/tools/perf/visual x 1.xml"));
  }
  
  public void testVisual2() throws Exception
  {
    System.setProperty("iterative-id", "2");
    run(getClass().getClassLoader().getResource(
        "org/jactr/tools/perf/visual x 2.xml"));
  }

  protected void run(URL url) throws Exception
  {
    IterativeMain main = new IterativeMain();
    long start = System.currentTimeMillis();
    main.run(url);
    long delta = System.currentTimeMillis() - start;
    if (LOGGER.isDebugEnabled()) LOGGER.debug("[" + delta + "ms]");
  }
}
