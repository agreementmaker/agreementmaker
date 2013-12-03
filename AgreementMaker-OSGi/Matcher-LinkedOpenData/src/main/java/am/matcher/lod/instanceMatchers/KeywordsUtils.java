package am.matcher.lod.instanceMatchers;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.StringUtil.Normalizer;
import am.utility.EnglishUtility;

public class KeywordsUtils {
	
	public static List<String> processKeywords(List<String> list) {
		List<String> retValue = new ArrayList<String>();
		
		char[] charBlackList = { ',', '(' , ')', '{', '}', '.'};
		
		String toProcess;
		String curr;
		String[] split;
		for (int i = 0; i < list.size(); i++) {
			toProcess = list.get(i).toLowerCase();
			
			for (int j = 0; j < charBlackList.length; j++) {
				toProcess = toProcess.replace(charBlackList[j], ' ');
			}
			
			split = toProcess.split("\\s");
			
			for (int j = 0; j < split.length; j++) {
				curr = split[j];
				if(curr.isEmpty()) continue;
				
				if(EnglishUtility.isStopword(curr)) continue;
				
				if(!retValue.contains(curr.trim()))
					retValue.add(curr.trim());
			}
		}
		return retValue;
	}
	
	/**
	 * Given a list of strings and a normalizer, 
	 * normalizes the strings, removes stopwords, tokenizes
	 * and puts all the tokens in the same string  
	 */
	public static List<String> processKeywords(List<String> list, Normalizer normalizer) {
		List<String> retValue = new ArrayList<String>();
		String string;
		String curr;
		String[] split;
		for (int i = 0; i < list.size(); i++) {
			string = list.get(i).toLowerCase();
			
			if(normalizer != null)
				string = normalizer.normalize(string);		
			
			split = string.split("\\s");
			
			for (int j = 0; j < split.length; j++) {
				curr = split[j].trim();
				if(curr.isEmpty()) continue;
				if(EnglishUtility.isStopword(curr)) continue;
				if(!retValue.contains(curr))
					retValue.add(curr);
			}
		}
		return retValue;
	}

}
