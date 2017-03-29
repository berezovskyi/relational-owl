package de.hhu.cs.dbs.db2.interfaces;

/**
 * <code>DB2Column</code> represents virtual column in DB2 RDBMS that 
 * can be created from schema representation in OWL as well as from metadata 
 * received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see Column
 */

import de.hhu.cs.dbs.interfaces.Column;

public interface DB2Column extends Column{

	/**
	 * @return Type of column.
	 */
	public String getType();

	/**
	 * @param type Type of column.
	 */
	public void setType(String type);

}