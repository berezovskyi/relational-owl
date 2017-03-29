package de.hhu.cs.dbs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class DatabaseModelFactory {

	public static final String RELATIONAL_OWL = 
		"http://www.dbs.cs.uni-duesseldorf.de/RDF/relational.owl#";	// relational-owl ontology
		
	private DatabaseModelFactory(){}
	
	/*
	 * Creates empty model with {@link OntModelSpec.OWL_MEM} specification
	 */
	public static OntModel createEmptyModel(){
		return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
	}
	
	/*
	 * Creates in memory default OntModel with specification OntModelSpec.OWL_MEM, 
	 * wich represents the relational.owl ontology.
	 */
	public static OntModel createDefaultModel() throws Exception{
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null); 
		try {
			URL url = new URL(RELATIONAL_OWL);
			m.read(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"), RELATIONAL_OWL);
			m.setNsPrefix("dbs",RELATIONAL_OWL);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	
		return m;
	}
	
	/*
	 * Creates default OntModel with specification OntModelSpec.OWL_MEM. Model represents  
	 * the relational.owl ontology and its instance schema from the file described with 
	 * path.
	 */
	public static OntModel createModelSchema(String path) throws Exception{
		OntModel model = createDefaultModel();
		try {
			String baseURI = "file:///" + path;
			model.read(new FileReader(path), baseURI);
		} catch (FileNotFoundException e) {
			throw new Exception(e.getMessage());
		}
				
		return model;
	}
	
	/*
	 * Creates default OntModel with type OntModelSpec.OWL_MEM that represents  
	 * instance data from the file described with argument path.
	 */
	public static OntModel createModelData(String path){
		OntModel m = createEmptyModel();
		InputStream in = FileManager.get().open(path);
		m.read(new InputStreamReader(in), "");
		return m;
	}
}
