package am.app.ontology.profiling.ontologymetrics;

import java.util.ArrayList;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import edu.smu.tspell.wordnet.Synset;

import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.ProfilingReport;
import am.app.ontology.profiling.classification.OntologyClassificator;
import am.app.ontology.profiling.ontologymetrics.OntologyEvaluation.Type;
import antlr.collections.impl.Vector;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class CoupleOntologyMetrics implements ProfilingReport {
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
	
	float relationshipRichness2;
	float attributeRichness2;
	float inheritanceRichness2;
	float labelWordnet2;
	float localWordnet2;
	float classRichness2;
	float avgPopulation2;
	float relationshipRichnessInstances2;
	float nullCommentPerc2;
	float nullLabelPerc2;
	float diffLabelLocPerc2;
	float avgDepth2;
	
	
	public CoupleOntologyMetrics(){}
	

	public CoupleOntologyMetrics(OntologyMetrics onto1, OntologyMetrics onto2){
		float[] f = onto1.getAllMetrics();
		float[] f2 = onto2.getAllMetrics();
		
		relationshipRichness = f[0];
		attributeRichness = f[1];
		inheritanceRichness = f[2];
		labelWordnet = f[3];
		localWordnet = f[4];
		classRichness = f[5];
		avgPopulation = f[6];
		relationshipRichnessInstances = f[7];
		nullCommentPerc = f[8];
		nullLabelPerc = f[9];
		diffLabelLocPerc = f[10];
		avgDepth = f[11];
		
		relationshipRichness2 = f2[0];
		attributeRichness2 = f2[1];
		inheritanceRichness2 = f2[2];
		labelWordnet2 = f2[3];
		localWordnet2 = f2[4];
		classRichness2 = f2[5];
		avgPopulation2 = f2[6];
		relationshipRichnessInstances2 = f2[7];
		nullCommentPerc2 = f2[8];
		nullLabelPerc2 = f2[9];
		diffLabelLocPerc2 = f2[10];
		avgDepth2 = f2[11];
		
	}
	
	
	
	
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

	
	public float getRelationshipRichness2() {
		return relationshipRichness2;
	}


	public void setRelationshipRichness2(float relationshipRichness2) {
		this.relationshipRichness2 = relationshipRichness2;
	}


	public float getAttributeRichness2() {
		return attributeRichness2;
	}


	public void setAttributeRichness2(float attributeRichness2) {
		this.attributeRichness2 = attributeRichness2;
	}


	public float getInheritanceRichness2() {
		return inheritanceRichness2;
	}


	public void setInheritanceRichness2(float inheritanceRichness2) {
		this.inheritanceRichness2 = inheritanceRichness2;
	}


	public float getLabelWordnet2() {
		return labelWordnet2;
	}


	public void setLabelWordnet2(float labelWordnet2) {
		this.labelWordnet2 = labelWordnet2;
	}


	public float getLocalWordnet2() {
		return localWordnet2;
	}


	public void setLocalWordnet2(float localWordnet2) {
		this.localWordnet2 = localWordnet2;
	}


	public float getClassRichness2() {
		return classRichness2;
	}


	public void setClassRichness2(float classRichness2) {
		this.classRichness2 = classRichness2;
	}


	public float getAvgPopulation2() {
		return avgPopulation2;
	}


	public void setAvgPopulation2(float avgPopulation2) {
		this.avgPopulation2 = avgPopulation2;
	}


	public float getRelationshipRichnessInstances2() {
		return relationshipRichnessInstances2;
	}


	public void setRelationshipRichnessInstances2(
			float relationshipRichnessInstances2) {
		this.relationshipRichnessInstances2 = relationshipRichnessInstances2;
	}


	public float getNullCommentPerc2() {
		return nullCommentPerc2;
	}


	public void setNullCommentPerc2(float nullCommentPerc2) {
		this.nullCommentPerc2 = nullCommentPerc2;
	}


	public float getNullLabelPerc2() {
		return nullLabelPerc2;
	}


	public void setNullLabelPerc2(float nullLabelPerc2) {
		this.nullLabelPerc2 = nullLabelPerc2;
	}


	public float getDiffLabelLocPerc2() {
		return diffLabelLocPerc2;
	}


	public void setDiffLabelLocPerc2(float diffLabelLocPerc2) {
		this.diffLabelLocPerc2 = diffLabelLocPerc2;
	}


	public float getAvgDepth2() {
		return avgDepth2;
	}


	public void setAvgDepth2(float avgDepth2) {
		this.avgDepth2 = avgDepth2;
	}


	@Override
	public String toString() {
		String ret = "Ontology1:\nrelationshipRichness:" + relationshipRichness +
			"\nattributeRichness:"+attributeRichness + "\ninheritanceRichness:"+ inheritanceRichness + 
			"\nlabelWordnet:" + labelWordnet + "\nlocalWordnet:" +localWordnet + 
			"\nclassRichness:" + classRichness + "\navgPopulation:" + avgPopulation + 
			//"\nrelationshipRichnessInstances:" + relationshipRichnessInstances +
			"\nnullLabelPerc:" + nullLabelPerc + "\ndiffLabelLocPerc:" + diffLabelLocPerc +
			"\nnullCommentPerc:" + nullCommentPerc;
		
		String ret2 = "\nOntology2:\nrelationshipRichness2:" + relationshipRichness2 +
				"\nattributeRichness2:"+attributeRichness2 + "\ninheritanceRichness2:"+ inheritanceRichness2 + 
				"\nlabelWordnet2:" + labelWordnet2 + "\nlocalWordnet2:" +localWordnet2 + 
				"\nclassRichness2:" + classRichness2 + "\navgPopulation2:" + avgPopulation2 + 
				//"\nrelationshipRichnessInstances:" + relationshipRichnessInstances +
				"\nnullLabelPerc2:" + nullLabelPerc2 + "\ndiffLabelLocPerc2:" + diffLabelLocPerc2 +
				"\nnullCommentPerc2:" + nullCommentPerc2;
		return ret + ret2;
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
	
	public float[] getAllMetrics(){
		float[] f = {relationshipRichness,
					 attributeRichness,
			         inheritanceRichness,
			         labelWordnet,
			         localWordnet,
			         classRichness,
			         avgPopulation,
			         relationshipRichnessInstances,
			         nullCommentPerc,
			         nullLabelPerc,
			         diffLabelLocPerc,
			         avgDepth,
	
			         relationshipRichness2,
					 attributeRichness2,
			         inheritanceRichness2,
			         labelWordnet2,
			         localWordnet2,
			         classRichness2,
			         avgPopulation2,
			         relationshipRichnessInstances2,
			         nullCommentPerc2,
			         nullLabelPerc2,
			         diffLabelLocPerc2,
			         avgDepth2,
		        };
		return f;
	}
	
	
	
	
	
}
