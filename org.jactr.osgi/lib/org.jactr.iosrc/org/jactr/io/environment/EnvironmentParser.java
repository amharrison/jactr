/*
 * Created on May 10, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.environment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.parser.RealityParser;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.IConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.utils.IInstallable;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;
import org.jactr.io.IOUtilities;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * parses the environment.xml file to configure the runtime <code>
 *  <environment>
 * 
 *  <!-- iff you want to control reality from here -->
 *  <commonreality>
 *   ..
 *  </commonreality>
 * 
 * 
 *   <controller class="org.jactr.core.runtime.DefaultController"/>
 *   <onStart class="com.some.class.onStartRunnable"/>
 *   <onStop class="com.some.class.onStopRunnable"/>
 * 
 *   <connector class="org.jactr.core.reality.connector.CommonRealityConnector">
 <credentials>
 <credential value="visualTest:pass" alias="visualTest"/>
 </credentials>
 </connector>
 * 
 *   <models>
 *    <model url=".../bleck.jactr" alias="bob"/>
 *   </models>
 * 
 *   <attachments>
 *    <attachment class="com.some.class.implements.IInstallable" attach="bob">
 *     <parameters>
 *     </parameters>
 *    </attachment>
 *   </attachments>
 *  </environment>
 * </code>
 * 
 * @author developer
 */
