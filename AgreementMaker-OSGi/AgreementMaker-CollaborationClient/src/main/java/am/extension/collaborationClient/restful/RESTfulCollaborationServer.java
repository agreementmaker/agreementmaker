package am.extension.collaborationClient.restful;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import am.extension.collaborationClient.api.CollaborationAPI;
import am.extension.collaborationClient.api.CollaborationCandidateMapping;
import am.extension.collaborationClient.api.CollaborationFeedback;
import am.extension.collaborationClient.api.CollaborationTask;
import am.extension.collaborationClient.api.CollaborationUser;

public class RESTfulCollaborationServer implements CollaborationAPI {

	private static final String SEP = "/";
	
	private static final String REGISTER = "register";
	
	private String baseURI;
	
	public static final ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * @param baseURI The base URI of the server.
	 */
	public RESTfulCollaborationServer(String baseURI) {
		this.baseURI = baseURI;
	}
	
	@Override
	public CollaborationUser register() {
		String queryURI = baseURI + SEP + REGISTER;
		
		URL uri;
		try {
			uri = new URL(queryURI);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		URLConnection connection;
		try {
			connection = uri.openConnection();
			connection.setRequestProperty("Accept", "application/json");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			return mapper.readValue(connection.getInputStream(), RESTfulUser.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<CollaborationTask> getTaskList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CollaborationCandidateMapping getCandidateMapping(
			CollaborationUser client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putFeedback(CollaborationUser client,
			CollaborationFeedback feedback) {
		// TODO Auto-generated method stub
		
	}

	
	
}
