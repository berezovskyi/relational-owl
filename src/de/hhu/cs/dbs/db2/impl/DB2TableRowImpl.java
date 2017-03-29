package de.hhu.cs.dbs.db2.impl;

/**
 * <code>DB2TableRowImpl</code> implements virtual table row in DB2 RDBMS that
 * can be created from schema representation in OWL as well as from metadata 
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see TableRow
 * @see DB2TableRow
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.hhu.cs.dbs.db2.interfaces.DB2Column;
import de.hhu.cs.dbs.db2.interfaces.DB2TableRow;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.TableRow;

public class DB2TableRowImpl implements DB2TableRow {
	private TableRow tableRow;
	
	/**
	 * Creates new DB2TableRowImpl
	 */
	public DB2TableRowImpl(){}
	
	/**
	 * Creates new DB2TableRowImpl wich wraps virtual {@link TableRow}
	 * @param tableRow Table row to wrap
	 */
	public DB2TableRowImpl(TableRow tableRow){
		this.tableRow = tableRow;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#database()
	 */
	public String database(){
		return tableRow.database();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#setDatabase(java.lang.String)
	 */
	public void setDatabase(String name){
		tableRow.setDatabase(name);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#table()
	 */
	public String table() {
		return tableRow.table();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#setTable(java.lang.String)
	 */
	public void setTable(String name){
		tableRow.setTable(name);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#addColumn(de.hhu.cs.dbs.interfaces.Column)
	 */
	public void addColumn(Column column){
		tableRow.addColumn(column);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#columns()
	 */
	public ArrayList columns() {
		return tableRow.columns();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#setColumns(java.util.ArrayList)
	 */
	public void setColumns(ArrayList columns) {
		tableRow.setColumns(columns);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#createSQL()
	 */
	public String createSQL(){
		String sql = "INSERT INTO \"" + database() + "\"." + table() + " ";
		String columnStmt = "(";
		String valuesStmt = " VALUES (";
		
		Iterator iter = columns().iterator();
		while (iter.hasNext()){
			DB2Column c = new DB2ColumnImpl((Column)iter.next());
			columnStmt += c.name().substring(c.name().indexOf(".") + 1, c.name().length()) + ",";
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\'\'");
					//token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime")){
				//valuesStmt += "'" + c.value() + "',";
				valuesStmt += "'" + value + "',";
			}
			else {
				//valuesStmt += c.value() + ",";
				valuesStmt += value + ",";
			}
		}
		columnStmt = columnStmt.substring(0, columnStmt.length() - 1) + ")";
		valuesStmt = valuesStmt.substring(0, valuesStmt.length() - 1) + ")";
		sql += columnStmt + valuesStmt;
		
		return sql;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#checkIfExistsSQL()
	 */
	public String checkIfExistsSQL(){
		String sql = "SELECT COUNT(*) FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = columns().iterator();
		while (iter.hasNext()){
			DB2Column c = new DB2ColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\'\'");
					//token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime")){
				//columnValue = "'" + c.value() + "'";
				columnValue = "'" + value + "'";
			}
			else {
				//columnValue = c.value();
				columnValue = value;
			}
			sql += columnName + " = " + columnValue + " AND ";
		}
		//cut last AND
		sql = sql.substring(0, sql.length() - 4);
		return sql;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#checkIfExistsSQL(java.util.ArrayList)
	 */
	public String checkIfExistsSQL(ArrayList columns){
		String sql = "SELECT COUNT(*) FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = columns.iterator();
		while (iter.hasNext()){
			DB2Column c = new DB2ColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\'\'");
					//token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime")){
				//columnValue = "'" + c.value() + "'";
				columnValue = "'" + value + "'";
			}
			else {
				//columnValue = c.value();
				columnValue = value;
			}
			sql += columnName + " = " + columnValue + " AND ";
		}
		//cut last AND
		sql = sql.substring(0, sql.length() - 4);
		return sql;		
	}
	
	public String deleteSQL() {
		String sql = "DELETE FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = tableRow.columns().iterator();
		while (iter.hasNext()){
			DB2Column c = new DB2ColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\'\'");
					//token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime")){
				//columnValue = "'" + c.value() + "'";
				columnValue = "'" + value + "'";
			}
			else {
				//columnValue = c.value();
				columnValue = value;
			}
			sql += columnName + " = " + columnValue + " AND ";
		}
		
		//cut last AND
		sql = sql.substring(0, sql.length() - 4);
		return sql;
	}

	public String deleteSQL(ArrayList columns) {
		String sql = "DELETE FROM \"" + database().trim() + "\"." + table().trim() + " WHERE ";
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = columns.iterator();
		while (iter.hasNext()){
			DB2Column c = new DB2ColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\'\'");
					//token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime")){
				//columnValue = "'" + c.value() + "'";
				columnValue = "'" + value + "'";
			}
			else {
				//columnValue = c.value();
				columnValue = value;
			}
			sql += columnName + " = " + columnValue + " AND ";
		}
		
		//cut last AND
		sql = sql.substring(0, sql.length() - 4);
		return sql;
	}
	
}
