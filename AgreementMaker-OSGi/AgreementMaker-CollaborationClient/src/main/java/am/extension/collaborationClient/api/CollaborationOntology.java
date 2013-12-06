package am.extension.collaborationClient.api;

import java.io.File;

public interface CollaborationOntology {

	public String getID();
	
	/**
	 * Download this ontology from the server to a file in the temporary
	 * directory on the computer.
	 * 
	 * @return The java.io.File object for the downloaded ontology.
	 */
	public File getFile();
}
