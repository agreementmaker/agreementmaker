package am.app.ontology.profiling.classification.trainingGeneration;

import java.util.Iterator;
import java.util.LinkedList;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;

public class InputTrainingGenerator {

	private LinkedList<InputTest> testList;
	private LinkedList<String> classList;
	
	
	public InputTrainingGenerator() {
		this.testList = new LinkedList<InputTest>();
		this.classList = new LinkedList<String>();
	}
	
	public void addTest(AbstractMatcher matcher,AbstractParameters param, String className ) throws Exception{
		if (!classList.contains(className)){
			InputTest i = new InputTest(matcher, param, className);
			testList.add(i);
			classList.add(className);
		}
		else{
			throw new Exception("The class Already exists");
		}
	}
	public void addTest(InputTest test ) throws Exception{
		if (!classList.contains(test.getClassName())){
			testList.add(test);
			classList.add(test.getClassName());
		}
		else{
			throw new Exception("The class Already exists");
		}
	}
	
	
	
	
	
/*	public void removeTest(String className){
		TODO
	}*/
	
	
	public LinkedList<InputTest> getTestList() {
		return testList;
	}
	public void setTestList(LinkedList<InputTest> testList) {
		this.testList = testList;
	}
	public LinkedList<String> getClassList() {
		return classList;
	}
	public void setClassList(LinkedList<String> classList) {
		this.classList = classList;
	}

	@Override
	public String toString() {
		String s = "InputTrainingGenerator : [\n";
		for (Iterator iterator = testList.iterator(); iterator.hasNext();) {
			InputTest test = (InputTest) iterator.next();
			s = s+ ""+test.toString()+"\n";
			
		}
		
		return s + "]";
	}
	
	
	
}
