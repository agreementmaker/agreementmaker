package am.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class HTTPUtility {

	public static int connTimeoutMillis = 10000;
    public static int socketTimeoutMillis = 10000;
	
	public static String getPage(String pageURL) throws IOException{
		
		HttpContext context = new BasicHttpContext();
		context.setAttribute(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13");
	    		
		DefaultHttpClient httpClient = new DefaultHttpClient();
	    HttpParams params = httpClient.getParams();
	    HttpConnectionParams.setConnectionTimeout(params, connTimeoutMillis);
	    HttpConnectionParams.setSoTimeout(params, socketTimeoutMillis);
	    
	    
	    HttpGet httpget = new HttpGet(pageURL);
	    httpget.setHeader("Referer", "http://www.google.com");

	    HttpResponse response = httpClient.execute(httpget, context);
	    
	    System.out.println(response);
	    
	    HttpEntity entity = response.getEntity();
	   
	    
	    InputStream is = entity.getContent();
	 
	    
	    BufferedReader in = new BufferedReader(
                new InputStreamReader(
                is));

		String page = "";
		String line;
		
		while ((line = in.readLine()) != null)
		page += line;
		
		in.close();

	    return page;
	}
	
	public static String processLabel(String label){
		if(label.contains(",")){
			String[] splitted = label.split(",");
			return splitted[1].trim() + " " + splitted[0].trim();
		}
		return label; 
	}
	
}
