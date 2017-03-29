package de.hhu.cs.dbs.interfaces;

/**
 * <code>DbManager</code> manage RDBMS databases, tables, columns and 
 * primary and foreign keys objects as well as its JDBC-Connection and 
 * instance data.
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public interface DbManager {

	/**
	 * @return actual {@link Connection} to RDBMS 
	 */
	public abstract Connection getConnection();
	
	/**
	 * @param schema Schema name
	 * @return actual {@link java.sql.Connection} to given database in RDBMS
	 */
	public abstract Connection getConnection(String schema);

	/**
	 * @return List of all databases as {@link Database} in managed RDBMS
	 */
	public abstract ArrayList getDatabases();
	
	/**
	 * @param name Database name
	 * @return {@link Database} with the given name
	 */
	public abstract Database getDatabase(String name);
	
	/**
	 * @return List of all databases as <code>String</code>'s in managed RDBMS
	 */
	public abstract ArrayList getDatabaseList();

	/**
	 * @param database Database name
	 * @return List of all tables as {@link Table} from database with given name 
	 */
	public abstract ArrayList getTables(String database);
	
	/**
	 * @param database Database name
	 * @return List of all tables as <code>String</code>'s in given database
	 */
	public abstract ArrayList getTableList(String database);

	/**
	 * @param database 	Database name
	 * @param table 	Table name
	 * @return List of all columns as {@link Column} from given table
	 * in given database
	 */
	public abstract ArrayList getColumns(String database, String table);

	/**
	 * @param sql SQL select statement to get data from RDBMS
	 * @return {@link java.sql.ResultSet} with data
	 * @throws Exception
	 */
	public abstract ResultSet getData(String sql) throws Exception;

	/**
	 * Drop foreign keys for table with the given name in given database
	 * @param database Database name
	 * @param table Table name
	 */
	public abstract void dropForeignKeys(String database, String table);
	
	
}