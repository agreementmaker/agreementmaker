package am.app.ontology.instance.endpoint;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.AMException;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.datasets.SeparateFileInstanceDataset;
import am.utility.HTTPUtility;

import com.hp.hpl.jena.rdfxml.xmlinput.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * SPARQL Endpoint wrapper.
 * 
 * @author federico
 *
 */
public class SparqlEndpoint implements SemanticWebEndpoint {
	public String endpoint;
	private boolean useCache = false;
	
	private HashMap<String, String> cache;
	
	private String cacheFile;
	
	public SparqlEndpoint(String endpoint){
		this.endpoint = endpoint;
		cache = new HashMap<String, String>();
		if(useCache){
			System.out.println("Sparql endpoint is loading cache...");
			loadCache();
		    System.out.println("Done");
		}
	}
	
	public SparqlEndpoint(String endpoint, String cacheFilename) {
		this.endpoint = endpoint;
		useCache = true;
		this.cacheFile = cacheFilename;
		cache = new HashMap<String, String>();
		loadCache();
		
	}

	
	public List<Statement> singleEntityQuery(String uri){
		List<Statement> stmts = new ArrayList<Statement>();
		
		String url;
		try {
			url = singleEntityQueryString(uri);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return stmts;
		}
				
		//System.out.println(url);
		
		String rdf = null;
		rdf = getRdfFromUrl(url);
		
		if(rdf == null){
			System.err.println("null rdf");
			return stmts; 
		}
		
		if(!rdf.contains("rdf")){
			System.err.println("Not an rdf document");
			return stmts; 
		}
		
		rdf = rdf.replaceAll("2000pop", "pop2000");
		
		if(!cache.containsKey(url))
			cache.put(url, rdf);
		
		Model model = ModelFactory.createDefaultModel();
		JenaReader reader = new JenaReader();
		try {
			reader.read(model, new ByteArrayInputStream(rdf.getBytes("UTF-8")), "http://blabla");
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("Impossible to create a model");
			System.out.println(rdf);
			return stmts;
		}
		
		return model.listStatements().toList();
	}
	
	public String singleEntityQueryString(String uri) throws UnsupportedEncodingException{
		String query = "construct { <" + uri + "> ?prop ?obj }" +
				   "\nWHERE {" +
				   "\n    <" + uri + "> ?prop ?obj ." +
				   "\n    FILTER ( lang(?obj) = \"\" || lang(?obj) = \"en\" )" +
				   "\n} LIMIT 1000";
		
		
		query = URLEncoder.encode(query, "UTF-8");
		
		String parameters = "?output=rdf&query=" + query;
		String urlStr = endpoint + parameters;
		
		return urlStr;
	}
	
	@Override
	public List<Instance> freeTextQuery(String searchTerm, String type) throws Exception {
		List<Instance> instances = new ArrayList<Instance>();
		
		String urlStr = freeTextQueryString(searchTerm, type, 10000);
		
		String rdf = null;
		rdf = getRdfFromUrl(urlStr);
		
		if(rdf == null){
			System.err.println("null rdf");
			return instances; 
		}
		
		if(!rdf.contains("rdf")){
			System.err.println("Not an rdf document");
			return instances; 
		}
		
		
		rdf = rdf.replaceAll("2000prop", "prop2000");
		
		
		Model model = ModelFactory.createDefaultModel();
		JenaReader reader = new JenaReader();
		try {
			reader.read(model, new ByteArrayInputStream(rdf.getBytes("UTF-8")), "http://blabla");
		} catch (Exception e) {
			//e.printStackTrace();
			System.err.println("Impossible to create a model");
			System.out.println(rdf);
			return instances;
		}
		
		//System.out.println(model);
		
		//System.out.println(model.listStatements().toList());
		
		
		ResIterator it = model.listSubjects();
		
		//System.out.println(it.toList());
		while(it.hasNext()){
			Resource res = it.next();
			
			String instanceURI = res.getURI();
			
			String label = SeparateFileInstanceDataset.getPropertyValue(model, instanceURI, RDFS.label.getURI());
					
			Instance instance = new Instance(instanceURI, null);
			instance.setProperty("label", label);
			
			instance.setStatements(model.listStatements(model.getResource(instanceURI), (Property) null, (RDFNode) null).toList());
			
			instances.add(instance);
		}
		return instances;
	}

