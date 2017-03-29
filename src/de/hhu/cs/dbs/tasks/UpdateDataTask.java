package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.sql.Statement;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.hhu.cs.dbs.DatabaseFactory;
import de.hhu.cs.dbs.DatabaseManagerFactory;
import de.hhu.cs.dbs.OntModelManager;
import de.hhu.cs.dbs.interfaces.DbManager;

public class UpdateDataTask {
	private Connection connection;
	private String driver;
	private String schemaPath = null;
	private String dataPath;
	private String schema;
	private OntModel schemaOntology;
	private OntModel dataOntology;
	private OntModelManager schemaOMM;
	private OntModelManager dataOMM;
	private DbManager dbManager;
	private Statement stmt;
	private int lengthOfTask;
	private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
    
	public UpdateDataTask(Connection connection, String driver, String schemaPath, String dataPath) {
    	this.connection = connection;
    	this.driver = driver;
    	this.schemaPath = schemaPath;
    	this.dataPath = dataPath;
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO UpdateDataTask.java Exception
			e.printStackTrace();
		}
		
		// calculate task length
		schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		if (!schemaPath.startsWith("file:"))
			schemaPath = "file:" + schemaPath.replace("\\", "//");
		schemaOntology.read(schemaPath);
		dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		if (!dataPath.startsWith("file:"))
			dataPath = "file:" + dataPath.replace("\\", "//");
		dataOntology.read(dataPath);
		
		schemaOMM = new OntModelManager(schemaOntology);
		dataOMM = new OntModelManager(dataOntology);
		this.schema = schemaOMM.getDatabase().name();
		//lengthOfTask = dataOMM.getDataRows(schemaOMM.getDatabase()).size();
		lengthOfTask = 1;
	}
	
	public UpdateDataTask(Connection connection, String driver, OntModel schema, OntModel data) {
    	this.connection = connection;
    	this.driver = driver;
    	this.schemaOntology = schema;
    	this.dataOntology = data;
    	this.schemaPath = null;
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO UpdateDataTask.java Exception
			e.printStackTrace();
		}
		
		// calculate task length
		schemaOMM = new OntModelManager(schemaOntology);
		dataOMM = new OntModelManager(dataOntology);
		this.schema = schemaOMM.getDatabase().name();
		//lengthOfTask = dataOMM.getDataRows(schemaOMM.getDatabase()).size();
		lengthOfTask = 1;
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
                return new UpdateData();
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
     * The export schemaPath running task.  This runs in a SwingWorker thread.
     */
    class UpdateData {
    	UpdateData() {
    		current++;
			
    		if (schemaPath != null){
    			
    			// delete all data from schema
	    		
    			DeleteDataTask deleteDataTask = new DeleteDataTask(connection, driver, schema);
	    		deleteDataTask.go();
	            while (!deleteDataTask.isDone()){
	                try {
						//current = deleteDataTask.getCurrent();
	                	Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
    		
    		
	            // create schema when does not exist
	            /*ImportSchemaTask importSchemaTask = new ImportSchemaTask(connection, driver, schemaPath);
	            importSchemaTask.go();
	            while (!importSchemaTask.isDone()){
	                try {
						//current = deleteDataTask.getCurrent();
	                	Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }*/
	            
	            // insert data again
	            ImportDataTask importDataTask = new ImportDataTask (connection, driver, schemaPath, dataPath);
	            importDataTask.go();
	            while (!importDataTask.isDone()){
	                try {
						current = importDataTask.getCurrent();
	                	Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	     	}
    		else{
    			if ((schemaOntology != null) && (dataOntology != null)){
    		
    				//	delete all data that are occuring in dataOntology
        			
    				DeleteDataTask deleteDataTask = new DeleteDataTask(connection, driver, schemaOntology, dataOntology);
    	    		deleteDataTask.go();
    	            while (!deleteDataTask.isDone()){
    	                try {
    						//current = deleteDataTask.getCurrent();
    	                	Thread.sleep(100);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    	            }	
    		
    	            // create schema when does not exist
    	            ImportSchemaTask importSchemaTask = new ImportSchemaTask(connection, driver, schemaOntology);
    	            importSchemaTask.go();
    	            while (!importSchemaTask.isDone()){
    	                try {
    						//current = deleteDataTask.getCurrent();
    	                	Thread.sleep(100);
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    	            }
    	            
    	            // insert data again
    	            ImportDataTask importDataTask = new ImportDataTask (connection, driver, schemaOntology, dataOntology);
    	            importDataTask.go();
    	            while (!importDataTask.isDone()){
    	                try {
    						current = importDataTask.getCurrent();
    	                	Thread.sleep(100);
    					} catch (InterruptedException e) {
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
                System.out.println("update done = " + done);
                current = lengthOfTask;
            }
    	}
    }

}
