package de.hhu.cs.dbs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.hhu.cs.dbs.impl.ColumnImpl;
import de.hhu.cs.dbs.impl.DatabaseImpl;
import de.hhu.cs.dbs.impl.PrimaryKeyImpl;
import de.hhu.cs.dbs.impl.TableImpl;
import de.hhu.cs.dbs.impl.TableRowImpl;
import de.hhu.cs.dbs.interfaces.Column;
import de.hhu.cs.dbs.interfaces.Database;
import de.hhu.cs.dbs.interfaces.PrimaryKey;
import de.hhu.cs.dbs.interfaces.Table;
import de.hhu.cs.dbs.interfaces.TableRow;

public class OntModelManager {
	
	private OntModel model;
	
	public OntModelManager(OntModel model){
		this.model = model;
	}
	
	public Database getDatabase(){
		OntModel model = getModel();
		Iterator iter = getModel().listClasses();
		
		while (iter.hasNext()){
			OntClass d = (OntClass)iter.next();
			// in case when model is saved in owl statement we have to iterate over its types
			// this is realised with isDatabase function
			if (isDatabase(d)){
				return (Database)getDatabase(d.getLocalName());
			}
		}	
		//		TODO
		// schema saved as RDF/XML
		// in this case classes occurying as Individuals
		iter = getModel().listIndividuals();
		while (iter.hasNext()){
			Individual d = (Individual)iter.next();
			if (isDatabase(d)){
				return (Database)getDatabase(d.getLocalName());
			}
		}
		return null;
	}
	
	public Database getDatabase(String databaseName){
		Database d = new DatabaseImpl();
		Table t = new TableImpl();
		
		//	ontology classes
		Iterator iter = getModel().listClasses();
		while (iter.hasNext()){
			OntClass c = (OntClass)iter.next();
			//	database
			if (isDatabase(c) && c.getLocalName().equals(databaseName)){
				d.setName(c.getLocalName());
				// tables
				Iterator iterTables = c.listProperties();
				while (iterTables.hasNext()){
					com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iterTables.next();
					Resource r = (Resource)stmt.getObject();
					Property p = (Property)stmt.getPredicate();
					if (p.getLocalName().equals("hasTable")){
						d.addTable(getTable(databaseName, r.getLocalName()));
					}
				}
				return d;
			}
		}
		
		//	rdf-xml classes
		iter = getModel().listIndividuals();
		while(iter.hasNext()){
			Individual c = (Individual)iter.next();
			//	database
			if (isDatabase(c) && c.getLocalName().equals(databaseName)){
				d.setName(c.getLocalName());
				// 	tables
				Iterator iterTables = c.listProperties();
				while (iterTables.hasNext()){
					com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iterTables.next();
					Resource r = (Resource)stmt.getObject();
					Property p = (Property)stmt.getPredicate();
					if (p.getLocalName().equals("hasTable")){
						d.addTable(getTable(databaseName, r.getLocalName()));
					}
				}
				return d;
			}
		}
		return d; 
	}
	
	public Table getTable(String databaseName, String tableName){
		Table t = new TableImpl();
		PrimaryKey pk = new PrimaryKeyImpl();
			
		//	ontology classes
		Iterator iter = getModel().listClasses();
		while (iter.hasNext()){
			OntClass c = (OntClass)iter.next();
			//	table
			if (isTable(c) && c.getLocalName().equals(tableName)){
				t.setName(tableName);
				t.setDatabase(databaseName);
				// columns
				StmtIterator iterColumns = c.listProperties();
				while (iterColumns.hasNext()){
					com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iterColumns.nextStatement();
					Resource r = (Resource)stmt.getObject();
					Property p = (Property)stmt.getPredicate();
					//	primary key
					if (p.getLocalName().equals("isIdentifiedBy") && isPrimaryKey(r)){
						pk = getPrimaryKey(r);
					}
					//	column
					if (p.getLocalName().equals("hasColumn")){
						t.addColumn(getColumn(r.getLocalName()));
					}				
				}
				t.setPrimaryKey(pk);
				return t;
			}
		}
		
		//	rdf-xml classes
		iter = getModel().listIndividuals();
		while (iter.hasNext()){
			Individual c = (Individual)iter.next();
			//	table
			if (isTable(c) && c.getLocalName().equals(tableName)){
				t.setName(tableName);
				t.setDatabase(databaseName);
				// columns
				StmtIterator iterColumns = c.listProperties();
				while (iterColumns.hasNext()){
					com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iterColumns.nextStatement();
					Resource r = (Resource)stmt.getObject();
					Property p = (Property)stmt.getPredicate();
					//	primary key
					if (p.getLocalName().equals("isIdentifiedBy")){
						pk = getPrimaryKeyRDF(r);
					}
					//	column
					if (p.getLocalName().equals("hasColumn")){
						t.addColumn(getColumn(r.getLocalName()));
					}				
				}
				t.setPrimaryKey(pk);
				return t;
			}
		}	
		return t;	
	}
	
