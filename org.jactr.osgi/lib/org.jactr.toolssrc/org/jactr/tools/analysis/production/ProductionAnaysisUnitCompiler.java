package org.jactr.tools.analysis.production;

/*
 * default logging
 */
import java.util.Collection;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.compiler.IUnitCompiler;
import org.jactr.tools.analysis.production.endstates.impl.AddEndStateComputer;
import org.jactr.tools.analysis.production.endstates.impl.ModifyEndStateComputer;
import org.jactr.tools.analysis.production.endstates.impl.PMAddEndStateComputer;
import org.jactr.tools.analysis.production.endstates.impl.RemovalEndStateComputer;
import org.jactr.tools.analysis.production.relationships.GeneralRelationshipComputer;

public class ProductionAnaysisUnitCompiler implements IUnitCompiler
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionAnaysisUnitCompiler.class);

  private SequenceAnalyzer _analyzer;

  private Collection<Integer>        _relevantTypes;

  public ProductionAnaysisUnitCompiler()
  {
    _analyzer = new SequenceAnalyzer();
    _relevantTypes = new TreeSet<Integer>();
    _relevantTypes.add(JACTRBuilder.CHUNK_TYPE);
    _relevantTypes.add(JACTRBuilder.PRODUCTION);

    _analyzer.add(new RemovalEndStateComputer());
    _analyzer.add(new ModifyEndStateComputer());
    _analyzer.add(new AddEndStateComputer());
    _analyzer.add(new PMAddEndStateComputer("visual", "visual-location",
        "motor", "aural", "aural-location", "vocal", "configural",
        "manipulative"));
    
    _analyzer.add(new GeneralRelationshipComputer());
  }

  public void compile(CommonTree node, Collection<Exception> info, Collection<Exception> warnings,
      Collection<Exception> errors)
  {
    int type = node.getType();
    if (type == JACTRBuilder.CHUNK_TYPE)
      _analyzer.addChunkType(node);
    else if (type == JACTRBuilder.PRODUCTION) _analyzer.addProduction(node);
  }

  public Collection<Integer> getRelevantTypes()
  {
    return _relevantTypes;
  }

  public void postCompile()
  {

  }

  public void preCompile()
  {
    _analyzer.reset();
  }

  public SequenceAnalyzer getAnalyzer()
  {
    return _analyzer;
  }
}
