package am.app.mappingEngine.testMatchers;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;

public class AllZeroMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = -1456140335684209855L;

	public AllZeroMatcher() {
		super();
		
		setName("AllZero Matcher");
		setCategory(MatcherCategory.UTILITY);
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
