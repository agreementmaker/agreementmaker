package am.extension.collaborationClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.ws.rs.Path;
import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.evaluation.disagreement.variance.VarianceDisagreementComparator;
import am.extension.batchmode.simpleBatchMode.SimpleBatchModeRunner;
import am.extension.collaborationClient.api.CollaborationCandidateMapping;
import am.extension.collaborationClient.api.CollaborationFeedback;
import am.extension.collaborationClient.api.CollaborationTask;
import am.extension.collaborationClient.api.CollaborationUser;
import am.extension.collaborationClient.api.CollaborationAPI;
import am.matcher.oaei.oaei2011.OAEI2011Matcher;
import am.matcher.oaei.oaei2011.OAEI2011Matcher.SubMatcherID;

// If you get errors because unresolved imports go here:
// http://tech.amikelive.com/node-269/eclipse-quick-tip-resolving-error-the-import-javaxservlet-cannot-be-resolved/

/**
 * This class is only here for HISTORICAL purposes. It may be removed at a later
 * time. It should not be considered working code for any function.
 * 
 * @author cosmin
 * 
 * @deprecated This class is here only for historical documentation.  It will be removed in the future.
 */
@Deprecated
@Path("/collaborationServer")
public class CollaborationServerImpl implements CollaborationAPI {
	
	private static final Logger sLog = Logger.getLogger(CollaborationServerImpl.class);

	List<String> users = new ArrayList<String>();
	List<CollaborationOntologyPair> ontologyPairs = new ArrayList<CollaborationOntologyPair>();
	List<OAEI2011Matcher> matchers = new ArrayList<OAEI2011Matcher>();
	
	Map<String, UserFeedbackRecord> feedback = new HashMap<String, UserFeedbackRecord>();
	
	Queue<Mapping> candidateRanking;
	
//	@Override
	public String sayHi(String text) {
		System.out.println("sayHi called");
        return "Hello " + text;
	}

	
//	@Override
	public String sayHello(String text, String name) {
		System.out.println("sayHello called");
		return "Hi, " + text + " " + name;
	}

	
	
//	@Override
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
			sLog.error(e, e);
		}
		
		matchers.add(oaei2011);
		
		return ontologyPairs.size() - 1;
	}
	
	
//	@Override
	public int addUser(String username) {
		users.add(username);
		return users.size() - 1;
	}
	
//	@Override
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
	
//	@Override
	public Queue<Mapping> getRankingQueue(int ontoPair) {
		
		OAEI2011Matcher matcher = matchers.get(ontoPair);

		List<AbstractMatcher> matchersToConsider = new ArrayList<AbstractMatcher>();
		
		matchersToConsider.add(matcher.getSubMatcherByID(SubMatcherID.PSM));
		matchersToConsider.add(matcher.getSubMatcherByID(SubMatcherID.VMM));
		matchersToConsider.add(matcher.getSubMatcherByID(SubMatcherID.LSM));
		matchersToConsider.add(matcher.getSubMatcherByID(SubMatcherID.MM));
		
		if(candidateRanking == null ) {
			
			
			SimilarityMatrix classesMatrix = matcher.getClassesMatrix();
			
			candidateRanking = new PriorityQueue<Mapping>( classesMatrix.getRows() * classesMatrix.getColumns(), 
					new VarianceDisagreementComparator(matchersToConsider) );
			
			for(int i = 0; i < classesMatrix.getRows(); i++ ) {
				for( int j = 0; j < classesMatrix.getColumns(); j++ ) {
					candidateRanking.add( classesMatrix.get(i, j) );
				}
			}
			
		}
		
		return candidateRanking;
	}
	
//	@Override
	public UserFeedback getCandidate( int ontoPair, int userID ) {
		
		return null;
	}
	
	
//	@Override
	public CollaborationOntologyPair getPair( int ontoPair ) {
		return ontologyPairs.get(ontoPair);
	}
	
	
	public static void main(String[] args) {		
		System.out.println("Starting Server");
		CollaborationAPI implementor = new CollaborationServerImpl();
		String address = "http://localhost:9000/helloWorld";
		Endpoint.publish(address, implementor);
		
	}


	/* These are the calls of the new API. They have not been implemented. */
	@Override public CollaborationUser register() { return null; }


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


	@Override
	public OntologyDefinition getOntologyDefinition(String ontologyURL) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Alignment<Mapping> getReferenceAlignment(String referenceURL) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
