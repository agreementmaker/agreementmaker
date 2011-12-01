package am.app.collaborationEngine;

public class CollaborationServerExperiment {

	
	
	public static void main(String[] args) {
		
		int ontoPair;
		int users = 5;
		
		
		String prefix = "/home/cosmin/Documents/Eclipse/ADVIS-Main/Ontologies/OAEI/2011/anatomy/";
		//String prefix = "/home/cosmin/Documents/eclipse/AgreementMaker_main_workspace/Ontologies/OAEI/2011/anatomy/";
		
		CollaborationServer cs = new CollaborationServerImpl();
		
		ontoPair = cs.addOntologyPair(prefix + "mouse.owl", prefix + "human.owl");
		
		cs.addUser("user1");
		
		
		
		
		
	}
	
	
	
}
