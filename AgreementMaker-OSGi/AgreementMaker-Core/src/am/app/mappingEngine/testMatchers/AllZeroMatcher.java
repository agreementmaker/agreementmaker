package am.app.mappingEngine.testMatchers;

import am.api.matching.Matcher;
import am.api.matching.MatcherProperties;
import am.api.matching.MatcherResult;
import am.api.ontology.Ontology;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.ds.matching.MatcherResultImpl;

public class AllZeroMatcher extends AbstractMatcher implements Matcher {
	public AllZeroMatcher() {
		super();
        properties.setProperty(MatcherProperties.DISPLAY_NAME, "AllZero Matcher");
        properties.setProperty(MatcherProperties.CATEGORY, am.api.matching.MatcherCategory.UTILITY);
	}

    @Override
    public MatcherResult match(Ontology source, Ontology target) {
        return new MatcherResultImpl(
                (sourceEntity, targetEntity) -> 0,
                (sourceEntity, targetEntity) -> 0,
                (sourceEntity, targetEntity) -> 0);
    }

    @Override
	protected void align() throws Exception {
		if( sourceOntology == null || targetOntology == null ) return;  // cannot align just one ontology 

		if(alignClass && !this.isCancelled() ) {
			classesMatrix = new SparseMatrix( sourceOntology, targetOntology, alignType.aligningClasses);	
		}
		if(alignProp && !this.isCancelled() ) {
			propertiesMatrix = new SparseMatrix( sourceOntology, targetOntology, alignType.aligningProperties);					
		}
	}
}
