package org.jactr.fluent.registry;

import java.util.function.Consumer;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.fluent.FluentChunkType;

public class DefaultVocalParticipant implements Consumer<IModel>
{

  @Override
  public void accept(IModel model)
  {
    try
    {
      IChunkType command = model.getDeclarativeModule().getChunkType("command")
          .get();

      IChunkType vocalCommand = FluentChunkType.from(command)
          .named("vocal-command").slot("string")
          .encode();
      FluentChunkType.from(vocalCommand).types("speak", "subvocalize");
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }

  }

}
