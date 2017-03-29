package de.hhu.cs.dbs.db2.impl;

/**
 * <code>DB2TableImpl</code> implements virtual table in DB2 RDBMS that 
 * can be created from schema representation in OWL as well as from metadata
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see Table
 * @see DB2Table
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.hhu.cs.dbs.db2.interfaces.DB2Column;
import de.hhu.cs.dbs.db2.interfaces.DB2Table;
import de.hhu.cs.dbs.impl.TableImpl;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.PrimaryKey;
import de.hhu.cs.dbs.interfaces.Table;

public class DB2TableImpl implements DB2Table{
	private Table t = new TableImpl();
	private String type = "";
	
	/**
	 * Creates new DB2TableImpl
	 */
	public DB2TableImpl(){}
	
	/**
	 * Creates new DB2TableImpl wich wraps virtual {@link Table}
	 * @param table Table to wrap
	 */
	public DB2TableImpl(Table table){
		this.t = table;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#name()
	 */
	public String name() {
		return t.name();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#setName(java.lang.String)
	 */
	public void setName(String name) {
		t.setName(name);
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
	public void setDatabase(String name) {
		t.setDatabase(name);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#getColumn(java.lang.String)
	 */
	public DB2Column getColumn(String name) {
		return new DB2ColumnImpl(t.getColumn(name));
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#addColumn(de.hhu.cs.dbs.interfaces.Column)
	 */
	public void addColumn (Column column){
		t.addColumn(column);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#primaryKey()
	 */
	public PrimaryKey primaryKey() {
		return t.primaryKey();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#setPrimaryKey(de.hhu.cs.dbs.interfaces.PrimaryKey)
	 */
	public void setPrimaryKey(PrimaryKey primaryKey) {
		t.setPrimaryKey(primaryKey);
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
		String sql = "CREATE TABLE \"" + database() + "\"." + t.name() + " ( \r\n";
		DB2ColumnImpl c = null;
		Iterator iter = t.columns().iterator();

		//	columns definition
		while (iter.hasNext()){
			c = new DB2ColumnImpl((Column)iter.next());
			StringTokenizer columnTokens = new StringTokenizer(c.name(),".");
			String columnName = c.name();
			while (columnTokens.hasMoreTokens()){
				columnName = columnTokens.nextToken();
			}
			sql += "	" + columnName + " " + c.getType();
			if (c.isPrimaryKey())
				sql += " NOT NULL ";
			sql += ", \r\n";
		}
		if (t.primaryKey().getColumns().size() == 0){
			sql = sql.substring(0, sql.lastIndexOf(","));
		}
		
		//	primary key definition
		iter = t.primaryKey().getColumns().iterator();
		if (iter.hasNext()){
			sql += "	PRIMARY KEY (";
			while (iter.hasNext()){
				c = new DB2ColumnImpl((Column)iter.next());
				// column name - the last element in tokenizer with delim '.'
				StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
				String name = "";
				while (tokenizer.hasMoreTokens()){
					name = tokenizer.nextToken();
				}
				sql += name + ","; 
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql += ")";
		}
		//	end parenthesis
		sql += " \r\n)";
		
		return sql;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#checkIfExistsSQL()
	 */
	public String checkIfExistsSQL(){
		String sql = "SELECT COUNT(*) FROM SYSIBM.SYSTABLES WHERE CREATOR = '" + database().trim() + "'  AND NAME = '" + name().trim() + "'";
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#createForeignKeySQL()
	 */
	public String createForeignKeySQL(){
		String sql = "ALTER TABLE \""  + database() + "\"." + t.name();
		String foreignKeySQL = "";
		String referencesSQL = "";
		String separateFKSql = "";
		boolean fkExist = false;
		ArrayList fkColumns = new ArrayList();
		ArrayList refColumns = new ArrayList();
		Hashtable fkValues = new Hashtable();
		Hashtable fkHash = new Hashtable();
		DB2ColumnImpl c = null;
		
		// foreign key hashtable
		Iterator iter = t.columns().iterator();
		while (iter.hasNext()){
			c = new DB2ColumnImpl((Column)iter.next());
			if (c.isForeignKey()){
				String tableReferenced = c.references().substring(0, c.references().indexOf("."));
				String columnReferenced = c.references().substring(c.references().indexOf(".") + 1, c.references().length());
				// forign column name - the last element in tokenizer with delim '.'
				StringTokenizer tokenizer = new StringTokenizer(c.name(), ".");
				String fkColumn = "";
				while (tokenizer.hasMoreTokens()){
					fkColumn = tokenizer.nextToken();
				} 
				
				// add foreign key to hashtable
				if (fkHash.containsKey(tableReferenced)){
					fkValues = (Hashtable)fkHash.get(tableReferenced);
					fkColumns = (ArrayList)fkValues.get("fkColumns");
					fkColumns.add(fkColumn);
					fkValues.put("fkColumns", fkColumns);
					refColumns = (ArrayList)fkValues.get("refColumns");
					refColumns.add(columnReferenced);
					fkValues.put("refColumns", refColumns);
					fkHash.remove(tableReferenced);
					fkHash.put(tableReferenced, fkValues);
				}
				else{
					fkValues = new Hashtable();
					fkColumns = new ArrayList();
					refColumns = new ArrayList();
					fkColumns.add(fkColumn);
					fkValues.put("fkColumns", fkColumns);
					refColumns.add(columnReferenced);
					fkValues.put("refColumns", refColumns);
					fkHash.put(tableReferenced, fkValues);
				}
			}
		}
		// foreign key sql statement
		Enumeration fkElements = fkHash.keys();
		while (fkElements.hasMoreElements()){
			String tableReferenced = fkElements.nextElement().toString();
			fkValues = (Hashtable)fkHash.get(tableReferenced);
			foreignKeySQL = "\r\n	ADD FOREIGN KEY (";
			referencesSQL = " REFERENCES \""  + database() + "\"." + tableReferenced + " (";
			separateFKSql = "";
			
			// foreign key sql
			fkColumns = (ArrayList)fkValues.get("fkColumns");
			iter = fkColumns.iterator();
			while (iter.hasNext()){
				foreignKeySQL += iter.next().toString() + ",";
				fkExist = true;
			}
			foreignKeySQL = foreignKeySQL.substring(0, foreignKeySQL.length() - 1) + ")"; 
			
			// references key sql
			refColumns = (ArrayList)fkValues.get("refColumns");
			int refColumnId = 0;
			iter = ((ArrayList)fkValues.get("refColumns")).iterator();
			while (iter.hasNext()){
				String columnReferenced = iter.next().toString();
				// when more columns reference to the same column in second table
				// they must have in db2 separate foreign key statements
				if (refColumns.indexOf(columnReferenced) != refColumns.lastIndexOf(columnReferenced)){
					separateFKSql += "\r\n	ADD FOREIGN KEY (" + fkColumns.get(refColumnId) + ")";
					separateFKSql += " REFERENCES \""  + database() + "\"." + tableReferenced + " (" + columnReferenced + ")";
					refColumnId++;
					fkExist = true;
				}
				else{
					referencesSQL += columnReferenced + ",";
				}
			}
			referencesSQL = referencesSQL.substring(0, referencesSQL.length() - 1) + ")"; 
			
			// check if there are not only references to the same column in second table
			if (referencesSQL.endsWith(" )")){
				sql += separateFKSql;
			}
			else{
				sql += foreignKeySQL + referencesSQL + separateFKSql;
			}		
		}
		if (! fkExist){
			sql = "";
		}	
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#checkIfExistsForeignkeySQL()
	 */
	public String checkIfExistsForeignkeySQL(){
		String sql = "SELECT COUNT(*) FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database().trim() + "'  AND TBNAME = '" + name().trim() + "'";
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#allTableDataSQL(java.lang.String)
	 */
	public String getAllDataSQL(){
		return "SELECT * FROM \"" + database() + "\"." + name();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#deleteAllDataSQL()
	 */
	public String deleteAllDataSQL(){
		return "DELETE FROM \"" + database() + "\"." + name();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Table#dropTableSQL()
	 */
	public String dropTableSQL(){
		return "DROP TABLE \"" + database() + "\"." + name();
	}
}
