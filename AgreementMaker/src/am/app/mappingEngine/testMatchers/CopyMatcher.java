package am.app.mappingEngine.testMatchers;


import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SimilarityMatrix;

public class CopyMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = 7262313405221341087L;


	public CopyMatcher() {
		maxInputMatchers = 1;
		minInputMatchers = 1;
	}
	
	@Override
	protected void beforeAlignOperations() throws Exception{
    	super.beforeAlignOperations();
    	AbstractMatcher a = inputMatchers.get(0);
    	modifiedByUser = false;
		alignClass = a.isAlignClass();
		alignProp = a.isAlignProp();
	}
	
	@Override
	public void addInputMatcher(AbstractMatcher a) {
		inputMatchers.add(a);

		needsParam = a.needsParam();
		if(needsParam)
			param = a.getParam();
	}
	
	@Override
    protected void align() {
    	AbstractMatcher a = inputMatchers.get(0);
		if(alignClass) {
			classesMatrix = (SimilarityMatrix)a.getClassesMatrix().clone();
			//classesMatrix.show();
		}
		if(alignProp) {
			propertiesMatrix = (SimilarityMatrix)a.getPropertiesMatrix().clone();
		}

	}


}
