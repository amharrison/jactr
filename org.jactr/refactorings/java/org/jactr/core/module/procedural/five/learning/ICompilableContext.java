package org.jactr.core.module.procedural.five.learning;

/*
 * default logging
 */

public interface ICompilableContext
{
  /**
   * return true if operations on this buffer are immediate, in 
   * that they will complete by the time the production has finished
   * firing (no longer duration requests like the visual buffer)
   * @return
   */
  public boolean isImmediate();
  
  /**
   * subsequent requests may result in a buffer jam (and abort/error)
   * @return
   */
  public boolean isJammable();
  
  /**
   * if the buffer requests/actions can be compiled out entirely (i.e.
   * the retrieval buffer)
   * @return
   */
  public boolean canCompileOut();
}
