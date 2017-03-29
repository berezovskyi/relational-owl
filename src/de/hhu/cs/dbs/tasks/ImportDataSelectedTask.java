package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import de.hhu.cs.dbs.interfaces.TableRow;
import de.hhu.cs.dbs.tasks.ExportSchemaTask.ExportSchema;

public class ImportDataSelectedTask {
	private DbManager dbManager;
	private Connection connection;
	private String driver;
	private String schemaPath;
	private String dataPath;
	private String[] tables;
	private OntModel schemaOntology;
	private OntModel dataOntology;
	private OntModelManager schemaOMM;
	private OntModelManager dataOMM;
	private Database database;
	private ArrayList dataRows;
	private long lengthOfTask;
    private long current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
    private Statement stmt;
	private ResultSet result;
	
	public ImportDataSelectedTask (Connection connection, String driver, String schemaPath, String dataPath, String[] tables) {
		this.connection = connection;
		this.driver = driver;
		this.schemaPath = schemaPath;
		this.dataPath = dataPath;
    	this.tables = tables;
    	initDataOntology();
    	try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO ImportDataSelected.java Exception
			e.printStackTrace();
		}
		lengthOfTask = lengthOfTask();
	}
    
	public ImportDataSelectedTask (Connection connection, String driver, OntModel schema, OntModel data, String[] tables) {
		this.connection = connection;
		this.driver = driver;
		this.schemaOntology = schema;
		this.dataOntology = data;
    	this.tables = tables;
    	//initDataOntology();
    	schemaOMM = new OntModelManager(schemaOntology);
		dataOMM = new OntModelManager(dataOntology);
		database = DatabaseFactory.createDatabaseInstance(driver, schemaOMM.getDatabase());
		dataRows = dataOMM.getDataRows(database);
		
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO ImportDataSelected.java Exception
			e.printStackTrace();
		}
		lengthOfTask = lengthOfTask();
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
                return new ImportDataSelected();
            }
        };
        worker.start();
    }
    
    /**
     * How much work needs to be done.
     */
    public long getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * How much has been done.
     */
    public long getCurrent() {
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
    
    private long lengthOfTask(){
    	long lengthOfTask = 0;
    	for (int i = 0; i < tables.length; i++) {
			Iterator rows = dataOMM.getDataRows(schemaOMM.getDatabase(), tables[i]).iterator();
	    	while (rows.hasNext()){
	    		lengthOfTask++;
	    	}
    	}
    	return lengthOfTask;
    }
    
    private void initDataOntology(){
    	try {
			schemaOntology = DatabaseModelFactory.createModelSchema(schemaPath);
		} catch (Exception e) {
			// TODO ImportDataSelected.java Exception
			e.printStackTrace();
		}
		dataOntology = DatabaseModelFactory.createModelData(dataPath);
		schemaOMM = new OntModelManager(schemaOntology);
		dataOMM = new OntModelManager(dataOntology);
		database = DatabaseFactory.createDatabaseInstance(driver, schemaOMM.getDatabase());
		dataRows = dataOMM.getDataRows(database);
	}
	
    public void importDataTable(String schemaPath, String dataPath, String table) throws SQLException{
    	Iterator rows = dataOMM.getDataRows(schemaOMM.getDatabase(), table).iterator();
		while (rows.hasNext()){
			TableRow row = DatabaseFactory.createTableRowInstance(driver, (TableRow)rows.next());
			if (row.table().equals(table)){
				current++;
				if (row.checkIfExistsSQL().length() > 0){
					result = stmt.executeQuery(row.checkIfExistsSQL());
					if (result.next() && (result.getInt(1) == 0)){
						stmt.executeUpdate(row.createSQL());
					}
				}
				else{
					//when mysql there is no need to check and row.checkIfExistsSQL().length() = 0
					stmt.executeUpdate(row.createSQL());
				}
			}
		}
	}

    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class ImportDataSelected {
    	ImportDataSelected() {
    		
    			// message
    			statMessage = "Completed " + current +
                " out of " + lengthOfTask + " tables.";
    			if (current >= lengthOfTask) {
                    done = true;
                    current = lengthOfTask;
                }
    			
    	}
    }

	public OntModel getDataOntology() {
		return dataOntology;
	}
}
