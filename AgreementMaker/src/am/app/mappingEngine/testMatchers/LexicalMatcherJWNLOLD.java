package am.app.mappingEngine.testMatchers;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;


public class LexicalMatcherJWNLOLD extends AbstractMatcher{
	
	private static final long serialVersionUID = -2443784819859112128L;
	
	//private Normalizer normalizer;
	private Dictionary dictionary;
	
	//Constructor
	public LexicalMatcherJWNLOLD() throws Exception {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = false;
		
		// If WordNet database is not initialized, do it
		if( !JWNL.isInitialized() ){
			JWNL.initialize(new FileInputStream("./wordnet-3.0/file_properties.xml"));
			System.out.println("JWNL initialized...");
		}
		dictionary = Dictionary.getInstance();
	}
	
	//Description of Algorithm
	public String getDescriptionString() {
		return "A lexical matcher using WordNet.\n"; 
	}
	
	
	/* Algorithm functions beyond this point */
	
	/**
	 * Function aligns 2 nodes using WordNet:
	 */
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		
		//Get local name		
		String sourceName= source.getLocalName();
		String targetName = target.getLocalName();
		
		//Run treatString() on each name to clean it up
		//Normalizer has already been used in another method.
		sourceName = treatString(sourceName);
		targetName = treatString(targetName);
		
		// I ASSUME STRING EQUALITY IS ALREADY CHECKED BEFORE BY ANOTHER MATCHER
		/*
		//If the labels are equal, then return a similarity of 1
		if( sourceName.equalsIgnoreCase(targetName) ) {
			//return new Alignment(source, target, 1.0d, Alignment.EQUIVALENCE);
		}
		*/
		
		double sHyperNoun = 0.0d;
		double sHyperVerb = 0.0d;
		double sSynoNoun = 0.0d;
		double sSynoVerb = 0.0d;
		
		try {
			IndexWord word1 = dictionary.lookupIndexWord(POS.NOUN, sourceName);
			IndexWord word2 = dictionary.lookupIndexWord(POS.NOUN, targetName);
			
			if(word1 != null && word2 != null){
				sHyperNoun = hypernymSimilarity(word1, word2);
				Synset[] Synset1 = word1.getSenses();
                Synset[] Synset2 = word2.getSenses();
                sSynoNoun = synonymSimilarity(Synset1, Synset2, sourceName, targetName);
			}

			IndexWord word3 = dictionary.lookupIndexWord(POS.VERB, sourceName);
			IndexWord word4 = dictionary.lookupIndexWord(POS.VERB, targetName);
			
			if(word3 != null && word4 != null){
				sHyperVerb = hypernymSimilarity(word3, word4);
				Synset[] Synset1 = word3.getSenses();
                Synset[] Synset2 = word4.getSenses();
                sSynoVerb = synonymSimilarity(Synset1, Synset2, sourceName, targetName);
			}	
			
		} catch (JWNLException e) {
			e.printStackTrace();
		}
		
		/*
		System.out.println( "Source Name: " + sourceName + 
							", Target Name: " + targetName +
							"\t Hypernym Noun: " + sHyperNoun +
							"\t  Hypernym Verb: " + sHyperVerb +
							"\t  Synonym Noun: " + sSynoNoun +
							"\t  Synonym Verb: " + sSynoVerb);
		*
		*/
		//Here take the max similarity value for returning alignment, and return.
		double max1 = sHyperNoun;
		double max2 = sSynoNoun;
		double max = max1;	//Max of max similarity value
		
		if(sHyperNoun < sHyperVerb){
			max1 = sHyperVerb;			
		}
		
		if(sSynoNoun < sSynoVerb){
			max2 = sSynoVerb;
		}
		
		if(max1 < max2){
			max = max2;
		}
		
