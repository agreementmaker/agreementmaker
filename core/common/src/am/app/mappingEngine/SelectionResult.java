package am.app.mappingEngine;

import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;

/**
 * Stores the result of a selection algorithm.
 * 
 * @author Cosmin Stroe
 *
 */
public class SelectionResult {

	public int sourceOntologyID;
	public int targetOntologyID;
	
	public Alignment<Mapping> classesAlignment;
	public Alignment<Mapping> instancesAlignment;
	public Alignment<Mapping> propertiesAlignment;
	public QualityEvaluationData qualEvalData;
	public ReferenceEvaluationData refEvalData;

	private MatchingTask task;
	
	public SelectionResult() {
		this.task = null;
	}
	
	public SelectionResult(MatchingTask task) {
		this.task = task;
	}
	
	public boolean areClassesAligned() {
		return classesAlignment != null;
	}

	public boolean arePropertiesAligned() {
		return propertiesAlignment != null;
	}
	
	public MatchingTask getMatchingTask() { return task; }
	
	public boolean isRefEvaluated() {return refEvalData != null;}

	public boolean isQualEvaluated() {return qualEvalData != null;}
	
	public ReferenceEvaluationData getRefEvaluation() {return refEvalData;}

	public QualityEvaluationData getQualEvaluation() {return qualEvalData;}
	
	public Alignment<Mapping> getClassAlignmentSet() {return classesAlignment;}

	public Alignment<Mapping> getPropertyAlignmentSet() {return propertiesAlignment;}

	public Alignment<Mapping> getInstanceAlignmentSet() {return instancesAlignment;}
	
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
	
	
    public int getNumberClassAlignments() {
    	int numAlign = 0;
		if(areClassesAligned()) {
			numAlign += getClassAlignmentSet().size();
		}
		return numAlign;
    }
    
    public int getNumberPropertiesAlignments() {
    	int numAlign = 0;
		if(arePropertiesAligned()) {
			numAlign += getPropertyAlignmentSet().size();
		}
		return numAlign;
    }
    
	public Alignment<Mapping> getAlignment() {
		Alignment<Mapping> mergedAlignment = new Alignment<Mapping>(sourceOntologyID, targetOntologyID);
		mergedAlignment.addAll(classesAlignment);
		mergedAlignment.addAll(propertiesAlignment);
		//mergedAlignment.addAll(instancesAlignment);
		return mergedAlignment;
	}

	public int getTotalNumberAlignments() {
		return getNumberClassAlignments() + getNumberPropertiesAlignments();
	}
	
}
