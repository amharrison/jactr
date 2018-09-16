/*
 * Created on Jun 2, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.serialization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URL;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.misc.DetailedCommonTree;
import org.jactr.io.antlr3.misc.DetailedCommonTreeAdaptor;

public class Serializer
{
  /**
   * logger definition
   */
  static public final Log                  LOGGER   = LogFactory
                                                        .getLog(Serializer.class);

  static private DetailedCommonTreeAdaptor _adaptor = new DetailedCommonTreeAdaptor();

  static public void write(CommonTree tree, DataOutput output)
      throws IOException
  {
    output.writeInt(tree.getType());
    output.writeUTF(tree.getText());

    // we ignore the token

    CommonToken token = (CommonToken) tree.getToken();
    output.writeInt(token.getType());
    output.writeUTF(token.getText());
    output.writeInt(token.getStartIndex());
    output.writeInt(token.getStopIndex());
    output.writeInt(token.getLine());
    output.writeInt(token.getCharPositionInLine());

    output.writeInt(tree.getTokenStartIndex());
    output.writeInt(tree.getTokenStopIndex());

    int start = -1;
    int end = -1;
    String url = "";
    if (tree instanceof DetailedCommonTree)
    {
      start = ((DetailedCommonTree) tree).getStartOffset();
      end = ((DetailedCommonTree) tree).getStopOffset();
      if (((DetailedCommonTree) tree).getSource() != null)
        url = ((DetailedCommonTree) tree).getSource().toString();
    }

    output.writeInt(start);
    output.writeInt(end);
    output.writeUTF(url);

    // children
    output.writeInt(tree.getChildCount());
    for (int i = 0; i < tree.getChildCount(); i++)
      write((CommonTree) tree.getChild(i), output);
  }

  static public CommonTree read(DataInput input) throws IOException
  {
    int nodeType = input.readInt();
    String nodeText = input.readUTF();

    int tokenType = input.readInt();
    String tokenText = input.readUTF();
    int tokenStart = input.readInt();
    int tokenStop = input.readInt();
    int tokenLine = input.readInt();
    int tokenLineOffset = input.readInt();

    CommonToken token = new CommonToken(tokenType, tokenText);
    token.setStartIndex(tokenStart);
    token.setStopIndex(tokenStop);
    token.setLine(tokenLine);
    token.setCharPositionInLine(tokenLineOffset);

    DetailedCommonTree node = (DetailedCommonTree) _adaptor.create(nodeType,
        token, nodeText);
    node.setTokenStartIndex(input.readInt());
    node.setTokenStopIndex(input.readInt());

    node.setStartOffset(input.readInt());
    node.setEndOffset(input.readInt());

    String url = input.readUTF();
    if (url.length() != 0) try
    {
      node.setSource(new URL(url));
    }
    catch (Exception e)
    {

    }

    int children = input.readInt();
    for (int i = 0; i < children; i++)
      node.addChild(read(input));
    return node;
  }

}
