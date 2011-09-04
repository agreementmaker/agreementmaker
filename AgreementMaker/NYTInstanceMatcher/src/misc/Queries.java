package misc;

import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class Queries {
	
	public static List<Statement> getIndividualsStatements(OntModel model, String type){
		Property property = model.getProperty(type);
		if(property == null) model.createProperty(type);
		
		List<Statement> list = model.listStatements(null, RDF.type, property).toList();
		
		return list;
	}
	
}
