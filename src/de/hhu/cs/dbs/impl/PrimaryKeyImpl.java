package de.hhu.cs.dbs.impl;

/**
 * <code>PrimaryKeyImpl</code> implements virtual RDBMS primary key that can be created 
 * from schema representation in OWL as well as from metadata received from RDBMS
 * 
 * @author Przemyslaw Dzikowski
 * @see de.hhu.cs.dbs.PrimaryKey
 */

import java.util.ArrayList;

import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.PrimaryKey;

public class PrimaryKeyImpl implements PrimaryKey {
	
	private ArrayList columns = new ArrayList();
	
	public PrimaryKeyImpl(){
	}
	
	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.PrimaryKey#addColumn(de.hhu.cs.dbs.Column)
	 */
	public void addColumn(Column c){
		columns.add(c);
	}

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.PrimaryKey#getColumns()
	 */
	public ArrayList getColumns() {
		return columns;
	}
	

	/* (non-Javadoc)
	 * @see de.hhu.cs.dbs.PrimaryKey#setColumns(java.util.ArrayList)
	 */
	public void setColumns(ArrayList columns) {
		this.columns = columns;
	}	
}
