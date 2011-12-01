package am.app.collaborationEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.ws.rs.Path;
import javax.xml.ws.Endpoint;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011Matcher;
import am.batchMode.simpleBatchMode.SimpleBatchModeRunner;

// If you get errors because unresolved imports go here:
// http://tech.amikelive.com/node-269/eclipse-quick-tip-resolving-error-the-import-javaxservlet-cannot-be-resolved/


@Path("/collaborationServer")
public class CollaborationServerImpl implements CollaborationServer {

	List<String> users = new ArrayList<String>();
	List<CollaborationOntologyPair> ontologyPairs = new ArrayList<CollaborationOntologyPair>();
	OAEI2011Matcher matcher;
	
	Queue<Mapping> candidateList = new PriorityQueue<Mapping>();
	
	Map<String, UserFeedbackRecord> feedback = new HashMap<String, UserFeedbackRecord>();
	
	@Override
	public String sayHi(String text) {
		System.out.println("sayHi called");
        return "Hello " + text;
	}

	
	@Override
	public String sayHello(String text, String name) {
		System.out.println("sayHello called");
		return "Hi, " + text + " " + name;
	}

	
	
	@Override
	public int addOntologyPair( String sourceOntology, String targetOntology ) {
		CollaborationOntologyPair cop = new CollaborationOntologyPair(sourceOntology, targetOntology);
		ontologyPairs.add(cop);
		
		
		SimpleBatchModeRunner sbmr = new SimpleBatchModeRunner((File)null);
		AbstractMatcher matcher = sbmr.instantiateMatcher(null);
		OAEI2011Matcher oaei2011 = null;
		if( matcher instanceof OAEI2011Matcher ) oaei2011 = (OAEI2011Matcher) matcher;
		
		oaei2011.setSourceOntology(cop.sourceOntology);
		oaei2011.setTargetOntology(cop.targetOntology);
		
		try {
			oaei2011.match();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		matcher = oaei2011;
		
		
		return ontologyPairs.size() - 1;
	}
	
	
	@Override
	public int addUser(String username) {
		users.add(username);
		return users.size() - 1;
	}
	
	@Override
	public void recordFeedback(int ontoPair, int userID, UserFeedback fb) {
		
		CollaborationOntologyPair cop = ontologyPairs.get(ontoPair);
		
		if( !feedback.containsKey(users.get(userID)) ) {
			UserFeedbackRecord ufbr = new UserFeedbackRecord(cop.sourceOntology.getClassesList().size(), cop.targetOntology.getClassesList().size());
			ufbr.addFeedback(fb);
			feedback.put(users.get(userID), ufbr);
		} else {
			UserFeedbackRecord ufbr = feedback.get(users.get(userID));
			ufbr.addFeedback(fb);
		}
		
	}
	
	@Override
	public UserFeedback getCandidate( int ontoPair, int userID ) {
		
		
		return null;
	}
	
	
	public static void main(String[] args) {		
		System.out.println("Starting Server");
		CollaborationServer implementor = new CollaborationServerImpl();
		String address = "http://localhost:9000/helloWorld";
		Endpoint.publish(address, implementor);
		
	}
	
}
