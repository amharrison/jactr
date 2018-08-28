/*
 * Created on May 24, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3;

import org.antlr.runtime.tree.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;

public class Support
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(Support.class);

  static public String outputTree(Tree tree)
  {
    return outputTree(tree, 0);
  }

  static public String outputTree(Tree tree, int level)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < level; i++)
      sb.append(" ");
    int type = tree.getType();
    sb.append("<").append(JACTRBuilder.tokenNames[type]).append("(").append(
        type).append(")");
    sb.append("  text=\"").append(tree.toString()).append("\"");
    if (tree.getChildCount() == 0)
      sb.append("/>");
    else
    {
      sb.append(">");
      for (int i = 0; i < tree.getChildCount(); i++)
        sb.append("\n").append(outputTree(tree.getChild(i), level + 2));
      sb.append("\n");
      for (int i = 0; i < level; i++)
        sb.append(" ");
      sb.append("</").append(JACTRBuilder.tokenNames[type]).append("(").append(
          type).append(")").append("  text=\"").append(tree.toString()).append(
          "\">");
    }

    return sb.toString();
  }

}
