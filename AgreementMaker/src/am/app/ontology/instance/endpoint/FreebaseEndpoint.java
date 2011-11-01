package am.app.ontology.instance.endpoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import am.AMException;
import am.app.ontology.instance.Instance;
import am.utility.HTTPUtility;

public class FreebaseEndpoint implements SemanticWebEndpoint {

	public static String endpoint = "http://www.freebase.com/api/service/search";
	public static String FREEBASE_URI = "http://rdf.freebase.com/ns/";
	public static int threshold = 40;
		
	static int count = 0;
	
	private boolean useCache = true;
	
	private HashMap<String, String> cache;
	
	private String cacheFile = "freebaseCacheLocations.ser";
	
	public FreebaseEndpoint(){
		cache = new HashMap<String, String>();
		if(useCache){
			System.out.println("Freebase is loading cache...");
			loadCache();
		    System.out.println("Done");
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
	
	public List<Instance> query(String query) throws JSONException{
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
			catch (IOException e) {	return instances;	}
		}
		
		JSONObject object = new JSONObject(json);
		
		//System.out.println(page);
		
		JSONArray results = object.getJSONArray("result");
				
		JSONObject result;
		String uri;
		String score;
		Instance instance = null;
		for (int i = 0; i < results.length(); i++) {
			result = (JSONObject) results.get(i);
			uri = result.get("id").toString();
			score = result.optString("relevance:score");
						
			uri = FREEBASE_URI + uri.substring(1).replace('/','.');
			
			instance = new Instance(uri, null);
			List<String> valueList = new ArrayList<String>();
			Object name = null;
			try{
				name = result.get("name");
			}
			catch (JSONException e) {
				continue;
			}
			valueList.add(name.toString());
			instance.setProperty("label",valueList);
			
			if(score != null) instance.setProperty("score", score);
						
			JSONArray types = result.getJSONArray("type");
			
			//System.out.println(types);
			if(types != null){
				String typeName;
				JSONObject type;
				for (int j = 0; j < types.length(); j++) {
					type = types.getJSONObject(j);
					typeName = type.getString("name");
					instance.setProperty("type", typeName);
				}
			}
			
			JSONArray alias = result.optJSONArray("alias");
			
			//System.out.println(types);
			if(alias != null){
				for (int j = 0; j < alias.length(); j++) {
					String al = alias.getString(j);
					instance.setProperty("alias", al);
				}
			}
			
			instances.add(instance);
			
		}
		return instances;
	}
		
	public static void main(String[] args) throws JSONException {
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
		
		try {	fis = new FileInputStream(cacheFile); }
		catch (FileNotFoundException e1) {
			System.err.println("The cache file doesn't exist");
			cache = new HashMap<String, String>();
			return;
		}
		
		try {
			in = new ObjectInputStream(fis);
			input  = in.readObject();
			cache = (HashMap<String, String>) input;
			
		} catch (IOException e1) {
			System.err.println("The cache will be empty");
			cache = new HashMap<String, String>();
			return;
			
		} catch (ClassNotFoundException e) {
			System.err.println("The cache will be empty");
			cache = new HashMap<String, String>();
			return;
		}
	}
	
}
