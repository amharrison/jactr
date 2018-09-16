/*
 * Created on Jan 19, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.participant;

import org.antlr.runtime.tree.CommonTree;

/**
 * used to specify whether or not a specific CommonTree element should be output
 * by a code generator.<br>
 * <br>
 * The name is a bit of a misnomer and should probably be changed. The
 * CommonTree describing the model does not change.
 * 
 * @author developer
 */
public interface IASTTrimmer
{
  /**
   * during the walking of the CommonTree describing the model, this will be
   * called for each visited node. If it returns true, the code for the element
   * will not be generated.<br>
   * <br>
   * in the case of nodes describing chunktypes, the chunk children will still
   * be visited.
   * 
   * @param element
   * @return
   */
  public boolean shouldIgnore(CommonTree element);
}
