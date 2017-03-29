package de.hhu.cs.dbs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.PrimaryKey;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.interfaces.TableRow;

public class DatabaseFactory {

	private static final String PROPS_PATH = "rdbms.properties";
	private static Document RDBMSDoc;

	private DatabaseFactory(){
	}
	
	private static void initializeProperties(){
		try {
			SAXBuilder builder = new SAXBuilder();
			RDBMSDoc = builder.build(new File(PROPS_PATH));			
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private static Element getDriverElement(String driver){
		initializeProperties();
		Element root = RDBMSDoc.getRootElement();
		
		Iterator iter = root.getChildren().iterator();
		while (iter.hasNext()){
			Element jdbcDriver = (Element)iter.next();
			if (jdbcDriver.getAttributeValue("driverName").equals(driver)){
				return jdbcDriver;
			}
		}	
		return null;
	}
	
	public static Database createDatabaseInstance(String driver, Database database){
		initializeProperties();
		Element jdbcDriver = getDriverElement(driver);
		String databaseImpl = jdbcDriver.getChild("databaseImpl").getValue();
		
		// return appropriate class with use of java reflection
		Class databaseClassDefinition;
	    Class[] databaseArgsClass = new Class[] {Database.class};
	    Object[] databaseArgs = new Object[] {database};
	    Constructor databaseArgsConstructor;
	    
	    try {
	    	databaseClassDefinition = Class.forName(databaseImpl);
	    	databaseArgsConstructor = 
	    		databaseClassDefinition.getConstructor(databaseArgsClass);
	    	
	    	return (Database) databaseArgsConstructor.newInstance(databaseArgs);
	      
	    } catch (ClassNotFoundException e) {
	          e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	          e.printStackTrace();
	    } catch (InvocationTargetException e) {
	          e.printStackTrace();
	    } catch (IllegalAccessException e) {
	          e.printStackTrace();
	    } catch (InstantiationException e) {
	          e.printStackTrace();
	    }
	      
	   	return null;
	}
	
	public static Table createTableInstance(String driver, Table table){
		initializeProperties();
		Element jdbcDriver = getDriverElement(driver);
		String tableImpl = jdbcDriver.getChild("tableImpl").getValue();
		
		// return appropriate class with use of java reflection
		Class tableClassDefinition;
	    Class[] tableArgsClass = new Class[] {Table.class};
	    Object[] tableArgs = new Object[] {table};
	    Constructor tableArgsConstructor;
	    
	    try {
	    	tableClassDefinition = Class.forName(tableImpl);
	    	tableArgsConstructor = 
	    		tableClassDefinition.getConstructor(tableArgsClass);
	    	
	    	return (Table) tableArgsConstructor.newInstance(tableArgs);
	      
	    } catch (ClassNotFoundException e) {
	          e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	          e.printStackTrace();
	    } catch (InvocationTargetException e) {
	          e.printStackTrace();
	    } catch (IllegalAccessException e) {
	          e.printStackTrace();
	    } catch (InstantiationException e) {
	          e.printStackTrace();
	    }
	      
	   	return null;
	}
	
	public static Column createColumnInstance(String driver, Column column){
		initializeProperties();
		Element jdbcDriver = getDriverElement(driver);
		String columnImpl = jdbcDriver.getChild("columnImpl").getValue();
		
		// return appropriate class with use of java reflection
		Class columnClassDefinition;
	    Class[] columnArgsClass = new Class[] {Column.class};
	    Object[] columnArgs = new Object[] {column};
	    Constructor columnArgsConstructor;
	    
	    try {
	    	columnClassDefinition = Class.forName(columnImpl);
	    	columnArgsConstructor = 
	    		columnClassDefinition.getConstructor(columnArgsClass);
	    	
	    	return (Column) columnArgsConstructor.newInstance(columnArgs);
	      
	    } catch (ClassNotFoundException e) {
	          e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	          e.printStackTrace();
	    } catch (InvocationTargetException e) {
	          e.printStackTrace();
	    } catch (IllegalAccessException e) {
	          e.printStackTrace();
	    } catch (InstantiationException e) {
	          e.printStackTrace();
	    }
	      
	   	return null;
	}
	
	public static PrimaryKey createPrimaryKeyInstance(String driver, PrimaryKey primaryKey){
		initializeProperties();
		Element jdbcDriver = getDriverElement(driver);
		String primaryKeyImpl = jdbcDriver.getChild("primaryKeyImpl").getValue();
		
		// return appropriate class with use of java reflection
		Class primaryKeyClassDefinition;
	    Class[] primaryKeyArgsClass = new Class[] {PrimaryKey.class};
	    Object[] primaryKeyArgs = new Object[] {primaryKey};
	    Constructor primaryKeyArgsConstructor;
	    
	    try {
	    	primaryKeyClassDefinition = Class.forName(primaryKeyImpl);
	    	primaryKeyArgsConstructor = 
	    		primaryKeyClassDefinition.getConstructor(primaryKeyArgsClass);
	    	
	    	return (PrimaryKey) primaryKeyArgsConstructor.newInstance(primaryKeyArgs);
	      
	    } catch (ClassNotFoundException e) {
	          e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	          e.printStackTrace();
	    } catch (InvocationTargetException e) {
	          e.printStackTrace();
	    } catch (IllegalAccessException e) {
	          e.printStackTrace();
	    } catch (InstantiationException e) {
	          e.printStackTrace();
	    }
	      
	   	return null;
	}
	
	public static TableRow createTableRowInstance(String driver, TableRow tableRow){
		initializeProperties();
		Element jdbcDriver = getDriverElement(driver);
		String tableRowImpl = jdbcDriver.getChild("tableRowImpl").getValue();
		
		// return appropriate class with use of java reflection
		Class tableRowClassDefinition;
	    Class[] tableRowArgsClass = new Class[] {TableRow.class};
	    Object[] tableRowArgs = new Object[] {tableRow};
	    Constructor tableRowArgsConstructor;
	    
	    try {
	    	tableRowClassDefinition = Class.forName(tableRowImpl);
	    	tableRowArgsConstructor = 
	    		tableRowClassDefinition.getConstructor(tableRowArgsClass);
	    	
	    	return (TableRow) tableRowArgsConstructor.newInstance(tableRowArgs);
	      
	    } catch (ClassNotFoundException e) {
	          e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	          e.printStackTrace();
	    } catch (InvocationTargetException e) {
	          e.printStackTrace();
	    } catch (IllegalAccessException e) {
	          e.printStackTrace();
	    } catch (InstantiationException e) {
	          e.printStackTrace();
	    }
	      
	   	return null;
	}
		
}
