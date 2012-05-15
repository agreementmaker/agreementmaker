package am.app.ontology.instance.queryExpansion;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import am.utility.HTTPUtility;

public class AbbreviationsService implements QueryExpansionService{
	private HashMapCache<String,String> cache;	
	
	public String serviceURL = "http://www.stands4.com/services/v1/abbr.aspx";
	//tokenid=tk1984&sortby=p&searchtype=e&term=asap
	public String tokenId = "tk1984";
	
	public String cacheFileName = "abbreviationsCache.ser";
	
	private boolean useCache = true;
	
	Logger log = Logger.getLogger(AbbreviationsService.class);
	
	public AbbreviationsService(){
		
	}
	
	public AbbreviationsService(String cacheFileName){
		this.cacheFileName = cacheFileName;
	}
	
	public String getXmlFromUrl(String url){
		String xml = null;
		boolean foundInCache = false;
		if(useCache){
			xml = cache.get(url);	
			if(xml != null)
				foundInCache = true;
		}
		if(!foundInCache){
			System.out.println(url);
			try {	xml = HTTPUtility.getPage(url);	} 
			catch (IOException e) {	
				System.err.println("Connection problem"); 
				return null;	
			}
		}
		return xml;
	}

	@Override
	public List<String> expandQuery(String query) {
		
		return null;
	}

}
