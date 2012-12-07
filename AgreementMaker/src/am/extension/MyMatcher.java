package am.extension;


import java.util.HashMap;
import java.util.Map;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.similarity.JaroWinklerSim;
import am.app.similarity.LevenshteinEditDistance;
import am.extension.semanticExplanation.CombinationCriteria;
import am.extension.semanticExplanation.ExplanationNode;
import am.extension.semanticExplanation.SemanticExpln;
import am.utility.FromWordNetUtils;

public class MyMatcher extends AbstractMatcher {
	
    private static final long serialVersionUID = -3772449944360959530L;

    private ExplanationNode stringSimilarityExplanation = new ExplanationNode("String Similarity");
    private ExplanationNode wordNetSimilarityExplanation = new ExplanationNode("WordNet Similarity");
    private ExplanationNode wordNetStringCombinationExplanation = new ExplanationNode("WordNet- String Combination");
    private ExplanationNode absoluteSimilarityExplanation = new ExplanationNode("Absolute Similarity");
    private ExplanationNode resultExplanation = new ExplanationNode("Final Explanation");
    private ExplanationNode levenshteinExplanation = new ExplanationNode("Levenshtein Distance");
    private ExplanationNode jarowinglerExplanation = new ExplanationNode("Jaro-Wingler metric");
    
    
    public MyMatcher() {
        setName("My Matcher"); // change this to something else if you want
    }

