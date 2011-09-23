package am.app.mappingEngine.hierarchy;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;

/**
 * A purely structural matcher based on tree similarity.
 * @author cosmin
 *
 */
public class HierarchyStructureMatcher extends AbstractMatcher {

	private static final long serialVersionUID = 7144844302412965889L;
	
	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes, SimilarityMatrix matrix) throws Exception {
	
		//if( )
		return null;
	}

}
