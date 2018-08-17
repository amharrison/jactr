package org.jactr.fluent.registry;

import java.util.function.Consumer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.fluent.FluentChunk;
import org.jactr.fluent.FluentChunkType;

public class DefaultDeclarativeParticipant implements Consumer<IModel>
{
  /**
  * Logger definition
  */
  static private final transient Log LOGGER = LogFactory
      .getLog(DefaultDeclarativeParticipant.class);

  @Override
  public void accept(IModel model)
  {
    IChunkType chunk = FluentChunkType.from(model).named("chunk").encode();

    FluentChunk.from(chunk).named("new").encode();
    FluentChunk.from(chunk).named("free").encode();
    FluentChunk.from(chunk).named("busy").encode();
    FluentChunk.from(chunk).named("error").encode();
    FluentChunk.from(chunk).named("full").encode();
    FluentChunk.from(chunk).named("empty").encode();
    FluentChunk.from(chunk).named("requested").encode();
    FluentChunk.from(chunk).named("unrequested").encode();
    FluentChunk.from(chunk).named("lowest").encode();
    FluentChunk.from(chunk).named("highest").encode();
    FluentChunk.from(chunk).named("reset").encode();

    FluentChunk.from(chunk).named("error-nothing-available").encode();
    FluentChunk.from(chunk).named("error-nothing-matches").encode();
    FluentChunk.from(chunk).named("error-no-longer-available").encode();
    FluentChunk.from(chunk).named("error-changed-too-much").encode();
    FluentChunk.from(chunk).named("error-invalid-index").encode();
    FluentChunk.from(chunk).named("error-chunk-deleted").encode();
    FluentChunk.from(chunk).named("error-unknown").encode();

    IChunkType command = FluentChunkType.from(model).named("command").encode();
    FluentChunkType.from(model).named("clear").childOf(command).slot("all")
        .encode();
    FluentChunkType.from(model).named("attend-to").childOf(command).slot("where").encode();
  }

}
