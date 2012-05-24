package am.app.mappingEngine;

import java.awt.Color;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.ontology.Ontology;

public class MatcherResult {
	
	private int sourceOntologyID;
	private int targetOntologyID;
	private Alignment<Mapping> classesAlignment;
	private Alignment<Mapping> propertiesAlignment;
	private Alignment<Mapping> instancesAlignment;
	private int id;
	private String matcherName;
	private boolean visible;
	private DefaultMatcherParameters params;
	private List<AbstractMatcher> inputMatchers;
	private boolean modifiedbyUser;
	private boolean alignClass;
	private boolean alignProp;
	private int numberOfAlignments;
	private long executionTime;
	private boolean refEvaluated;
	private ReferenceEvaluationData refEvalData;
	private QualityEvaluationData qualEvalData;
	private boolean qualEvaluated;
	private Color color;
	private SimilarityMatrix classesMatrix;
	private SimilarityMatrix propMatrix;
	private Ontology sourceOntology;
	private Ontology targetOntology;

	public MatcherResult(AbstractMatcher a) {
		this.classesAlignment = a.getClassAlignmentSet();
		this.propertiesAlignment = a.getPropertyAlignmentSet();
		this.instancesAlignment = a.getInstanceAlignmentSet();
		
		if( classesAlignment != null ) {
			sourceOntologyID = classesAlignment.getSourceOntologyID();
			targetOntologyID = classesAlignment.getTargetOntologyID();
		}
		else {
			sourceOntologyID = -1;
			targetOntologyID = -1;
		}
		
		
		
		id=a.getID();
		matcherName=a.getName();
		visible=a.isShown();
		params=a.getParam();
		inputMatchers=a.getInputMatchers();
		modifiedbyUser=a.isModifiedByUser();
		alignClass=a.isAlignClass();
		alignProp=a.isAlignProp();
		numberOfAlignments=a.getNumberClassAlignments();
		executionTime=a.getExecutionTime();
		refEvaluated=a.isRefEvaluated();
		refEvalData=a.getRefEvaluation();
		qualEvalData=a.getQualEvaluation();
		qualEvaluated=a.isQualEvaluated();
		color=a.getColor();
		classesMatrix=a.getClassesMatrix();
		propMatrix=a.getPropertiesMatrix();
	}
	
	public Alignment<Mapping> getAlignment() {
		Alignment<Mapping> mergedAlignment = new Alignment<Mapping>(sourceOntologyID, targetOntologyID);
		mergedAlignment.addAll(classesAlignment);
		mergedAlignment.addAll(propertiesAlignment);
		//mergedAlignment.addAll(instancesAlignment);
		return mergedAlignment;
	}

	public Alignment<Mapping> getClassAlignmentSet() {return classesAlignment;}

	public Alignment<Mapping> getPropertyAlignmentSet() {return propertiesAlignment;}

	public Alignment<Mapping> getInstanceAlignmentSet() {return instancesAlignment;}

	public int getID() {return id;}

	public String getMatcherName() {return matcherName;}

	public boolean isShown() {return visible;}

	public DefaultMatcherParameters getParameters() {return params;}

	public List<AbstractMatcher> getInputMatchers() {return inputMatchers;}

	public boolean isModifiedByUser() {return modifiedbyUser;}

	public boolean isAlignClass() {return alignClass;}

	public boolean isAlignProp() {return alignProp;}

	public int getTotalNumberAlignments() {return numberOfAlignments;}

	public long getExecutionTime() {return executionTime;}

	public boolean isRefEvaluated() {return refEvaluated;}

	public ReferenceEvaluationData getRefEvaluation() {return refEvalData;}

	public QualityEvaluationData getQualEvaluation() {return qualEvalData;}

	public boolean isQualEvaluated() {return qualEvaluated;}

	public Color getColor() {return color;}

	public void setAlignClass(boolean b) {alignClass=b;}

	public void setAlignProp(boolean b) {alignProp=b;}

	public void setColor(Color c) {
		color=c;
		MatchingTaskChangeEvent mce = new MatchingTaskChangeEvent(this, MatchingTaskChangeEvent.EventType.MATCHER_COLOR_CHANGED);
		Core.getInstance().fireEvent(mce);
	}

	public void setMaxSourceAlign(int i) {params.maxSourceAlign=i;}

	public void setMaxTargetAlign(int i) {params.maxTargetAlign=i;}

	public void setShown(boolean b) {visible=b;}

	public void setThreshold(double d) {
		if( params == null ) { params = new DefaultMatcherParameters(); }
			params.threshold = d;
	}

	public SimilarityMatrix getClassesMatrix() {return classesMatrix;}

	public SimilarityMatrix getPropertiesMatrix() {return propMatrix;}

	public void setMatcherName(String s) {matcherName=s;}

	public Ontology getSourceOntology() { return sourceOntology; }
	public Ontology getTargetOntology() { return targetOntology; }

	public void setClassAlignmentSet(Alignment<Mapping> set) {
		classesAlignment = set;
	}

	public void setInstanceAlignmentSet(Alignment<Mapping> set) {
		instancesAlignment = set;
	}

	public void setPropertyAlignmentSet(Alignment<Mapping> set) {
		propertiesAlignment = set;
	}

	public void setQualEvaluation(QualityEvaluationData data) {
		qualEvalData = data;
	}

	public void setRefEvaluation(ReferenceEvaluationData data) {
		refEvalData = data;
	}
    
}
