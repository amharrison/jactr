package org.jactr.tools.masterslave.master;

/*
 * default logging
 */
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultSlotAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.action.IllegalActionStateException;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.tools.masterslave.slave.SlaveStateCondition;

/**
 * load a model but dont run it yet. The action slots 'model' and 'alias' must
 * be defined
 * 
 * @author harrison
 */
public class LoadModelAction extends DefaultSlotAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(LoadModelAction.class);

  static public final String         ALIAS_SLOT = SlaveStateCondition.ALIAS_SLOT;

  static public final String         MODEL_SLOT = "model";
  
  private final String _alias;
  private final URL _location;

  public LoadModelAction()
  {
     _alias = null;
     _location = null;
  }
  
  
  private LoadModelAction(VariableBindings bindings,
      IUniqueSlotContainer slotContainer) throws CannotInstantiateException
  {
    super(bindings, slotContainer.getSlots());
    
    ISlot slot = getSlot(ALIAS_SLOT);
    if(slot==null || slot.getValue()==null)
      throw new CannotInstantiateException("slave model alias must be defined");
    
    _alias = slot.getValue().toString();
    
    slot = getSlot(MODEL_SLOT);
    if(slot==null || slot.getValue()==null)
      throw new CannotInstantiateException("slave model location must be defined");
    
    _location = getClass().getClassLoader().getResource(slot.getValue().toString());
    if (_location == null)
      throw new IllegalActionStateException("could not find model at "+slot.getValue().toString());
  }

  /**
   * 
   */
  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    /*
     * first, do we have an alias slot?
     */
    ISlot slot = getSlot(ALIAS_SLOT);
    if(slot==null || slot.getValue()==null)
      throw new CannotInstantiateException("slave model alias must be defined "+getSlots());
    
    slot = getSlot(MODEL_SLOT);
    if(slot==null || slot.getValue()==null)
      throw new CannotInstantiateException("slave model location must be defined");
    
    return new LoadModelAction(variableBindings, getSlotContainer());
  }

  @Override
  public void dispose()
  {

  }

  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
   
    // shouldn't ever happen, but better safe than sorry
    if (_alias == null || _location == null) return 0;

    try
    {
      MasterExtension me = MasterExtension.getMaster(instantiation.getModel());
      me.loadModelAs(_location, _alias);
    }
    catch (Exception e)
    {
      throw new IllegalActionStateException(String.format(
          "Could not load %s as %s", _location, _alias), e);
    }

    return 0;
  }

}
