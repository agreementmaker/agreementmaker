package misc;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class Queries {
	
	public static List<Statement> getIndividualsStatements(OntModel model, String type){
		Property property = model.getProperty(type);
		if(property == null) model.createProperty(type);
		
		List<Statement> list = model.listStatements(null, RDF.type, property).toList();
		
		return list;
	}
	
	public static String getPropertyValue(OntModel model, String instanceURI, String propertyURI){
		Property property = model.getProperty(propertyURI);
		if(property == null) model.createProperty(propertyURI);
		Resource instance = model.createResource(instanceURI);
		List<Statement> list = model.listStatements(instance, property, (RDFNode)null).toList();
		
		if(list.size() == 1){
			RDFNode object = list.get(0).getObject();
			if(object.isLiteral()) return object.asLiteral().getString();
			else if(object.isResource()) return object.asResource().getURI();	
		}	
		return "";
	}
	
	public static String executeQuery(String endpoint, String query) throws IOException{
		query = URLEncoder.encode(query, "UTF-8"); 
		String parameters = "?output=xml&query=" + query;
		String urlStr = endpoint + parameters;
		System.out.println(urlStr);
		return Utilities.getPage(urlStr);
	}
	
}
