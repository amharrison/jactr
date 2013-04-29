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
package org.jactr.modules.pm.aural;

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

public class AuralModuleTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log    LOGGER               = LogFactory
                                                      .getLog(AuralModuleTest.class);

  static public final String ENVIRONMENT_FILE     = "org/jactr/modules/pm/aural/environment.xml";

  ACTRRuntime                _runtime;

  DebugController            _controller;

  String[]                   _productionSequence  = {
      "search-for-sound-succeeded", "encoding-correct", "heard-tone",
      "search-for-sound-succeeded", "encoding-correct", "heard-digit",
      "search-for-sound-succeeded", "encoding-correct", "heard-word",
      "search-for-sound-succeeded", "encoding-correct", "heard-speech" };

  String[]                   _failedProductions   = { "encoding-failed",
      "encoding-incorrect-kind", "encoding-incorrect-content",
      "encoding-match-failed"                    };

  String[]                   _ignoreProductions   = {
      "search-for-sound-failed", "search-for-sound" };

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
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Model run has completed");
    assertEquals("Not all the productions have fired",
        _productionSequence.length, _productionFireCount);

    if (LOGGER.isDebugEnabled())
      for (StringBuilder sb : CommonIO.generateSource(model, "jactr"))
        LOGGER.debug(sb.toString());

    model.dispose();
  }

  protected void testProduction(IProduction production)
  {
    String pName = production.getSymbolicProduction().getName();

    for (String name : _ignoreProductions)
      if (name.equals(pName)) return;

    // fail any in the _failedProductions
    for (String name : _failedProductions)
      if (name.equals(pName))
        fail("production " + name +
            " should not have fired. should have been " +
            _productionSequence[_productionFireCount]);

    if (!_productionSequence[_productionFireCount].equals(pName))
      fail(pName + " was fired out of sequence, expecting " +
          _productionSequence[_productionFireCount]);
    _productionFireCount++;
  }
}