public class EnvironmentParser
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(EnvironmentParser.class);

  public void parse(URL input) throws IOException, SAXException,
      ParserConfigurationException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document doc = parser.parse(input.openStream());
    process(doc, getModelDescriptors(doc, input));
  }

  /**
   * process the environment descriptor.
   * 
   * @param document
   * @param modelDescriptors
   */
  public void process(Document document, Collection<CommonTree> modelDescriptors)
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();

    /*
     * first things first.. if there are reality tags, proccess them
     */
    configureReality(document);

    instantiateConnector(document, runtime);

    instantiateController(document, runtime);

    instantiateOnStartStop(document, runtime);

    Collection<IModel> models = buildModels(modelDescriptors);

    // now let's actually add all the models
    for (IModel model : models)
      runtime.addModel(model);

    instantiateAttachments(document, models);
  }

  /**
   * build all the models
   * 
   * @param modelDescriptors
   * @return
   */
  protected Collection<IModel> buildModels(
      Collection<CommonTree> modelDescriptors)
  {
    Collection<IModel> models = new ArrayList<IModel>();
    for (CommonTree modelDescriptor : modelDescriptors)
    {
      Collection<Exception> warnings = new HashSet<Exception>();
      Collection<Exception> errors = new HashSet<Exception>();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Building " + ASTSupport.getName(modelDescriptor));

      IModel model = IOUtilities.constructModel(modelDescriptor, warnings,
          errors);

      if (errors.size() != 0)
        throw new RuntimeException("Failed to build "
            + ASTSupport.getName(modelDescriptor), errors.iterator().next());

      models.add(model);
    }
    return models;
  }

  /**
   * load the model descriptors based on the document contents and the root url
   * 
   * @param document
   * @return
   */
  public Collection<CommonTree> getModelDescriptors(Document document, URL root)
  {
    Collection<CommonTree> models = new ArrayList<CommonTree>();

    NodeList nl = document.getElementsByTagName("model");
    for (int i = 0; i < nl.getLength(); i++)
    {
      Element modelElement = (Element) nl.item(i);
      String modelName = modelElement.getAttribute("alias");
      String location = modelElement.getAttribute("url");

      URL modelLocation = resolveURLLocation(root, location);

      if (modelLocation != null)
      {
        Collection<Exception> warnings = new HashSet<Exception>();
        Collection<Exception> errors = new HashSet<Exception>();
        CommonTree modelDescriptor = null;
        try
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Loading " + modelLocation);
          modelDescriptor = IOUtilities.loadModelFile(modelLocation, warnings,
              errors);
        }
        catch (Exception e)
        {
          throw new RuntimeException("Failed to load " + modelLocation, e);
        }

        if (errors.size() != 0)
          throw new RuntimeException("Parsing error on " + modelLocation,
              errors.iterator().next());

        IOUtilities.compileModelDescriptor(modelDescriptor, warnings, errors);

        if (errors.size() != 0)
          throw new RuntimeException("Compilation error on " + modelLocation,
              errors.iterator().next());

        /*
         * set the model name
         */
        ASTSupport.getFirstDescendantWithType(modelDescriptor,
            JACTRBuilder.NAME).getToken().setText(modelName);

        models.add(modelDescriptor);
      }
      else
        throw new RuntimeException(
            String
                .format(
                    "Could not load model from %s. If this was working a second ago, check out %s.",
                    location, "http://jact-r.org/node/148"));
    }

    return models;
  }

  protected void configureReality(Document env)
  {
    NodeList nl = env.getElementsByTagName("commonreality");
    if (nl.getLength() == 1)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("configuring reality interface");
      // pass it on to the RealityParser
      RealityParser rp = new RealityParser();
      rp.parse((Element) nl.item(0));
    }
  }

  static public Object instantiate(Element element, String objectType)
  {
    String className = element.getAttribute("class");
    try
    {
      Object rtn = EnvironmentParser.class.getClassLoader()
          .loadClass(className).newInstance();

      if (rtn instanceof IParameterized)
      {
        NodeList nl = element.getElementsByTagName("parameters");
        if (nl.getLength() != 0)
          applyParameters((Element) nl.item(0), (IParameterized) rtn);
      }

      return rtn;
    }
    catch (Exception e)
    {
      String message = new String("Could not instantiate " + objectType
          + " from " + className);
      LOGGER.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  protected Collection<IInstallable> instantiateAttachments(Document env,
      Collection<IModel> models)
  {
    Collection<IInstallable> attachments = new ArrayList<IInstallable>();
    NodeList nl = env.getElementsByTagName("attachment");
    for (int i = 0; i < nl.getLength(); i++)
    {
      Element attachment = (Element) nl.item(i);
      String[] modelNames = attachment.getAttribute("attach").split(",");
      IInstallable installable = (IInstallable) instantiate(attachment,
          "installable");
      for (String modelName : modelNames)
      {
        modelName = modelName.trim();
        // attach to everyone
        if (modelName.equalsIgnoreCase("all"))
        {
          for (IModel model : models)
            if (installable instanceof IInstrument)
              model.install((IInstrument) installable);
            else
              installable.install(model);
        }
        else
          for (IModel model : models)
            if (model.getName().equals(modelName))
              if (installable instanceof IInstrument)
                model.install((IInstrument) installable);
              else
                installable.install(model);
      }
    }
    return attachments;
  }

  protected IConnector instantiateConnector(Document env, ACTRRuntime runtime)
  {
    IConnector connector = null;
    NodeList nl = env.getElementsByTagName("connector");
    if (nl.getLength() == 1)
    {
      Element contEl = (Element) nl.item(0);
      connector = (IConnector) instantiate(contEl, "connector");
      runtime.setConnector(connector);
    }

    return connector;
  }

  /**
   * handle the controller
   * 
   * @param env
   * @param runtime
   * @return
   */
  protected IController instantiateController(Document env, ACTRRuntime runtime)
  {
    IController controller = null;
    NodeList nl = env.getElementsByTagName("controller");
    if (nl.getLength() == 1)
    {
      Element contEl = (Element) nl.item(0);
      controller = (IController) instantiate(contEl, "controller");
      runtime.setController(controller);
    }

    if (controller == null)
    {
      controller = new DefaultController();
      runtime.setController(controller);
    }

    return controller;
  }

  static protected void applyParameters(Element parameters,
      IParameterized parameterized)
  {
    NodeList nl = parameters.getElementsByTagName("parameter");
    for (int i = 0; i < nl.getLength(); i++)
    {
      Element param = (Element) nl.item(i);
      String parameterName = param.getAttribute("name");
      String parameterValue = param.getAttribute("value");
      parameterized.setParameter(parameterName, parameterValue);
    }
  }

  /**
   * snag the onStart onStop classes and instantiate them
   * 
   * @param env
   * @param runtime
   */
  protected void instantiateOnStartStop(Document env, ACTRRuntime runtime)
  {
    NodeList nl = env.getElementsByTagName("onstart");
    if (nl.getLength() == 1)
      runtime
          .setOnStart((Runnable) instantiate((Element) nl.item(0), "onstart"));

    nl = env.getElementsByTagName("onstop");
    if (nl.getLength() == 1)
      runtime.setOnStop((Runnable) instantiate((Element) nl.item(0), "onstop"));
  }

  protected URL resolveURLLocation(URL enviromentLocation, String location)
  {
    URL url = null;

    try
    {
      url = new URL(location);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Loading from full url " + url);
    }
    catch (MalformedURLException e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(location + " is not a valid url, trying to resolve it");
    }

    if (url == null)
      try
      {
        url = enviromentLocation.toURI().resolve(location).toURL();
        File fp = new File(url.toURI());
        if (!fp.exists())
        {
          url = null;
          if (LOGGER.isDebugEnabled()) LOGGER.debug("No file found at " + url);
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("Loading from relative URL " + url);
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Could not resolve url from " + location, e);
      }

    if (url == null) url = getClass().getClassLoader().getResource(location);
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Loading " + url);

    return url;
  }
}
