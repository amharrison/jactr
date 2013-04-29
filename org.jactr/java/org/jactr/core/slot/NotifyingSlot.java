/*
 * Created on Sep 21, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.slot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * slot that is aware of its parent container and can pass on change
 * notification s
 */
public class NotifyingSlot extends DefaultMutableSlot
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(NotifyingSlot.class);

  private INotifyingSlotContainer    _container;

  public NotifyingSlot(String name, Object value,
      INotifyingSlotContainer container)
  {
    super(name, value);
    setContainer(container);
  }

  /**
   * @param slot
   */
  public NotifyingSlot(ISlot slot, INotifyingSlotContainer container)
  {
    super(slot);
    setContainer(container);
  }

  private NotifyingSlot(NotifyingSlot slot)
  {
    super(slot);
    setContainer(slot.getContainer());
  }

  protected void setContainer(INotifyingSlotContainer container)
  {
    _container = container;
  }

  public INotifyingSlotContainer getContainer()
  {
    return _container;
  }

  @Override
  public void setValue(Object newValue)
  {
    Object oldValue = getValue();
    boolean changed = oldValue != null && !oldValue.equals(newValue)
        || newValue != null && !newValue.equals(oldValue);

    if (changed)
    {
      super.setValue(newValue);
      try
      {
        if (_container != null)
          _container.valueChanged(this, oldValue, newValue);
      }
      catch (Exception e)
      {
        // roll back
        if (LOGGER.isWarnEnabled())
          LOGGER
              .warn(
                  String
                      .format(
                          "Change of %s=%s to %s resulted in an exception (%s), rolling back",
                          getName(), oldValue, newValue, e.getMessage()), e);
        super.setValue(oldValue);
      }
    }
  }

  @Override
  public NotifyingSlot clone()
  {
    return new NotifyingSlot(this);
  }
}
