package org.jactr.modules.versioned.procedural;

/*
 * default logging
 */
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.IProduction;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.antlr3.misc.DetailedCommonTreeAdaptor;
import org.jactr.io.resolver.ASTResolver;

/**
 * copies and rewrites productions in response to chunk/type refinement.
 * 
 * @author harrison
 */
public class ProductionRewriter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER       = LogFactory
                                                      .getLog(ProductionRewriter.class);

  CommonTreeAdaptor                  _treeAdaptor = new DetailedCommonTreeAdaptor();

  ASTSupport                         _support     = new ASTSupport();

  private IProceduralModule          _proceduralModule;
  
  private IModel _model;

  public ProductionRewriter(IModel model)
  {
    _model = model;
  }

  public void refineChunkType(IChunkType oldType, IChunkType newType)
  {
    refine(oldType.toString(), newType.toString(),
        JACTRBuilder.CHUNK_TYPE_IDENTIFIER);

  }

  public void refineChunk(IChunk oldChunk, IChunk newChunk)
  {
    refine(oldChunk.toString(), newChunk.toString(),
        JACTRBuilder.CHUNK_IDENTIFIER);
  }

  private void refine(String oldID, String newID, int token)
  {
    try
    {
    	if(_proceduralModule == null) _proceduralModule = _model.getProceduralModule();
      for (IProduction production : _proceduralModule.getProductions().get())
      {
        CommonTree tree = ASTResolver.toAST(production);

        boolean modified = false;
        for (CommonTree chunkTypeID : ASTSupport.getAllDescendantsWithType(
            tree, token))
          if (chunkTypeID.getText().equals(oldID))
          {
            modified = true;
            int index = chunkTypeID.getChildIndex();
            // _treeAdaptor.setText(chunkTypeID, newID);
            _treeAdaptor.setChild(chunkTypeID.getParent(), index,
                _treeAdaptor.create(token, newID));
          }

        if (modified)
        {
          CommonTree params = ASTSupport.getFirstDescendantWithType(tree,
              JACTRBuilder.PARAMETERS);

          CommonTree version = _support.create(JACTRBuilder.PARAMETER,
              "parameter");
          version.addChild(_support.create(JACTRBuilder.NAME, "version"));
          version.addChild(_support.create(JACTRBuilder.STRING,
              Double.toString(_proceduralModule.getModel().getAge())));
          for (CommonTree param : ASTSupport.getAllDescendantsWithType(params,
              JACTRBuilder.PARAMETER))
            if (param.getFirstChildWithType(JACTRBuilder.NAME).getText() == "version")
            {
              _treeAdaptor.setChild(params, param.getChildIndex(), version);
              version = null;
              break;
            }
          if (version != null) params.addChild(version);

          CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
          JACTRBuilder builder = new JACTRBuilder(nodes);
          builder.setModel(_proceduralModule.getModel());
          try
          {
            // will call addProduction
            IProduction newProduction = builder.production();
            // need to apply parameters

            builder.applyParameters(newProduction.getSubsymbolicProduction(),
                ASTSupport.getAllDescendantsWithType(params,
                    JACTRBuilder.PARAMETER));
          }
          catch (RecognitionException e)
          {
            LOGGER.error("Invalid structure ", e);
          }
        }
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to get productions", e);
    }
  }
}
