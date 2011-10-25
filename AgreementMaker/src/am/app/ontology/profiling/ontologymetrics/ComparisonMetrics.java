package am.app.ontology.profiling.ontologymetrics;

public class ComparisonMetrics {
	double relationshipSimilarity;
	//comments null or irrelevant
	boolean noComments;
	//labels null or irrelevant
	boolean noLabels;
	//local names irrelevant
	boolean noLocal;
	//no instances
	boolean noInstances;
	
	public boolean noLocal() {
		return noLocal;
	}

	public void setNoLocal(boolean noLocal) {
		this.noLocal = noLocal;
	}

	public boolean noInstances() {
		return noInstances;
	}

	public void setNoInstances(boolean noInstances) {
		this.noInstances = noInstances;
	}

	public double getRelationshipSimilarity() {
		return relationshipSimilarity;
	}
	
	public void setRelationshipSimilarity(double relationshipSimilarity) {
		this.relationshipSimilarity = relationshipSimilarity;
	}
	
	public boolean noComments() {
		return noComments;
	}
	
	public void setNoComments(boolean noComments) {
		this.noComments = noComments;
	}
	
	public boolean noLabels() {
		return noLabels;
	}
	
	public void setNoLabels(boolean noLabels) {
		this.noLabels = noLabels;
	}
}
