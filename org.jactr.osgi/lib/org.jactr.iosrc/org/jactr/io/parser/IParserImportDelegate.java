/*
 * Created on Mar 19, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.parser;

import java.net.URL;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;

/**
 * handles the importing of content based on either a url or a module classname
 * 
 * @author developer
 */
public interface IParserImportDelegate
{

  /**
   * @param modelDescriptor
   * @param moduleClassName
   * @param importContents
   * @return TODO
   * @throws Exception
   */
  public CommonTree importModuleInto(CommonTree modelDescriptor, String moduleClassName,
      boolean importContents) throws Exception;
  

  public CommonTree importExtensionInto(CommonTree modelDescriptor, String extensionClassName,
      boolean importContents) throws Exception;
  
  public void importInto(CommonTree modelDescriptor, URL url, boolean importBuffers) throws Exception;
  
  public URL resolveURL(String url, URL baseURL);

  public Set<URL> getImportSources();

  public void reset();
}
