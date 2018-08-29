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
package org.jactr.core.production.request;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.models.FluentSemantic;

import junit.framework.TestCase;

public class RequestTest extends TestCase
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(RequestTest.class);

  IModel                             _model;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();

    _model = new FluentSemantic().get();

  }

  @Override
  protected void tearDown() throws Exception
  {
    _model.dispose();
    _model = null;

    super.tearDown();
  }

  public void testRequestComparison() throws Exception
  {
    IChunk p1 = _model.getDeclarativeModule().getChunk("p1").get();
    IChunk p2 = _model.getDeclarativeModule().getChunk("p2").get();

    ChunkTypeRequest ctr1 = new ChunkTypeRequest(p1);
    ChunkTypeRequest ctr3 = new ChunkTypeRequest(p1);
    ChunkTypeRequest ctr2 = new ChunkTypeRequest(p2);

    assertTrue("p1 should be equal to p1", ctr1.equals(ctr3));
    assertFalse("p1 and p2 chunkTypeRequests should not be equal",
        ctr1.equals(ctr2));
  }




}
