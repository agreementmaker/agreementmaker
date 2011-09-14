package am.app.ontology.profiling.classification;

public class Test {
	private String sourceOntology;
	private String targetOntology; 
	private String className;
	
	
	public Test(String sourceOntology, String targetOntology) {
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
		this.className = "";
	}
	
	public Test(String sourceOntology, String targetOntology, String className) {
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
		this.className = className;
	}
	public String getSourceOntology() {
		return sourceOntology;
	}
	public void setSourceOntology(String sourceOntology) {
		this.sourceOntology = sourceOntology;
	}
	public String getTargetOntology() {
		return targetOntology;
	}
	public void setTargetOntology(String targetOntology) {
		this.targetOntology = targetOntology;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String toString() {
		return "Test [sourceOntology=" + sourceOntology + ", targetOntology="
				+ targetOntology + ", className=" + className + "]";
	}
	
	

}
