package am.app.mappingEngine;

import java.util.List;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
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
}
