/*
 * Created on Nov 21, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.module;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.core.model.IModel;
import org.jactr.io.CommonIO;

public abstract class ModuleTest extends TestCase
{

  private IModel _model;

  public ModuleTest()
  {
    super();
  }

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    _model = loadBootstrapModel();
  }

  @Override
  protected void tearDown() throws Exception
  {
    _model.dispose();
    super.tearDown();
  }



  
  protected IModel loadBootstrapModel() throws Exception
  {
    CommonTree md = CommonIO.parserTest(
        "org/jactr/core/module/bootstrap.jactr", true, true);
    assertNotNull(md);

    CommonIO.compilerTest(md, true, true);

    IModel model = CommonIO.constructorTest(md);
    assertNotNull(model);

    return model;
  }

  public IModel getModel()
  {
    return _model;
  }

}
