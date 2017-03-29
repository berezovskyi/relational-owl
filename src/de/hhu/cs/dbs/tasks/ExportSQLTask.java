package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
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

public class ExportSQLTask {
	private static final String RELATIONAL_OWL = "http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#";	// relational-owl ontology
	private String schema;
	private String schemaPath;
	private String SQL;
	private String dbinstNS;
	private DbManager dbManager;
	private OntModel relationalOntology;
	private OntModel schemaOntology;
	private OntModel dataOntology;
	private String dbsNS;
	private String rdfNS;
	private String rdfsNS;
	private String baseNS;
	private OntClass databaseClass;
	private OntClass tableClass;
	private OntClass columnClass;
	private OntClass primaryKeyClass;
	private Property typeProperty;
	private Property domainProperty;
	private Property rangeProperty;
	private OntProperty lengthProperty;
	private OntProperty scaleProperty;
	private OntProperty hasTable;
	private OntProperty hasColumn;
	private OntProperty isIdentifiedBy;
	private OntProperty references;	
	private Resource database;
	private Resource table;
	private Resource column;
	private Resource primaryKey;
	private Resource foreignKey;
	
	private long lengthOfTask;
    private long current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
   
	public ExportSQLTask (Connection connection, String driver, String schema, String schemaPath, String SQL) {
		this.schema = schema;
    	this.schemaPath = schemaPath;
		this.SQL = SQL;
		initSchemaOntology();
		initDataOntology();
		try {	
			dbManager = DatabaseManagerFactory.getDbManagerInstance(connection, driver);
		} catch (Exception e) {
			e.printStackTrace();
			//	TODO ExportSQLTask Exception
		}	
		lengthOfTask = lengthOfTask();
	}
    
