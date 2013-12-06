package am.extension.collaborationClient.restful;

import am.extension.collaborationClient.api.CollaborationUser;

/**
 * Implements the {@link CollaborationUser} interface for a RESTful
 * Collaboration Server.
 * 
 * @author cosmin
 * 
 */
public class RESTfulUser implements CollaborationUser {

	private final String id;
	
	public RESTfulUser(String clientID) {
		this.id = clientID;
	}
	
	@Override
	public String getID() {
		return id;
	}
}
