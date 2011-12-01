package am.app.collaborationEngine;

import java.util.Queue;

import javax.jws.WebParam;
import javax.jws.WebService;

import am.app.mappingEngine.Mapping;


@WebService
public interface CollaborationServer {

	
	public String sayHi(@WebParam(name="text") String text);
	public String sayHello(@WebParam(name="t") String text, @WebParam(name="n") String name);
	public int addUser(String username);
	public void recordFeedback(int ontoPair, int userID, UserFeedback fb);
	int addOntologyPair(String sourceOntology, String targetOntology);
	UserFeedback getCandidate(int ontoPair, int userID);
	Queue<Mapping> getRankingQueue(int ontoPair);
	CollaborationOntologyPair getPair(int ontoPair);
	
	
	
}
