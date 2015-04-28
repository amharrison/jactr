/*
 * Created on Jan 7, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.tools.data;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
@Deprecated
public interface IMetric
{

  public void addDataSource(IDataSource dataSource);

  public void removeDataSource(IDataSource dataSource);

  public Object compute();
}