		return new Mapping(source, target, max, Mapping.EQUIVALENCE);
	}
	
	/**
	 * Function calculates the similarity using hypernyms of the concepts
	 */
    public double hypernymSimilarity(IndexWord index1, IndexWord index2) {
        // the max number of common concepts between the two tokens
        double commonMax = 0;

        //The two lists giving the best match
        PointerTargetNodeList best1 = new PointerTargetNodeList();
        PointerTargetNodeList best2 = new PointerTargetNodeList();

        //The two lists being compared
        PointerTargetNodeList ptnl1 = new PointerTargetNodeList();
        PointerTargetNodeList ptnl2 = new PointerTargetNodeList();

        //If concepts are in the WordNet
        //ALREADY CHECKED BEFORE CALLING FUNCTION
        //if (index1 != null && index2 != null) {
            try {
                int maxOfLists = 0;

                Synset[] Synset1 = index1.getSenses();
                Synset[] Synset2 = index2.getSenses();
                
                for (int i = 0; i < index1.getSenseCount(); i++) {
                    Synset synset1 = Synset1[i];
                    for (int k = 0; k < index2.getSenseCount(); k++) {
                        Synset synset2 = Synset2[k];
                        
                        //Get hypernyms of the current synonym
                        List hypernymList1 = PointerUtils.getInstance().getHypernymTree(synset1).toList();
                        List hypernymList2 = PointerUtils.getInstance().getHypernymTree(synset2).toList();

                        Iterator hList1iter = hypernymList1.iterator();

                        while (hList1iter.hasNext()) {
                            ptnl1 = (PointerTargetNodeList) hList1iter.next();
                            Iterator hList2iter = hypernymList2.iterator();
                            while (hList2iter.hasNext()) {
                                ptnl2 = (PointerTargetNodeList) hList2iter.next();

                                int commonNum = getCommonConcepts(ptnl1, ptnl2);
                                if (commonNum > maxOfLists) {
                                	maxOfLists = commonNum;
                                    best1 = ptnl1;
                                    best2 = ptnl2;
                                }
                            }
                        }
                        if (maxOfLists > commonMax) {
                        	commonMax = maxOfLists;
                        }
                    }
                }
                
                if (best1.isEmpty() && best2.isEmpty())
                    return 0;
                return (2 * commonMax / (best1.size() + best2.size()));
            }
            catch (JWNLException e) {
                e.printStackTrace();
            }
        //}
        return 0;
    }

    /**
     * Function finds the number of common concepts between the lists given
     */
    public int getCommonConcepts(PointerTargetNodeList list1, PointerTargetNodeList list2) {
            int commonNum = 0;
            int i = 1;
            while (i <= Math.min(list1.size(), list2.size()) && 
            		((PointerTargetNode) list1.get(list1.size() - i)).getSynset() 
            		== ((PointerTargetNode) list2.get(list2.size() - i)).getSynset()) {
            	commonNum++;
                i++;
            }
            return commonNum;
        }
	
	
	 /**
	  * Input: One Synset[] and one string.
	  * This function checks: 
	  * 1)  if source is in the synonyms of target
	  * 2)  if target is in the synonyms of source
	  * 3)  if synonyms of source is in the synonyms of target
	  */ 
	 float synonymSimilarity(Synset[] senses1, Synset[] senses2, String s1, String s2){
		 
		 //Check target in synonyms of source
		 if(! (senses1.length == 0) ){
			 for ( Synset s : senses1 ){
				 if ( s.toString().equalsIgnoreCase( s2 ) ) {
					 //System.out.println("IN SYNONYM SIM...");
					 return 0.99f;
				}
			 }
		 }
		 
		//Check source in synonyms of target
		 if(! (senses2.length == 0) ){
			 for ( Synset s : senses2 ){
				 if ( s.toString().equalsIgnoreCase( s1 ) ) {
					 //System.out.println("IN SYNONYM SIM...");
					 return 0.99f;
				}
			 }
		 }
		 
		 //Check each synonym of target in synonyms of source.
		 if(senses1.length == 0 || senses2.length == 0){
			 return 0.0f;
		 }
		 else{
			 for ( Synset sn1 : senses1 ){
				 for ( Synset sn2 : senses2 ){
				 	if ( sn1.equals(sn2) ) {
				 		//System.out.println("IN SYNONYM SIM...");
				 		return 0.99f;
				 	}
				 }
			 }
		 }
		 return 0f;
	 }
	 
	 

		/**
		 * This function treats a string to make it more comparable:
		 * 1) Removes numbers, punctuation etc.
		 * 2) Removes non-content words.
		 * 3) Separates capitalized words, ( "BaseSimilarity" -> "Base Similarity" )
		 */
		 private String treatString(String label) {
			 
			 //Remove anything from a string that isn't a Character or a space
		     //e.g. numbers, punctuation etc.
			 String result = "";
			 for(int i=0; i<label.length(); i++){
				 if( Character.isLetter(label.charAt(i)) || Character.isWhitespace( label.charAt(i) ) ){
					 result += label.charAt(i);
				 }
			 }
			 label = result;
			    
			 label = label.toLowerCase();
			 
			 //Remove non-content words
			 if(label.startsWith("has"))
				 label = label.replaceFirst("has", "");
			 else if(label.startsWith("is"))
				 label = label.replaceFirst("is", "");
			 else if(label.startsWith("are"))
				 label = label.replaceFirst("are", "");
			 else if(label.startsWith("be"))
				 label = label.replaceFirst("be", "");
			 else if(label.endsWith(" by"))
				 label = label.substring(0, label.length()-3);
			 else if(label.endsWith(" in"))
				 label = label.substring(0, label.length()-3);
			 else if(label.endsWith(" at"))
				 label = label.substring(0, label.length()-3);
			 else if(label.endsWith(" to"))
				 label = label.substring(0, label.length()-3);
			 else if(label.endsWith(" on"))
				 label = label.substring(0, label.length()-3);
			 else if(label.endsWith(" for"))
				 label = label.substring(0, label.length()-4);
			 
			 int len = label.length();
			 //Separate words with spaces
			 for(int i=0;i<len-1; i++){
				 if( Character.isLowerCase(label.charAt(i)) &&  Character.isUpperCase(label.charAt(i+1)) ){
			    
					 label = label.substring(0,i+1) + " " + label.substring(i+1); len++;}
			 }
			 
		    return label.trim();
		 }

	
}
