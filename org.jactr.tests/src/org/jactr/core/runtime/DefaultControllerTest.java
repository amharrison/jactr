/*
 * Created on Jul 2, 2005
 * Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.runtime;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.io.CommonIO;
public class DefaultControllerTest extends TestCase
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
      .getLog(DefaultControllerTest.class);
  IController _controller;
  IModel _model;
  
  protected void setUp() throws Exception
  {
    super.setUp();
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    
    _controller = new DefaultController();
    runtime.setController(_controller);
    
    _model = CommonIO.constructorTest(CommonIO.compilerTest(CommonIO.parserTest("org/jactr/core/runtime/semantic-model.jactr",false,true),false,true));
    
   configureModel(_model);
    
    runtime.addModel(_model);
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
    ACTRRuntime.getRuntime().removeModel(_model);
    _model.dispose();
    _model = null;
    ACTRRuntime.getRuntime().setController(null);
  }
  
  protected IModel configureModel(IModel model)
  {
    org.jactr.core.logging.impl.DefaultModelLogger dml = new DefaultModelLogger();
    
    //dml.setParameter(Logger.CYCLE,"out");
    dml.setParameter(Logger.Stream.TIME.toString(), "err");
    dml.setParameter(Logger.Stream.OUTPUT.toString(), "err");
    //dml.setParameter(Logger.Stream.GOAL.toString(), "err");
    dml.setParameter(Logger.Stream.PROCEDURAL.toString(), "err");
    //dml.setParameter(Logger.CONFLICT_RESOLUTION, "out");
    //dml.setParameter(Logger.CONFLICT_SET, "out");
    //dml.setParameter(Logger.ACTIVATION_BUFFER,"out");
    //dml.setParameter(Logger.MATCHES,"out");
    //dml.setParameter(Logger.EXACT_MATCH,"out");
    //dml.setParameter(Logger.PARTIAL_MATCH,"out");
    
    model.install(dml);
    
    return model;
  }
  
  public void testSuspendAndResume() throws Exception
  {

    //set the goal
    _model.getActivationBuffer("goal").addSourceChunk(_model.getDeclarativeModule().getChunk("g1").get());
    
    //start this party and then immediately suspend it..
    LOGGER.info("Running model");
    _controller.start(true).get();
    LOGGER.info("Suspending runtime");
    _controller.suspend().get(); //this is redundant when true is passed above

    LOGGER.info("resuming from wait");
   assertTrue(_controller.isSuspended());
   
   LOGGER.info("resume");
   _controller.resume().get();
   LOGGER.info("waiting for resumption");
   assertFalse(_controller.isSuspended());
   
   LOGGER.info("wait for completion"); 
   _controller.complete().get();
   
    
  }

}


