package org.jactr.io.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.io.parser.IModelParser;
import org.jactr.io.parser.ModelParserFactory;
import org.jactr.io.participant.ASTParticipantRegistry;
import org.jactr.io.participant.IASTParticipant;
import org.jactr.io.participant.impl.BasicASTParticipant;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptingManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(Activator.class);

  // The plug-in ID
  public static final String         PLUGIN_ID = "org.jactr.io";

  // The shared instance
  private static Activator           plugin;

  /**
   * The constructor
   */
  public Activator()
  {
    super();
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext context) throws Exception
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Starting " + PLUGIN_ID);
    super.start(context);

    installASTParticipants();
    installSyntaxProviders();
    installScriptProviders();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault()
  {
    return plugin;
  }
  

  @SuppressWarnings("unchecked")
  protected void installSyntaxProviders()
  {
    IExtension[] extensions = getExtensions("org.jactr.io.syntaxproviders");
    for (IExtension extension : extensions)
    {
      IConfigurationElement[] allExtensions = extension
          .getConfigurationElements();
      for (IConfigurationElement actualExtension : allExtensions)
        if (actualExtension.getName().equals("syntaxprovider"))
        {
          String fileExtension = actualExtension.getAttribute("extension");
          String parserClassName = actualExtension.getAttribute("parser");
          String generatorClassName = actualExtension.getAttribute("generator");

          /*
           * do them separately.. first the parser..
           */
          if (parserClassName != null && parserClassName.length() != 0)
            try
            {
              Class< ? extends IModelParser> parserClass = (Class< ? extends IModelParser>) getClass()
                  .getClassLoader().loadClass(parserClassName);
              ModelParserFactory.addParser(fileExtension, parserClass);
            }
            catch (Exception e)
            {
              String message = "Could not install parser " + parserClassName
                  + " for " + fileExtension;
              LOGGER.error(message, e);
              IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, message,
                  e);
              getLog().log(status);
            }

          /**
           * and now the code generator
           */
          if (generatorClassName != null && generatorClassName.length() != 0)
            try
            {
              ICodeGenerator generator = (ICodeGenerator) actualExtension
                  .createExecutableExtension("generator");
              CodeGeneratorFactory.addCodeGenerator(fileExtension, generator);
            }
            catch (Exception e)
            {
              String message = "Could not install code generator "
                  + generatorClassName + " for " + fileExtension;
              LOGGER.error(message, e);
              IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, message,
                  e);
              getLog().log(status);
            }

        }
    }
  }

  @SuppressWarnings("unchecked")
  protected void installScriptProviders()
  {
    IExtension[] extensions = getExtensions("org.jactr.scriptfactory");
    for (IExtension extension : extensions)
    {
      IConfigurationElement[] allExtensions = extension
          .getConfigurationElements();
      for (IConfigurationElement actualExtension : allExtensions)
        if (actualExtension.getName().equals("factory"))
        {
          String factoryClassName = actualExtension.getAttribute("class");

          /*
           * do them separately.. first the parser..
           */
          if (factoryClassName != null && factoryClassName.length() != 0)
            try
            {
              Class<? extends IScriptableFactory> factoryClass = (Class<? extends IScriptableFactory>) getClass()
                  .getClassLoader().loadClass(factoryClassName);
              ScriptingManager.install(factoryClass.newInstance());
              // ModelParserFactory.addParser(fileExtension, parserClass);
            }
            catch (Exception e)
            {
              String message = "Could not install scripting factory "
                  + factoryClassName;
              LOGGER.error(message, e);
              IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, message,
                  e);
              getLog().log(status);
            }
        }
    }
  }

  protected void installASTParticipants()
  {
    IExtension[] extensions = getExtensions("org.jactr.io.astparticipants");

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Got " + extensions.length
          + " extensions to astparticipants");

    for (IExtension extension : extensions)
    {
      IConfigurationElement[] allExtensions = extension
          .getConfigurationElements();
      for (IConfigurationElement actualExtension : allExtensions)
        if (actualExtension.getName().equals("astparticipant"))
        {
          String contributingClassName = actualExtension.getAttribute("contributingClass");
          String participantClassName = actualExtension
              .getAttribute("class");
          String contentLocation = actualExtension.getAttribute("content");
          IASTParticipant participant = null;

          if (participantClassName != null
              && participantClassName.length() != 0)
            try
            {
              /*
               * this may be the wrong class loader.. I should delegate the
               * creation of this guy over to jactr.io since ppl will be likely
               * to register it as a buddy.. but then jactr.io would have to
               * have eclipse code.. grrr..
               */
              participant = (IASTParticipant) actualExtension
                  .createExecutableExtension("class");
            }
            catch (Exception e)
            {
              String message = "Could not install participant "
                  + participantClassName + " for module " + contributingClassName
                  + " will try content " + contentLocation;
              LOGGER.warn(message, e);
              IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, message,
                  e);
              getLog().log(status);
            }

          /*
           * didn't make the participant above, fall back to BasicASTParticipant
           * with the url to content location
           */
          if (participant == null && contentLocation != null
              && contentLocation.length() != 0)
            try
            {
              participant = new BasicASTParticipant(getClass().getClassLoader()
                  .getResource(contentLocation));
            }
            catch (Exception e)
            {
              String message = "Could not install basic participant for module "
                  + contributingClassName + " with content from " + contentLocation;
              LOGGER.warn(message, e);
              IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, 0,
                  message, e);
              getLog().log(status);
            }

          /*
           * install it if we've got it
           */
          if (participant != null)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Installing " + participant + " for "
                  + contributingClassName);

            ASTParticipantRegistry.addParticipant(contributingClassName, participant);
          }
          else
          {
            String message = "Could not install participant "
                + participantClassName + ":" + contentLocation + " for module "
                + contributingClassName;
            LOGGER.error(message);
            IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, message,
                null);
            getLog().log(status);
          }
        }
        else if (LOGGER.isWarnEnabled())
          LOGGER.warn("No clue what to do with configuration element "
              + actualExtension + " from "
              + actualExtension.getContributor().getName());
    }
  }

  static public IExtension[] getExtensions(String extensionPoint)
  {
    try
    {
      IExtensionRegistry extReg = Platform.getExtensionRegistry();
      IExtensionPoint iep = extReg.getExtensionPoint(extensionPoint);
      IExtension[] extensions = iep.getExtensions();
      return extensions;
    }
    catch (Exception e)
    {
      String message = "Could not retrieve extensions of " + extensionPoint;
      LOGGER.error(message, e);
      IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, message, e);
      getDefault().getLog().log(status);
      return new IExtension[0];
    }
  }
}
