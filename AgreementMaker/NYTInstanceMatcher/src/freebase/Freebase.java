package freebase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import matching.Instance;
import misc.Utilities;

public class Freebase {
	
	public static String endpoint = "http://www.freebase.com/api/service/search";
	public static String FREEBASE_URI = "http://rdf.freebase.com/ns/";
	public static int threshold = 10;
	
	public static List<Instance> query(String query) throws JSONException{
		String url = endpoint + query;
		
		System.out.println(url);
		
		String page;
		try {	page = Utilities.getPage(url);	} 
		catch (IOException e) {	return null;	}
		
		JSONObject object = new JSONObject(page);
		JSONArray results = object.getJSONArray("result");
		
		ArrayList<Instance> instances = new ArrayList<Instance>();
		
		JSONObject result;
		String uri;
		Instance instance;
		for (int i = 0; i < results.length(); i++) {
			result = (JSONObject) results.get(i);
			uri = result.get("id").toString();
			
			uri = FREEBASE_URI + uri.substring(1).replace('/','.');
			
			instance = new Instance(uri);
			instance.setLabel(result.get("name").toString());
			instances.add(instance);
			
			System.out.print( uri + " " + result.get("name") + " " + result.get("relevance:score") + "\n");
		}
		return instances;
	}
	
	public static void main(String[] args) throws JSONException {
		String query = "?query=al+gore&type=/people/person&threshold=" + threshold;
		query(query);
	}

	public static List<Instance> freeTextQueryOnline(String search, String type, int n) throws JSONException, UnsupportedEncodingException {
		search = URLEncoder.encode(search, "UTF-8");
		type = URLEncoder.encode(type, "UTF-8"); 
		String query = "?query=" + search + "&type=" + type + "&threshold=" + threshold;
		return query(query);
	}
	

}
