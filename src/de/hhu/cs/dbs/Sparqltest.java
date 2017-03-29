package de.hhu.cs.dbs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import de.hhu.cs.dbs.DatabaseManagerFactory;
import de.hhu.cs.dbs.interfaces.DbManager;
import de.hhu.cs.dbs.tasks.*;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;

public class Sparqltest {

   public static void main(String[] args) {
       new Sparqltest();
   }

   Sparqltest(){
       try{
           /*DbManager dbManager = DatabaseManagerFactory.getDbManagerInstance(
                   "com.mysql.jdbc.Driver",
                   "jdbc:mysql://dbcip.cs.uni-duesseldorf.de/northwind",
                   "dzikowsk",
                   "BdP4uc!");
           // */
           
    	   DbManager dbManager = DatabaseManagerFactory.getDbManagerInstance(
                   "com.mysql.jdbc.Driver",
                   "jdbc:mysql://localhost/northwind",
                   "root",
                   "pemodzik");
           
           /*System.out.println("-----------schema------------------------");
           ExportSchemaTask taskES =
               new ExportSchemaTask(dbManager.getConnection(),"com.mysql.jdbc.Driver", "northwind");
           taskES.go();
           while (!taskES.isDone()){
               Thread.sleep(100);
           } // */
           
           /*OntModel schemaOntology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
           schemaOntology = taskES.getSchemaOntology();
           OutputStream output = System.out; 
		   RDFWriter utf8Writer = schemaOntology.getWriter("RDF/XML");
		   utf8Writer.setProperty("allowBadURIs","true");
		   utf8Writer.setProperty("relativeURIs","same-document,relative");
		   utf8Writer.write(schemaOntology, output, "");
		   // */
		   
           /*System.out.println("-----------data------------------------");
           ExportDataTask taskED= new ExportDataTask(dbManager.getConnection(),"com.mysql.jdbc.Driver","northwind","c:/tmp/schema.owl");
           taskED.go();
           while (!taskED.isDone()){
               Thread.sleep(100);
           }
           
           /*OntModel data=taskED.getDataOntology();
           RDFWriter utf8DataWriter= data.getWriter("RDF/XML-ABBREV");
           utf8DataWriter.setProperty("allowBadURIs","true");
           utf8DataWriter.setProperty("relativeURIs","same-document,relative");
           utf8DataWriter.write(data, System.out, ""); 
           // */
           
           System.out.println("-----------delete data------------------------");
           DeleteDataTask taskDD= new DeleteDataTask(dbManager.getConnection(),"com.mysql.jdbc.Driver","northwind");
           taskDD.go();
           while (!taskDD.isDone()){
               Thread.sleep(100);
           }
           
           
           System.out.println("-----------update data------------------------");
           UpdateDataTask taskUD= new UpdateDataTask(dbManager.getConnection(),"com.mysql.jdbc.Driver","C:/tmp/schema.owl","C:/tmp/data.owl");
           taskUD.go();
           while (!taskUD.isDone()){
               Thread.sleep(100);
           }
           
           System.out.println("-----------operationen durchgeführt------------------------");
           //OutputStream output = System.out; 
           //utf8DataWriter.write(data, System.out, ""); 
           //utf8DataWriter.write(data2, output, "");  
           //utf8DataWriter.write(data, new FileOutputStream(new File("c:/tmp/data-test.owl")), "");    
       
           
       }
       catch (Exception e){e.printStackTrace();}
   }
} 