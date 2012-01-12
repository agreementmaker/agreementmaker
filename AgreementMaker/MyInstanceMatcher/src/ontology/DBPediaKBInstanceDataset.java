package ontology;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.AMException;
import am.app.ontology.instance.Instance;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class DBPediaKBInstanceDataset extends KnowledgeBaseInstanceDataset{
	
	Logger log;
	
	boolean useInfoboxes = true;
	
	public DBPediaKBInstanceDataset(String xmlFile, String datasetId) {
		super(xmlFile, datasetId);
		log = Logger.getLogger(DBPediaKBInstanceDataset.class);
		log.setLevel(Level.DEBUG);
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		//System.out.println("Providing candidate instances for " + searchTerm);
		List<Instance> instances = new ArrayList<Instance>();
		
		OntModel labels = kb.getOntModelByID("dbp_labels");
		OntModel disambiguations = kb.getOntModelByID("dbp_disambiguation");
		OntModel redirects = kb.getOntModelByID("dbp_redirects");
		OntModel infoboxes = kb.getOntModelByID("dbp_infoboxes");
				
		searchTerm = searchTerm.replaceAll("'", "\\\\'");
		
		String property = "http://www.w3.org/2000/01/rdf-schema#label"; 
		
		String queryString = "PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>" +
		"\nPREFIX :       <http://example/>" +
		"\nPREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>" +
		"\nSELECT ?s ?score{" +
		"\n      (?lit ?score) pf:textMatch '\"" + searchTerm + "\"' ." +
		"\n	?s ?property ?lit." +
		"\n  FILTER (?score > " + luceneScoreThreshold +")" +
		"\n } ORDER BY DESC(?score) LIMIT 1000";
				
		QuerySolutionMap map = new QuerySolutionMap();
		Property prop = labels.getProperty(property);
		if(prop == null) labels.createProperty(property);
		map.add("property", prop);
		
		ResultSet results = sparqlQuery(labels, queryString, map);
		
		HashSet<String> instanceURIs = new HashSet<String>();
		
		while(results.hasNext()){
			QuerySolution soln = results.next();
			RDFNode node = soln.get("s");
			
			if(node == null) continue;
			
			log.debug("Working on:" + node.toString());
			
			Individual individual = labels.getIndividual(node.toString());
						
			if(individual != null){
				boolean isDisambiguationPage = isDisambiguationPage(disambiguations, node.toString()); 
				if(isDisambiguationPage){
					log.info("Dis: " + node.toString());				
					continue;
				}
				
				String redirectsTo = redirectsTo(redirects, node.toString());				
				
				if(redirectsTo != null){
					if(instanceURIs.contains(node.toString())){
						//log.info("Redirects: " + redirectsTo);
						continue;
					}
						
					else{
						instanceURIs.add(node.toString());
					}
				}
				
				//TODO give the individual a type
				Instance instance = new Instance(individual.getURI(), null);
				
				if(useInfoboxes){
					Resource ind = infoboxes.getResource(individual.getURI());
					List<Statement> stmts = infoboxes.listStatements(ind, (Property) null, (RDFNode) null).toList();
					log.debug(stmts.toString().replaceAll("],", "]\n"));
					instance.setStatements(stmts);
				}
				else {
					//TODO prepare the statements in case of no infoboxes available
				}
				
				String label = individual.getLabel("");
				instance.setProperty("label", label);
				instances.add(instance);
			}		
		}
	      
		return instances;
	}
	
		

	private String redirectsTo(OntModel model, String subject) {
		String queryString = "select ?r WHERE {" +
		"\n    <" + subject + ">	<http://dbpedia.org/ontology/wikiPageRedirects> ?r"  +
		"\n} LIMIT 1";

		ResultSet results = sparqlQuery(model, queryString, null);
		
		while(results.hasNext()){
			QuerySolution soln = results.next();
			RDFNode node = soln.get("r");
			
			if(node == null) return null;
			
			else return node.toString();
		}
		return null;
	}

	private boolean isDisambiguationPage(OntModel model, String subject) {
		String queryString = "select ?r WHERE {" +
		"\n    <" + subject + ">	?p ?r"  +
		"\n} LIMIT 1";

		ResultSet results = sparqlQuery(model, queryString, null);
		
		while(results.hasNext()){
			QuerySolution soln = results.next();
			RDFNode node = soln.get("r");
			
			if(node == null) return false;
			
			else return true;
		}
		return false;
	}
	
	public ResultSet sparqlQuery(OntModel model, String queryString, QuerySolutionMap map){
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = null;
		
		if(map == null)
			qexec = QueryExecutionFactory.create(query, model);
		else qexec = QueryExecutionFactory.create(query, model, map);
		
		ResultSet results = qexec.execSelect() ;
		
		return results;
	}
	
	public static void main(String[] args) throws AMException {
		System.out.println(System.getProperty("user.dir"));
		String xmlFile = new File(System.getProperty("user.dir")).getParent() + "/Datasets/dbpedia.xml";
		String datasetId = "dbp_labels";
		DBPediaKBInstanceDataset KBdataset = new DBPediaKBInstanceDataset(xmlFile, datasetId);		
		System.out.println(KBdataset.getCandidateInstances("michael schumacher", null).toString().replaceAll(", ", "\n"));
		System.out.println(KBdataset.getCandidateInstances("monza", null).toString().replaceAll(", ", "\n"));
		System.out.println(KBdataset.getCandidateInstances("CNBC", null).toString().replaceAll(", ", "\n"));
	}
	
}
