package de.hhu.cs.dbs.mysql.impl;

/**
 * <code>MySQLTableRowImpl</code> implements virtual table row in MySQL RDBMS that 
 * can be created from schema representation in OWL as well as from metadata 
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.hhu.cs.dbs.impl.TableRowImpl;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.TableRow;
import de.hhu.cs.dbs.mysql.interfaces.MySQLColumn;
import de.hhu.cs.dbs.mysql.interfaces.MySQLTableRow;

public class MySQLTableRowImpl implements MySQLTableRow {
	private TableRow tableRow = new TableRowImpl();
	
	/**
	 * Creates new DB2ColumnImpl
	 */
	public MySQLTableRowImpl(){}
	
	/**
	 * Creates new MySQLTableRowImpl wich wraps virtual {@link TableRow}
	 * @param tableRow TableRow to wrap
	 */
	public MySQLTableRowImpl(TableRow tableRow){
		this.tableRow = tableRow;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#addColumn(de.hhu.cs.dbs.interfaces.Column)
	 */
	public void addColumn(Column column){
		tableRow.addColumn(column);
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
		String sql = "INSERT INTO " + database() + "." + table();
		String columnsSQL = " (";
		String valuesSQL = "VALUES (";
		
		Iterator iter = columns().iterator();
		while (iter.hasNext()){
			Column c = (Column)iter.next();
			columnsSQL += c.name() + ",";
			// TODO replace mysql special chars ' and ""
			// see documentation at www.mysql.org
			// can still be bug f.e. "This is comment a'la "double quota in comment"
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\\'");
					token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			valuesSQL += "'" + value + "',";
		}
		// remove last ',' from valuesSQL 
		valuesSQL = valuesSQL.substring(0, valuesSQL.length() - 1) + ")";
		// remove last ',' from columnsSQL and add last )
		columnsSQL = columnsSQL.substring(0, columnsSQL.length() - 1) + ")";
		sql += columnsSQL + " " + valuesSQL;
		
		return sql;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.TableRow#checkIfExistsSQL()
	 */
	public String checkIfExistsSQL(){
		String sql = "SELECT COUNT(*) FROM " + database().trim() + "." + table().trim() + " WHERE ";
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = columns().iterator();
		while (iter.hasNext()){
			MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\\'");
					token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime") || c.range().equals("timestamp") || c.range().equals("char")){
				columnValue = "'" + value + "'";
			}
			else {
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
		String sql = "SELECT COUNT(*) FROM " + database().trim() + "." + table().trim() + " WHERE ";
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = columns.iterator();
		while (iter.hasNext()){
			MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\\'");
					token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime") || c.range().equals("timestamp") || c.range().equals("char")){
				columnValue = "'" + value + "'";
			}
			else {
				columnValue = value;
			}
			sql += columnName + " = " + columnValue + " AND ";
		}
		//cut last AND
		sql = sql.substring(0, sql.length() - 4);
		return sql;	
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

	public String deleteSQL() {
		String sql = "DELETE FROM " + database().trim() + "." + table().trim() + " WHERE ";
	
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = columns().iterator();
		while (iter.hasNext()){
			MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\\'");
					token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime") || c.range().equals("timestamp") || c.range().equals("char")){
				columnValue = "'" + value + "'";
			}
			else {
				columnValue = value;
			}
			sql += columnName + " = " + columnValue + " AND ";
		}
		//cut last AND
		sql = sql.substring(0, sql.length() - 4);
		return sql;
	
	}

	public String deleteSQL(ArrayList columns) {
		String sql = "DELETE FROM " + database().trim() + "." + table().trim() + " WHERE ";
	
		String columnName = "";
		String columnValue = "";
		
		Iterator iter = columns.iterator();
		while (iter.hasNext()){
			MySQLColumn c = new MySQLColumnImpl((Column)iter.next());
			columnName = c.name().substring(c.name().indexOf(".") + 1, c.name().length());
			String value = "";
			StringTokenizer valueTokenizer = new StringTokenizer(c.value());
			while (valueTokenizer.hasMoreTokens()){
				String token = valueTokenizer.nextToken();
				if (((token.indexOf("'") != -1) && (!(token.indexOf("\"") != -1)))
						|| (!(token.indexOf("'") != -1) && ((token.indexOf("\"") != -1)))){
					token = token.replace("\'", "\\'");
					token = token.replace("\"", "\"\"");
				}
				value += token;
			}
			if (c.range().equals("string") || c.range().equals("date") || c.range().equals("dateTime") || c.range().equals("timestamp") || c.range().equals("char")){
				columnValue = "'" + value + "'";
			}
			else {
				columnValue = value;
			}
			sql += columnName + " = " + columnValue + " AND ";
		}
		//cut last AND
		sql = sql.substring(0, sql.length() - 4);
		return sql;
	
	}
	
}
