package am.app.collaborationEngine;

import javax.jws.WebParam;
import javax.jws.WebService;


@WebService
public interface CollaborationServer {

	
	String sayHi(@WebParam(name="text") String text);
	
	
	String sayHello(@WebParam(name="t") String text, @WebParam(name="n") String name);
	
	
	
}
