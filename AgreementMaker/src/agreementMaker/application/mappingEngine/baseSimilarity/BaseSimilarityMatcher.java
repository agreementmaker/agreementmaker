package agreementMaker.application.mappingEngine.baseSimilarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.stemmer.PorterStemmer;
import agreementMaker.application.ontology.Node;


public class BaseSimilarityMatcher extends AbstractMatcher { 


	// JAWS WordNet interface
	private WordNetDatabase wordnet  = null;
	
	
	public BaseSimilarityMatcher(int n, String s) {
		// warning, param is not available at the time of the constructor
		
		super(n, s);
		needsParam = true;
		
		
		parametersPanel = new BaseSimilarityMatcherParametersPanel();
		
	}


	public BaseSimilarityMatcherParametersPanel getParametersPanel() {
		return (BaseSimilarityMatcherParametersPanel) parametersPanel;
	}
	
	
	
	
	
	/* *******************************************************************************************************
	 * Algorithm functions beyond this point
	 * **********************************************************************************************************
	 */
	
	
	
	/*
	 * This function does the main base similarity algorithm.
	 */
	public Alignment alignTwoNodes(Node source, Node target) {
		
		String sourceName= source.getLocalName();
		String targetName = target.getLocalName();
		
		/*
		 * Ok, Here is the bird's eye view of this algorithm.
		 *
		 * 	Input: 		sourceName, targetName: these are the names of the nodes 
		 * 				(either the class name or the property name)
		 *
		 * 	Step 1:		run treatString on each name to clean it up
		 * 
		 *  Step 2:  	Check right away if the strings are equal (ignoring case).  
		 *  			If they're equal, return a similarity of 1.0.
		 *  
		 *  Step 3a:	If the user wants to use a dictionary, lookup the related nouns
		 *  			and verbs and compare the words shared by the definitions
		 *  
		 *  			Return a similarity based on that.
		 *  
		 *  Step 3b:	The user does not want to use a dictionary, perform a basic
		 *  			string matching algorithm.	
		 */
		
		
		// Step 1:		run treatString on each name to clean it up
		sourceName = treatString(sourceName);
		targetName = treatString(targetName);
		
		
		// Step 2:	If the labels are equal, then return a similarity of 1
		if( sourceName.equalsIgnoreCase(targetName) ) {
			String relation = Alignment.EQUIVALENCE;
			return new Alignment(source, target, 1.0d, relation);
		}
		
		
		
		if( ((BaseSimilarityParameters) param).useDictionary ) {  // Step 3a
			
			// if we haven't initialized our wordnet database, do it
			if( wordnet == null )
				wordnet = WordNetDatabase.getFileInstance();
			
			// The user wants us to use a dictionary to find related words
			
			Synset[] sourceNouns = wordnet.getSynsets(sourceName, SynsetType.NOUN );
			Synset[] targetNouns = wordnet.getSynsets(targetName, SynsetType.NOUN );
			
			float nounSimilarity = getSensesComparison(sourceNouns, targetNouns);
			
			Synset[] sourceVerbs = wordnet.getSynsets(sourceName, SynsetType.VERB);
			Synset[] targetVerbs = wordnet.getSynsets(targetName, SynsetType.VERB);
			
			float verbSimilarity = getSensesComparison(sourceVerbs, targetVerbs);
			
			String rel = Alignment.EQUIVALENCE;
	        
			// select the best similarity found. (either verb or noun)
	        if( nounSimilarity > verbSimilarity ) {
	        	return new Alignment(source, target, nounSimilarity, rel);
	        }
	        else {
	        	return new Alignment(source, target, verbSimilarity, rel);
	        }
			
		}
		else {  // Step 3b
			// the user does not want to use the dictionary
			// TODO: Work out this part of the algorithm.
			
			return new Alignment( source, target, 0.0f, Alignment.EQUIVALENCE);
		}
		
		
	}
	
	
	
	
	
	
	/**
	 * This function treats a string to make it more comparable:
	 * 1) Removes dashes and underscores
	 * 2) Separates capitalized words, ( "BaseSimilarity" -> "Base Similarity" )
	 */
	
	 private String treatString(String s) {
		 
		 
		 String s2 = s.replace("_"," ");
		 s2 = s2.replace("-"," ");
	    	
	    int len = s2.length();
	    
	    for(int i=0;i<len-1; i++){
	    	if( Character.isLowerCase(s2.charAt(i)) &&  Character.isUpperCase(s2.charAt(i+1)) ){
		    
	    		s2 = s2.substring(0,i+1) + " " + s2.substring(i+1); len++;}

		}
	    	
	    return s2;
	 }
	 
	

  

	
	      
	/*
	 * Input: Two synsets of words.
	 * 
	 * This function calculates the similarity between both synsets
	 *     
	 */   

