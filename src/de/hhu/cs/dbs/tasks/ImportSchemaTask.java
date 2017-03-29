package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.XSD;

import de.hhu.cs.dbs.DatabaseFactory;
import de.hhu.cs.dbs.DatabaseManagerFactory;
import de.hhu.cs.dbs.DatabaseModelFactory;
import de.hhu.cs.dbs.OntModelManager;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.tasks.ExportSchemaTask.ExportSchema;

public class ImportSchemaTask {
	private DbManager dbManager;
	private Connection connection;
	private String driver;
	private String path;
	private OntModel schemaOntology;
	private OntModel dataOntology;
	private OntModelManager schemaOMM;
	private Database database;
	private Statement stmt;
	private ResultSet result;
	
	private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
   
	public ImportSchemaTask (Connection connection, String driver, String path) {
		this.connection = connection;
		this.driver = driver;
		this.path = path;
		initSchemaOntology();
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
	    } catch (Exception e) {
			// TODO ImportSchemaTask.java Exception
			e.printStackTrace();
		}
		lengthOfTask = database.getTables().size();
	}
    
	public ImportSchemaTask (Connection connection, String driver, OntModel schema) {
		this.connection = connection;
		this.driver = driver;
		this.schemaOntology = schema;
		//initSchemaOntology();
		schemaOMM = new OntModelManager(schemaOntology);
		database = DatabaseFactory.createDatabaseInstance(driver, schemaOMM.getDatabase());
	
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
	    } catch (Exception e) {
			// TODO ImportSchemaTask.java Exception
			e.printStackTrace();
		}
		lengthOfTask = database.getTables().size();
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
                return new ImportSchema();
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
    
    private void initSchemaOntology(){
    	try {
			schemaOntology = DatabaseModelFactory.createModelSchema(path);
		} catch (Exception e) {
			// TODO Handle Exception in task class
			e.printStackTrace();
		}
		schemaOMM = new OntModelManager(schemaOntology);
		database = DatabaseFactory.createDatabaseInstance(driver, schemaOMM.getDatabase());
	}
	
    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class ImportSchema {
    	ImportSchema() {
    		
    		try{
    			//check if database exists
    			result = stmt.executeQuery(database.checkIfExistsSQL());
    			if (result.next() && (result.getObject(1).toString().equals("0"))){
    				// create database
    				stmt.executeUpdate(database.createSQL());
    			}
    			else{
    				// mysql extract
    				result = stmt.executeQuery(database.checkIfExistsSQL());
    				if (!result.next()){
    					stmt.executeUpdate(database.createSQL());
    				}
    			}
    		
    			// create tables without foreign keys
    			ArrayList tables = database.getTables();
    			//lengthOfTask = tables.size();
    			Iterator iterTables = database.getTables().iterator();
    			while (iterTables.hasNext()){
    				current++;
    				Table t = DatabaseFactory.createTableInstance(driver, (Table)iterTables.next());
    				//	check if table exists
    				result = stmt.executeQuery(t.checkIfExistsSQL());
    				if (result.next() && (result.getObject(1).toString().equals("0"))){
    					// create table
    					stmt.executeUpdate(t.createSQL());
    				}
    				else{
    					// mysql extract
    					result = stmt.executeQuery(t.checkIfExistsSQL());
    					if (!result.next()){
    						stmt.executeUpdate(t.createSQL());
    					}
    				}
    			}
    			
    			// create foreign keys for tables
    			iterTables = database.getTables().iterator();
    			while (iterTables.hasNext()){
    				Table t = DatabaseFactory.createTableInstance(driver, (Table)iterTables.next());
    				if (t.createForeignKeySQL().length() > 0){
    					stmt.executeUpdate(t.createForeignKeySQL());
    				}
    			}	
    		}
    		catch (java.sql.SQLException e){
    			// TODO Handle Exception in task class
    			e.printStackTrace();
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
    
}
