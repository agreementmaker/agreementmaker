package am.app.ontology.profiling.classification.trainingGeneration;

public class Winner {

	private String sourceOntologyFileName;
	private String targetOntologyFileName;
	private String className;
	public String getSourceOntologyFileName() {
		return sourceOntologyFileName;
	}
	public void setSourceOntologyFileName(String sourceOntologyFileName) {
		this.sourceOntologyFileName = sourceOntologyFileName;
	}
	public String getTargetOntologyFileName() {
		return targetOntologyFileName;
	}
	public void setTargetOntologyFileName(String targetOntologyFileName) {
		this.targetOntologyFileName = targetOntologyFileName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	@Override
	public String toString() {
		return "Winner [sourceOntologyFileName=" + sourceOntologyFileName
				+ ", targetOntologyFileName=" + targetOntologyFileName
				+ ", className=" + className + "]";
	}
	public Winner(String sourceOntologyFileName, String targetOntologyFileName,
			String className) {
		super();
		this.sourceOntologyFileName = sourceOntologyFileName;
		this.targetOntologyFileName = targetOntologyFileName;
		this.className = className;
	}
	
	
	
	
	
	
	
}
