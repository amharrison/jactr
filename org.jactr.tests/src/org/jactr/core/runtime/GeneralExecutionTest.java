/*
 * Created on Mar 20, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.io.CommonIO;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.resolver.ASTResolver;

/**
 * @author developer
 */
public class GeneralExecutionTest extends TestCase
{
  /**
   * logger definition
   */
  static private final Log LOGGER       = LogFactory
                                            .getLog(GeneralExecutionTest.class);

  private boolean          _dumpOnStart = true;

  private boolean          _dumpOnStop  = true;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    ACTRRuntime.getRuntime().setController(new DefaultController());
  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    ACTRRuntime.getRuntime().setController(null);
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
    // org.jactr.core.logging.impl.DefaultModelLogger dml = new
    // DefaultModelLogger();
    // dml.setParameter("all", "err");
    // model.install(dml);
    return model;
  }

  protected void dump(IModel model)
  {
    CommonTree modelDesc = ASTResolver.toAST(model, true);
    for (StringBuilder line : CodeGeneratorFactory.getCodeGenerator("jactr")
        .generate(modelDesc, true))
      LOGGER.debug(line.toString());
  }

  protected void execute(IModel... models) throws Exception
  {
    if (_dumpOnStart)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Initial model state");
      for (IModel model : models)
        dump(model);
    }
    for (IModel model : models)
      ACTRRuntime.getRuntime().addModel(model);

    IController controller = ACTRRuntime.getRuntime().getController();

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Running");
    controller.start().get();

    controller.complete().get();
    assertFalse(controller.isRunning());

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Terminated");
    for (IModel model : models)
      assertTrue(controller.getTerminatedModels().contains(model));

    for (IModel model : models)
    {
      ACTRRuntime.getRuntime().removeModel(model);
      if (_dumpOnStop) dump(model);
      model.dispose();
    }

  }

  protected void test(String... modelLocations) throws Exception
  {
    List<IModel> models = new ArrayList<IModel>();
    for (String modelLocation : modelLocations)
    {
      IModel model = load(modelLocation);
      configureModel(model);
      models.add(model);
    }
    execute(models.toArray(new IModel[models.size()]));
  }

  // public void testLispSemantic() throws Exception
  // {
  // test("org/jactr/core/runtime/semantic-lisp.lisp");
  // }

  public void testBasicSemantic() throws Exception
  {
    test("org/jactr/core/runtime/semantic-model.jactr");
  }

  public void testFullSemantic() throws Exception
  {
    test("org/jactr/core/models/semantic-full.jactr");
  }

  public void testAddition() throws Exception
  {
    test("org/jactr/core/models/addition.jactr");
  }

  public void testCount() throws Exception
  {
    test("org/jactr/core/models/count.jactr");
  }

  public void testMultiple() throws Exception
  {
    test("org/jactr/core/models/count.jactr",
        "org/jactr/core/models/addition.jactr");
  }

}
