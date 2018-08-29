package org.jactr.modules.pm.aural;

import java.util.Map;
import java.util.function.Supplier;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class FluentAural implements Supplier<IModel>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(FluentAural.class);

  @Override
  public IModel get()
  {
    try
    {
      IModel model = FluentModel.named("aural-test").withCoreModules()
          .withPMModules().build();

      ((DefaultAuralModule6) model.getModule(DefaultAuralModule6.class))
          .setParameter("EnableBufferStuff", "false"); // turn off stuffing

      // reduces the logging noise
      ((DefaultVisualModule6) model.getModule(DefaultVisualModule6.class))
          .setParameter("EnableVisualBufferStuff", "false");

      IChunkType chunk = model.getDeclarativeModule().getChunkType("chunk")
          .get();

      IChunkType tone = model.getDeclarativeModule().getChunkType("tone").get();

      Map<String, IChunk> definedChunks = FluentChunk.from(chunk).chunks(
          "searching", "encoding", "starting", "testing", "failed",
          "succeeded");

      IChunkType goalType = FluentChunkType.from(model).named("goal")
          .slot("status", definedChunks.get("starting")).slot("kind").encode();

      IChunkType aTestType = FluentChunkType.from(goalType)
          .named("attending-test").slot("testValue").encode();

      IChunk goalChunk = FluentChunk.from(aTestType).slot("testValue", "a")
          .slot("kind", tone).build();

      model.getActivationBuffer("goal").addSourceChunk(goalChunk);

      /*
       * lots of boiler plate productions. The first set "heard-" productions
       * just define what we are listening for and have heard.
       */
      String[][] heardComponents = { { "tone", "digit", "1" },
          { "digit", "word", "foobar" },
          { "word", "speech", "hey you over there" },
          { "speech", null, null } };

      for (String[] heard : heardComponents)
        FluentProduction.from(model).named("heard-" + heard[0])
            .condition(FluentCondition.match("goal", aTestType)
                .slot("status", definedChunks.get("succeeded"))
                .slot("kind",
                    model.getDeclarativeModule().getChunkType(heard[0]).get())
                .build())
            .action(heard[1] == null ? FluentAction.remove("goal").build()
                : FluentAction.modify("goal")
                    .slot("status", definedChunks.get("starting"))
                    .slot("kind",
                        model.getDeclarativeModule().getChunkType(heard[1])
                            .get())
                    .slot("testValue", heard[2]).build())
            .encode();

      /*
       * perception productions. search-for-sound (-failed,-succeeded),
       */

      FluentProduction.from(model).named("search-for-sound")
          .condition(FluentCondition.match("goal", aTestType)
              .slot("status", definedChunks.get("starting")).slot("kind",
                  "=kind")
              .build())
          .condition(FluentCondition.query("aural-location").slot("state")
              .not(model.getDeclarativeModule().getBusyChunk()).build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("searching")).build())
          .action(FluentAction
              .add("aural-location",
                  model.getDeclarativeModule().getChunkType("audio-event")
                      .get())
              .slot("kind", "=kind").slot(":attended", null).build())
          .encode();

      // resets on error
      FluentProduction.from(model).named("search-for-sound-failed")
          .condition(FluentCondition.match("goal", aTestType)
              .slot("status", definedChunks.get("searching"))
              .slot("kind", "=kind").build())
          .condition(FluentCondition.query("aural-location")
              .slot("state", model.getDeclarativeModule().getErrorChunk())
              .build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("starting")).build())
          .encode();

      // success
      FluentProduction.from(model).named("search-for-sound-succeeded")
          .condition(FluentCondition.match("goal", aTestType)
              .slot("status", definedChunks.get("searching"))
              .slot("kind", "=kind").build())
          .condition(FluentCondition.match("aural-location",
              model.getDeclarativeModule().getChunkType("audio-event").get())
              .slot("kind", "=kind").build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("encoding")).build())
//one way to attend (across modalities)
          .action(FluentAction
              .add("aural",
                  model.getDeclarativeModule().getChunkType("attend-to").get())
              .slot("where", "=aural-location").build())
// or this one, the aural specific old style
//
//          .action(FluentAction
//              .add("aural",
//                  model.getDeclarativeModule().getChunkType("sound").get())
//              .slot("event", "=aural-location").build())
          .encode();

      /*
       * encoding productions. encoding- (correct, failed, incorrect-kind,
       * incorrect-content)
       */

      // bail on fail
      FluentProduction.from(model).named("encoding-failed")
          .condition(FluentCondition.match("goal", aTestType)
              .slot("status", definedChunks.get("encoding")).build())
          .condition(FluentCondition.query("aural")
              .slot("state", model.getDeclarativeModule().getErrorChunk())
              .build())
          .action(FluentAction.remove("goal").build()).encode();

      FluentProduction.from(model).named("encoding-incorrect-kind")
          .condition(
              FluentCondition.match("goal", aTestType)
                  .slot("status", definedChunks.get("encoding")).slot("kind",
                      "=kind")
                  .slot("testValue", "=value").build())
          .condition(FluentCondition
              .match("aural",
                  model.getDeclarativeModule().getChunkType("sound").get())
              .slot("kind").not("=kind").build())
          .condition(FluentCondition.query("aural")
              .slot("state", model.getDeclarativeModule().getFreeChunk())
              .build())
          .action(FluentAction.remove("goal").build()).encode();

      FluentProduction.from(model).named("encoding-incorrect-content")
          .condition(
              FluentCondition.match("goal", aTestType)
                  .slot("status", definedChunks.get("encoding")).slot("kind",
                      "=kind")
                  .slot("testValue", "=value").build())
          .condition(FluentCondition
              .match("aural",
                  model.getDeclarativeModule().getChunkType("sound").get())
              .slot("content").not("=value").build())
          .condition(FluentCondition.query("aural")
              .slot("state", model.getDeclarativeModule().getFreeChunk())
              .build())
          .action(FluentAction.remove("goal").build()).encode();

      FluentProduction.from(model).named("encoding-correct")
          .condition(FluentCondition.match("goal", aTestType)
              .slot("status", definedChunks.get("encoding")).slot("kind",
                  "=kind")
              .slot("testValue", "=value").build())
          .condition(FluentCondition
              .match("aural",
                  model.getDeclarativeModule().getChunkType("sound").get())
              .slot("content", "=value").slot("kind", "=kind").build())
          .condition(FluentCondition.query("aural")
              .slot("state", model.getDeclarativeModule().getFreeChunk())
              .build())
          .action(FluentAction.modify("goal")
              .slot("status", definedChunks.get("succeeded")).build())
          .action(FluentAction.remove("aural").build())
          .action(FluentAction.remove("aural-location").build()).encode();

      try
      {
        model.initialize();
      }
      catch (Exception e2)
      {
        LOGGER.error(e2);
        e2.printStackTrace();
      }
      return model;
    }
    catch (Exception e)
    {
      LOGGER.error(e);
      throw new RuntimeException(e);
    }
  }

}
