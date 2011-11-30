package am.app.collaborationEngine;

public class CollaborationServerExperiment {

	
	
	public static void main(String[] args) {
		
		
		
		CollaborationServer cs = new CollaborationServerImpl();
		
		cs.addOntologyPair("/home/cosmin/Documents/eclipse/AgreementMaker_main_workspace/Ontologies/OAEI/2011/anatomy/mouse.owl", 
				           "/home/cosmin/Documents/eclipse/AgreementMaker_main_workspace/Ontologies/OAEI/2011/anatomy/human.owl");
		
		
		
		
		
	}
	
	
	
}
