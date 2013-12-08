package am.extension.collaborationClient.api;

import java.util.List;

import am.app.ontology.ontologyParser.OntologyDefinition;

/**
 * This interface reflects the Collaboration Server API.
 * 
 * @author cosmin
 *
 */
public interface CollaborationAPI {
	
	/**
	 * Register this client with the server.
	 * 
	 * TODO: Decide if the registration should persist over multiple sessions.
	 * 
	 * @return The CollaborationClient (which contains a unique id) will be used for all client API calls. 
	 */
	public CollaborationUser register();
	
	//public void unregister(CollaborationClient clientID);
	
	/**
	 * @return A list of matching tasks available on the server.
	 */
	public List<CollaborationTask> getTaskList();

	/**
	 * @param clientID
	 * @return A candidate mapping for the current client.
	 */
	public CollaborationCandidateMapping getCandidateMapping(CollaborationUser client);
	
	/**
	 * @param client
	 * @param feedback
	 */
	public void putFeedback(CollaborationUser client, CollaborationFeedback feedback);
	
	public OntologyDefinition getOntologyDefinition(String ontologyURL);
	
}
