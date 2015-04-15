package org.jactr.io.antlr3.parser.xml;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Map;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.io.CommonIO;
import org.jactr.io.IOUtilities;
import org.jactr.io.antlr3.compiler.JACTRCompiler;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.parser.IModelParser;
import org.jactr.io.parser.ModelParserFactory;

public class XMLParserText extends TestCase
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(XMLParserText.class);

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    ModelParserFactory.addParser("jactr",
        org.jactr.io.antlr3.parser.xml.JACTRModelParser.class);
  }

  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  public void testPatrol() throws Exception
  {
    int types = 24;
    int chunks = 37;
    int productions = 5;
    int buffers = 5;
    CommonTree mDesc = parserTest(
        "org/jactr/io/antlr3/parser/xml/scan-report.jactr", types, chunks,
        productions, buffers);
    mDesc = compilerTest(mDesc);
    IModel model = builderTest(mDesc, types, chunks, productions, buffers);
    model.dispose();
  }

  private CommonTree compilerTest(CommonTree modelDesc) throws Exception
  {
    ArrayList<Exception> warnings = new ArrayList<Exception>();
    ArrayList<Exception> errors = new ArrayList<Exception>();

    IOUtilities.compileModelDescriptor(modelDesc, warnings, errors);

    String name = ASTSupport.getName(modelDesc);

    for (Exception warning : warnings)
      LOGGER.warn("Warning " + name + " ", warning);

    for (Exception error : errors)
      LOGGER.error("Error " + name + " ", error);

    if (errors.size() != 0) fail("Compilation errors detected ");

    return modelDesc;
  }

  private IModel builderTest(CommonTree modelDesc, int chunkTypes, int chunks,
      int productions, int buffers) throws Exception
  {
    ArrayList<Exception> warnings = new ArrayList<Exception>();
    ArrayList<Exception> errors = new ArrayList<Exception>();

    IModel model = IOUtilities.constructModel(modelDesc, warnings, errors);

    String name = ASTSupport.getName(modelDesc);

    for (Exception warning : warnings)
      LOGGER.debug("Warning " + name + " ", warning);

    for (Exception error : errors)
      LOGGER.debug("Error " + name + " ", error);

    if (errors.size() != 0) fail("Building errors detected ");

    return model;
  }

  private CommonTree parserTest(String file, int chunkTypes, int chunks,
      int productions, int buffers) throws Exception
  {
    IModelParser modelParser = CommonIO.parseModel(file);
    CommonTree modelDescriptor = CommonIO.getModelDescriptor(modelParser);

    Map<String, CommonTree> elements = ASTSupport.getMapOfTrees(
        modelDescriptor, JACTRCompiler.CHUNK_TYPE);
    assertEquals("Incorrect number of chunktypes, known :" + elements.keySet(),
        chunkTypes, elements.size());

    elements = ASTSupport.getMapOfTrees(modelDescriptor, JACTRCompiler.CHUNK);
    assertEquals("Incorrect number of chunks, known : " + elements.keySet(),
        chunks, elements.size());

    elements = ASTSupport.getMapOfTrees(modelDescriptor,
        JACTRCompiler.PRODUCTION);
    assertEquals(
        "Incorrect number of productions, known : " + elements.keySet(),
        productions, elements.size());

    elements = ASTSupport.getMapOfTrees(modelDescriptor, JACTRCompiler.BUFFER);
    // expecting imaginal, retrieval, goal
    assertEquals("Incorrect number of buffers, known : " + elements.keySet(),
        buffers, elements.size());

    if (LOGGER.isDebugEnabled())
    {
      for (StringBuilder line : CommonIO
          .generateSource(modelDescriptor, "lisp"))
        LOGGER.debug(line.toString());

      for (StringBuilder line : CommonIO.generateSource(modelDescriptor,
          "jactr"))
        LOGGER.debug(line.toString());
    }

    for (Exception warning : modelParser.getParseWarnings())
      LOGGER.debug("Warning " + file + " ", warning);

    for (Exception error : modelParser.getParseErrors())
      LOGGER.debug("Error " + file + " ", error);

    return modelDescriptor;
  }
}
