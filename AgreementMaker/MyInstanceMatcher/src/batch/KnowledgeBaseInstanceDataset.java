package batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;

import edu.uic.advis.im.knowledgebase.ontology.OntologyBackedKnowledgeBase;
import edu.uic.advis.im.knowledgebase.ontology.OntologyKBFactory;

import am.AMException;
import am.app.mappingEngine.InstanceMatcherFede;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.InstanceDataset;


public class KnowledgeBaseInstanceDataset implements InstanceDataset{
	OntologyBackedKnowledgeBase kb;
	String datasetId;
	
	private double luceneScoreThreshold = 0.2;
	
	public KnowledgeBaseInstanceDataset(String xmlFile, String datasetId){
		kb = OntologyKBFactory.createKBFromXML(new File(xmlFile));		
		this.datasetId = datasetId;
		
		OntModel model = kb.getOntModelByID(datasetId);
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		System.out.println("Providing candidate instances for " + searchTerm);
		List<Instance> instances = new ArrayList<Instance>();
		
		OntModel model = kb.getOntModelByID(datasetId);
		
		searchTerm = searchTerm.replaceAll("'", "\\\\'");
		
		String property = "http://www.w3.org/2000/01/rdf-schema#label"; 
		
		//searchTerm = InstanceMatcherFede.processLabelBeforeCandidatesGeneration(searchTerm);
		/*String queryString = "PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>" +
		"\nPREFIX :       <http://example/>" +
		"\nPREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>" +
		"\nSELECT ?s ?score{" +
		"\n      (?lit ?score) pf:textMatch '\"" + searchTerm + "\"' ." +
		"\n	?s ?property ?lit." +
		"\n  FILTER (?score > " + luceneScoreThreshold +")}";*/
		
		String queryString = "PREFIX xsd:    <http://www.w3.org/2001/XMLSchema#>" +
		"\nPREFIX :       <http://example/>" +
		"\nPREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>" +
		"\nSELECT ?s ?score{" +
		"\n      (?lit ?score) pf:textMatch '\"" + searchTerm + "\"' ." +
		"\n	?s ?property ?lit." +
		"\n  FILTER (?score > " + luceneScoreThreshold +")" +
		"\n } ORDER BY DESC(?score) LIMIT 1000";
		
		
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
					//TODO parametrize the type
					Instance instance = new Instance(individual.getURI(), null);
					Resource ind = model.getResource(individual.getURI());
					List<Statement> stmts = model.listStatements(ind, (Property) null, (RDFNode) null).toList();
					String label = individual.getLabel("");
					instance.setProperty("label", label);
					instance.setStatements(stmts);
					instances.add(instance);
				}
					
			}
	    } finally {
	    	model.leaveCriticalSection();
	    }
	      
		return instances;
	}

	@Override
	public Instance getInstance(String uri) throws AMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instance> getInstances(String type, int limit)
			throws AMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instance> getInstances() throws AMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isIterable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[] args) throws AMException {
				
		System.out.println(System.getProperty("user.dir"));
		
		String xmlFile = new File(System.getProperty("user.dir")).getParent() + "/Datasets/dbpedia.xml";
		String datasetId = "dbp_labels";
		
		KnowledgeBaseInstanceDataset KBdataset = new KnowledgeBaseInstanceDataset(xmlFile, datasetId);
				
		System.out.println(KBdataset.getCandidateInstances("U2", null).toString().replaceAll(", ", "\n"));
		
	}
}
