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
public interface IMutableDataSource extends IDataSource
{

  public void setValueAt(long row, long column, Object value);

  public void setRowAt(long row, Collection rowData);

  public void setColumnAt(long column, Collection columnData);

  /**
   * @return column index
   */
  public long addColumn();

  /**
   * @param rowData
   *          data to be added, size must equal getNumberOfColumns(), if null,
   *          an empty row is added
   * @return row index
   */
  public long addRow(Collection rowData);

  /**
   * remove a row
   * 
   * @param row
   *          removed data
   * @return
   */
  public Collection removeRow(long row);

  public Collection removeColumn(long column);
}