	// creates column from owl:DatatypeProperty
	public Column getColumn(String name){
		Column col = new ColumnImpl();
		
		//	ontology classes
		Iterator iter = getModel().listDatatypeProperties();
		while (iter.hasNext()){
			DatatypeProperty c = (DatatypeProperty)iter.next();
			
			// column
			if (isColumn(c) && c.getLocalName().equals(name)){
				col.setName(name);
				Iterator iterProps = c.listProperties();
				
				// props
				while (iterProps.hasNext()){
					com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iterProps.next();
					RDFNode r = (RDFNode)stmt.getObject();
					Property p = (Property)stmt.getPredicate();
					
					// tablename
					if (p.getLocalName().equals("domain")){
						// this if-statement is in case of OWL_MEM_RULE_INF reasoning
						// the properties are always shown like in down-up order
						// and column properties like tablename to wich it belongs,
						// range of data, length, scale lie always on first branch
						if (col.table().length() == 0) col.setTable(r.asNode().getLocalName());
					}
					// range
					if (p.getLocalName().equals("range")){
						if (col.range().length() == 0) col.setRange(r.asNode().getLocalName());
					}
					// length
					if (p.getLocalName().equals("length")){
						if (col.length().length() == 0) col.setLength(r.asNode().getLiteral().getValue().toString());
					}
					// scale
					if (p.getLocalName().equals("scale")){
						if (col.scale().length() == 0) col.setScale(r.asNode().getLiteral().getValue().toString());
					}
					// foreign key
					if (p.getLocalName().equals("references")){
						col.setIsForeignKey(true);
						col.setReferences(r.asNode().getLocalName());
					}
				}
				return col;
			}
		}
		
		//	rdf-xml classes
		iter = getModel().listIndividuals();
		while (iter.hasNext()){
			Individual c = (Individual)iter.next();
			
			if (isColumn(c) && c.getLocalName().equals(name)){
				col.setName(name);
				Iterator iterProps = c.listProperties();
				
				// props
				while (iterProps.hasNext()){
					com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iterProps.next();
					RDFNode r = (RDFNode)stmt.getObject();
					Property p = (Property)stmt.getPredicate();
					
					// tablename
					if (p.getLocalName().equals("domain")){
						if (col.table().length() == 0) col.setTable(r.asNode().getLocalName());
					}
					// range
					if (p.getLocalName().equals("range")){
						if (col.range().length() == 0) col.setRange(r.asNode().getLocalName());
					}
					// length
					if (p.getLocalName().equals("length")){
						if (col.length().length() == 0) col.setLength(r.asNode().getLiteral().getValue().toString());
					}
					// scale
					if (p.getLocalName().equals("scale")){
						if (col.scale().length() == 0) col.setScale(r.asNode().getLiteral().getValue().toString());
					}
					// foreign key
					if (p.getLocalName().equals("references")){
						col.setIsForeignKey(true);
						col.setReferences(r.asNode().getLocalName());
					}
				}
				return col;
			}
		}
		return col;
	}

