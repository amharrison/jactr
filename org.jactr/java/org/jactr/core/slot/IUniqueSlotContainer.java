/*
 * Created on Sep 25, 2003 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jactr.core.slot;

/**
 * Specifies that this ISlotContainer has one and only one slot of any given
 * name To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IUniqueSlotContainer extends ISlotContainer
{

  /**
   * return the actual named slot. this is the backing slot, changes to it are
   * propogated throughout the model
   * 
   * @param slotName
   * @return
   */
  public ISlot getSlot(String slotName);
  
  
  public boolean hasSlot(String slotName);
}