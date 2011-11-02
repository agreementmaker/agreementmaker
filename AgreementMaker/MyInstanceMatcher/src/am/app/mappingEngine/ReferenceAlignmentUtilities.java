package am.app.mappingEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.instance.Instance;

public class ReferenceAlignmentUtilities {

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
	
	public static List<MatchingPair> getMatchingPairs(String filename){
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
