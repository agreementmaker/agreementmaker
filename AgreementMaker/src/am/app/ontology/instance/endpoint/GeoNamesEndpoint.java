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

import org.apache.log4j.Logger;

import com.hp.hpl.jena.Jena;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.vocabulary.RDF;

import am.AMException;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.SeparateFileInstanceDataset;
import am.utility.HTTPUtility;

public class GeoNamesEndpoint implements SemanticWebEndpoint{

	public static String endpoint = "http://api.geonames.org/search?type=rdf&maxRows=10&username=me";
	public static String GEONAMES_URI = "http://www.geonames.org/ontology#";
	public static int threshold = 40;
		
	static int count = 0;
	
	private boolean useCache = true;
	
	private HashMap<String, String> cache;
	
	private String cacheFile = "geonamesRDFCache.ser";
	
	private Logger log;
	
	public GeoNamesEndpoint(){
		log = Logger.getLogger(GeoNamesEndpoint.class);
		cache = new HashMap<String, String>();
		if(useCache){
			System.out.println("GeoNames is loading cache...");
			loadCache();
		    System.out.println("Done");
		}
	}
	
	public GeoNamesEndpoint(String cacheFilename){
		cacheFile = cacheFilename;
		useCache = true;
		cache = new HashMap<String, String>();
		
		System.out.println("Freebase is loading cache...");
		loadCache();
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
			System.out.println("Geonames is querying online");
			try {	rdf = HTTPUtility.getPage(url);	} 
			catch (IOException e) {	
				System.err.println("Connection problem"); 
				return null;	
			}
		}
		return rdf;
	}
	
	public List<Instance> query(String query){
		String url = endpoint + query;
		
		ArrayList<Instance> instances = new ArrayList<Instance>();
		
		String rdf = null;
				
		boolean foundInCache = false;
		if(useCache){
			rdf = cache.get(url);	
			if(rdf != null)
				foundInCache = true;
		}
		//System.out.println(url);
		if(!foundInCache){
			try {	rdf = HTTPUtility.getPage(url);	} 
			catch (IOException e) {	return instances;	}
		}
		
		//System.out.println(rdf);
		
		if(rdf == null){
			System.err.println("null rdf");
			return instances;
		}
		
		Model model = ModelFactory.createDefaultModel();
		JenaReader reader = new JenaReader();
		try {
			reader.read(model, new ByteArrayInputStream(rdf.getBytes("UTF-8")), "http://blabla");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(model);
		
		StmtIterator it = model.listStatements((Resource)null, RDF.type, (RDFNode)null);		
		while(it.hasNext()){
			Statement stmt = it.next();
			
			String instanceURI = stmt.getSubject().getURI();
			
			String label = SeparateFileInstanceDataset.getPropertyValue(model, instanceURI, GEONAMES_URI + "name");
					
			Instance instance = new Instance(instanceURI, null);
			instance.setProperty("label", label);
			
			instance.setStatements(model.listStatements(model.getResource(instanceURI), (Property) null, (RDFNode) null).toList());
			
			instances.add(instance);
		}
		
		return instances;
	}
		
	public static void main(String[] args) throws Exception {
		String searchTerm = "Monza";
		GeoNamesEndpoint geoNames = new GeoNamesEndpoint();
		System.out.println(geoNames.freeTextQuery(searchTerm, null));
	}

	@Override
	public List<Instance> freeTextQuery(String searchTerm, String type) throws Exception {
		searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
		String query = "&q=" + searchTerm;
		return query(query);
	}
	
	public String freeTextQueryString(String searchTerm, String type) throws Exception {
		searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
		String query = "&q=" + searchTerm;
		return endpoint + query;
	}

	@Override
	public String getPropertyValue(Instance i, String propertyURI) throws Exception {
		throw new AMException("This method is not supported for a FreeBase endpoint.");
	}

	@Override
	public List<Instance> listInstances(String type, int limit) throws Exception {
		throw new AMException("This method is not supported for a FreeBase endpoint.");
	}
	
	public void setCacheFile(String cacheFile){
		this.cacheFile = cacheFile; 
		cache = null;
		loadCache();
	}

	private void loadCache() {
		ObjectInput in;
		try {
			in = new ObjectInputStream(new FileInputStream(cacheFile));
			Object input = in.readObject();
			cache = (HashMap<String, String>) input;	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void persistCache() throws FileNotFoundException, IOException{
		 log.info("Writing cache to file... [" + cacheFile + "]");
		 ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFile));
		 out.writeObject(cache);
		 out.close();
		 log.info("Done");		
	}

}
