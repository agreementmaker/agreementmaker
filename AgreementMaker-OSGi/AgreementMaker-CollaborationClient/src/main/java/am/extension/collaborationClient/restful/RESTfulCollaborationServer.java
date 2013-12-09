package am.extension.collaborationClient.restful;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.extension.collaborationClient.api.CollaborationAPI;
import am.extension.collaborationClient.api.CollaborationCandidateMapping;
import am.extension.collaborationClient.api.CollaborationFeedback;
import am.extension.collaborationClient.api.CollaborationTask;
import am.extension.collaborationClient.api.CollaborationUser;
import am.extension.*;

public class RESTfulCollaborationServer implements CollaborationAPI {

	private static final String SEP = "/";
	
	private static final String REGISTER = "register";
	
	private static final String LISTTASKS = "listTasks";
	
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
			InputStream s = connection.getInputStream();
			/*StringBuilder content = new StringBuilder();
			int i = -1;
			while( (i = s.read()) != -1 ) content.append((char)i);
			System.out.println("Read from server:\n" + content.toString());*/
			
			return mapper.readValue(s, RESTfulUser.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<CollaborationTask> getTaskList() {
		String queryURI = baseURI + SEP + LISTTASKS;
		
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
			InputStream s = connection.getInputStream();
			/*StringBuilder content = new StringBuilder();
			int i = -1;
			while( (i = s.read()) != -1 ) content.append((char)i);
			System.out.println("Read from server:\n" + content.toString());*/
			
			return mapper.readValue(s, new TypeReference<List<RESTfulTask>>() {});
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public CollaborationCandidateMapping getCandidateMapping(
			CollaborationUser client) {
		return null;
	}

	@Override
	public void putFeedback(CollaborationUser client,
			CollaborationFeedback feedback) {
		// TODO Auto-generated method stub
		
	}

	
	public static File downloadFile(String url, String prefix, String suffix) {
		URL uri;
		try {
			uri = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		URLConnection connection;
		try {
			connection = uri.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		InputStream s = null;
		OutputStream o = null;
		try {
			s = connection.getInputStream();
			
			File tempFile = File.createTempFile(prefix, suffix);
			tempFile.deleteOnExit();
			o = new FileOutputStream(tempFile);
			
			int read = 0;
			byte[] bytes = new byte[1024];
	 
			while ((read = s.read(bytes)) != -1) {
				o.write(bytes, 0, read);
			}
			
			return tempFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (o != null) {
				try {
					o.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	 
			}
		}
	}
	
	@Override
	public Alignment<Mapping> getReferenceAlignment(String referenceAlignmentURL) {
		File refFile = downloadFile(referenceAlignmentURL, "ref", ".rdf");
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters p = new ReferenceAlignmentParameters();
		p.fileName = refFile.getAbsolutePath();
		p.format = ReferenceAlignmentMatcher.OAEI;
		
		matcher.setSourceOntology(Core.getInstance().getSourceOntology());
		matcher.setTargetOntology(Core.getInstance().getTargetOntology());
		
		matcher.setParameters(p);
		
		try {
			matcher.match();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return matcher.getAlignment();
	}
	
	@Override
	public OntologyDefinition getOntologyDefinition(String ontologyURL) {
		File ontFile = downloadFile(ontologyURL,"ont",".owl");
		OntologyDefinition o = new OntologyDefinition(true, ontFile.getAbsolutePath(), OntologyLanguage.OWL, OntologySyntax.RDFXML);
		return o;
	}
	
}
