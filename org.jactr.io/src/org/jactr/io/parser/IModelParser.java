/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
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
package org.jactr.io.parser;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IModelParser
{

  public URL getBaseURL();

  /**
   * called by the ModelParserFactory
   * 
   * @param url
   */
  public void setInput(URL url) throws IOException;

  /**
   * this parser will handle pure string input
   * 
   * @param content
   * @throws IOException
   */
  public void setInput(String content) throws IOException;

  /**
   * parse the url..
   * 
   * @return true if the parse was completely succcessful (no errors)
   */
  public boolean parse();

  /**
   * get all the lexing/parsing exceptions
   * 
   * @return
   */
  public Collection<Exception> getParseErrors();

  public Collection<Exception> getParseWarnings();

  /**
   * return the common AST document tree. this can only be called after parse()
   * 
   * @return
   */
  public CommonTree getDocumentTree();

  public IParserImportDelegate getImportDelegate();

  public void setImportDelegate(IParserImportDelegate delegate);

  public void delegate(CommonTree node);

  public void addTreeTracker(ITreeTracker tracker);

  public void removeTreeTracker(ITreeTracker tracker);
  
  public void reset();

  public void dispose();
}