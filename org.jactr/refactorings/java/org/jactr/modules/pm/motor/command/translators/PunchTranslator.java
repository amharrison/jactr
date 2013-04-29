package org.jactr.modules.pm.motor.command.translators;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.ICompoundCommand;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.modalities.motor.MotorUtilities;
import org.commonreality.object.IEfferentObject;
import org.commonreality.sensors.keyboard.PressCommand;
import org.commonreality.sensors.keyboard.ReleaseCommand;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.motor.IMotorModule;

public class PunchTranslator extends AbstractManualTranslator
{
  /**
   * Logger definition
   */
  static final transient Log LOGGER = LogFactory.getLog(PunchTranslator.class);

  public boolean handles(ChunkTypeRequest request)
  {
    try
    {
      IChunkType actual = request.getChunkType();
      IChunkType punch = actual.getModel().getDeclarativeModule().getChunkType(
          "punch").get();

      // return actual.isA(punch);
      return actual.equals(punch);
    }
    catch (Exception e)
    {
      /**
       * Error :
       */
      LOGGER.error("Failed to get punch chunk type ", e);
      return false;
    }
  }

  public IEfferentCommand translate(ChunkTypeRequest request,
      IEfferentObject muscle, IModel model) throws IllegalArgumentException
  {
    try
    {
      IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);
      IMotorModule motor = (IMotorModule) model.getModule(IMotorModule.class);
      /*
       * we need a compound, a press and a release
       */
      ICompoundCommand compound = (ICompoundCommand) getTemplateNamed(
          "compound", muscle).instantiate(agent, muscle);

      double[] origin = MotorUtilities.getPosition(muscle);
      double[] target = new double[] { origin[0], origin[1], 0 };
      double[] rate = computeRate(origin, target, getMotorBurstTime(motor));

      PressCommand press = (PressCommand) getTemplateNamed("press", muscle)
          .instantiate(agent, muscle);
      press.press(origin, target, rate);

      ReleaseCommand release = (ReleaseCommand) getTemplateNamed("release",
          muscle).instantiate(agent, muscle);
      release.release(target, origin, rate);

      compound.add(press);
      compound.add(release);

      return compound;
    }
    catch (IllegalArgumentException iae)
    {
      throw iae;
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException("Could not create command for "
          + request + " ", e);
    }
  }



}
