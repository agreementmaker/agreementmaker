package matching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import parallel.SearchThread;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;

import edu.uic.advis.im.knowledgebase.ontology.OntologyBackedKnowledgeBase;
import edu.uic.advis.im.knowledgebase.ontology.OntologyKBFactory;

import misc.NYTConstants;
import misc.Queries;
import misc.Utilities;
import am.app.ontology.Ontology;


public class NYTInstanceMatcher{
	Ontology sourceOntology;
	double luceneScoreThreshold = 0.7;
	
	private OntologyBackedKnowledgeBase kb;
	
	private String targetId;
	
	private String endpoint;
	
	private Vector<InstanceMapping> mappings;
	
	ExecutorService e =  Executors.newFixedThreadPool(8);
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3520832217713242380L;

	public NYTInstanceMatcher(String source, String targetId){
		this.targetId = targetId;
		
		mappings = new Vector<InstanceMapping>();
		
		System.out.println("Opening source ontology...");
		sourceOntology = Utilities.openOntology(source);
		System.out.println("Done");
		
		System.out.println("Target is " + targetId  + ", choosing dataset");
		if(targetId.equals(NYTConstants.DBP_PERSON)){
			kb = OntologyKBFactory.createKBFromXML(new File(System.getProperty("user.dir") + File.separator + NYTConstants.DBP_PERSON_XML));
			endpoint = NYTConstants.DBP_ENDPOINT;
		}
	}
	
	public void initialize(){
		
		
	}
	
	
	
	public void match() throws Exception {
		OntModel sourceModel = sourceOntology.getModel();
		List<Statement> indStatements = Queries.getIndividualsStatements(sourceModel, NYTConstants.SKOS_CONCEPT);
		
		
		System.out.println(targetId);
		OntModel targetModel = kb.getOntModelByID(targetId);
		
		int online = 0;
		
		String instanceURI;
		Statement stmt;
		for(int i = 0; i < indStatements.size(); i++ ){
			stmt = indStatements.get(i);
			instanceURI = stmt.getSubject().getURI();
			
			String label = Queries.getPropertyValue(sourceModel, instanceURI, NYTConstants.SKOS_PREFLABEL);
			
			//System.out.println();
			
			label = Utilities.processLabel(label);
			
			List<Individual> candidates = freeTextQuery(targetModel, label, NYTConstants.FOAF_NAME);
			
			Individual matched = null;
			
			if(candidates.size() == 1){
				matched = candidates.get(0);
				addMapping(instanceURI, matched.getURI());
			}
			else if(candidates.size() == 0){
				online++;
				e.execute(new SearchThread(this, instanceURI, label, endpoint));
			}
			

		}
		
		System.out.println(online);
		
	}
	
	
	
	public List<Individual> freeTextQuery(OntModel model, String search, String property){
		search = search.replaceAll("'", "\\\\'");
		
		String queryString = "PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>" +
		"\nPREFIX :       <http://example/>" +
		"\nPREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>" +
		"\nSELECT ?s ?score{" +
		"\n      (?lit ?score) pf:textMatch '\"" + search + "\"' ." +
		"\n	?s ?property ?lit." +
		"\n  FILTER (?score > " + luceneScoreThreshold +")}";

		//System.out.println(queryString);
		
		Query query = QueryFactory.create(queryString);

		QuerySolutionMap map = new QuerySolutionMap();
		Property prop = model.getProperty(property);
		if(prop == null) model.createProperty(property);
		
		map.add("property", prop);
		
		QueryExecution qexec = QueryExecutionFactory.create(query, model, map);
		
		ResultSet results = qexec.execSelect() ;
		
		ArrayList<Individual> candidates = new ArrayList<Individual>();
	    
	    model.enterCriticalSection( Lock.READ );
	    try {
			while(results.hasNext()){
				QuerySolution soln = results.next();
				RDFNode node = soln.get("s");
				
				if(node == null) continue;
				
				Individual individual = model.getIndividual(node.toString());
				
				if(individual != null){
					candidates.add(individual);
				}
					
			}
	    } finally {
	    	model.leaveCriticalSection();
	    }
		return candidates;
	}
	
	public static ResultSet freeTextQueryOnline(String endpoint, String search) throws IOException{
		System.out.println("ONLINE!");
		search = search.replaceAll("'", "\\\\'");
		
		String queryString = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>" +
				"PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"select distinct ?p ?name {" +
				"    ?p rdf:type <http://dbpedia.org/ontology/Person> ." +
				"    ?p foaf:name ?name ." +
				"    ?name bif:contains '\"" + search + "\"'" +
						"} LIMIT 100";
		
		
		//System.out.println(queryString);
		
		String result = Queries.executeQuery(endpoint, queryString);
		
		ResultSet set = ResultSetFactory.fromXML(result);
		
		while(set.hasNext()){
			System.out.println(set.next());
		}
		
		//ArrayList<Individual> candidates = new ArrayList<Individual>();
	    	    
		return set;
	}
	
	public void addMapping(String source, String target){
		mappings.add(new InstanceMapping(source, target));
	}
	
}
