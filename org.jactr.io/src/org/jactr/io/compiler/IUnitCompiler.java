/*
 * Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * Created on Apr 25, 2005 by developer
 */

package org.jactr.io.compiler;

import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;

/**
 * Basic unit compiler interface. As the compiler walks the AST, it checks its
 * installed {@link IUnitCompiler}s for any that are interested in the current
 * node type (via {@link #getRelevantTypes()}) and routes that AST segment to
 * the unit compiler.</br> </br> Unit compilers need not be thread safe as they
 * are instaniated for each compiler, which should only be used by a single
 * thread.
 */
public interface IUnitCompiler
{

  /**
   * return a collection of JACTRBuilder. types that this unit compiler is
   * interested in
   * 
   * @return
   */
  public Collection<Integer> getRelevantTypes();

  /**
   * @param node
   * @param warnings
   * @param errors
   * @return
   */
  public void compile(CommonTree node, Collection<Exception> info,
      Collection<Exception> warnings, Collection<Exception> errors);

  public void preCompile();

  public void postCompile();
}
