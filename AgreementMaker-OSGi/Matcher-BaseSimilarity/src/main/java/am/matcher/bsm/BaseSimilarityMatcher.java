package am.matcher.bsm;

import java.util.Iterator;

import am.api.alignment.AlignmentContext;
import am.api.matcher.Matcher;
import am.api.matcher.MatcherCategory;
import am.api.matcher.MatcherProperties;
import am.api.matcher.MatcherResult;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.StringUtil.PorterStemmer;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.profiling.OntologyProfiler;
import am.utility.Pair;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;


public class BaseSimilarityMatcher implements Matcher {
    // JAWS WordNet interface
    private transient WordNetDatabase wordnet = null;


    @Override
    public MatcherProperties getProperties() {
        return new MatcherProperties.Builder()
                .setName("Base Similarity Matcher")
                .setCategory(MatcherCategory.SYNTACTIC)
                .setMinInputMatchers(0)
                .setMaxInputMatchers(0)
                .build();
    }

    @Override
    public MatcherResult match(AlignmentContext task) {
        return null;
    }

    protected void initializeVariables() {
        String cwd = System.getProperty("user.dir");
        String wordnetdir = cwd + "/wordnet-3.0";
        System.setProperty("wordnet.database.dir", wordnetdir);
    }

    /*
	 * This function does the main base similarity algorithm.
	 */
    @Override
    public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix) throws Exception {


		
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

        OntologyProfiler pro = Core.getInstance().getOntologyProfiler();
        if (pro != null && ((BaseSimilarityParameters) param).useProfiling) {
            // we are using ontology profiling
            double highestSimilarity = 0.0d;
            Pair<String, String> highestPair = null;

            Iterator<Pair<String, String>> annIter = pro.getAnnotationIterator(source, target);
            while (annIter.hasNext()) {
                Pair<String, String> currentPair = annIter.next();
                double currentSimilarity = calculateSimilarity(currentPair.getLeft(), currentPair.getRight());
                if (currentSimilarity > highestSimilarity) {
                    highestSimilarity = currentSimilarity;
                    highestPair = currentPair;
                }
            }

            if (highestSimilarity == 0.0d) return null;
            else {
                StringBuilder prov = new StringBuilder();
                if (param.storeProvenance) {
                    //set provenance string
                    StringBuilder proc1 = new StringBuilder();
                    if (((BaseSimilarityParameters) param).useDictionary)
                        proc1.append("dictionary");
                    else if (highestSimilarity == 1)
                        proc1.append("exact match \"" + highestPair.getLeft());
                    else if (highestSimilarity == .95)
                        proc1.append("stem \"" + norm1.normalize(highestPair.getLeft()));
                    else if (highestSimilarity == .90)
                        proc1.append("stem \"" + norm2.normalize(highestPair.getLeft()));
                    else //has to be .8d sim here
                        proc1.append("stem \"" + norm3.normalize(highestPair.getLeft()));

                    //the provenance string has the left and right pair with the way it was matched
                    prov.append("\t********BaseSimilarityMatcher********\n");
                    prov.append("sim(\"");
                    prov.append(highestPair.getLeft());
                    prov.append("\", \"");
                    prov.append(highestPair.getRight());
                    prov.append("\") = ");
                    prov.append(highestSimilarity);
                    prov.append("\nmatched with ");
                    prov.append(proc1);
                    prov.append("\"");
                }
                Mapping pmapping = new Mapping(source, target, highestSimilarity, relation, typeOfNodes);
                if (param.storeProvenance && highestSimilarity >= param.threshold) {
                    prov.append("\n");
                    pmapping.setProvenance(prov.toString());
                }
                return pmapping;
            }
        } else {
            //throw new AMException("Base Similarity Matcher requires Annotation Profiling to be setup.");
            // we are not using ontology profiling
            return withoutProfiling(source, target, typeOfNodes);
        }
    }

    /**
     * This is exactly the algorithm before ontology profiling.
     *
     * @param source
     * @param target
     * @return
     */
    private Mapping withoutProfiling(Node source, Node target, alignType typeOfNode) {
        if (param != null && ((BaseSimilarityParameters) param).useDictionary) {  // Step 3a
            String sourceName = source.getLabel();
            String targetName = target.getLabel();

            // Step 1:		run treatString on each name to clean it up
            sourceName = treatString(sourceName);
            targetName = treatString(targetName);


            // Step 2:	If the labels are equal, then return a similarity of 1
            if (sourceName.equalsIgnoreCase(targetName)) {
                return new Mapping(source, target, 1.0d, MappingRelation.EQUIVALENCE, typeOfNode);
            }
            // if we haven't initialized our wordnet database, do it
            if (wordnet == null)
                wordnet = WordNetDatabase.getFileInstance();

            // The user wants us to use a dictionary to find related words

            Synset[] sourceNouns = wordnet.getSynsets(sourceName, SynsetType.NOUN);
            Synset[] targetNouns = wordnet.getSynsets(targetName, SynsetType.NOUN);

            float nounSimilarity = getSensesComparison(sourceNouns, targetNouns);

            Synset[] sourceVerbs = wordnet.getSynsets(sourceName, SynsetType.VERB);
            Synset[] targetVerbs = wordnet.getSynsets(targetName, SynsetType.VERB);

            float verbSimilarity = getSensesComparison(sourceVerbs, targetVerbs);

            // select the best similarity found. (either verb or noun)
            if (nounSimilarity > verbSimilarity) {
                String provenanceString = null;
                if (param.storeProvenance) {
                    //set provenance string
                    provenanceString = "\t********BaseSimilarityMatcher********\n";
                    provenanceString += "sim(\""
                            + sourceName + "\", \""
                            + targetName
                            + "\") = "
                            + nounSimilarity
                            + "\nmatched by label based noun similarity";
                }
                Mapping pmapping = new Mapping(source, target, nounSimilarity, MappingRelation.EQUIVALENCE, typeOfNode);
                if (param.storeProvenance) pmapping.setProvenance(provenanceString + "\n");
                return pmapping;
            } else {
                String provenanceString = null;
                if (param.storeProvenance) {
                    //set provenance string
                    provenanceString = "\t********BaseSimilarityMatcher********\n";
                    provenanceString += "sim(\""
                            + sourceName + "\", \""
                            + targetName
                            + "\") = "
                            + verbSimilarity
                            + ", matched by label based verb similarity";
                }
                Mapping pmapping = new Mapping(source, target, verbSimilarity, MappingRelation.EQUIVALENCE, typeOfNode);
                if (param.storeProvenance) pmapping.setProvenance(provenanceString + "\n");
                return pmapping;
            }

        } else {  // Step no dictionary
            // the user does not want to use the dictionary

            //FOCUS ON LOCALNAMES
            if (((BaseSimilarityParameters) param).useLocalname) {
                String sLocalname = source.getLocalName();
                String tLocalname = target.getLocalName();

                //equivalence return 1
                if (sLocalname.equalsIgnoreCase(tLocalname))
                    return new Mapping(source, target, 1d, MappingRelation.EQUIVALENCE, typeOfNode);

                if (((BaseSimilarityParameters) param).useNorm1) {
                    //all normalization without stemming and digits return 0.95
                    String sProcessedLocalnames = norm1.normalize(sLocalname);
                    String tProcessedLocalnames = norm1.normalize(tLocalname);
                    if (sProcessedLocalnames.equals(tProcessedLocalnames))
                        return new Mapping(source, target, 0.95d, MappingRelation.EQUIVALENCE, typeOfNode);
                }

                if (((BaseSimilarityParameters) param).useNorm2) {
                    //all normalization without digits return 0.90
                    String sProcessedLocalnames = norm2.normalize(sLocalname);
                    String tProcessedLocalnames = norm2.normalize(tLocalname);
                    if (sProcessedLocalnames.equals(tProcessedLocalnames))
                        return new Mapping(source, target, 0.9d, MappingRelation.EQUIVALENCE, typeOfNode);
                }

                if (((BaseSimilarityParameters) param).useNorm3) {
                    //all normalization return 0.8
                    String sProcessedLocalnames = norm3.normalize(sLocalname);
                    String tProcessedLocalnames = norm3.normalize(tLocalname);
                    if (sProcessedLocalnames.equals(tProcessedLocalnames))
                        return new Mapping(source, target, 0.9d, MappingRelation.EQUIVALENCE, typeOfNode);
                }
            }

            //FOCUS ON LABELS
            //equivalence return 1
            if (((BaseSimilarityParameters) param).useLabel == true) {
                String sLabel = source.getLabel();
                String tLabel = target.getLabel();

                if (!(sLabel.equals("") || tLabel.equals(""))) {
                    if (sLabel.equalsIgnoreCase(tLabel))
                        return new Mapping(source, target, 1d, MappingRelation.EQUIVALENCE, typeOfNode);

                    if (((BaseSimilarityParameters) param).useNorm1) {
                        //all normalization without stemming and digits return 0.95
                        String sProcessedLabel = norm1.normalize(sLabel);
                        String tProcessedLabel = norm1.normalize(tLabel);
                        if (sProcessedLabel.equals(tProcessedLabel))
                            return new Mapping(source, target, .95d, MappingRelation.EQUIVALENCE, typeOfNode);
                    }


                    if (((BaseSimilarityParameters) param).useNorm2) {
                        //apply stem return 0.90
                        String sProcessedLabel = norm2.normalize(sLabel);
                        String tProcessedLabel = norm2.normalize(tLabel);
                        if (sProcessedLabel.equals(tProcessedLabel))
                            return new Mapping(source, target, .9d, MappingRelation.EQUIVALENCE, typeOfNode);
                    }

                    if (((BaseSimilarityParameters) param).useNorm3) {
                        //apply normDigits return 0.8
                        String sProcessedLabel = norm3.normalize(sLabel);
                        String tProcessedLabel = norm3.normalize(tLabel);
                        if (sProcessedLabel.equals(tProcessedLabel))
                            return new Mapping(source, target, .8d, MappingRelation.EQUIVALENCE, typeOfNode);
                    }
                }
            }
            //none of the above
            return null;
        }
    }

	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new BaseSimilarityMatcherParametersPanel();
		}
		return parametersPanel;
	}
	      
}

