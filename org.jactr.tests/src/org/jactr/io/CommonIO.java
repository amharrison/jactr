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
 * Created on May 21, 2005 by developer
 */

package org.jactr.io;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.io.antlr3.Support;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.io.parser.IModelParser;
import org.jactr.io.parser.ModelParserFactory;
import org.jactr.io.resolver.ASTResolver;

public class CommonIO extends Assert
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory.getLog(CommonIO.class);

  static public Collection<StringBuilder> generateSource(IModel model,
      String extension)
  {
    CommonTree modelTree = ASTResolver.toAST(model, true);
    return generateSource(modelTree, extension);
  }

  static public Collection<StringBuilder> generateSource(CommonTree modelDesc,
      String extension)
  {
    ICodeGenerator coder = CodeGeneratorFactory.getCodeGenerator(extension);
    assertNotNull(coder);

    Collection<StringBuilder> lines = coder.generate(modelDesc, true);
    assertNotNull(lines);
    assertTrue(lines.size() != 0);

    return lines;
  }
  

  /**
   * construct an already compiled model
   * 
   * @param modelDescriptor
   * @return
   */
  static public IModel constructorTest(CommonTree modelDescriptor)
  {
    IModel model = null;

    try
    {
      ArrayList<Exception> warnings = new ArrayList<Exception>();
      ArrayList<Exception> errors = new ArrayList<Exception>();
      long startTime = System.currentTimeMillis();
      model = IOUtilities.constructModel(modelDescriptor, warnings, errors);
      LOGGER.info("Building took " + (System.currentTimeMillis() - startTime)
          + "ms");

      String name = ((CommonTree) modelDescriptor
          .getFirstChildWithType(JACTRBuilder.NAME)).getText();
      processExceptions(name, warnings, true, modelDescriptor);
      processExceptions(name, errors, true, modelDescriptor);
      assertNotNull(model);
    }
    catch (RuntimeException e)
    {
      LOGGER.error("Could not generate model ", e);
      for (StringBuilder sb : generateSource(modelDescriptor, "jactr"))
        LOGGER.error(sb.toString());
      throw e;
    }

    return model;
  }

  /**
   * compile a model - compile is actually a slight misnomer
   * 
   * @param md
   * @param failWarnings
   * @param failErrors
   */
  public static CommonTree compilerTest(CommonTree md, boolean failWarnings,
      boolean failErrors)
  {

    LOGGER.info("IModel Descriptor : " + md.toStringTree());
    ArrayList<Exception> warnings = new ArrayList<Exception>();
    ArrayList<Exception> errors = new ArrayList<Exception>();

    long startTime = System.currentTimeMillis();
    IOUtilities.compileModelDescriptor(md, warnings, errors);
    LOGGER.info("Compiling took " + (System.currentTimeMillis() - startTime)
        + "ms");

    String name = ((CommonTree) md.getFirstChildWithType(JACTRBuilder.NAME))
        .getText();
    processExceptions(name, warnings, failWarnings, md);
    processExceptions(name, errors, failErrors, md);
    return md;
  }

  /**
   * parse a model into a modeldescriptor
   * 
   * @param fileName
   * @param failWarnings
   * @param failErrors
   * @return
   */
  static public CommonTree parserTest(String fileName, boolean failWarnings,
      boolean failErrors)
  {
    IModelParser mp;
    try
    {
      mp = parseModel(fileName);
      processExceptions(fileName, mp.getParseWarnings(), failWarnings, null);
      processExceptions(fileName, mp.getParseErrors(), failErrors, null);
      CommonTree md = getModelDescriptor(mp);
      return md;
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("Could not load " + fileName, e);
      return null;
    }
  }

  static public IModelParser parseModel(String fileName) throws IOException
  {
    URL modelURL = CommonIO.class.getClassLoader().getResource(fileName);
    assertNotNull(modelURL);

    IModelParser mp = ModelParserFactory.getModelParser(modelURL);
    assertNotNull(mp);

    long startTime = System.currentTimeMillis();
    mp.parse();
    LOGGER.info("Parsing took " + (System.currentTimeMillis() - startTime)
        + "ms");
    return mp;
  }

  static public CommonTree getModelDescriptor(IModelParser mp)
  {
    CommonTree tree = mp.getDocumentTree();
    assertNotNull(tree);
    return tree;
  }

  static public void processExceptions(String fileName,
      Collection<Exception> exceptions, boolean failOnException, CommonTree tree)
  {
    for (Exception e : exceptions)
    {
      LOGGER.error("Failed to process " + fileName + " ", e);
      if (failOnException)
      {
        if (tree != null) LOGGER.error(Support.outputTree(tree));
        fail(e.getMessage());
        LOGGER.error("exception : ", e);
      }
    }
  }

  static public IModel loadModel(String fileName)
  {
    LOGGER.info("Loading " + fileName);
    CommonTree md = CommonIO.parserTest(fileName, true, true);
    assertNotNull(md);

    LOGGER.info("Compiling " + fileName);
    CommonIO.compilerTest(md, true, true);

    // CommonIO.generateSource(md, "jactr");

    LOGGER.info("Constructing " + fileName);
    IModel model = CommonIO.constructorTest(md);
    assertNotNull(model);

    return model;
  }

  static public IModel mockModel()
  {
    CommonTree modelDesc = IOUtilities.createModelDescriptor("mock");

    // compile & build
    return constructorTest(compilerTest(modelDesc, false, true));
  }
}
