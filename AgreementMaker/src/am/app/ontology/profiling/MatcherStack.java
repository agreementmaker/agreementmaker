package am.app.ontology.profiling;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;

/**
 * This method is meant to represent a configuration of a stack of matching algorithms.
 * 
 * A MatcherStack object gets created by the Ontology Profiling algorithms.
 * 
 * @author cosmin
 *
 */
public interface MatcherStack {

	/** This method runs the matcher stack on two ontologies. */
	public void align( Ontology sourceOntology, Ontology targetOntology );
	
	/** @return The alignment that was created by the matcher stack.  Return null if align() was not called. */
	public Alignment<Mapping> getAlignment();
	
}
