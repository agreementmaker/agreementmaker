package am.matcher.LexicalMatcherJWNL;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.dictionary.Dictionary;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

public class LexicalMatcherJWNL extends AbstractMatcher{

	private static final long serialVersionUID = 7662236197561302505L;

	private Dictionary dictionary;
	private List<String>  sourceClassTreatedStrings;
	private List<String>  targetClassTreatedStrings;
	private List<String>  sourcePropTreatedStrings;
	private List<String>  targetPropTreatedStrings;
	
	private List<IndexWord> sourceClassNounWords;
	private List<IndexWord> sourceClassVerbWords;
	private List<IndexWord> sourcePropNounWords;
	private List<IndexWord> sourcePropVerbWords;
	
	private List<IndexWord> targetClassNounWords;
	private List<IndexWord> targetClassVerbWords;
	private List<IndexWord> targetPropNounWords;
	private List<IndexWord> targetPropVerbWords;
	

	
	
	//Constructor
	public LexicalMatcherJWNL() throws Exception {
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
	@Override
	public String getDescriptionString() {
		return "A lexical matcher that takes advantage of WordNet to find similarities between concepts\n"; 
	}
	
	@Override
	protected void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		
		//for each concept compute the treated string
		sourceClassTreatedStrings = computeStrings(sourceOntology.getClassesList());
		targetClassTreatedStrings = computeStrings(targetOntology.getClassesList());
		sourcePropTreatedStrings = computeStrings(sourceOntology.getPropertiesList());
		targetPropTreatedStrings = computeStrings(targetOntology.getPropertiesList());
		//for each concept builds the list of lookedUp strings from wordnet
		
		//source nouns and verbs words
		//classes
		sourceClassNounWords = computeWords(sourceClassTreatedStrings, POS.NOUN);
		sourceClassVerbWords = computeWords(sourceClassTreatedStrings, POS.VERB);
		//properties
		sourcePropNounWords = computeWords(sourcePropTreatedStrings, POS.NOUN);
		sourcePropVerbWords = computeWords(sourcePropTreatedStrings, POS.VERB);
		
		//target nouns and verbs words
		//classes
		targetClassNounWords = computeWords(targetClassTreatedStrings, POS.NOUN);
		targetClassVerbWords = computeWords(targetClassTreatedStrings, POS.VERB);
		//properties
		targetPropNounWords = computeWords(targetPropTreatedStrings, POS.NOUN);
		targetPropVerbWords = computeWords(targetPropTreatedStrings, POS.VERB);
	}
	
	private ArrayList<String> computeStrings( List<Node> list) throws Exception {
		ArrayList<String> result = new ArrayList<String>();
		Iterator<Node> it = list.iterator();
		while(it.hasNext()){
			//for each concept we get the indexWord from the label
			//if the concept has no label we consider the localname
			Node n = it.next();
			String nodeString = n.getLabel();
			if(nodeString == null || nodeString.equals(""))
				nodeString = n.getLocalName();
			//Run treatString() on each name to clean it up
			String processedString = treatString(nodeString);
			result.add(processedString);
		}
		return result;
	}

	private ArrayList<IndexWord> computeWords( List<String> list, POS pos) throws Exception {
		ArrayList<IndexWord> result = new ArrayList<IndexWord>();
		Iterator<String> it = list.iterator();
		String processedString;
		while(it.hasNext()){
			processedString = it.next();
			IndexWord iWord = dictionary.lookupIndexWord(pos, processedString);
			result.add(iWord);//it may also be null, but we have to keep it in the list to mantain the order with concept index
		}
		return result;
	}


	
	/* Algorithm functions beyond this point */
	
	/**
	 * Function aligns 2 nodes using WordNet:
	 */
	@Override
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix) throws Exception {
		
		double sHyperNoun = 0.0d;
		double sHyperVerb = 0.0d;
		double sSynoNoun = 0.0d;
		double sSynoVerb = 0.0d;
		IndexWord word1;
		IndexWord word2;
		IndexWord word3;
		IndexWord word4;
		String sourceName;
		String targetName;
		
		try {
			if(typeOfNodes.equals(alignType.aligningClasses)){
				word1 = sourceClassNounWords.get(source.getIndex());
				word2 = targetClassNounWords.get(target.getIndex());
				word3 = sourceClassVerbWords.get(source.getIndex());
				word4 = targetClassVerbWords.get(target.getIndex());
				sourceName = sourceClassTreatedStrings.get(source.getIndex());
				targetName = targetClassTreatedStrings.get(target.getIndex());
			}
			else{
				word1 = sourcePropNounWords.get(source.getIndex());
				word2 = targetPropNounWords.get(target.getIndex());
				word3 = sourcePropVerbWords.get(source.getIndex());
				word4 = targetPropVerbWords.get(target.getIndex());
				sourceName = sourcePropTreatedStrings.get(source.getIndex());
				targetName = targetPropTreatedStrings.get(target.getIndex());
			}
			
			if(word1 != null && word2 != null){
				sHyperNoun = hypernymSimilarity(word1, word2);
				Synset[] Synset1 = word1.getSenses();
                Synset[] Synset2 = word2.getSenses();
                sSynoNoun = synonymSimilarity(Synset1, Synset2, sourceName, targetName);
			}
			
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
		
		return new Mapping(source, target, max, MappingRelation.EQUIVALENCE);
	}
	
	/**
	 * Function calculates the similarity using hypernyms of the concepts
	 */
 
	public double hypernymSimilarity(IndexWord index1, IndexWord index2) throws Exception {
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
                        List<PointerTargetNodeList> hypernymList1 = PointerUtils.getInstance().getHypernymTree(synset1).toList();
                        List<PointerTargetNodeList> hypernymList2 = PointerUtils.getInstance().getHypernymTree(synset2).toList();

                        Iterator<PointerTargetNodeList> hList1iter = hypernymList1.iterator();

                        while (hList1iter.hasNext()) {
                            ptnl1 = hList1iter.next();
                            Iterator<PointerTargetNodeList> hList2iter = hypernymList2.iterator();
                            while (hList2iter.hasNext()) {
                                ptnl2 = hList2iter.next();

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
	 float synonymSimilarity(Synset[] senses1, Synset[] senses2, String s1, String s2) throws Exception {
		 
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
		 private String treatString(String label) throws Exception {
			 
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
