package am.utility.referenceAlignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.dom4j.DocumentException;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluationData;
import am.app.ontology.Ontology;
import am.app.ontology.instance.Instance;

public class AlignmentUtilities {

	public static AlignmentsComparison diff(List<MatchingPair> sourceList, List<MatchingPair> targetList){
		return diff(sourceList, targetList, null, null);
	}
	
	/**
	 * Performs a comparison between two reference alignments. The results are stored in the AlignmentsComparison
	 * data structure
	 */
	public static AlignmentsComparison diff(List<MatchingPair> sourceList, List<MatchingPair> targetList, List<String> inSource, List<String> inTarget){
		MatchingPair source;
		MatchingPair target;
		
		AlignmentsComparison comparison = new AlignmentsComparison();
		
		boolean found = false;
		
		HashSet<MatchingPair> foundTargets = new HashSet<MatchingPair>();
				
		for (int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			found = false;
			for (int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);
				
				if(source.sameSource(target) && source.sameTarget(target)){
					foundTargets.add(target);
					if(source.relation.equals(target.relation)){
						//Right mapping
						found = true;
						comparison.getEqualMappingsInSource().add(source);
						comparison.getEqualMappingsInTarget().add(target);
						break;
					}
					else{
						//right source and target, wrong relation
						comparison.getDifferentRelationInSource().add(source);
						comparison.getDifferentRelationInTarget().add(target);
					}
				}
			}
			if(found == false){
				//wrong mapping
				comparison.getNotInTarget().add(source);
			}
			
			if((inSource != null && !inSource.contains(source.sourceURI)) ||
					inTarget != null && !inTarget.contains(source.targetURI))
				comparison.getNonSolvableSource().add(source);
			
		}
		
		for (int i = 0; i < targetList.size(); i++) {
			target = targetList.get(i);
			if(!foundTargets.contains(target)){
				//This mapping was not found in the source
				comparison.getNotInSource().add(target);

				//Checking if the lists of classes/properties contains the source and target 
				if((inSource != null && !inSource.contains(target.sourceURI)) ||
						inTarget != null && !inTarget.contains(target.targetURI))
					comparison.getNonSolvableSource().add(target);
			}
		}
		
