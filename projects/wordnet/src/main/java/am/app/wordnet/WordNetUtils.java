package am.app.wordnet;

import edu.smu.tspell.wordnet.api.Synset;
import edu.smu.tspell.wordnet.api.WordNetDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class WordNetUtils {
	
	private static final Logger LOG = LogManager.getLogger(WordNetUtils.class);
	
	private static WordNetDatabase wordNet; 
	
	private HashMap<String, Boolean> isSynonym = new HashMap<>();

	/**
	 * @param amRoot The AgreementMaker root directory, usually [project root]/AM_ROOT
	 */
	public WordNetUtils(File amRoot){
		initWordnet(new File(amRoot.getAbsoluteFile() + File.separator + "wordnet-3.0"));
	}

	/**
	 * @param wordnetDir The directory which contains the WordNet 3.0 dictionary files.
	 *                         The dictionary files are stored in the dict directory of the
	 *                         WordNet 3.0 distribution archive.
	 */
	private void initWordnet(File wordnetDir) {
		if( wordNet != null ) return; // skip the initialization, wordnet has already been initialized

		String canonicalPath;

		try {
			canonicalPath = wordnetDir.getCanonicalPath();
		} catch (IOException ex) {
			RuntimeException rex = new RuntimeException(
					"Cannot find canonical path of WordNet directory.  " +
							"Caught exception: " + ex.getClass().getName() + ": " + ex.getMessage());
			LOG.error(rex);
			throw rex;
		}

		if( !wordnetDir.exists() || !wordnetDir.canRead() ) {
			RuntimeException ex = new RuntimeException(
					"Cannot find WordNet dictionary files. " +
							"Expected the files to exist in this directory: " + canonicalPath);

			LOG.error(ex);
			throw ex;
		}

		System.setProperty("wordnet.database.dir", canonicalPath);

		try {
			wordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) {
			LOG.error("Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetDir, e);
			throw e;
		}
	}

	public boolean areSynonyms(String source, String target) {
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
