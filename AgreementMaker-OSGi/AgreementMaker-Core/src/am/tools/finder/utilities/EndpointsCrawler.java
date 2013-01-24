package am.tools.finder.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class EndpointsCrawler {
	final static String urlString = "http://esw.w3.org/SparqlEndpoints";  
	
	public static ArrayList<SparqlEndpoint> crawl() 
	{
		HTMLEditorKit.ParserCallback callback = new CallBack();
		URL url;
		try {	url = new URL(urlString); }
		catch (MalformedURLException e) {	
			e.printStackTrace();
			return null;
		}
	    BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(url
			    .openStream()));
			ParserDelegator delegator = new ParserDelegator();
			delegator.parse(reader, callback, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ArrayList<SparqlEndpoint> endpoints = ((CallBack)callback).getEndpoints();
		System.out.println(endpoints.size() + " endpoints");
		return endpoints;
	}
}
// Implement the call back class. Just like a SAX content handler
class CallBack extends HTMLEditorKit.ParserCallback {
	boolean DEBUG = false;
	
	boolean firstTable = false;
	boolean stop = false;
	int row = 0;
	Tag latest = null;
	
	boolean blockName = false;
	
	ArrayList<SparqlEndpoint> endpoints = new ArrayList<SparqlEndpoint>();
	SparqlEndpoint endpoint;
	
	public void flush() throws BadLocationException{}
	
	public ArrayList<SparqlEndpoint> getEndpoints() {
		return endpoints;
	}
	public void handleComment(char[] data, int pos){}

	public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos)
	{
		if(DEBUG) System.out.println("Tag: " + tag );
		latest = tag;
		
		if(tag == Tag.TABLE && firstTable==false && stop==false){
			if(DEBUG) System.out.println("begin table");
			firstTable = true;
		}
		else if(tag == Tag.TABLE && firstTable==true){
			firstTable = false;
			stop = true;
		}
			
		if(tag == tag.TD && firstTable){
			if(row < 5)
				row++;
			else row = 1;
			if(DEBUG) System.out.println(row);
		}
		
		if(firstTable && tag == Tag.A && row==3 ){
			//System.out.println("a: "+a);
			if(DEBUG) System.out.println("href: "+ a.getAttribute(Attribute.HREF));
			endpoint.setUrl((String)a.getAttribute(Attribute.HREF));
			System.out.println(endpoint.getName());
			endpoints.add(endpoint);
			blockName = false;
		}
	}

	public void handleEndTag(HTML.Tag t, int pos){}
	public void handleSimpleTag(HTML.Tag t,MutableAttributeSet a, int pos){
	}
	public void handleError(String errorMsg, int pos){}
	public void handleEndOfLineString(String eol){}

	public void handleText(char[] data, int pos)
	{
		if(firstTable && latest==Tag.A && row==1 && blockName==false){
			endpoint = new SparqlEndpoint();
			String name = new String(data);
			endpoint.setName(name);
			blockName = true;
		}
	}
}
