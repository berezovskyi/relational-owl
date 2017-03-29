package de.hhu.cs.dbs.db2;

/**
 * <code>DB2DbManager</code> manage DB2 RDBMS databases, tables, columns and 
 * primary and foreign keys objects as well as its JDBC-Connection and 
 * instance data.
 * 
 * @author Przemyslaw Dzikowski
 * @see DbManager
 */

import de.hhu.cs.dbs.db2.impl.DB2ColumnImpl;
import de.hhu.cs.dbs.db2.impl.DB2DatabaseImpl;
import de.hhu.cs.dbs.db2.impl.DB2TableImpl;
import de.hhu.cs.dbs.db2.interfaces.DB2Column;
import de.hhu.cs.dbs.db2.interfaces.DB2Database;
import de.hhu.cs.dbs.db2.interfaces.DB2Table;
import de.hhu.cs.dbs.impl.ColumnImpl;
import de.hhu.cs.dbs.impl.DatabaseImpl;
import de.hhu.cs.dbs.impl.TableImpl;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.DbManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author  Administrator
 */
public class DB2DbManager implements DbManager{

	private String driver = "com.ibm.db2.jcc.DB2Driver";
	private Connection connection;
	private String sql; 
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
		
	public DB2DbManager(String dbUrl, String dbUser, String dbPassword){
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
	
	public DB2DbManager(String driver, String dbUrl, String dbUser, String dbPassword) throws Exception{
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
		
	public DB2DbManager(Connection connection){
		this.connection = connection;
	}
	
	/**
	 * @return  Returns the connection.
	 * @uml.property  name="connection"
	 */
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
				connection = DriverManager.getConnection(dbUrl + "/" + dbName, dbUser, dbPassword);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}
	
	public ArrayList getDatabases(){
		ArrayList databases = new ArrayList();
		sql = "SELECT * FROM SYSIBM.SYSSCHEMATA";
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				DB2Database database = new DB2DatabaseImpl(new DatabaseImpl(result.getString(1)));
				database.setTables(getTables(database.name()));
				databases.add(database);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return databases;
	}
	
	public Database getDatabase(String databaseName){
		DB2Database database;
		sql = "SELECT * FROM SYSIBM.SYSSCHEMATA";
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				database = new DB2DatabaseImpl(new DatabaseImpl(result.getString(1)));
				if (database.name().equals(databaseName)){
					database.setTables(getTables(database.name()));
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
		sql = "SELECT * FROM SYSIBM.SYSSCHEMATA";
		
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
		sql = "SELECT * FROM SYSIBM.SYSTABLES WHERE CREATOR ='" + database.trim() + "'";
		
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				DB2Table table = new DB2TableImpl(new TableImpl(result.getString(1)));
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
		sql = "SELECT * FROM SYSIBM.SYSTABLES WHERE CREATOR ='" + database.trim() + "'";
		
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
		ArrayList pkColumns = getPrimaryKeys(database, table);
		ArrayList fKeys = getForeignKeys(database, table);
		sql = "SELECT * FROM SYSIBM.COLUMNS WHERE TABLE_SCHEMA = '" + database.trim() + "' AND TABLE_NAME = '" + table.trim() + "'";
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				DB2Column column = new DB2ColumnImpl(new ColumnImpl(result.getString(4)));
				String columnName = column.name();
				column.setType(result.getString("DATA_TYPE"));
				column.setRange(result.getString("DATA_TYPE"));
				//length character
				if (result.getString("CHARACTER_MAXIMUM_LENGTH") != null){
					column.setLength(result.getString("CHARACTER_MAXIMUM_LENGTH"));	
				}
				//numeric precision
				if (result.getString("NUMERIC_PRECISION") != null){
					column.setLength(result.getString("NUMERIC_PRECISION"));	
				}
				//scale
				if (result.getString("NUMERIC_SCALE") != null){
					column.setScale(result.getString("NUMERIC_SCALE"));	
				}// when doesn't exist set 0
				else{
					column.setScale("0");	
				}
				//primary key
				if (pkColumns.contains(columnName)){
					column.setIsPrimaryKey(true);
				}
				//foreign key
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
	
	private ArrayList getPrimaryKeys(String database, String table){
		ArrayList primaryKeys = new ArrayList();
		sql = "SELECT *  FROM SYSIBM.SYSINDEXES WHERE TBCREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'";
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				String pkColumns = result.getString("COLNAMES");
				StringTokenizer tokenizer = new StringTokenizer(pkColumns,"+");
				while (tokenizer.hasMoreTokens()){
					primaryKeys.add(tokenizer.nextToken());
				}
			}
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return primaryKeys;
	}
	
	private ArrayList getForeignKeys(String database, String table){
		ArrayList foreignKeys = new ArrayList();
		Hashtable fKey = new Hashtable();
		sql = "SELECT * FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'";
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			while (result.next()){
				String fkColumns = result.getString("FKCOLNAMES");
				String fkReferencedColumns = result.getString("PKCOLNAMES");
				String refTable = result.getString("REFTBNAME");
				// foreign key columns count has always to be equal referenced columns count
				StringTokenizer tokenizer = new StringTokenizer(fkColumns," ");
				StringTokenizer tokenizer2 = new StringTokenizer(fkReferencedColumns, " ");
				while (tokenizer.hasMoreTokens() && tokenizer2.hasMoreTokens()){
					String fkColumn = tokenizer.nextToken();
					String referencedColumn = refTable + "." + tokenizer2.nextToken();
					fKey.put(fkColumn, referencedColumn);
					foreignKeys.add(fKey);
				}
			}
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return foreignKeys;
	}

	public void dropForeignKeys(String database, String table){
		String sql = "SELECT COUNT(*) FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'";	
		try {
			java.sql.Statement stmt = getConnection().createStatement();
			java.sql.ResultSet result = stmt.executeQuery(sql);
			
			// check if there are foreign keys to drop
			if (result.next()){
				// how many foreign keys exist
				int fkeysCount = result.getInt(1);
				
				// TODO Implement concurent processing. At the same time the number of foreign keys can be changed !!
				for (int i = 0; i < fkeysCount; i++){
					sql = "SELECT * FROM SYSIBM.SYSRELS WHERE CREATOR = '" + database.trim() + "' AND TBNAME = '" + table.trim() + "'";	
					result = stmt.executeQuery(sql);
					if (result.next()){
						String foreignKey = result.getString("RELNAME");
						sql = "ALTER TABLE \"" + database.trim() + "\".\"" + table.trim() + "\" DROP FOREIGN KEY \"" + foreignKey + "\"";
						stmt.executeUpdate(sql);			
					}
				}
			}		
		} catch (SQLException e) {
			// TODO Exception up to return
			e.printStackTrace();
		}
	}
	
}
