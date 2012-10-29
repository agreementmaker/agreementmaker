package am.app.ontology.profiling.classification.trainingGeneration;

public class Result {
	
	private String className;
	private String matcherName; 
	private String param;
	private String sourceOntologyFileName;
	private String targetOntologyFileName;
	private String sourceOntologyName;
	private String targetOntologyName;
	private double precision;
	private double recall;
	private double fMeasure;
	
	
	


	public String getSourceOntologyName() {
		return sourceOntologyName;
	}


	public void setSourceOntologyName(String sourceOntologyName) {
		this.sourceOntologyName = sourceOntologyName;
	}


	public String getTargetOntologyName() {
		return targetOntologyName;
	}


	public void setTargetOntologyName(String targetOntologyName) {
		this.targetOntologyName = targetOntologyName;
	}


	public Result(String className, String matcherName, String param,
			String sourceOntologyFileName, String targetOntologyFileName,
			String sourceOntologyName, String targetOntologyName,
			double precision, double recall, double fMeasure) {
		this.className = className;
		this.matcherName = matcherName;
		this.param = param;
		this.sourceOntologyFileName = sourceOntologyFileName;
		this.targetOntologyFileName = targetOntologyFileName;
		this.sourceOntologyName = sourceOntologyName;
		this.targetOntologyName = targetOntologyName;
		this.precision = precision;
		this.recall = recall;
		this.fMeasure = fMeasure;
	}


	

	@Override
	public String toString() {
		return "Result [className=" + className + ", matcherName="
				+ matcherName + ", param=" + param
				+ ", sourceOntologyFileName=" + sourceOntologyFileName
				+ ", targetOntologyFileName=" + targetOntologyFileName
				+ ", sourceOntologyName=" + sourceOntologyName
				+ ", targetOntologyName=" + targetOntologyName + ", precision="
				+ precision + ", recall=" + recall + ", fMeasure=" + fMeasure
				+ "]";
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getMatcherName() {
		return matcherName;
	}


	public void setMatcherName(String matcherName) {
		this.matcherName = matcherName;
	}


	public String getParam() {
		return param;
	}


	public void setParam(String param) {
		this.param = param;
	}


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


	public double getPrecision() {
		return precision;
	}


	public void setPrecision(double precision) {
		this.precision = precision;
	}


	public double getRecall() {
		return recall;
	}


	public void setRecall(double recall) {
		this.recall = recall;
	}


	public double getfMeasure() {
		return fMeasure;
	}


	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}
	
	
	
	
	
}
