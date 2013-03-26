package am.matcher.lod.instanceMatcher;


import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import am.GlobalStaticVariables;
import am.app.ontology.Ontology;
import am.app.ontology.instance.SeparateFileInstanceDataset;
import am.app.ontology.instance.endpoint.SparqlEndpoint;
import am.app.ontology.ontologyParser.OldOntoTreeBuilder;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class DBPediaCacher {
	static String endpoint = "http://dbpedia.org/sparql";
	
	static String sourceDataset = "C:/Users/federico/workspace/AgreementMaker/OAEI2011/NYTDatasets/locations.rdf";	
	static String rdfCacheFilename = "dbpediaLocationsRDFCache.ser";
	static HashMap<String, String> rdfAnswers;	
	
	static String type = "http://dbpedia.org/ontology/Place";
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
	
		System.out.println("Opening ontology...");
		Ontology ontology = openOntology(sourceDataset);
		System.out.println("Done");
		
		SparqlEndpoint dbpedia = new SparqlEndpoint(endpoint, rdfCacheFilename);
		
		//System.out.println(ontology.);
		rdfAnswers = new HashMap<String, String>();
		
		List<Statement> indStmts = SeparateFileInstanceDataset.getIndividualsStatements(ontology.getModel(), null);
		
		String uri;
		int count = 0;
		for (Statement stmt: indStmts) {
			System.out.println(count);
			count++;
			if(count % 100 == 0)	System.out.println(count);			
			
			uri = stmt.getSubject().getURI();
						
			String label = SeparateFileInstanceDataset.getPropertyValue(ontology.getModel(), uri, NYTConstants.SKOS_PREFLABEL);
			
			//System.out.println(label);
			
			label = LabelUtils.processLabel(label);
			
			System.out.println(label);
			
			if(label.isEmpty()) continue;
			
			String rdf = null;
						
			String query;
			
			try {
				query = dbpedia.freeTextQueryString(label, type, 1000);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			//System.out.println(query);
			
			rdf = dbpedia.getRdfFromUrl(query);
			
			Model model = ModelFactory.createDefaultModel();
			JenaReader reader = new JenaReader();
			try {
				reader.read(model, new ByteArrayInputStream(rdf.getBytes("UTF-8")), "http://blabla");
			} catch (Exception e) {
				//e.printStackTrace();
				System.err.println("Impossible to create a model");
				continue;
			}
			
			
			int stmtsNumber = model.listStatements().toList().size();
			//System.out.println("#stmts: " + );
			
			if(stmtsNumber >= 900){
				
				try {
					query = dbpedia.lightFreeTextQueryString(label, type, 1000);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
				try {
					Thread.sleep(5);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				System.out.println(query);
				
				rdf = dbpedia.getRdfFromUrl(query);
				
				System.out.println(rdf);
				
				rdfAnswers.put(query, rdf);
				continue;
			}
			
			//System.out.println(rdf);
					
			
			if(rdf == null || !rdf.contains("rdf")) {
				System.err.println("Not rdf");
				continue;
			}
			
			rdfAnswers.put(query, rdf);
		}	
		
		
	    System.out.println("Writing to file...");
	    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rdfCacheFilename));
	    out.writeObject(rdfAnswers);
	    out.close();
	    System.out.println("Done");
		
	}

	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OldOntoTreeBuilder treeBuilder = new OldOntoTreeBuilder(ontoName,
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
	
	public static String processLabel(String label, List<String> keywords){
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			if(keywords != null){
				keywords.add(label.substring(beg + 1, end));	
			}
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		if(label.contains(",")){
			String[] splitted = label.split(",");
			return splitted[1].trim() + " " + splitted[0].trim();
		}
		return label; 
	}

}


