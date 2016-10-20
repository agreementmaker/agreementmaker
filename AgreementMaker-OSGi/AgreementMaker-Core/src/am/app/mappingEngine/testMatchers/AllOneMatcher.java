package am.app.mappingEngine.testMatchers;

import am.api.matching.Matcher;
import am.api.matching.MatcherResult;
import am.api.ontology.Ontology;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.ds.matching.MatcherResultImpl;

public class AllOneMatcher extends AbstractMatcher implements Matcher {
	
	private static final long serialVersionUID = 2854018267136437040L;

	public AllOneMatcher() {
		super();
		
		setName("AllOne Matcher");
		setCategory(MatcherCategory.UTILITY);
	}

    @Override
    public MatcherResult match(Ontology source, Ontology target) {
        return new MatcherResultImpl(
                (sourceEntity, targetEntity) -> 1,
                (sourceEntity, targetEntity) -> 1,
                (sourceEntity, targetEntity) -> 1);
    }

    /**Set all alignment sim to 1*/
	@Override
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix) {
		double sim = 1;
		MappingRelation rel = MappingRelation.EQUIVALENCE;
		return new Mapping(source, target, sim, rel);
	}
}
