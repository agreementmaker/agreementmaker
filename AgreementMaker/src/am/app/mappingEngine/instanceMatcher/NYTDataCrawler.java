package am.app.mappingEngine.instanceMatcher;

import gov.nih.nlm.nls.lexCheck.Api.ApiOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Statement;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.Ontology;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.ontologyParser.OldOntoTreeBuilder;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.utility.HTTPUtility;

public class NYTDataCrawler{
	static String sourceDataset = "C:/Users/federico/workspace/AgreementMaker/OAEI2011/NYTDatasets/people.rdf";	
	static String searchApi = "http://data.nytimes.com/elements/search_api_query";
	static String nytKey = "&api-key=1f39b4de086c492574d2c7456e3e3849:6:64824021";
	
	static HashMap<String, String> jsonAnswers;	
	
	public static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
	
		Ontology ontology = openOntology(sourceDataset);
		
		//System.out.println(ontology.);
		jsonAnswers = new HashMap<String, String>();
		
		List<Statement> indStmts = InstanceDataset.getIndividualsStatements(ontology.getModel(), null);
		
		System.out.println(indStmts.size());
		
		String uri;
		for (Statement stmt: indStmts) {
			uri = stmt.getSubject().getURI();
			System.out.println(uri);
			
			
			String query = InstanceDataset.getPropertyValue(ontology.getModel(), uri, searchApi);
			
			query += nytKey;
			
			System.out.println(query);
			
			if(query.isEmpty()){
				System.err.println("Empty Page String");
				continue;
			}
			
			
			
			String json = null;
			
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
			
			System.out.println(json);
			
			jsonAnswers.put(uri, json);
			
		}	
		
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream("jsonAnswers.ser"));
	    out.writeObject(jsonAnswers);
	    out.close();
		
	}

	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OldOntoTreeBuilder treeBuilder = new OldOntoTreeBuilder(ontoName, GlobalStaticVariables.SOURCENODE,
			GlobalStaticVariables.LANG_OWL, 
			GlobalStaticVariables.SYNTAX_RDFXML, false, true);
			treeBuilder.build();
			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	
}
