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

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public interface IConditionalSlot extends IMutableSlot
{
  public final static int EQUALS              = 0;

  public final static int LESS_THAN           = 1;

  public final static int LESS_THAN_EQUALS    = 2;

  public final static int GREATER_THAN        = 3;

  public final static int GREATER_THAN_EQUALS = 4;

  public final static int WITHIN              = 5;

  public final static int NOT_EQUALS          = 6;

  public int getCondition();

  public void setCondition(int condition);

  /**
   * test the value to see if it satisfies the condition specified by this
   * conditional slot
   * 
   * @param value
   * @return
   */
  public boolean matchesCondition(Object value);

}
