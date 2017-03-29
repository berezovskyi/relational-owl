package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import de.hhu.cs.dbs.DatabaseFactory;
import de.hhu.cs.dbs.DatabaseManagerFactory;
import de.hhu.cs.dbs.impl.TableImpl;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.Table;

public class ExportDataSelectedTask  {
	private DbManager dbManager;
	private String driver;
	private String schema;
	private String schemaPath;
	private String dbinstNS;
	private String[] tables;
	private OntModel schemaOntology;
	private OntModel dataOntology;
	private long lengthOfTask;
    private long current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
   
	public ExportDataSelectedTask (Connection connection, String driver, String schema, String schemaPath, String[] tables) {
    	this.driver = driver;
		this.schema = schema;
    	this.schemaPath = schemaPath;
    	this.tables = tables;
    	initDataOntology();
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
		} catch (Exception e) {
			// TODO ExportDataSelected Exception
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
                return new ExportDataSelected();
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
    
    private void initDataOntology(){	
    	schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		//schemaPath = "file:" + schemaPath.replace("\\", "//");
		//dbinstNS = "file:" + schemaPath + "#";
	  	schemaPath = schemaPath.replace("\\", "/");
		dbinstNS = "file:///" + schemaPath + "#";
	}
	
    private ResultSet getData(String SQL){
    	try {
			return dbManager.getData(SQL);
		} catch (Exception e) {
			// TODO ExportDataSelected Exception
			e.printStackTrace();
		}
		return null;
    }
    
    private long getDataSize(ResultSet result){
    	long size = 0;
    	try {
			while(result.next()){
				size++;
			}
		} catch (SQLException e) {
			// TODO ExportDataSelected Exception
			e.printStackTrace();
		}
    	return size;
    }
    
    private long lengthOfTask(){
    	long lengthOfTask = 0;
    	for (int i = 0; i < tables.length; i++) {
    		Table table = DatabaseFactory.createTableInstance(driver, new TableImpl(tables[i]));
			table.setDatabase(schema);
			lengthOfTask += getDataSize(getData(table.getAllDataSQL()));
			//String SQL = "SELECT * FROM " + schema + "." + tables[i];
			//lengthOfTask += getDataSize(getData(SQL));
		}
    	return lengthOfTask;
    }
    
    private OntModel exportTable(String schema, String schemaPath, String table) throws SQLException{
    	
		Iterator tables = dbManager.getTables(schema).iterator();
		while (tables.hasNext()){
			Table dbsTable = (Table)tables.next();
			if (table.toUpperCase().equals(dbsTable.name().toUpperCase())){
				ResultSet result = getData(dbsTable.getAllDataSQL());
				ResultSetMetaData metaData = result.getMetaData();
				// transform table rows to ontology data
				try {
					while (result.next()){
						current++;
						Resource tableResource = dataOntology.getResource(dbinstNS + table);
						Resource tableRow = dataOntology.createResource(tableResource);
						for (int i = 1; i <= metaData.getColumnCount(); i++){
							String column = metaData.getColumnName(i);
							if (result.getObject(i) != null){
								Property columnProperty = schemaOntology.getProperty(dbinstNS + table + "." + column);
								RDFNode value = dataOntology.createLiteral(result.getObject(i).toString());
								dataOntology.add(tableRow, columnProperty, value);
							}
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					throw new SQLException("SQL Exception by fetching the data for schema " + schema + ", table " + table);
				}
			}
		}
		return dataOntology;
	
    }
    
    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class ExportDataSelected {
    	ExportDataSelected() {
    		
    		try {
				if (tables.length > 0){
	    			dataOntology = exportTable(schema, schemaPath, tables[0]);
					for (int i = 1; i < tables.length; i++) {
						dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, 
								dataOntology.union(exportTable(schema, schemaPath, tables[i])));
					}
				}
			} catch (SQLException e) {
				// TODO ExportDataSelected Exception
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

	public OntModel getDataOntology() {
		return dataOntology;
	}
}
