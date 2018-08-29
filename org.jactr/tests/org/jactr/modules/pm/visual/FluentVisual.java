package org.jactr.modules.pm.visual;

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

public class FluentVisual implements Supplier<IModel>
{

  @Override
  public IModel get()
  {
    try
    {
      IModel model = FluentModel.named("test-visual").withCoreModules()
          .withPMModules().build();

      ((DefaultAuralModule6) model.getModule(DefaultAuralModule6.class))
          .setParameter("EnableBufferStuff", "false"); // turn off stuffing

      // reduces the logging noise
      ((DefaultVisualModule6) model.getModule(DefaultVisualModule6.class))
          .setParameter("EnableVisualBufferStuff", "false");

      IChunkType chunk = model.getDeclarativeModule().getChunkType("chunk")
          .get();
      IChunkType visLoc = model.getDeclarativeModule()
          .getChunkType("visual-location").get();

      IChunk free = model.getDeclarativeModule().getFreeChunk();
      IChunk error = model.getDeclarativeModule().getErrorChunk();

      IChunk ltc = model.getDeclarativeModule().getChunk("less-than-current")
          .get();
      IChunk gtc = model.getDeclarativeModule().getChunk("greater-than-current")
          .get();

      Map<String, IChunk> definedChunks = FluentChunk.from(chunk).chunks(
          "searching", "found", "starting", "failed", "succeeded", "test-kind",
          "test-less-than", "test-greater-than", "test-value", "test-color",
          "test-size");

      IChunkType goalType = FluentChunkType.from(model).named("goal")
          .slot("status", definedChunks.get("starting")).slot("stage").encode();

      IChunkType testType = FluentChunkType.from(goalType)
          .named("attending-test").slot("testValue").encode();

      IChunk goalChunk = FluentChunk.from(testType).slot("testValue", "center")
          .slot("stage", definedChunks.get("test-kind")).build();

      model.getActivationBuffer("goal").addSourceChunk(goalChunk);

      FluentProduction.from(model).named("search-kind")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("starting"))
              .slot("stage", definedChunks.get("test-kind")).build())
          .condition(FluentCondition.query("visual-location")
              .slot("state", free).build())
          .action(FluentAction.add("visual-location", visLoc)
              .slot(":attended", null)
              .slot("nearest",
                  model.getDeclarativeModule().getChunk("current").get())
              .slot("kind",
                  model.getDeclarativeModule().getChunkType("text").get())
              .build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("searching")).build())
          .encode();

      FluentProduction.from(model).named("search-less-than")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("succeeded"))
              .slot("stage", definedChunks.get("test-kind")).build())
          .condition(FluentCondition.query("visual-location")
              .slot("state", free).build())
          .action(FluentAction.add("visual-location", visLoc)
              .slot(":attended", null).slot("screen-x", ltc)
              .slot("screen-y", ltc).build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("searching"))
              .slot("stage", definedChunks.get("test-less-than"))
              .slot("testValue", "lowerLeft").build())
          .encode();

      FluentProduction.from(model).named("search-greater-than")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("succeeded"))
              .slot("stage", definedChunks.get("test-less-than")).build())
          .condition(FluentCondition.query("visual-location")
              .slot("state", free).build())
          .action(FluentAction.add("visual-location", visLoc)
              .slot(":attended", null).slot("screen-x", gtc)
              .slot("screen-y", gtc).build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("searching"))
              .slot("stage", definedChunks.get("test-greater-than"))
              .slot("testValue", "upperRight").build())
          .encode();

      FluentProduction.from(model).named("search-color")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("succeeded"))
              .slot("stage", definedChunks.get("test-greater-than")).build())
          .condition(FluentCondition.query("visual-location")
              .slot("state", free).build())
          .action(FluentAction.add("visual-location", visLoc)
              .slot(":attended", null)
              .slot("color",
                  model.getDeclarativeModule().getChunk("white").get())
              .build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("searching"))
              .slot("stage", definedChunks.get("test-color"))
              .slot("testValue", "lowerRight").build())
          .encode();

      FluentProduction.from(model).named("search-size")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("succeeded"))
              .slot("stage", definedChunks.get("test-color")).build())
          .condition(FluentCondition.query("visual-location")
              .slot("state", free).build())
          .action(FluentAction.add("visual-location", visLoc)
              .slot(":attended", null).slot("size", gtc).build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("searching"))
              .slot("stage", definedChunks.get("test-size"))
              .slot("testValue", "lowerMiddle").build())
          .encode();

      FluentProduction.from(model).named("search-size-succeeded")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("succeeded"))
              .slot("stage", definedChunks.get("test-size")).build())
          .action(FluentAction.remove("goal").build())
          .action(FluentAction.remove("visual-location").build()).encode();

      FluentProduction.from(model).named("search-match-failed")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("searching"))
              .slot("stage", "=stage").slot("testValue", "=value").build())
          .condition(FluentCondition.match("visual-location", visLoc)
              .slot("value").not("=value").build())
          .action(FluentAction.remove("goal").build())
          .action(FluentAction.remove("visual-location").build()).encode();

      FluentProduction.from(model).named("search-failed")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("searching")).build())
          .condition(FluentCondition.query("visual-location")
              .slot("state", error).build())
          .action(FluentAction.remove("goal")
              .slot("status", definedChunks.get("failed")).build())
          .action(FluentAction.remove("visual-location").build()).encode();

      FluentProduction.from(model).named("search-succeeded")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("searching"))
              .slot("testValue", "=value").build())
          .condition(FluentCondition.match("visual-location", visLoc)
              .slot("value", "=value").build())
          .condition(
              FluentCondition.query("visual").slot("state", free).build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("found")).build())
          .action(FluentAction.modify("visual-location").build())
          // general version of attending
          .action(FluentAction
              .add("visual",
                  model.getDeclarativeModule().getChunkType("attend-to").get())
              .slot("where", "=visual-location").build())
          // canonical version
//          .action(FluentAction
//              .add("visual",
//                  model.getDeclarativeModule().getChunkType("move-attention").get())
//              .slot("screen-pos", "=visual-location").build())
          .encode();

      FluentProduction.from(model).named("encoding-succeeded")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("found"))
              .slot("testValue", "=value").build())
          .condition(FluentCondition.match("visual",
              model.getDeclarativeModule().getChunkType("visual-object").get())
              .slot("value", "=value").build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("succeeded")).build())
          .encode();

      FluentProduction.from(model).named("encoding-match-failed")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("found")).slot("stage",
                  "=stage")
              .slot("testValue", "=value").build())
          .condition(FluentCondition.match("visual",
              model.getDeclarativeModule().getChunkType("visual-object").get())
              .slot("value").not("=value").build())
          .action(FluentAction.remove("goal").build())
          .action(FluentAction.remove("visual").build()).encode();

      FluentProduction.from(model).named("encoding-failed")
          .condition(FluentCondition.match("goal", testType)
              .slot("status", definedChunks.get("found")).build())
          .condition(
              FluentCondition.query("visual").slot("state", error).build())
          .action(FluentAction.remove("goal").build())
          .action(FluentAction.remove("visual").build()).encode();

      model.initialize();
      return model;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
