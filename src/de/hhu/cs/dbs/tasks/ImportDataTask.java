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
import de.hhu.cs.dbs.interfaces.TableRow;
import de.hhu.cs.dbs.tasks.ExportSchemaTask.ExportSchema;

public class ImportDataTask {
	private DbManager dbManager;
	private Connection connection;
	private String driver;
	private String schemaPath;
	private String dataPath;
	private Statement stmt;
	private ResultSet result;
	private OntModel schemaOntology;
	private OntModel dataOntology;
	private OntModelManager schemaOMM;
	private OntModelManager dataOMM;
	private Database database;
	private ArrayList dataRows;

	private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
   
	public ImportDataTask (Connection connection, String driver, String schemaPath, String dataPath) {
		this.connection = connection;
		this.driver = driver;
		this.schemaPath = schemaPath;
		this.dataPath = dataPath;
		initDataOntology();
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO ImportDataTask.java Exception
			e.printStackTrace();
		}
		lengthOfTask = dataRows.size();
	}
    
	public ImportDataTask (Connection connection, String driver, OntModel schema, OntModel data) {
		this.connection = connection;
		this.driver = driver;
		this.schemaOntology = schema;
		this.dataOntology = data;
		//initDataOntology();
    	schemaOMM = new OntModelManager(schemaOntology);
		dataOMM = new OntModelManager(dataOntology);
		database = DatabaseFactory.createDatabaseInstance(driver, schemaOMM.getDatabase());
		dataRows = dataOMM.getDataRows(database);

		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
			this.stmt = this.connection.createStatement();
		} catch (Exception e) {
			// TODO ImportDataTask.java Exception
			e.printStackTrace();
		}
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
                return new ImportData();
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
    
    private void initDataOntology(){
    	try {
			schemaOntology = DatabaseModelFactory.createModelSchema(schemaPath);
		} catch (Exception e) {
			// TODO ImportDataTask.java exception
			e.printStackTrace();
		}
		dataOntology = DatabaseModelFactory.createModelData(dataPath);
		schemaOMM = new OntModelManager(schemaOntology);
		dataOMM = new OntModelManager(dataOntology);
		database = DatabaseFactory.createDatabaseInstance(driver, schemaOMM.getDatabase());
		dataRows = dataOMM.getDataRows(database);
	}
	
    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class ImportData {
    	ImportData() {
    		
    		Iterator rows = dataRows.iterator();
    		while (rows.hasNext()){
    			current++;
    			TableRow row = DatabaseFactory.createTableRowInstance(driver, (TableRow)rows.next());
    			try{
    				//	check if table exist in rdbms
    				Table table = DatabaseFactory.createTableInstance(driver, database.getTable(row.table()));
    				if (table.checkIfExistsSQL().length() > 0){
    					//if (table.getClass().toString().indexOf("de.hhu.cs.dbs.mysql.impl.MySQLTableImpl") == -1){
    					//	result = stmt.executeQuery(table.checkIfExistsSQL());
    					//	if (result.next() && (result.getInt(1) != 0)){
    							//	check if row exist
    							//System.out.println("check = " + row.checkIfExistsSQL());
								if (row.checkIfExistsSQL(getPrimaryColumns(row)).length() > 0){
									result = stmt.executeQuery(row.checkIfExistsSQL(getPrimaryColumns(row)));
    								if (result.next() && (result.getInt(1) == 0)){
    									// TODO narrow throtle
    									// extend TableRow to create patches of data for each table
    									// it will be possible to enormous save on calling
    									// the ALERT TABLE DROP CONSTRAINT sql
    									
    									// insert new data
    									stmt.executeUpdate(row.createSQL());
    									System.out.println(row.createSQL());
        							}
    							}
    					/*	}
    					}
    					else{
    						//when mysql there is no need to check and t.checkIfExistsSQL().length() = 0
    						stmt.executeUpdate(row.createSQL());
    					}*/
    				}
    			}
    			catch (java.sql.SQLException e){
    				// TODO Handle exception in task class
    				e.printStackTrace();
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
    
	public OntModel getDataOntology() {
		return dataOntology;
	}
}
