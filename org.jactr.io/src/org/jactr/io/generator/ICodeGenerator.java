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
 * Created on May 20, 2005 by developer
 */

package org.jactr.io.generator;

import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;

/**
 * interface for code that takes an AST model description and produces valid
 * source code in some language. implementors must be completely thread safe -
 * no internal variables
 * 
 * @author developer
 */
public interface ICodeGenerator
{
  /**
   * generate code for the following types: MODEL, BUFFER, EXTENSION, MODULE
   * PRODUCTION, CHUNK_TYPE, CHUNK, SLOTS, PARAMETER, CHECK_CONDITION
   * 
   * @param root
   * @param shouldTrim
   *          assumes model
   * @return
   */
  public Collection<StringBuilder> generate(CommonTree root, boolean shouldTrim);

  public Collection<StringBuilder> generate(CommonTree root,
      boolean shouldTrim, Collection<Exception> warnings,
      Collection<Exception> errors);
  
  public Collection<StringBuilder> comment(String message);
}
