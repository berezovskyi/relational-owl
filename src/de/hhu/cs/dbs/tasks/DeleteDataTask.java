package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModel;

import de.hhu.cs.dbs.DatabaseFactory;
import de.hhu.cs.dbs.DatabaseManagerFactory;
import de.hhu.cs.dbs.OntModelManager;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.interfaces.TableRow;

public class DeleteDataTask {
	private Connection connection;
	private String driver;
	private String schema = null;
	private DbManager dbManager;
	private OntModel schemaOntology = null;
	private OntModel dataOntology = null;
	private OntModelManager schemaOMM;
	private OntModelManager dataOMM;
	private Database database;
	private ArrayList dataRows;
	private Statement stmt;
	private int lengthOfTask;
	private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
    
	public DeleteDataTask(Connection connection, String driver, String schema) {
    	this.connection = connection;
    	this.driver = driver;
    	this.schema = schema;
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lengthOfTask = dbManager.getTables(schema).size();
	}
	
	public DeleteDataTask(Connection connection, String driver, OntModel schema, OntModel data){
		this.connection = connection;
    	this.driver = driver;
    	try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.schemaOntology = schema;
		this.dataOntology = data;
		schemaOMM = new OntModelManager(schemaOntology);
		dataOMM = new OntModelManager(dataOntology);
		database = DatabaseFactory.createDatabaseInstance(driver, schemaOMM.getDatabase());
		dataRows = dataOMM.getDataRows(database);

		// TODO lengthOfTask
		lengthOfTask = dataRows.size();
	}
	
	/**
     * Start the task.
     */
    public void go() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statMessage = null;
                return new DeleteData();
            }
        };
        worker.start();
    }
    
    /**
     * How much work needs to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * How much has been done.
     */
    public int getCurrent() {
        return current;
    }

    public void stop() {
        canceled = true;
        statMessage = null;
    }

    /**
     * Find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Get most recent status message, or null if there is 
     * no current status message.
     */
    public String getMessage() {
        return statMessage;
    }
   
    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class DeleteData {
    	DeleteData() {
    		
    		if (schema != null){
    			
    			// delete all data from all tables from schema
    			
	    		Iterator tables = dbManager.getTables(schema).iterator();
	    		while (tables.hasNext()){
	    			current++;
	    			Table table = (Table)tables.next();
	    			// delete data from table
	    			try {
	    				stmt.executeUpdate(table.deleteAllDataSQL());
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	    	}
    		else{
    			if ((schemaOntology != null) && (dataOntology != null)){
    				
    				// delete data which are occurring in dataOntology
    				
    				Iterator rows = dataRows.iterator();
    	    		while (rows.hasNext()){
    	    			current++;
    	    			TableRow row = DatabaseFactory.createTableRowInstance(driver, (TableRow)rows.next());
    	    			try {
    	    				stmt.executeUpdate(row.deleteSQL(getPrimaryColumns(row)));
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	    		}
    			}
    		}
    		
    		// message
			statMessage = "Completed " + current +
            " out of " + lengthOfTask + " tables.";
			if (current >= lengthOfTask) {
                done = true;
                current = lengthOfTask;
            }
	
    	}
    }

    private ArrayList getPrimaryColumns(TableRow row){
    	ArrayList pkCols = new ArrayList();
    	Table t = null;
    	
    	if (schemaOMM != null){
    		t = schemaOMM.getTable(row.database(), row.table());
    	}
    	
    	if (t != null){
    		Iterator tPkColsIter = t.primaryKey().getColumns().iterator();
    		while (tPkColsIter.hasNext()){
    			Column c = DatabaseFactory.createColumnInstance(this.driver, (Column)tPkColsIter.next());
    			
    			Iterator iter = row.columns().iterator();
        		while (iter.hasNext()){
        			Column c2 = (Column)iter.next();
        			if (c2.name().equals(c.name())){
        				c.setValue(c2.value());
        				pkCols.add(c);
        			}
        		}
    		}
    	}
    	
    	return pkCols;
    }
}
