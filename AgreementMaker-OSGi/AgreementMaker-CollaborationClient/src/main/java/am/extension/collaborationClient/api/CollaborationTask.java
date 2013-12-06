package am.extension.collaborationClient.api;

public interface CollaborationTask {

	/**
	 * @return The unique id of this task on the server.
	 */
	public String getID();
	
	/**
	 * @return The human readable name of this task.
	 */
	public String getName();
	
	public CollaborationOntology getSourceOntology();
	
	public CollaborationOntology getTargetOntology();
}
