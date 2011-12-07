package am.app.feedback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.userInterface.MatchingProgressDisplay;

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
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(sourceOntology.getClassesList(),classesMatrix, Ontology.SOURCE, alignType.aligningClasses));
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(sourceOntology.getPropertiesList(),propertiesMatrix, Ontology.SOURCE, alignType.aligningProperties));
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(targetOntology.getClassesList(),classesMatrix, Ontology.TARGET, alignType.aligningClasses));
		allNonFilteredConcepts.addAll(getNonFilteredCandidateConcepts(targetOntology.getPropertiesList(),propertiesMatrix, Ontology.TARGET, alignType.aligningProperties));
		
		// we now have the masterList, sort it.
		Collections.sort( allNonFilteredConcepts, Collections.reverseOrder() );  
		//Collections.shuffle(allNonFilteredConcepts);
		
		System.out.println("");
		System.out.println("***** The MASTER list:");
		for( MatchingProgressDisplay mpd : fbL.getProgressDisplays() ) mpd.appendToReport("***** The MASTER list (similarity based):");
		Iterator<CandidateConcept> ccitr = allNonFilteredConcepts.iterator();
		while( ccitr.hasNext() ) {
			CandidateConcept currentCandidate = ccitr.next();
			for( MatchingProgressDisplay mpd : fbL.getProgressDisplays() ) mpd.appendToReport("\t* " + currentCandidate.toString());
			System.out.println( "\t* " + currentCandidate.toString() );
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
		
		for( MatchingProgressDisplay mpd : fbL.getProgressDisplays() ) mpd.appendToReport("***** topK (similarity based):");
		for( int i = 0; i < topK.size(); i++ ) {
			for( MatchingProgressDisplay mpd : fbL.getProgressDisplays() ) mpd.appendToReport("\t"+ i + "." + topK.get(i).toString());
		}
		
		return topK;
	}

	private List<CandidateConcept> getNonFilteredCandidateConcepts(
			List<Node> list, FilteredAlignmentMatrix matrix,
			int whichOntology, alignType whichType) {

		FeedbackLoopParameters param = (FeedbackLoopParameters)fbL.getParam();
		double lowTH = param.lowThreshold;
		int M = param.M;
		List<CandidateConcept> result = new ArrayList<CandidateConcept>();
		CandidateConcept c;
		Mapping a;
		for(int i = 0; i < list.size(); i++){
			Mapping[] candidateCells = null;
			if(whichOntology == Ontology.SOURCE){ 
				if(!matrix.isRowFiltered(i)){
					candidateCells = matrix.getRowMaxValues(i, M);
				}
			}
			else if(whichOntology == Ontology.TARGET){ 
				if(!matrix.isColFiltered(i)){
					candidateCells = matrix.getColMaxValues(i, M);
				}
			}
			if(candidateCells != null && candidateCells.length > 0){
				ArrayList<Mapping> candidateMappings = new ArrayList<Mapping>();
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
