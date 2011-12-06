package am.utility;

import java.util.HashMap;

import am.Utility;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetUtils {
	private WordNetDatabase wordNet; 
	
	HashMap<String, Boolean> isSynonym = new HashMap<String, Boolean>();
	
	public WordNetUtils(){
		initWordnet();
	}
	
	private void initWordnet() {
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate 
		try {
			wordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) {
			Utility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
	}

	public boolean areSynonyms(String source, String target) {
//		if(true)
//		return false;
//		
		String key = source + ":" + target;
		Boolean answer = isSynonym.get(key);
		if(answer != null) return answer;
		
		Synset[] sourceSynsets = wordNet.getSynsets(source);
		Synset[] targetSynsets = wordNet.getSynsets(target);
		
		for (int i = 0; i < sourceSynsets.length; i++) {
			for (int j = 0; j < targetSynsets.length; j++) {
				if(sourceSynsets[i] == targetSynsets[j]){
					//System.out.println(source + " " + target + " synonyms!!");
					isSynonym.put(key, true);
					return true;
				}
			}
		}
		isSynonym.put(key, false);
		return false;
	}
	
	public WordNetDatabase getWordNet() {
		return wordNet;
	}
}
