package am.matcher.lod.instanceMatcher;

import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OldOntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;

public class NYTUtils {
	
	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OntologyDefinition def = new OntologyDefinition(true, ontoName, 
					OntologyLanguage.OWL, OntologySyntax.RDFXML);
			OldOntoTreeBuilder treeBuilder = new OldOntoTreeBuilder(def);
			treeBuilder.build();
			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	

}
