package am.app.wordnet;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import edu.smu.tspell.wordnet.api.Synset;
import edu.smu.tspell.wordnet.api.WordNetDatabase;

public class WordNetUtils {
	
	private static final Logger LOG = LogManager.getLogger(WordNetUtils.class);
	
	private static WordNetDatabase wordNet; 
	
	private HashMap<String, Boolean> isSynonym = new HashMap<>();
	
	public WordNetUtils(){
		initWordnet();
	}
	
	private void initWordnet() {
		if( wordNet != null ) return; // skip the initialization, wordnet has already been initialized

		// Initialize the WordNet interface.
		String wordnetDir = "wordnet-3.0";

		File rootDir = new File(wordnetDir);
		if( !rootDir.exists() || !rootDir.canRead() ) {
			String cwd = System.getProperty("user.dir");
			
			String wordnetDir1 = cwd + "/AgreementMaker-OSGi/AM_ROOT/wordnet-3.0";
			String wordnetDir2 = cwd + "/../AgreementMaker-OSGi/AM_ROOT/wordnet-3.0";
			String[] dirs = { wordnetDir1, wordnetDir2 };
			
			for( String currentDir : dirs ) {
				File wndir = new File(currentDir);
				if( wndir.exists() && wndir.canRead() ) {
					wordnetDir = currentDir;
					break;
				}
			}
		}

		System.setProperty("wordnet.database.dir", wordnetDir);
		// Instantiate 
		try {
			wordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) {
			LOG.error("Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetDir, e);
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

		for (Synset sourceSynset : sourceSynsets) {
			for (Synset targetSynset : targetSynsets) {
				if (sourceSynset == targetSynset) {
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
