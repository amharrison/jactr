/*
 * Created on Jan 21, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.utils.parameter;

import javax.naming.OperationNotSupportedException;

import org.jactr.core.model.IModel;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("unchecked")
public class ACTRParameterHandler extends ParameterHandler
{

  IModel _model;
  
  public ACTRParameterHandler(IModel model)
  {
    _model = model;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.jactr.core.utils.ParameterHandler#coerce(java.lang.Object)
   */
  public Object coerce(String value)
  { 
    if(_model==null)
     throw new ParameterException("Cannot coerce "+value+" without IModel", new OperationNotSupportedException("Must used coerce(String, IModel) instead"));
    
    return coerce(value, _model);
  }
  
  public Object coerce(String value, IModel model)
  {
    if(value==null) return null;
    
    try
    {
      Object rtn = null;
      
      rtn = model.getDeclarativeModule().getChunk(value).get();
      
      if(rtn==null)
        rtn = model.getDeclarativeModule().getChunkType(value).get();
      
      if(rtn==null)
        rtn = model.getProceduralModule().getProduction(value).get();
      
      if(rtn==null)
        rtn = model.getActivationBuffer(value);
      
      return rtn;
    }
    catch(Exception e)
    {
      throw new ParameterException("Could not coerce "+value+" into a theoretical object",e);
    }
  }

}