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
import java.util.HashSet;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.compiler.CompilationError;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.participant.ASTParticipantRegistry;
import org.jactr.io.participant.IASTInjector;
import org.jactr.io.participant.IASTParticipant;
import org.jactr.io.participant.impl.BasicASTInjector;
import org.jactr.io.participant.impl.BasicASTParticipant;

/**
 * @author developer
 */
public class DefaultParserImportDelegate implements IParserImportDelegate
{

  static private final transient Log LOGGER         = LogFactory
                                                        .getLog(DefaultParserImportDelegate.class);

  private Set<URL>                   _importSources = new HashSet<URL>();

  /**
   * @see org.jactr.io.parser.IParserImportDelegate#importInto(org.antlr.runtime.tree.CommonTree,
   *      java.lang.String)
   */
  public CommonTree importModuleInto(CommonTree modelDescriptor,
      String moduleClassName, boolean importContents) throws Exception
  {
    if (!isValidClassName(moduleClassName))
      throw new CompilationError("Could not find module class named "
          + moduleClassName, null);

    /*
     * first, find the reference or create it is it doesnt exist
     */

    CommonTree moduleNode = new ASTSupport().createModuleTree(moduleClassName);

    inject(moduleNode, modelDescriptor, importContents);

    return moduleNode;
  }

  public CommonTree importExtensionInto(CommonTree modelDescriptor,
      String extensionClassName, boolean importContents) throws Exception
  {
    if (!isValidClassName(extensionClassName))
      throw new CompilationError("Could not find extension class named "
          + extensionClassName, null);

    CommonTree extensionNode = new ASTSupport()
        .createExtensionTree(extensionClassName);

    inject(extensionNode, modelDescriptor, importContents);

    return extensionNode;
  }

  private void inject(CommonTree classBasedNode, CommonTree modelDescriptor,
      boolean importContents) throws Exception
  {
    String className = ASTSupport.getFirstDescendantWithType(classBasedNode,
        JACTRBuilder.CLASS_SPEC).getText();
    IASTParticipant participant = getASTParticipant(className);


    // short term fix until new antlr can be tested
    if (importContents)
    {
      IASTInjector injector = null;
      if (participant != null)
        injector = participant.getInjector(this);
      else
      // throw new CompilationWarning("Could not find IASTParticipant for "
      // + className, null);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Could not find IASTParticipant for " + className, null);

      if (injector != null)
        injector.inject(modelDescriptor, true);
      else
      // throw new CompilationWarning("Could not find IASTInjector for "
      // + className, null);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Could not find IASTInjector for " + className, null);

      if (injector instanceof BasicASTInjector)
        ((BasicASTInjector) injector).injectParameters(classBasedNode);
    }
  }

  /**
   * return the IASTParticipant installed for this classname, if any
   * 
   * @param moduleClassName
   * @return
   */
  protected IASTParticipant getASTParticipant(String moduleClassName)
  {
    return ASTParticipantRegistry.getParticipant(moduleClassName);
  }

  protected boolean isValidClassName(String moduleClassName)
  {
    try
    {
      getClass().getClassLoader().loadClass(moduleClassName);
    }
    catch (Exception e)
    {
      return false;
    }
    return true;
  }

  /**
   * @see org.jactr.io.parser.IParserImportDelegate#importInto(org.antlr.runtime.tree.CommonTree,
   *      java.net.URL)
   */
  public void importInto(CommonTree modelDescriptor, URL url,
      boolean importBuffers)
  {
    if (!_importSources.contains(url))
    {
      BasicASTParticipant bap = new BasicASTParticipant(url);
      bap.getInjector(this).inject(modelDescriptor, importBuffers);
      _importSources.add(url);
    }
  }

  /**
   * checks classpath, absolute url, or relative url to base
   * 
   * @param url
   * @param baseURL
   * @return
   * @see org.jactr.io.parser.IParserImportDelegate#resolveURL(java.lang.String,
   *      java.net.URL)
   */
  public URL resolveURL(String url, URL baseURL)
  {
    URL rtn = null;
    try
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Checking class path for " + url);
      rtn = getClass().getClassLoader().getResource(url);
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Could not find resource " + url, e);
    }

    if (rtn == null)
      try
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Trying to make url of " + url);
        rtn = new URL(url);
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(url + " is not a valid url ", e);
      }

    if (rtn == null)
      try
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Trying to resolve relative to " + baseURL);
        rtn = baseURL.toURI().resolve(url).toURL();
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Failed to resolve relative to " + baseURL, e);
      }

    return rtn;
  }

  public Set<URL> getImportSources()
  {
    return _importSources;
  }

  public void reset()
  {
    _importSources.clear();
  }

}
