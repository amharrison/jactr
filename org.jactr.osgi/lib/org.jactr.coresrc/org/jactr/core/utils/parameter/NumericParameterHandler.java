/*
 * Created on Jan 21, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.utils.parameter;


/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class NumericParameterHandler extends ParameterHandler<Number>
{

  /**
   * Coerce some value into a number
   * 
   * @return value iff value isa Number Double.valueOf(value) iff value isa
   *         String Double.NaN otherwise
   */
  public Number coerce(String value)
  {
    try
    {
      return Double.valueOf(value);
    }
    catch(NumberFormatException nfe)
    {
      throw new ParameterException("Could not convert "+value+" to number",nfe);
    }
  }
}