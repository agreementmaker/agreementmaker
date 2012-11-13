package am.utility;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import am.Utility;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class FromWordNetUtils {
	/**
	 * Displays word forms and definitions for synsets containing the word form
	 * specified on the command line. To use this application, specify the word
	 * form that you wish to view synsets for, as in the following example which
	 * displays all synsets containing the word form "airplane": <br>
	 * java TestJAWS airplane
	 */
	/**
	 * Main entry point. The command-line arguments are concatenated together
	 * (separated by spaces) and used as the word form to look up.
	 * 
	 */
	private static WordNetDatabase wordNet;

	public Set<String> findSynonyms(String wordForm) {
		
		Set<String> synonymSet = new HashSet<String>();
		if(wordForm.isEmpty()){
			return synonymSet;
		}
		NounSynset nounSynset;
		NounSynset[] hyponyms;
//		NounSynset[] hypernyms;

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(wordForm);
		if (synsets.length > 0) {
			for (int i = 0; i < synsets.length; i++) {

				String[] wordForms = synsets[i].getWordForms();

				try{
				nounSynset = (NounSynset) (synsets[i]);
				hyponyms = nounSynset.getHyponyms();
//				hypernyms = nounSynset.getHypernyms();
				for (NounSynset n : hyponyms) {
					for (String s : n.getWordForms()) {
						 synonymSet.add(s.toLowerCase());
					}
				}
//				for (NounSynset n : hypernyms) {
//					for (String s : n.getWordForms()) {
//						synonymSet.add(s.toLowerCase());
//					}
//				}
				}
				catch (Exception e) {
					}
				
				for (String s : wordForms) {
					synonymSet.add(s.toLowerCase());
				}
				

			}
		} else {
//			System.err.println("No synsets exist that contain " + "the word form '" + wordForm + "'");
		}
		return synonymSet;
	}

	public void printSynonyms(String source){
		Set<String> stringSet = findSynonyms(source);
		if(stringSet.isEmpty()){
//			System.out.println("No Synonyms Found!!");
			return;
		}
		Iterator<String> iterator = stringSet.iterator();
		while(iterator.hasNext()){
//			System.out.println(iterator.next());
		}
	}
	
	public boolean areSynonyms(String source, String target) {

		Set<String> sourceSet = findSynonyms(source);
		Set<String> targetSet = findSynonyms(target);

		Iterator<String> iterator = sourceSet.iterator();
		while (iterator.hasNext()) {
			String sourceElement = iterator.next();
			if (targetSet.contains(sourceElement)) {
				return true;
			}
		}
		return false;
	}

	
	
	public FromWordNetUtils() {
		initWordnet();
	}

	private void initWordnet() {
		if (wordNet != null)
			return; // skip the initialization, wordnet has already been
					// initialized

		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");

		String wordnetdir = null;
		String wordnetdir1 = cwd + "/wordnet-3.0";
		String wordnetdir2 = cwd + "/../AgreementMaker/wordnet-3.0";
		String wordnetdir3 = cwd + "/../InformationMatching/wordnet-3.0";
		String[] dirs = { wordnetdir1, wordnetdir2, wordnetdir3 };

		// search through the directories for the wordnet files
		for (String currentDir : dirs) {
			File wndir = new File(currentDir);
			if (wndir.exists() && wndir.canRead()) {
				wordnetdir = currentDir;
				break;
			}
		}

		if (wordnetdir == null) {
			Logger log = Logger.getLogger(FromWordNetUtils.class);
			log.error("Could not find WordNet directory!");
			return;
		}

		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate
		try {
			wordNet = WordNetDatabase.getFileInstance();
		} catch (Exception e) {
			Utility.displayErrorPane(e.getMessage(),
					"Cannot open WordNet files.\nWordNet should be in the following directory:\n"
							+ wordnetdir);
		}
	}

}
