package am.extension.batchmode.simpleBatchMode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;;

public class jena_check {
	 public static void main(String[] args) {
		
		Model model = ModelFactory.createDefaultModel();		
		String inputFileName="C:/workspaceFinalProject/conf_track/cmt.owl";
		
		 InputStream in = FileManager.get().open( inputFileName );
		 
		 if (in == null) {
		     throw new IllegalArgumentException(
		                                  "File: " + inputFileName + " not found");
		 }

		 // read the RDF/XML file
		 model.read(in, null);

		
		Resource concept = model.getResource("http://cmt#Bid");	
		Resource concept1 = model.getResource("http://cmt#Acceptance");	

		
	
	
		
	//	String name=concept.getRequiredProperty(RDFS.subClassOf).asTriple().getMatchObject().getURI().toString();
		List<Statement> name=concept.listProperties(RDFS.subClassOf).toList();
		List<Statement> name1=concept1.listProperties(RDFS.subClassOf).toList();
		
		
		if(name.isEmpty()){
			System.out.println("Empty");
					
		}else{
			System.out.println(name.get(0).getObject().toString());
	//		System.out.println("Subclass  of :"+ name);

		}
		
		if(name1.isEmpty()){
			System.out.println("Empty1");
					
		}else{
			System.out.println(name1.get(0).getObject().toString());
	//		System.out.println("Subclass  of :"+ name1);

		}
		
		//				
				

	//	while(disjoint.hasNext()){
	//		System.out.println("Classes its disjoint with :"+ disjoint.next().asTriple().getMatchObject().getURI().toString());
//		}		


	//	System.out.println("Subclass  of :"+ name);
		
		
	}

}