package am.matcher.wikipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WikiEngine {
	final static String OPEN_QUERY = "http://en.wikipedia.org/w/api.php?action=opensearch&format=txt&search=";
	final static String CATEGORY_QUERY = "http://en.wikipedia.org/w/api.php?action=query&format=xml&cllimit=100&prop=categories&titles=";
	
	public WikiEngine(){
		
	}
	
	public String openSearch(String search){
		search = search.toLowerCase();
		try { 
			search = URLEncoder.encode(search, "UTF-8"); 
		} 
		catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			return null;
		}
		
		//output=xml&
		
		String urlStr = OPEN_QUERY + search;
		//System.out.println(urlStr);
		return getPage(urlStr);
	}
	
	private String queryCategories(String page){
		try { 
			page = URLEncoder.encode(page, "UTF-8"); 
		} 
		catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
			return null;
		}
		
		//output=xml&
		
		String urlStr = CATEGORY_QUERY + page;
		//System.out.println(urlStr);
		return getPage(urlStr);
	}
	
	private String getPage(String urlStr){
		URL url = null;
		
		try {
			url = new URL(urlStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		URLConnection conn = null;
		
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		BufferedReader rd = null;
		try {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		
		String result = ""; 
		String line;
		try {
			while ((line = rd.readLine()) != null){
				result += line + "\n";
			}
			rd.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return result; 
	}
	
	public ArrayList<String> getPageResults(String pages){
		ArrayList<String> retValue = new ArrayList<String>();
	
		if(!pages.startsWith("[\"")){
			System.out.println("page format problem");
			return null;
		}
		
		pages = pages.substring(2);
			
		int start = pages.indexOf("[");
		
		if(start < 0){
			System.out.println("page format problem");
			return null;
		}
		
		int stop = pages.indexOf("]");
		
		if(stop < 0){
			System.out.println("page format problem");
			return null;
		}
		
		pages = pages.substring(start+1, stop);
		System.out.println(pages);
		
		String[] splitted = pages.split(",");
		
		int l;
		for (int i = 0; i < splitted.length; i++) {
			l = splitted[i].length();
			if(l > 0)
			retValue.add(splitted[i].substring(1, l-1));
		}
		
		return retValue;
	}
	
	public ArrayList<String> getCategoryResults(String page){
		ArrayList<String> retValue = new ArrayList<String>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db;
		Document dom;
		
		InputSource source = new InputSource();
		source.setCharacterStream(new StringReader(page));
		
		try {
			db = dbf.newDocumentBuilder();
			dom = db.parse(source);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return retValue;
		} catch (SAXException e) {
			e.printStackTrace();
			return retValue;
		} catch (IOException e) {
			e.printStackTrace();
			return retValue;
		}
		
		
		NodeList nodes = dom.getElementsByTagName("page");
		
		Element elem;
		String name;
		for(int i=0; i < nodes.getLength(); i++){
			elem = (Element) nodes.item(i);
			name = elem.getAttribute("title");
			if(name != null)
				retValue.add(name);
		}
		
		NodeList cats = dom.getElementsByTagName("cl");
		
		for(int i=0; i < cats.getLength(); i++){
			elem = (Element) cats.item(i);
			name = elem.getAttribute("title");
			if(name != null)
				retValue.add(name);
		}
		
		return retValue;
	}
	
	public ArrayList<String> getPages(String search){
		return getPageResults(openSearch(search));
	}
	
	public ArrayList<String> getCategories(String search){
		return getCategoryResults(queryCategories(search));
	}
	
	public static void main(String[] args) {
		WikiEngine wiki = new WikiEngine();
		System.out.println(wiki.openSearch("phd thesis"));
		System.out.println(wiki.openSearch("formula one"));
		System.out.println(wiki.openSearch("asdjahsgdas"));
		System.out.println(wiki.openSearch("grand prix"));
		System.out.println(wiki.getPageResults(wiki.openSearch("phd thesis")));
		System.out.println(wiki.getPageResults(wiki.openSearch("formula one")));
		System.out.println(wiki.getPageResults(wiki.openSearch("blablabla")));
		
		System.out.println(wiki.queryCategories("phd thesis"));
		System.out.println(wiki.queryCategories("formula one"));
		System.out.println(wiki.queryCategories("asdjahsgdas"));
		System.out.println(wiki.queryCategories("grand prix"));
		
		wiki.getCategoryResults(wiki.queryCategories("phd thesis"));
		wiki.getCategoryResults(wiki.queryCategories("Formula One"));
		wiki.getCategoryResults(wiki.queryCategories("asdashdlas"));
		wiki.getCategoryResults(wiki.queryCategories("Grand Prix"));
		wiki.getCategoryResults(wiki.queryCategories("Albert Einstein"));
		
	}
}
