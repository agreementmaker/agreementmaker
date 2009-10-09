package am.app.mappingEngine.testMatchers;


import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AlignmentMatrix;

public class CopyMatcher extends AbstractMatcher {
	
	public CopyMatcher() {
		maxInputMatchers = 1;
		minInputMatchers = 1;
	}
	
	protected void beforeAlignOperations() throws Exception{
    	super.beforeAlignOperations();
    	AbstractMatcher a = inputMatchers.get(0);
    	modifiedByUser = false;
		alignClass = a.isAlignClass();
		alignProp = a.isAlignProp();
	}
	
	public void addInputMatcher(AbstractMatcher a) {
		inputMatchers.add(a);

		needsParam = a.needsParam();
		if(needsParam)
			param = a.getParam();
	}
	
	
    protected void align() {
    	AbstractMatcher a = inputMatchers.get(0);
		if(alignClass) {
			classesMatrix = (AlignmentMatrix)a.getClassesMatrix().clone();
			//classesMatrix.show();
		}
		if(alignProp) {
			propertiesMatrix = (AlignmentMatrix)a.getPropertiesMatrix().clone();
		}

	}


}
