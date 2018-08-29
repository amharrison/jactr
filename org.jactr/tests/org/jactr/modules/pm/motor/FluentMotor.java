package org.jactr.modules.pm.motor;

import java.util.Map;
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
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;

public class FluentMotor implements Supplier<IModel>
{
  @Override
  public IModel get()
  {
    try
    {
      IModel model = FluentModel.named("test-motor").withCoreModules()
          .withPMModules().build();

      ((DefaultAuralModule6) model.getModule(DefaultAuralModule6.class))
          .setParameter("EnableBufferStuff", "false"); // turn off stuffing

      // reduces the logging noise
      ((DefaultVisualModule6) model.getModule(DefaultVisualModule6.class))
          .setParameter("EnableVisualBufferStuff", "false");

      IChunkType chunk = model.getDeclarativeModule().getChunkType("chunk")
          .get();

      Map<String, IChunk> definedChunks = FluentChunk.from(chunk).chunks(
          "starting", "retrieving", "motor-started", "motor-completed",
          "finished");

      IChunk right = model.getDeclarativeModule().getChunk("right").get();
      IChunk left = model.getDeclarativeModule().getChunk("left").get();
      IChunk index = model.getDeclarativeModule().getChunk("index").get();
      IChunk free = model.getDeclarativeModule().getFreeChunk();
      IChunk error = model.getDeclarativeModule().getErrorChunk();

      IChunkType goal = FluentChunkType.from(model).named("goal")
          .slot("state", definedChunks.get("starting")).encode();

      IChunkType sequence = FluentChunkType.from(model).named("sequence")
          .slots("order", "command", "finger", "hand", "r", "theta").encode();

      /*
       * sequence of chunks that defines the execution
       */
      FluentChunk.from(sequence).named("punch-j").slot("order", 1)
          .slot("command",
              model.getDeclarativeModule().getChunkType("punch").get())
          .slot("finger", index).slot("hand", right).encode();

      FluentChunk.from(sequence).named("peck-g").slot("order", 2)
          .slot("command",
              model.getDeclarativeModule().getChunkType("peck").get())
          .slot("finger", index).slot("hand", left).slot("r", 1)
          .slot("theta", 0).encode();

      FluentChunk.from(sequence).named("punch-g").slot("order", 3)
          .slot("command",
              model.getDeclarativeModule().getChunkType("punch").get())
          .slot("finger", index).slot("hand", left).encode();

      FluentChunk.from(sequence).named("peck-recoil-h").slot("order", 4)
          .slot("command",
              model.getDeclarativeModule().getChunkType("peck-recoil").get())
          .slot("finger", index).slot("hand", right).slot("r", 1)
          .slot("theta", 3.14).encode();

      /*
       * productions
       */
      FluentProduction.from(model).named("start")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("starting")).build())
          .condition(
              FluentCondition.query("retrieval").slot("state", free).build())
          .action(
              FluentAction.add("retrieval", sequence).slot("order", 1).build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("retrieving")).build())
          .encode();

      /*
       * javascript for math
       */
      FluentProduction.from(model).named("retrieve-next-movement")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("motor-completed")).build())
          .condition(FluentCondition.match("retrieval", sequence)
              .slot("order", "=index").build())
          .condition(FluentCondition.script("javascript",
              "function matches()\n" + "          {\n"
                  + "            jactr.requires(\"=index\");\n"
                  + "            var index = jactr.get(\"=index\");\n"
                  + "            jactr.set(\"=next\", ++index);\n"
                  + "            return true;\n" + "          }"))
          .action(FluentAction.add("retrieval", sequence).slot("order", "=next")
              .build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("retrieving")).build())
          .encode();

      FluentProduction.from(model).named("retrieval-failed")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("retrieving")).build())
          .condition(
              FluentCondition.query("retrieval").slot("state", error).build())
          .action(FluentAction.remove("retrieval").build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("finished")).build())
          .encode();

      FluentProduction.from(model).named("movement-completed")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("motor-started")).build())
          .condition(FluentCondition.match("retrieval", sequence)
              .slot("command", "=command").build())
          .condition(FluentCondition.query("motor").slot("state", free).build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("motor-completed")).build())
          .action(FluentAction.modify("retrieval").build())
          .encode();

      FluentProduction.from(model).named("movement-failed")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("motor-started")).build())
          .condition(FluentCondition.match("retrieval", sequence)
              .slot("command", "=command").build())
          .condition(
              FluentCondition.query("motor").slot("state", error).build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("finished")).build())
          .encode();

      FluentProduction.from(model).named("start-punch")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("retrieving")).build())
          .condition(FluentCondition.match("retrieval", sequence)
              .slot("command",
                  model.getDeclarativeModule().getChunkType("punch").get())
              .slot("finger", "=finger").slot("hand", "=hand").build())
          .condition(FluentCondition.query("motor").slot("state", free).build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("motor-started")).build())
          .action(
              FluentAction.modify("retrieval").build())
          .action(FluentAction
              .add("motor",
                  model.getDeclarativeModule().getChunkType("punch").get())
              .slot("hand", "=hand").slot("finger", "=finger").build())
          .encode();

      FluentProduction.from(model).named("start-peck")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("retrieving")).build())
          .condition(FluentCondition.match("retrieval", sequence)
              .slot("command",
                  model.getDeclarativeModule().getChunkType("peck").get())
              .slot("finger", "=finger").slot("hand", "=hand").slot("r", "=r")
              .slot("theta", "=theta").build())
          .condition(FluentCondition.query("motor").slot("state", free).build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("motor-started")).build())
          .action(
              FluentAction.modify("retrieval").build())
          .action(FluentAction
              .add("motor",
                  model.getDeclarativeModule().getChunkType("peck").get())
              .slot("hand", "=hand").slot("finger", "=finger").slot("r", "=r")
              .slot("theta", "=theta").build())
          .encode();

      FluentProduction.from(model).named("start-peck-recoil")
          .condition(FluentCondition.match("goal", goal)
              .slot("state", definedChunks.get("retrieving")).build())
          .condition(
              FluentCondition.match("retrieval", sequence)
                  .slot("command",
                      model.getDeclarativeModule().getChunkType("peck-recoil")
                          .get())
                  .slot("finger", "=finger").slot("hand", "=hand")
                  .slot("r", "=r").slot("theta", "=theta").build())
          .condition(FluentCondition.query("motor").slot("state", free).build())
          .action(FluentAction.modify("goal")
              .slot("state", definedChunks.get("motor-started")).build())
          .action(FluentAction.modify("retrieval").build())
          .action(FluentAction
              .add("motor",
                  model.getDeclarativeModule().getChunkType("peck-recoil")
                      .get())
              .slot("hand", "=hand").slot("finger", "=finger").slot("r", "=r")
              .slot("theta", "=theta").build())
          .encode();
      // goal
      model.getActivationBuffer("goal")
          .addSourceChunk(FluentChunk.from(goal).build());

      model.initialize();
      return model;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
