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
 * Created on May 19, 2005 by developer
 */

package org.jactr.io;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.compiler.CompilationWarning;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.antlr3.misc.CommonTreeException;
import org.jactr.io.compiler.DefaultCompiler;
import org.jactr.io.parser.IModelParser;
import org.jactr.io.parser.IParserImportDelegate;
import org.jactr.io.parser.ModelParserFactory;
import org.jactr.io.parser.ParserImportDelegateFactory;

/**
 * convenience utilities for the lazy
 * 
 * @author developer
 */
public class IOUtilities
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
      .getLog(IOUtilities.class);

  static public Supplier<IModel> loadModel(URL modelLocation,
      Collection<Exception> warnings, Collection<Exception> errors)
  {
    Supplier<IModel> rtn = new Supplier<IModel>() {

      @Override
      public IModel get()
      {
        try
        {
          CommonTree ast = loadModelFile(modelLocation, warnings, errors);
          if (compileModelDescriptor(ast, warnings, errors))
            return constructModel(ast, warnings, errors);
          else
            throw new RuntimeException(
                "Compilation errors for " + modelLocation);
        }
        catch (Exception e)
        {
          throw new RuntimeException(e);
        }
      }
    };

    return rtn;
  }

  static public CommonTree createModelDescriptor(String modelName)
  {
    return createModelDescriptor(modelName, false);
  }

  static public CommonTree createModelDescriptor(String modelName,
      boolean installModules)
  {
    ASTSupport support = new ASTSupport();
    CommonTree modelDesc = support.createModelTree(modelName);

    // add default modules
    if (installModules) try
    {
      IParserImportDelegate delegate = ParserImportDelegateFactory
          .createDelegate((Object[]) null);
      CommonTree modulesRoot = ASTSupport.getFirstDescendantWithType(modelDesc,
          JACTRBuilder.MODULES);
      modulesRoot.addChild(delegate.importModuleInto(modelDesc,
          org.jactr.core.module.declarative.six.DefaultDeclarativeModule6.class
              .getName(),
          true));
      modulesRoot.addChild(delegate.importModuleInto(modelDesc,
          org.jactr.core.module.procedural.six.DefaultProceduralModule6.class
              .getName(),
          true));
      modulesRoot.addChild(delegate.importModuleInto(modelDesc,
          org.jactr.core.module.goal.six.DefaultGoalModule6.class.getName(),
          true));
      modulesRoot.addChild(delegate.importModuleInto(modelDesc,
          org.jactr.core.module.retrieval.six.DefaultRetrievalModule6.class
              .getName(),
          true));
    }
    catch (Exception e)
    {

    }
    return modelDesc;
  }

  static public CommonTree loadModelFile(String modelFile,
      Collection<Exception> warnings, Collection<Exception> errors)
      throws IOException
  {
    URL url = IOUtilities.class.getClassLoader().getResource(modelFile);
    return loadModelFile(url, warnings, errors);
  }

  static public CommonTree loadModelFile(URL modelFileLocation,
      Collection<Exception> warnings, Collection<Exception> errors)
      throws IOException
  {
    return loadModelFile(modelFileLocation, null, warnings, errors);
  }

  /**
   * load the specified model file and store any and all warnings and exceptions
   * 
   * @param modelFileLocation
   * @param warnings
   * @param errors
   * @return null if a critical error occured
   */
  static public CommonTree loadModelFile(URL modelFileLocation,
      IParserImportDelegate delegate, Collection<Exception> warnings,
      Collection<Exception> errors) throws IOException
  {
    CommonTree modelDescriptor = null;
    IModelParser parser = null;
    try
    {
      parser = ModelParserFactory.getModelParser(modelFileLocation);
      if (parser != null)
      {
        if (delegate != null) parser.setImportDelegate(delegate);

        parser.parse();
        modelDescriptor = parser.getDocumentTree();
      }
      else
        errors.add(new RuntimeException(
            "Could not find installed parser for " + modelFileLocation));
    }
    catch (CommonTreeException cte)
    {
      if (cte instanceof CompilationWarning)
        warnings.add(cte);
      else
        errors.add(cte);
    }
    catch (IOException e)
    {
      LOGGER.error("Failed to load model file :" + modelFileLocation, e);
    }
    finally
    {
      if (parser != null)
      {
        warnings.addAll(parser.getParseWarnings());
        errors.addAll(parser.getParseErrors());
        parser.dispose();
      }
    }
    return modelDescriptor;
  }

  /**
   * attempt to compile the model
   * 
   * @param modelDescriptor
   * @param warnings
   * @param errors
   * @return true if there are no compilation errors
   */
  static public boolean compileModelDescriptor(CommonTree modelDescriptor,
      Collection<Exception> warnings, Collection<Exception> errors)
  {
    Collection<Exception> errs = new ArrayList<Exception>();
    Collection<Exception> warn = new ArrayList<Exception>();
    Collection<Exception> info = new ArrayList<Exception>();
    DefaultCompiler compiler = new DefaultCompiler();
    compiler.compile(modelDescriptor, info, warn, errs);
    warnings.addAll(warn);
    errors.addAll(errs);
    return errs.size() == 0;
  }

  /**
   * construct the described model
   * 
   * @param modelDescriptor
   * @return
   * @throws BuilderException
   */
  static public IModel constructModel(CommonTree modelDescriptor,
      Collection<Exception> warnings, Collection<Exception> errors)
  {
    IModel model = null;
    try
    {
      CommonTreeNodeStream nodes = new CommonTreeNodeStream(modelDescriptor);
      JACTRBuilder builder = new JACTRBuilder(nodes);
      model = builder.model();
      errors.addAll(builder.getErrors());
      warnings.addAll(builder.getWarnings());
    }
    catch (RecognitionException e)
    {
      LOGGER.error("Invalid structure ", e);
      errors.add(e);
    }
    return model;
  }
}
