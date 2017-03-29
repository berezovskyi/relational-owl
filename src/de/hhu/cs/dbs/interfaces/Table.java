package de.hhu.cs.dbs.interfaces;

/**
 * <code>Table</code> represents virtual RDBMS table that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.util.ArrayList;

public interface Table {

	/**
	 * Get table name
	 * @return Table name
	 */
	public abstract String name();

	/**
	 * Set table name
	 * @param name Table name
	 */
	public abstract void setName(String name);
	
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
	 * Get {@link Column} with a given name
	 * @param name Column name 
	 * @return {@link Column} with a given name
	 */
	public abstract Column getColumn(String name);

	/**
	 * Add column
	 * @param column Column
	 */
	public abstract void addColumn(Column column);

	/**
	 * Get primary key
	 * @return {@link PrimaryKey} primary key
	 */
	public abstract PrimaryKey primaryKey();

	/**
	 * Set primary key
	 * @param primaryKey {@link PrimaryKey} primary key
	 */
	public abstract void setPrimaryKey(PrimaryKey primaryKey);

	/**
	 * Get list of all columns in this table
	 * @return List of all columns in this table as {@link Column} elements
	 */
	public abstract ArrayList columns();
	
	/**
	 * Set columns for this table
	 * @param columns List of all columns in this table as {@link Column} elements
	 */
	public abstract void setColumns(ArrayList columns);
	
	/**
	 * @return SQL-Statement that creates this table in RDBMS
	 */
	public abstract String createSQL();
	
	/**
	 * @return SQL-Statement that checks wheather this table occours in RDBMS
	 * in connected database
	 */
	public abstract String checkIfExistsSQL();
	
	/**
	 * @return SQL-Statement that checks wheather any foreign keys existing 
	 * for this table
	 */
	public abstract String checkIfExistsForeignkeySQL();
		
	/**
	 * @return SQL-Statement that creates foreign key for this table
	 */
	public abstract String createForeignKeySQL();

	/**
	 * Create SQL Statement which receives all data from the given table
	 */
	public abstract String getAllDataSQL();
	
	/**
	 * Create SQL Statement which removes all data from the given table
	 */
	public abstract String deleteAllDataSQL();
	
	/**
	 * Create SQL Statement which removes all data from the given table
	 */
	public abstract String dropTableSQL();

}