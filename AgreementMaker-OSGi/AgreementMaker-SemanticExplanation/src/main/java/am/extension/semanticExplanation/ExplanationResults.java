package am.extension.semanticExplanation;

public class ExplanationResults {

	private boolean areSameWords;
	
	private boolean areTheySynonymns;
	
	private boolean areTheyHyponymns;
	
	private boolean areTheyHypernymns;
	
	private boolean sourceSynonymOfTarget;
	
	
	private double stringSimilarityValue;

	public boolean isAreSameWords() {
		return areSameWords;
	}

	public void setAreSameWords(boolean areSameWords) {
		this.areSameWords = areSameWords;
	}

	public boolean getAreTheySynonymns() {
		return areTheySynonymns;
	}

	public void setAreTheySynonymns(boolean areTheySynonymns) {
		this.areTheySynonymns = areTheySynonymns;
	}

	public boolean getAreTheyHyponymns() {
		return areTheyHyponymns;
	}

	public void setAreTheyHyponymns(boolean areTheyHyponymns) {
		this.areTheyHyponymns = areTheyHyponymns;
	}

	public boolean getAreTheyHypernymns() {
		return areTheyHypernymns;
	}

	public void setAreTheyHypernymns(boolean areTheyHypernymns) {
		this.areTheyHypernymns = areTheyHypernymns;
	}

	public double getStringSimilarityValue() {
		return stringSimilarityValue;
	}

	public void setStringSimilarityValue(double stringSimilarityValue) {
		this.stringSimilarityValue = stringSimilarityValue;
	}

	public boolean getSourceSynonymOfTarget() {
		return sourceSynonymOfTarget;
	}

	public void setSourceSynonymOfTarget(boolean sourceSynonymOfTarget) {
		this.sourceSynonymOfTarget = sourceSynonymOfTarget;
	}
	
}
