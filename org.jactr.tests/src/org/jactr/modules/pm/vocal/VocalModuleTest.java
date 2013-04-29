/*
 * Created on Jul 7, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.modules.pm.vocal;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.jactr.io.CommonIO;
import org.jactr.entry.Main;

public class VocalModuleTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log    LOGGER               = LogFactory
                                                      .getLog(VocalModuleTest.class);

  static public final String ENVIRONMENT_FILE     = "org/jactr/modules/pm/vocal/environment.xml";

  ACTRRuntime                _runtime;

  DebugController            _controller;

  String[]                   _productionSequence  = { "search-kind",
      "search-succeeded", "encoding-succeeded", "search-less-than",
      "search-succeeded", "encoding-succeeded", "search-greater-than",
      "search-succeeded", "encoding-succeeded", "search-color",
      "search-succeeded", "encoding-succeeded", "search-size",
      "search-succeeded", "encoding-succeeded", "search-size-succeeded" };

  String[]                   _failedProductions   = { "search-failed",
      "search-match-failed", "encoding-failed", "encoding-match-failed" };

  int                        _productionFireCount = 0;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    _runtime = (new Main()).createRuntime(getClass().getClassLoader()
        .getResource(ENVIRONMENT_FILE));
    _controller = (DebugController) _runtime.getController();
  }

  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  public void test() throws Exception
  {
    assertEquals(1, _runtime.getModels().size());
    IModel model = _runtime.getModels().iterator().next();
    assertNotNull(model);

    // for(StringBuilder line : CommonIO.generateSource(model, "jactr"))
    // System.err.println(line);

    // will be null until we start
    assertNull(_runtime.getConnector().getAgent(model));

    /*
     * we need to run this model and track all the visual-locations that get
     * inserted into the visual-location buffer
     */

    model.getProceduralModule().addListener(
        new ProceduralModuleListenerAdaptor() {

          @Override
          public void conflictSetAssembled(ProceduralModuleEvent pme)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("conflict set : " + pme.getProductions());
          }

          @Override
          public void productionWillFire(ProceduralModuleEvent pme)
          {
            IProduction production = pme.getProduction();
            LOGGER.debug(production + " is about to run, checking");
            testProduction(production);

          }
        }, ExecutorServices.INLINE_EXECUTOR);

    _controller.start().get();

    if (LOGGER.isDebugEnabled()) LOGGER.debug("model run has started");

    _controller.complete().get();

    try
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Model run has completed");
      assertEquals("Not all the productions have fired",
          _productionSequence.length, _productionFireCount);
    }
    finally
    {
      if (LOGGER.isDebugEnabled())
        for (StringBuilder sb : CommonIO.generateSource(model, "jactr"))
          LOGGER.debug(sb.toString());
    }
  }

  protected void testProduction(IProduction production)
  {
    String pName = production.getSymbolicProduction().getName();
    // fail any in the _failedProductions
    for (String name : _failedProductions)
      if (name.equals(pName))
        fail("production " + name + " should not have fired. should have been "
            + _productionSequence[_productionFireCount]);

    if (!_productionSequence[_productionFireCount].equals(pName))
      fail(pName + " was fired out of sequence, expecting "
          + _productionSequence[_productionFireCount]);
    _productionFireCount++;
  }
}
