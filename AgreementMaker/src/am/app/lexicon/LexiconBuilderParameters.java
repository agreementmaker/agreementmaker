package am.app.lexicon;

import java.util.ArrayList;
import java.util.List;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;

import com.hp.hpl.jena.rdf.model.Property;

public class LexiconBuilderParameters {

	public Ontology sourceOntology;
	public Ontology targetOntology;
	
	public boolean sourceUseLocalname;
	public boolean targetUseLocalname;
	public boolean sourceUseSCSLexicon;
	public boolean targetUseSCSLexicon;
	
	public List<Property> sourceSynonyms;
	public List<Property> sourceDefinitions;
	
	public List<Property> targetSynonyms;
	public List<Property> targetDefinitions;
	
	/**
	 * Automatically try to detect standard synonym and definition annotations, given an ontology. 
	 * 
	 * Right now, this is a simple string checking.  In the future, try to figure out
	 * a better way.
	 */
	public void detectStandardProperties( Ontology ont ) {

		// look for a label property (we consider this a synonym)
		
		List<Property> annotationList = new ArrayList<Property>();
		for( Node classNode : ont.getClassesList() ) ManualOntologyProfiler.createClassAnnotationsList(annotationList, classNode);
		for( Node propertyNode : ont.getPropertiesList() ) ManualOntologyProfiler.createPropertyAnnotationsList(annotationList, propertyNode);
		
		List<Property> synonymAnnotations = new ArrayList<Property>();
		for( Property property : annotationList ) {
			if( property.getLocalName().equalsIgnoreCase("label") || 
				property.getLocalName().toLowerCase().contains("synonym") ) {
				if( !synonymAnnotations.contains(property) ) synonymAnnotations.add(property);
			}
		}
		
		List<Property> definitionAnnotations = new ArrayList<Property>();
		for( Property property : annotationList ) {
			if( property.getLocalName().toLowerCase().contains("defini") ) {
				if( !definitionAnnotations.contains(property) ) definitionAnnotations.add(property);
			}
		}
		if( definitionAnnotations.isEmpty() ) {
			// assume comment is a definition
			for( Property property : annotationList ) {
				if( property.getLocalName().equalsIgnoreCase("comment") ) {
					if( !definitionAnnotations.contains(property) ) definitionAnnotations.add(property);
				}
			}
		}
		
		if( ont.isSource() ) {
			sourceSynonyms = synonymAnnotations;
			sourceDefinitions = definitionAnnotations;
		} else {
			targetSynonyms = synonymAnnotations;
			targetDefinitions = definitionAnnotations;
		}
				
	}
	
}
