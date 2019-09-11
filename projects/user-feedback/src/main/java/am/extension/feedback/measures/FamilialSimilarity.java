package am.extension.feedback.measures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.extension.feedback.CandidateConcept;
import am.extension.feedback.FilteredAlignmentMatrix;
import am.extension.feedback.InitialMatchers;


public class FamilialSimilarity extends RelevanceMeasure {

	private static final Logger sLog = Logger.getLogger(FamilialSimilarity.class);
	
	int whichOntology;
	alignType whichType;
	
	
	InitialMatchers im;
	
	public FamilialSimilarity() {
		super();
		im = new InitialMatchers();
	}
	
	public FamilialSimilarity(double th) {
		super(th);
		im = new InitialMatchers();
	}


	public void calculateRelevances() {
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		whichOntology = Ontology.SOURCE;
		
		
		// source classes
		whichType     = alignType.aligningClasses;
		try {
			visitNode( sourceOntology.getClassesRoot() , fbl.getClassesMatrix(), true);
		} catch (Exception e) {
			sLog.error(e, e);
		}
		
		// source properties
		whichType     = alignType.aligningProperties;
		try {
			visitNode( sourceOntology.getPropertiesRoot() , fbl.getPropertiesMatrix(), true);
		} catch (Exception e) {
			sLog.error(e, e);
		}
		
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		whichOntology = Ontology.TARGET;
		
		// target classes
		whichType     = alignType.aligningClasses;
		try {
			visitNode( targetOntology.getClassesRoot(),fbl.getClassesMatrix(), false );
		} catch (Exception e) {
			sLog.error(e, e);
		}
		
		// target properties
		whichType     = alignType.aligningProperties;
		try {
			visitNode( targetOntology.getPropertiesRoot(), fbl.getPropertiesMatrix(), false );
		} catch (Exception e) {
			sLog.error(e, e);
		}
		
	}
	
	
	// makes a list of the children and compares each child to every other
	protected void visitNode( Node concept, FilteredAlignmentMatrix matrix, boolean isSource) throws Exception {
		
		

		ArrayList<Node> childrenList = new ArrayList<Node>();

		// construct the childrenList
		int numChildren = concept.getChildCount();
		for( int i = 0; i < numChildren; i++ ) {
			childrenList.add(concept.getChildAt(i));
		}
		
		
		if( childrenList.size() > 1 ) {
			// two or more children
			for( int i = 0; i < childrenList.size(); i++ ) {
				if(isSource && matrix.isRowFiltered(childrenList.get(i).getIndex())){
					//skip the node because it has been mapped and validated already
					continue;
				}
				else if(!isSource && matrix.isColFiltered(childrenList.get(i).getIndex())){
					//skip the node because it has been mapped and validated already
					continue;
				}
				else{
					int sim = simAboveThreshold( childrenList, i);
					if( sim > 0 ) {
						candidateList.add( new CandidateConcept( childrenList.get(i), sim, whichOntology, whichType ));
					}
				}
			}
		}

		// visit the children
		for( int i = 0; i < childrenList.size(); i++ ) {
			visitNode( childrenList.get(i), matrix, isSource);
		}
		
		
	}
	
	
	// compares each child to every other using the initial matchers, and returns the number of similarities above the threshold
	private int simAboveThreshold( ArrayList<Node> childrenList, int indexofC1 ) throws Exception {
		
		int simAbove = 0;
		Node C1;
		Node C2;
		for( int j = 0; j < childrenList.size(); j++ ) {
			if( indexofC1 == j ) continue;
			
			C1 = childrenList.get(indexofC1);
			C2 = childrenList.get(j);
			
			Mapping ali = im.alignTwoNodes(C1, C2, whichType, null );
			if ( ali.getSimilarity() >= threshold ) {
				simAbove++;
			}
			
		}
		
		return simAbove;
		
	}
	
	// compares each child to every other using the initial matchers, and returns the number of similarities above the threshold
	public HashMap<Node, Double> simSetAboveThreshold( List<Node> childrenList, Node C1 ) throws Exception {
		
		HashMap<Node, Double> vl = new HashMap<Node, Double>();
		
		Node C2;
		for( int j = 0; j < childrenList.size(); j++ ) {

			C2 = childrenList.get(j);
			
			Mapping ali = im.alignTwoNodes(C1, C2, whichType, null);
			if ( ali.getSimilarity() >= threshold ) {
				vl.put(C2, ali.getSimilarity());
			}
			
		}
		
		return vl;
		
	}
	
}
