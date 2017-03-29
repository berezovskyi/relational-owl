package de.hhu.cs.dbs.impl;

/**
 * <code>ColumnImpl</code> implements virtual RDBMS column that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see Column
 */

import de.hhu.cs.dbs.interfaces.Column;

public class ColumnImpl implements Column {
	private String name = "";
	private String type = "";
	private String tableName = "";
	private String range = "";
	private String length = "";
	private String scale = "";
	private boolean isPrimaryKey = false;
	private boolean isForeignKey = false;
	private String value = "";
	private String columnReferenced = "";
	
	public ColumnImpl(){
	}
	
	public ColumnImpl(String name){
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#scale()
	 */
	public String scale() {
		return scale;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setScale(java.lang.String)
	 */
	public void setScale(String scale) {
		this.scale = scale;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#length()
	 */
	public String length() {
		return length;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setLength(java.lang.String)
	 */
	public void setLength(String length) {
		this.length = length;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#range()
	 */
	public String range() {
		return range;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setRange(java.lang.String)
	 */
	public void setRange(String range) {
		this.range = range;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#table()
	 */
	public String table() {
		return tableName;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setTable(java.lang.String)
	 */
	public void setTable(String tableName) {
		this.tableName = tableName;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#getType()
	 */
	public String getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setType(java.lang.String)
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#name()
	 */
	public String name() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#isPrimaryKey()
	 */
	public boolean isPrimaryKey(){
		return isPrimaryKey;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setIsPrimaryKey(boolean)
	 */
	public void setIsPrimaryKey(boolean isPrimaryKey){
		this.isPrimaryKey = isPrimaryKey;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#value()
	 */
	public String value(){
		return value;
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setValue(java.lang.String)
	 */
	public void setValue(String value){
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#createSQL()
	 */
	public String createSQL(){
		return "";
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#isForeignKey()
	 */
	public boolean isForeignKey(){
		return isForeignKey;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setIsForeignKey(boolean)
	 */
	public void setIsForeignKey(boolean isForeignKey){
		this.isForeignKey = isForeignKey;		
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#references()
	 */
	public String references(){
		return columnReferenced;
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.Column#setReferences(java.lang.String)
	 */
	public void setReferences(String columnReferenced){
		this.columnReferenced = columnReferenced;
	}

}
