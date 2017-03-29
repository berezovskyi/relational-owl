package de.hhu.cs.dbs.interfaces;

/**
 * <code>Database</code> represents virtual RDBMS database that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.util.ArrayList;

public interface Database {

	/**
	 * Get database name
	 * @return Database name
	 */
	public abstract String name();

	/**
	 * Set database name
	 * @param name Database name
	 */
	public abstract void setName(String name);

	/**
	 * Get {@link Table} corresponding to given name
	 * @param name Table name
	 * @return  {@link Table} corresponding to name
	 */
	public abstract Table getTable(String name);
	
	/**
	 * Add table to this database
	 * @param table Table to add to this database
	 */
	public abstract void addTable(Table table);
	
	/**
	 * Get list of all tables in this database as  {@link Table}  elements
	 * @return  List of all tables in this database as  {@link Table}  elements
	 * @uml.property  name="tables"
	 */
	public abstract ArrayList getTables();
	
	/**
	 * Set tables occouring in this database
	 * @param tables  List of all tables as  {@link Table}  elements that should occour  in this database
	 * @uml.property  name="tables"
	 */
	public abstract void setTables(ArrayList tables);
	
	/**
	 * @return SQL-Statement that creates this database in RDBMS
	 */
	public abstract String createSQL();

	/**
	 * @return SQL-Statement that checks wheather this database occours in RDBMS
	 */
	public abstract String checkIfExistsSQL();
	
	/**
	 * @param check on/off constraint checking in RDBMS
	 * @return SQL-Statement for switching on/off constraints check in RDBMS
	 */
	public abstract String checkConstraintsSQL(boolean check);

}