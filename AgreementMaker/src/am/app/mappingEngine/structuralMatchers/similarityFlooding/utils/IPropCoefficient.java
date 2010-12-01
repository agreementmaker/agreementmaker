/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.app.ontology.Node;
import am.utility.DirectedGraphEdge;

/**
 * @author Michele Caci
 * @param <E>
 *
 */
public interface IPropCoefficient {

	public void applyCoefficient(DirectedGraphEdge<Node> selectedEdge); 
}
