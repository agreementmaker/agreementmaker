package am.app.ontology.profiling.classification.trainingGeneration;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;

public class InputTest {
	
	private AbstractMatcher matcher; 
	private AbstractParameters param; 
	private String className;
	
	
	
	public InputTest(AbstractMatcher matcher, AbstractParameters param, String className) {
		this.matcher = matcher;
		this.param = param;
		this.className = className;
	}
	public AbstractMatcher getMatcher() {
		return matcher;
	}
	public void setMatcher(AbstractMatcher matcher) {
		this.matcher = matcher;
	}
	public AbstractParameters getParam() {
		return param;
	}
	public void setParam(AbstractParameters param) {
		this.param = param;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public String toString() {
		
		return "ClassName: "+className+"Matcher: "+ matcher.getName() + "Parameters: " +param.toString();
	} 
	
	

}
