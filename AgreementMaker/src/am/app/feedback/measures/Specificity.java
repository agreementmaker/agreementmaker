package am.app.feedback.measures;

import java.util.ArrayList;

import am.app.Core;
import am.app.feedback.CandidateConcept;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.userInterface.vertex.Vertex;

public class Specificity extends RelevanceMeasure {

	CandidateConcept.ontology whichOntology;
	alignType whichType;

	public void calculateRelevances() {
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		whichOntology = CandidateConcept.ontology.source;
		
		// source classes
		whichType     = alignType.aligningClasses;
		visitNode( sourceOntology.getClassesTree(), 1 );
		if( sourceOntology.getClassesTree().getNode() != null )
			candidateList.add( new CandidateConcept( sourceOntology.getClassesTree().getNode(), 1.0d, whichOntology, whichType ));

		// source properties
		whichType     = alignType.aligningProperties;
		visitNode( sourceOntology.getPropertiesTree(), 1 );
		if( sourceOntology.getPropertiesTree().getNode() != null )		
			candidateList.add( new CandidateConcept( sourceOntology.getPropertiesTree().getNode(), 1.0d, whichOntology, whichType ));

		
		
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		whichOntology = CandidateConcept.ontology.target;
		
		// target classes
		whichType     = alignType.aligningClasses;
		visitNode( targetOntology.getClassesTree(), 1 );
		if( targetOntology.getClassesTree().getNode() != null )
			candidateList.add( new CandidateConcept( targetOntology.getClassesTree().getNode(), 1.0d, whichOntology, whichType ));

		
		// target properties
		whichType     = alignType.aligningProperties;
		visitNode( targetOntology.getPropertiesTree(), 1 );
		if( targetOntology.getPropertiesTree().getNode() != null )
			candidateList.add( new CandidateConcept( targetOntology.getPropertiesTree().getNode(), 1.0d, whichOntology, whichType ));

		
		
		
	}
	
	protected void visitNode( Vertex concept, int depth ) {
		
		ArrayList<Vertex> childrenList = new ArrayList<Vertex>();
		int numChildren = concept.getChildCount();
		
		for( int i = 0; i < numChildren; i++ ) {
			childrenList.add((Vertex) concept.getChildAt(i));
		}
		
		int fanout = childrenList.size();
		
		
		// visit the children
		double Depth = new Double(depth);
		double Fanout = new Double(fanout);
		for( int i = 0; i < childrenList.size(); i++ ) {
			
			Node curr = childrenList.get(i).getNode();
			
			//if( !fbl.isValidated(curr) ) {
				double specificity = (1 / Depth) * ( 1 / Fanout);
				candidateList.add( new CandidateConcept( childrenList.get(i).getNode(), specificity, whichOntology, whichType ));
			//}	
			visitNode( childrenList.get(i), depth + 1 );
		}
		
	}
	
	
	
	
}