		return comparison;
	}
	
	
	/**
	 * Used in instance matching. Given a reference alignment, a source URI, and a list of candidates, it returns 
	 * the solution to the problem of matching the source, if it is present in the candidates. This is based on 
	 * the notion of solvability, if the solution is not present in the candidates, there's nothing you can do in
	 * the disambiguation phase
	 *  
	 */
	public static String candidatesContainSolution(List<MatchingPair> pairs, String uri, List<Instance> candidates) {
		MatchingPair pair;
		for (int i = 0; i < pairs.size(); i++) {
			pair = pairs.get(i);
			if(pair.sourceURI.equals(uri)){
				
				for (int j = 0; j < candidates.size(); j++) {
					if(candidates.get(j).getUri().equals(pair.targetURI))
						return pair.targetURI;					
				}
			}
		}
		return null;
	}
	
	public static MatchingPair candidatesContainSolution(List<MatchingPair> pairs, String source, String target) {
		MatchingPair pair;
		for (int i = 0; i < pairs.size(); i++) {
			pair = pairs.get(i);
			if(pair.sourceURI.equals(source) && pair.targetURI.equals(target)){
				return pair;
			}
		}
		return null;
	}
	
	/**
	 * Given a filename, opens the file and parses it expecting an alignment in the OAEI format
	 * It returns the alignments in the form of List of MatchingPairs.
	 */
	public static List<MatchingPair> getMatchingPairsOAEI(String filename){
		ReferenceAlignmentMatcher refMatcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = filename;
		refMatcher.setParam(parameters);
		ArrayList<MatchingPair> refPairs;
		try {
			refPairs = refMatcher.parseStandardOAEI();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}
		return refPairs;
	}
	
	public static List<MatchingPair> getMatchingPairsTAB(String filename){
		ReferenceAlignmentMatcher refMatcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = filename;
		refMatcher.setParam(parameters);
		ArrayList<MatchingPair> refPairs = null;
		BufferedReader refBR = null;
		try {
			refBR = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return refPairs;
		}
		try {
			refPairs = refMatcher.parseRefFormat2(refBR);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return refPairs;
	}

	public static Alignment<Mapping> getAlignmentTAB(String filename) {
		ReferenceAlignmentMatcher refMatcher = new ReferenceAlignmentMatcher();
		ReferenceAlignmentParameters parameters = new ReferenceAlignmentParameters();
		parameters.fileName = filename;
		parameters.threshold = 0.01;
		refMatcher.setParam(parameters);
		ArrayList<MatchingPair> refPairs = null;
		BufferedReader refBR = null;
		try {
			refBR = new BufferedReader(new FileReader(filename));
			refPairs = refMatcher.parseRefFormat2(refBR);
			refMatcher.match();
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		return refMatcher.getAlignment();
	}

	public static List<MatchingPair> alignmentToMatchingPairs(
			Alignment<Mapping> alignment) {
		
		//System.out.println("Creating matching pairs from an alignment " + alignment.size());
		
		List<MatchingPair> pairs = new ArrayList<MatchingPair>();
		
		for (Mapping mapping : alignment) {
			//System.out.println(mapping);
			pairs.add(new MatchingPair(mapping.getEntity1().getLocalName(), mapping.getEntity2().getLocalName(), 
					mapping.getSimilarity(), mapping.getRelation()));
		}
		return pairs;
	}
	
	public static ReferenceEvaluationData compare(List<MatchingPair> toEvaluate, List<MatchingPair> reference){
		ReferenceEvaluationData rd = new ReferenceEvaluationData();
		int count = 0;
		MatchingPair p1;
		MatchingPair p2;
		MatchingPair right = null;
		boolean found;
		
		Ontology sOnt = null;
		Ontology tOnt = null;
		
		
		HashSet<MatchingPair> foundTargets = new HashSet<MatchingPair>();
		for (int i = 0; i < toEvaluate.size(); i++) {
			found = false;
			p1 = toEvaluate.get(i);
			for (int j = 0; j < reference.size(); j++) {
				p2 = reference.get(j);
				if(p1.sourceURI.equals(p2.sourceURI)){
					right = p2;
				}
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					count++;
					found = true;
					foundTargets.add(p2);
					break;
				}
			}
		}	
		
		//System.out.println("right mappings: "+count);
		//System.out.println("prec:"+ (float)count/toEvaluate.size() + " rec: " +  (float)count/reference.size());
		
		double prec;
        if(count == 0.0d) {
        	prec = 0.0d;
        }
        else prec = (double) count / (double) toEvaluate.size();
        
        double rec;
        if(reference.size() == 0.0d) {
        	rec = 0.0d;
        }
        else rec = (double) count / (double) reference.size();
        //System.out.println("Precision: " + prec + ", Recall: " + rec);
        // F-measure
        double fm;
        if(prec + rec == 0.0d) {
        	fm = 0.0d;
        }
        //else  fm = (1 + ALPHA) * (prec * rec) / (ALPHA * prec + rec);
        else fm = 2 * (prec * rec) / (prec + rec);  // from Ontology Matching book
        
        System.out.print(prec + "\t" +  rec + "\t");
		
        rd.setPrecision(prec);
        rd.setRecall(rec);
        rd.setFmeasure(fm);
		
		return rd;
	}
	
	public static void removeDuplicates(List<MatchingPair> pairs){
		MatchingPair p1;
		MatchingPair p2;
		for (int i = 0; i < pairs.size(); i++) {
			for (int j = i+1; j < pairs.size(); j++) {
				p1 = pairs.get(i);
				p2 = pairs.get(j);
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					pairs.remove(j);
					//System.out.println(p2.getTabString());
					j--;
				}
					
			}
		}
	}
}
