package am.app.ontology.instance.endpoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import am.AMException;
import am.app.ontology.instance.Instance;
import am.utility.HTTPUtility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FreebaseEndpoint implements SemanticWebEndpoint {

	public static String endpoint = "http://www.freebase.com/api/service/search";
	public static String FREEBASE_URI = "http://rdf.freebase.com/ns/";
	public static int threshold = 40;
		
	static int count = 0;
	
	private boolean useCache = false;
	
	private HashMap<String, String> cache;
	
	private String cacheFile = "freebaseCache.ser";
	
	//private String outputCacheFile = "freebaseCacheOutput.ser";
	
	private Logger log;
	
	public FreebaseEndpoint(){
		log = Logger.getLogger(FreebaseEndpoint.class);
		cache = new HashMap<String, String>();
		if(useCache){
			loadCache();
		}
	}
	
	public FreebaseEndpoint(String cacheFilename){
		cacheFile = cacheFilename;
		useCache = true;
		cache = new HashMap<String, String>();
		
		System.out.println("Freebase is loading cache...");
		loadCache();
		System.out.println("Done");
	
	}
	
	public String getJsonFromUrl(String url){
		String json = null;
		boolean foundInCache = false;
		if(useCache){
			json = cache.get(url);	
			if(json != null)
				foundInCache = true;
		}
		if(!foundInCache){
			System.out.println(url);
			try {	json = HTTPUtility.getPage(url);	} 
			catch (IOException e) {	
				System.err.println("Connection problem"); 
				return null;	
			}
		}
		return json;
	}
	
	public List<Instance> query(String query) throws JsonProcessingException, IOException {
		String url = endpoint + query;
		
		ArrayList<Instance> instances = new ArrayList<Instance>();
		
		String json = null;
				
		boolean foundInCache = false;
		if(useCache){
			json = cache.get(url);	
			if(json != null)
				foundInCache = true;
		}
		if(!foundInCache){
			System.out.println(url);
			try {	json = HTTPUtility.getPage(url);	} 
			catch (IOException e) {	
				log.error("Problems in getting the page");
				return instances;	
			}
			System.out.println("caching");
			cache.put(url, json);
		}
		
		ObjectMapper m = new ObjectMapper();
		JsonNode rootNode = m.readTree(json);
		
		//JSONObject object = new JSONObject(json);
		
		//System.out.println(page);
		
		JsonNode resultsNode = rootNode.path("result");
				
		JsonNode result;
		String uri;
		Instance instance = null;
		for (int i = 0; resultsNode.get(i) != null; i++) {
			result = resultsNode.get(i);
			uri = result.get("id").textValue();
			uri = FREEBASE_URI + uri.substring(1).replace('/','.');
			
			instance = new Instance(uri, null);
			Set<String> valueList = new HashSet<String>();
			Object name = null;
			name = result.get("name");
			
			valueList.add(name.toString());
			instance.setProperty("label",valueList);
			
			if(result.has("relevance:score")) {
				String score = result.get("relevance:score").textValue();
				instance.setProperty("score", score);
			}
						
			JsonNode types = result.get("type");
			
			//System.out.println(types);
			if(types != null){
				String typeName;
				JsonNode type;
				for (int j = 0; types.get(j) != null; j++) {
					type = types.get(j);
					typeName = type.get("name").textValue();
					instance.setProperty("type", typeName);
				}
			}
						
			//System.out.println(types);
			if (result.has("alias")) {
				JsonNode alias = result.get("alias");
				for (int j = 0; alias.get(j) != null; j++) {
					String al = alias.get(j).textValue();
					instance.setProperty("alias", al);
				}
			}
			
			instances.add(instance);
			
		}
		return instances;
	}
		
	public static void main(String[] args) throws JsonProcessingException, IOException {
		String query = "?query=al+gore&type=/people/person&threshold=" + threshold;
		FreebaseEndpoint fb = new FreebaseEndpoint();
		fb.query(query);
	}

	@Override
	public List<Instance> freeTextQuery(String searchTerm, String type) throws Exception {
		searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
		if(type != null)
			type = URLEncoder.encode(type, "UTF-8"); 
		String query = "?query=" + searchTerm;
		if(type != null) query += "&type=" + type;
		query += "&threshold=" + threshold;
		return query(query);
	}
	
	public String freeTextQueryString(String searchTerm, String type) throws Exception {
		searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
		if(type != null)
			type = URLEncoder.encode(type, "UTF-8"); 
		String query = "?query=" + searchTerm;
		if(type != null) query += "&type=" + type;
		query += "&threshold=" + threshold;
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
		FileInputStream fis = null;
		ObjectInputStream in;
		Object input = null;
		
		log.info("Freebase is loading cache..." + " [" + cacheFile + "]");
				
		try {	fis = new FileInputStream(cacheFile); }
		catch (FileNotFoundException e1) {
			log.error("The cache file doesn't exist");
			cache = new HashMap<String, String>();
			return;
		}
		try {
			in = new ObjectInputStream(fis);
			input  = in.readObject();
			cache = (HashMap<String, String>) input;
		} catch (Exception e1) {
			log.error("The cache will be empty");
			cache = new HashMap<String, String>();
			return;
		}
		
		log.info("Done");
	}
	
	public void persistCache() throws FileNotFoundException, IOException{
		 log.info("Writing cache to file... [" + cacheFile + "]");
		 ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFile));
		 out.writeObject(cache);
		 out.close();
		 log.info("Done");		
	}
	
}
