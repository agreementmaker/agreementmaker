package evaluation;

import gov.nih.nlm.nls.lvg.Flows.ToExpansions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;

public class NYTEvaluator {
	static String toEvaluate = "alignment.rdf";
	static String reference = "OAEI2011/NYTReference/nyt-dbpedia-people-mappings.rdf";
	
	
	public static void evaluate(String file, String reference) throws Exception{
		ReferenceAlignmentMatcher matcher = new ReferenceAlignmentMatcher();
		
		ReferenceAlignmentParameters param = new ReferenceAlignmentParameters();
		param.fileName = toEvaluate;
		matcher.setParam(param);		
		ArrayList<MatchingPair> filePairs = matcher.parseStandardOAEI();
		
		param.fileName = reference;
		ArrayList<MatchingPair> refPairs = matcher.parseStandardOAEI();
		
		
		//System.out.println("FP:" + filePairs.size());
		//System.out.println("RP:" + refPairs.size());
		
		//System.out.println("FP:" + filePairs.size());
		//System.out.println("RP:" + refPairs.size());
		
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
