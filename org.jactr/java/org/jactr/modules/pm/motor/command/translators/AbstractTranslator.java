package org.jactr.modules.pm.motor.command.translators;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.efferent.IEfferentCommandTemplate;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.motor.MotorUtilities;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.delta.DeltaTracker;
import org.commonreality.object.manager.IEfferentObjectManager;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.motor.command.ICommandTranslatorDelegate;

public abstract class AbstractTranslator implements ICommandTranslatorDelegate
{

  private Map<IIdentifier, IEfferentObject> _muscleCache;
  private Map<String, IIdentifier> _muscleNameCache;

  public AbstractTranslator()
  {
    _muscleCache = new HashMap<IIdentifier, IEfferentObject>();
    _muscleNameCache = new HashMap<String, IIdentifier>();
  }
  
  protected Collection<String> getCachedMuscleNames()
  {
    return new ArrayList<String>(_muscleNameCache.keySet());
  }

  /**
   * translate a string name into an {@link IEfferentObject} that represents the
   * muscle. This is called during a buffer query that has been scoped on a
   * specific muscle group
   * 
   * @param muscleName
   * @param model
   * @return
   * @throws IllegalArgumentException
   *             if no muscle is found
   */
  public IEfferentObject getMuscle(String muscleName, IModel model)
      throws IllegalArgumentException
  {
    IEfferentObjectManager manager = ACTRRuntime.getRuntime().getConnector()
        .getAgent(model).getEfferentObjectManager();
    muscleName = muscleName.toLowerCase();
  
    if (_muscleNameCache.containsKey(muscleName))
      return _muscleCache.get(_muscleNameCache.get(muscleName));
  
    for (IIdentifier mid : manager.getIdentifiers())
    {
      IEfferentObject muscle = manager.get(mid);
      if (MotorUtilities.isMotor(muscle)
          && muscleName.equalsIgnoreCase(MotorUtilities.getName(muscle)))
      {
        _muscleNameCache.put(muscleName, mid);
        _muscleCache.put(mid, muscle);
        return muscle;
      }
    }
  
    throw new IllegalArgumentException("Could not find a muscle named "
        + muscleName);
  }

  @SuppressWarnings("unchecked")
  protected IEfferentCommandTemplate getTemplateNamed(String templateName, IEfferentObject muscle)
      throws IllegalArgumentException
  {
    for(IEfferentCommandTemplate template : muscle.getCommandTemplates())
      if(templateName.equalsIgnoreCase(template.getName()))
        return template;
    
    throw new IllegalArgumentException("No template named "+templateName+" could be found for muscle "+muscle.getIdentifier());
  }

  public void adjust(ChunkTypeRequest request, DeltaTracker tracker,
      IEfferentCommand command, IModel model) throws IllegalArgumentException,
      OperationNotSupportedException
  {
    throw new OperationNotSupportedException(
        "Midflight adjustments not supported");
  }

}