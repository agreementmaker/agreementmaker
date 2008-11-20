package agreementMaker.application.mappingEngine.Matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import edu.gwu.wordnet.DictionaryDatabase;
import edu.gwu.wordnet.FileBackedDictionary;
import edu.gwu.wordnet.IndexWord;
import edu.gwu.wordnet.POS;
import edu.gwu.wordnet.Synset;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.stemmer.PorterStemmer;
import agreementMaker.application.ontology.Node;


public class BaseSimilarityMatcher extends AbstractMatcher { 

	
	public BaseSimilarityMatcher(int n, String s) {
		super(n, s);
	}
	
	/**Set all alignment sim to a random value between 0 and 1*/
	public Alignment alignTwoNodes(Node source, Node target) {
		
		String sourceLabel = source.getLabel();
		String targetLabel = source.getLabel();
		
		sourceLabel = treatString(sourceLabel);
		targetLabel = treatString(targetLabel);
		
		// if the labels are equal, then return a similarity of 1
		if( sourceLabel.equalsIgnoreCase(targetLabel) ) {
			String relation = Alignment.EQUIVALENCE;
			return new Alignment(source, target, 1.0d, relation);
		}
		
		
		float nounSimilarity = wordnetNouns(sourceLabel, targetLabel);
        float verbSimilarity = wordnetVerbs(sourceLabel, targetLabel);
		
		
		
		double sim = Math.random();
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
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
	 * This function looks up the words in the WordNet dictionary looking for a noun,
	 * then gets the Definition of the noun, stems each word of the definition, 
	 * and then one by one compares how many words are shared by both word Definitions    
	 */
	public float wordnetNouns(String theWord, String theWord2){
	
	        
		// Load Dictionary 
		DictionaryDatabase dictionary = new FileBackedDictionary("wordnetdata");

		// Look up words relating to each word 
		IndexWord word1 = dictionary.lookupIndexWord(POS.NOUN, theWord);
		IndexWord word2 = dictionary.lookupIndexWord(POS.NOUN, theWord2);
	        
		return getSensesComparison(word1, word2);
	                
	 }    

	
	/*
	 * This function does the same thing as wordnetNouns, but looks for a verb instead of a noun
	 */
	public float wordnetVerbs(String theWord, String theWord2){
	          
		// Load Dictionary 
		DictionaryDatabase dictionary = new FileBackedDictionary("wordnetdata");

		// Look up words relating to each word		
		IndexWord word1 = dictionary.lookupIndexWord(POS.VERB, theWord);
		IndexWord word2 = dictionary.lookupIndexWord(POS.VERB, theWord2);
		
		return getSensesComparison(word1, word2);
	}    

	
	      
	/*
	 * This function gets the senses of each word, then 
	 */

	private float getSensesComparison(IndexWord word1, IndexWord word2){
		Synset[] senses1;
		Synset[] senses2;
	    	      
		try {
			senses1 = word1.getSenses();
			senses2 = word2.getSenses();
		}
		catch(NullPointerException e) {
			return 0f;   
		}
	    	        
	    	       
	    	String s1="", s2="";
	    	
	    	float[] results = new float[senses1.length * senses2.length];
	    	
	    	// Explore related words. 
	    	for (int i=0; i < senses1.length; i++) {   
	    		Synset sense1 = senses1[i];

	    	    // Print Synset Description 
	    	    //   System.out.println((i+1) + ". " + sense1.getLongDescription());
	    	    s1 += sense1.getLongDescription();
	    	       
	    	    for(int j=0; j< senses2.length; j++){
	    	    	Synset sense2 = senses2[j];   
	    	    	//     System.out.println((j+1) + ". " + sense2.getLongDescription());  
	    	        s2 += sense2.getLongDescription();
	    	        
	    	        results[i+j] = compare(removeNonChar(s1),removeNonChar(s2));
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
	
	
	   public float compare(String d1, String d2){
		    
		    if(d1.equalsIgnoreCase(d2)) return 1;
		    
		    d1 = treatString(d1);
		    d2 = treatString(d2);
		    
		    if(d1.equalsIgnoreCase(d2)) return 1;
		    
		     ArrayList d1Tokens = new ArrayList(); 
		     ArrayList d2Tokens = new ArrayList();
		     PorterStemmer ps = new PorterStemmer();
		    
		     StringTokenizer st = new StringTokenizer(d1);
		     String word;
		     
		     while(st.hasMoreTokens()){
		       word = st.nextToken();
		        word = ps.stem(word);
		        if(!isNonContent(word) && notRepeated(word,d1Tokens) && !word.equalsIgnoreCase("Invalid term"))
		         d1Tokens.add(word);
		     }
		     
		     st = new StringTokenizer(d2);

		     while(st.hasMoreTokens()){
		       word = st.nextToken();
		       word = ps.stem(word);
		        if(!isNonContent(word) && notRepeated(word,d2Tokens) && !word.equalsIgnoreCase("Invalid term"))
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
		        def1[i] = (String)d1Tokens.get(i);
		       
		       
		    
		    for(int i=0; i<d2Tokens.size(); i++)
		        def2[i] = (String)d2Tokens.get(i);
		        
		    if(def1.length == 0 || def2.length == 0)
		        return 0;
		    
		    
		    int counter =0;

		    for(int i=0; i<def1.length; i++)
		        for(int j=0; j<def2.length; j++)
		            if(def1[i].equalsIgnoreCase(def2[j]) )
		                counter++;
		        
		    //printStringArray(def1);
		    //printStringArray(def2);
		     
		    
		    return ((float)counter /((float) (def1.length + def2.length )/2.0f));
		     
		    }
	   
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
	          
	          return true;
	          
	          return false;
	           
	        }
	    
	    private boolean notRepeated(String word,ArrayList sentence){
	        
	        for(int i=0; i<sentence.size(); i++)
	                 if(word.equalsIgnoreCase((String)sentence.get(i)))
	                   return false;
	         
	        
	        return true;
	       }
	      
}