	public ArrayList getDataRows(Database database){
		TableRow row = new TableRowImpl(); 
		ArrayList rows = new ArrayList();
		ArrayList sortedRows = new ArrayList();
		int rowsAdded = 0;
		RDFNode lastSubject = null;
		String tableName = "";

		Iterator iter = getModel().listStatements();
		while (iter.hasNext()){
			com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iter.next();
			//read only data rows
			if (stmt.getObject().asNode().isLiteral()){
				//add column
				Literal o = (Literal)stmt.getObject();
				Property p = (Property)stmt.getPredicate();
				RDFNode s = (RDFNode)stmt.getSubject();
				Column c = null;
				//new row
				if ((lastSubject == null) || (!lastSubject.equals(s))){
					//table name
					if ((	new StringTokenizer(p.getLocalName(), ".")).countTokens() > 1) {
						tableName = p.getLocalName().substring(0, p.getLocalName().indexOf("."));
					}
					else{
						tableName = p.getLocalName();
					}
					row = new TableRowImpl(tableName);
					row.setDatabase(database.name());
					rows.add(row);
					rowsAdded++;
				}
				
				//this check is necessery becouse of type of jena output
				//it can occour f.e. <j.0:LAND></j.0:LAND> in  <rdf:Description> node
				if (( new StringTokenizer(p.getLocalName(), ".")).countTokens() > 1) {
					Table t = database.getTable(tableName);
					Column colTemp = t.getColumn(p.getLocalName());
					c = new ColumnImpl(colTemp.name());
					c.setRange(colTemp.range());
					c.setValue(o.getValue().toString());
					if (rowsAdded > 0){
						((TableRow)rows.get(rowsAdded - 1)).addColumn(c);
					}
				}	
				//remember last table row 
				lastSubject = (RDFNode)stmt.getSubject();
			}
		}
		// sort row to allow foreign key constraints check
		ArrayList tablesOrder = sortTablesByForeignKeys(database.getTables());
		
		for (Iterator tablesIter = tablesOrder.iterator(); tablesIter.hasNext();) {
			Table table = (Table) tablesIter.next();
			for (Iterator rowsIter = rows.iterator(); rowsIter.hasNext();) {
				row = (TableRow) rowsIter.next();
				if (row.table().equals(table.name())){
					sortedRows.add(row);
				}
			}
		}
		return sortedRows;
	}
	
	public ArrayList getDataRows(Database database, Table table){
		ArrayList rows = new ArrayList();
		
		Iterator rowsIter = getDataRows(database).iterator();
		while (rowsIter.hasNext()){
			TableRow row = (TableRow)rowsIter.next();
			if (row.table().toUpperCase().equals(table.name().toUpperCase()))
				rows.add(row);
		}
		return rows;
	}
	
	public ArrayList getDataRows(Database database, String table){
		ArrayList rows = new ArrayList();
		
		Iterator rowsIter = getDataRows(database).iterator();
		while (rowsIter.hasNext()){
			TableRow row = (TableRow)rowsIter.next();
			if (row.table().toUpperCase().equals(table.toUpperCase()))
				rows.add(row);
		}
		return rows;
	}
	

	private ArrayList sortTablesByForeignKeys(ArrayList tables){
		ArrayList tablesSorted = new ArrayList();
		ArrayList tablesNextLevel = new ArrayList();
		
		if (tables.size() == 0){
			return tablesSorted;
		}
		// get tables that haven't reference at analyzed level
		for (Iterator iter = tables.iterator(); iter.hasNext();) {
			Table table1 = (Table) iter.next();
			boolean references = false;
			for (Iterator iter2 = tables.iterator(); iter2
					.hasNext();) {
				Table table2 = (Table) iter2.next();
				// check references to another table 
				for (Iterator iter3 = table1.columns().iterator(); iter3
						.hasNext();) {
					Column column1 = (Column) iter3.next();
					if (column1.references().toUpperCase()
							.indexOf(table2.name().toUpperCase().concat(".")) != -1 ){
						// process table next level
						tablesNextLevel.add(table1);
						references = true;
						break;
					}
				}
				if (references) break;
			}
		}
		
		// get to next processing only tables that referencing to another tables
		for (Iterator iter = tables.iterator(); iter.hasNext();) {
			Table table1 = (Table) iter.next();
			boolean goesToNextLevel = false;
			for (Iterator iter2 = tablesNextLevel.iterator(); iter2.hasNext();) {
				Table table2 = (Table) iter2.next();
				if (table1.name().equals(table2.name()))
					goesToNextLevel = true;
			}
			if (!goesToNextLevel)
				tablesSorted.add(table1);
		}
		
		
		ArrayList nextLevelSorted = sortTablesByForeignKeys(tablesNextLevel);
		// add tables from next level sorted 
		for (Iterator iter3 = nextLevelSorted.iterator(); iter3.hasNext();) {
			Table table = (Table) iter3.next();
			tablesSorted.add(table);
		}

		return tablesSorted;
	}
	
	private ArrayList sortRowsByForeignKeys(Database database, ArrayList rows){
		ArrayList rowsSorted = new ArrayList();
		
		return rowsSorted;
	}

