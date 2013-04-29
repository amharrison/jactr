/*
 * Created on Jun 24, 2005 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.runtime;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.slot.ISlot;
import org.jactr.io.CommonIO;
import org.jactr.io.resolver.ASTResolver;

public class ModelExecutionTest extends TestCase
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModelExecutionTest.class);

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  protected IModel load(String fileName)
  {
    LOGGER.info("Loading " + fileName);
    CommonTree md = CommonIO.parserTest(fileName, true, true);
    assertNotNull(md);

    LOGGER.info("Compiling " + fileName);
    CommonIO.compilerTest(md, true, true);

    // CommonIO.generateSource(md, "jactr");

    LOGGER.info("Constructing " + fileName);
    IModel model = CommonIO.constructorTest(md);
    assertNotNull(model);

    return model;
  }

  protected IModel configureModel(IModel model)
  {

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Attaching logger");
    org.jactr.core.logging.impl.DefaultModelLogger dml = new DefaultModelLogger();

    // dml.setParameter(Logger.CYCLE,"out");
    dml.setParameter(Logger.Stream.TIME.toString(), "out");
    dml.setParameter(Logger.Stream.OUTPUT.toString(), "out");
    // dml.setParameter(Logger.Stream.GOAL.toString(), "err");
    dml.setParameter(Logger.Stream.PROCEDURAL.toString(), "out");
    // dml.setParameter(Logger.CONFLICT_RESOLUTION, "out");
    // dml.setParameter(Logger.CONFLICT_SET, "out");
    // dml.setParameter(Logger.ACTIVATION_BUFFER,"out");
    // dml.setParameter(Logger.MATCHES,"out");
    // dml.setParameter(Logger.EXACT_MATCH,"out");
    // dml.setParameter(Logger.PARTIAL_MATCH,"out");

    model.install(dml);

    // dump the model..
    // for(StringBuilder sb :
    // CodeGeneratorFactory.getCodeGenerator("jactr").generate(ASTResolver.toAST(model,true)))
    // LOGGER.error(sb);
    
    //model.install(new BeanShellInterface());

    return model;
  }

  protected void testModel(IModel model, String goalName, String slotToCheck,
      IChunk slotValue) throws Exception
  {
    LOGGER
        .debug("======================================================================");
    LOGGER.debug("Testing model for goal " + goalName);

    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    IController controller = runtime.getController();

    IChunk goal = model.getDeclarativeModule().getChunk(goalName).get();
    Object value = goal.getSymbolicChunk().getSlot("object").getValue();
    LOGGER
        .debug("goal slot value " + value + ", " + value.getClass().getName());

    assertNotNull(goal);
    model.getActivationBuffer("goal").addSourceChunk(goal);
    
    model.setParameter(BasicModel.AGE_PARAM, "0");

    IChunk currentGoal = model.getActivationBuffer("goal").getSourceChunk();
    LOGGER.debug("current goal is " + currentGoal);

    assertNotNull(currentGoal);

    /*
     * this can not pass in 6.0 since chunks are actually copies
     */
    // assertTrue("Source chunk is not assigned", goal.equals(model
    // .getActivationBuffer("goal").getSourceChunk()));
    long startTime = System.currentTimeMillis();
    controller.start().get();
    //assertTrue(controller.isRunning());
    controller.complete().get();
    long runTime = System.currentTimeMillis() - startTime;

    LOGGER.info("Run took " + runTime + " ms");

    assertFalse(controller.isRunning());

    // goal should now have... g2.judgement = yes
    ISlot slot = currentGoal
        .getSymbolicChunk().getSlot(slotToCheck);
    LOGGER.debug("Comparing " + slot.getValue() + " to " + slotValue);

    try
    {
      assertTrue("Comparing " + slot.getValue() + " to " + slotValue, slot
          .equalValues(slotValue));
    }
    catch (AssertionFailedError afe)
    {
      LOGGER.error("Failed, dumping terminal model source (" +
          afe.getMessage() + ")");
      CommonTree md = ASTResolver.toAST(model, true);
      for (StringBuilder line : CommonIO.generateSource(md, "jactr"))
        System.err.println(line.toString());

      throw afe;
    }
  }

  private void testSemanticModel(String fileName, String slotToCheck)
      throws Exception
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
//    DefaultController controller = new DefaultController() {
//      @Override
//      protected Runnable createModelRunner(IModel model, ExecutorService service)
//      {
//        return new ProfilingModelRunner(service, model,
//            new DefaultCycleProcessor6());
//      }
//    };
    DefaultController controller = new DefaultController();
    
    runtime.setController(controller);

    IModel model = load(fileName);

    // CommonIO.generateSource(ASTResolver.toAST(model,true),"jactr");

    configureModel(model);

    runtime.addModel(model);

    IChunk yes = model.getDeclarativeModule().getChunk("yes").get();
    IChunk no = model.getDeclarativeModule().getChunk("no").get();
    assertNotNull(yes);
    assertNotNull(no);

    String[] goalNames = { "g1", "g2", "g3" };
    IChunk[] slotValues = { yes, yes, no };

    for (int i = 0; i < goalNames.length; i++)
    {
//      controller.reset();
      assertTrue(runtime.getModels().size() == 1);
      testModel(model, goalNames[i], slotToCheck, slotValues[i]);
    }
    runtime.removeModel(model);
    model.dispose();
    runtime.setController(null);
  }

//  public void testLispSemanticModel() throws Exception
//  {
//    testSemanticModel("org/jactr/core/runtime/semantic-lisp.lisp", "judgment");
//  }

  public void testJactrSemanticModel() throws Exception
  {
    testSemanticModel("org/jactr/core/runtime/semantic-model.jactr",
        "judgement");
  }

}
