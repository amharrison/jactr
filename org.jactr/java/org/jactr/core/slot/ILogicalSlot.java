package org.jactr.core.slot;

public interface ILogicalSlot extends ISlot, ISlotContainer {

  public final static int NOT = 0;

  public final static int OR  = 1;

  public final static int AND = 2;

  public int getOperator();

  public void setOperator(int operator);

}
