package de.hhu.cs.dbs.db2.impl;

/**
 * <code>DB2ColumnImpl</code> implements virtual column in DB2 RDBMS that 
 * can be created from schema representation in OWL as well as from metadata 
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see Column
 * @see DB2Column
 */

import de.hhu.cs.dbs.db2.interfaces.DB2Column;
import de.hhu.cs.dbs.impl.ColumnImpl;
import de.hhu.cs.dbs.interfaces.Column;

public class DB2ColumnImpl implements DB2Column {
	private Column c = new ColumnImpl();
	private String type = "";
	
	/**
	 * Creates new DB2ColumnImpl
	 */
	public DB2ColumnImpl(){}
	
	/**
	 * Creates new DB2ColumnImpl wich wraps virtual {@link Column}
	 * @param column Column to wrap
	 */
	public DB2ColumnImpl(Column column){
		this.c = column;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#scale()
	 */
	public String scale() {
		return c.scale();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setScale(java.lang.String)
	 */
	public void setScale(String scale) {
		c.setScale(scale);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#length()
	 */
	public String length() {
		String length = "";
		if ((c.length().length() > 0) && (new Integer(c.length()).intValue() > 2024)){
			length = "2024";
		}
		else{
			length = c.length();
		}
		return length;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setLength(java.lang.String)
	 */
	public void setLength(String length) {
		c.setLength(length);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#range()
	 */
	public String range() {
		return c.range();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setRange(java.lang.String)
	 */
	public void setRange(String range) {
		if (range.toUpperCase().equals("INTEGER")){
			c.setRange("integer");
		}
		if (range.toUpperCase().equals("VARCHAR")){
			c.setRange("string");
		}
		if (range.toUpperCase().equals("CHARACTER VARYING")){
			c.setRange("string");
		}
		if (range.toUpperCase().equals("CHARACTER")){
			c.setRange("string");
		}
		if (range.toUpperCase().equals("TIME")){
			c.setRange("dateTime");
		}
		if (range.toUpperCase().equals("TIMESTAMP")){
			c.setRange("dateTime");
		}
		if (range.toUpperCase().equals("DECIMAL")){
			c.setRange("decimal");
		}
		if (range.toUpperCase().equals("DATE")){
			c.setRange("date");
		}
		
		//	TODO Another DB2 types when appended to outcoming schema
		if (range.toUpperCase().equals("BIGINT")){
			c.setRange("integer");
		}
		if (range.toUpperCase().equals("BLOB")){
			c.setRange("blob");
		}
		if (range.toUpperCase().equals("BOOLEAN")){
			c.setRange("boolean");
		}
		if (range.toUpperCase().equals("DATALINK")){
			c.setRange("datalink");
		}
		if (range.toUpperCase().equals("DOUBLE")){
			c.setRange("double");
		}
		if (range.toUpperCase().equals("LONG VARCHAR")){
			c.setRange("string");
		}
		if (range.toUpperCase().equals("REAL")){
			c.setRange("real");
		}
		if (range.toUpperCase().equals("REFERENCE")){
			c.setRange("reference");
		}
		if (range.toUpperCase().equals("SMALLINT")){
			c.setRange("smallint");
		}
		
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#table()
	 */
	public String table() {
		return c.table();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setTable(java.lang.String)
	 */
	public void setTable(String name) {
		c.setTable(name);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.db2.interfaces.DB2Column#getType()
	 */
	public String getType() {
		if (range().equals("string")) {
			type = "VARCHAR(" + length() + ")";
		}
		if (range().equals("integer")) {
			type = "INTEGER";
		}
		if (range().equals("decimal")){
			type = "DECIMAL(" + length() + "," + scale() + ")";
		}
		if (range().equals("dateTime")){
			type = "TIMESTAMP";
		}
		if (range().equals("date")){
			type = "DATE";
		}
		
		//	TODO Another DB2 types when appended to schema.owl
		if (range().equals("tinyint")){
			type = "SMALLINT";
		}
		if (range().equals("smallint")){
			type = "SMALLINT";
		}
		if (range().equals("mediumint")){
			type = "INTEGER";
		}
		if (range().equals("int")){
			type = "INTEGER";
		}
		if (range().equals("bigint")){
			type = "BIGINT";
		}
		if (range().equals("real")){
			type = "REAL(" + length() + "," + scale() + ")";
		}
		if (range().equals("float")){
			type = "DECIMAL(" + length() + "," + scale() + ")";
		}
		if (range().equals("double")){
			type = "DOUBLE(" + length() + "," + scale() + ")";
		}
		if (range().equals("numeric")){
			type = "DECIMAL(" + length() + "," + scale() + ")";
		}
		if (range().equals("time")){
			type = "TIME";
		}
		if (range().equals("timestamp")){
			type = "TIMESTAMP";
		}
		if (range().equals("char")){
			type = "CHARACTER(" + length() + ")";
		}
		if (range().equals("tinyblob")){
			type = "BLOB";
		}
		if (range().equals("mediumblob")){
			type = "BLOB";
		}
		if (range().equals("longblob")){
			type = "BLOB";
		}
		if (range().equals("tinytext")){
			type = "VARCHAR(32)";
		}
		if (range().equals("text")){
			type = "VARCHAR(64)";
		}
		if (range().equals("mediumtext")){
			type = "VARCHAR(128)";
		}
		if (range().equals("longtext")){
			type = "VARCHAR(255)";
		}
		
		return type;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.db2.interfaces.DB2Column#setType(java.lang.String)
	 */
	public void setType(String type){
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#name()
	 */
	public String name() {
		return c.name();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setName(java.lang.String)
	 */
	public void setName(String name) {
		c.setName(name);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#isPrimaryKey()
	 */
	public boolean isPrimaryKey() {
		return c.isPrimaryKey();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setIsPrimaryKey(boolean)
	 */
	public void setIsPrimaryKey(boolean isPrimaryKey){
		c.setIsPrimaryKey(isPrimaryKey);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#isForeignKey()
	 */
	public boolean isForeignKey() {
		return c.isForeignKey();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setIsForeignKey(boolean)
	 */
	public void setIsForeignKey(boolean isForeignKey){
		c.setIsForeignKey(isForeignKey);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#value()
	 */
	public String value(){
		return c.value();
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setValue(java.lang.String)
	 */
	public void setValue(String value){
		c.setValue(value);
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#references()
	 */
	public String references(){
		return c.references();
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.interfaces.Column#setReferences(java.lang.String)
	 */
	public void setReferences(String columnReferenced){
		c.setReferences(columnReferenced);
	}
}
