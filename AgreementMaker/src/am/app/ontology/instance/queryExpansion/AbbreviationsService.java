package am.app.ontology.instance.queryExpansion;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import am.utility.HTTPUtility;

public class AbbreviationsService implements QueryExpansionService{
	private HashMapCache<String,String> cache;	
	
	public String serviceURL = "http://www.abbreviations.com/services/v2/abbr.php";
	//tokenid=tk1984&sortby=p&searchtype=e&term=asap
	public String tokenId = "tk1984";
	
	public static String defaultCacheFileName = "abbreviationsCache.ser";
	public String cacheFileName;
	
	private boolean useCache = true;
	
	Logger log = Logger.getLogger(AbbreviationsService.class);
	
	int misses = 0;
	int missesLimit = 30;	
	
	public AbbreviationsService(){}
	
	public AbbreviationsService(String cacheFileName){
		this.cacheFileName = cacheFileName;
		cache = new HashMapCache<String, String>(cacheFileName);
	}
	
	//http://www.abbreviations.com/services/v2/abbr.php?uid=1002&tokenid=tk1984&term=asap
	//http://www.abbreviations.com/services/v2/abbr.php?uid=1002&tokenid=tk1984&term=lol
	//http://www.abbreviations.com/services/v2/abbr.php?uid=1002&tokenId=tk1984&term=lol
	
	public String abbreviationsQueryURL(String searchTerm) throws Exception {
		searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
		String query = "?";
		query += "uid=1002";
		query += "&tokenid=" + tokenId;
		query += "&term=" + searchTerm;
		//query += "&categoryid=General";
		return serviceURL + query;
	}
	
	public String getXmlFromUrl(String url){
		String xml = null;
		boolean foundInCache = false;
		
		if(useCache && cache != null){
			xml = cache.get(url);	
			if(xml != null)
				foundInCache = true;
			else misses++;
		}
		if(!foundInCache){
			//System.out.println(url);
			try {	xml = HTTPUtility.getPage(url);	} 
			catch (IOException e) {	
				System.err.println("Connection problem"); 
				return null;	
			}
			if(useCache && misses == missesLimit){
				cache.persist();
				misses = 0;
			}
		}
		return xml;
	}

	@Override
	public List<String> expandQuery(String query, int limit) {
		List<String> expansions = new ArrayList<String>();
		
		Set<String> singleExpansions = new LinkedHashSet<String>();
		
		String url = null;
		try {
			url = abbreviationsQueryURL(query);
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("Problems in generating the URL");
			return expansions;
		}

		String xml = getXmlFromUrl(url);
			
		//System.out.println(xml);
		
		int count = 0;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);

		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		NodeList terms = doc.getElementsByTagName("result");
		
		for (int i = 0; i < terms.getLength() && (limit == 0 || count < limit); i++) {
			Node node = terms.item(i);
			
			for (int j = 0; j < node.getChildNodes().getLength(); j++) {
				Node child = node.getChildNodes().item(j);
								
				if(child.getNodeName().equals("term")){
					log.debug(child.getTextContent());
				}
				else if(child.getNodeName().equals("definition")){
					log.debug(child.getTextContent());
					expansions.add(child.getTextContent());
					singleExpansions.add(child.getTextContent());
				}
				else if(child.getNodeName().equals("category")){
					log.debug(child.getTextContent());
				}	
			}
			count++;
		}
		
		List<String> exp = new ArrayList<String>();
		for (String string : singleExpansions) {
			exp.add(string);
		}
		
		return exp;
	}
	
	public static void main(String[] args) throws Exception {
		//Logger.getLogger(AbbreviationsService.class).setLevel(Level.DEBUG);
				
		AbbreviationsService as = new AbbreviationsService(defaultCacheFileName);
		//String url = as.abbreviationsQueryURL("lol");
		List<String> exp = as.expandQuery("aaj");
		System.out.println(exp);
		exp = as.expandQuery("mtr");
		System.out.println(exp);
		exp = as.expandQuery("cia");
		System.out.println(exp);
	}

	@Override
	public List<String> expandQuery(String query) {
		return expandQuery(query, 5);
	}
}
