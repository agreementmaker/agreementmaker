package am.app.mappingEngine;

import java.awt.Color;
import java.util.List;

import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;

public class MatcherResultImpl implements MatcherResult {

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

	public MatcherResultImpl(AbstractMatcher a) {
		this.classesAlignment = a.getClassAlignmentSet();
		this.propertiesAlignment = a.getPropertyAlignmentSet();
		this.instancesAlignment = a.getInstanceAlignmentSet();
		
		sourceOntologyID = classesAlignment.getSourceOntologyID();
		targetOntologyID = classesAlignment.getTargetOntologyID();
		
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
	
	@Override
	public Alignment<Mapping> getAlignment() {
		Alignment<Mapping> mergedAlignment = new Alignment<Mapping>(sourceOntologyID, targetOntologyID);
		mergedAlignment.addAll(classesAlignment);
		mergedAlignment.addAll(propertiesAlignment);
		//mergedAlignment.addAll(instancesAlignment);
		return mergedAlignment;
	}

	@Override
	public Alignment<Mapping> getClassAlignmentSet() {return classesAlignment;}

	@Override
	public Alignment<Mapping> getPropertyAlignmentSet() {return propertiesAlignment;}

	@Override
	public Alignment<Mapping> getInstanceAlignmentSet() {return instancesAlignment;}

	@Override
	public int getID() {return id;}

	@Override
	public String getMatcherName() {return matcherName;}

	@Override
	public boolean isShown() {return visible;}

	@Override
	public DefaultMatcherParameters getParameters() {return params;}

	@Override
	public List<AbstractMatcher> getInputMatchers() {return inputMatchers;}

	@Override
	public boolean isModifiedByUser() {return modifiedbyUser;}

	@Override
	public boolean isAlignClass() {return alignClass;}

	@Override
	public boolean isAlignProp() {return alignProp;}

	@Override
	public int getTotalNumberAlignments() {return numberOfAlignments;}

	@Override
	public long getExecutionTime() {return executionTime;}

	@Override
	public boolean isRefEvaluated() {return refEvaluated;}

	@Override
	public ReferenceEvaluationData getRefEvaluation() {return refEvalData;}

	@Override
	public QualityEvaluationData getQualEvaluation() {return qualEvalData;}

	@Override
	public boolean isQualEvaluated() {return qualEvaluated;}

	@Override
	public Color getColor() {return color;}

	@Override
	public void setAlignClass(boolean b) {alignClass=b;}

	@Override
	public void setAlignProp(boolean b) {alignProp=b;}

	@Override
	public void setColor(Color c) {color=c;}

	@Override
	public void setMaxSourceAlign(int i) {params.maxSourceAlign=i;}

	@Override
	public void setMaxTargetAlign(int i) {params.maxTargetAlign=i;}

	@Override
	public void setShown(boolean b) {visible=b;}

	@Override
	public void setThreshold(double d) {
		if( params == null ) { params = new DefaultMatcherParameters() {}; }
			params.threshold = d;
	}

	@Override
	public SimilarityMatrix getClassesMatrix() {return classesMatrix;}

	@Override
	public SimilarityMatrix getPropertiesMatrix() {return propMatrix;}

	@Override
	public void setMatcherName(String s) {matcherName=s;}
	
	

}
