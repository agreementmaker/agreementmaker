package am.utility;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetUtils {
	
	private static final Logger LOG = LogManager.getLogger(WordNetUtils.class);
	
	private static WordNetDatabase wordNet; 
	
	HashMap<String, Boolean> isSynonym = new HashMap<String, Boolean>();
	
	public WordNetUtils(){
		initWordnet();
	}
	
	private void initWordnet() {
		if( wordNet != null ) return; // skip the initialization, wordnet has already been initialized
		
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		
		String wordnetdir = null;
		String wordnetdir1 = cwd + "/wordnet-3.0";
		String wordnetdir2 = cwd + "/../AgreementMaker/wordnet-3.0";
		String wordnetdir3 = cwd + "/../InformationMatching/wordnet-3.0";
		String[] dirs = { wordnetdir1, wordnetdir2, wordnetdir3 };
		
		// search through the directories for the wordnet files
		for( String currentDir : dirs ) {
			File wndir = new File(currentDir);
			if( wndir.exists() && wndir.canRead() ) {
				wordnetdir = currentDir;
				break;
			}
		}

		if( wordnetdir == null ) {
			Logger log = Logger.getLogger(WordNetUtils.class);
			log.error("Could not find WordNet directory!");
			return;
		}
		
		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate 
		try {
			wordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) {
			LOG.error("Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir, e);
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
