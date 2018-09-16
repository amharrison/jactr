package org.jactr.tools.tracer.transformer.chunk;

/*
 * default logging
 */
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.event.IACTREvent;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.tracer.transformer.IEventTransformer;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

public class ChunkEventTransformer implements IEventTransformer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER   = LogFactory
                                                  .getLog(ChunkEventTransformer.class);

  private ASTSupport                 _support = new ASTSupport();

  public ITransformedEvent transform(IACTREvent actrEvent)
  {
    ChunkEvent event = (ChunkEvent) actrEvent;
    ChunkEvent.Type type = event.getType();
    CommonTree ast = null;
    boolean handled = false;

    try
    {
      switch (type)
      {
        case ENCODED:
          ast = encoded(event);
          handled = ast != null;
          break;
        case SLOT_VALUE_CHANGED:
          ast = slotsChanged(event);
          handled = ast != null;
          break;
        case MERGING_WITH: // unnecessary to send, implied by into..
          break;
        case MERGING_INTO:
          // eek, how's this for readability..
          handled = (ast = merge(event, event.getSource().getSymbolicChunk()
              .getName(), event.getChunk().getSymbolicChunk().getName())) != null;
          break;
        default:
          handled = false;

      }

      if (handled)
        return new TransformedChunkEvent(
            event.getSource().getModel().getName(), event.getSource()
                .getSymbolicChunk().getName(), event.getSystemTime(), event
                .getSimulationTime(), type, ast);
    }
    catch (Exception e)
    {
      LOGGER.debug("Could not transform event ", e);
    }

    return null;
  }

  protected CommonTree encoded(ChunkEvent event)
  {
    return ASTResolver.toAST(event.getSource(), true);
  }

  protected CommonTree slotsChanged(ChunkEvent event)
  {
    Object value = event.getNewSlotValue();
    String valueStr = "null";
    if (value != null) valueStr = value.toString();

    CommonTree ast = _support.create(JACTRBuilder.SLOTS);
    ast.addChild(_support.createSlot(event.getSlotName(), _support.create(
        JACTRBuilder.STRING, valueStr)));

    return ast;
  }

  protected CommonTree merge(ChunkEvent event, String chunkA, String chunkB)
  {
    CommonTree ast = _support.create(JACTRBuilder.CHUNKS);
    ast.addChild(_support.create(JACTRBuilder.CHUNK_IDENTIFIER, chunkA));
    ast.addChild(_support.create(JACTRBuilder.CHUNK_IDENTIFIER, chunkB));
    return ast;
  }
}
