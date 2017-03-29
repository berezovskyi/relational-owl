package de.hhu.cs.dbs.mysql.impl;

/**
 * <code>MySQLColumnImpl</code> implements virtual column in MySQL RDBMS that can 
 * be created from schema representation in OWL as well as from metadata 
 * received from MySQL RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import de.hhu.cs.dbs.impl.ColumnImpl;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.mysql.interfaces.MySQLColumn;

public class MySQLColumnImpl implements MySQLColumn {
	private Column c = new ColumnImpl();
	private String type = "";
	
	/**
	 * Creates new MySQLColumnImpl
	 */
	public MySQLColumnImpl(){}
	
	/**
	 * Creates new MySQLColumnImpl wich wraps virtual {@link Column}
	 * @param column Column to wrap
	 */
	public MySQLColumnImpl(Column column){
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
		if (range.equals("int")){
			c.setRange("integer");
		}
		if (range.equals("varchar")){
			c.setRange("string");
		}
		if (range.equals("datetime")){
			c.setRange("dateTime");
		}
		if (range.equals("decimal")){
			c.setRange("decimal");
		}
		if (range.equals("date")){
			c.setRange("date");
		}
		
		//	TODO Another MySQL types when appenden to outcoming schema 
		if (range.equals("tinyint")){
			c.setRange("tinyint");
		}
		if (range.equals("smallint")){
			c.setRange("smallint");
		}
		if (range.equals("mediumint")){
			c.setRange("mediumint");
		}
		if (range.equals("bigint")){
			c.setRange("bigint");
		}
		if (range.equals("real")){
			c.setRange("real");
		}
		if (range.equals("float")){
			c.setRange("float");
		}
		if (range.equals("double")){
			c.setRange("double");
		}
		if (range.equals("numeric")){
			c.setRange("numeric");
		}
		if (range.equals("time")){
			c.setRange("time");
		}
		if (range.equals("timestamp")){
			c.setRange("timestamp");
		}
		if (range.equals("char")){
			c.setRange("char");
		}
		if (range.equals("tinyblob")){
			c.setRange("tinyblob");
		}
		if (range.equals("mediumblob")){
			c.setRange("mediumblob");
		}
		if (range.equals("longblob")){
			c.setRange("longblob");
		}
		if (range.equals("tinytext")){
			c.setRange("tinytext");
		}
		if (range.equals("text")){
			c.setRange("text");
		}
		if (range.equals("mediumtext")){
			c.setRange("mediumtext");
		}
		if (range.equals("longtext")){
			c.setRange("longtext");
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
	public void setTable(String tableName) {
		c.setTable(tableName);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.mysql.interfaces.MySQLColumn#getType()
	 */
	public String getType() {
		if (range().equals("string")) {
			type = "VARCHAR(" + length() + ")";
		}
		if (range().equals("integer")) {
			type = "INTEGER(11)";
		}
		if (range().equals("decimal")){
			type = "DECIMAL(" + length() + "," + scale() + ")";
		}
		if (range().equals("dateTime")){
			type = "DATETIME";
		}
		if (range().equals("date")){
			type = "DATE";
		}
		
		//	TODO 
		//  Another MySQL types when appended to schema.owl
		if (range().equals("tinyint")){
			type = "TINYINT(4)";
		}
		if (range().equals("smallint")){
			type = "SMALLINT(6)";
		}
		if (range().equals("mediumint")){
			type = "MEDIUMINT(8)";
		}
		if (range().equals("int")){
			type = "INT(11)";
		}
		if (range().equals("bigint")){
			type = "BIGINT(12)";
		}
		if (range().equals("real")){
			type = "REAL(" + length() + "," + scale() + ")";
		}
		if (range().equals("float")){
			type = "FLOAT(" + length() + "," + scale() + ")";
		}
		if (range().equals("double")){
			type = "DOUBLE(" + length() + "," + scale() + ")";
		}
		if (range().equals("numeric")){
			type = "NUMERIC(" + length() + "," + scale() + ")";
		}
		if (range().equals("time")){
			type = "TIME";
		}
		if (range().equals("timestamp")){
			type = "TIMESTAMP";
		}
		if (range().equals("char")){
			type = "CHAR(" + length() + ")";
		}
		if (range().equals("tinyblob")){
			type = "TINYBLOB";
		}
		if (range().equals("mediumblob")){
			type = "MEDIUMBLOB";
		}
		if (range().equals("longblob")){
			type = "LONGBLOB";
		}
		if (range().equals("tinytext")){
			type = "TINYTEXT";
		}
		if (range().equals("text")){
			type = "TEXT";
		}
		if (range().equals("mediumtext")){
			type = "MEDIUMTEXT";
		}
		if (range().equals("longtext")){
			type = "LONGTEXT";
		}
		// enum(val1, ...) and set(val1, ...) fail
		
		return type;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.mysql.interfaces.MySQLColumn#setType(java.lang.String)
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