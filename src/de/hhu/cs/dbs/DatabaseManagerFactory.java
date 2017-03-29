package de.hhu.cs.dbs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.constants.DriverConstants;

public class DatabaseManagerFactory  implements DriverConstants{
	
	private final static String PROPS_PATH = "rdbms.properties";
	private static Document RDBMSDoc;
	
	private DatabaseManagerFactory(){
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
	
	public static DbManager getDbManagerInstance(String driver, String dbUrl, String dbUser, String dbPassword) throws Exception{
		initializeProperties();
		Element jdbcDriver = getDriverElement(driver);
		String managerClassName = jdbcDriver.getChild("managerClassName").getValue();
		
		// return appropriate class with use of java reflection
		Class dbManagerDefinition;
	    Class[] stringArgsClass = new Class[] {String.class, String.class, String.class, String.class};
	    Object[] stringArgs = new Object[] {driver, dbUrl, dbUser, dbPassword};
	    Constructor stringArgsConstructor;
	    
	    try {
	    	dbManagerDefinition = Class.forName(managerClassName);
	    	stringArgsConstructor = 
	    		dbManagerDefinition.getConstructor(stringArgsClass);
	    	return (DbManager) stringArgsConstructor.newInstance(stringArgs);
	    } catch (ClassNotFoundException e) {
	          e.printStackTrace();
	          throw new Exception(e.getMessage());
	    } catch (NoSuchMethodException e) {
	          e.printStackTrace();
	          throw new Exception(e.getMessage());
	    }   
	}
	
    public static DbManager getDbManagerInstance(Connection connection, String driver) throws Exception{
		initializeProperties();
		Element jdbcDriver = getDriverElement(driver);
		String managerClassName = jdbcDriver.getChild("managerClassName").getValue();
		
		// return appropriate class with use of java reflection
		Class dbManagerDefinition;
	    Class[] stringArgsClass = new Class[] { Connection.class};
	    Object[] stringArgs = new Object[] { connection};
	    Constructor stringArgsConstructor;
	    
	    try {
	    	dbManagerDefinition = Class.forName(managerClassName);
	    	stringArgsConstructor = 
	    		dbManagerDefinition.getConstructor(stringArgsClass);
	    	return (DbManager) stringArgsConstructor.newInstance(stringArgs);
	    } catch (ClassNotFoundException e) {
	          e.printStackTrace();
	          throw new Exception(e.getMessage());
	    } catch (NoSuchMethodException e) {
	          e.printStackTrace();
	          throw new Exception(e.getMessage());
	    }
	}
		
}
