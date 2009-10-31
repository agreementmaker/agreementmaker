package am.app.feedback.measures;

import java.util.ArrayList;

import am.app.Core;
import am.app.feedback.CandidateConcept;
import am.app.feedback.ConceptList;
import am.app.feedback.InitialMatchers;
import java.util.HashMap;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.userInterface.vertex.Vertex;


public class FamilialSimilarity extends RelevanceMeasure {

	
	
	CandidateConcept.ontology whichOntology;
	alignType whichType;
	
	InitialMatchers im;
	
	private double[][] similarityRepository;

	
	public FamilialSimilarity() {
		super();
	}
	
	public FamilialSimilarity(double th) {
		super(th);
	}


	public void calculateRelevances() {
		
		im = new InitialMatchers();
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		whichOntology = CandidateConcept.ontology.source;
		
		
		
		
		// source classes
		whichType     = alignType.aligningClasses;
		try {
			visitNode( sourceOntology.getClassesTree() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// source properties
		whichType     = alignType.aligningProperties;
		try {
			visitNode( sourceOntology.getPropertiesTree() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		whichOntology = CandidateConcept.ontology.target;
		
		// target classes
		whichType     = alignType.aligningClasses;
		try {
			visitNode( targetOntology.getClassesTree() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// target properties
		whichType     = alignType.aligningProperties;
		try {
			visitNode( targetOntology.getPropertiesTree() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	// makes a list of the children and compares each child to every other
	protected void visitNode( Vertex concept ) throws Exception {
		
		ArrayList<Vertex> childrenList = new ArrayList<Vertex>();
		int numChildren = concept.getChildCount();
		
		for( int i = 0; i < numChildren; i++ ) {
			childrenList.add((Vertex) concept.getChildAt(i));
		}
		
		if( childrenList.size() > 1 ) {
			// two or more children
			for( int i = 0; i < childrenList.size(); i++ ) {
				int sim = simAboveThreshold( childrenList, i);
				if( sim > 0 ) {
					candidateList.add( new CandidateConcept( childrenList.get(i).getNode(), sim, whichOntology, whichType ));
				}
			}
		}
		
		// visit the children
		for( int i = 0; i < childrenList.size(); i++ ) {
			visitNode( childrenList.get(i));
		}
		
		
	}
	
	
	// compares each child to every other using the initial matchers, and returns the number of similarities above the threshold
	private int simAboveThreshold( ArrayList<Vertex> childrenList, int indexofC1 ) throws Exception {
		
		int simAbove = 0;
		Vertex C1;
		Vertex C2;
		for( int j = 0; j < childrenList.size(); j++ ) {
			if( indexofC1 == j ) continue;
			
			C1 = childrenList.get(indexofC1);
			C2 = childrenList.get(j);
			
			Alignment ali = im.alignTwoNodes(C1.getNode(), C2.getNode(), whichType );
			if ( ali.getSimilarity() >= threshold ) {
				simAbove++;
			}
			
		}
		
		return simAbove;
		
	}
	
	// compares each child to every other using the initial matchers, and returns the number of similarities above the threshold
	public HashMap<Node, Double> simSetAboveThreshold( ArrayList<Vertex> childrenList, Vertex C1 ) throws Exception {
		
		HashMap<Node, Double> vl = new HashMap<Node, Double>();
		
		Vertex C2;
		for( int j = 0; j < childrenList.size(); j++ ) {

			C2 = childrenList.get(j);
			
			Alignment ali = im.alignTwoNodes(C1.getNode(), C2.getNode(), whichType );
			if ( ali.getSimilarity() >= threshold ) {
				vl.put(C2.getNode(), ali.getSimilarity());
			}
			
		}
		
		return vl;
		
	}

	private double SimilarityRepository( Node c1, Node c2 ) {
		
		return 0.0d;
	}

}
