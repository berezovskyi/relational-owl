package de.hhu.cs.dbs.interfaces;

/**
 * <code>PrimaryKey</code> represents virtual RDBMS primary key that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.util.ArrayList;

public interface PrimaryKey {

	/**
	 * Add column to primary key
	 * @param column {@link Column}
	 */
	public abstract void addColumn(Column column);

	/**
	 * Get all Columns as  {@link Column}  elements
	 * @return  List of all columns as  {@link Column}  elements
	 * @uml.property  name="columns"
	 */
	public abstract ArrayList getColumns();

	/**
	 * Set columns that belong to this primary key
	 * @param columns  List of all columns as  {@link Column}  elements
	 * @uml.property  name="columns"
	 */
	public abstract void setColumns(ArrayList columns);

}