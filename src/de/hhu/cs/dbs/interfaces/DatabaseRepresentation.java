package de.hhu.cs.dbs.interfaces;

public interface DatabaseRepresentation{
		
	boolean connect(String userName, String password, String url, java.sql.Driver jdbcDriver);
	
	String getSchema();
	
	String getData();
	
	String getTable(String tableName);

	
}