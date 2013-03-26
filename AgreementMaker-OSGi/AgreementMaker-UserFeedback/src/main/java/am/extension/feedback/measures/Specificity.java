package am.extension.feedback.measures;

import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.extension.feedback.CandidateConcept;

public class Specificity extends RelevanceMeasure {

	int whichOntology;
	alignType whichType;

	public void calculateRelevances() {
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		whichOntology = Ontology.SOURCE;
		
		// source classes
		whichType     = alignType.aligningClasses;
		visitNode( sourceOntology.getClassesRoot(), 1 );
		if( sourceOntology.getClassesRoot() != null )
			candidateList.add( new CandidateConcept( sourceOntology.getClassesRoot(), 1.0d, whichOntology, whichType ));

		// source properties
		whichType     = alignType.aligningProperties;
		visitNode( sourceOntology.getPropertiesRoot(), 1 );
		if( sourceOntology.getPropertiesRoot() != null )		
			candidateList.add( new CandidateConcept( sourceOntology.getPropertiesRoot(), 1.0d, whichOntology, whichType ));

		
		
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		whichOntology = Ontology.TARGET;
		
		// target classes
		whichType     = alignType.aligningClasses;
		visitNode( targetOntology.getClassesRoot(), 1 );
		if( targetOntology.getClassesRoot() != null )
			candidateList.add( new CandidateConcept( targetOntology.getClassesRoot(), 1.0d, whichOntology, whichType ));

		
		// target properties
		whichType     = alignType.aligningProperties;
		visitNode( targetOntology.getPropertiesRoot(), 1 );
		if( targetOntology.getPropertiesRoot() != null )
			candidateList.add( new CandidateConcept( targetOntology.getPropertiesRoot(), 1.0d, whichOntology, whichType ));

		
		
		
	}
	
	protected void visitNode( Node concept, int depth ) {
		
		List<Node> childrenList = new ArrayList<Node>();
		int numChildren = concept.getChildCount();
		
		for( int i = 0; i < numChildren; i++ ) {
			childrenList.add( concept.getChildAt(i) );
		}
		
		int fanout = childrenList.size();
		
		
		// visit the children
		double Depth = new Double(depth);
		double Fanout = new Double(fanout);
		for( int i = 0; i < childrenList.size(); i++ ) {
			
			//Node curr = childrenList.get(i).getNode();
			
			//if( !fbl.isValidated(curr) ) {
				double specificity = (1 / Depth) * ( 1 / Fanout);
				candidateList.add( new CandidateConcept( childrenList.get(i), specificity, whichOntology, whichType ));
			//}	
			visitNode( childrenList.get(i), depth + 1 );
		}
		
	}
	
	
	
	
}
