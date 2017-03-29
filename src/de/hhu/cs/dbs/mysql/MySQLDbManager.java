package de.hhu.cs.dbs.mysql;

/**
 * <code>MySQLDbManager</code> manage MySQL RDBMS databases, tables, columns and 
 * primary and foreign keys objects as well as its JDBC-Connection and 
 * instance data.
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.hhu.cs.dbs.impl.ColumnImpl;
import de.hhu.cs.dbs.impl.DatabaseImpl;
import de.hhu.cs.dbs.impl.TableImpl;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.mysql.impl.MySQLColumnImpl;
import de.hhu.cs.dbs.mysql.impl.MySQLDatabaseImpl;
import de.hhu.cs.dbs.mysql.impl.MySQLTableImpl;
import de.hhu.cs.dbs.mysql.interfaces.MySQLColumn;
import de.hhu.cs.dbs.mysql.interfaces.MySQLDatabase;
import de.hhu.cs.dbs.mysql.interfaces.MySQLTable;

public class MySQLDbManager implements DbManager {

	private String driver = "com.mysql.jdbc.Driver";
	private Connection connection;
	private String sql; 
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	
	public MySQLDbManager(String dbUrl, String dbUser, String dbPassword){
		try{
			Class.forName(driver).newInstance(); 
			connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			this.dbUrl = dbUrl;
			this.dbUser = dbUser;
			this.dbPassword = dbPassword;
		}
		catch (Exception e) { 
			e.printStackTrace(); 
	        System.exit( 1 ); 
	    }  
	}
	
	public MySQLDbManager(String driver, String dbUrl, String dbUser, String dbPassword) throws Exception{
		this.driver = driver;
		try{
			Class.forName(driver).newInstance(); 
			connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			this.dbUrl = dbUrl;
			this.dbUser = dbUser;
			this.dbPassword = dbPassword;
		}
		catch (Exception e) { 
			e.printStackTrace(); 
			throw new Exception(e.getMessage());	
	    }  
	}
			
	public MySQLDbManager(Connection connection){
		this.connection = connection;
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	public Connection getConnection(String dbName){
		StringTokenizer connTokens = new StringTokenizer(dbUrl, "/");
		String connToken = null;
		while (connTokens.hasMoreTokens()){
			connToken = connTokens.nextToken();
		}
		if ((connToken != null) && (!connToken.equals(dbName))){
			try {
				//add / when not occours at the and of dbUrl
				if (!dbUrl.substring(dbUrl.length() -1, dbUrl.length()).equals("/"))
					dbUrl += "/";
				connection = DriverManager.getConnection(dbUrl + dbName, dbUser, dbPassword);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}
	
	public ArrayList getDatabases(){
		ArrayList databases = new ArrayList();
		sql = "SHOW DATABASES";
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				MySQLDatabase database = new MySQLDatabaseImpl(new DatabaseImpl(result.getString(1)));
				database.setTables(getTables(database.name()));
				databases.add(database);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return databases;
	}
	
	public Database getDatabase(String databaseName){
		MySQLDatabase database;
		sql = "SHOW DATABASES";
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				database = new MySQLDatabaseImpl(new DatabaseImpl(result.getString(1)));
				if (database.name().equals(databaseName)){
					database.setTables(getTables(database.name()));
					// return database found
					return database;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return null;
	}

	public ArrayList getDatabaseList(){
		ArrayList databaseList = new ArrayList();
		sql = "SHOW DATABASES";
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				databaseList.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return databaseList;
	}

	public ArrayList getTables(String database){
		ArrayList tables = new ArrayList();
		sql = "SHOW TABLES FROM " + database.trim();
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				MySQLTable table = new MySQLTableImpl(new TableImpl(result.getString(1)));
				table.setDatabase(database);
				table.setColumns(getColumns(database, table.name()));
				tables.add(table);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return tables;
	}
	
	public ArrayList getTableList(String database){
		ArrayList tableList = new ArrayList();
		sql = "SHOW TABLES FROM " + database.trim();
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				tableList.add(result.getString(1));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return tableList;
	}

	public ArrayList getColumns(String database, String table){
		ArrayList columns = new ArrayList();
		ArrayList fKeys = getForeignKeys(database, table);
		sql = "SHOW COLUMNS FROM " + database + "." + table;
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				MySQLColumn column = new MySQLColumnImpl(new ColumnImpl(result.getString(1)));
				String type, length, scale, range;
				String columnName = column.name();
				column.setType(result.getString(2));
				
				// range  
				type = result.getString(2);
				if (type.indexOf(")") != -1){
					type = type.substring(0, type.lastIndexOf(")") + 1);
				}
				if (type.endsWith(")")){
					StringTokenizer typeTokenizer = new StringTokenizer(type,"(");
					range = typeTokenizer.nextToken();
					column.setRange(range);
					
					// length and scale
					length = typeTokenizer.nextToken();
					typeTokenizer = new StringTokenizer(length, ",");
					if (typeTokenizer.countTokens() > 1){
						length = typeTokenizer.nextToken();
						column.setLength(length);
						scale = typeTokenizer.nextToken();
						column.setScale(scale.substring(0, scale.length() - 1));
					}
					else{
						column.setLength(length.substring(0, length.length() - 1));
						column.setScale("0");
					}
				}
				else {
					column.setRange(type);
					column.setScale("0");
				}
				// primary key
				if (result.getString(4).toUpperCase().equals("PRI")){
					column.setIsPrimaryKey(true);
				}
				// foreign key
				Iterator iter = fKeys.iterator();
				while (iter.hasNext()){
					Hashtable fKey = (Hashtable)iter.next();
					ArrayList fkColumns = new ArrayList(fKey.keySet());
					if (fkColumns.contains(columnName)){
						column.setIsForeignKey(true);
						column.setReferences(fKey.get(columnName).toString());
					}
				}	
				columns.add(column);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return columns;
	}
	private ArrayList getForeignKeys(String database, String table){
		ArrayList foreignKeys = new ArrayList();
		Hashtable fKey = new Hashtable();
		sql = "SHOW CREATE TABLE " + database.trim() + "." + table.trim();
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			result.next();
			String createStmt = result.getString(2);
			String fkColumn = "";
			String referencedColumn = "";
			String refTable = "";
			
			while (createStmt.indexOf("FOREIGN KEY") > -1){
				createStmt = createStmt.substring(createStmt.indexOf("FOREIGN KEY") + 12, createStmt.length());
				String fkStmt = createStmt;
				fkColumn = fkStmt.substring(fkStmt.indexOf("(`") + 2, fkStmt.indexOf("`)"));
				// cut fkColumn statement
				fkStmt = fkStmt.substring(fkStmt.indexOf("`)") + 2, fkStmt.length());
				refTable = fkStmt.substring(fkStmt.indexOf("`") + 1, fkStmt.indexOf("(`") - 2);
				referencedColumn = fkStmt.substring(fkStmt.indexOf("(`") + 2, fkStmt.indexOf("`)"));
				referencedColumn = refTable + "." + referencedColumn;
				if (!fKey.containsValue(referencedColumn)){
					fKey.put(fkColumn, referencedColumn);
					foreignKeys.add(fKey);
				}
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return foreignKeys;
	}
	
	public java.sql.ResultSet getData(String sql) throws Exception{
		java.sql.ResultSet result = null;
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			result = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return result;
	}
	
	public void dropForeignKeys(String database, String table){
		
	}
	
	
}
