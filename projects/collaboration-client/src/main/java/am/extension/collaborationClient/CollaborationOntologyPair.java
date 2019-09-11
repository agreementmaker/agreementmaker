package am.extension.collaborationClient;

import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class CollaborationOntologyPair {

	Ontology sourceOntology;
	Ontology targetOntology;
	
	public CollaborationOntologyPair( String sourceFile, String targetFile ) {
		sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceFile);
		targetOntology = OntoTreeBuilder.loadOWLOntology(targetFile);
	}
	
}
