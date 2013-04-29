/*
 * Created on Jan 7, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.tools.data;

import java.util.Collection;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface INamedDataSource extends IDataSource
{

  public long getIndexOfName(String columnName);

  public String getNameOfIndex(long index);

  public Object getValueAt(long row, String columnName);

  public Collection getColumnAt(String columnName);
}