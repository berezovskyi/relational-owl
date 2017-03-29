package de.hhu.cs.dbs.mysql.interfaces;

/**
 * <code>MySQLColumn</code> represents virtual column in MySQL RDBMS that can 
 * be created from schema representation in OWL as well as from metadata 
 * received from MySQL RDBMS
 * 
 * @author Przemyslaw Dzikowski
 *
 */

import de.hhu.cs.dbs.interfaces.Column;

public interface MySQLColumn extends Column{

	/**
	 * @return Type of column.
	 */
	public String getType();

	/**
	 * @param type Type of column.
	 */
	public void setType(String type);

}