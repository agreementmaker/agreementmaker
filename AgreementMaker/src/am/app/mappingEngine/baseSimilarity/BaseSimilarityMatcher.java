package am.app.mappingEngine.baseSimilarity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.StringUtil.PorterStemmer;
import am.app.ontology.Node;


public class BaseSimilarityMatcher extends AbstractMatcher { 


	// JAWS WordNet interface
	private WordNetDatabase wordnet  = null;
	
	private NormalizerParameter param1;
	private NormalizerParameter param2;
	private NormalizerParameter param3;
	private Normalizer norm1;
	private Normalizer norm2;
	private Normalizer norm3;
	
	public BaseSimilarityMatcher() {
		// warning, param is not available at the time of the constructor (when creating a matcher from the User Interface)
		super();
		needsParam = true;
		
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
	}
	
	// Constructor used when the parameters are available at the time of matcher initialization
	public BaseSimilarityMatcher( BaseSimilarityParameters param_new ) {  
		super(param_new);	
	}
	
	protected void initializeVariables() {
		super.initializeVariables();

		// setup the different normalizers
		param1 = new NormalizerParameter();
		param1.setAllTrue();
		param1.normalizeDigit = false;
		param1.stem = false;
		norm1 = new Normalizer(param1);
		
		
		param2 = new NormalizerParameter();
		param2.setAllfalse();
		param2.stem = true;
		norm2 = new Normalizer(param2);
		
		param3 = new NormalizerParameter();
		param3.setAllfalse();
		param3.normalizeDigit = true;
		norm3 = new Normalizer(param3);

	}
	
	
	public String getDescriptionString() {
		return "Performs a local matching using a String Based technique. To be used as first matcher.\n" +
				"Only Nodes' local-names (XML id) and labels are considered in the process.\n" +
				"String are preprocessed with cleaning, stemming, stop-words removing, and tokenization techniques.\n" +
				"A similarity matrix contains the similarity between each pair (sourceNode, targetNode).\n" +
				"A selection algorithm select valid alignments considering threshold and number of relations per node.\n"; 
	}
	
	
	
	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */
	
	
	
	/*
	 * This function does the main base similarity algorithm.
	 */
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) throws Exception {


		
		/*
		 * Ok, Here is the bird's eye view of this algorithm.
		 * NON DICTIONARY
		 *  
		 *  check if labels are equivalent if so return 1
		 *  check if localnames are equivalent, if so return 1
		 *  normalize (without stemming) labels if they are equivalent return 0.95
		 *  normalize (without stemming) localnames if they are equivalent return 0.95
		 *  apply also stemming labels if they are equivalent return 0.9
		 *  apply also stemming localnames if they are equivalent return 0.9
		 *  remove digits and return 0.8
		 *  else return 0
		 * 
		 * 
		 *  USE DICTIONARY PART
		 *
		 * 	Input: 		sourceName, targetName: these are the names of the nodes 
		 * 				(either the class name or the property name)
		 *
		 * 	Step 1:		run treatString on each name to clean it up
		 * 
		 *  Step 2:  	Check right away if the strings are equal (ignoring case).  
		 *  			If they're equal, return a similarity of 1.0.
		 *  
		 *  Step 3a:	lookup the related nouns
		 *  			and verbs and compare the words shared by the definitions
		 *  
		 *  			Return a similarity based on that.
		 *  
		 *  Step 3b:	The user does not want to use a dictionary, perform a basic
		 *  			string matching algorithm.	
		 */
		
		

		
		
		
		if( param != null && ((BaseSimilarityParameters) param).useDictionary ) {  // Step 3a
			//this is the same as the one of William Sunan
			//String sourceName = source.getLocalName();
			//String targetName = target.getLocalName();
			
			String sourceName = source.getLabel();
			String targetName = target.getLabel();
			
			// Step 1:		run treatString on each name to clean it up
			sourceName = treatString(sourceName);
			targetName = treatString(targetName);
			
			
			// Step 2:	If the labels are equal, then return a similarity of 1
			if( sourceName.equalsIgnoreCase(targetName) ) {
				String relation = Alignment.EQUIVALENCE;
				return new Alignment(source, target, 1.0d, relation);
			}
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
		else {  // Step no dictionary
			// the user does not want to use the dictionary
			// Changed from the one of Sunna
			
			//FOCUS ON LOCALNAMES
			//equivalence return 1
			String sLocalname = source.getLocalName();
			String tLocalname = target.getLocalName();
			
			if(sLocalname.equalsIgnoreCase(tLocalname))
				return new Alignment( source, target, 1d, Alignment.EQUIVALENCE);
			//all normalization without stemming and digits return 0.95
			
			String sProcessedLocalnames = norm1.normalize(sLocalname);
			String tProcessedLocalnames = norm1.normalize(tLocalname);
			if(sProcessedLocalnames.equals(tProcessedLocalnames))
				return new Alignment( source, target, 0.95d, Alignment.EQUIVALENCE);
			//all normalization without digits return 0.90

			sProcessedLocalnames = norm2.normalize(sLocalname);
			tProcessedLocalnames = norm2.normalize(tLocalname);
			if(sProcessedLocalnames.equals(tProcessedLocalnames))
				return new Alignment( source, target, 0.9d, Alignment.EQUIVALENCE);
			//all normalization return 0.8

			sProcessedLocalnames = norm3.normalize(sLocalname);
			tProcessedLocalnames = norm3.normalize(tLocalname);
			if(sProcessedLocalnames.equals(tProcessedLocalnames))
				return new Alignment( source, target, 0.8d, Alignment.EQUIVALENCE);
	
			//FOCUS ON LABELS
			//equivalence return 1
			String sLabel = source.getLabel();
			String tLabel = target.getLabel();
			if( !(sLabel.equals("") || tLabel.equals("")) ){
				if(sLabel.equalsIgnoreCase(tLabel))
					return new Alignment( source, target, 1, Alignment.EQUIVALENCE);
				//all normalization without stemming and digits return 0.95
				
				
				
				String sProcessedLabel = norm1.normalize(sLabel);
				String tProcessedLabel = norm1.normalize(tLabel);
				if(sProcessedLabel.equals(tProcessedLabel))
					return new Alignment( source, target, 0.95d, Alignment.EQUIVALENCE);
				//apply stem return 0.90 
				
				
				
				sProcessedLabel = norm2.normalize(sLabel);
				tProcessedLabel = norm2.normalize(tLabel);
				if(sProcessedLabel.equals(tProcessedLabel))
					return new Alignment( source, target, 0.9d, Alignment.EQUIVALENCE);
				//apply normDigits return 0.8
				

				sProcessedLabel = norm3.normalize(sLabel);
				tProcessedLabel = norm3.normalize(tLabel);
				if(sProcessedLabel.equals(tProcessedLabel))
					return new Alignment( source, target, 0.8d, Alignment.EQUIVALENCE);
			}
			//none of the above
			return new  Alignment( source, target, 0.0d, Alignment.EQUIVALENCE);
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
		 s2 = s2.replace("."," ");	
	    
	    /*
	    for(int i=0;i<len-1; i++){
	    	if( Character.isLowerCase(s2.charAt(i)) &&  Character.isUpperCase(s2.charAt(i+1)) ){
		    
	    		s2 = s2.substring(0,i+1) + " " + s2.substring(i+1); len++;}

		}
	    */	
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
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new BaseSimilarityMatcherParametersPanel();
		}
		return parametersPanel;
	}
	      
}

