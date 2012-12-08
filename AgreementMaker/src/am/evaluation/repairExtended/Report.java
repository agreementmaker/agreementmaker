package am.evaluation.repairExtended;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.utility.referenceAlignment.AlignmentUtilities;

/**
 * @author Pavan
 *
 *	Reporting class for repairExtended
 */
public class Report {
	
	private static Logger log = Logger.getLogger(RepairAlignment.class);
	private static String initialMeasures = "Initial F-Measure";
	private static String finalMeasures = "Final F-Measure";
	private static String mappingPrecision = " mappings identified by merged ontology are inconsistent";
	private static String hittingSetPrecision = " mappings identified in the minimum hitting set are inconsistent ";
	
	private ArrayList<MatchingPair> incorrectMappings = new ArrayList<MatchingPair>();
	
	public Double initialMeasure(String toEvaluate, String reference){
		
		log.info(initialMeasures);
		return computeMeasures(toEvaluate,reference);		
	}
	
	public Double finalMeasure(String toEvaluate, String reference){
		
		log.info(finalMeasures);
		return computeMeasures(toEvaluate,reference);		
	}
	
	public void mergePrecision(ArrayList<OWLAxiom> axioms){
		
		ArrayList<MatchingPair> pairs =  getMatchingPairs(axioms);
		log.info(getMatchingPairPrecision(pairs)  + mappingPrecision);
		System.out.println(" ");
	}
	
	public void hittingSetPrecison(ArrayList<OWLAxiom> axioms){
		
		ArrayList<MatchingPair> pairs =  getMatchingPairs(axioms);
		log.info(getMatchingPairPrecision(pairs) + hittingSetPrecision);	
		System.out.println(" ");
	}

	public Double computeMeasures(String toEvaluate, String reference){
		
		int count = 0;
		ArrayList<MatchingPair> filePairs = null;
		ArrayList<MatchingPair> refPairs = null;
		
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();

		matcher.setParameters(param);
		
		try {
			param.fileName = toEvaluate;
			filePairs = matcher.parseStandardOAEI();
			
			param.fileName = reference.toString();
			refPairs = matcher.parseStandardOAEI();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		for (MatchingPair p1 : filePairs) {
			for (MatchingPair p2 : refPairs) {
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					count++;
					break;
				}				
			}
		}
		double precision = (double)count/filePairs.size();
		double recall = (float)count/refPairs.size();
		double fmeasure = 2 * precision * recall / (precision + recall);

		log.info("Precision: " + precision + " Recall: " + recall + " FMeasure: " + fmeasure);
		System.out.println(" ");

		return precision;
	}
	
	public void getIncorrectMappings(File alignmentFile, File reference){
		
		List<MatchingPair> alignment = AlignmentUtilities.getMatchingPairsOAEI(alignmentFile.getAbsolutePath());
		List<MatchingPair> refAlignment = AlignmentUtilities.getMatchingPairsOAEI(reference.getAbsolutePath());
		
		ArrayList<MatchingPair> toremove = new ArrayList<MatchingPair>();
		incorrectMappings = (ArrayList<MatchingPair>) alignment;
		
		for(MatchingPair alignmentPair : alignment ){
			for(MatchingPair refAlignmentPair : refAlignment){
				
				alignmentPair.similarity = 0.0;
				refAlignmentPair.similarity = 0.0;
				alignmentPair.relation = null;
				refAlignmentPair.relation = null;
				
				if(alignmentPair.equals(refAlignmentPair)){
					toremove.add(alignmentPair);
					//incorrectMappings.remove(alignmentPair);					
				}				
			}
		}
		
		incorrectMappings.removeAll(toremove);
	}
	
	private ArrayList<MatchingPair> getMatchingPairs(ArrayList<OWLAxiom> axioms) {
		
		ArrayList<MatchingPair> pairs = new ArrayList<MatchingPair>();
		
		for(OWLAxiom axiom : axioms){
			
			OWLClass sourceClass = new ArrayList<OWLClass>(axiom.getClassesInSignature()).get(0);
			OWLClass targetClass = new ArrayList<OWLClass>(axiom.getClassesInSignature()).get(1);
						
			pairs.add(new MatchingPair(targetClass.getIRI().toURI().toString()
					,sourceClass.getIRI().toURI().toString(),0.0,null));
		}
		
		return pairs;
	}
	
	private String getMatchingPairPrecision(ArrayList<MatchingPair> pairs) {
		
		Integer count = 0;
		pairs = removeDuplicates(pairs);
		
		for(MatchingPair incorrectPair : removeDuplicates(incorrectMappings)){
			for(MatchingPair match : pairs){
										
				if(incorrectPair.equals(match)){
					count++;
					//log.info(match);
				}				
			}
		}
		
		return count + "/" + pairs.size();
	}
	
	public <T> void printMUPS(ArrayList<T> list){
		
		//ArrayList<T> distinctList = new ArrayList<T>();
		
		for(T p : list){
			log.info(p);
		}	
	}
	
	private <T> ArrayList<T> removeDuplicates(ArrayList<T> list){
		
		ArrayList<T> distinctList = new ArrayList<T>();
		
		for(T p : list){
			if(!distinctList.contains(p))
				distinctList.add(p);
		}
		
		return distinctList;
	}
	
	//prints the complete list of conflict sets - DEBUG
	public void printConflictSetList(ConflictSetList conflictSetList){
			
		for(ConflictSet set : conflictSetList.getConflictSets()){
				
			log.info("--class - " + set.getInconsistentClass());
				
			for(AxiomRank ar : set.getAxiomList()){
				log.info("-----axiom - " + ar.getAxiom() + " (rank - " + ar.getRank() + " )");
			}
		}			
	}
	
	//prints the complete list of axioms and respective ranks - DEBUG
	//TODO - Should make the list distinct
	public void printAxiomRanks(ConflictSetList conflictSetList){

		ArrayList<AxiomRank> ranks = removeDuplicates(conflictSetList.getAxiomRankList());
		
		for(AxiomRank ar : ranks){
			log.info(ar.getAxiom() + " -- " + ar.getRank());
		}		
	}
}