	@Override
	public String getPropertyValue(Instance i, String propertyURI) throws Exception {
		throw new AMException("Method not implemented.");
	}

	@Override
	public List<Instance> listInstances(String type, int limit)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void loadCache() {
		System.out.println("Sparql endpoint is loading cache...");
		ObjectInput in;
		try {
			in = new ObjectInputStream(new FileInputStream(cacheFile));
			Object input = in.readObject();
			cache = (HashMap<String, String>) input;	
		} catch (Exception e) {
			cache = new HashMap<String, String>();
		}
		System.out.println("Done");
	}
	
	public String getRdfFromUrl(String url){
		String rdf = null;
		boolean foundInCache = false;
		if(useCache){
			rdf = cache.get(url);	
			if(rdf != null)
				foundInCache = true;
		}
		if(!foundInCache){
			System.out.println("Sparql endpoint is querying online: " + url);
			try {	rdf = HTTPUtility.getPage(url);	} 
			catch (IOException e) {	
				System.err.println("Connection problem"); 
				return null;	
			}
			
			//System.out.println(rdf);
		}
		return rdf;
	}

	public String freeTextQueryString(String searchTerm, String type, int limit) throws Exception {
		searchTerm = searchTerm.replaceAll("'", "\\\\'");
		
		//System.out.println(type);
		String query;
		
		if(type != null){
			query = "PREFIX foaf:	<http://xmlns.com/foaf/0.1/>" +
			   "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			   "construct { ?p ?prop ?obj }" +
			   "WHERE {" +
			   "	?p rdf:type <" + type + "> ." +
			   "	?p rdfs:label ?name ." +
			   "    ?p ?prop ?obj ." +
			   "    ?name bif:contains '\"" + searchTerm + "\"' ." +
			   "    FILTER ( lang(?obj) = \"\" || lang(?obj) = \"en\" )" +
			   "} LIMIT " + limit;

		}
		else{
			query = "PREFIX foaf:	<http://xmlns.com/foaf/0.1/>" +
			   "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			   "construct { ?p ?prop ?obj }" +
			   "WHERE {" +
			   "	?p rdfs:label ?name ." +
			   "    ?p ?prop ?obj ." +
			   "    ?name bif:contains '\"" + searchTerm + "\"' ." +
			   "    FILTER ( lang(?obj) = \"\" || lang(?obj) = \"en\" )" +
			   "} LIMIT " + limit;
		}
		
		query = URLEncoder.encode(query, "UTF-8"); 
		String parameters = "?output=rdf&query=" + query;
		String urlStr = endpoint + parameters;
		//System.out.println(urlStr);
		
		return urlStr;
	}

	public void setCacheFile(String cacheFile) {
		this.cacheFile = cacheFile;
		useCache = true;
		loadCache();
	}

	public String lightFreeTextQueryString(String searchTerm, String type, int limit) throws UnsupportedEncodingException {
		searchTerm = searchTerm.replaceAll("'", "\\\\'");
		
		//System.out.println(type);
		
		String query = "PREFIX foaf:	<http://xmlns.com/foaf/0.1/>" +
				   "\nPREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				   "\nselect ?p ?label ?comment" +
				   "\nWHERE {" +
				   "\n	?p rdf:type <" + type + "> ." +
				   "\n	?p rdfs:label ?label ." +
				   "\n    ?p rdfs:comment ?comment ." +
				   "\n    ?label bif:contains '\"" + searchTerm + "\"' ." +
				   "\n    FILTER ( lang(?label) = \"\" || lang(?label) = \"en\" ) ." +
				   "\n    FILTER ( lang(?comment) = \"\" || lang(?comment) = \"en\" )" +
				   "\n} LIMIT " + limit;
	
		query = URLEncoder.encode(query, "UTF-8"); 
		String parameters = "?output=rdf&query=" + query;
		String urlStr = endpoint + parameters;
		//System.out.println(urlStr);
		
		return urlStr;
	}
	
	public void persistCache() throws FileNotFoundException, IOException{
		 System.out.println("Writing to file...");
		 ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFile));
		 out.writeObject(cache);
		 out.close();
		 System.out.println("Done");		
	}

}
