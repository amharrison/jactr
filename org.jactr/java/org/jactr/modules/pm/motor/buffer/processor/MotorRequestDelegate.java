package org.jactr.modules.pm.motor.buffer.processor;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.SimpleRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.command.IMovement;

public class MotorRequestDelegate extends SimpleRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER     = LogFactory
      .getLog(MotorRequestDelegate.class);

  static public final String         ADJUSTMENT = ":adjustment";

  private IMotorModule               _module;

  public MotorRequestDelegate(IChunkType chunkType, IMotorModule motor)
  {
    super(chunkType);
    _module = motor;
  }

  public boolean request(IRequest request, IActivationBuffer buffer,
      double requestTime)
  {
    if (isAdjustment((ChunkTypeRequest) request))
    {
      IMovement movement = AbortRequestDelegate.getMovement(
          (ChunkTypeRequest) request, (AbstractMotorModule) _module);
      String msg = null;

      if (movement != null)
      {
        msg = String.format("Adjusting %s with %s", movement, request);
        _module.adjust(movement, (ChunkTypeRequest) request, requestTime);
      }
      else
        msg = String.format("No executing movement found matching %s, ignoring",
            request);

      IModel model = _module.getModel();
      if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.MOTOR, msg);
    }
    else
      _module.prepare((ChunkTypeRequest) request, requestTime, false);

    return true;
  }

  /**
   * request is immediate
   */
  public void clear()
  {

  }

  private boolean isAdjustment(ChunkTypeRequest request)
  {
    List<ISlot> container = FastListFactory.newInstance();
    request.getSlots(container);
    try
    {
      for (ISlot slot : container)
        if (slot.getName().equals(ADJUSTMENT)) return true;
      return false;
    }
    finally
    {
      FastListFactory.recycle(container);
    }
  }
}
