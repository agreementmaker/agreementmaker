package am.app.collaborationEngine;

import javax.ws.rs.Path;
import javax.xml.ws.Endpoint;

// If you get errors because unresolved imports go here:
// http://tech.amikelive.com/node-269/eclipse-quick-tip-resolving-error-the-import-javaxservlet-cannot-be-resolved/


@Path("/collaborationServer")
public class CollaborationServerImpl implements CollaborationServer {

	@Override
	public String sayHi(String text) {
		System.out.println("sayHi called");
        return "Hello " + text;
	}

	
	
	
	
	public static void main(String[] args) {
		
		
		System.out.println("Starting Server");
		CollaborationServer implementor = new CollaborationServerImpl();
		String address = "http://localhost:9000/helloWorld";
		Endpoint.publish(address, implementor);
	}





	@Override
	public String sayHello(String text, String name) {
		System.out.println("sayHello called");
		return "Hi, " + text + " " + name;
	}
}
