package am.matcher.lod.instanceMatcher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.ontology.Ontology;
import am.app.ontology.instance.datasets.SeparateFileInstanceDataset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


public class NYTArticles {
	static String sourceDataset = "C:/Users/federico/workspace/AgreementMaker/OAEI2011/NYTDatasets/organizations.rdf";	
	static String[] peopleProperties = { "nytd_per_facet", "per_facet" }; 
	static String[] orgProperties = { "org_facet" }; 
	static String[] desProperties = { "des_facet", "nytd_section_facet" }; 
	static String titleNYTD = "nytd_title";
	static String outString = "organizationsWithArticles.rdf";
	static String jsonInputFile = "jsonAnswersOrganizations.ser";
	
	
	static int articleNumber = 1;
	static OntClass articleClass;
	
	static Property orgKeywords;
	static Property peopleKeywords;
	static Property desKeywords;
	
	static String titleURI = "http://data.nytimes.com/elements/title";
	static Property title;
	
	static Property hasArticle;
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInput in = new ObjectInputStream(new FileInputStream(jsonInputFile));
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
		
		List<Statement> indStmts = SeparateFileInstanceDataset.getIndividualsStatements(ontology.getModel(), null);
		
		System.out.println(indStmts.size());
		
		String uri;
		String json;
		int count = 0;
		for (Statement stmt: indStmts) {
			count++;
			if(count % 100 == 0) System.out.println(count);
			uri = stmt.getSubject().getURI();
			System.out.println(uri);
			
			json = jsonAnswers.get(uri);
			if(json == null){
				System.err.println("Please check the jsonAnswers file");
				continue;
				//return;
			}
			
			List<String> facets = processJson(ontology.getModel(), json, uri);
			
			//System.out.println(facets);
		}
		
		System.out.println("Writing model to output...");
		ontology.getModel().write(new FileOutputStream(outString), "RDF/XML");
		System.out.println("Done");
	}

	private static List<String> processJson(OntModel model, String json, String indURI) throws JsonProcessingException, IOException {
		ObjectMapper m = new ObjectMapper();
		JsonNode object = m.readTree(json);
		List<String> facets = new ArrayList<String>();
		JsonNode results = object.get("results");
		
		//System.out.println(results.length());
				
		for (int i = 0; results.get(i) != null; i++) {
			JsonNode result = results.get(i);
			
			Individual article = model.createIndividual(NYTConstants.NYT_URI + "article" + articleNumber, articleClass);
			
			articleNumber++;
			
			
			facets = getStringsOfProperty(peopleProperties, result);
			article.addProperty(peopleKeywords, formatList(facets));

			facets = getStringsOfProperty(orgProperties, result);
			article.addProperty(orgKeywords, formatList(facets));

			facets = getStringsOfProperty(desProperties, result);
			article.addProperty(desKeywords, formatList(facets));
			
			String titleString = null;
			if (result.has(titleNYTD)) {
				titleString = result.get(titleNYTD).textValue();
			}
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
	
	public static List<String> getStringsOfProperty(String[] properties, JsonNode res) {
		JsonNode facet = null;
		List<String> facets = new ArrayList<String>();
		for (int j = 0; j < properties.length; j++) {
			facet = res.get(properties[j]);
			
			//System.out.println(properties[j] + ": " + facet);
			
			if(facet == null) continue;
			
			String keyword;
			for (int k = 0; facet.get(k) != null; k++) {
				keyword = facet.get(j).textValue().toLowerCase();
				if(!facets.contains(keyword))
					facets.add(keyword);
			}
		}
		return facets;
	}
}
