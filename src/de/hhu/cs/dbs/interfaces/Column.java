package de.hhu.cs.dbs.interfaces;

/**
 * <code>Column</code> represents virtual RDBMS column that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */
public interface Column {

	/**
	 * Get column scale parameter
	 * @return Column scale parameter
	 */
	public abstract String scale();

	/**
	 * Set column scale parameter
	 * @param scale		Column scale parameter
	 */
	public abstract void setScale(String scale);

	/**
	 * Get column length parameter
	 * @return Column length parameter
	 */
	public abstract String length();

	/**
	 * Set column length parameter
	 * @param length	Column length parameter
	 */
	public abstract void setLength(String length);

	/**
	 * Get column range parameter
	 * @return Column range parameter
	 */
	public abstract String range();

	/**
	 * Set column range parameter
	 * @param range	Column range parameter
	 */
	public abstract void setRange(String range);

	/**
	 * Get table name in which this colum occours
	 * @return Table name 
	 */
	public abstract String table();

	/**
	 * Set table name in which this column occours
	 * @param name Table name
	 */
	public abstract void setTable(String name);

	/**
	 * Get column name
	 * @return Column name
	 */
	public abstract String name();

	/**
	 * Set column name
	 * @param name Column name
	 */
	public abstract void setName(String name);

	/**
	 * Get column value
	 * @return Column value
	 */
	public abstract String value();

	/**
	 * Set column value
	 * @param value Column value
	 */
	public abstract void setValue(String value);

	/**
	 * Check if column occours in primary key definition
	 * @return <code>true</code> if column occours in primary key, 
	 * <code>false</code> otherwise
	 */
	public abstract boolean isPrimaryKey();

	/**
	 * Set column to occour in primary key
	 * @param isPrimaryKey <code>true</code> if column occours in primary key,
	 * <code>false</code> otherwise
	 */
	public abstract void setIsPrimaryKey(boolean isPrimaryKey);

	/**
	 * Check wheather column occours in foreign key definition
	 * @return <code>true</code> if column occours in foreign key, 
	 * <code>false</code> otherwise
	 */
	public abstract boolean isForeignKey();

	/**
	 * Set column to occour in foreign key 
	 * @param isForeignKey <code>true</code> if column occours in foreign key, 
	 * <code>false</code> otherwise
	 */
	public abstract void setIsForeignKey(boolean isForeignKey);

	/**
	 * Get full qualified column name (i.e. in form tableName.columnName) beeing
	 * referenced in foreign key
	 * @return Full qualified column name 
	 */
	public abstract String references();

	/**
	 * Set full qualified column name (i.e. in form tableName.columnName) beeing 
	 * referenced in foreign key 
	 * @param columnReferenced Full qualified column name  
	 */
	public abstract void setReferences(String columnReferenced);

}