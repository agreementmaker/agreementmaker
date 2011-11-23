package am.app.mappingEngine.matchersCombinationML;

import am.app.ontology.Ontology;


/**
 * generates all possible correspondences(sourceConcept - targetConcept) pairs 
 * given two ontologies, source and target.
 * @author vivek
 *
 */
@Deprecated
public class CorrespondencesGenerator {
	
	Ontology sourceOntology;
	Ontology targetOntology;
	
	public CorrespondencesGenerator(Ontology sourceOntology,Ontology targetOntology)
	{
		this.sourceOntology=sourceOntology;
		this.targetOntology=targetOntology;
	}

}
