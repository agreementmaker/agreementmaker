package ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.rulesys.builtins.IsDType;

public class DBPediaKBInstanceDataset extends KnowledgeBaseInstanceDataset{
	
	Logger log = Logger.getLogger(DBPediaKBInstanceDataset.class);
	
	boolean useInfoboxes = true;
	boolean useGeo = true;
	boolean useAbstracts = true;
	
	boolean autoSave = false;
	
	HashMap<String, List<String>> uriCache = new HashMap<String, List<String>>();
	
	HashMap<String, List<Instance>> cache = new HashMap<String, List<Instance>>();
	
	String uriCacheFilename = "";
	String cacheFilename = "";
	
	int count = 0;
	
	public DBPediaKBInstanceDataset(String xmlFile, String datasetId) {
		super(xmlFile, datasetId);
		//log.setLevel(Level.DEBUG);
		
	}
	
	public DBPediaKBInstanceDataset(String cacheFilename){
		this.cacheFilename = cacheFilename;
		if(new File(cacheFilename).exists()){
			setCache(cacheFilename);
		}
	}
	
	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
	throws AMException {
		count++;
		
		if(autoSave && count % 500 == 0){
			try {
				persistCache(cacheFilename + ".asv");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
					
		log.debug("Providing candidate instances for " + searchTerm + " type=" + type);
		List<Instance> instances = new ArrayList<Instance>();

		OntModel labels = null;
		OntModel disambiguations = null;
		OntModel redirects = null;
		OntModel infoboxes = null;
		OntModel geo = null;
		OntModel types = null;
		OntModel abstracts = null;
		
		if(kb != null){
			labels = kb.getOntModelByID("dbp-labels");
			disambiguations = kb.getOntModelByID("dbp-disambiguation");
			redirects = kb.getOntModelByID("dbp-redirects");
			infoboxes = kb.getOntModelByID("dbp-infoboxes");
			geo = kb.getOntModelByID("dbp-geo");
			types = kb.getOntModelByID("dbp-types");
			abstracts = kb.getOntModelByID("dbp-abstracts");
		}
		
		//System.out.println(types.listStatements().next());
		
		//log.info(disambiguations);
		//log.info(redirects);

		searchTerm = searchTerm.replaceAll("'", "\\\\'");

		String queryId = searchTerm + ":" + type;
		
		List<Instance> candidates = cache.get(queryId);
		if(candidates != null)
			return candidates;
		

		List<String> instanceURIs = uriCache.get(queryId);

		if(instanceURIs != null){
			System.out.println("hit in cache");
		}
		else{
			
			
			
			instanceURIs = new ArrayList<String>();
			
			//WE HAVE TO CREATE AND EXECUTE THE QUERY
			String property = "http://www.w3.org/2000/01/rdf-schema#label"; 
			String queryString;
			int limit = 100;
			
			queryString = "\nPREFIX pf:     <http://jena.hpl.hp.com/ARQ/property#>" +
						"\nSELECT ?s ?score{" +
						"\n (?lit ?score) pf:textMatch '\"" + searchTerm + "\"' ." +
						"\n	?s ?property ?lit." +
						"\n  FILTER (?score > " + luceneScoreThreshold +")" +
						"\n } ORDER BY DESC(?score) LIMIT " + limit;
	
			QuerySolutionMap map = new QuerySolutionMap();
			Property prop = labels.getProperty(property);
			if(prop == null) labels.createProperty(property);
			map.add("property", prop);

			ResultSet results = sparqlQuery(labels, queryString, map);
			
			
			HashSet<String> uriSet = new HashSet<String>();

			if(!results.hasNext()) log.info("No results");
			
			while(results.hasNext()){
				QuerySolution soln = results.next();
				RDFNode node = soln.get("s");

				if(node == null) continue;

				if(type != null){
					String typeQuery = "select ?t WHERE {" +
							"\n    <" + node.toString() + "> ?t <"+ type +">"  +
							"\n}";

					//System.out.println(typeQuery);
					
					ResultSet res = sparqlQuery(types, typeQuery, null);

					//No results means the instance is not of the right type
					if(!res.hasNext()){
						//log.info(node.toString() + " not a " + type);
						continue;	
					}
				}
				//log.debug("Working on:" + node.toString());

				boolean isDisambiguationPage = isDisambiguationPage(disambiguations, node.toString()); 
				if(isDisambiguationPage){
					log.debug("Dis: " + node.toString());				
					continue;
				}

				String redirectsTo = redirectsTo(redirects, node.toString());				

				if(redirectsTo != null){
					if(isDisambiguationPage(disambiguations, redirectsTo)){
						continue;
					}
					//log.info(node.toString() + " redirectsTo " + redirectsTo);
					if(!uriSet.contains(redirectsTo)){
						uriSet.add(redirectsTo);
					}
				}
				else{
					uriSet.add(node.toString());
				}
			}
			
			Iterator<String> it = uriSet.iterator();
			while(it.hasNext()){
				instanceURIs.add(it.next());
			}
			uriCache.put(queryId, instanceURIs);
		}
		
		Iterator<String> it = instanceURIs.iterator();
		String uri;
		while(it.hasNext()){
			uri = it.next();

			//log.info(uri);			
			Individual individual = labels.getIndividual(uri);
			if(individual == null) continue;
			
			//TODO give the individual a type
			Instance instance = new Instance(individual.getURI(), null);

			if(useInfoboxes){
				Resource ind = infoboxes.getResource(individual.getURI());
				List<Statement> stmts = infoboxes.listStatements(ind, (Property) null, (RDFNode) null).toList();
				//log.debug(stmts.toString().replaceAll("],", "]\n"));
				instance.setStatements(stmts);
			}
			if(useGeo){
				Resource ind = geo.getResource(individual.getURI());
				if(ind != null){
					List<Statement> stmts = geo.listStatements(ind, (Property) null, (RDFNode) null).toList();
					//log.debug(stmts.toString().replaceAll("],", "]\n"));
					instance.addStatements(stmts);
				}
			}
			if(useAbstracts){
				Resource ind = abstracts.getResource(individual.getURI());
				if(ind != null){
					List<Statement> stmts = abstracts.listStatements(ind, (Property) null, (RDFNode) null).toList();
					//log.debug(stmts.toString().replaceAll("],", "]\n"));
					instance.addStatements(stmts);
				}
			}
			else {
				//TODO prepare the statements in case of no infoboxes available
			}

			String label = individual.getLabel("");
			instance.setProperty("label", label);
			instances.add(instance);

		}
		cache.put(queryId, instances);		
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
	
	public void setUriCache(String filename){
		uriCacheFilename = filename;
		loadUriCache();
	}
	
	private void loadUriCache() {
		FileInputStream fis = null;
		ObjectInputStream in;
		Object input = null;
		
		log.info("DBpedis is loading cache..." + " [" + uriCacheFilename + "]");
				
		try {	fis = new FileInputStream(uriCacheFilename); }
		catch (FileNotFoundException e1) {
			log.error("The cache file doesn't exist");
			uriCache = new HashMap<String, List<String>>();
			return;
		}
		try {
			in = new ObjectInputStream(fis);
			input  = in.readObject();
			uriCache = (HashMap<String, List<String>>) input;
		} catch (Exception e1) {
			log.error("The cache will be empty");
			uriCache = new HashMap<String, List<String>>();
			return;
		}
		log.info("Done");
	}
	
	public void persistUriCache() throws FileNotFoundException, IOException{
		 log.info("Writing URI cache to file... [" + uriCacheFilename + "]");
		 ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(uriCacheFilename));
		 out.writeObject(uriCache);
		 out.close();
		 log.info("Done");			
	}
	
	public static void main(String[] args) throws AMException {
		System.out.println(System.getProperty("user.dir"));
		String xmlFile = new File(System.getProperty("user.dir")).getParent() + "/Datasets/dbpedia.xml";
//		String datasetId = "dbp_labels";
//		DBPediaKBInstanceDataset KBdataset = new DBPediaKBInstanceDataset(xmlFile, datasetId);		
//		System.out.println(KBdataset.getCandidateInstances("michael schumacher", null).toString().replaceAll(", ", "\n"));
//		System.out.println(KBdataset.getCandidateInstances("monza", null).toString().replaceAll(", ", "\n"));
//		System.out.println(KBdataset.getCandidateInstances("CNBC", null).toString().replaceAll(", ", "\n"));

		String datasetId = "dbp_redirects";
		DBPediaKBInstanceDataset KBdataset = new DBPediaKBInstanceDataset(xmlFile, datasetId);		
		
		OntModel redirects = KBdataset.getKb().getOntModelByID("dbp-redirects");
		OntModel disambiguations = KBdataset.getKb().getOntModelByID("dbp-disambiguation");
		
		
		System.out.println(KBdataset.isDisambiguationPage(disambiguations, "http://dbpedia.org/resource/Hart_Island"));
		
//		System.out.println("Printing size");
//		StmtIterator it = redirects.listStatements();
//		int i = 0;
//		while(it.hasNext()) i++;
//		System.out.println("size: " + i);
//		
		//System.out.println(KBdataset.redirectsTo(redirects, "http://dbpedia.org/resource/Gregory_B._Craig"));
		
	}

	public void setCache(String cacheFile) {
		cacheFilename = cacheFile;
		loadCache();
	}

	private void loadCache() {
		FileInputStream fis = null;
		ObjectInputStream in;
		Object input = null;
		
		log.info("DBpedia is loading cache..." + " [" + cacheFilename + "]");
				
		try {	fis = new FileInputStream(cacheFilename); }
		catch (FileNotFoundException e1) {
			log.error("The cache file doesn't exist");
			cache = new HashMap<String, List<Instance>>();
			return;
		}
		try {
			in = new ObjectInputStream(fis);
			input  = in.readObject();
			cache = (HashMap<String, List<Instance>>) input;
		} catch (Exception e1) {
			log.error("The cache will be empty");
			cache = new HashMap<String, List<Instance>>();
			return;
		}
		log.info("Done");
		
	}

	public void persistCache(String file) throws FileNotFoundException, IOException {
		if(file == null) file = cacheFilename;
		log.info("Writing cache to file... [" + file + "]");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(cache);
		out.close();
		log.info("Done");	
	}

}
