package org.jactr.modules.pm.motor.command;

/*
 * default logging
 */
import javax.naming.OperationNotSupportedException;

import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.modalities.motor.MovementCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.delta.DeltaTracker;
import org.jactr.core.model.IModel;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * interface that handles the transformation of {@link ChunkPattern}s into
 * {@link IEfferentCommand}s
 * 
 * @author harrison
 */
public interface ICommandTranslator
{

  /**
   * translate a string name into an {@link IEfferentObject} that represents the
   * muscle. This is called during a buffer query that has been scoped on a
   * specific muscle group
   * 
   * @param muscleName
   * @param model
   * @return
   * @throws IllegalArgumentException
   *           if no muscle is found
   */
  public IEfferentObject getMuscle(String muscleName, IModel model)
      throws IllegalArgumentException;

  /**
   * translates a set of slot values into a {@link IEfferentObject} that
   * represents a muscle defined within the pattern. Since many ACT-R movement
   * commands use multiple slots to define a muscle, this collapses them. (i.e.
   * translates hand right finger index into right-index). In addition to the
   * returned {@link IEfferentObject} this method should also ensure that the
   * slots used to define the muscle are nulled out and the muscle slot is
   * specified.
   * 
   * @param request
   * @param model
   * @return
   * @throws IllegalArgumentException
   */
  public IEfferentObject getMuscle(ChunkTypeRequest request, IModel model)
      throws IllegalArgumentException;

  /**
   * translate a {@link ChunkPattern} into an appropriate
   * {@link IEfferentCommand}. By setting the
   * {@link MovementCommand#MOVEMENT_RATE}, the translator may provide a hint to
   * common reality regarding the actual execution time of the movement.
   * 
   * @param request
   * @param muscle
   * @param model
   * @return
   * @throws IllegalArgumentException
   */
  public IEfferentCommand translate(ChunkTypeRequest request,
      IEfferentObject muscle, IModel model) throws IllegalArgumentException;

  /**
   * adjust a command midflight
   */
  public void adjust(ChunkTypeRequest request, DeltaTracker tracker,
      IEfferentCommand command, IModel model) throws IllegalArgumentException,
      OperationNotSupportedException;
}
