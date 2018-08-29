package org.jactr.core.models;

import java.util.function.Supplier;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.fluent.FluentAction;
import org.jactr.fluent.FluentChunk;
import org.jactr.fluent.FluentChunkType;
import org.jactr.fluent.FluentCondition;
import org.jactr.fluent.FluentModel;
import org.jactr.fluent.FluentProduction;

public class FluentCount implements Supplier<IModel>
{

  @Override
  public IModel get()
  {
    try
    {
      IModel model = FluentModel.named("count").withCoreModules().build();
      IChunkType countOrder = FluentChunkType.from(model).named("count-order")
          .slots("first", "second").encode();
      IChunkType countFrom = FluentChunkType.from(model).named("count-from")
          .slots("start", "end", "step").encode();

      IChunkType chunk = model.getDeclarativeModule().getChunkType("chunk")
          .get();

      IChunk start = FluentChunk.from(chunk).named("start").encodeIfAbsent();
      IChunk counting = FluentChunk.from(chunk).named("counting")
          .encodeIfAbsent();
      IChunk stop = FluentChunk.from(chunk).named("stop").encodeIfAbsent();
      IChunk error = model.getDeclarativeModule().getErrorChunk();

      FluentChunk.from(chunk).named("stop").encodeIfAbsent();

      for (char index = 'b'; index < 'g'; index++)
        FluentChunk.from(countOrder).named("" + index)
            .slot("first", index - 'b' + 1).slot("second", index - 'b' + 2)
            .encode();

      FluentProduction.from(model).named("start")
          .condition(FluentCondition.match("goal", countFrom)
              .slot("start", "=num1").slot("step", start).build())
          .action(FluentAction.modify("goal").slot("step", counting).build())
          .action(FluentAction.add("retrieval", countOrder)
              .slot("first", "=num1").build())
          .action(FluentAction
              .output("Searching for something starting with =num1").build())
          .encode();

      FluentProduction.from(model).named("failed")
          .condition(FluentCondition.match("goal", countFrom)
              .slot("start", "=num").slot("step", counting).build())
          .condition(
              FluentCondition.query("retrieval").slot("state", error).build())
          .action(FluentAction.remove("goal").build()).encode();

      FluentProduction.from(model).named("increment")
          .condition(
              FluentCondition.match("goal", countFrom).slot("start", "=num1")
                  .slot("end").not("=num1").slot("step", counting).build())
          .condition(FluentCondition.match("retrieval", countOrder)
              .slot("first", "=num1").slot("second", "=num2").build())
          .action(FluentAction.modify("goal").slot("start", "=num2").build())
          .action(FluentAction.add("retrieval", countOrder)
              .slot("first", "=num2").build())
          .encode();

      FluentProduction.from(model).named("stop")
          .condition(
              FluentCondition.match("goal", countFrom).slot("start", "=num")
                  .slot("end", "=num").slot("step", counting).build())
          .action(FluentAction.modify("goal").slot("step", stop).build())
          .action(FluentAction.output("Answer =num").build()).encode();
      /*
       * goal
       */

      IChunk goal = FluentChunk.from(countFrom).slot("start", 2).slot("end", 5)
          .slot("step", start).build();

      model.getActivationBuffer("goal").addSourceChunk(goal);

      model.initialize();

      return model;
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

}
