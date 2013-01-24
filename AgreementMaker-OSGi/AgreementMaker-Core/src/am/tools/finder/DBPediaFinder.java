package am.tools.finder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import am.GlobalStaticVariables;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.TreeBuilder;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;

/*
 * The idea is to search for a word in the DBpedia ontology and find the matching concepts and properties,
 * then automatically create some SPARQL queries on the DB
 */
public class DBPediaFinder implements Finder{
	String filename = "dbpedia_3.6.owl";	
	final static String endpoint = "http://dbpedia.org/sparql";
	OntModel model;
	
	enum QueryType { ALL_PROP_BY_DOMAIN, ALL_PROP_BY_RANGE, ALL_CLASS_INSTANCES, ALL_PROP_INSTANCES, 
		ALL_PROP_VALUES, ALL_PROP_AND_VALUE };
	
	public void openOntology(){
		try {
			TreeBuilder t = new OntoTreeBuilder(filename, GlobalStaticVariables.SOURCENODE, 
					GlobalStaticVariables.LANG_OWL,
					GlobalStaticVariables.SYNTAX_RDFXML, true, true);
			t.build();
			Ontology ontology = t.getOntology();
			model = ontology.getModel();
			System.out.println(t.getReport());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public List<OntClass> search(String term){
		ArrayList<OntClass> classes = new ArrayList<OntClass>();
		for (OntClass cl: model.listClasses().toList()) {
			if(matches(term, cl.getLabel("EN")))
				classes.add(cl);
		}
		return classes;
	}
	
	//Very simple match: the word has to be contained in the label.
	public boolean matches(String term, String label){
		if(label.toLowerCase().contains(term.toLowerCase()))
			return true;
		return false;
	}
	
	public void printResults(List<OntClass> classes){
		for (OntClass cl: classes) {
			System.out.println(cl.getLocalName() + " " + cl.getURI());
		}
	}
	
	public String getQuery(String URI, QueryType type){
		String query = null;
		if(type == QueryType.ALL_PROP_BY_DOMAIN){
			query = "select ?prop where { " +
					"?prop rdfs:domain <" + URI + "> } LIMIT 10000";
			System.out.println(query);
		}
		else if(type == QueryType.ALL_PROP_BY_RANGE){
			query = "select ?prop where { " +
			"?prop rdfs:range <" + URI + "> } LIMIT 10000";
			System.out.println(query);
		}		
		else if(type == QueryType.ALL_CLASS_INSTANCES){
			query = "select distinct ?conc where { " +
					"?conc rdf:type <" + URI +
					"> } LIMIT 10000";
			System.out.println(query);
		}
		else if(type == QueryType.ALL_PROP_INSTANCES){
			query = "SELECT distinct ?property " +
					"WHERE { ?conc rdf:type <" + URI + ">.\n" +
					"?conc ?property ?value } LIMIT 1000";
			System.out.println(query);
		}
		else if(type == QueryType.ALL_PROP_VALUES){
			query = "SELECT distinct ?value " +
					"WHERE { ?conc <" + URI + "> ?value.\n" +
					"} LIMIT 10000";
			System.out.println(query);
		}
		else if(type == QueryType.ALL_PROP_AND_VALUE){
			query = "SELECT ?prop ?value " +
					"WHERE { <" + URI + "> ?prop ?value.\n" +
					"} LIMIT 10000";
			System.out.println(query);
		}
		return query;
	}
	
	public String query(String urlStr){
		URL url = null;
		
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		URLConnection conn = null;
		
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		
		String result = ""; 
		String line;
		try {
			while ((line = rd.readLine()) != null){
				result += line + "\n";
			}
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return result; 
	}
	
	public String executeQuery(String query){
		try { 
			query = URLEncoder.encode(query, "UTF-8"); 
		} 
		catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			return null;
		}
		
		//output=xml&
		
		String parameters = "?query=" + query;
		String urlStr = endpoint + parameters;
		System.out.println(urlStr);
		return query(urlStr);
	}
	
	public static void main(String[] args) {
//		DBPediaFinder finder = new DBPediaFinder();
//		List<OntClass> results = finder.search("Song");
//		finder.printResults(results);
//		String query;
//		if(results.size()>1){
//			finder.getQuery(results.get(0).getURI(), QueryType.ALL_PROP_BY_DOMAIN);
//			finder.getQuery(results.get(0).getURI(), QueryType.ALL_INSTANCES);
//			query = finder.getQuery(results.get(0).getURI(), QueryType.ALL_PROP_INSTANCES);
//		
//			finder.executeQuery(query);
//		
//		}
		
		DBPediaFinder finder = new DBPediaFinder();
		
		finder.initialize();
		
		String url = "http://dbpedia.org/sparql?output=xml&query=SELECT+distinct+%3Fproperty+WHERE+%7B+%3Fsong+rdf%3Atype+%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2FSong%3E.%0A%3Fsong+%3Fproperty+%3Fvalue+%7D+LIMIT+1000";
		
		String result = finder.query(url);
		
		ResultSet set = ResultSetFactory.fromXML(result);
		
		int count = 0;
		while(set.hasNext()){
			System.out.println(set.next());
			count++;
		}
		System.out.println("total: " + count + " tuples");		
	}

	@Override
	public void initialize() {
		openOntology();		
	}
}
