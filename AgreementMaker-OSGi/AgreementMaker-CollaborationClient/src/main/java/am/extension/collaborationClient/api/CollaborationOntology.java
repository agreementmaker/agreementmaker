package am.extension.collaborationClient.api;

import am.app.ontology.ontologyParser.OntologyDefinition;

public interface CollaborationOntology {

	public String getID();
	
	/**
	 * Download this ontology from the server to a file in the temporary
	 * directory on the computer and create a corresponding ontology definition.
	 * 
	 * @return The OntologyDefinition object for the downloaded ontology.
	 */
	public OntologyDefinition getDefinition();
}
