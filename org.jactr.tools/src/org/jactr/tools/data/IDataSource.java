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
public interface IDataSource
{

  public long getNumberOfRows();

  public long getNumberOfColumns();

  public Object getValueAt(long row, long column);

  public Collection getRowAt(long row);

  public Collection getColumnAt(long column);
}