    /**
     * Start the task.
     */
    public void go(){
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statMessage = null;
                return new ExportSQL();
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
    
    private void initSchemaOntology(){
		// ontologies
		relationalOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		relationalOntology.read(RELATIONAL_OWL);
		schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		schemaOntology.setNsPrefix("dbs",RELATIONAL_OWL);
		// namespaces
		dbsNS = RELATIONAL_OWL;
		rdfNS = relationalOntology.getNsPrefixURI("rdf");
		rdfsNS = relationalOntology.getNsPrefixURI("rdfs");
		baseNS = "#";
		// relational.owl properties and ressources
		databaseClass = relationalOntology.getOntClass(dbsNS + "Database");
		tableClass = relationalOntology.getOntClass(dbsNS + "Table");
		columnClass = relationalOntology.getOntClass(dbsNS + "Column");
		primaryKeyClass = relationalOntology.getOntClass(dbsNS + "PrimaryKey");
		typeProperty = relationalOntology.getProperty(rdfNS + "type");
		domainProperty = relationalOntology.getProperty(rdfsNS + "domain");
		rangeProperty = relationalOntology.getProperty(rdfsNS + "range");
		lengthProperty = relationalOntology.getOntProperty(dbsNS + "length");
		scaleProperty = relationalOntology.getOntProperty(dbsNS + "scale");
		hasTable = relationalOntology.getOntProperty(dbsNS + "hasTable");
		hasColumn = relationalOntology.getOntProperty(dbsNS + "hasColumn");
		isIdentifiedBy = relationalOntology.getOntProperty(dbsNS + "isIdentifiedBy");
		references = relationalOntology.getOntProperty(dbsNS + "references");
		// schema resources
		database = null;
		table = null;
		primaryKey = schemaOntology.createResource();
		column = null;
		foreignKey = null;	
	}
    
    private void initDataOntology(){
       	dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		//schemaPath = "file:" + schemaPath.replace("\\", "//"); Bug?
       	schemaPath = schemaPath.replace("\\", "/");
		dbinstNS = "file:///" + schemaPath + "#";
	}
	
	private OntModel exportSchemaSQL(String schemaPath, String SQL) throws SQLException{
		ResultSet result = getData(SQL);
		ResultSetMetaData metaData = result.getMetaData();
		String databaseName = "SQL_TEMP_SCHEMA";
		String tableName = "SQL_TEMP_VIEW";
		
		database = schemaOntology.createResource(databaseName);
		schemaOntology.add(database, typeProperty, databaseClass);		
		table = schemaOntology.createResource(baseNS + tableName);
		schemaOntology.add(table, typeProperty, tableClass);
		schemaOntology.add(database, hasTable, table);
		
		for (int i = 1; i <= metaData.getColumnCount(); i++){
			String columnName = metaData.getColumnName(i);
			column = schemaOntology.createResource(baseNS + tableName + 
							'.' + columnName);
			schemaOntology.add(column, typeProperty, columnClass);
			schemaOntology.add(table, hasColumn, column);
			
			String range, scale, length;
			range = metaData.getColumnTypeName(i);
			scale = new Integer(metaData.getScale(i)).toString();
			length = new Integer(metaData.getPrecision(i)).toString();
			// domain
			schemaOntology.add(column, domainProperty, table);
			// range
			if (range.toLowerCase().equals("integer") || range.toLowerCase().equals("int"))				
				schemaOntology.add(column, rangeProperty, XSD.integer);
			if (range.toLowerCase().equals("varchar"))			
				schemaOntology.add(column, rangeProperty, XSD.xstring);
			if (range.toLowerCase().equals("timestamp") || range.toLowerCase().equals("datetime"))				
				schemaOntology.add(column, rangeProperty, XSD.dateTime);
			if (range.toLowerCase().equals("decimal"))				
				schemaOntology.add(column, rangeProperty, XSD.decimal);
			if (range.toLowerCase().equals("date"))				
				schemaOntology.add(column, rangeProperty, XSD.date);
			if (range.toLowerCase().equals("float"))				
				schemaOntology.add(column, rangeProperty, XSD.xfloat);
			if (range.toLowerCase().equals("smallint"))				
				schemaOntology.add(column, rangeProperty, XSD.xint);
			if (range.toLowerCase().equals("tinyint"))				
				schemaOntology.add(column, rangeProperty, XSD.xint);
			// length
			if (length.length() > 0){
				schemaOntology.add(column, lengthProperty, length);
			}
			// scale
			if (scale.length() > 0){
				schemaOntology.add(column, scaleProperty, scale);
			}
		}
		return schemaOntology;
	}
	
	public OntModel exportSQL(String schema, String schemaPath, String SQL) throws SQLException{
		ResultSet result = getData(SQL);
		ResultSetMetaData metaData = result.getMetaData();
		while (result.next()){
			current++;
			Resource tableResource = schemaOntology.getResource(dbinstNS + "SQL_TEMP_VIEW");
			Resource tableRow = dataOntology.createResource(tableResource);
			for (int i = 1; i <= metaData.getColumnCount(); i++){
				String column = metaData.getColumnName(i);
				if (result.getObject(i) != null){
					Property columnProperty = schemaOntology.getProperty(dbinstNS + "SQL_TEMP_VIEW." + column);
					RDFNode value = dataOntology.createLiteral(result.getObject(i).toString());
					dataOntology.add(tableRow, columnProperty, value);
				}
			}
		}
		return dataOntology;
	}
	
	private ResultSet getData(String SQL){
    	try {
			return dbManager.getData(SQL);
		} catch (Exception e) {
			// TODO ExportSQLTask Exception
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
			// TODO ExportSQLTask Exception
			e.printStackTrace();
		}
    	return size;
    }
    
    private long lengthOfTask(){
    	return getDataSize(getData(SQL));
    }

    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class ExportSQL {
    	ExportSQL() {
    		
    		try {
				schemaOntology = exportSchemaSQL(schemaPath, SQL);
			} catch (SQLException e) {
				e.printStackTrace();
				//	TODO ExportSQLTask Exception
			}
    		
    		try {
				dataOntology = exportSQL(schema, schemaPath, SQL);
			} catch (SQLException e) {
				e.printStackTrace();
				// TODO ExportSQLTask Exception
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

    public OntModel getSchemaOntology(){
    	return schemaOntology;
    }
    
	public OntModel getDataOntology() {
		return dataOntology;
	}
}
