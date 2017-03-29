package de.hhu.cs.dbs.impl;

/**
 * <code>TableRowImpl</code> implements virtual RDBMS table row that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.TableRow;
import java.util.ArrayList;

public class TableRowImpl implements TableRow{
	private String tableName = "";
	private String databaseName = "";
	private ArrayList columns = new ArrayList();
	
	public TableRowImpl(){
	}
	
	public TableRowImpl(String tableName){
		this.tableName = tableName;
	}
	
	public TableRowImpl(String tableName, ArrayList columns){
		this.tableName = "";
		this.columns = columns;
	}
	
	public void addColumn(Column c){
		columns.add(c);
	}
	
	public String table() {
		return tableName;
	}
	
	public void setTable(String tableName) {
		this.tableName = tableName;
	}
	
	public ArrayList columns() {
		return columns;
	}
	
	/**
	 * @param columns  The columns to set.
	 * @uml.property  name="columns"
	 */
	public void setColumns(ArrayList columns) {
		this.columns = columns;
	}
	
	public String createSQL(){
		return "";
	}

	public String checkIfExistsSQL(){
		return "";
	}

	public String checkIfExistsSQL(ArrayList columns){
		return "";
	}
	
	public String database() {
		return databaseName;
	}

	public void setDatabase(String databaseName) {
		this.databaseName = databaseName;
	}

	public String deleteSQL() {
		return "";
	}

	public String deleteSQL(ArrayList columns) {
		return "";
	}
	
}