    /* (non-Javadoc)
     * @see am.app.mappingEngine.AbstractMatcher#alignTwoNodes(am.app.ontology.Node, am.app.ontology.Node, am.app.mappingEngine.AbstractMatcher.alignType, am.app.mappingEngine.SimilarityMatrix)
     */
    @Override
    protected Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix) throws Exception {
    	resultExplanation = new ExplanationNode("Final Explanation");
    	levenshteinExplanation = new ExplanationNode("Levenshtein Distance");
    	jarowinglerExplanation = new ExplanationNode("Jaro-Wingler metric");
    	absoluteSimilarityExplanation = new ExplanationNode("Absolute Similarity");
    	wordNetStringCombinationExplanation = new ExplanationNode("WordNet- String Combination");
    	wordNetSimilarityExplanation = new ExplanationNode("WordNet Similarity");
    	stringSimilarityExplanation = new ExplanationNode("String Similarity");
    	
    	if(source.getLocalName() != null) {
    		resultExplanation.setSource(source.getLocalName());
    	}
    	if(target.getLocalName() != null) {
    		resultExplanation.setTarget(target.getLocalName());
    	}
        FromWordNetUtils wordNetUtils = new FromWordNetUtils();

        Map<String, String> sourceMap = getInputs(source);
        Map<String, String> targetMap = getInputs(target);
        
        
        /*
         * Finding out the string similarity value between source node and target node
         * using Leveinshtein and Jaro Winkler string similarity metric
         */
        
        double stringSimilarity = findStringSimilarity(sourceMap, targetMap);
        
        double finalSimilarity = 0;
        if(source.getLocalName().equals("chapters") && target.getLocalName().equals("annotation")) {
        	System.out.println("Testing here");
        }

        /*
         * Checking if values already map exactly, then we set absolute similarity
         */
        
        if (    (sourceMap.containsKey("name") && targetMap.containsKey("name") && sourceMap.get("name").equals(targetMap.get("name"))) || 
                (sourceMap.containsKey("name") && targetMap.containsKey("label") && sourceMap.get("name").equals(targetMap.get("label"))) ||
                (sourceMap.containsKey("name") && targetMap.containsKey("comment") && sourceMap.get("name").equals(targetMap.get("comment"))  ) ){
            finalSimilarity = 1.0;
            absoluteSimilarityExplanation.setDescription("Absolute Similarity");
            absoluteSimilarityExplanation.setVal(1.0);
        }

        else if((sourceMap.containsKey("label") && targetMap.containsKey("label") && sourceMap.get("label").equals(targetMap.get("label"))) ||
                (sourceMap.containsKey("label") && targetMap.containsKey("comment") && sourceMap.get("label").equals(targetMap.get("comment"))) ||
                (sourceMap.containsKey("label") && targetMap.containsKey("name") && sourceMap.get("label").equals(targetMap.get("name"))) ||
                
                (sourceMap.containsKey("comment") && targetMap.containsKey("label") && sourceMap.get("comment").equals(targetMap.get("label"))) ||
                (sourceMap.containsKey("comment") && targetMap.containsKey("name") && sourceMap.get("comment").equals(targetMap.get("name")))  ||
                (sourceMap.containsKey("comment") &&targetMap.containsKey("comment") && sourceMap.get("comment").equals(targetMap.get("comment")))) {
                if(finalSimilarity ==0)
                	finalSimilarity = 0.9;
                
                absoluteSimilarityExplanation.setDescription("Absolute Similarity");
                absoluteSimilarityExplanation.setVal(0.9);
        }
        /*
         * Computing Synonym Similarity using Wordnet
         */
        if (wordNetUtils.areSynonyms(source.getLabel(), target.getLabel()) || 
                wordNetUtils.areSynonyms(source.getLocalName(), target.getLocalName()) || 
                wordNetUtils.areSynonyms(source.getComment(), target.getComment()) ||
                wordNetUtils.areSynonyms(source.getLabel(), target.getLocalName()) || 
                wordNetUtils.areSynonyms(source.getLabel(), target.getComment()) ||
                wordNetUtils.areSynonyms(source.getComment(), target.getLocalName()) || 
                wordNetUtils.areSynonyms(source.getComment(), target.getLabel()) ||
                wordNetUtils.areSynonyms(source.getLocalName(), target.getLabel())|| 
                wordNetUtils.areSynonyms(source.getLocalName(), target.getComment())) {
            if(finalSimilarity == 0)
            	finalSimilarity = (.80);
            wordNetSimilarityExplanation.setDescription("WordNet Similarity");
            wordNetSimilarityExplanation.setVal(.8);
            
            wordNetStringCombinationExplanation.setVal(.8);
            wordNetStringCombinationExplanation.setCriteria(CombinationCriteria.VOTING);
        } 
        if(finalSimilarity <stringSimilarity){
            finalSimilarity = stringSimilarity;
            
            wordNetStringCombinationExplanation.setVal(stringSimilarity);
            wordNetStringCombinationExplanation.setCriteria(CombinationCriteria.VOTING);
        }
        wordNetStringCombinationExplanation.addChild(wordNetSimilarityExplanation);
        wordNetStringCombinationExplanation.addChild(stringSimilarityExplanation);
        
        resultExplanation.addChild(wordNetStringCombinationExplanation);
        resultExplanation.addChild(absoluteSimilarityExplanation);
        
        resultExplanation.setVal(finalSimilarity);
        resultExplanation.setCriteria(CombinationCriteria.VOTING);
       
       //storing into the appropriate location inside the explanation matrix
        if(!sourceMap.isEmpty() && !targetMap.isEmpty()) {
        	setExplanationMatrix(source,target);
        }
        return new Mapping(source, target, finalSimilarity);
    }
    
    /**
     * @param source
     * @param target
     * Method to insert the ExplanationNode into the Class or Property Explanation Matrix at the appropriate location
     */
    private void setExplanationMatrix(Node source, Node target) {
    	Integer sourceIndex = null;
    	Integer targetIndex = null;
    	if(getSourceOntology().getClassesList().contains(source) && getTargetOntology().getClassesList().contains(target)) {
    		sourceIndex = getSourceOntology().getClassesList().indexOf(source);
    		targetIndex = getTargetOntology().getClassesList().indexOf(target);
    	} else if(getSourceOntology().getPropertiesList().contains(source) && getTargetOntology().getPropertiesList().contains(target)) {
    		sourceIndex = getSourceOntology().getPropertiesList().indexOf(source);
    		targetIndex = getTargetOntology().getPropertiesList().indexOf(target);
    	}
    	if(sourceIndex != null && targetIndex != null) {
    		if(source.isClass() && target.isClass()) {
            	SemanticExpln.getInstance().getClassExplanationMatrix()[sourceIndex][targetIndex] = resultExplanation;
    		} else if(source.isProp() && target.isProp()) {
            	SemanticExpln.getInstance().getPropertiesExplanationMatrix()[sourceIndex][targetIndex] = resultExplanation;
    		}
    	}
	}

	/**
     * Essential combination algorithm to compute the basic string similarity between the source and target. 
     * Three Algorithms are combined- Levenshtein and Jaro-Winkler.
     * @param sourceMap
     * @param targetMap
     * @return
     */

    private double findStringSimilarity(Map<String, String> sourceMap, Map<String, String> targetMap) {
        double levenshteinSimilarity = 0;
        double jarowinglerSimilarity = 0;
        int divisor = 0;

       
        if (sourceMap.containsKey("name") && targetMap.containsKey("name")) {
            levenshteinSimilarity += 2 * levenshteinStringSimilarity(sourceMap.get("name"), targetMap.get("name"));
            divisor += 2;
        }
        if (sourceMap.containsKey("label") && targetMap.containsKey("label")) {
            levenshteinSimilarity += 2 * levenshteinStringSimilarity(sourceMap.get("label"), targetMap.get("label"));
            divisor += 2;
        }
        if (sourceMap.containsKey("comment") && targetMap.containsKey("comment")) {
            levenshteinSimilarity += 2 * levenshteinStringSimilarity(sourceMap.get("comment"), targetMap.get("comment"));
            divisor += 2;
        }

        if (sourceMap.containsKey("name") && targetMap.containsKey("label")) {
            levenshteinSimilarity += levenshteinStringSimilarity(sourceMap.get("name"), targetMap.get("label"));
            divisor++;
        }
        if (sourceMap.containsKey("name") && targetMap.containsKey("comment")) {
            levenshteinSimilarity += levenshteinStringSimilarity(sourceMap.get("name"), targetMap.get("comment"));
            divisor++;
        }

        if (sourceMap.containsKey("label") && targetMap.containsKey("comment")) {
            levenshteinSimilarity += levenshteinStringSimilarity(sourceMap.get("label"), targetMap.get("comment"));
            divisor++;
        }
        if (sourceMap.containsKey("label") && targetMap.containsKey("name")) {
            levenshteinSimilarity += levenshteinStringSimilarity(sourceMap.get("label"), targetMap.get("name"));
            divisor++;
        }

        if (sourceMap.containsKey("comment") && targetMap.containsKey("name")) {
            levenshteinSimilarity += levenshteinStringSimilarity(sourceMap.get("comment"), targetMap.get("name"));
            divisor++;
        }
        if (sourceMap.containsKey("comment") && targetMap.containsKey("label")) {
            levenshteinSimilarity += levenshteinStringSimilarity(sourceMap.get("comment"), targetMap.get("label"));
            divisor++;
        }

        levenshteinSimilarity = levenshteinSimilarity / divisor;
        
        levenshteinSimilarity = pruneValues(levenshteinSimilarity);
        
        levenshteinExplanation.setVal(levenshteinSimilarity);
        levenshteinExplanation.setDescription("Levenshtein Distance");
        
        divisor = 0;
        if (sourceMap.containsKey("name") && targetMap.containsKey("name")) {
            jarowinglerSimilarity += 2 * jarowinklerStringSimilarity(sourceMap.get("name"), targetMap.get("name"));
            divisor += 2;
        }
        if (sourceMap.containsKey("label") && targetMap.containsKey("label")) {
            jarowinglerSimilarity += 2 * jarowinklerStringSimilarity(sourceMap.get("label"), targetMap.get("label"));
            divisor += 2;
        }
        if (sourceMap.containsKey("comment") && targetMap.containsKey("comment")) {
            jarowinglerSimilarity += 2 * jarowinklerStringSimilarity(sourceMap.get("comment"), targetMap.get("comment"));
            divisor += 2;
        }

        if (sourceMap.containsKey("name") && targetMap.containsKey("label")) {
            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("name"), targetMap.get("label"));
            divisor++;
        }
        if (sourceMap.containsKey("name") && targetMap.containsKey("comment")) {
            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("name"), targetMap.get("comment"));
            divisor++;
        }

        if (sourceMap.containsKey("label") && targetMap.containsKey("comment")) {
            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("label"), targetMap.get("comment"));
            divisor++;
        }
        if (sourceMap.containsKey("label") && targetMap.containsKey("name")) {
            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("label"), targetMap.get("name"));
            divisor++;
        }

        if (sourceMap.containsKey("comment") && targetMap.containsKey("name")) {
            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("comment"), targetMap.get("name"));
            divisor++;
        }
        if (sourceMap.containsKey("comment") && targetMap.containsKey("label")) {
            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("comment"), targetMap.get("label"));
            divisor++;
        }

        jarowinglerSimilarity = jarowinglerSimilarity / divisor;
        jarowinglerSimilarity = pruneValues(jarowinglerSimilarity);
        
        jarowinglerExplanation.setVal(jarowinglerSimilarity);
        jarowinglerExplanation.setDescription("JaroWingler Similarity Metric");
        /*
         * Weighted average of String Similarity
         * 
         * Through iterations, I came to a conclusion that the results are given
         * in the ratio of Levenshtein > Jaro-Wingler So, the
         * corresponding weight-age was given while calculating the mean.
         */
        double finalsimilarity = pruneValues((3 * levenshteinSimilarity + jarowinglerSimilarity) / 4);
        
        stringSimilarityExplanation.addChild(jarowinglerExplanation);
        stringSimilarityExplanation.addChild(levenshteinExplanation);
        
        stringSimilarityExplanation.setVal(finalsimilarity);
        stringSimilarityExplanation.setDescription("Combined String Similarity");
        stringSimilarityExplanation.setCriteria(CombinationCriteria.LWC);
        return finalsimilarity;
    }

    
    /**
     * @param distance
     * @return
     * Rounding the double value to two decimal places
     */
    private static double pruneValues(double distance) {
    	distance = (double)Math.round(distance * 100) / 100;
    	return distance;
    }
    
    /**
     * Takes in a node, and returns a Map<String,String> of label, localName and Comment
     */
    private Map<String, String> getInputs(Node node) {
        Map<String, String> stringMap = new HashMap<String, String>();
        if (node.getLabel() != null && !node.getLabel().isEmpty())
            stringMap.put("label", node.getLabel().toLowerCase().replaceAll("\\s", ""));
        if (node.getLocalName() != null && !node.getLocalName().isEmpty())
            stringMap.put("name", node.getLocalName().toLowerCase().replaceAll("\\s", ""));
        if (node.getComment() != null && !node.getComment().isEmpty())
            stringMap.put("comment", node.getComment().toLowerCase().replaceAll("\\s", ""));

        return stringMap;
    }
    

    /* 
     * Overriding the beforeAlignOperations to initialize our explanation Matrix
     */
    @Override
    protected void beforeAlignOperations() throws Exception {
    	super.beforeAlignOperations();
    	Ontology source = getSourceOntology();
    	Ontology target = getTargetOntology();
    	SemanticExpln.getInstance().setClassExplanationMatrix(source.getTreeCount(), target.getTreeCount());  
    	SemanticExpln.getInstance().setPropertiesExplanationMatrix(source.getTreeCount(), target.getTreeCount());    	

    }

    /* 
     * Overriding the afterSelectionOperations to set the jung visualization tree for every aligned mapping
     */
    @Override
    protected void afterSelectionOperations() {
       	super.afterSelectionOperations();
    	Alignment<Mapping> alignmentMappings =  getAlignment();
    	SemanticExpln.findUniversalMostSignificantPath(alignmentMappings);
    	for(Mapping m:alignmentMappings) {
    		if(m.getEntity1().isClass() && m.getEntity2().isClass()) {
    			SemanticExpln.getInstance().getClassExplanationMatrix()[m.getEntity1().getIndex()][m.getEntity2().getIndex()].describeTopDown();
    		} else if(m.getEntity1().isProp() && m.getEntity2().isProp()) {
    			SemanticExpln.getInstance().getPropertiesExplanationMatrix()[m.getEntity1().getIndex()][m.getEntity2().getIndex()].describeTopDown();

    		}
    	}
    }
    

    /**
     * Levenshtein String Similarity.
     */
    private static double levenshteinStringSimilarity(String sourceString, String targetString) {

        LevenshteinEditDistance levenshtein = new LevenshteinEditDistance();
      //  levenshtein.calculate();
        return levenshtein.getSimilarity(sourceString, targetString);
    }

    /**
     * Jaro-Winkler String Similarity.
     */
    private static double jarowinklerStringSimilarity(String sourceString, String targetString) {
    	JaroWinklerSim jaro = new JaroWinklerSim();
   //     jaro.calculate();
        return jaro.getSimilarity(sourceString, targetString);
    }

}