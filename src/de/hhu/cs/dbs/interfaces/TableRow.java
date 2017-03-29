package de.hhu.cs.dbs.interfaces;

/**
 * <code>TableRow</code> represents virtual RDBMS table row that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.util.ArrayList;

public interface TableRow {

	/**
	 * Add column
	 * @param column Column
	 */
	public void addColumn(Column column);

	/**
	 * Get table name
	 * @return Table name
	 */
	public String table();
	
	/**
	 * Set table name
	 * @param name Table name
	 */
	public void setTable(String name);

	/**
	 * Get database name
	 * @return Database name
	 */
	public abstract String database();

	/**
	 * Set database name
	 * @param name Database name
	 */
	public abstract void setDatabase(String name);

	/**
	 * Get list of all columns in this table row
	 * @return List of all columns in this table row as {@link Column} elements
	 */
	public ArrayList columns();

	/**
	 * Set columns for this table
	 * @param columns List of all columns in this table as {@link Column} elements
	 */
	public void setColumns(ArrayList columns);

	/**
	 * @return SQL-Statement that creates this table in RDBMS
	 */
	public String createSQL();

	/**
	 * @return SQL-Statement that checks wheather this table occours in RDBMS
	 * in connected database
	 */
	public String checkIfExistsSQL();
	
	/**
	 * @return SQL-Statement that checks wheather in RDBMS exists the row with
	 * conditions for columns given with parameter columns
	 * @param columns conditions for columns
	 */
	public String checkIfExistsSQL(ArrayList columns);
	
	/**
	 * @return SQL-Statement that deletes this row from RDBMS
	 */
	public String deleteSQL();
	
	/**
	 * @return SQL-Statement that deletes this row from RDBMS with
	 * conditions for columns given with parameter columns
	 * @param columns conditions for columns
	 */
	public String deleteSQL(ArrayList columns);
	
}