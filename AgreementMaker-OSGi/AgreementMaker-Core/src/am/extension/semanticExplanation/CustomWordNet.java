package am.extension.semanticExplanation;

import java.util.ArrayList;

import am.utility.WordNetUtils;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class CustomWordNet {
	
	private static WordNetUtils wordNetUtils;
	private static WordNetDatabase wordNet;

	public CustomWordNet() {
		wordNetUtils = new WordNetUtils();
		wordNet = wordNetUtils.getWordNet();
	}
	
	public ArrayList<String> getSynonyms(String word) {
		ArrayList<String> synonymList = new ArrayList<String>();
		if(word != null) {
			Synset[] wordSynsets = wordNet.getSynsets(word);
			for(Synset s:wordSynsets) {
				for(String str:s.getWordForms()) {
					if(!str.equals(word)) {
						synonymList.add(str);
					}
				}
			}
		}
		return synonymList;
	}
	
	public ArrayList<String> getHypernyms(String word) {
		NounSynset[] hypernymNounSynset;
		VerbSynset[] hypernymVerbSynset;
		ArrayList<String> hypernymList = new ArrayList<String>();
		if(word != null) {
			Synset[] wordSynsets = wordNet.getSynsets(word);
			for(Synset s: wordSynsets) {
				hypernymNounSynset = ((NounSynset) s).getHypernyms();
				hypernymVerbSynset = ((VerbSynset) s).getHypernyms();
				for(NounSynset ns: hypernymNounSynset) {
					for(String str: ns.getWordForms()) {
						hypernymList.add(str);
					}
				}
				for(VerbSynset vs: hypernymVerbSynset) {
					for(String str: vs.getWordForms()) {
						hypernymList.add(str);
					}
				}
			}
		}	
		return hypernymList;
	}
	
	public ArrayList<String> getHyponyms(String word) {
		NounSynset[] hyponymNounSynset;
		ArrayList<String> hyponymList = new ArrayList<String>();
		if(word != null) {
			Synset[] wordSynsets = wordNet.getSynsets(word);
			for(Synset s: wordSynsets) {
				hyponymNounSynset = ((NounSynset) s).getHyponyms();
				for(NounSynset ns: hyponymNounSynset) {
					for(String str: ns.getWordForms()) {
						hyponymList.add(str);
					}
				}
			}
		}
		
		return hyponymList;
	}
	
	
	
	//For Future use
	
/*	public ArrayList<String> getSynonymsOfSynonyms(ArrayList<String> synonymList) {
		ArrayList<String> adjSynonymList = new ArrayList<String>();
		for(String synonym:synonymList) {
			
			Synset[] wordSynsets = wordNet.getSynsets(word);
			for(Synset s:wordSynsets) {
				for(String str:s.getWordForms()) {
					if(!str.equals(word)) {
						adjSynonymList.add(str);
					}
				}
			}
		}
		return adjSynonymList;
	}*/
	
	
	public static void main(String args[]) {
		CustomWordNet csw = new CustomWordNet();
		csw.getSynonyms("look");
	}
	
}
