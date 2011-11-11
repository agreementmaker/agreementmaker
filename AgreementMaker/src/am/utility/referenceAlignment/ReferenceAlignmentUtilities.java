package am.utility.referenceAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.dom4j.DocumentException;

import am.app.mappingEngine.LinkedOpenData.LODOntology;
import am.app.mappingEngine.LinkedOpenData.LODUtils;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.instance.Instance;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class ReferenceAlignmentUtilities {

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
	
	
	
}
