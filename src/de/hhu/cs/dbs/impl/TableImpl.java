package de.hhu.cs.dbs.impl;

/**
 * <code>TableImpl</code> implements virtual RDBMS table that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * 
 */

import java.util.ArrayList;
import java.util.Iterator;

import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.PrimaryKey;
import de.hhu.cs.dbs.interfaces.Table;

public class TableImpl implements Table {

	private String name = "";
	private String databaseName = "";
	private ArrayList columns = new ArrayList();
	private PrimaryKey primaryKey = new PrimaryKeyImpl();
	
	public TableImpl(){
	}

	public TableImpl(String name){
		this.name = name;
	}
	
	public TableImpl(Table table){
		this.name = table.name();
		this.databaseName = table.database();
		this.columns = table.columns();
		this.primaryKey = table.primaryKey();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsTable#getName()
	 */
	public String name() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsTable#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsTable#getColumn(java.lang.String)
	 */
	public Column getColumn(String name){
		boolean blnColExists = false;
		Column c = null;
		Iterator iter = columns.listIterator();
		
		// get column searched
		while (iter.hasNext()){
			c = (Column)iter.next();
			if (c.name().equals(name)){
				blnColExists = true;
				break;
			}
		}
		// return column found or ColumnNotExistException 
		//if (!blnColExists) throw new ColumnNotFoundException();
		return c;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsTable#addColumn(de.hhu.cs.dbs.Column)
	 */
	public void addColumn(Column c){
		if (!columns.contains(c)){
			columns.add(c);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsTable#getPrimaryKey()
	 */
	public PrimaryKey primaryKey() {
		return primaryKey;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsTable#setPrimaryKey(de.hhu.cs.dbs.PrimaryKey)
	 */
	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
		//change isPrimaryKey flag on columns that contains primaryKey
		Iterator iter = primaryKey.getColumns().iterator();
		while (iter.hasNext()){
			Column pkColumn = (Column)iter.next();
			Iterator iter2 = columns().iterator();
			while (iter2.hasNext()){
				Column column = (Column)iter2.next();
				if (column.name().equals(pkColumn.name())){
					column.setIsPrimaryKey(true);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.DbsTable#getColumns()
	 */
	public ArrayList columns() {
		return columns;
	}
	
	public void setColumns(ArrayList columns) {
		this.columns = columns;
	}

	public String createSQL(){
		return "";
	}

	public String checkIfExistsSQL(){
		return "";
	}
	
	public String createForeignKeySQL(){
		return "";
	}
	
	public String checkIfExistsForeignkeySQL(){
		return "";
	}
	
	public String database() {
		return databaseName;
	}
	

	public void setDatabase(String databaseName) {
		this.databaseName = databaseName;
	}
	
	public String getAllDataSQL(){
		return "SELECT * FROM \"" + database() + "\"." + name();
	}

	public String deleteAllDataSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String dropTableSQL() {
		// TODO Auto-generated method stub
		return null;
	}
}
