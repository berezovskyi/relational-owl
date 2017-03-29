package de.hhu.cs.dbs.tasks;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

import de.hhu.cs.dbs.DatabaseManagerFactory;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.Table;

public class ExportSchemaTask {
	private static final String RELATIONAL_OWL = "http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#";
	private DbManager dbManager;
	private String schema;
	private OntModel relationalOntology;
	private OntModel schemaOntology;
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
	private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private boolean canceled = false;
    private String statMessage;
    
    public ExportSchemaTask(Connection connection, String driver, String schema) {
    	this.schema = schema;
    	initSchemaOntology();
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
    public void go() {
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                current = 0;
                done = false;
                canceled = false;
                statMessage = null;
                return new ExportSchema();
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
	

    /**
     * The export schema running task.  This runs in a SwingWorker thread.
     */
    class ExportSchema {
    	ExportSchema() {
    		int pkNumber = 0;
    		
    		// database
    		database = schemaOntology.createResource(baseNS + schema.trim());
    		schemaOntology.add(database, typeProperty, databaseClass);
    		// tables
    	    Iterator tables = dbManager.getTables(schema).iterator();
    	    while (tables.hasNext()){
    			Table dbsTable = (Table)tables.next();
    			String tableName = (dbsTable).name().trim();
    			current++;
    			try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			// table and primary key resource
    			//table = schemaOntology.createResource(baseNS + tableName.trim());
    			//primaryKey = schemaOntology.createResource(baseNS + "PK" + pkNumber++);
				Resource tableAsIdentifier = schemaOntology.createResource(baseNS + tableName.trim());
    			table = schemaOntology.createResource(tableName.trim());
				Resource primaryKeyAsIdentifier = schemaOntology.createResource(baseNS + "PK" + pkNumber);
				primaryKey = schemaOntology.createResource("PK" + pkNumber++);
    			schemaOntology.add(table, typeProperty, tableClass);
    			schemaOntology.add(primaryKey, typeProperty, primaryKeyClass);
    			schemaOntology.add(table, isIdentifiedBy, primaryKeyAsIdentifier);
    			schemaOntology.add(database, hasTable, tableAsIdentifier);
    			
    			// columns
    			Iterator columns = dbsTable.columns().iterator();
    			while (columns.hasNext()){
    				//  resource
    				Column dbsColumn = (Column)columns.next();
    				String columnName = dbsColumn.name().trim();
    				
    				column = schemaOntology.createResource(tableName + 
    						'.' + columnName);
    				Resource columnAsIdentifier = schemaOntology.createResource(baseNS + tableName + 
    						'.' + columnName);
    				schemaOntology.add(column, typeProperty, columnClass);
    				schemaOntology.add(table, hasColumn, columnAsIdentifier);
    				// domain
    				schemaOntology.add(column, domainProperty, tableAsIdentifier);
    				// range
    				if (dbsColumn.range().equals("integer"))				
    					schemaOntology.add(column, rangeProperty, XSD.integer);
    				if (dbsColumn.range().equals("string"))				
    					schemaOntology.add(column, rangeProperty, XSD.xstring);
    				if (dbsColumn.range().equals("dateTime"))				
    					schemaOntology.add(column, rangeProperty, XSD.dateTime);
    				if (dbsColumn.range().equals("decimal"))				
    					schemaOntology.add(column, rangeProperty, XSD.decimal);
    				if (dbsColumn.range().equals("date"))				
    					schemaOntology.add(column, rangeProperty, XSD.date);
    				if (dbsColumn.range().equals("float"))				
    					schemaOntology.add(column, rangeProperty, XSD.xfloat);
    				
    				// mysql data types
    				if (dbsColumn.range().equals("tinyint"))				
    					schemaOntology.add(column, rangeProperty, XSD.xint);
    				if (dbsColumn.range().equals("smallint"))				
    					schemaOntology.add(column, rangeProperty, XSD.xint);
    				if (dbsColumn.range().equals("mediumint"))				
    					schemaOntology.add(column, rangeProperty, XSD.xint);
    				if (dbsColumn.range().equals("bigint"))				
    					schemaOntology.add(column, rangeProperty, XSD.xint);
    				if (dbsColumn.range().equals("real"))				
    					schemaOntology.add(column, rangeProperty, XSD.decimal);
    				if (dbsColumn.range().equals("double"))				
    					schemaOntology.add(column, rangeProperty, XSD.decimal);
    				if (dbsColumn.range().equals("numeric"))				
    					schemaOntology.add(column, rangeProperty, XSD.decimal);
    				if (dbsColumn.range().equals("time"))				
    					schemaOntology.add(column, rangeProperty, XSD.dateTime);
    				if (dbsColumn.range().equals("timestamp"))				
    					schemaOntology.add(column, rangeProperty, XSD.dateTime);
    				if (dbsColumn.range().equals("char")){				
    					schemaOntology.add(column, rangeProperty, XSD.xstring);
    					schemaOntology.add(column, lengthProperty, 1);
    				}
    				if (dbsColumn.range().equals("tinytext")){				
    					schemaOntology.add(column, rangeProperty, XSD.xstring);
    					schemaOntology.add(column, lengthProperty, 255);
    				}
    				if (dbsColumn.range().equals("text")){				
    					schemaOntology.add(column, rangeProperty, XSD.xstring);
    					schemaOntology.add(column, lengthProperty, 65535);
    				}
    				if (dbsColumn.range().equals("mediumtext")){				
    					schemaOntology.add(column, rangeProperty, XSD.xstring);
    					schemaOntology.add(column, lengthProperty, 16777215);
    				}
    				if (dbsColumn.range().equals("longtext")){				
    					schemaOntology.add(column, rangeProperty, XSD.xstring);
    					// TODO Can Double represent 4,294,967,295?
    					schemaOntology.add(column, lengthProperty, new Double("4294967295").doubleValue());
    				}
    				if (dbsColumn.range().toUpperCase().equals("BIT")){				
    					schemaOntology.add(column, rangeProperty, XSD.xboolean);
    				}
    				// TODO MySQL types tinyblob, mediumblob, longblob
    				
    				// db2 data types
    				if (dbsColumn.range().toUpperCase().equals("boolean")){				
    					schemaOntology.add(column, rangeProperty, XSD.xboolean);
    				}
    				if (dbsColumn.range().toUpperCase().equals("smallint")){				
    					schemaOntology.add(column, rangeProperty, XSD.xint);
    				}
    				// TODO DB2 types blob, datalink, reference
    				
    				
    				// length
    				if (dbsColumn.length().length() > 0)
    					schemaOntology.add(column, lengthProperty, dbsColumn.length());
    				// scale
    				if (dbsColumn.scale().length() > 0)
    					schemaOntology.add(column, scaleProperty, dbsColumn.scale());
    				// primary key
    				if (dbsColumn.isPrimaryKey()){
    					schemaOntology.add(primaryKey, hasColumn, columnAsIdentifier);
    				}
    				// foreign key
    				if (dbsColumn.isForeignKey()){
    					foreignKey = schemaOntology
    								.getResource(baseNS + dbsColumn.references());
    					schemaOntology.add(column, references, foreignKey);
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
    }

	public OntModel getSchemaOntology() {
		return schemaOntology;
	}
}
