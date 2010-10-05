package am.app.mappingEngine.StringUtil;

import java.util.HashMap;
import java.util.HashSet;

import am.app.Core;

public class Normalizer {
	
	private HashMap<String, String> normalizeMap;
	private HashSet<String> stopWords;
	private NormalizerParameter param;
	private PorterStemmer ps;
	
	public Normalizer(NormalizerParameter np) {
		param = np;
		initNormalizeMap();
		initStopWordsSet();
		ps = new PorterStemmer();
	}
	
	
	
	
	// THERE IS A TEST CLASS IN THE TEST PACKAGE TO TEST THIS METHOD
	//Lowercasing, normalization, stemming and stopwords removing
	public String normalize(String s) {
		NormalizerParameter parameters = (NormalizerParameter)param;
		char[] chars = s.toCharArray();
		String newString = ""; //the processed string to be returned at the end
		String currentChar = null; //the real char in the string
		String currentWord = ""; //the newString will filled word by word. each processed word will be added if valid (no stopwords and so on)
		char c;
		
		boolean endOfWord;
		
		for(int i = 0 ; i < chars.length; i++) {	
			endOfWord = false;
			//new word when we find an uppercase char
			if(parameters.normalizeBlank && Character.isUpperCase(chars[i]))
				endOfWord = true;
			//lowercase a sequence of uppercase chars so that they are not going to be considered new words (e.g. myHOME and myHome both are only 2 words)
			for(int j = i; j < chars.length && Character.isUpperCase(chars[j]); j++) { //lowercase each sequence of uppercase char, the first uppercase char of the sequence tells me that i'm at the end of a word
				chars[j] = Character.toLowerCase(chars[j]);
			}
			
			//normalization of char
			c = chars[i];
			currentChar = normalizeMap.get(""+c); 
			if(currentChar == null) //nothing has been found in the map so i had to keep the character of the string. no replacing
				currentChar = ""+c;
			
			//we are at the end of a word if we found a blank or an uppercase char or if we are at the end of the string
			if(currentChar.equals(" ")) {
				endOfWord = true;
				currentChar = ""; //spaces are added manually so the currentchar to be added is empty
			}
			else if(i == chars.length-1) { //we are at the last iteration ,so we have to add also this char to the currentword because there won't be any other word
				endOfWord = true;
				currentWord += currentChar;
			}
			
			if(endOfWord) {
				if(currentWord.length()>0 && !currentWord.equals(" ")) {
					if(!parameters.removeStopWords || !stopWords.contains(currentWord)) { // if we pass this test it means that the word has to be added to the final processed string
						if(parameters.stem) {
							try {
								String beforeStem = null;
								if( Core.DEBUG_NORMALIZER ) { beforeStem = currentWord; }
								currentWord = ps.stem(currentWord);
								if( Core.DEBUG_NORMALIZER ) System.out.println("Stemmed word: " + beforeStem + " -> " + currentWord);
							}
							catch(Exception ex) {
								System.out.println("Error stemming this word: "+currentWord);
								ex.printStackTrace();
							}
						}
						//add the word to the string
						if(newString.length()>0) // if is not the first word we put a blank
							newString+= " ";
						newString += currentWord; //Attention this is the last word, it doesnt contain the new current char, it does always have a blank in the beginning unless is the first word
					}
				}
				currentWord = ""; //we just visited a new word so we have to reinit currentword in all cases even if we added it or not
			}
			
			//in all cases we add the new char to the current word, both current word and char may be just "", 
			currentWord += currentChar; // if this is the beginning of a new word, currentchar will have a blank as first char, unless is the first word
		}
		
		return newString;
	}
	
	
	/* *******************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */


	private void initNormalizeMap() {
		NormalizerParameter parameters  = (NormalizerParameter)param;
		normalizeMap = new HashMap<String, String>();
		//char to be always replaced
		String emptyString = "";
		String blank = " ";
		
		if(parameters.normalizeBlank) {
			normalizeMap.put("-", blank);
			normalizeMap.put("_", blank);
			normalizeMap.put("\n", blank);
			normalizeMap.put("\t", blank);
		}
		
		if(parameters.normalizeDiacritics) {
			normalizeMap.put("�", "o");
			normalizeMap.put("�", "a");
			normalizeMap.put("�", "u");
			normalizeMap.put("�", "e");
			normalizeMap.put("�", "e");
			normalizeMap.put("�", "i");
		}
		
		if(parameters.normalizePunctuation) {
			normalizeMap.put("!", blank);
			normalizeMap.put("?", blank);
			normalizeMap.put(".", blank);
			normalizeMap.put(",", blank);
			normalizeMap.put(":", blank);
			normalizeMap.put(";", blank);
			normalizeMap.put("\"", blank); // "
			normalizeMap.put(" ' ",blank); // '
			normalizeMap.put("/", blank);
			normalizeMap.put("\\", blank);
		}
		
		if(parameters.normalizeDigit) {
			normalizeMap.put("1", emptyString);
			normalizeMap.put("2", emptyString);
			normalizeMap.put("3", emptyString);
			normalizeMap.put("4", emptyString);
			normalizeMap.put("5", emptyString);
			normalizeMap.put("6", emptyString);
			normalizeMap.put("7", emptyString);
			normalizeMap.put("8", emptyString);
			normalizeMap.put("0", emptyString);
		}
		
		//i don't care about \ / | & ( )[] {}  > < = + *  #  ^ �  �  �
		
		
		
	}

	private void initStopWordsSet() {
		stopWords = new HashSet<String>();
		
		if(((NormalizerParameter)param).removeStopWords) {
			
			//STOP WORDS SHOULD BE MEANING LESS
			stopWords.add("a");
			stopWords.add("an");
			stopWords.add("as");
			stopWords.add("at");
			stopWords.add("by");
			stopWords.add("about");
			stopWords.add("as");
			stopWords.add("for");
			stopWords.add("from");
//			stopWords.add("in"); //this is risky because of in out meaning in ontology words
			stopWords.add("of");
//			stopWords.add("on"); // this is also risky for on/off meaning like onCampus offCampus
			stopWords.add("or");
			stopWords.add("and");
			stopWords.add("&");
			stopWords.add("the");
			stopWords.add("to");
			stopWords.add("@en"); //this is in any comment
			
			/* Other stopwords commnly eliminated in web searching, not very useful here. TODO a detailed check should be done
			stopWords.add("I");
			stopWords.add("are");
			stopWords.add("be");
			stopWords.add("com");
			stopWords.add("de");
			stopWords.add("en");
			stopWords.add("how");
			stopWords.add("is");
			stopWords.add("it");
			stopWords.add("Ia");
			stopWords.add("that");
			stopWords.add("this");
			stopWords.add("who");
			stopWords.add("what");
			stopWords.add("was");
			stopWords.add("when");
			stopWords.add("where");
			stopWords.add("will");
			stopWords.add("with");
			stopWords.add("und");
			stopWords.add("www");
			*/
		}
	}
}
