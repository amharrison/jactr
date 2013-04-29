/*
 * Created on Apr 14, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.utils.StringUtilities;

/**
 * @author developer
 */
public class ExecutionVerifyTest extends TestCase
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ExecutionVerifyTest.class);

  public void testSemanticRun() throws Exception
  {
    URL url = getClass().getClassLoader().getResource(
        "org/jactr/tools/test/semantic.xml");
    String[] sequence = new String[] { "initial-retrieve", "chain-category",
        "chain-category", "fail" };

    ExecutionTester tester = new ExecutionTester();
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Testing " + url);

    for (Throwable thrown : tester.test(url, "semantic", Arrays
        .asList(sequence), Collections.EMPTY_LIST))
    {
      LOGGER.error(thrown);
      fail(thrown.getMessage());
    }
  }

  public void testCountRun() throws Exception
  {
    URL url = getClass().getClassLoader().getResource(
        "org/jactr/tools/test/count.xml");
    String[] sequence = new String[] { "start", "increment", "increment",
        "increment", "stop" };

    ExecutionTester tester = new ExecutionTester();
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Testing " + url);

    for (Throwable thrown : tester.test(url, "count", Arrays.asList(sequence),
        Arrays.asList(new String[] { "failed" })))
    {
      LOGGER.error(thrown);
      fail(thrown.getMessage());
    }
  }

  public void testAdditionRun() throws Exception
  {
    URL url = getClass().getClassLoader().getResource(
        "org/jactr/tools/test/addition.xml");
    String[] sequence = new String[] { "initialize-addition", "increment-sum",
        "increment-count", "increment-sum", "increment-count",
        "terminate-addition" };

    ExecutionTester tester = new ExecutionTester() {

      @Override
      public void verifyModelState(IModel model, IInstantiation instantiation)
      {
        if (LOGGER.isDebugEnabled())
        {
          IActivationBuffer buffer = model
              .getActivationBuffer(IActivationBuffer.GOAL);
          LOGGER.debug("Goal buffer contents");
          for (IChunk chunk : buffer.getSourceChunks())
            LOGGER.debug("\t" + StringUtilities.toString(chunk) + "\n");
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Variable Bindings : "
                + instantiation.getVariableBindings());
        }
      }
    };
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Testing " + url);

    for (Throwable thrown : tester.test(url, "addition", Arrays
        .asList(sequence), Collections.EMPTY_LIST))
    {
      LOGGER.error(thrown);
      fail(thrown.getMessage());
    }
  }

}
