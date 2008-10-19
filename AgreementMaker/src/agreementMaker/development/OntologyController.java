package agreementMaker.development;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import agreementMaker.mappingEngine.DefnMapping;
import agreementMaker.userInterface.Canvas;
import agreementMaker.userInterface.vertex.Vertex;

/**
 * This class has been developed to modularize the system
 * It will contains methods which could be put into canvas but instead we put them here to start separating control from view
 * The UI will contain the canvas and the ontologyController.
 * These methods manage ontologies and their tree structiure, not visualization on canvas panel
 * Methods will be working on trees but they should let other classes to acces them. So there should not be a getLocalRoot() method in this class 
 * but all methods in this class will use it probably.
 */
public class OntologyController {
	
	private Canvas canvas;
	
	public OntologyController(Canvas c) {
		canvas = c;
	}
	

	public ArrayList<MatchingPair> getDefnMatchingsList() {
		ArrayList<MatchingPair> matchList = new ArrayList<MatchingPair>();
		Vertex sourceRoot = canvas.getGlobalTreeRoot();
		
		Vertex sourcenode;
		Vertex targetnode;
		DefnMapping dmsource;	
		MatchingPair mp;
		int myLines;
		//preorderEnum is the same order visualized on the AM display.
		for (Enumeration e = sourceRoot.preorderEnumeration(); e.hasMoreElements(); ) {
			sourcenode = (Vertex) e.nextElement();
			dmsource = sourcenode.getDefnMapping();
			if(dmsource!=null && dmsource.getLocalVertices() != null) {//if this source node has some mappings
				Iterator it = dmsource.getLocalVertices().iterator();
				myLines = 0;
				while(it.hasNext() && myLines < canvas.getDefnOptions().numRel) {
					targetnode = (Vertex) it.next();
					mp = new MatchingPair(sourcenode.getName(),targetnode.getName());
					//System.out.println(mp.getTabString()+" "+dmsource.getMappingValue1(targetnode));
					matchList.add(mp);
					myLines++;
				}
				//DEBUGGING OF OTHER RELATIONS FOUND BUT AFTER MINIMUMNUMBER OF REL
				/*
				while(it.hasNext()) {
					targetnode = (Vertex) it.next();
					//System.out.println(sourcenode.getName()+" "+v.getName());
					mp = new MatchingPair(sourcenode.getName(),targetnode.getName());
					System.out.println(mp.getTabString()+" "+dmsource.getMappingValue1(targetnode));
				}
				*/ 
				//END OF DEBUGGING
			}
		}
		return matchList;
	}

}
