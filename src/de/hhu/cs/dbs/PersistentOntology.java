package de.hhu.cs.dbs;

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

import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.interfaces.TableRow;

public class PersistentOntology { 
	private static final String RELATIONAL_OWL = "http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#";	// relational-owl ontology
	private RelationalOWLProperties props;
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
	
	public PersistentOntology(RelationalOWLProperties props) throws Exception{
		this.props = props;
	}
	
	private void initSchemaOntology(){
		// ontologies
		relationalOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		relationalOntology.read(RELATIONAL_OWL);
		schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dataOntology.setNsPrefix("dbs",RELATIONAL_OWL);
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
	
	private void checkExportSchema(String schema) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
	}
	
	private void checkExportDataAll(String schema, String schemaPath) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");	
	}
	
	private void checkExportTable(String schema, String schemaPath, String name) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");
		if (name == null || name.length() == 0)
			throw new Exception("Table name have to be passed as well.");
	}
	
	private void checkExportSQL(String schema, String schemaPath, String sql) throws Exception{
		if (schema == null || schema.length() == 0)
			throw new Exception("Enter schema name from which data should be exported.");
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");
		if (sql == null || sql.length() == 0)
			throw new Exception("You have to pass SQL-Statement as well.");	
	}
	
	private void checkExportSchemaSQL(String schemaPath, String sql) throws Exception{
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Enter path to schema that the data should be an instance from.");
		if (sql == null || sql.length() == 0)
			throw new Exception("You have to pass SQL-Statement as well.");	
	}
	
	private void checkImportSchema(String path) throws Exception{
		if (path == null || path.length() == 0)
			throw new Exception("Schema path has to be given.");
	}
	
	private void checkImportData(String schemaPath, String dataPath) throws Exception{
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Schema path has to be given.");
		if (dataPath == null || dataPath.length() == 0)
			throw new Exception("Data path has to be given.");	
	}

	private void checkImportDataTable(String schemaPath, String dataPath, String table) throws Exception{
		if (schemaPath == null || schemaPath.length() == 0)
			throw new Exception("Schema path has to be given.");
		if (dataPath == null || dataPath.length() == 0)
			throw new Exception("Data path has to be given.");	
		if (table == null || table.length() == 0)
			throw new Exception("Table to import has to be given.");		
	}
	
	public void importSchema(String path) throws Exception{
		checkImportSchema(path);
		OntModel m = DatabaseModelFactory.createModelSchema(path);
		OntModelManager ontModelManager = new OntModelManager(m);
		
		// create database instance
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		Database d = DatabaseFactory.createDatabaseInstance(props.getJDBCDriver(), ontModelManager.getDatabase());
		try{
			java.sql.Connection conn = dbManager.getConnection();
			java.sql.Statement stmt = conn.createStatement();
			//check if database exists
			java.sql.ResultSet result = stmt.executeQuery(d.checkIfExistsSQL());
			if (result.next() && (result.getObject(1).toString().equals("0"))){
				stmt.executeUpdate(d.createSQL());
			}
			else{
				// mysql check 
				result = stmt.executeQuery(d.checkIfExistsSQL());
				if (!result.next()){
					stmt.executeUpdate(d.createSQL());
				}
			}
		
			// create tables without foreign keys
			Iterator iterTables = d.getTables().iterator();
			while (iterTables.hasNext()){
				Table t = DatabaseFactory.createTableInstance(props.getJDBCDriver(), (Table)iterTables.next());
				//	check if table exists
				result = stmt.executeQuery(t.checkIfExistsSQL());
				if (result.next() && (result.getObject(1).toString().equals("0"))){
					stmt.executeUpdate(t.createSQL());
				}
				else{
					// mysql check
					result = stmt.executeQuery(t.checkIfExistsSQL());
					if (!result.next()){
						stmt.executeUpdate(t.createSQL());
					}
				}
			}
			
			// create foreign keys for tables
			iterTables = d.getTables().iterator();
			while (iterTables.hasNext()){
				Table t = DatabaseFactory.createTableInstance(props.getJDBCDriver(), (Table)iterTables.next());
				if (t.createForeignKeySQL().length() > 0){
					stmt.executeUpdate(t.createForeignKeySQL());
				}
			}	
		}
		catch (java.sql.SQLException e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
		
	public void importData(String schemaPath, String dataPath) throws Exception{
		checkImportData(schemaPath, dataPath);
		OntModel modelSchema = DatabaseModelFactory.createModelSchema(schemaPath);
		OntModel modelData = DatabaseModelFactory.createModelData(dataPath);
		OntModelManager schemaOntModelManager = new OntModelManager(modelSchema);
		OntModelManager dataOntModelManager = new OntModelManager(modelData);
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		Database database = DatabaseFactory.createDatabaseInstance(props.getJDBCDriver(), schemaOntModelManager.getDatabase());
		
		Iterator dataRows = dataOntModelManager.getDataRows(database).iterator();
		while (dataRows.hasNext()){
			TableRow row = DatabaseFactory.createTableRowInstance(props.getJDBCDriver(), (TableRow)dataRows.next());
			//	add data to database
			try{
				java.sql.Connection conn = dbManager.getConnection();
				java.sql.Statement stmt = conn.createStatement();
				
				//	check if table (and dafault database) exist in rdbms
				Table table = DatabaseFactory.createTableInstance(props.getJDBCDriver(), database.getTable(row.table()));
				if (table.checkIfExistsSQL().length() > 0){
					if (table.getClass().toString().indexOf("de.hhu.cs.dbs.mysql.impl.MySQLTableImpl") == -1){
						java.sql.ResultSet result = stmt.executeQuery(table.checkIfExistsSQL());
						if (result.next() && (result.getInt(1) != 0)){
							//	check if row exist
							if (row.checkIfExistsSQL().length() > 0){
								result = stmt.executeQuery(row.checkIfExistsSQL());
								if (result.next() && (result.getInt(1) == 0)){
									// TODO narrow throtle
									// extend TableRow to create patches of data for each table
									// it will be possible to enormous save on calling
									// the ALERT TABLE DROP CONSTRAINT sql
									
									// drop foreign keys if existing
									//dbManager.dropForeignKeys(d.getName(), table.name());
									
									// insert new data
									stmt.executeUpdate(row.createSQL());
									
									// restore foreign keys
									//if (table.createForeignKeySQL().length() > 0){
									//	stmt.executeUpdate(table.createForeignKeySQL());
									//}
								}
							}
						}
					}
					else{
						//when mysql there is no need to check and t.checkIfExistsSQL().length() = 0
						stmt.executeUpdate(row.createSQL());
					}
				}
			}
			catch (java.sql.SQLException e){
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}	
		}
	}
	
	public void importDataTable(String schemaPath, String dataPath, String table) throws Exception{
		checkImportDataTable(schemaPath, dataPath, table);
		OntModel modelSchema = DatabaseModelFactory.createModelSchema(schemaPath);
		OntModel modelData = DatabaseModelFactory.createModelData(dataPath);
		OntModelManager schemaOntModelManager = new OntModelManager(modelSchema);
		OntModelManager dataOntModelManager = new OntModelManager(modelData);
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		
		Iterator dataRows = dataOntModelManager.getDataRows(schemaOntModelManager.getDatabase()).iterator();
		while (dataRows.hasNext()){
			TableRow row = DatabaseFactory.createTableRowInstance(props.getJDBCDriver(), (TableRow)dataRows.next());
			if (row.table().equals(table)){
				//	add data to database
				try{
					java.sql.Connection conn = dbManager.getConnection();
					java.sql.Statement stmt = conn.createStatement();
					//	check if database exists
					if (row.checkIfExistsSQL().length() > 0){
						java.sql.ResultSet result = stmt.executeQuery(row.checkIfExistsSQL());
						if (result.next() && (result.getInt(1) == 0)){
							stmt.executeUpdate(row.createSQL());
						}
					}
					else{
						//when mysql there is no need to check and t.checkIfExistsSQL().length() = 0
						stmt.executeUpdate(row.createSQL());
					}
				}
				catch (java.sql.SQLException e){
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
			}
		}
	}
	
	public OntModel exportSchema(String schema) throws Exception{
		checkExportSchema(schema);
		initSchemaOntology();
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		database = schemaOntology.createResource(baseNS + schema);
		schemaOntology.add(database, typeProperty, databaseClass);
		int pkNumber = 0;
		// tables
		Iterator tables = dbManager.getTables(schema).iterator();
		while (tables.hasNext()){
			Table dbsTable = (Table)tables.next();
			String tableName = (dbsTable).name().trim();
			// table and primary key resource
			table = schemaOntology.createResource(baseNS + tableName.trim());
			primaryKey = schemaOntology.createResource(baseNS + "PK" + pkNumber++);
			schemaOntology.add(table, typeProperty, tableClass);
			schemaOntology.add(primaryKey, typeProperty, primaryKeyClass);
			schemaOntology.add(table, isIdentifiedBy, primaryKey);
			schemaOntology.add(database, hasTable, table);
			
			// columns
			Iterator columns = dbsTable.columns().iterator();
			while (columns.hasNext()){
				// column resource
				Column dbsColumn = (Column)columns.next();
				String columnName = dbsColumn.name().trim();
				column = schemaOntology.createResource(baseNS + tableName + 
				//column = schemaOntology.createResource(tableName + 
						'.' + columnName);
				schemaOntology.add(column, typeProperty, columnClass);
				schemaOntology.add(table, hasColumn, column);
				// domain
				schemaOntology.add(column, domainProperty, table);
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
				if (dbsColumn.range().equals("smallint"))				
					schemaOntology.add(column, rangeProperty, XSD.xint);
				if (dbsColumn.range().equals("tinyint"))				
					schemaOntology.add(column, rangeProperty, XSD.xint);
				
				// length
				if (dbsColumn.length().length() > 0){
					schemaOntology.add(column, lengthProperty, dbsColumn.length());
				}
				// scale
				if (dbsColumn.scale().length() > 0){
					schemaOntology.add(column, scaleProperty, dbsColumn.scale());
				}
				// primary key
				if (dbsColumn.isPrimaryKey()){
					schemaOntology.add(primaryKey, hasColumn, column);
				}
				// foreign key
				if (dbsColumn.isForeignKey()){
					foreignKey = schemaOntology
								.getResource(baseNS + dbsColumn.references().trim());
					//			.getResource(dbsColumn.references().toUpperCase());
					schemaOntology.add(column, references, foreignKey);
				}
			}
		}
		return schemaOntology;
	}
		
	public OntModel exportDataAll(String schema, String schemaPath) throws Exception{
		checkExportDataAll(schema, schemaPath);
		OntModel schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		schemaPath = "file:" + schemaPath.replace("\\", "//");
		String dbinst = "file:" + schemaPath + "#";
		
		Iterator tables = dbManager.getTables(schema).iterator();
		while (tables.hasNext()){
			//	data for all tables
			String tableName = ((Table)tables.next()).name().trim();
			String sql = "SELECT * FROM " + schema.trim() + "." + tableName;
			java.sql.ResultSet data = dbManager.getData(sql);
			try {
				// data for one table
				java.sql.ResultSetMetaData metaData = data.getMetaData();
				while (data.next()){
					Resource table = schemaOntology.getResource(dbinst + tableName.trim());
					Resource tableRow = dataOntology.createResource(table);
					for (int i = 1; i <= metaData.getColumnCount(); i++){
						String columnName = metaData.getColumnName(i);
						if (data.getObject(i) != null){
							Property column = schemaOntology.getProperty(dbinst + tableName.trim() + "." + columnName.trim());
							RDFNode value = dataOntology.createLiteral(data.getObject(i).toString());
							dataOntology.add(tableRow, column, value);
						}
					}
				}
			} catch (SQLException e) {
				// TODO Exception MessageBox
				e.printStackTrace();
			}
		}
		return dataOntology;
	}
	
	public OntModel exportTable(String schema, String schemaPath, String name) throws Exception{
		checkExportTable(schema, schemaPath, name);
		OntModel schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		schemaPath = "file:" + schemaPath.replace("\\", "//");
		String dbinst = "file:" + schemaPath + "#";
		
		Iterator tables = dbManager.getTables(schema).iterator();
		while (tables.hasNext()){
			String tableName = ((Table)tables.next()).name().trim();
			if (tableName.equals(name.trim())){
				String sql = "SELECT * FROM " + schema.trim() + "." + tableName;
				java.sql.ResultSet data = dbManager.getData(sql);
				// table rows
				try {
					java.sql.ResultSetMetaData metaData = data.getMetaData();
					while (data.next()){
						Resource table = dataOntology.getResource(dbinst + tableName.trim());
						Resource tableRow = dataOntology.createResource(table);
						for (int i = 1; i <= metaData.getColumnCount(); i++){
							String columnName = metaData.getColumnName(i);
							if (data.getObject(i) != null){
								Property column = schemaOntology.getProperty(dbinst + tableName.trim() + "." + columnName.trim());
								RDFNode value = dataOntology.createLiteral(data.getObject(i).toString());
								dataOntology.add(tableRow, column, value);
							}
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return dataOntology;
	}
	
	public OntModel exportSchemaSQL(String schemaPath, String sql) throws Exception{
		checkExportSchemaSQL(schemaPath, sql);
		initSchemaOntology();
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		
		java.sql.ResultSet data = dbManager.getData(sql);
		java.sql.ResultSetMetaData metaData = data.getMetaData();
		String databaseName; String tableName; String columnName;
		databaseName = "SQL_TEMP_SCHEMA";
		tableName = "SQL_TEMP_VIEW";
		
		database = schemaOntology.createResource(baseNS + databaseName);
		schemaOntology.add(database, typeProperty, databaseClass);		
		table = schemaOntology.createResource(baseNS + tableName);
		//primaryKey = schemaOntology.createResource(baseNS + "PK1");
		schemaOntology.add(table, typeProperty, tableClass);
		//schemaOntology.add(primaryKey, typeProperty, primaryKeyClass);
		schemaOntology.add(table, isIdentifiedBy, primaryKey);
		schemaOntology.add(database, hasTable, table);
		
		for (int i = 1; i <= metaData.getColumnCount(); i++){
			columnName = metaData.getColumnName(i);
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
			// length
			if (length.length() > 0){
				schemaOntology.add(column, lengthProperty, length);
			}
			// scale
			if (scale.length() > 0){
				schemaOntology.add(column, scaleProperty, scale);
			}
			// primary key
			if (i == 1){
				//schemaOntology.add(primaryKey, hasColumn, column);
			}
		}
		return schemaOntology;
	}
	
	public OntModel exportSQL(String schema, String schemaPath, String sql) throws Exception{
		checkExportSQL(schema, schemaPath, sql);
		OntModel schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dataOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		dbManager = DatabaseManagerFactory.getDbManagerInstance(props.getJDBCDriver(), props.getDbUrl(), props.getDbUser(), props.getDbPassword());
		schemaPath = "file:" + schemaPath.replace("\\", "//");
		String dbinst = "file:" + schemaPath + "#";
		
		java.sql.ResultSet data = dbManager.getData(sql);
		try {
			java.sql.ResultSetMetaData metaData = data.getMetaData();
			while (data.next()){
				Resource table = schemaOntology.getResource(dbinst + "SQL_TEMP_VIEW");
				Resource tableRow = dataOntology.createResource(table);
				for (int i = 1; i <= metaData.getColumnCount(); i++){
					String columnName = metaData.getColumnName(i);
					if (data.getObject(i) != null){
						Property column = schemaOntology.getProperty(dbinst + "SQL_TEMP_VIEW." + columnName.trim());
						RDFNode value = dataOntology.createLiteral(data.getObject(i).toString());
						dataOntology.add(tableRow, column, value);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return dataOntology;
	}
	
}
	
	
	