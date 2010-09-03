/**
 * 
 */
package am.app.mappingEngine.baseSimilarity.compoundWordSimilarity;

import java.util.ArrayList;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.ontology.Node;

/**
 * @author Michele Caci
 *
 */
public class CompoundWordSimilarityMatcher extends BaseSimilarityMatcher {

	private static ArrayList<String> isHas = new ArrayList<String>(2);
	private static ArrayList<String> prep = new ArrayList<String>();
	/**
	 * 
	 */
	public CompoundWordSimilarityMatcher() {
		super();
		param = new CompoundWordSimilarityParameters();
		
		isHas.add("is");
		isHas.add("has");
		
		prep.add("to"); 
		prep.add("at"); 
		prep.add("in");
		prep.add("on");
		prep.add("of");
		prep.add("for");
	}

	/**
	 * @param param_new
	 */
	public CompoundWordSimilarityMatcher(CompoundWordSimilarityParameters param_new) {
		super(param_new);
		
		isHas.add("is");
		isHas.add("has");
		
		prep.add("to"); 
		prep.add("at"); 
		prep.add("in");
		prep.add("on");
		prep.add("of");
		prep.add("for");
	}
	
	/**
	 * Overridden method 
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#alignTwoNodes(Node source, Node target, alignType typeOfNodes)
	 * @author michele
	 */
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) throws Exception {
		// Step 0: tokenize source and target nodes (if possible) and separate by relevance
		// System.out.println(source.getLocalName() + " " + target.getLocalName());
		
		// prepare list of data
		String sLN = super.treatString(source.getLocalName());
		String tLN = super.treatString(target.getLocalName());
		
		// Step 1: produce first comparisons and create a local alignment matrix
		double simValue = nonContentWordCheck(sLN.split("\\s"), tLN.split("\\s"));
		
		// Step 2: choose the best 		
		return new Alignment(source, target, 0.0);
	}
	
	/**
	 * Overridden method 
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#alignNodesOneByOnegetParametersPanel()
	 * @author michele
	 */
	public AbstractMatcherParametersPanel getParametersPanel(){
		if(parametersPanel == null){
			parametersPanel = new CompoundWordSimilarityMatcherParametersPanel();
		}
		return parametersPanel;
	}
	
	/**
	 *
	 */
	private double nonContentWordCheck(String[] sourceLocalName, String[] targetLocalName){
		double simValue = 0.0;
		
		
		
		for(int i = 0; i < sourceLocalName.length; i++){
			if(isNonContent(sourceLocalName[i])){
				if(isRelevantString(sourceLocalName[i])){
					for(int j = 0; j < targetLocalName.length; j++){
						if(isRelevantString(targetLocalName[j]) && sourceLocalName[i].equals(targetLocalName[j])){
							simValue = 0.5;
							break;
						}
						else if(isRelevantString(targetLocalName[j]) && !sourceLocalName[i].equals(targetLocalName[j])){
							simValue = 0.0;
						}
					}
				}					
			}
		}
		
		return simValue;
	}
	
	/**
	 * Overridden method
	 * We need particular care for some words
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#isNonContent()
	 * @author michele
	 */
	private boolean isNonContent(String s){
	    
		if(s.equalsIgnoreCase("the") || 
		   s.equalsIgnoreCase("this") || 
		   s.equalsIgnoreCase("are") || 
		   s.equalsIgnoreCase("a") || 
		   s.equalsIgnoreCase("or") ||
		   s.equalsIgnoreCase("and") || 
		   s.equalsIgnoreCase("that") ||
		   s.equalsIgnoreCase("is") || 
		   s.equalsIgnoreCase("has") || 
		   s.equalsIgnoreCase("to") || 
		   s.equalsIgnoreCase("at") || 
		   s.equalsIgnoreCase("in") ||
		   s.equalsIgnoreCase("of") ||
		   s.equalsIgnoreCase("for") ) 
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Overridden method
	 * We need particular care for some words
	 * @see am.app.mappingEngine.BaseSimilarityMatcher#isNonContent()
	 * @author michele
	 */
	private boolean isRelevantString(String s){
	    
		if(s.equalsIgnoreCase("is") || 
		   s.equalsIgnoreCase("has") || 
		   s.equalsIgnoreCase("to") || 
		   s.equalsIgnoreCase("at") || 
		   s.equalsIgnoreCase("in") ||
		   s.equalsIgnoreCase("of") ||
		   s.equalsIgnoreCase("for") )
		{
			return true;
		}
		else{
			return false;
		}
	}
	
}
