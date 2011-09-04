package misc;

import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.RDF;

public class Queries {
	
	public static Vector<Individual> getIndividuals(OntModel model, String type){
		Property property = model.getProperty(NYTConstants.SKOS_PREFLABEL);
		if(property == null) model.createProperty(NYTConstants.SKOS_PREFLABEL);
		
		model.listStatements(null, RDF.type, property);
		
		
		
		return null;
	}
	
}
