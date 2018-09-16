/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.protocol.IProtocolConfiguration;
import org.commonreality.net.transport.ITransportProvider;
import org.commonreality.netty.protocol.NOOPProtocol;
import org.commonreality.netty.protocol.SerializingProtocol;
import org.commonreality.netty.service.ClientService;
import org.commonreality.netty.service.ServerService;
import org.commonreality.netty.transport.LocalTransportProvider;
import org.commonreality.netty.transport.NIOTransportProvider;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.reality.connector.LocalConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.BreakpointType;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.tools.async.controller.RemoteInterface;
import org.jactr.tools.async.shadow.ShadowController;

import junit.framework.TestCase;

/**
 * general MINA remote controller test. the ShadowController is the shadow copy
 * of the actual NetworkedIODebugController. They can both run as either servers
 * or clients.
 * 
 * @author developer
 */
public class ShadowControllerTest extends TestCase
{
  /**
   * logger definition
   */
  static private final Log   LOGGER         = LogFactory.getLog(ShadowControllerTest.class);


  protected IModel           _model;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();

    _model = new FluentSemantic().get();
    configureModel(_model);
    configureRuntime(_model);
  }

  protected void configureRuntime(IModel model) throws Exception
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    runtime.setConnector(new LocalConnector());
    runtime.setController(new DebugController());
    runtime.addModel(model);
  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    runtime.setController(null);
    runtime.removeModel(_model);
    _model.dispose();
  }



  protected void configureModel(IModel model)
  {
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
  }


  protected void connectAndTest(ITransportProvider transport,
      IProtocolConfiguration protocol, String addressInfo,
      String credentials, boolean runtimeIsServer,
      ExecutorService shadowExecutor, ExecutorService runtimeExecutor)
      throws Exception
  {
    RemoteInterface controller = new RemoteInterface();
    controller.setTransportProvider(transport);
    controller.setProtocol(protocol);
    controller.setAddressInfo(addressInfo);
    controller.setCredentialInformation(credentials);
    // controller.setIOExecutorService(runtimeExecutor);
    controller.setSendOnSuspend(true);

    /*
     * the remote controller that just reflects the real one that it
     * communicates with
     */
    ShadowController remote = new ShadowController();
    remote.setTransportProvider(transport);
    remote.setProtocol(protocol);
    remote.setAddressInfo(addressInfo);
    remote.setCredentialInformation(credentials);
    // remote.setIOExecutorService(shadowExecutor);

    // addHandlers(remote.getHandler());

    if (runtimeIsServer)
    {
      remote.setService(new ClientService());
      controller.setService(new ServerService());
    }
    else
    {
      remote.setService(new ServerService());
      controller.setService(new ClientService());
    }

    Exception delayedException = null;
    try
    {
      /*
       * the server must be running first
       */
      if (runtimeIsServer)
      {
        _model.install(controller);
        remote.attach();
      }
      else
      {
        remote.attach();
        _model.install(controller);
      }

      generalTest(remote);
    }
    catch (Exception e)
    {
      LOGGER.error("Exception has been caught, delaying until after cleanup ",
          e);
      delayedException = e;
    }
    finally
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Cleaning up - detaching");

      /*
       * disconnect
       */
      remote.detach(false);
      
      controller.disconnectSafe(false);

      /*
       * would normally block until all connections are closed
       */
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Uninstalling");
      _model.uninstall(controller);

      /*
       * we should sleep for a bit just to make sure we we've unbound
       */
      Thread.sleep(1000);
    }

    if (delayedException != null) throw delayedException;
  }

  protected void generalTest(ShadowController remote) throws Exception
  {
    remote.waitForConnection(0);
    assertTrue(remote.isConnected());
    assertFalse(remote.isRunning());
    assertFalse(remote.isSuspended());
    breakpointTest(remote);
    // runToCompletionTest(remote);
    assertFalse(remote.isRunning());
    assertFalse(remote.isSuspended());
  }

  protected void runToCompletionTest(ShadowController controller)
      throws Exception
  {
    controller.start();
    controller.waitForStart();
    assertTrue(controller.isRunning());
    while (controller.isRunning())
      controller.waitForCompletion(100);
    assertFalse(controller.isRunning());
  }

  protected void breakpointTest(ShadowController controller) throws Exception
  {
    
    /*
     * add break points for all productions
     */
    for (IProduction production : _model.getProceduralModule().getProductions()
        .get())
      controller.addBreakpoint(BreakpointType.PRODUCTION, _model.getName(),
          production.getSymbolicProduction().getName());

    controller.start();
    controller.waitForStart();

    assertTrue(controller.isRunning());

    String[] productionSequence = { "initial-retrieval", "chain-category",
        "chain-category", "fail" };

    int i = 0;
    for (String brokeProduction : productionSequence)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting for breakpoint");
      assertTrue(controller.waitForSuspension());

      CommonTree ast = controller.getBreakpointData(_model.getName());
      assertNotNull(ast);

      String actualName = ASTSupport.getName(ast);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Expecting " + brokeProduction + " got " + actualName);

      assertEquals("production " + i + " fired out of sequence", actualName,
          brokeProduction);
      i++;

      if (LOGGER.isDebugEnabled()) LOGGER.debug("Requesting resumption");
      controller.resume();
      if (LOGGER.isDebugEnabled()) LOGGER.debug("waiting for resumption");
      boolean resumed = controller.waitForResumption();
      if (!resumed)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug("*** runtime state has changed and we have missed some events, suspended:" +
                  controller.isSuspended());
      }
      else if (LOGGER.isDebugEnabled()) LOGGER.debug("runtime has resumed");
    }

    assertEquals("Not all productions fired", i, productionSequence.length);

    if (controller.isConnected()) controller.waitForCompletion();
    assertFalse(controller.isRunning());
  }

  public void testLocalRuntimeServer() throws Exception
  {
    ExecutorService shadow = Executors.newCachedThreadPool();
    ExecutorService runtime = Executors.newCachedThreadPool();
    connectAndTest(new LocalTransportProvider(), new NOOPProtocol(), "6969",
        "user:password", true, shadow, runtime);
    shadow.shutdown();
    runtime.shutdown();
  }

  public void testLocalControllerServer() throws Exception
  {
    ExecutorService shadow = Executors.newCachedThreadPool();
    ExecutorService runtime = Executors.newCachedThreadPool();
    connectAndTest(new LocalTransportProvider(), new NOOPProtocol(), "6969",
        "user:password", false, shadow, runtime);
    shadow.shutdown();
    runtime.shutdown();
  }

  public void testNIORuntimeServer() throws Exception
  {
    ExecutorService shadow = Executors.newCachedThreadPool();
    ExecutorService runtime = Executors.newCachedThreadPool();
    connectAndTest(new NIOTransportProvider(), new SerializingProtocol(),
        "localhost:6969", "user:password", true, shadow, runtime);
    shadow.shutdown();
    runtime.shutdown();
  }

  public void testNIOControllerServer() throws Exception
  {
    ExecutorService shadow = Executors.newCachedThreadPool();
    ExecutorService runtime = Executors.newCachedThreadPool();
    connectAndTest(new NIOTransportProvider(), new SerializingProtocol(),
        "localhost:6970", "user:password", false, shadow, runtime);
    shadow.shutdown();
    runtime.shutdown();
  }
}
