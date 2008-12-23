package agreementMaker.application.mappingEngine.parametricStringMatcher;

import java.util.HashMap;
import java.util.HashSet;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.stemmer.PorterStemmer;
import agreementMaker.application.ontology.Node;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*; //all sim metrics are in here

public class ParametricStringMatcher extends AbstractMatcher { 


	private HashMap<String, String> normalizeMap;
	private HashSet<String> stopWords;
	
	public ParametricStringMatcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
		parametersPanel = new ParametricStringParametersPanel();
	}
	
	
	public String getDescriptionString() {
		return "Performs a local matching using a String Based technique.\n" +
				"Different concept strings are considered in the process.\n" +
				"The user can select a different weight to each concept string\n" +
				"Strings are preprocessed with cleaning, stemming, stop-words removing, and tokenization techniques.\n" +
				"Users can also select preprocessing preferences.\n" +
				"Different String similarity techniques are available to compare preprocessed strings.\n" +
				"A similarity matrix contains the similarity between each pair (sourceNode, targetNode).\n" +
				"A selection algorithm select valid alignments considering threshold and number of relations per node.\n"; 
	}
	
	
	
	/* *******************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		initNormalizeMap();
		initStopWordsSet();
	}


	private void initNormalizeMap() {
		ParametricStringParameters parameters  = (ParametricStringParameters)param;
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
		
		if(((ParametricStringParameters)param).removeStopWords) {
			
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
	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */

	public Alignment alignTwoNodes(Node source, Node target) {
		double localSim = 0;
		double labelSim = 0;
		double commentSim = 0;
		double seeAlsoSim = 0;
		double isDefBySim = 0;
		double sim = 0;
		ParametricStringParameters parameters  = (ParametricStringParameters)param;
		//i need to use local varables for weights  to modify them in case of weights redistribution but without modifying global parameters
		double localWeight = parameters.localWeight;
		double labelWeight = parameters.labelWeight;
		double commentWeight = parameters.commentWeight; 
		double seeAlsoWeight = parameters.seeAlsoWeight; 
		double isDefinedByWeight = parameters.isDefinedByWeight; 
		
		//The redistrubution is implicit in the weighted average mathematical formula
		//i just need to put the weight equal to 0
		if(parameters.redistributeWeights) {
			//if parameters.weight == 0 is needed to speed up the if, in fact often many weights are already 0 and is useless to check other boolean value
			if(parameters.localWeight == 0 || Utility.isIrrelevant(source.getLocalName()) || Utility.isIrrelevant(target.getLocalName()))
				localWeight = 0;
			if(parameters.labelWeight == 0 || Utility.isIrrelevant(source.getLabel()) || Utility.isIrrelevant(target.getLabel()))
				labelWeight = 0;
			if(parameters.commentWeight == 0 || Utility.isIrrelevant(source.getComment()) || Utility.isIrrelevant(target.getComment()))
				commentWeight = 0;
			if(parameters.seeAlsoWeight == 0 || Utility.isIrrelevant(source.getSeeAlso()) || Utility.isIrrelevant(target.getSeeAlso()))
				seeAlsoWeight = 0;
			if(parameters.isDefinedByWeight == 0 || Utility.isIrrelevant(source.getIsDefinedBy()) || Utility.isIrrelevant(target.getIsDefinedBy()))
				isDefinedByWeight = 0;			
		}
		
		double totWeight = localWeight + labelWeight + commentWeight + seeAlsoWeight + isDefinedByWeight; //important to get total after the redistribution
		if(totWeight > 0) {
			if(localWeight > 0) {
				localSim =  performStringSimilarity(source.getLocalName(), target.getLocalName());
				localSim *= localWeight;
			}
			if(labelWeight > 0) {
				labelSim =  performStringSimilarity(source.getLabel(), target.getLabel());
				labelSim *= labelWeight;
			}
			if(commentWeight > 0) {
				commentSim = performStringSimilarity(source.getComment(), target.getComment());
				commentSim *= commentWeight;
			}
			if(seeAlsoWeight > 0) {
				seeAlsoSim = performStringSimilarity(source.getSeeAlso(), target.getSeeAlso());
				seeAlsoSim *= seeAlsoWeight;
			}
			if(isDefinedByWeight > 0) {
				isDefBySim = performStringSimilarity(source.getIsDefinedBy(), target.getIsDefinedBy());
				isDefBySim *= isDefinedByWeight;
			}
			
			sim = localSim + labelSim + commentSim + seeAlsoSim + isDefBySim;
			//Weighted average, this normalize everything so also if the sum of  weights is not one, the value is always between 0 and 1. 
			//this also automatically redistribute 0 weights.
			sim /= totWeight; 
		}
		
		return new Alignment(source, target, sim);
		
	}
	
	
	private double performStringSimilarity(String sourceString, String targetString) {

		double sim = 0;
		if(sourceString == null || targetString == null )
			return 0; //this should never happen because we set string to empty string always
		
		else { //real string comparison
			ParametricStringParameters parameters  = (ParametricStringParameters)param;
			
			//PREPROCESSING
			String processedSource = preProcessString(sourceString);
			String processedTarget = preProcessString(targetString);
			
			//usually empty strings shouldn't be compared, but if redistrubute weights is not selected 
			//in the redistribute weights case this can't happen because the code won't arrive till here
			if(processedSource.equals("")) 
				if(processedTarget.equals(""))
					return 1;
				else return 0;
			else if(processedTarget.equals(""))
				return 0;
			
			//this could be done with registry enumeration techinque but is not worth it
			if(parameters.measure.equals(ParametricStringParameters.AMSUB)) {
				//TODO this should be our string metric
				throw new RuntimeException("Not implemented yet");
			}
			else if(parameters.measure.equals(ParametricStringParameters.EDIT)) {
				Levenshtein lv = new Levenshtein();
				sim = lv.getSimilarity(processedSource, processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.JARO)) {
				JaroWinkler jv = new JaroWinkler();
				sim =jv.getSimilarity(processedSource, processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.QGRAM)) {
				QGramsDistance q = new QGramsDistance();
				sim = q.getSimilarity(processedSource, processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.SUB)) {
				sim = AMStringMetrics.substringScore(processedSource,processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.COMBINED)){
				//TODO this should be an average between some of the above, maybe edit and AMSUB when is ready.
				throw new RuntimeException("Not implemented yet");
			}
			
		}
		return sim;
	}


	
	// THERE IS A TEST CLASS IN THE TEST PACKAGE TO TEST THIS METHOD
	//Lowercasing, normalization, stemming and stopwords removing
	public String preProcessString(String s) {
		ParametricStringParameters parameters = (ParametricStringParameters)param;
		char[] chars = s.toCharArray();
		String newString = ""; //the processed string to be returned at the end
		String currentChar; //the real char in the string
		String currentWord = ""; //the newString will filled word by word. each processed word will be added if valid (no stopwords and so on)
		char c;
		PorterStemmer ps = new PorterStemmer();
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
								currentWord = ps.stem(currentWord);
							}
							catch(Exception ex) {
								System.out.println("Can't stem this word: "+currentWord);
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

	      
}

