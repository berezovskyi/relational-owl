package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import de.hhu.cs.dbs.DatabaseManagerFactory;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.tasks.ExportSchemaTask.ExportSchema;

public class ExportDataTask {
	private DbManager dbManager;
	private String driver;
	private String schema;
	private String schemaPath;
	private String dbinstNS;
	private OntModel schemaOntology;
	private OntModel dataOntology;
	private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
   
	public ExportDataTask(Connection connection, String driver, String schema, String schemaPath){
    	this.driver = driver;
		this.schema = schema;
    	this.schemaPath = schemaPath;
    	initDataOntology();
		try {
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lengthOfTask = dbManager.getTables(schema).size();
	}
    
    /**
     * Start the task.
     */
    public void go() throws Exception{
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statMessage = null;
                return new ExportData();
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
    	schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		//schemaPath = "file:" + schemaPath.replace("\\", "//");
		//dbinstNS = "file:" + schemaPath + "#";
	  	schemaPath = schemaPath.replace("\\", "/");
		dbinstNS = "file:///" + schemaPath + "#";
	}
	

    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class ExportData {
    	ExportData(){
    		
    		Iterator tables = dbManager.getTables(schema).iterator();
    		while (tables.hasNext()){
    			//	data for all tables
    			Table table = (Table)tables.next();
    			current++;
    			try {
    				// data for one table
    				java.sql.ResultSet data = dbManager.getData(table.getAllDataSQL());
        			java.sql.ResultSetMetaData metaData = data.getMetaData();
    				while (data.next()){
    					Resource tableResource = schemaOntology.getResource(dbinstNS + table.name());
    					Resource tableRow = dataOntology.createResource(tableResource);
    					for (int i = 1; i <= metaData.getColumnCount(); i++){
    						String columnName = metaData.getColumnName(i);
    						if (data.getObject(i) != null){
    							String columnValue = data.getObject(i).toString();
        						if ((metaData.getColumnType(i) == 4) || (metaData.getColumnType(i) == -7)){
        							if (columnValue.equals("true"))
        								columnValue = "1";
        							if (columnValue.equals("false"))
        								columnValue = "0";
        						}
    							Property column = schemaOntology.getProperty(dbinstNS + table.name() + "." + columnName);
    							RDFNode value = dataOntology.createLiteral(columnValue);
    							dataOntology.add(tableRow, column, value);
    						}
    					}
    				}
    			} catch (SQLException e) {
    				// TODO Exception MessageBox
    				e.printStackTrace();
    			} catch (Exception e) {
					// TODO Exception MessageBox
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

	public OntModel getDataOntology() {
		return dataOntology;
	}
}
