package org.jactr.modules.pm.motor.buffer.six;

/*
 * default logging
 */
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IEfferentObject;
import org.jactr.core.buffer.delegate.AddChunkTypeRequestDelegate;
import org.jactr.core.buffer.delegate.ExpandChunkRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.slot.ProxyUniqueSlotContainer;
import org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.buffer.IMotorActivationBuffer;
import org.jactr.modules.pm.motor.buffer.processor.AbortRequestDelegate;
import org.jactr.modules.pm.motor.buffer.processor.ClearRequestDelegate;
import org.jactr.modules.pm.motor.buffer.processor.MotorRequestDelegate;
import org.jactr.modules.pm.motor.managers.MuscleState;

public class DefaultMotorActivationBuffer6 extends AbstractPMActivationBuffer6
    implements IMotorActivationBuffer
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultMotorActivationBuffer6.class);

  public DefaultMotorActivationBuffer6(String name, AbstractMotorModule module)
  {
    super(name, module);
  }

  /**
   * we dont permit any chunks in the motor buffer, so always return false
   * 
   * @see org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6#isValidChunkType(org.jactr.core.chunktype.IChunkType)
   */
  @Override
  protected boolean isValidChunkType(IChunkType chunkType)
  {
    return chunkType.isA(((AbstractMotorModule) getModule())
        .getCompoundCommandChunkType());
  }

  @Override
  public void initialize()
  {
    super.initialize();
    IMotorModule module = (IMotorModule) getModule();
    try
    {
      /*
       * automatically expand chunks..
       */
      addRequestDelegate(new ExpandChunkRequestDelegate(false));
      /*
       * needs to be first to avoid the command translator tests
       */
      addRequestDelegate(new AddChunkTypeRequestDelegate() {
        @Override
        public boolean willAccept(IRequest request)
        {
          return request instanceof ChunkTypeRequest
              && ((ChunkTypeRequest) request).getChunkType().isA(
                  ((AbstractMotorModule) getModule())
                      .getCompoundCommandChunkType());
        }
      });

      addRequestDelegate(new AbortRequestDelegate(module.getAbortChunkType()));

      addRequestDelegate(new ClearRequestDelegate(module.getModel()
          .getDeclarativeModule().getChunkType("clear").get()));

      addRequestDelegate(new MotorRequestDelegate(
          module.getMovementChunkType(), module));

    }
    catch (Exception e)
    {
      /**
       * Error : error
       */
      throw new IllegalModuleStateException(
          "Could not install required chunk pattern processor ", e);
    }
  }

  @Override
  public int bind(SlotBasedRequest request, Map<String, Object> bindings,
      boolean isIterative) throws CannotMatchException
  {
    AbstractMotorModule motor = (AbstractMotorModule) getModule();
    /**
     * if muslce level parallelism is enabled and this request has a muscle
     * slot, we will create a spoof slot container that will contain all the
     * buffers states plus those of the muscle (overriding state, error, etc)
     */
    String muscleName = null;
    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getName().equalsIgnoreCase(IMotorModule.MUSCLE_SLOT)
          && cSlot.getCondition() == IConditionalSlot.EQUALS)
      {
        muscleName = cSlot.getValue().toString();
        break;
      }

    IUniqueSlotContainer slotContainer = this;

    if (motor.isMuscleParallelismEnabled() || muscleName != null)
      if (muscleName != null)
        try
        {
          IEfferentObject muscle = motor.getCommandTranslator().getMuscle(
              muscleName, getModel());
          MuscleState state = motor.getMuscleManager().getMuscleState(
              muscle.getIdentifier());
          /*
           * we need to duplicate this container and add the muscle state info
           * (which includes the muscle slot)
           */
          slotContainer = new ProxyUniqueSlotContainer(state);
          ((ProxyUniqueSlotContainer) slotContainer).addSlotContainer(this);
        }
        catch (IllegalArgumentException iae)
        {
          throw new CannotMatchException("No muscle named " + muscleName
              + " available.");
        }

    if (muscleName == null) muscleName = getName();

    return request.bind(getModel(), muscleName, slotContainer, bindings,
        isIterative);
  }

}
