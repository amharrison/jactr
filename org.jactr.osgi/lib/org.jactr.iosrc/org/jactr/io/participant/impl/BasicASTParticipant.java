/*
 * Created on Jan 10, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.participant.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.IInstallable;
import org.jactr.io.IOUtilities;
import org.jactr.io.parser.IParserImportDelegate;
import org.jactr.io.participant.IASTInjector;
import org.jactr.io.participant.IASTParticipant;
import org.jactr.io.participant.IASTTrimmer;

/**
 * basic astparticipant that will load a modelDescriptor from
 * getModelDescriptorURL() and take the contents and insert them into the
 * modelDescriptor during the call to install(CommonTree model) or cull the same
 * nodes during the call to shouldExclude() it will also take care of the
 * parameters if provided
 * 
 * @author developer
 */
public class BasicASTParticipant implements IASTParticipant
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(BasicASTParticipant.class);

  private URL                        _modelDescriptorURL;

  private Map<String, String>        _parameterMap;

  private Class< ? extends IInstallable>  _installableClass;

  public BasicASTParticipant(String location)
  {
    this(BasicASTParticipant.class.getClassLoader().getResource(location));
  }

  public BasicASTParticipant(URL modelDescriptor)
  {
    _modelDescriptorURL = modelDescriptor;
  }

  public BasicASTParticipant(URL modelDescriptor,
      Class< ? extends IInstallable> installableClass, Map<String, String> parameterMap)
  {
    this(modelDescriptor);
    _parameterMap = new TreeMap<String, String>(parameterMap);
    setInstallableClass(installableClass);
  }

  protected URL getURL()
  {
    return _modelDescriptorURL;
  }

  protected void setURL(URL url)
  {
    _modelDescriptorURL = url;
  }

  protected void setInstallableClass(Class< ? extends IInstallable> pClass)
  {
    _installableClass = pClass;
  }

  protected void setParameterMap(Map<String, String> pMap)
  {
    _parameterMap = new TreeMap<String, String>(pMap);
  }

  protected Map<String, String> getParameterMap()
  {
    return _parameterMap;
  }

  protected Class< ? extends IInstallable> getParticipantClass()
  {
    return _installableClass;
  }

  protected CommonTree load(URL importModel,
      IParserImportDelegate delegateForLoading) throws IOException
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Attempting to load from " + importModel);
    if (importModel == null) return null;
    CommonTree defaults = IOUtilities.loadModelFile(importModel,
        delegateForLoading,
        new ArrayList<Exception>(), new ArrayList<Exception>());
    return defaults;
  }

  public IASTInjector getInjector(IParserImportDelegate delegateForLoading)
  {
    CommonTree toBeInjected = null;
    try
    {
      toBeInjected = load(getURL(), delegateForLoading);
    }
    catch (IOException ioe)
    {
      LOGGER.error("Could not load content to be injected from " + getURL(),
          ioe);
    }

    return new BasicASTInjector(toBeInjected, getParticipantClass(),
        getParameterMap());
  }

  public IASTTrimmer getTrimmer(IParserImportDelegate delegateForLoading)
  {
    CommonTree toBeTrimmed = null;
    try
    {
      toBeTrimmed = load(getURL(), delegateForLoading);
    }
    catch (IOException ioe)
    {
      LOGGER
          .error("Could not load content to be trimmed from " + getURL(), ioe);
    }
    return new BasicASTTrimmer(toBeTrimmed);
  }

}