	private float getSensesComparison(Synset[] senses1, Synset[] senses2){
		

		if( senses1.length == 0 || senses2.length == 0 ) {
			// one of the words had no definition
			return 0.0f;
		}
	    	       
		String s1="", s2="";
	    	
		float[] results = new float[senses1.length * senses2.length];
	    	
	    // Explore related words. 
		for (int i=0; i < senses1.length; i++) {   
			Synset sense1 = senses1[i];

			// Print Synset Description 
			//   System.out.println((i+1) + ". " + sense1.getLongDescription());
			s1 += sense1.getDefinition();
			   
			for(int j=0; j< senses2.length; j++){
				Synset sense2 = senses2[j];   
				//     System.out.println((j+1) + ". " + sense2.getLongDescription());  
			    s2 += sense2.getDefinition();
			    
			    results[i+j] = calculateWordSimilarity(removeNonChar(s1),removeNonChar(s2));
			}
	    	       
		} // end-outer-for 
	    	       
		Arrays.sort(results);
	    	
	    	/*
	    	for(int k=0; k<results.length; k++)
	    		System.out.println(results[k]);
	    	*/
	    	       
	    	       
	    	return results[senses1.length * senses2.length-1];
	    }
	
	
	/*
	 * Remove anything from a string that isn't a Character or a space
	 */
	private String removeNonChar(String s){
        
		String result = "";
		for(int i=0; i<s.length(); i++)
			if(Character.isLetter(s.charAt(i)) || s.charAt(i)==' ')
				result += s.charAt(i);
             
		return result;   
	}
	
	
	/*
	 * This function takes two word DEFINITIONS, stems them, 
	 * removes non-content and repeated words, then determines how many words
	 * are in common between the definitions, and calculates a similarity 
	 * based on the number of common words found.
	 * 
	 */
	private float calculateWordSimilarity(String d1, String d2){
		    
		if(d1.equalsIgnoreCase(d2)) return 1;
		    
		// treat the long descriptions
		d1 = treatString(d1); 
		d2 = treatString(d2);
		    
		if(d1.equalsIgnoreCase(d2)) return 1; // the definitions are exactly equal
		    
		ArrayList<String> d1Tokens = new ArrayList<String>(); 
		ArrayList<String> d2Tokens = new ArrayList<String>();
		PorterStemmer ps = new PorterStemmer();

		String word;
		
		// Tokenize the first description, using space as the token separator
		// then remove non-content and repeated words.
		StringTokenizer st = new StringTokenizer(d1);
		
		while(st.hasMoreTokens()){
		  word = st.nextToken();
		   word = ps.stem(word);
		   if(!isNonContent(word) && isNotRepeated(word,d1Tokens) && !word.equalsIgnoreCase("Invalid term"))
		    d1Tokens.add(word);
		}
		 
		st = new StringTokenizer(d2);
		
		while(st.hasMoreTokens()){
		  word = st.nextToken();
		  word = ps.stem(word);
		   if(!isNonContent(word) && isNotRepeated(word,d2Tokens) && !word.equalsIgnoreCase("Invalid term"))
		    d2Tokens.add(word);
		}

		/*
		 for(int i=0; i< d1Tokens.size(); i++)
		    System.out.println(d1Tokens.get(i));
		    
		 for(int i=0; i< d2Tokens.size(); i++)
		    System.out.println(d2Tokens.get(i));
		
		 */
		
		String [] def1 = new String[ d1Tokens.size()];
		String [] def2 = new String[d2Tokens.size()];
		
		for(int i=0; i<d1Tokens.size(); i++)
		    def1[i] = d1Tokens.get(i);
		   
		   
		
		for(int i=0; i<d2Tokens.size(); i++)
		    def2[i] = d2Tokens.get(i);
		    
		if(def1.length == 0 || def2.length == 0)
		    return 0;
		
		
		int counter =0;
		
		// count how many words the lists has in common
		for(int i=0; i<def1.length; i++)
		    for(int j=0; j<def2.length; j++)
		        if(def1[i].equalsIgnoreCase(def2[j]) )
		            counter++;
		    
		//printStringArray(def1);
		//printStringArray(def2);
		 
		
		// return the computed similarity (based on the common words)
		return ((float)counter /((float) (def1.length + def2.length )/2.0f));
		 
	}
	
	/*
	 * Determine whether this is a non-content word
	 */
	private boolean isNonContent(String s){
	    
	if(s.equalsIgnoreCase("the") || 
	   s.equalsIgnoreCase("is") || 
	   s.equalsIgnoreCase("this") || 
	   s.equalsIgnoreCase("are") || 
	   s.equalsIgnoreCase("to") || 
	   s.equalsIgnoreCase("a") || 
	   s.equalsIgnoreCase("in") ||
	   s.equalsIgnoreCase("or") ||
	   s.equalsIgnoreCase("and") || 
	   s.equalsIgnoreCase("for") || 
	   s.equalsIgnoreCase("that") ) 
	{
		return true;
	}
		
	return false;
	       
	}

	/*
	 * Determine if this word is already in the sentence array.
	 */
	private boolean isNotRepeated(String word,ArrayList<String> sentence){
	    
		for(int i=0; i<sentence.size(); i++)
			if(word.equalsIgnoreCase( sentence.get(i) ))
				return false;
		 
		
		return true;
	}
	      
}

