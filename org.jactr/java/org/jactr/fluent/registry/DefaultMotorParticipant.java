package org.jactr.fluent.registry;

import java.util.Map;
import java.util.function.Consumer;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.fluent.FluentChunk;
import org.jactr.fluent.FluentChunkType;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.command.translators.AbstractManualTranslator;
import org.jactr.modules.pm.motor.command.translators.PeckRecoilTranslator;
import org.jactr.modules.pm.motor.command.translators.PeckTranslator;
import org.jactr.modules.pm.motor.command.translators.PunchTranslator;
import org.jactr.modules.pm.motor.six.DefaultMotorModule6;

public class DefaultMotorParticipant implements Consumer<IModel>
{

  @Override
  public void accept(IModel model)
  {
    try
    {
      IChunkType command = model.getDeclarativeModule().getChunkType("command")
          .get();

      IChunkType motorCommand = FluentChunkType.from(command)
          .named("motor-command").slot("muscle").encode();

      FluentChunkType.from(motorCommand).named("compound-motor-command")
          .slot("state").encode();

      IChunkType handCommand = FluentChunkType.from(motorCommand)
          .named("hand-command").slot("hand").encode();
      IChunkType fingerCommand = FluentChunkType.from(handCommand)
          .named("finger-command").slot("finger").encode();

      IChunkType constant = FluentChunkType.from(model).named("motor-constant")
          .encode();
      Map<String, IChunk> definedChunks = FluentChunk.from(constant).chunks(
          "right", "left", "index", "middle", "ring", "thumb", "pinkie",
          "mouse", "joystick1", "joystick2", "aborting");

      IChunkType peck = FluentChunkType.from(fingerCommand).named("peck")
          .slots("r", "theta").encode();
      FluentChunkType.from(peck).named("peck-recoil").encode();

      FluentChunkType.from(fingerCommand).named("punch").encode();
      FluentChunkType.from(handCommand).named("point-hand-at-key")
          .slot("to-key").encode();
      FluentChunkType.from(motorCommand).named("press-key").slot("key")
          .encode();
      FluentChunkType.from(motorCommand).named("click-mouse").encode();
      FluentChunkType.from(handCommand).named("hand-to-mouse")
          .slot("hand", definedChunks.get("right")).encode();
      FluentChunkType.from(handCommand).named("hand-to-home")
          .slot("hand", definedChunks.get("right")).encode();
      FluentChunkType.from(motorCommand).named("move-cursor")
          .slots("object", "loc", "device").encode();
      FluentChunkType
          .from(model.getDeclarativeModule().getChunkType("clear").get())
          .slot("muscle").encode();

      /*
       * do some parameter setting since this also installs default handlers
       */
      DefaultMotorModule6 motorModule = (DefaultMotorModule6) model
          .getModule(DefaultMotorModule6.class);
      motorModule.setParameter(IAsynchronousModule.STRICT_SYNCHRONIZATION_PARAM,
          "true");
      motorModule.setParameter(
          AbstractMotorModule.ENABLE_PARALLEL_MUSCLES_PARAM, "false");
      motorModule.setParameter(AbstractManualTranslator.MINIMUM_FITTS_TIME,
          "0.1");
      motorModule.setParameter(AbstractManualTranslator.MINIMUM_MOVEMENT_TIME,
          "0.05");
      motorModule.setParameter(AbstractManualTranslator.PECK_FITTS_COEFFICIENT,
          "0.075");
      motorModule.setParameter(PunchTranslator.class.getName(), "true");
      motorModule.setParameter(PeckTranslator.class.getName(), "true");
      motorModule.setParameter(PeckRecoilTranslator.class.getName(), "true");
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
