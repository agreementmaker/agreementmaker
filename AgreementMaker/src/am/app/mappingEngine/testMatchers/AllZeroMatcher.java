package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;

public class AllZeroMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = -1456140335684209855L;

	/**Set all alignment sim to 1*/
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		double sim = 0;
		MappingRelation rel = MappingRelation.EQUIVALENCE;
		return new Mapping(source, target, sim, rel);
	}
}
