package am.matcher.lod.instanceMatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import am.GlobalStaticVariables;
import am.app.ontology.Ontology;
import am.app.ontology.instance.SeparateFileInstanceDataset;
import am.app.ontology.ontologyParser.OldOntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.utility.HTTPUtility;

import com.hp.hpl.jena.rdf.model.Statement;


public class NYTDataCrawler{
	static String sourceDataset = "C:/Users/federico/workspace/AgreementMaker/OAEI2011/NYTDatasets/locations.rdf";	
	static String searchApi = "http://data.nytimes.com/elements/search_api_query";
	static String nytKey = "&api-key=1f39b4de086c492574d2c7456e3e3849:6:64824021";
	static String outputFile = "jsonAnswersLocations.ser";
	static String inputFile = "imdata/jsonAnswersLocations2.ser";	
	
	static HashMap<String, String> jsonAnswers;	
	
	public static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
	
		Ontology ontology = openOntology(sourceDataset);
		
		//System.out.println(ontology.);
		try {
			File input = new File(inputFile);
			FileInputStream fis = new FileInputStream(input);		
			ObjectInputStream in = new ObjectInputStream(fis);
			Object object;
			object = in.readObject();
			jsonAnswers = (HashMap<String, String>) object;
		} catch (Exception e2) {
			System.out.println("No cache file found");
			jsonAnswers = new HashMap<String, String>();
		}
		
		List<Statement> indStmts = SeparateFileInstanceDataset.getIndividualsStatements(ontology.getModel(), null);
		
		System.out.println(indStmts.size());
		
		String uri;
		int count = 0;
		for (Statement stmt: indStmts) {
			count++;
			if(count % 100 == 0) System.out.println(count);
			
			uri = stmt.getSubject().getURI();
			System.out.println(uri);
			
			String query = SeparateFileInstanceDataset.getPropertyValue(ontology.getModel(), uri, searchApi);
			
			if(query.isEmpty()){
				System.out.println("Empty query");
				continue;
			}
			
			query += nytKey;
			
			//System.out.println(query);
			
			if(query.isEmpty()){
				System.err.println("Empty Page String");
				continue;
			}
			
			String json = null;
			
			if(!jsonAnswers.containsKey(uri)){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				try {
					json = HTTPUtility.getPage(query);
				} catch (IOException e) {
					System.err.println("Connection problem");
					continue;
				}
				
				if(!json.startsWith("{")){
					System.err.println("Not a JSON answer");
					continue;
				}
				
				//System.out.println(json);
				
				jsonAnswers.put(uri, json);	
			}
			else System.out.println("contained");
		}	
		
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(outputFile));
	    out.writeObject(jsonAnswers);
	    out.close();
		
	}

	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OntologyDefinition def = new OntologyDefinition(true, ontoName, 
					OntologyLanguage.OWL, OntologySyntax.RDFXML);
			OldOntoTreeBuilder treeBuilder = new OldOntoTreeBuilder(def);
			treeBuilder.build();
			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	
}
