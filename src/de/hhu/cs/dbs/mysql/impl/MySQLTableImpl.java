package de.hhu.cs.dbs.mysql.impl;

/**
 * <code>MySQLTableImpl</code> implements virtual table in MySQL RDBMS that 
 * can be created from schema representation in OWL as well as from metadata
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.hhu.cs.dbs.impl.TableImpl;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.PrimaryKey;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.mysql.interfaces.MySQLTable;

public class MySQLTableImpl implements MySQLTable{
	public Table t = new TableImpl();
	public String databaseName = "";
	public String type = "";
	
	/**
	 * Creates new MySQLTableImpl
	 */
	public MySQLTableImpl(){}
	
	/**
	 * Creates new MySQLTableImpl wich wraps virtual {@link Table}
	 * @param table Table to wrap
	 */
	public MySQLTableImpl(Table table){
		this.t = table;
	}
		
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#addColumn(de.hhu.cs.dbs.interfaces.Column)
	 */
	public void addColumn (Column column){
		t.addColumn(column);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#getColumn(java.lang.String)
	 */
	public MySQLColumnImpl getColumn(String name) {
		return new MySQLColumnImpl(t.getColumn(name));
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#columns()
	 */
	public ArrayList columns() {
		return t.columns();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#setColumns(java.util.ArrayList)
	 */
	public void setColumns(ArrayList columns){
		t.setColumns(columns);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#name()
	 */
	public String name() {
		return t.name();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#primaryKey()
	 */
	public PrimaryKey primaryKey() {
		return t.primaryKey();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#setName(java.lang.String)
	 */
	public void setName(String name) {
		t.setName(name);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#setPrimaryKey(de.hhu.cs.dbs.interfaces.PrimaryKey)
	 */
	public void setPrimaryKey(PrimaryKey primaryKey) {
		t.setPrimaryKey(primaryKey);
	}

	/**
	 * Get table type
	 * @return Table type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set table type
	 * @param type Table type
	 */
	public void setType(String type) {
		setType(type);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#createSQL()
	 */
	public String createSQL(){
		String sql = "CREATE TABLE IF NOT EXISTS " + database() + "." + t.name() + " ( \r\n";
		String indexSQL = "";
		MySQLColumnImpl c = null;
		
		//	columns definition
		Iterator iter = t.columns().iterator();
		while (iter.hasNext()){
			c = new MySQLColumnImpl((Column)iter.next());
			sql += "	" + c.name() + " " + c.getType();
			if (c.isPrimaryKey())
				sql += " NOT NULL ";
			sql += ",\r\n";
		}
		sql = sql.substring(0, sql.length() - 3);
		
		//	primary key definition
		iter = t.primaryKey().getColumns().iterator();
		if (iter.hasNext()){
			sql += ",\r\n	PRIMARY KEY (";
			while (iter.hasNext()){
				c = new MySQLColumnImpl((Column)iter.next());
				
				// column name - the last element in tokenizer with delim '.'
				StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
				String name = "";
				while (tokenizer.hasMoreTokens()){
					name = tokenizer.nextToken();
				}
				sql += name + ","; 
				indexSQL += "\r\n	INDEX (" + name + "),";
			}
			//get rid of last ','
			sql = sql.substring(0, sql.length() - 1); 
			//close primary key definition
			sql += "),";
			//add index definition
			sql += indexSQL; 
		} else {
			sql = sql + ")";
		}
		
		//get rid of last ','
		sql = sql.substring(0, sql.length() - 1); 
		//if (indexSQL.length() == 0)
		//	sql = sql.substring(0, sql.length() - 3); 
		
		//end parenthesis end table properties
		sql += ") CHARSET=utf8 \r\n";  //TYPE=MyISAM";
	
		//	TODO Table type in createSQL
		// 	table type
		//if (t.getType().length() > 0){
		//	sql += "TYPE=" + t.getType();
		//}
		
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#createForeignKeySQL()
	 */
	public String createForeignKeySQL(){
		String sql = "ALTER TABLE "  + database() + "." + t.name();
		String foreignKeySQL = "";
		String referencesSQL = "";
		MySQLColumnImpl c = null;
		
		// foreign key definition
		int foreignColumnsCount = 0;
		Iterator iter = t.columns().iterator();
		while (iter.hasNext()){
			c = new MySQLColumnImpl((Column)iter.next());
			if (c.isForeignKey()){
				foreignColumnsCount++;
				foreignKeySQL = "\r\n	ADD FOREIGN KEY (";
				referencesSQL = "\r\n	REFERENCES ";
				// forign column name - the last element in tokenizer with delim '.'
				StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
				String fkName = "";
				while (tokenizer.hasMoreTokens()){
					fkName = tokenizer.nextToken();
				}
				foreignKeySQL += fkName + ") REFERENCES ";
				foreignKeySQL += c.references().substring(0, c.references().indexOf(".")) + "(";
				foreignKeySQL += c.references().substring(c.references().indexOf(".") + 1, c.references().length());				
				sql += foreignKeySQL + "),";
			}
		}
		// check if table has foreign key
		if (foreignColumnsCount > 0){
			// get rid of last ','
			sql = sql.substring(0, sql.length() - 1); 
		} else {
			sql = "";
		}
			
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#checkIfExistsForeignkeySQL()
	 */
	public String checkIfExistsForeignkeySQL(){
		String sql = "";
		return sql;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#database()
	 */
	public String database() {
		return t.database();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#setDatabase(java.lang.String)
	 */
	public void setDatabase(String databaseName) {
		t.setDatabase(databaseName);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#checkIfExistsSQL()
	 */
	public String checkIfExistsSQL(){
		return "SHOW TABLES FROM " + database() + " LIKE '" + name() +"'";
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#allTableDataSQL(java.lang.String)
	 */
	public String getAllDataSQL(){
		return "SELECT * FROM " + database() + "." + name();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#deleteAllDataSQL()
	 */
	public String deleteAllDataSQL(){
		return "DELETE FROM " + database() + "." + name();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#dropTableSQL()
	 */
	public String dropTableSQL(){
		return "DROP TABLE " + database() + "." + name();
	}
}
