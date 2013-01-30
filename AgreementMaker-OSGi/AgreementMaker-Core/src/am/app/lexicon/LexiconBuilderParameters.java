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
	
	public List<String> synonymPropertySeeds;
	public List<String> definitionPropertySeeds;
	
	public void detectStandardProperties() {
		List<String> synonymPropertySeeds = new ArrayList<String>();
		synonymPropertySeeds.add("label");
		synonymPropertySeeds.add("synonym");
		
		List<String> definitionPropertySeeds = new ArrayList<String>();
		definitionPropertySeeds.add("defini");
		//definitionProperties.add("defined");
		//definitionProperties.add("comment");
		
		detectStandardProperties(synonymPropertySeeds, definitionPropertySeeds);
	}

	public void detectStandardProperties(List<String> synonymPropertySeeds, List<String> definitionPropertySeeds) {
		List<Property> sourceAnnotationList = getAnnotationList(sourceOntology);
		sourceSynonyms    = detectStandardSynonymProperties( sourceAnnotationList, synonymPropertySeeds);
		sourceDefinitions = detectStandardDefinitionProperties( sourceAnnotationList, definitionPropertySeeds);
		
		List<Property> targetAnnotationList = getAnnotationList(targetOntology);
		targetSynonyms    = detectStandardSynonymProperties( targetAnnotationList, synonymPropertySeeds);
		targetDefinitions = detectStandardDefinitionProperties( targetAnnotationList, definitionPropertySeeds);
	}
	
	
	public List<Property> getAnnotationList(Ontology ont) {
		List<Property> annotationList = new ArrayList<Property>();
		
		for( Node classNode : ont.getClassesList() ) 
			annotationList.addAll(ManualOntologyProfiler.createClassAnnotationsList(classNode));
		
		for( Node propertyNode : ont.getPropertiesList() ) 
			annotationList.addAll(ManualOntologyProfiler.createPropertyAnnotationsList(propertyNode));
		
		return annotationList;
	}
	
	/**
	 * Automatically try to detect standard synonym annotations, given an ontology. 
	 * 
	 * Right now, this is a simple string checking.  In the future, try to figure out
	 * a better way.
	 */
	public List<Property> detectStandardSynonymProperties(List<Property> sourceAnnotationList, List<String> synonymPropertySeeds) {

		List<Property> synonymAnnotations = new ArrayList<Property>();
		for( Property property : sourceAnnotationList ) {
			for( String synonym : synonymPropertySeeds ) {
				if( property.getLocalName().equalsIgnoreCase(synonym) || 
					property.getLocalName().toLowerCase().contains(synonym) ) {
					if( !synonymAnnotations.contains(property) ) synonymAnnotations.add(property);
				}
			}
		}
		
		return synonymAnnotations;
	}
	
	/**
	 * Automatically try to detect standard definition annotations, given an ontology. 
	 * 
	 * Right now, this is a simple string checking.  In the future, try to figure out
	 * a better way.
	 */
	public List<Property> detectStandardDefinitionProperties(List<Property> sourceAnnotationList, List<String> definitionPropertySeeds) {
		
		List<Property> definitionAnnotations = new ArrayList<Property>();
		for( Property property : sourceAnnotationList ) {
			for( String definition : definitionPropertySeeds ) {
				if( property.getLocalName().toLowerCase().contains(definition) ) {
					if( !definitionAnnotations.contains(property) ) definitionAnnotations.add(property);
				}
			}
		}
		
		return definitionAnnotations;
	}
	
}
