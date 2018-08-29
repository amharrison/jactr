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
package org.jactr.core.models;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.reality.connector.LocalConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.utils.StringUtilities;
import org.jactr.fluent.FluentChunk;
import org.jactr.test.ExecutionTester;

import junit.framework.TestCase;

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

  protected void run(IModel model) throws Exception
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    runtime.setConnector(new LocalConnector());
    runtime.setController(new DefaultController());

    runtime.addModel(model);

    IController controller = runtime.getController();

    controller.start().get();
    controller.waitForCompletion().get();

    runtime.removeModel(model);
  }

  protected void cleanup(ExecutionTester tester, IModel model, boolean dispose)
  {
    model.uninstall(tester);
    if (dispose) model.dispose();
  }

  protected ExecutionTester setup(IModel model, String[] validSequence,
      boolean fullLogging)
  {
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
            LOGGER.debug("Instantiation : " + instantiation.getProduction()
                .getSymbolicProduction().getName());
        }
      }
    };

    tester.setProductionSequence(Arrays.asList(validSequence));
    model.install(tester);

    if (fullLogging)
    {
      DefaultModelLogger dml = new DefaultModelLogger();
      dml.setParameter("all", "err");
      model.install(dml);
    }

    return tester;
  }

  public void testSemanticRun() throws Throwable
  {
    IModel model = new FluentSemantic().get();
    String[] sequence = new String[] { "initial-retrieval", "chain-category",
        "chain-category", "fail" };

    LOGGER.debug("G3");
    ExecutionTester tester = setup(model, sequence, false);
    run(model);
    cleanup(tester, model, false);

    for (Throwable thrown : tester.getExceptions())
      throw thrown;

    LOGGER.debug("G1");

    /*
     * by default it uses example g3, let's test the other goals..
     */
    IChunkType isMember = model.getDeclarativeModule().getChunkType("is-member")
        .get();
    IChunk canary = model.getDeclarativeModule().getChunk("canary").get();
    IChunk bird = model.getDeclarativeModule().getChunk("bird").get();
    IChunk animal = model.getDeclarativeModule().getChunk("animal").get();

    IChunk g1 = FluentChunk.from(isMember).slot("object", canary)
        .slot("category", bird).build();

    sequence = new String[] { "initial-retrieval", "direct-verify" };
    model.getActivationBuffer("goal").addSourceChunk(g1);

    tester = setup(model, sequence, false);
    run(model);
    cleanup(tester, model, false);

    for (Throwable thrown : tester.getExceptions())
      throw thrown;

    /**
     * g2
     */
    LOGGER.debug("G2");
    IChunk g2 = FluentChunk.from(isMember).slot("object", canary)
        .slot("category", animal).build();
    sequence = new String[] { "initial-retrieval", "chain-category",
        "direct-verify" };
    model.getActivationBuffer("goal").addSourceChunk(g2);

    tester = setup(model, sequence, false);
    run(model);
    cleanup(tester, model, true);

    for (Throwable thrown : tester.getExceptions())
      throw thrown;
  }


  public void testCountRun() throws Throwable
  {
    IModel model = new FluentCount().get();

    String[] sequence = new String[] { "start", "increment", "increment",
        "increment", "stop" };

    ExecutionTester tester = setup(model, sequence, false);
    run(model);
    cleanup(tester, model, true);

    for (Throwable thrown : tester.getExceptions())
      throw thrown;
  }

  public void testAdditionRun() throws Throwable
  {
    IModel model = new FluentAddition().get();
    String[] sequence = new String[] { "initialize-addition", "increment-sum",
        "increment-count", "increment-sum", "increment-count",
        "terminate-addition" };

    ExecutionTester tester = setup(model, sequence, false);
    run(model);
    cleanup(tester, model, true);

    for (Throwable thrown : tester.getExceptions())
      throw thrown;

  }

}
