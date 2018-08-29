package org.jactr.core.models;

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

public class FluentSemantic implements Supplier<IModel>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(FluentSemantic.class);

  @Override
  public IModel get()
  {
    String[] chunkConstants = { "shark", "dangerous", "true", "locomotion",
        "swimming", "category", "fish", "salmon", "edible", "breathe", "gills",
        "animal", "moves", "skin", "canary", "yellow", "sings", "bird",
        "ostrich", "flies", "false", "height", "tall", "wings", "flying", "yes",
        "no", "pending", "color" };

    String[][] propertyConstants = { { "shark", "dangerous", "true" },
        { "shark", "locomotion", "swimming" }, { "shark", "category", "fish" },

        { "salmon", "edible", "true" }, { "salmon", "locomotion", "swimming" },
        { "salmon", "category", "fish" },

        { "fish", "breathe", "gills" }, { "fish", "locomotion", "swimming" },
        { "fish", "category", "animal" },

        { "animal", "moves", "true" }, { "animal", "skin", "true" },

        { "canary", "color", "yellow" }, { "canary", "sings", "true" },
        { "canary", "category", "bird" },

        { "ostrich", "flies", "false" }, { "ostrich", "height", "tall" },
        { "ostrich", "category", "bird" },

        { "bird", "wings", "true" }, { "bird", "locomotion", "flying" },
        { "bird", "category", "animal" } };
    try
    {
      IModel model = FluentModel.named("semantic").withCoreModules().build();

      IChunkType propertyType = FluentChunkType.from(model)
          .slots("object", "attribute", "value").encode();

      IChunkType isMember = FluentChunkType.from(model).named("is-member")
          .slots("object", "category", "judgement").encode();

      IChunkType chunk = model.getDeclarativeModule().getChunkType("chunk")
          .get();

      /*
       * marker chunk constants
       */
      Map<String, IChunk> definedChunks = FluentChunk.from(chunk)
          .chunks(chunkConstants);

      /*
       * property tuples
       */
      for (int i = 0; i < propertyConstants.length; i++)
      {
        String[] values = propertyConstants[i];
        FluentChunk.from(propertyType).named("p" + (i + 1))
            .slot("object", definedChunks.get(values[0]))
            .slot("attribute", definedChunks.get(values[1]))
            .slot("value", definedChunks.get(values[2])).encode();
      }

      /*
       * productions
       */

      /*
       * initial-retrieval. Notice we can't use :state shortcut in match and
       * must use a query instead. However, we can use :recently-retrieved in
       * add.
       */
      FluentProduction.from(model).named("initial-retrieval")
          .condition(
              FluentCondition.match("goal", isMember).slot("object", "=obj")
                  .slot("category", "=cat").slot("judgement", null).build())
          .condition(FluentCondition.query("retrieval")
              .slot("state", model.getDeclarativeModule().getFreeChunk())
              .build())
          .action(FluentAction.modify("goal")
              .slot("judgement", definedChunks.get("pending")).build())
          .action(
              FluentAction.add("retrieval", propertyType).slot("object", "=obj")
                  .slot("attribute", definedChunks.get("category"))
                  .slot(":recently-retrieved", null).build())
          .encode();

      FluentProduction.from(model).named("direct-verify")
          .condition(FluentCondition.match("goal", isMember)
              .slot("object", "=obj").slot("category", "=cat")
              .slot("judgement", definedChunks.get("pending")).build())
          .condition(FluentCondition.match("retrieval", propertyType)
              .slot("object", "=obj")
              .slot("attribute", definedChunks.get("category"))
              .slot("value", "=cat").build())
          .action(FluentAction.modify("goal")
              .slot("judgement", definedChunks.get("true")).build())
          .action(FluentAction.remove("retrieval").build()).encode();

      FluentProduction.from(model).named("chain-category")
          .condition(FluentCondition.match("goal", isMember)
              .slot("object", "=obj").slot("category", "=cat")
              .slot("judgement", definedChunks.get("pending")).build())
          .condition(FluentCondition.match("retrieval", propertyType)
              .slot("object", "=obj")
              .slot("attribute", definedChunks.get("category"))
              .slot("value", "=value").slot("value").not("=cat").build())
          .action(FluentAction.modify("goal").slot("object", "=value").build())
          .action(FluentAction.add("retrieval", propertyType)
              .slot("object", "=value")
              .slot("attribute", definedChunks.get("category")).build())
          .encode();

      FluentProduction.from(model).named("fail")
          .condition(FluentCondition.match("goal", isMember)
              .slot("object", "=obj").slot("category", "=cat")
              .slot("judgement", definedChunks.get("pending")).build())
          .condition(FluentCondition.query("retrieval")
              .slot("state", model.getDeclarativeModule().getErrorChunk())
              .build())
          .action(FluentAction.modify("goal")
              .slot("judgement", definedChunks.get("no")).build())
          .action(FluentAction.remove("retrieval").build()).encode();
      /*
       * goal
       */
      IChunk g3 = FluentChunk.from(isMember)
          .slot("object", definedChunks.get("canary"))
          .slot("category", definedChunks.get("fish")).build();

      model.getActivationBuffer("goal").addSourceChunk(g3);

      model.initialize();
      return model;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
