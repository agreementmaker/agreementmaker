package am.app.feedback.measures;

import java.util.ArrayList;

import am.app.Core;
import am.app.feedback.CandidateConcept;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class InformationGain extends RelevanceMeasure {

	CandidateConcept.ontology whichOntology;
	alignType whichType;
	
	
	public void calculateRelevances() {
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		whichOntology = CandidateConcept.ontology.source;
		
		// source classes
		whichType     = alignType.aligningClasses;
		visitNode( sourceOntology.getClassesList(), 1 );
		candidateList.add( new CandidateConcept( sourceOntology.getClassesTree().getNode(), 1.0d, whichOntology, whichType ));

		// source properties
		whichType     = alignType.aligningProperties;
		visitNode( sourceOntology.getPropertiesList(), 1 );
		candidateList.add( new CandidateConcept( sourceOntology.getPropertiesTree().getNode(), 1.0d, whichOntology, whichType ));

		
		
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		whichOntology = CandidateConcept.ontology.target;
		
		// target classes
		whichType     = alignType.aligningClasses;
		visitNode( targetOntology.getClassesList(), 1 );
		candidateList.add( new CandidateConcept( targetOntology.getClassesTree().getNode(), 1.0d, whichOntology, whichType ));

		
		// target properties
		whichType     = alignType.aligningProperties;
		visitNode( targetOntology.getPropertiesList(), 1 );
		candidateList.add( new CandidateConcept( targetOntology.getPropertiesTree().getNode(), 1.0d, whichOntology, whichType ));

		
		
		
	}
	
	private void visitNode(ArrayList<Node> classesList, int i) {
		// TODO: WRITE THIS !
		
	}

	
	
	
}
