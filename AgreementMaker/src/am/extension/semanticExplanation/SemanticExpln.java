package am.extension.semanticExplanation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.instanceMatchers.WordNetUtils;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.app.similarity.AMSubEditSim;
import am.app.similarity.JaroWinklerSim;
import am.app.similarity.LevenshteinEditDistance;

public class SemanticExpln {


	private static ParametricStringMatcher	m_asm;
	private static WordNetUtils wordnet;
	private static CustomWordNet cusWordNet;
    private static ExplanationResults er;

	public static void main(String args[]) {
		
		if( wordnet == null ) wordnet = new WordNetUtils();
		cusWordNet = new CustomWordNet();
		// Step 1.  Load my ontologies.
		
		String sourceOntFile = "/Users/meriyathomas/Documents/fall2012/DWSemantics/benchmark/101/onto.rdf";
		String targetOntFile = "/Users/meriyathomas/Documents/fall2012/DWSemantics/benchmark/205/onto.rdf";
		
		Ontology sourceOntology = readOntology(sourceOntFile);
		Ontology targetOntology = readOntology(targetOntFile);
		
		// Step 2. Instantiate the Advanced Similarity Matcher.
		
//		AbstractMatcher asm = MatcherFactory.getMatcherInstance(MatchersRegistry.AdvancedSimilarity.getMatcherName());
		// Step 3. Set the parameters for the matcher.
		
		ParametricStringParameters asmp = new ParametricStringParameters();
		asmp.threshold = 0.75;  // set the threshold
		
		asmp.maxSourceAlign = 1; // set the source cardinality
		asmp.maxTargetAlign = 1; // set the target cardinality
		
		m_asm = new ParametricStringMatcher(asmp);

	
	//	asm.setParameters(asmp); // set the matcher parameters
		m_asm.setOntologies(sourceOntology, targetOntology);
	//	asm.setSourceOntology(sourceOntology);  // set the source ontology for the matcher
	//	asm.setTargetOntology(targetOntology);  // set the target ontology for the matcher
		
		
		// Step 4.  Align the ontologies.
		
		try {
			m_asm.match();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		Alignment<Mapping> alignmentMappings = m_asm.getAlignment();
		
		for(Mapping m:alignmentMappings) {
			System.out.println("Source Node ---> Target Node");
			System.out.println("-----------------------------");
			System.out.println(m.getString());
			System.out.println("Label");
			System.out.println("-----");
			System.out.println(m.getEntity1().getLabel()+" ---> "+m.getEntity2().getLabel());
			System.out.println("Local Name");
			System.out.println("-----------");
			System.out.println(m.getEntity1().getLocalName()+" ---> "+m.getEntity2().getLocalName());
			System.out.println("Comment");			
			System.out.println("-------");
			System.out.println(m.getEntity1().getComment()+" ---> "+m.getEntity2().getComment());
			System.out.println("Their Similarity Value="+m.getSimilarity());
			System.out.println("\n");
			findExplanation(m.getEntity1(),m.getEntity2(),m);
		}
		// Step 5.  Save the alignment.
//		try {
//			String alignmentFilename = "/Users/meriyathomas/Documents/fall2012/DWSemantics/benchmarks/101/refalign.rdf";
//			AlignmentOutput output = new AlignmentOutput(m_asm.getAlignment(), alignmentFilename);
//			String sourceUri = sourceOntology.getURI();
//			String targetUri = targetOntology.getURI();
//			output.write(sourceUri, targetUri, sourceUri, targetUri, m_asm.getName());
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
		
		
	}
	
	public static void findExplanation(Node source, Node target, Mapping m) {
        Map<String, String> sourceMap = getInputs(source);
        Map<String, String> targetMap = getInputs(target);
        er = new ExplanationResults();
        er.setStringSimilarityValue(findStringSimilarity(sourceMap, targetMap));
        System.out.println("String similarity between the two="+er.getStringSimilarityValue());
        if(er.getStringSimilarityValue()==1.0) {
        	er.setAreSameWords(true);
        	System.out.println("Source and Target are the same");
        } else if(areSynonyms(sourceMap, targetMap)){
        	er.setAreTheySynonymns(true);
        } else if(areHypnonyms(sourceMap, targetMap)) {
        	er.setAreTheyHyponymns(true);
        } else if(areHypernyms(sourceMap, targetMap)) {
        	er.setAreTheyHypernymns(true);
        }

	}
	
	private static boolean areSynonyms(Map<String, String> sourceMap, Map<String, String> targetMap) {
		
		/*Currently checking only first level
		 * Checking if source.getLocalName() or source.getLabel() is in the synonym set of target.getLocalName() or target.getLabel()
		 * and vice versa
		*/
		boolean foundSynonym = false;
		
		ArrayList<String> sourceNameSynonymList = new ArrayList<String>();
		ArrayList<String> targetNameSynonymList = new ArrayList<String>();
		ArrayList<String> sourceLabelSynonymList = new ArrayList<String>();
		ArrayList<String> targetLabelSynonymList = new ArrayList<String>();
		
		sourceNameSynonymList = cusWordNet.getSynonyms(sourceMap.get("name"));
		targetNameSynonymList = cusWordNet.getSynonyms(targetMap.get("name"));
		sourceLabelSynonymList = cusWordNet.getSynonyms(sourceMap.get("label"));
		targetLabelSynonymList = cusWordNet.getSynonyms(targetMap.get("label"));

		if(sourceMap.containsKey("name")) {
			if(containsWordInList(targetNameSynonymList, sourceMap.get("name"))) {
				//while change later but for now just printing on screen
				System.out.println("Source:"+sourceMap.get("name")+"is a synonym of Target:"+targetMap.get("name"));
				foundSynonym = true;
			}
		}
		if(targetMap.containsKey("name")) {
			if(containsWordInList(sourceNameSynonymList, targetMap.get("name"))) {
				System.out.println("Target:"+targetMap.get("name")+"is a synonym of Source:"+sourceMap.get("name"));
				foundSynonym = true;
			}
		}
		if(sourceMap.containsKey("label")) {
			if(containsWordInList(targetLabelSynonymList, sourceMap.get("label"))) {
				System.out.println("Source:"+sourceMap.get("label")+"is a synonym of "+targetMap.get("label"));
				foundSynonym = true;
			}
		}
		if(targetMap.containsKey("label")) {
			if(containsWordInList(sourceLabelSynonymList, targetMap.get("label"))) {
				System.out.println("Target:"+targetMap.get("label")+"is a synonym of Source:"+sourceMap.get("label"));
				foundSynonym = true;
			}
		}	
		return foundSynonym;
	}
	
	private static boolean areHypnonyms(Map<String, String> sourceMap, Map<String, String> targetMap) {
		
		/*Currently checking only first level
		 * Checking if source.getLocalName() or source.getLabel() is in the synonym set of target.getLocalName() or target.getLabel()
		 * and vice versa
		*/
		ArrayList<String> sourceNameHyponymList = new ArrayList<String>();
		ArrayList<String> targetNameHyponymList = new ArrayList<String>();
		ArrayList<String> sourceLabelHyponymList = new ArrayList<String>();
		ArrayList<String> targetLabelHyponymList = new ArrayList<String>();
		
		sourceNameHyponymList = cusWordNet.getHyponyms(sourceMap.get("name"));
		targetNameHyponymList = cusWordNet.getHyponyms(targetMap.get("name"));
		sourceLabelHyponymList = cusWordNet.getHyponyms(sourceMap.get("label"));
		targetLabelHyponymList = cusWordNet.getHyponyms(targetMap.get("label"));
		
		boolean foundHyponym = false;
		if(sourceMap.containsKey("name")) {
			if(containsWordInList(targetNameHyponymList, sourceMap.get("name"))) {
				//while change later but for now just printing on screen
				System.out.println("Source:"+sourceMap.get("name")+"is a hyponym of Target:"+targetMap.get("name"));
				foundHyponym = true;
			}
		}
		if(targetMap.containsKey("name")) {
			if(containsWordInList(sourceNameHyponymList, targetMap.get("name"))) {
				System.out.println("Target:"+targetMap.get("name")+"is a hyponym of Source:"+sourceMap.get("name"));
				foundHyponym = true;
			}
		}
		if(sourceMap.containsKey("label")) {
			if(containsWordInList(targetLabelHyponymList, sourceMap.get("label"))) {
				System.out.println("Source:"+sourceMap.get("label")+"is a hyponym of "+targetMap.get("label"));
				foundHyponym = true;
			}
		}
		if(targetMap.containsKey("label")) {
			if(containsWordInList(sourceLabelHyponymList, targetMap.get("label"))) {
				System.out.println("Target:"+targetMap.get("label")+"is a hyponym of Source:"+sourceMap.get("label"));
				foundHyponym = true;
			}
		}	
		return foundHyponym;
	}
	
	private static boolean areHypernyms(Map<String, String> sourceMap, Map<String, String> targetMap) {
		
		/*Currently checking only first level
		 * Checking if source.getLocalName() or source.getLabel() is in the synonym set of target.getLocalName() or target.getLabel()
		 * and vice versa
		*/
		ArrayList<String> sourceNameHypernymList = new ArrayList<String>();
		ArrayList<String> targetNameHypernymList = new ArrayList<String>();
		ArrayList<String> sourceLabelHypernymList = new ArrayList<String>();
		ArrayList<String> targetLabelHypernymList = new ArrayList<String>();
		
		sourceNameHypernymList = cusWordNet.getHyponyms(sourceMap.get("name"));
		targetNameHypernymList = cusWordNet.getHyponyms(targetMap.get("name"));
		sourceLabelHypernymList = cusWordNet.getHyponyms(sourceMap.get("label"));
		targetLabelHypernymList = cusWordNet.getHyponyms(targetMap.get("label"));

		boolean foundHypernym = false;
		
		if(sourceMap.containsKey("name")) {
			if(containsWordInList(targetNameHypernymList, sourceMap.get("name"))) {
				//while change later but for now just printing on screen
				System.out.println("Source:"+sourceMap.get("name")+"is a hyponym of Target:"+targetMap.get("name"));
				foundHypernym = true;
			}
		}
		if(targetMap.containsKey("name")) {
			if(containsWordInList(sourceNameHypernymList, targetMap.get("name"))) {
				System.out.println("Target:"+targetMap.get("name")+"is a hyponym of Source:"+sourceMap.get("name"));
				foundHypernym = true;
			}
		}
		if(sourceMap.containsKey("label")) {
			if(containsWordInList(targetLabelHypernymList, sourceMap.get("label"))) {
				System.out.println("Source:"+sourceMap.get("label")+"is a hyponym of "+targetMap.get("label"));
				foundHypernym = true;
			}
		}
		if(targetMap.containsKey("label")) {
			if(containsWordInList(sourceLabelHypernymList, targetMap.get("label"))) {
				System.out.println("Target:"+targetMap.get("label")+"is a hyponym of Source:"+sourceMap.get("label"));
				foundHypernym = true;
			}
		}	
		return foundHypernym;

	}

	private static boolean containsWordInList(ArrayList<String> list, String word) {
	    for (String p : list) {
	        if (p.equals(word)) {
	            return true;
	        }
	    }
	    return false;
	}
    /**
     * Takes in a node, and returns a Map<String,String> of label, localName and Comment
     */
    private static Map<String, String> getInputs(Node node) {
        Map<String, String> stringMap = new HashMap<String, String>();
        if (node.getLabel() != null && !node.getLabel().isEmpty())
            stringMap.put("label", node.getLabel().toLowerCase().replaceAll("\\s", ""));
        if (node.getLocalName() != null && !node.getLocalName().isEmpty())
            stringMap.put("name", node.getLocalName().toLowerCase().replaceAll("\\s", ""));
        if (node.getComment() != null && !node.getComment().isEmpty())
            stringMap.put("comment", node.getComment().toLowerCase().replaceAll("\\s", ""));

        return stringMap;
    }
    
    /**
     * Essential combination algorithm to compute the basic string similarity between the source and target. 
     * Three Algorithms are combined- Hamming, Levenshtein and Jaro-Winkler.
     * @param sourceMap
     * @param targetMap
     * @return
     */

    private static double findStringSimilarity(Map<String, String> sourceMap, Map<String, String> targetMap) {
        double amSubEditSimilarity = 0;
        double jarowinglerSimilarity = 0;
        int divisor = 0;

        /*
         * Checking simple string similarity
         * 
         *  Levenshtein and Jaro-Winkler are 2 of the very widely used
         * algorithms for string comparison. Here, all possible combinations
         * between label, comment and localName are found for every algorithm,
         * and then the weighted average is found. localName=localName and
         * label=label is given more weightage.
         */


        divisor = 0;
        if (sourceMap.containsKey("name") && targetMap.containsKey("name")) {
        	amSubEditSimilarity += 2 * amSubEditSimilarity(sourceMap.get("name"), targetMap.get("name"));
            divisor += 2;
        }
        if (sourceMap.containsKey("label") && targetMap.containsKey("label") &&
        		amSubEditSimilarity < 0.6) {
        	amSubEditSimilarity += 2 * amSubEditSimilarity(sourceMap.get("label"), targetMap.get("label"));
            divisor += 2;
        }
        if (sourceMap.containsKey("comment") && targetMap.containsKey("comment") &&
        		amSubEditSimilarity < 0.6) {
        	amSubEditSimilarity += 2 * amSubEditSimilarity(sourceMap.get("comment"), targetMap.get("comment"));
            divisor += 2;
        }

        if (sourceMap.containsKey("name") && targetMap.containsKey("label") &&
        		amSubEditSimilarity< 0.6) {
        	amSubEditSimilarity += amSubEditSimilarity(sourceMap.get("name"), targetMap.get("label"));
            divisor++;
        }
        if (sourceMap.containsKey("name") && targetMap.containsKey("comment") &&
        		amSubEditSimilarity < 0.6) {
        	amSubEditSimilarity += amSubEditSimilarity(sourceMap.get("name"), targetMap.get("comment"));
            divisor++;
        }

        if (sourceMap.containsKey("label") && targetMap.containsKey("comment") &&
        		amSubEditSimilarity < 0.6) {
        	amSubEditSimilarity += amSubEditSimilarity(sourceMap.get("label"), targetMap.get("comment"));
            divisor++;
        }
        if (sourceMap.containsKey("label") && targetMap.containsKey("name") &&
        		amSubEditSimilarity < 0.6) {
        	amSubEditSimilarity += amSubEditSimilarity(sourceMap.get("label"), targetMap.get("name"));
            divisor++;
        }

        if (sourceMap.containsKey("comment") && targetMap.containsKey("name") &&
        		amSubEditSimilarity < 0.6) {
        	amSubEditSimilarity += amSubEditSimilarity(sourceMap.get("comment"), targetMap.get("name"));
            divisor++;
        }
        if (sourceMap.containsKey("comment") && targetMap.containsKey("label") &&
        		amSubEditSimilarity < 0.6) {
        	amSubEditSimilarity += amSubEditSimilarity(sourceMap.get("comment"), targetMap.get("label"));
            divisor++;
        }

        amSubEditSimilarity = amSubEditSimilarity / divisor;

//        divisor = 0;
//        if (sourceMap.containsKey("name") && targetMap.containsKey("name")) {
//            jarowinglerSimilarity += 2 * jarowinklerStringSimilarity(sourceMap.get("name"), targetMap.get("name"));
//            divisor += 2;
//        }
//        if (sourceMap.containsKey("label") && targetMap.containsKey("label") &&
//        		jarowinglerSimilarity <0.6) {
//            jarowinglerSimilarity += 2 * jarowinklerStringSimilarity(sourceMap.get("label"), targetMap.get("label"));
//            divisor += 2;
//        }
//        if (sourceMap.containsKey("comment") && targetMap.containsKey("comment") &&
//        		jarowinglerSimilarity < 0.6) {
//            jarowinglerSimilarity += 2 * jarowinklerStringSimilarity(sourceMap.get("comment"), targetMap.get("comment"));
//            divisor += 2;
//        }
//
//        if (sourceMap.containsKey("name") && targetMap.containsKey("label") &&
//        		jarowinglerSimilarity < 0.6) {
//            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("name"), targetMap.get("label"));
//            divisor++;
//        }
//        if (sourceMap.containsKey("name") && targetMap.containsKey("comment") &&
//        		jarowinglerSimilarity < 0.6) {
//            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("name"), targetMap.get("comment"));
//            divisor++;
//        }
//
//        if (sourceMap.containsKey("label") && targetMap.containsKey("comment") &&
//        		jarowinglerSimilarity < 0.6) {
//            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("label"), targetMap.get("comment"));
//            divisor++;
//        }
//        if (sourceMap.containsKey("label") && targetMap.containsKey("name") &&
//        		jarowinglerSimilarity < 0.6) {
//            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("label"), targetMap.get("name"));
//            divisor++;
//        }
//
//        if (sourceMap.containsKey("comment") && targetMap.containsKey("name") &&
//        		jarowinglerSimilarity < 0.6) {
//            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("comment"), targetMap.get("name"));
//            divisor++;
//        }
//        if (sourceMap.containsKey("comment") && targetMap.containsKey("label") &&
//        		jarowinglerSimilarity < 0.6) {
//            jarowinglerSimilarity += jarowinklerStringSimilarity(sourceMap.get("comment"), targetMap.get("label"));
//            divisor++;
//        }
//
//        jarowinglerSimilarity = jarowinglerSimilarity / divisor;

        /*
         * Weighted average of String Similarity
         * 
         * Through iterations, I came to a conclusion that the results are given
         * in the ratio of Levenshtein > Hamming > Jaro-Wingler So, the
         * corresponding weightage was given while calculating the mean.
         */
        double weightedSimilarity =amSubEditSimilarity;
        double nonWeightedSimilarity;
        if (    (sourceMap.containsKey("name") && targetMap.containsKey("name") && sourceMap.get("name").equals(targetMap.get("name"))) || 
        		(sourceMap.containsKey("label") && targetMap.containsKey("label") && sourceMap.get("label").equals(targetMap.get("label"))) ||
        		 (sourceMap.containsKey("comment") &&targetMap.containsKey("comment") && sourceMap.get("comment").equals(targetMap.get("comment")))
        		) {
         	nonWeightedSimilarity = 1.0;
            return nonWeightedSimilarity;
        }

        else if(   (sourceMap.containsKey("name") && targetMap.containsKey("label") && sourceMap.get("name").equals(targetMap.get("label"))) ||
                   (sourceMap.containsKey("name") && targetMap.containsKey("comment") && sourceMap.get("name").equals(targetMap.get("comment"))) ||
           
        		(sourceMap.containsKey("label") && targetMap.containsKey("comment") && sourceMap.get("label").equals(targetMap.get("comment"))) ||
                (sourceMap.containsKey("label") && targetMap.containsKey("name") && sourceMap.get("label").equals(targetMap.get("name"))) ||
                
                (sourceMap.containsKey("comment") && targetMap.containsKey("label") && sourceMap.get("comment").equals(targetMap.get("label"))) ||
                (sourceMap.containsKey("comment") && targetMap.containsKey("name") && sourceMap.get("comment").equals(targetMap.get("name")))
               ) {
        	nonWeightedSimilarity = 0.9;
        	return nonWeightedSimilarity;
        } else {
        	return weightedSimilarity;
        }
    }


    /**
     * Levenshtein String Similarity.
     */
    private static double levenshteinStringSimilarity(String sourceString, String targetString) {

    	LevenshteinEditDistance levenshtein = new LevenshteinEditDistance();
        return levenshtein.getSimilarity(sourceString.toLowerCase(),targetString.toLowerCase());
    }
    
    private static double amSubEditSimilarity(String sourceString, String targetString) {

    	AMSubEditSim amSubEditSim = new AMSubEditSim();
        return amSubEditSim.getSimilarity(sourceString.toLowerCase(),targetString.toLowerCase());
    }



    /**
     * Jaro-Winkler String Similarity.
     */
    private static double jarowinklerStringSimilarity(String sourceString, String targetString) {

        JaroWinklerSim jaro = new JaroWinklerSim();
        return jaro.getSimilarity(sourceString.toLowerCase(), targetString.toLowerCase());
    }

    

	/**
	 * Method to read in an OWL ontology.
	 * @param sourceOntFile Path of the OWL ontology file.
	 * @return Ontology data structure.
	 */
	private static Ontology readOntology(String ontoURI) {
		
		Ontology onto = OntoTreeBuilder.loadOntology(ontoURI, OntologyLanguage.RDFS, OntologySyntax.RDFXML);
//		OntoTreeBuilder ontoBuilder = new OntoTreeBuilder( sourceOntFile,
//				Ontology.SOURCE, GlobalStaticVariables.LANG_OWL, 
//				GlobalStaticVariables.SYNTAX_RDFXML, 
//				false, false);
//		
//		ontoBuilder.build(OntoTreeBuilder.Profile.noReasoner);  // read in the ontology file, create the Ontology object.
		
		return onto;
	}
	
}