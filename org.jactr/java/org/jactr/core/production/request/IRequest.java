package org.jactr.core.production.request;

import org.jactr.core.chunk.IChunk;

/*
 * default logging
 */

import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;

/**
 * basic foundation of a module request. Request can be cloned and then
 * bound and resolved against a variable mapping.
 * @author harrison
 *
 */
public interface IRequest extends Cloneable
{

  /**
   * duplicate this IRequest so that the clone can
   * be resolved against since the process is destructive
   * 
   * @return deep copy
   */
  public IRequest clone();
  
  
  /**
   * attempt to resolve and bind any variables within this
   * request. This method can be called iteratively and is permitted
   * to make perminent changes to the request since this should be a clone (or
   * transient). <br>
   * bind will attempt to resolve and bind any variables it contains against
   * those in the bindings. If it has any unresolved bindings and this is an
   * iterative call, it can return the unresolved count. If it is not an iterativeCall,
   * it cannot match and the exception is thrown.<br>
   * If the request determines that it cannot match at anytime, it should throw
   * the cannot match exception.
   * 
   * @param bindings
   * @param iterativeCall
   * @return number of unresolved variables in this request
   * @throws CannotMatchException
   */
  public int bind(IModel model, VariableBindings bindings, boolean iterativeCall) throws CannotMatchException;
  
  /**
   * non-destructive check if the chunk matches this request
   * 
   * @param reference
   * @return
   */
  public boolean matches(IChunk reference);

}
