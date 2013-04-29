package org.jactr.tools.masterslave.master;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
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
public class StopModelAction extends DefaultSlotAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(StopModelAction.class);

  static public final String         ALIAS_SLOT = SlaveStateCondition.ALIAS_SLOT;
  
  private final String _alias;

  public StopModelAction()
  {
    _alias = null;
  }
  
  private StopModelAction(VariableBindings bindings,
      IUniqueSlotContainer slotContainer) throws CannotInstantiateException
  {
    super(bindings, slotContainer.getSlots());
    
    ISlot slot = getSlot(ALIAS_SLOT);
    if(slot==null || slot.getValue()==null)
      throw new CannotInstantiateException("slave model alias must be defined");
    
    _alias = slot.getValue().toString();
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
    
    return new StopModelAction(variableBindings, getSlotContainer());
  }

  @Override
  public void dispose()
  {

  }

  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    

    MasterExtension me = MasterExtension.getMaster(instantiation.getModel());
    IModel model = me.getSlaveModel(_alias);
    
    if (model == null)
      throw new IllegalActionStateException(
          "StopModelAction could not find loaded model " + _alias);
    try
    {
      me.stopModel(model);
    }
    catch (Exception e)
    {
      throw new IllegalActionStateException(String.format("Could not stop %s",
          _alias), e);
    }

    return 0;
  }

}
