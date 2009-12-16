package am.app.feedback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import am.app.Core;
import am.app.feedback.CandidateConcept.ontology;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class SimilarityBasedCandidateSelection extends CandidateSelection {
	
	public SimilarityBasedCandidateSelection(FeedbackLoop ufl){
		super(ufl, ALLMEASURES);//the single measure string is irrelevant here
	}
	
	public ArrayList<CandidateConcept> getCandidateAlignments( int k, int m ) {
		FilteredAlignmentMatrix classesMatrix = (FilteredAlignmentMatrix)fbL.getClassesMatrix();
		FilteredAlignmentMatrix propertiesMatrix = (FilteredAlignmentMatrix)fbL.getPropertiesMatrix();
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();
		ArrayList<CandidateConcept> allNonFilteredConcepts = new ArrayList<CandidateConcept>();
		
		//Get all non filtered candidate concepts with their candidate mappings
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(sourceOntology.getClassesList(),classesMatrix, ontology.source, alignType.aligningClasses));
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(sourceOntology.getPropertiesList(),propertiesMatrix, ontology.source, alignType.aligningProperties));
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(targetOntology.getClassesList(),classesMatrix, ontology.target, alignType.aligningClasses));
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(targetOntology.getPropertiesList(),propertiesMatrix, ontology.target, alignType.aligningProperties));
		
		// we now have the masterList, sort it.
		Collections.sort( allNonFilteredConcepts, Collections.reverseOrder() );  
		
		System.out.println("");
		System.out.println("***** The MASTER list:");
		Iterator<CandidateConcept> ccitr = allNonFilteredConcepts.iterator();
		while( ccitr.hasNext() ) {
			System.out.println( "\t* " + ccitr.next().toString() );
		}
		
		//add the topK with at least one mapping to the result
		ArrayList<CandidateConcept> topK = new ArrayList<CandidateConcept>();
		Iterator<CandidateConcept> it = allNonFilteredConcepts.iterator();
		while(it.hasNext() && topK.size() < k){
			CandidateConcept top1 = it.next();
			if(top1.getCandidateMappings() != null && top1.getCandidateMappings().size() > 0){//this should always be true considering the way we build the list in getNonFilteredCandidateConcepts
				topK.add(top1);
			}
		}
		return topK;
	}

	private ArrayList<CandidateConcept> getNonFilteredCandidateConcepts(
			ArrayList<Node> list, FilteredAlignmentMatrix matrix,
			ontology whichOntology, alignType whichType) {

		FeedbackLoopParameters param = (FeedbackLoopParameters)fbL.getParam();
		double lowTH = param.lowThreshold;
		int M = param.M;
		ArrayList<CandidateConcept> result = new ArrayList<CandidateConcept>();
		CandidateConcept c;
		Alignment a;
		for(int i = 0; i < list.size(); i++){
			Alignment[] candidateCells = null;
			if(whichOntology == ontology.source){ 
				if(!matrix.isRowFiltered(i)){
					candidateCells = matrix.getRowMaxValues(i, M);
				}
			}
			else if(whichOntology == ontology.target){ 
				if(!matrix.isColFiltered(i)){
					candidateCells = matrix.getColMaxValues(i, M);
				}
			}
			if(candidateCells != null && candidateCells.length > 0){
				ArrayList<Alignment> candidateMappings = new ArrayList<Alignment>();
				double relevance = 0;//is the sum of the similarities of the top K mappings between hTH and lTH
				for(int j = 0; j < candidateCells.length; j++){
					a = candidateCells[j];
					if(a != null && a.getSimilarity() >= lowTH){
						candidateMappings.add(a);
						relevance += a.getSimilarity();
					}
				}
				if(candidateMappings.size() > 0){
					c = new CandidateConcept(list.get(i), relevance, whichOntology, whichType);
					c.setCandidateMappings(candidateMappings);
					result.add(c);
				}
			}
		}
		return result;
	}

}
