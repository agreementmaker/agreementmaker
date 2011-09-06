package freebase;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import misc.Utilities;

public class Freebase {
	
	public static String endpoint = "http://www.freebase.com/api/service/search";
	public static int threshold = 10;
	
	public static String query(String query){
		String url = endpoint + query;
		
		String result;
		try {	result = Utilities.getPage(url);	} 
		catch (IOException e) {	return null;	}
		
		return result;
	}
	
	
	
	public static void main(String[] args) throws JSONException {
		String query = "?query=al+gore&type=/people/person&threshold=" + threshold;
		
		String json = query(query);
		
		JSONObject object = new JSONObject(json);
		
		JSONArray results = object.getJSONArray("result");
		
		
		JSONObject result;
		for (int i = 0; i < results.length(); i++) {
			result = (JSONObject) results.get(i);
			System.out.print(result.get("id") + " " + result.get("name") + " " + result.get("relevance:score") + "\n");
		}
		
		//System.out.println(results);
		
	}
	

}
