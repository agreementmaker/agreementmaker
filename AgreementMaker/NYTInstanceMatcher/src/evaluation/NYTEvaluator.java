package evaluation;

import java.util.ArrayList;

import misc.NYTConstants;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;

public class NYTEvaluator {
	static String toEvaluate = "alignment.rdf";
	static String reference = NYTConstants.REF_FREEBASE_ORGANIZATION;
	
	
	public static void evaluate(String file, String reference) throws Exception{
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();
		param.fileName = toEvaluate;
		matcher.setParam(param);		
		ArrayList<MatchingPair> filePairs = matcher.parseStandardOAEI();
		
		param.fileName = reference;
		ArrayList<MatchingPair> refPairs = matcher.parseStandardOAEI();
		
		compare(filePairs, refPairs);	
		
		System.out.println();
	}
	
	public static void compare(ArrayList<MatchingPair> toEvaluate, ArrayList<MatchingPair> reference){
		int count = 0;
		MatchingPair p1;
		MatchingPair p2;
		
		for (int i = 0; i < toEvaluate.size(); i++) {
			p1 = toEvaluate.get(i);
			for (int j = 0; j < reference.size(); j++) {
				p2 = reference.get(j);
				//System.out.println(p2.getTabString());
				if(p1.sourceURI.equals(p2.sourceURI) && p1.targetURI.equals(p2.targetURI)
						&& p1.relation.equals(p2.relation)){
					count++;
					break;
				}
			}
		}	
		//System.out.println("right mappings: "+count);
		//System.out.println("prec:"+ (float)count/toEvaluate.size() + " rec: " +  (float)count/reference.size());
		System.out.print((float)count/toEvaluate.size() + "\t" +  (float)count/reference.size() + "\t");
	}
	
	public static void main(String[] args) throws Exception {
		evaluate(toEvaluate, reference);
	}
}
