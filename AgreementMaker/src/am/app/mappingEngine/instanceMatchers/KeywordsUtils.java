package am.app.mappingEngine.instanceMatchers;

import java.util.ArrayList;
import java.util.List;

import am.utility.EnglishUtility;

public class KeywordsUtils {
	
	public static List<String> processKeywords(List<String> list) {
		List<String> retValue = new ArrayList<String>();
		
		char[] charBlackList = { ',', '(' , ')', '{', '}', '.'};
		
		String toProcess;
		String curr;
		String[] splitted;
		for (int i = 0; i < list.size(); i++) {
			toProcess = list.get(i).toLowerCase();
			
			for (int j = 0; j < charBlackList.length; j++) {
				toProcess = toProcess.replace(charBlackList[j], ' ');
			}
			
			splitted = toProcess.split(" ");
			
			for (int j = 0; j < splitted.length; j++) {
				curr = splitted[j];
				if(curr.isEmpty()) continue;
				
				if(EnglishUtility.isStopword(curr)) continue;
				
				if(!retValue.contains(curr.trim()))
					retValue.add(curr.trim());
			}
		}
		return retValue;
	}

}
