package am.app.ontology.profiling.ontologymetrics;

import am.app.ontology.profiling.ProfilingReport;

public class OntologyMetrics implements ProfilingReport {
	float relationshipRichness;
	float attributeRichness;
	float inheritanceRichness;
	float labelWordnet;
	float localWordnet;
	float classRichness;
	float avgPopulation;
	float relationshipRichnessInstances;
	float nullCommentPerc;
	float nullLabelPerc;
	float diffLabelLocPerc;
	float avgDepth;
	
	public float getAvgDepth() {
		return avgDepth;
	}

	public void setAvgDepth(float avgDepth) {
		this.avgDepth = avgDepth;
	}

	public float getNullCommentPerc() {
		return nullCommentPerc;
	}

	public void setNullCommentPerc(float nullCommentPerc) {
		this.nullCommentPerc = nullCommentPerc;
	}

	public float getNullLabelPerc() {
		return nullLabelPerc;
	}

	public void setNullLabelPerc(float nullLabelPerc) {
		this.nullLabelPerc = nullLabelPerc;
	}

	public float getDiffLabelLocPerc() {
		return diffLabelLocPerc;
	}

	public void setDiffLabelLocPerc(float diffLabelLocPerc) {
		this.diffLabelLocPerc = diffLabelLocPerc;
	}

	public float getRelationshipRichness() {
		return relationshipRichness;
	}
	
	public void setRelationshipRichness(float relationshipRichness) {
		this.relationshipRichness = relationshipRichness;
	}
	
	public float getAttributeRichness() {
		return attributeRichness;
	}
	
	public void setAttributeRichness(float attributeRichness) {
		this.attributeRichness = attributeRichness;
	}
	
	public float getInheritanceRichness() {
		return inheritanceRichness;
	}
	
	public void setInheritanceRichness(float inheritanceRichness) {
		this.inheritanceRichness = inheritanceRichness;
	}
	
	public float getLabelWordnet() {
		return labelWordnet;
	}
	
	public void setLabelWordnet(float labelWordnet) {
		this.labelWordnet = labelWordnet;
	}
	
	public float getLocalWordnet() {
		return localWordnet;
	}
	
	public void setLocalWordnet(float localWordnet) {
		this.localWordnet = localWordnet;
	}
	
	public float getAvgPopulation() {
		return avgPopulation;
	}
	
	public void setAvgPopulation(float avgPopulation) {
		this.avgPopulation = avgPopulation;
	}
	
	public float getRelationshipRichnessInstances() {
		return relationshipRichnessInstances;
	}
	
	public void setRelationshipRichnessInstances(float relationshipRichnessInstances) {
		this.relationshipRichnessInstances = relationshipRichnessInstances;
	}
	
	public float getClassRichness() {
		return classRichness;
	}

	public void setClassRichness(float classRichness) {
		this.classRichness = classRichness;
	}
	
	@Override
	public String toString() {
		String ret = "relationshipRichness:" + relationshipRichness +
			"\nattributeRichness:"+attributeRichness + "\ninheritanceRichness:"+ inheritanceRichness + 
			"\nlabelWordnet:" + labelWordnet + "\nlocalWordnet:" +localWordnet + 
			"\nclassRichness:" + classRichness + "\navgPopulation:" + avgPopulation + 
			//"\nrelationshipRichnessInstances:" + relationshipRichnessInstances +
			"\nnullLabelPerc:" + nullLabelPerc + "\ndiffLabelLocPerc:" + diffLabelLocPerc +
			"\nnullCommentPerc:" + nullCommentPerc;
		return ret;
	}
	
	public String toTuple() {
		String ret = relationshipRichness + "\t" + inheritanceRichness + "\t" + avgDepth + "\t"
		+ attributeRichness + "\t"+ labelWordnet + "\t"	+localWordnet + "\t" + classRichness + "\t" + avgPopulation + 
			"\t" + nullLabelPerc + "\t" + diffLabelLocPerc + "\t" + nullCommentPerc;
		return ret;
	}
	
	public static String getHeader(){
		return "relationshipRichness\tinheritanceRichness\tavgDepth" +
				"\tattributeRichness\tlabelWordnet\tlocalWordnet\tclassRichness\tavgPopulation\t" +
				"nullLabelPerc\tdiffLabelLocPerc\tnullCommentPerc";
	}
}
