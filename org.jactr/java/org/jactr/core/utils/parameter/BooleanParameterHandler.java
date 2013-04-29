/*
 * Created on Jan 21, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.utils.parameter;


/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BooleanParameterHandler extends ParameterHandler<Boolean>
{

  /**
   * @return Boolean iff value isa Boolean Boolean.FALSE iff value=="null",
   *         "nil", "f", "false" Boolean.TRUE iff value=="true" "t"
   *         Boolean.FALSE otherwise
   */
  public Boolean coerce(String value)
  {
    if(value==null)
      return Boolean.FALSE;
    
    if(value.equalsIgnoreCase("null")||value.equalsIgnoreCase("nil")||value.equalsIgnoreCase("f"))
      return Boolean.FALSE;
    
    if(value.equalsIgnoreCase("t"))
      return Boolean.TRUE;
    
    return Boolean.valueOf(value);
  }

}