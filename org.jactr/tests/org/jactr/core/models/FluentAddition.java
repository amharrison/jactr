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

public class FluentAddition implements Supplier<IModel>
{

  @Override
  public IModel get()
  {
    IModel model = FluentModel.named("addition").withCoreModules().build();

    IChunkType add = FluentChunkType.from(model).named("add")
        .slots("arg1", "arg2", "count", "sum").encode();
    IChunkType countOrder = FluentChunkType.from(model).named("count-order")
        .slots("first", "second").encode();

    /*
     * define existing knowledge
     */
    for (char count = 'a'; count < 'k'; count++)
    {
      int first = count - 'a';
      int second = first + 1;
      FluentChunk.from(countOrder).slot("first", first).slot("second", second)
          .encode();
    }

    IChunk free = model.getDeclarativeModule().getFreeChunk();

    /*
     * define the productions
     */

    FluentProduction.from(model).named("initialize-addition")
        .condition(FluentCondition.match("goal", add).slot("arg1", "=num1")
            .slot("arg2", "=num2").slot("sum", null).build())
        .action(FluentAction.modify("goal").slot("count", 0)
            .slot("sum", "=num1").build())
        .action(FluentAction.add("retrieval", countOrder).slot("first", "=num1")
            .build())
        .encode();

    FluentProduction.from(model).named("terminate-addition")
        .condition(FluentCondition.match("goal", add).slot("arg1", "=num1")
            .slot("arg2", "=num2").slot("count", "=num2").slot("sum", "=answer")
            .build())
        .action(FluentAction.modify("goal").slot("count", null).build())
        .encode();

    FluentProduction.from(model).named("increment-count")
        .condition(FluentCondition.match("goal", add).slot("sum", "=sum")
            .slot("count", "=count").build())
        .condition(FluentCondition.match("retrieval", countOrder)
            .slot("first", "=count").slot("second", "=newCount").build())
        .condition(
            FluentCondition.query("retrieval").slot("state", free).build())
        .action(FluentAction.modify("goal").slot("count", "=newCount").build())
        .action(FluentAction.add("retrieval", countOrder).slot("first", "=sum")
            .build())
        .encode();

    FluentProduction.from(model).named("increment-sum")
        .condition(FluentCondition.match("goal", add).slot("sum", "=sum")
            .slot("count", "=count").slot("arg2").not("=count").build())
        .condition(FluentCondition.match("retrieval", countOrder)
            .slot("first", "=sum").slot("second", "=newSum").build())
        .condition(
            FluentCondition.query("retrieval").slot("state", free).build())
        .action(FluentAction.modify("goal").slot("sum", "=newSum").build())
        .action(FluentAction.add("retrieval", countOrder)
            .slot("first", "=count").build())
        .action(FluentAction.output("=newSum").build()).encode();

    /*
     * the actual goal
     */
    IChunk goal = FluentChunk.from(add).slot("arg1", 5).slot("arg2", 2).build();

    model.getActivationBuffer("goal").addSourceChunk(goal);

    try
    {
      model.initialize();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    return model;
  }

}
