package am.matcher.lod.instanceMatcher;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import am.app.ontology.Ontology;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.datasets.SeparateFileInstanceDataset;
import am.app.ontology.instance.endpoint.FreebaseEndpoint;
import am.app.ontology.ontologyParser.OldOntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.utility.HTTPUtility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Statement;

public class FreebaseCacher {
	
	static String sourceDataset = "OAEI2011/NYTDatasets/organizations.rdf";	
	static String type = "/people/person";
	static String cacheFilename = "newFreebaseCacheOrganizationsNoType.ser";
	static String rdfCacheFilename = "freebaseRDFCache.ser";
	public static String FREEBASE_URI = "http://rdf.freebase.com/rdf/";
	
	static String searchApi = "http://data.nytimes.com/elements/search_api_query";
		
	static HashMap<String, String> jsonAnswers;	
	static HashMap<String, String> rdfAnswers;	
	
	public static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";
	
	
	static boolean extractRDF = false;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
	
		System.out.println("Opening ontology...");
		Ontology ontology = openOntology(sourceDataset);
		System.out.println("Done");
		
		FreebaseEndpoint freebase = new FreebaseEndpoint(cacheFilename);
		
		//System.out.println(ontology.);
		jsonAnswers = new HashMap<String, String>();
		rdfAnswers = new HashMap<String, String>();
		
		List<Statement> indStmts = SeparateFileInstanceDataset.getIndividualsStatements(ontology.getModel(), null);
		
		String uri;
		int count = 0;
		for (Statement stmt: indStmts) {
			count++;
			if(count % 100 == 1)	System.out.println(count);			
			
			uri = stmt.getSubject().getURI();
						
			String label = SeparateFileInstanceDataset.getPropertyValue(ontology.getModel(), uri, NYTConstants.SKOS_PREFLABEL);
			
			label = LabelUtils.processLabel(label);
			
			String json = null;
						
			String query;
			
			try {
				query = freebase.freeTextQueryString(label, null);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			json = freebase.getJsonFromUrl(query);
			//System.out.println(json);
			if(json != null)
				jsonAnswers.put(query, json);
			else {
				System.out.println("null answer");
				continue;
			}
			
			String id = null;
			

			
			JsonNode object = null;
			try {
				ObjectMapper m = new ObjectMapper();
				object = m.readTree(json);
			} catch (JsonProcessingException e) {
				System.out.println("JSONCorrupted");
				continue;
			}
			
			JsonNode results = object.get("result");
			if(results == null){
				System.err.println("No results");
				continue;
			}
			
			if(!extractRDF) continue; 
			
			JsonNode result;
			String fbUri;
			String score;
			Instance instance = null;
			for (int i = 0; results.get(i) != null; i++) {
				result = results.get(i);
				fbUri = result.get("id").toString();
			
				fbUri = FREEBASE_URI + fbUri.substring(1).replace('/','.');
				
				System.out.println(fbUri);
				
				String rdf = null;
				
				try{
					rdf = HTTPUtility.getPage(fbUri);
				}
				catch(Exception e){
					System.err.println("Connection failed");
					continue;
				}
				if(!rdf.contains("rdf")) {
					System.err.println("Not rdf");
					continue;
				}
				
				rdfAnswers.put(fbUri, rdf);
			}
			
		}	
		
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(cacheFilename));
	    out.writeObject(jsonAnswers);
	    out.close();
	    
//	    System.out.println("Writing to file...");
//	    out = new ObjectOutputStream(new FileOutputStream(rdfCacheFilename));
//	    out.writeObject(rdfAnswers);
//	    out.close();
	    System.out.println("Done");
		
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
