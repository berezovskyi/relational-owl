package de.hhu.cs.dbs.impl;

/**
 * <code>DatabaseImpl</code> implements virtual RDBMS database that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see Database
 */

import java.util.ArrayList;
import java.util.Iterator;

import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.Table;

public class DatabaseImpl implements Database {
	
	private String name = "";
	private ArrayList tables = new ArrayList();
	
	public DatabaseImpl(){	
	}
	
	public DatabaseImpl(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsDatabase#getName()
	 */
	public String name() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsDatabase#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsDatabase#getTable(java.lang.String)
	 */
	public Table getTable(String name){
		boolean blnTableExists = false;
		Table t = new TableImpl();
		Iterator iter = tables.iterator();
		
		// get table searched
		while (iter.hasNext()){
			t = (Table)iter.next();
			if (t.name().equals(name)){
				blnTableExists = true;
				break;
			}
		}
		// return table found or ColumnNotExistException 
		//if (!blnTableExists) throw new TableNotFoundException();
		return t;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsDatabase#addTable(de.hhu.cs.dbs.DbsTable)
	 */
	public void addTable(Table t){
		tables.add(t);
	}
	
	public ArrayList getTables() {
		return tables;
	}
	
	public void setTables(ArrayList tables){
		this.tables = tables;
	}	
	
	public String createSQL(){
		return "";
	}
	
	public String checkIfExistsSQL(){
		return "";
	}
	
	public String checkConstraintsSQL(boolean check){
		return "";
	}
}
