package am.app.mappingEngine.instanceMatcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openjena.atlas.json.JsonObject;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import am.app.ontology.Ontology;
import am.app.ontology.instance.InstanceDataset;


public class NYTArticles {
	static String sourceDataset = "C:/Users/federico/workspace/AgreementMaker/OAEI2011/NYTDatasets/people.rdf";	
	static String[] peopleProperties = { "nytd_per_facet", "per_facet" }; 
	static String[] orgProperties = { "org_facet" }; 
	static String[] desProperties = { "des_facet", "nytd_section_facet" }; 
	static String titleNYTD = "nytd_title";
	static String outString = "peopleWithArticles.rdf";
	
	
	static int articleNumber = 1;
	static OntClass articleClass;
	
	static Property orgKeywords;
	static Property peopleKeywords;
	static Property desKeywords;
	
	static String titleURI = "http://data.nytimes.com/elements/title";
	static Property title;
	
	static Property hasArticle;
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInput in = new ObjectInputStream(new FileInputStream("jsonAnswers2.ser"));
	    Object input = in.readObject();
		
	    HashMap<String, String> jsonAnswers = (HashMap<String, String>) input;
		
		System.out.println(jsonAnswers.keySet());
		System.out.println(jsonAnswers.keySet().size());
		
		System.out.println("Opening ontology...");
		Ontology ontology = NYTUtils.openOntology(sourceDataset);
		System.out.println("Done");		
		
		articleClass = ontology.getModel().createClass(NYTConstants.NYT_URI + "Article");
		
		orgKeywords = ontology.getModel().createProperty(NYTConstants.orgKeywordsURI);
		peopleKeywords = ontology.getModel().createProperty(NYTConstants.peopleKeywordsURI);
		desKeywords = ontology.getModel().createProperty(NYTConstants.desKeywordsURI);
		
		hasArticle = ontology.getModel().createProperty(NYTConstants.hasArticleURI);
		
		title = ontology.getModel().createProperty(titleURI);
		
		List<Statement> indStmts = InstanceDataset.getIndividualsStatements(ontology.getModel(), null);
		
		System.out.println(indStmts.size());
		
		String uri;
		String json;
		for (Statement stmt: indStmts) {
			uri = stmt.getSubject().getURI();
			System.out.println(uri);
			
			json = jsonAnswers.get(uri);
			if(json == null){
				System.err.println("Please check the jsonAnswers file");
				continue;
				//return;
			}
			
			List<String> facets = null;
			
			try {
				facets = processJson(ontology.getModel(), json, uri);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			//System.out.println(facets);
			
		}
		
		ontology.getModel().write(new FileOutputStream(outString), "RDF/XML");
	}

	private static List<String> processJson(OntModel model, String json, String indURI) throws JSONException {
		JSONObject object = new JSONObject(json);
		List<String> facets = new ArrayList<String>();
		JSONArray results = (JSONArray) object.get("results");
		
		//System.out.println(results.length());
				
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = (JSONObject) results.get(i);
			
			Individual article = model.createIndividual(NYTConstants.NYT_URI + "article" + articleNumber, articleClass);
			
			articleNumber++;
			
			
			facets = getStringsOfProperty(peopleProperties, result);
			article.addProperty(peopleKeywords, formatList(facets));

			facets = getStringsOfProperty(orgProperties, result);
			article.addProperty(orgKeywords, formatList(facets));

			facets = getStringsOfProperty(desProperties, result);
			article.addProperty(desKeywords, formatList(facets));
			
			String titleString = result.optString(titleNYTD);
			if(titleString != null) article.addProperty(title, titleString);
			
			Resource res = model.getResource(indURI);
			//System.out.println("res:" + res);
			Statement stmt = model.createStatement(res, hasArticle, article);
			model.add(stmt);
		} 
		
		return facets;
	}
	
	public static String formatList(List<String> list){
		String ret = "";
		for (int i = 0; i < list.size(); i++) {
			ret += list.get(i);
			if(i != list.size() - 1) ret += "|";
		}
		return ret;
	}
	
	public static List<String> getStringsOfProperty(String[] properties, JSONObject res) throws JSONException{
		JSONArray facet = null;
		List<String> facets = new ArrayList<String>();
		for (int j = 0; j < properties.length; j++) {
			facet = (JSONArray) res.optJSONArray(properties[j]);
			
			//System.out.println(properties[j] + ": " + facet);
			
			if(facet == null) continue;
			
			String keyword;
			for (int k = 0; k < facet.length(); k++) {
				keyword = facet.getString(k).toLowerCase();
				if(!facets.contains(keyword))
					facets.add(keyword);
			}
		}
		return facets;
	}
}