	private PrimaryKey getPrimaryKey(Resource r){
		PrimaryKey pk = new PrimaryKeyImpl();
		StmtIterator iterPK = r.listProperties();
		
		//ontology classes
		try{
			//	add column
			while (iterPK.hasNext()){
				com.hp.hpl.jena.rdf.model.Statement stmt2 = (com.hp.hpl.jena.rdf.model.Statement)iterPK.nextStatement();
				Property p = (Property)stmt2.getPredicate();
				Resource r2 = (Resource)stmt2.getObject();
				
				if (p.getLocalName().equals("hasColumn")){
					Column col = new ColumnImpl(r2.getLocalName());
					col.setIsPrimaryKey(true);
					pk.addColumn(col);
				}
			}	
		}
		catch (Exception e) { 
			e.printStackTrace(); 
			System.exit( 1 ); 
		}
		
		return pk;		
	}
	
	private PrimaryKey getPrimaryKeyRDF(Resource r){
		//rdf-xml classes
		PrimaryKey pk = new PrimaryKeyImpl();
		Iterator iter = getModel().listIndividuals();
		
		while (iter.hasNext()){
			Individual c = (Individual)iter.next();
			if (c.getLocalName().equals(r.getLocalName())){
				StmtIterator iterPK = c.listProperties();
				//add column
				while (iterPK.hasNext()){
					com.hp.hpl.jena.rdf.model.Statement stmt2 = (com.hp.hpl.jena.rdf.model.Statement)iterPK.nextStatement();
					Property p = (Property)stmt2.getPredicate();
					Resource r2 = (Resource)stmt2.getObject();
					
					if (p.getLocalName().equals("hasColumn")){
						Column col = new ColumnImpl(r2.getLocalName());
						col.setIsPrimaryKey(true);
						pk.addColumn(col);
					}
				}
				return pk;
			}
		}
		
		return pk;
	}
	
	private Table setTablePKColumns(Table table){
		// set isPrimaryKey flag by all columns that contains PrimaryKey
		Table t = new TableImpl(table.name());
		ArrayList columns = t.columns();
		Iterator iter = ((PrimaryKey)t.primaryKey()).getColumns().iterator();
		while (iter.hasNext()){
			Column pkColumn = (Column)iter.next();
			Iterator iter2 = columns.iterator();
			while (iter2.hasNext()){
				Column column = (Column)iter2.next();
				if (column.name().equals(pkColumn.name())){
					columns.remove(column);
					column.setIsPrimaryKey(true);
					columns.add(column);
				}
			}
		}
		t.setColumns(columns);
		return t;
	}
	
	private boolean isDatabase(OntClass c){
		//if (c.getLocalName().equals("Database"));
		
		Iterator iter = c.listRDFTypes(true);
		while(iter.hasNext()){
			Resource res = (Resource)iter.next();
			if (res.getLocalName().equals("Database")) return true;
			//if (((Resource)iter.next()).getLocalName().equals("Database")) return true;
		}
		return false;
	}
	
	private boolean isDatabase(Individual c){
		Iterator iter = c.listRDFTypes(true);
		while(iter.hasNext()){
			//Resource rdfType = (Resource)iter.next();
			//if (rdfType.getLocalName().equals("Database")) return true;
			if (((Resource)iter.next()).getLocalName().equals("Database")) return true;
		}
		return false;
	}
	
	private boolean isTable(OntClass c){
		Iterator iter = c.listRDFTypes(true);
		while(iter.hasNext()){
			if (((Resource)iter.next()).getLocalName().equals("Table")) return true;		
		}
		//if (c.getLocalName().equals("Table")) return true;
		return false;
	}
	
	private boolean isTable(Individual c){
		Iterator iter = c.listRDFTypes(true);
		while(iter.hasNext()){
			if (((Resource)iter.next()).getLocalName().equals("Table")) return true;		
		}
		//if (c.getLocalName().equals("Table")) return true;
		return false;
	}
	
	private boolean isColumn(DatatypeProperty d){
		Iterator iter = d.listRDFTypes(true);
		while(iter.hasNext()){
			if (((Resource)iter.next()).getLocalName().equals("Column")) return true;
		}
		return false;
	}
	
	private boolean isColumn(Individual d){
		Iterator iter = d.listRDFTypes(true);
		while(iter.hasNext()){
			if (((Resource)iter.next()).getLocalName().equals("Column")) return true;
		}
		return false;
	}
	
	private boolean isPrimaryKey(Resource r){
		StmtIterator iter = r.listProperties();
		
		while(iter.hasNext()){
			com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement)iter.nextStatement();
			Resource object = (Resource)stmt.getObject();
			if (object.getLocalName().equals("PrimaryKey")) return true;
		}
		//if (r.getLocalName().equals("PrimaryKey")) return true;
		return false;
	}

	public OntModel getModel() {
		return model;
	}		
}
