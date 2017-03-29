package de.hhu.cs.dbs.mysql.impl;

/**
 * <code>MySQLDatabaseImpl</code> implements virtual database in MySQL RDBMS that 
 * can be created from schema representation in OWL as well as from metadata 
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.util.ArrayList;

import de.hhu.cs.dbs.impl.DatabaseImpl;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.mysql.interfaces.MySQLDatabase;

public class MySQLDatabaseImpl implements MySQLDatabase{
	private Database d = new DatabaseImpl();
	
	/**
	 * Creates new MySQLDatabaseImpl
	 */
	public MySQLDatabaseImpl(){}
		
	/**
	 * Creates new MySQLDatabaseImpl wich wraps virtual {@link Database}
	 * @param database Database to wrap
	 */
	public MySQLDatabaseImpl(Database database){
		this.d = database;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#addTable(de.hhu.cs.dbs.interfaces.Table)
	 */
	public void addTable(Table t) {
		d.addTable(t);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#name()
	 */
	public String name() {
		return d.name();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#getTable(java.lang.String)
	 */
	public Table getTable(String name){
		return d.getTable(name);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#setName(java.lang.String)
	 */
	public void setName(String name) {
		d.setName(name);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#getTables()
	 */
	public ArrayList getTables(){
		return d.getTables();
	}	
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#setTables(java.util.ArrayList)
	 */
	public void setTables(ArrayList tables){
		d.setTables(tables);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#createSQL()
	 */
	public String createSQL(){
		String sql = "CREATE DATABASE IF NOT EXISTS " + d.name();
		
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#checkIfExistsSQL()
	 */
	public String checkIfExistsSQL(){
		return "SHOW DATABASES LIKE '" + d.name() + "'";
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#checkConstraintsSQL(boolean)
	 */
	public String checkConstraintsSQL(boolean check){
		String sql = "";
		if (check) sql = "SET FOREIGN_KEY_CHECKS = 1";
		else sql = "SET FOREIGN_KEY_CHECKS = 0";
		
		return sql;
	}

}
