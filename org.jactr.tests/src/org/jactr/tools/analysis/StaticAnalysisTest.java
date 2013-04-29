package org.jactr.tools.analysis;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.CommonIO;
import org.jactr.io.compiler.DefaultCompiler;
import org.jactr.tools.analysis.production.ProductionAnaysisUnitCompiler;

public class StaticAnalysisTest extends TestCase
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(StaticAnalysisTest.class);

  
  public void testAddition() throws Exception
  {
    analyze("org/jactr/core/models/addition.jactr");
  }
  
  public void testSemantic() throws Exception
  {
    analyze("org/jactr/core/runtime/semantic-model.jactr");
  }
  
  private void analyze(String modelName) throws Exception
  {
    CommonTree modelDesc = CommonIO.parserTest(modelName, false, true);
    
    Collection<Exception> errs = new ArrayList<Exception>();
    Collection<Exception> warn = new ArrayList<Exception>();
    Collection<Exception> info = new ArrayList<Exception>();
    DefaultCompiler compiler = new DefaultCompiler();
    
    ProductionAnaysisUnitCompiler analysisCompiler = new ProductionAnaysisUnitCompiler();
    
    compiler.addCompiler(analysisCompiler);
    
    compiler.compile(modelDesc, info,warn, errs);
  }
}
