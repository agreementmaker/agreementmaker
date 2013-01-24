package matching;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import parallel.MQLSearchThread;
import parallel.SPARQLSearchThread;

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
import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.Ontology;
import am.output.AlignmentOutput;

public class NYTInstanceMatcher{
	Ontology sourceOntology;
	public double luceneScoreThreshold = 0.7;
	public double AMSubstringThreshold = 0.8;
	
	private OntologyBackedKnowledgeBase kb;
	
	private String targetId;
	private String endpoint;
	
	private Vector<MatchingPair> mappings;
	
	boolean online = true;
	
	ExecutorService e =  Executors.newFixedThreadPool(8);
	
	ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5000));
	
	int ambiguous = 0;
	int noResults = 0;
	int singleResult = 0;
	
	Vector<Integer> runningThreads = new Vector<Integer>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3520832217713242380L;

	public NYTInstanceMatcher(String source, String targetId){
		this.targetId = targetId;
		
		mappings = new Vector<MatchingPair>();
		
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
		
		OntModel targetModel = null;
		
		if(kb != null)
			targetModel = kb.getOntModelByID(targetId);
		
		if(targetModel == null){
			System.out.println("We have no model, we have to go online");
		}
		
		String instanceURI;
		Statement stmt;
		
		for(int i = 0; i < indStatements.size(); i++ ){
			if(i % 100 == 0) System.out.println(i);
			
			stmt = indStatements.get(i);
			instanceURI = stmt.getSubject().getURI();
			
			String sourceLabel = Queries.getPropertyValue(sourceModel, instanceURI, NYTConstants.SKOS_PREFLABEL);
			sourceLabel = Utilities.processLabel(sourceLabel);
			
			System.out.println(sourceLabel);
			
			String topicPage = Queries.getPropertyValue(sourceModel, instanceURI, NYTConstants.NYT_TOPICPAGE);
			
			System.out.println(topicPage);
			
			List<Individual> candidates;
			
			if(targetModel != null){
				candidates = freeTextQuery(targetModel, sourceLabel, NYTConstants.FOAF_NAME);
			}
			else candidates = new ArrayList<Individual>();
			
			Individual matched = null;
			
			if(candidates.size() == 1){
				addSingleResult();
				
				matched = candidates.get(0);
				
				String targetLabel = Queries.getPropertyValue(targetModel, matched.getURI(), NYTConstants.FOAF_NAME);
				
				double sim = StringMetrics.AMsubstringScore(sourceLabel, targetLabel);
				
				if(sim > AMSubstringThreshold)
					addMapping(instanceURI, matched.getURI());
			}
			else if(candidates.size() == 0){
				if(online){
					runningThreads.add(i);
					
					if(targetId.equals(NYTConstants.DBP_PERSON)){
						executor.execute(new SPARQLSearchThread(this, i, instanceURI, sourceLabel, endpoint));
					}
					else if(targetId.equals(NYTConstants.FRB_PERSON)){
						executor.execute(new MQLSearchThread(this, i, instanceURI, "/people/person", sourceLabel));
					}
					else if(targetId.equals(NYTConstants.FRB_ORGANIZATION)){
						int index = sourceLabel.indexOf("(");
						if(index != -1)
							sourceLabel = sourceLabel.substring(0, index).trim();
						executor.execute(new MQLSearchThread(this, i, instanceURI, "/organization/organization", sourceLabel));
					}
					else if(targetId.equals(NYTConstants.FRB_LOCATION)){
						int index = sourceLabel.indexOf("(");
						if(index != -1)
							sourceLabel = sourceLabel.substring(0, index).trim();
						executor.execute(new MQLSearchThread(this, i, instanceURI, "/location/location", sourceLabel));
					}
					else{
						
					}
				}
				else {
					System.out.println("Without going online there's nothing we can do");
				}
			}
			else {
				addAmbiguous();
			}
		}
		//System.out.println(online);
		
		if(online){
			//executor.awaitTermination(5, TimeUnit.MINUTES);
			executor.shutdown();
			
			int remaining = executor.getActiveCount() + executor.getQueue().size();
			
			while(remaining > 0){
				Thread.sleep(3000);
				System.out.println(runningThreads);
				System.out.println(remaining);
				
				remaining = executor.getActiveCount() + executor.getQueue().size();
				
			}
			
			if(runningThreads.size() > 0){
				//TODO redo the queries which didn't work
			}
		}
			
		System.out.println("Ambiguous: " + ambiguous);
		System.out.println("No Results: " + noResults);
		System.out.println("Single result: " + singleResult);
		System.out.println("Total: " + (ambiguous + noResults + singleResult));
		
		if(mappings.size() == 0 ){
			System.out.println("Nosthing to output");
			return;
		}
		
		System.out.println("Writing on file...");
		String output = alignmentsToOutput(mappings);
		FileOutputStream fos = new FileOutputStream("alignment.rdf");
		fos.write(output.getBytes());
		System.out.println("Done");
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
	
	public static ResultSet freeTextQueryOnline(String endpoint, String search, int n) throws IOException{
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
		
		System.out.println(n + " exe query");
		String result = Queries.executeQuery(endpoint, queryString);
				
		System.out.println(n + " " + result);
		
		if(!result.startsWith("<sparql")) return null;
		
		ResultSet set = ResultSetFactory.fromXML(result);		
		//ArrayList<Individual> candidates = new ArrayList<Individual>();
	    	    
		return set;
	}
	
	public void addMapping(String source, String target){
		mappings.add(new MatchingPair(source, target));
	}
	
	public String alignmentsToOutput(Vector<MatchingPair> mappings){
		AlignmentOutput ao = new AlignmentOutput(null);
		ao.stringNS();
        ao.stringStart("yes", "0", "11", "onto1", "onto2", "uri1", "uri2");
        
        for (int i = 0, n = mappings.size(); i < n; i++) {
            MatchingPair mapping = mappings.get(i);
            String e1 = mapping.sourceURI;
            String e2 = mapping.targetURI;
            String measure = Double.toString(1.0);
            ao.stringElement(e1, e2, measure);
        }
        
        ao.stringEnd();
        return ao.getString();
	}
	
	public synchronized void addAmbiguous(){
		ambiguous++;
	}
	
	public synchronized void addSingleResult(){
		singleResult++;
	}
	
	public synchronized void addNoResults(){
		noResults++;
	}
	
	public void deleteRunningThread(Integer id){
		runningThreads.remove((Object) id );
	}
	
}
