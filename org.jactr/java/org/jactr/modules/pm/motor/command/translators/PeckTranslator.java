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
import org.commonreality.modalities.motor.TranslateCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.sensors.keyboard.PressCommand;
import org.commonreality.sensors.keyboard.ReleaseCommand;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.motor.IMotorModule;

public class PeckTranslator extends AbstractManualTranslator
{
  /**
   * Logger definition
   */
  static final transient Log LOGGER = LogFactory.getLog(PeckTranslator.class);

  public boolean handles(ChunkTypeRequest request)
  {
    try
    {
      IChunkType actual = request.getChunkType();
      IChunkType punch = actual.getModel().getDeclarativeModule().getChunkType(
          "peck").get();

//      return actual.isA(punch);
      return actual.equals(punch);
    }
    catch (Exception e)
    {
      /**
       * Error :
       */
      LOGGER.error("Failed to get peck chunk type ", e);
      return false;
    }
  }



  protected double[] getTarget(ChunkTypeRequest request, IEfferentObject muscle)
  {
    double[] origin = MotorUtilities.getPosition(muscle);
    double distance = Double.NaN;
    double theta = Double.NaN;

    for (ISlot slot : request.getSlots())
      if (slot.getName().equalsIgnoreCase("r"))
        distance = ((Number) slot.getValue()).doubleValue();
      else if (slot.getName().equalsIgnoreCase("theta"))
        theta = ((Number) slot.getValue()).doubleValue();

    if (Double.isNaN(distance) || Double.isNaN(theta))
      throw new IllegalArgumentException(
          "Both theta and r must be defined when computing finger trajectories");

    double dX = Math.cos(theta) * distance;
    double dY = Math.sin(theta) * distance;

    double[] target = new double[origin.length];
    target[0] = origin[0] + dX;
    target[1] = origin[1] + dY;
    return target;
  }
  
  protected double computeDistance(double[] origin, double[] target)
  {
    double rtn = 0;
    for(int i=0;i<origin.length;i++)
      rtn += Math.abs(origin[i]-target[i])*Math.abs(origin[i]-target[i]);
    
    return Math.sqrt(rtn);
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

      TranslateCommand translate = (TranslateCommand) getTemplateNamed("translate", muscle)
          .instantiate(agent, muscle);

      double[] origin = MotorUtilities.getPosition(muscle);

      double[] target = getTarget(request, muscle);
      double[] peckRate = computeRate(origin, target, computeFitts(
          getPeckFittsCoefficient(motor), computeDistance(origin, target), 1));
      
      translate.translate(origin, target, peckRate);

      double[] punchTarget = new double[] { target[0], target[1], 0 };

      /*
       * all the time for a peck is eaten up by the movement, not the punch..
       * silly, but this is ACT-R cannonical
       */
      double[] punchRate = computeRate(target, punchTarget, 0.000001);

      PressCommand press = (PressCommand) getTemplateNamed("press", muscle)
          .instantiate(agent, muscle);
      press.press(target, punchTarget, punchRate);

      ReleaseCommand release = (ReleaseCommand) getTemplateNamed("release",
          muscle).instantiate(agent, muscle);
      release.release(punchTarget, target, punchRate);

      compound.add(translate);
      compound.add(press);
      compound.add(release);

      return compound;
    }
    catch(IllegalArgumentException iae)
    {
      throw iae;
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException("Could not create command for "+request+" ",e);
    }
  }
}
