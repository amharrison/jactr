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
@Deprecated
public interface INamedMutableDataSource extends
    IMutableDataSource,
    INamedDataSource
{

  public void setValueAt(long row, String columnName, Object value);

  public void setColumnAt(String columnName, Collection columnData);

  public long addColumn(String columnName);

  public Collection removeColumn(String columnName);
}