package org.jactr.tools.analysis.production;

/*
 * default logging
 */
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.tools.analysis.production.endstates.impl.AddEndStateComputer;
import org.jactr.tools.analysis.production.endstates.impl.ModifyEndStateComputer;
import org.jactr.tools.analysis.production.endstates.impl.PMAddEndStateComputer;
import org.jactr.tools.analysis.production.endstates.impl.RemovalEndStateComputer;
import org.jactr.tools.analysis.production.relationships.GeneralRelationshipComputer;

public class ProductionAnalyzer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProductionAnalyzer.class);

  private SequenceAnalyzer _analyzer;
  
  public ProductionAnalyzer()
  {
    _analyzer = new SequenceAnalyzer();
    _analyzer.add(new RemovalEndStateComputer());
    _analyzer.add(new ModifyEndStateComputer());
    _analyzer.add(new AddEndStateComputer());
    _analyzer.add(new PMAddEndStateComputer("visual", "visual-location",
        "motor", "aural", "aural-location", "vocal", "configural",
        "manipulative"));
    
    _analyzer.add(new GeneralRelationshipComputer());
  }
  
  public void setModelDescriptor(CommonTree modelDescriptor)
  {
    _analyzer.reset();
    
    for(CommonTree chunkType : ASTSupport.getTrees(modelDescriptor, JACTRBuilder.CHUNK_TYPE))
      _analyzer.addChunkType(chunkType);
    
    for(CommonTree production : ASTSupport.getTrees(modelDescriptor, JACTRBuilder.PRODUCTION))
      _analyzer.addProduction(production);
  }
  
  public SequenceAnalyzer getAnalyzer()
  {
    return _analyzer;
  }
}
