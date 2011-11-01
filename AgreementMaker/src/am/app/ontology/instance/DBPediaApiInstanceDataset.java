package am.app.ontology.instance;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hsqldb.lib.InOutUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.hp.hpl.jena.rdf.model.Statement;

import am.AMException;
import am.app.ontology.instance.endpoint.SparqlEndpoint;
import am.utility.HTTPUtility;

public class DBPediaApiInstanceDataset implements InstanceDataset{

	String endpoint = "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch";
	
	HashMap<String, String> cache;
	
	String xmlCacheFilename = "dbpediaXmlCache";
	//String rdfCacheFilename = "dbpediaRDFCache";
	
	
	SparqlEndpoint sparqlEndpoint;
	
	public DBPediaApiInstanceDataset(){
		loadCache();
		
		sparqlEndpoint = new SparqlEndpoint("http://dbpedia.org/sparql", "dbpediaSingleLocationsCache");
		
		
	}
	

	@Override
	public boolean isIterable() {
		return false;
	}

	@Override
	public List<Instance> getInstances(String type, int limit)
			throws AMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instance> getCandidateInstances(String searchTerm, String type)
			throws AMException {
		
		System.out.println("API is providing results for: searchString=" + searchTerm  + " type=" + type);
		
		List<Instance> instances = new ArrayList<Instance>();
		
		//http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryClass=place&QueryString=Bilbao
		
		try {
			searchTerm = URLEncoder.encode(searchTerm, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("Problems encoding the url");
			return instances;
		}
		
		String url = endpoint + "?QueryString=" + searchTerm;
		
		if(type != null) 
			url += "&QueryClass=" + type;
		
		String xml = null;
		
		//System.out.println("Querying: " + url);
		
		xml = cache.get(url);
		if(xml == null){
			try {
				xml = HTTPUtility.getPage(url);
			} catch (IOException e) {
				System.err.println("Problems getting the page");
				return instances;
			}
		}
		
		cache.put(url, xml);
		
		//System.out.println("xml:" + xml);
		
		DocumentBuilderFactory dbf =
	            DocumentBuilderFactory.newInstance();
			
				
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);
			
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader reader = new StringReader(xml);
			InputSource inputSource = new InputSource( reader );
			doc = db.parse(inputSource);
		} catch (Exception e) {
			e.printStackTrace();
			return instances;
		}
		
		NodeList results = doc.getElementsByTagName("Result");
		NodeList children;
		Node result;
		for (int i = 0; i < results.getLength(); i++) {
			result = results.item(i);
			children = result.getChildNodes();

			Instance instance = new Instance(null, null);
			
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);
				
				String name = child.getNodeName();
				
				Element el;
				if(name.equals("Label")){
					el = (Element)child;
					instance.setProperty("label", el.getTextContent());
				}
				else if(name.equals("URI")){
					el = (Element)child;
					instance.setURI(el.getTextContent());
				}
				else if(name.equals("Description")){
					el = (Element)child;
					//instance.setProperty("comment", el.getTextContent());
				}
				else if(name.equals("Classes")){
					NodeList classes = child.getChildNodes();
					
					for (int k = 0; k < classes.getLength(); k++) {
						//classes.
					}
					
				}
			}
			
			instances.add(instance);
			//System.out.println(result);
		}
		
		//System.out.println(results);
		
//		for (int i = 0; i < instances.size(); i++) {
//			List<Statement> stmts = sparqlEndpoint.singleEntityQuery(instances.get(i).getUri());
//			instances.get(i).setStatements(stmts);
//		}
		
		
		return instances;
	}

	@Override
	public List<Instance> getInstances() throws AMException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instance getInstance(String uri) throws AMException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void loadCache() {
		ObjectInput in;
		try {
			in = new ObjectInputStream(new FileInputStream(xmlCacheFilename));
			Object input = in.readObject();
			cache = (HashMap<String, String>) input;	
		} catch (Exception e) {
			e.printStackTrace();
			cache = new HashMap<String, String>();
		}
	}
	
	public void persistCache() throws FileNotFoundException, IOException{
		 System.out.println("Writing to file...");
		 ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(xmlCacheFilename));
		 out.writeObject(cache);
		 out.close();
		 System.out.println("Done");
		 sparqlEndpoint.persistCache();
	}

}
