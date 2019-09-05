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

	private String id;
		
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "ClientID " + id;
	}
}
