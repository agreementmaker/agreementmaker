/**
 * 
 */
package am.app.mappingEngine.baseSimilarity.advancedSimilarity;

import java.util.ArrayList;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Node;

/**
 * @author Michele Caci
 */
public class AdvancedSimilarityMatcher extends BaseSimilarityMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2316666472074501151L;
	
	private static ArrayList<String> isHas = new ArrayList<String>();
	private static ArrayList<String> prep = new ArrayList<String>();
	
	private static final double NO_MATCH = -1.0; // I use this value when I want to point out that two concepts should not match
	
	// final result is the sum of all these contributions
	private static double start_value_contribution = 0; // similarity value given by string analysis
	private static double word_distance_contribution = 0; // distance between words composing the two concepts
	private static double avg_word_number_contribution = 0; // average number of words composing the two concepts
	
	private static int min_word_distance = 3; // average number of words composing the two concepts
	
	//public boolean useLabelInsteadOfLocalname = true;  //TODO: REMOVE THIS! VERY BAD!
	
	private ArrayList<String> sourceWords;
	private ArrayList<String> targetWords;
	
	/**
	 * Empty constructor
	 */
	public AdvancedSimilarityMatcher() {
		super();
		needsParam = true;
	}

	/**
	 * Constructor with parameters: TODO->introduce parameters for local matcher and its parameters
	 * @param params_new parameters to give to the BSS
	 */
	public AdvancedSimilarityMatcher(AdvancedSimilarityParameters param_new) {
		super(param_new);
		initializeVariables();
	}
	
	/**
	 * Overridden method
	 * Works well also on compound words. Total similarity is built in two steps: first it check if there is some bad combinations using 
	 * some keywords inside it (some auxiliaries and prepositions) then it looks at the other words that compose the two concepts to align
	 * and runs a string comparison algorithm to get the similarity value 
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#alignTwoNodes(Node source, Node target, alignType typeOfNodes)
	 * @author michele
	 */
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) throws Exception {
		// TODO: works no more while returning null... no sparse matrix now.... creating 0.0 alignment
		
		// Step 0: tokenize source and target nodes (if possible) and separate by relevance
		// prepare list of data
		
		String sLN, tLN;
		
		AdvancedSimilarityParameters parameters = (AdvancedSimilarityParameters)param;
		
		//if( !useLabelInsteadOfLocalname ) {
			sLN = super.treatString(source.getLocalName()); // source LocalName (sLN)
			tLN = super.treatString(target.getLocalName()); // target LocalName (tLN)
/*		} else {
			sLN = super.treatString(source.getLabel()); // source LocalName (sLN)
			tLN = super.treatString(target.getLabel()); // target LocalName (tLN)
		}
		*/
		String tokenized_sLN[] = sLN.split("\\s"); // token array of source LocalName (sLN)
		String tokenized_tLN[] = tLN.split("\\s"); // token array of target LocalName (tLN)
		
		// Step 1: similarity check between less "meaningful" words
		// e.g. if one concept has "is" in the word and another one has "has", I assume they are not matchable, same thing with prepositions 
		double simValue = 0.0;
		double simValueContribution = nonContentWordCheck(tokenized_sLN, tokenized_tLN);
		
		if(simValueContribution == NO_MATCH){ // immediately discard those considered unmatchable
			return new Mapping(source, target, 0);
		}
		// Step 2: check out for similarity between meaningful words
		else {
			sourceWords = createWordsList(tokenized_sLN);
			targetWords = createWordsList(tokenized_tLN);
			
			// here is where we perform the check
			simValue = contentWordCheck(sourceWords, targetWords, typeOfNodes);
		}
		
		if(simValue > 0.0){
			if(simValueContribution > 0.0){
				return new Mapping(source, target, Math.min(1, simValue * (1.0 + simValueContribution)));
			}
			else{
				return new Mapping(source, target, Math.min(1, simValue));
			}
		}
		return new Mapping(source, target, 0);
	}
	
	/**
	 * ContentWordCheck compares the source and the target words (either compound or simple) to compute the similarity value between those words
	 * @param source list of words composing the source node localname (single element if the word is a simple one) 
	 * @param target list of words composing the source node localname (single element if the word is a simple one) 
	 * @param typeOfNodes tells whether nodes involved are classes or properties (exploited with the alignment matrix) 
	 * @return similarity value between source and target
	 * @author michele
	 */
	private double contentWordCheck(ArrayList<String> source, ArrayList<String> target, alignType typeOfNodes) throws Exception {
		// initializing local variables
		int sSize = source.size(), tSize = target.size();
		String s = null, t = null;
		
		// initializing local matcher (to be abstracted) and matrix
		ParametricStringParameters localMatcherParams = new ParametricStringParameters();
		localMatcherParams.initForOAEI2009();
		ParametricStringMatcher localMatcher = new ParametricStringMatcher();
		localMatcher.setParam(localMatcherParams);
		localMatcher.initializeNormalizer();
		SimilarityMatrix localMatrix = new ArraySimilarityMatrix(source.size(), target.size(), typeOfNodes);
		
		/* ------------- BEGIN FOR #1 --------------- */
		double tempValue = 0.0;
		// DEBUG INFO
		// System.out.println(source.toString() + " " + target.toString());
		for(int i = 0; i < sSize; i++){
			s = source.get(i).toLowerCase();
					
			/* ------------- BEGIN FOR #2 --------------- */
			for(int j = 0; j < tSize; j++){
				
				t = target.get(j).toLowerCase();
				tempValue = ((ParametricStringMatcher) localMatcher).performStringSimilarity(s, t);
				//localMatrix.setSimilarity(i, j, tempValue);
				localMatrix.set(i, j, new Mapping(new Node(i, s, typeOfNodes.toString(), sourceOntology.getIndex()),
													new Node(j, t, typeOfNodes.toString(), targetOntology.getIndex()),
													tempValue));
				
				// DEBUG INFO
				// System.out.println(s + " " + t + " " + localMatrix.getSimilarity(i, j));
			}
			/* ------------- END FOR #2 --------------- */
		}
		/* ------------- END FOR #1 --------------- */

		ArrayList<Mapping> localResult = localMatrix.chooseBestN();
		
		double simValue = 0;
		for(int i = 0; i < localResult.size(); i++){
			simValue += localResult.get(i).getSimilarity();
		}
		simValue /= localResult.size();
		return computeValue(simValue, Math.max(sSize, tSize) - Math.min(sSize, tSize), (sSize + tSize)/2);
	}
	
	/**
	 * nonContentWordCheck compares the source and the target words (either compound or simple) to check some wrong mapping to discard
	 * @param sourceLocalName list of words composing the source node localname (single element if the word is a simple one) 
	 * @param targetLocalName list of words composing the source node localname (single element if the word is a simple one) 
	 * @return small similarity value bonus or 0.0 if there are no nonContent strings involved or NO_MATCH
	 * @author michele
	 */
	private double nonContentWordCheck(String[] sourceLocalName, String[] targetLocalName){
		double simValue = 0.0;
		String s = null, t = null;
		
		/*/ DEBUG INFORMATION
		for(int i = 0; i < sourceLocalName.length; i++){
			for(int j = 0; j < targetLocalName.length; j++){
				System.out.println(sourceLocalName[i] + " " + targetLocalName[j]);
			}
		}
		*/
		
		/* ------------- BEGIN FOR #1 --------------- */
		for(int i = 0; i < sourceLocalName.length; i++){
			s = sourceLocalName[i].toLowerCase();
			// check if word is not relevant first
			if(isNonContent(s)){
				// among those select those meaningful
				if(isRelevantString(s)){
					
					// and compare them to the meaningful non-relevant strings of the second concept
					/* ------------- BEGIN FOR #2 --------------- */
					for(int j = 0; j < targetLocalName.length; j++){
						t = targetLocalName[j].toLowerCase();
						
						if(isNonContent(t)) {
							if(isRelevantString(t)){
							
								// here begins the part of assigning the similarity value (it can be modified using other values)
								// begin if #1
								if(s.equals(t)){
									simValue = 0.05;
									break;
								} // end if #1
								// begin else #1
								else{
									if(isHas.contains(s) && isHas.contains(t)){
										return NO_MATCH;
									}
									else if(prep.contains(s) && prep.contains(t)){
										return NO_MATCH;
									}
									else{
										simValue = 0.0;
									}
								} // end else #1
							}
						}
					}
					/* ------------- END FOR #2 --------------- */
					
				}					
			}
		}
		/* ------------- END FOR #1 --------------- */
		
		return simValue;
	}
	
	/**
	 * Overridden method
	 * Select best instead of solving the stable marriage problem
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#oneToOneMatching(SimilarityMatrix matrix)
	 * @author michele
	 */
	protected Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix){
		ArrayList<Mapping> list = matrix.chooseBestN();
		Alignment<Mapping> result = new Alignment<Mapping>();
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getSimilarity() < getThreshold()){
				break;
			}
			result.addMapping(list.get(i));
		}
		return result;
		
	}
	
	/**
	 * Overridden method
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#getDescriptionString()
	 * @author michele
	 */
	public String getDescriptionString() {
		return "The Advanced Similarity Matcher (ASM for short) is a matching method that compare the source and the target concepts\n" +
				"by looking at the words that compose them and use a string-matching technique to provide the overall result.\n" +
				"The idea is that we try to look inside the strings to see if there is some mapping between the words composing\n" +
				"the string if the word is compound and see if we can find some words that can tell us if there can be a mapping between them.\n\n" +
				"The ASM method is a matcher that belongs to the First Layer Matchers, meaning that it doesn't require another Matcher to\n" +
				"create an initial similarity values between the nodes and can be easily combined with other string-matching techniques.\n" +
				"ASM needs to get parameters to specify the technique to use when comparing strings and words composing them\n\n";
	}
	
	/******************************************* SUPPORT METHODS *******************************************************/
	
	/**
	 * startValue gives a starting value for the similarity of two words with respect to the difference in number of words composing them
	 * @param wordDistance number of words that a compound word has more (or less) with respect to another one
	 * @author michele
	 */
	protected double computeValue(double startValue, int wordDistance, float avgWordLength){
		switch(wordDistance){ // wordDistance determines the different kind of weights and works out uncertainty
			case 0:
				start_value_contribution = 1.0;
				word_distance_contribution = 0.0;
				avg_word_number_contribution = 0.0;
				break;
			case 1:
				start_value_contribution = 0.75;
				word_distance_contribution = 0.0;
				avg_word_number_contribution = 0.25;
				break;
			case 2:
				start_value_contribution = 0.65;
				word_distance_contribution = 0.0;
				avg_word_number_contribution = 0.35;
				break;
			default:
				start_value_contribution = 0.60;
				word_distance_contribution = 0.15;
				avg_word_number_contribution = 0.25;
				break;
		}
		
		double startSimilarity = startValue * start_value_contribution; // similarity weighted to contribution value
		double distContribution = (1 - Math.exp(wordDistance - 2.5)) * word_distance_contribution; // exp weighted contribution
		double quantityContribution = (1.0/Math.PI * Math.atan(avgWordLength - min_word_distance) + 0.5) * avg_word_number_contribution; // arctan weighted contribution starting from 0.125
		return Math.min(1, startSimilarity + distContribution + quantityContribution);// + distContribution + quantityContribution);
	}
	
	/**
	 * Overridden method
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#isNonContent()
	 * @author michele
	 */
	protected boolean isNonContent(String s){
		return (super.isNonContent(s.toLowerCase()) || isRelevantString(s.toLowerCase()));
	}
	
	/**
	 * isRelevantString: check if the string can be used to tell us immediately if two concepts cannot match
	 * NOTE: using isHas list is useful to kill wrong mappings like "isPartOf/hasPartOf" which are very easy to match 
	 * but it also wrongly kills good mappings like "hasAuhtor/isWrittenBy" which are more difficult to match. This is a trade-off.
	 * (last mapping example taken from cmt-edas reference alignment)
	 * @author michele
	 */
	private boolean isRelevantString(String s){
		return (prep.contains(s.toLowerCase()) || isHas.contains(s.toLowerCase()));
	}
	
	/**
	 * createWordsList: create an ArrayList of strings for relevant words from an Array
	 * @author michele
	 */
	private ArrayList<String> createWordsList(String[] word){
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < word.length; i++){
			if(!isNonContent(word[i])){
				list.add(word[i]);
			}
		}
		return list;		
	}
	
	/**
	 * Overridden method 
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#initializeVariables()
	 * @author michele
	 */
	protected void initializeVariables() {
		super.initializeVariables();
		sourceWords = new ArrayList<String>();
		targetWords = new ArrayList<String>();
		
		isHas.add("is");
		isHas.add("are");
		isHas.add("was");
		isHas.add("were");
		isHas.add("has");
		isHas.add("have");
		//isHas.add("had"); // risky because it can be used in past perfect
		
		prep.add("to"); 
		prep.add("at"); 
		prep.add("as"); 
		prep.add("in");
		prep.add("on");
		prep.add("of");
		prep.add("by");
		prep.add("for");
	}
	
	/**
	 * Overridden method 
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#alignNodesOneByOnegetParametersPanel()
	 * @author michele
	 */
	public AbstractMatcherParametersPanel getParametersPanel(){
		if(parametersPanel == null){
			parametersPanel = new AdvancedSimilarityMatcherParametersPanel();
		}
		return parametersPanel;
	}
	
}
