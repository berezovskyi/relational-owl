package de.hhu.cs.dbs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import de.hhu.cs.dbs.interfaces.constants.PropertiesConstants;

public class RelationalOWLProperties implements PropertiesConstants{
	
	private String PROPS_PATH = "connections.properties";
	private String SAMPLE_PROPS_PATH = "de/hhu/cs/dbs/samples/connections.properties";
	private Document propertyDoc;
	private Element jdbcConnection;
	
	public RelationalOWLProperties(String path){
		this.PROPS_PATH = path;
		
		try {
			SAXBuilder builder = new SAXBuilder();
			try{
				propertyDoc = builder.build(new File(PROPS_PATH));
			}
			catch (java.io.FileNotFoundException e){
				InputStream is=Thread.currentThread().getContextClassLoader().getSystemResourceAsStream(SAMPLE_PROPS_PATH);
				propertyDoc = builder.build(is);
				store();
			}
			Element root = propertyDoc.getRootElement();
			Iterator iter = root.getChildren().iterator();
			while (iter.hasNext()){
				Element jdbcConnection = (Element)iter.next();
				if (jdbcConnection.getAttributeValue("checked").equals("true")){
					this.jdbcConnection = jdbcConnection;
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void store(){
		try {
			XMLOutputter outp = new XMLOutputter();
			outp.output(propertyDoc, new FileOutputStream(new File(PROPS_PATH)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setActiveConnection(String connectionName){
		Element oldJDBCConnection = this.jdbcConnection;
		boolean foundConnection = false;
		
		Element root = propertyDoc.getRootElement();
		
		//	set new/reload connection "checked" attribute to "false"
		Iterator iter = root.getChildren().iterator();
		while (iter.hasNext()){
			Element jdbcConnection = (Element)iter.next();
			if (jdbcConnection.getAttributeValue("name").equals(connectionName)){
				foundConnection = true;
				jdbcConnection.setAttribute("checked", "true");
				this.jdbcConnection = jdbcConnection;
			}
		}
		
		// set actual connection "checked" attribute to "false"
		if (foundConnection){
			iter = root.getChildren().iterator();
			while (iter.hasNext()){
				Element jdbcConnection = (Element)iter.next();
				if (!jdbcConnection.getAttributeValue("name").equals(connectionName)
						&& jdbcConnection.getAttributeValue("checked").equals("true")){
					jdbcConnection.setAttribute("checked", "false");
				}
			}
		}
	}
	
	public ArrayList getConnectionNames(){
		ArrayList names = new ArrayList();
		
		try {
			SAXBuilder builder = new SAXBuilder();
			propertyDoc = builder.build(new File(PROPS_PATH));
			
			Element root = propertyDoc.getRootElement();
			Iterator iter = root.getChildren().iterator();
			while (iter.hasNext()){
				Element jdbcConnection = (Element)iter.next();
				names.add(jdbcConnection.getAttribute("name").getValue());
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return names;
	}
	
	public Element getJDBCConnection(String name){
		Element root = propertyDoc.getRootElement();
		Element jdbcConnection = null;
		
		Iterator iter = root.getChildren().iterator();
		while (iter.hasNext()){
			jdbcConnection = (Element)iter.next();
			if (jdbcConnection.getAttributeValue("name").equals(name)){
				return jdbcConnection;
			}
		}		
		return null;
	}
	
	public void addConnection(String name){
		this.jdbcConnection = new Element("jdbc-connection");
		this.jdbcConnection.setAttribute("name", name);
		this.jdbcConnection.setAttribute("checked", "false");
		Element child = new Element(PropertiesConstants.CMD_JDBC_DRIVER);
		this.jdbcConnection.addContent(child);
		child = new Element(PropertiesConstants.CMD_DB_URL);
		this.jdbcConnection.addContent(child);
		child = new Element(PropertiesConstants.CMD_DB_USER);
		this.jdbcConnection.addContent(child);
		child = new Element(PropertiesConstants.CMD_DB_PASSWORD);
		this.jdbcConnection.addContent(child);
		
		Element root = propertyDoc.getRootElement();
		root.addContent(this.jdbcConnection);
	}
	
	public void removeConnection(String name){
		try {
			SAXBuilder builder = new SAXBuilder();
			propertyDoc = builder.build(new File(PROPS_PATH));
			
			Element root = propertyDoc.getRootElement();
			Element jdbcConnection = null;
			
			Iterator iter = root.getChildren().iterator();
			while (iter.hasNext()){
				jdbcConnection = (Element)iter.next();
				if (jdbcConnection.getAttributeValue("name").equals(name)){
					root.removeContent(jdbcConnection);
					store();
					return;
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getActiveConnection(){
		Element root = propertyDoc.getRootElement();
		Iterator iter = root.getChildren().iterator();
		while (iter.hasNext()){
			Element jdbcConnection = (Element)iter.next();
			if (jdbcConnection.getAttributeValue("checked").equals("true")){
				return jdbcConnection.getAttributeValue("name");
			}
		}
		return "";
	}
	
	public String getConnectionName(){
		return jdbcConnection.getAttributeValue("name");
	}
	
	public String getDbUrl(){
		return jdbcConnection.getChild(PropertiesConstants.CMD_DB_URL).getText();
	}
	
	public String getDbUser(){
		return jdbcConnection.getChild(PropertiesConstants.CMD_DB_USER).getText();
	}

	public String getDbPassword(){
		return jdbcConnection.getChild(PropertiesConstants.CMD_DB_PASSWORD).getText();
	}

	public String getJDBCDriver(){
		return jdbcConnection.getChild(PropertiesConstants.CMD_JDBC_DRIVER).getText();
	}
	
	public void setConnectionName(String name){
		jdbcConnection.getAttribute("name").setValue(name);
	}
	
	public void setConnectionName(String connection, String name){
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null){
			jdbcConnection.setAttribute("name", name);
		}
	}
		
	public void setDbUrl(String dbUrl){
		jdbcConnection.getChild(PropertiesConstants.CMD_DB_URL).setText(dbUrl);
	}
	
	public void setDbUrl(String connection, String newDbUrl){
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null){
			jdbcConnection.getChild(PropertiesConstants.CMD_DB_URL).setText(newDbUrl);
		}
	}
	
	public void setDbUser(String dbUser){
		jdbcConnection.getChild(PropertiesConstants.CMD_DB_USER).setText(dbUser);
	}

	public void setDbUser(String connection, String newDbUser){
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null){
			jdbcConnection.getChild(PropertiesConstants.CMD_DB_USER).setText(newDbUser);
		}
	}
	
	public void setDbPassword(String dbPassword){
		jdbcConnection.getChild(PropertiesConstants.CMD_DB_PASSWORD).setText(dbPassword);
	}

	public void setDbPassword(String connection, String newDbPassword){
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null){
			jdbcConnection.getChild(PropertiesConstants.CMD_DB_PASSWORD).setText(newDbPassword);
		}
	}

	public void setJDBCDriver(String jdbcDriver){
		jdbcConnection.getChild(PropertiesConstants.CMD_JDBC_DRIVER).setText(jdbcDriver);
	}

	public void setJDBCDriver(String connection, String newJDBCDriver){
		Element jdbcConnection = getJDBCConnection(connection);
		if (jdbcConnection != null){
			jdbcConnection.getChild(PropertiesConstants.CMD_JDBC_DRIVER).setText(newJDBCDriver);
		}
	}

}
