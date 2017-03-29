package de.hhu.cs.dbs.db2.impl;

/**
 * <code>DB2DatabaseImpl</code> implements virtual database in DB2 RDBMS that 
 * can be created from schema representation in OWL as well as from metadata 
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see Database
 * @see DB2Database
 */

import de.hhu.cs.dbs.db2.interfaces.DB2Database;
import de.hhu.cs.dbs.impl.DatabaseImpl;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.Table;
import java.util.ArrayList;

/**
 * @author  Administrator
 */
public class DB2DatabaseImpl implements DB2Database{
	private Database d = new DatabaseImpl();
	
	/**
	 * Creates new DB2ColumnImpl
	 */
	public DB2DatabaseImpl(){}
		
	/**
	 * Creates new DB2DatabaseImpl wich wraps virtual {@link Database}
	 * @param database database to wrap
	 */
	public DB2DatabaseImpl(Database database){
		this.d = database;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#name()
	 */
	public String name() {
		return d.name();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#setName(java.lang.String)
	 */
	public void setName(String name) {
		d.setName(name);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#getTable(java.lang.String)
	 */
	public Table getTable(String name){
		return d.getTable(name);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#addTable(de.hhu.cs.dbs.interfaces.Table)
	 */
	public void addTable(Table table) {
		d.addTable(table);
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
		String sql = "CREATE SCHEMA " + d.name();
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#checkIfExistsSQL()
	 */
	public String checkIfExistsSQL(){
		String sql = "SELECT COUNT(*) FROM SYSIBM.SYSSCHEMATA WHERE NAME = '" + d.name() + "'";
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Database#checkConstraintsSQL(boolean)
	 */
	public String checkConstraintsSQL(boolean check){
		String sql = "";
		return sql;
	}
}
