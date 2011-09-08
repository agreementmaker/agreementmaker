package am.app.ontology.instance.endpoint;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
	public static int threshold = 30;
	
	public static List<Instance> query(String query) throws JSONException{
		String url = endpoint + query;
		
		ArrayList<Instance> instances = new ArrayList<Instance>();
		
		//System.out.println(url);
		
		String page;
		try {	page = HTTPUtility.getPage(url);	} 
		catch (IOException e) {	return instances;	}
		
		JSONObject object = new JSONObject(page);
		JSONArray results = object.getJSONArray("result");
				
		JSONObject result;
		String uri;
		Instance instance;
		for (int i = 0; i < results.length(); i++) {
			result = (JSONObject) results.get(i);
			uri = result.get("id").toString();
			
			uri = FREEBASE_URI + uri.substring(1).replace('/','.');
			
			instance = new Instance(uri, null);
			List<String> valueList = new ArrayList<String>();
			valueList.add(result.get("name").toString());
			instance.setProperty("label",valueList);
			
			JSONArray types = result.getJSONArray("type");
			
			//System.out.println(types);
			if(types != null){
				String typeName;
				JSONObject type;
				for (int j = 0; j < types.length(); j++) {
					type = types.getJSONObject(j);
					//		getJSONObject("name").toString();
					typeName = type.getString("name");
					//System.out.println(typeName);
					instance.setProperty("type", typeName);
				}
			}
			
			instances.add(instance);
			
			//System.out.print( uri + " " + result.get("name") + " " + result.get("relevance:score") + "\n");
		}
		return instances;
	}
	
	public static void main(String[] args) throws JSONException {
		String query = "?query=al+gore&type=/people/person&threshold=" + threshold;
		query(query);
	}

	@Override
	public List<Instance> freeTextQuery(String searchTerm, String type) throws Exception {
		searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
		type = URLEncoder.encode(type, "UTF-8"); 
		String query = "?query=" + searchTerm + "&type=" + type + "&threshold=" + threshold;
		return query(query);
	}

	@Override
	public String getPropertyValue(Instance i, String propertyURI) throws Exception {
		throw new AMException("This method is not supported for a FreeBase endpoint.");
	}

	@Override
	public List<Instance> listInstances(String type, int limit) throws Exception {
		throw new AMException("This method is not supported for a FreeBase endpoint.");
	}
	
}
