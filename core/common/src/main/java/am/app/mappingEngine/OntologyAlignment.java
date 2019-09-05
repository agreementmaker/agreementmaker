package am.app.mappingEngine;

import am.app.ontology.Node;

/**
 * AgreementMaker Alignment interface.
 * 
 * Defines the methods an alignment class should have.
 * 
 * @author Cosmin Stroe, Sept. 12, 2011
 *
 */
public interface OntologyAlignment {

	public boolean isMapped( Node n );
	
	public double getSimilarity( Node sourceNode, Node targetNode  );
	
	public void setSimilarity( Node sourceNode, Node targetNode, double sim );
	
	//public boolean add( Mapping m );
	
	public Mapping contains( Node sourceNode, Node targetNode );
	
}
