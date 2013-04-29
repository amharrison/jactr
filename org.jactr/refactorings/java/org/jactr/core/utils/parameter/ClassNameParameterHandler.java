/*
 * Created on Jan 21, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.utils.parameter;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("unchecked")
public class ClassNameParameterHandler extends ParameterHandler<Class>
{

  /*
   * (non-Javadoc)
   * 
   * @see org.jactr.core.utils.ParameterHandler#coerce(java.lang.Object)
   */
  public Class coerce(String value)
  {
    try
    {
      return getClass().getClassLoader().loadClass(value);
    }
    catch (Exception e)
    {
      throw new ParameterException("Could not coerve " + value
          + " into a class", e);
    }
  }

  public String toString(Class classSpec)
  {
    if (classSpec != null) return classSpec.getName();
    return null;
  }

}