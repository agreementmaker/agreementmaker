package am.matcher.lod.instanceMatcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import am.GlobalStaticVariables;
import am.app.ontology.Ontology;
import am.app.ontology.instance.SeparateFileInstanceDataset;
import am.app.ontology.ontologyParser.OldOntoTreeBuilder;
import am.utility.HTTPUtility;

import com.hp.hpl.jena.rdf.model.Statement;

public class NYTDataRefiner {
	static String sourceDataset = "C:/Users/federico/workspace/AgreementMaker/OAEI2011/NYTDatasets/people.rdf";	
	static String searchApi = "http://data.nytimes.com/elements/search_api_query";
	static String nytKey = "&api-key=1f39b4de086c492574d2c7456e3e3849:6:64824021";
	
	static HashMap<String, String> jsonAnswers;	
	
	public static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInput in = new ObjectInputStream(new FileInputStream("jsonAnswers.ser"));
	    Object input = in.readObject();
		jsonAnswers = (HashMap<String, String>) input;
		
		int count = 0;
		
		
		Ontology ontology = openOntology(sourceDataset);
		
		//System.out.println(ontology.);
		
		List<Statement> indStmts = SeparateFileInstanceDataset.getIndividualsStatements(ontology.getModel(), null);
		
		System.out.println(indStmts.size());
		
		String uri;
		for (Statement stmt: indStmts) {
			uri = stmt.getSubject().getURI();
			System.out.println(uri);
			
			if(jsonAnswers.containsKey(uri)){
				continue;
			}
					
			
			String query = SeparateFileInstanceDataset.getPropertyValue(ontology.getModel(), uri, searchApi);
			
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
		
		System.out.println(count);
		
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream("jsonAnswers2.ser"));
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
