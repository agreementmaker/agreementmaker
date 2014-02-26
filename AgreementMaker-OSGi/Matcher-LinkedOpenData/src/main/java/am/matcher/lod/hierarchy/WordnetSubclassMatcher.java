package am.matcher.lod.hierarchy;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import simpack.measure.weightingscheme.StringTFIDF;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.StringUtil.AMStringWrapper;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.utility.MatchingPair;
import am.app.ontology.Node;
import am.ui.UIUtility;
import am.visualization.graphviz.wordnet.ScoredSynset;
import am.visualization.graphviz.wordnet.WordnetVisualizer;

import com.hp.hpl.jena.ontology.OntClass;
import com.wcohen.ss.api.StringWrapper;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordnetSubclassMatcher extends AbstractMatcher{

	private static final long serialVersionUID = -8046957574666609849L;

	private StringTFIDF tfidfClasses;

	private List<AMStringWrapper> sourceClassDocuments;
	private List<AMStringWrapper> targetClassDocuments;
	private List<AMStringWrapper> sourceSynsetsDocuments;
	private List<AMStringWrapper> targetSynsetsDocuments;
	
	private HashMap<Synset, String> synsetDefinitions = new HashMap<Synset, String>();
	
	private HashMap<Node, List<ScoredSynset>> sourceScoredSynsets = new HashMap<Node, List<ScoredSynset>>();
	private HashMap<Node, List<ScoredSynset>> targetScoredSynsets = new HashMap<Node, List<ScoredSynset>>();
	
	private List<Node> sourceClasses;
	private List<Node> targetClasses;
	
	private WordNetDatabase WordNet;
	
	private HashSet<String> compoundNames = new HashSet<String>();
	
	//double addidtionalConstant = 0.0;
	
	DecimalFormat format = new DecimalFormat("0.000");
		
	Logger LOG;
	
	double wordnetSynsetsLimit = 10000;
	
	private boolean useRightWord = false;
	
	double hypernymsThreshold = 0.0;
	
	public enum SubclassSimilarityFunction { COUNT, DISTANCE, DISTANCE_SOURCE, DISTANCE_SOURCE_TARGET, COUNT_SOURCE_TARGET };
	private SubclassSimilarityFunction subclassFunction = SubclassSimilarityFunction.DISTANCE;
	
	public enum NormalizationFunction { HYPERNYMS_COUNT, HYPERNYMS_LOG, NONE };
	private NormalizationFunction normalizationFunction = NormalizationFunction.HYPERNYMS_LOG;
	
	boolean writeWordnetFiles = false;
	
	boolean useSuperclasses = true;
	
	WordnetVisualizer viz;
	
	boolean useAdditionalConstant = false;
	double additionalConstant = 0.0255943;
	double nonZeroThreshold = 0.3;
	boolean useNonZero = true;
	
	public WordnetSubclassMatcher(){
		initWordnet();
		LOG = Logger.getLogger(WordnetSubclassMatcher.class);
		LOG.setLevel(Level.DEBUG);
		
		if(writeWordnetFiles){
			viz = new WordnetVisualizer();
		}
			
	}
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		param.maxSourceAlign = ANY_INT;
		param.maxTargetAlign = ANY_INT;	
		sourceClasses = sourceOntology.getClassesList();
		targetClasses = targetOntology.getClassesList();
		classesMatrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, alignType.aligningClasses);
		
		//log.setLevel(Level.DEBUG);
		buildVirtualDocuments();
		//log.setLevel(Level.INFO);
	}
	
	
	@Override
	protected void align() throws Exception {
		LOG.info("Matching started...");
		
		addHypernymsToScoredSynsets();
			
		Node sourceNode;
		Node targetNode;
		List<ScoredSynset> sourceScored;
		List<ScoredSynset> targetScored;
		double matchSub;
		double matchSuper;
		for (int i = 0; i < sourceClasses.size(); i++){
			matchSub = 0;
			matchSuper = 0;
			sourceNode = sourceClasses.get(i);
			LOG.debug("Source: " + sourceNode.getUri());
			sourceScored = sourceScoredSynsets.get(sourceNode);
			
//			for (int j = 0; j < sourceScored.size(); j++) {
//				log.debug(sourceScored.get(j).getSynset());
//				log.debug(sourceScored.get(j).getHypernymsByLevel().toString().replaceAll(",", ",\n"));
//			}
			
			for (int j = 0; j < targetClasses.size(); j++) {
				targetNode = targetClasses.get(j);
							
				targetScored = targetScoredSynsets.get(targetNode);
					
				LOG.debug("Matching " + sourceNode.getLocalName() + " " + targetNode.getLocalName());
				//log.debug("sourceComment: " + sourceNode.getComment());
				LOG.debug(sourceScored);
				//log.debug("targetComment: " + targetNode.getComment());
				LOG.debug(targetScored);
				
				boolean sourceCompound = compoundNames.contains(targetNode.getLocalName());				
				boolean targetCompound = compoundNames.contains(sourceNode.getLocalName());
				
				if(!useRightWord || !sourceCompound){
					matchSub = synsetsInHypernymsSimilarity(sourceScored, targetScored, sourceNode, targetNode, true);
					
					if(sourceCompound) matchSub /= 3;					
					
					LOG.debug("HypScore ST: " + matchSub);
					
					if(matchSub > hypernymsThreshold)
						newMapping(sourceNode, targetNode, matchSub, MappingRelation.SUPERCLASS, "Wordnet mediator ST ");
					
				}
				
				if(!useRightWord || !targetCompound){
					matchSuper = synsetsInHypernymsSimilarity(targetScored, sourceScored, targetNode, sourceNode, false);	
					
					if(targetCompound) matchSub /= 3;
					
					if(matchSuper > hypernymsThreshold)
						newMapping(sourceNode, targetNode, matchSuper, MappingRelation.SUBCLASS, "Wordnet mediator TS ");
					
					LOG.debug("HypScore TS: " + matchSuper);	
					
					
				}	
				
				if(matchSub > 0 && matchSuper > 0){
					System.out.println("Weird: " + matchSub + " " + matchSuper + " " + sourceNode + " " + targetNode);					
				}
			}
		}
		
		if(writeWordnetFiles){
			LOG.info("Writing wordnet files...");
			writeWordnetFiles();
			LOG.info("Done");
			
		}
		
		//normalizeMatrix();
		
	}
	
	private void writeWordnetFiles(){
		Node source;
		Node target;
		List<ScoredSynset> sourceScored;
		List<ScoredSynset> targetScored;
		Map<Synset, ScoredSynset> sourceScoredBySynset;
		Map<Synset, ScoredSynset> targetScoredBySynset;
		boolean invert;
		for (int i = 0; i < sourceClasses.size(); i++) {
			source = sourceClasses.get(i);
			sourceScored = sourceScoredSynsets.get(source);
			
			sourceScoredBySynset = new HashMap<Synset, ScoredSynset>();
			for (int k = 0; k < sourceScored.size(); k++) {
				sourceScoredBySynset.put(sourceScored.get(k).getSynset(), sourceScored.get(k));
			}
			
			for (int j = 0; j < targetClasses.size(); j++) {
				
				Mapping m = classesMatrix.get(i, j);
				if(m == null || m.getSimilarity() < 0.01) continue;
				
				if(m.getRelation() == MappingRelation.SUPERCLASS)
					invert = true;
				else invert = false;
				
				System.out.println(m.getSimilarity());
				
				target = targetClasses.get(j);
				
				targetScored = targetScoredSynsets.get(target);
				targetScoredBySynset = new HashMap<Synset, ScoredSynset>();
				for (int t = 0; t < targetScored.size(); t++) {
					targetScoredBySynset.put(targetScored.get(t).getSynset(), targetScored.get(t));
				}
				
				if(!invert)
					viz.saveGraphOnFile(source.getLocalName() + " - " + target.getLocalName() + " " + format.format(m.getSimilarity()),
							sourceScoredBySynset, targetScoredBySynset);
				else viz.saveGraphOnFile(target.getLocalName() + " - " + source.getLocalName() + " " + format.format(m.getSimilarity()),
						targetScoredBySynset, sourceScoredBySynset);
			}
			
		}
		
	}
	
	private void normalizeMatrix() {
		double max = classesMatrix.getMaxValue();
		//log.info("Normalizing factor: " + max);
		System.out.println("Normalizing factor: " + max);
		for (int i = 0; i < sourceClasses.size(); i++) {
			for (int j = 0; j < targetClasses.size(); j++) {
				Mapping mapping = classesMatrix.get(i, j);
				if(mapping != null && mapping.getSimilarity() > 0.0){
					mapping.setSimilarity(mapping.getSimilarity() / max);
					classesMatrix.set(i, j, mapping);
				}
			}
		}
	}

	/**
	 * Initialize the WordNet Interface (JAWS)
	 */
	private void initWordnet(){
		String root = Core.getInstance().getRoot();
		String wordnetdir = root + File.separator + "wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate 
		try{
			WordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ){
			UIUtility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
	}
		
	private void buildVirtualDocuments() {
		LOG.info("Building comments vectors");
		
		NormalizerParameter param = new NormalizerParameter();
		param.normalizeBlank = true;
		param.normalizeDiacritics = true;
		param.normalizePunctuation = true;
		param.normalizeDigit = true;
		param.normalizeSlashes = true;
		param.removeAllStopWords = true;
		param.removeStopWords = true;
		param.stem = true;
				
		Normalizer normalizer = new Normalizer(param);
		
		//Create and normalize documents from the list of nodes
		sourceClassDocuments = createNormalizedDocuments(sourceClasses, normalizer);
		targetClassDocuments = createNormalizedDocuments(targetClasses, normalizer);
		//Create and normalize documents from the synsets definitions
		sourceSynsetsDocuments = buildSynsetCorpus(sourceClasses, normalizer);		
		targetSynsetsDocuments = buildSynsetCorpus(targetClasses, normalizer);		
		
		List<StringWrapper> classCorpus = new ArrayList<StringWrapper>();
		classCorpus.addAll(sourceClassDocuments);
		classCorpus.addAll(targetClassDocuments);
		classCorpus.addAll(sourceSynsetsDocuments);
		classCorpus.addAll(targetSynsetsDocuments);
		//Create the corpus of documents
		//the TFIDF requires a corpus that is the list of total documents
		//each node consist of one document
		tfidfClasses = new StringTFIDF(classCorpus);
		
		buildScoredSynsets(sourceClasses, sourceClassDocuments, sourceScoredSynsets);
		buildScoredSynsets(targetClasses, targetClassDocuments, targetScoredSynsets);

	}
	
	public String getHead(String localName){
		localName = Utilities.separateWords(localName);	
		String[] split = localName.split(" ");
		if(split.length > 1 && split.length <= 2)
			localName = split[split.length - 1];
		return localName;
	}
	
	public List<AMStringWrapper> buildSynsetCorpus(List<Node> nodeList, Normalizer normalizer){
		Node node;
		List<AMStringWrapper> normalizedDocuments = new ArrayList<AMStringWrapper>();
		for (int i = 0; i < nodeList.size(); i++) {
			node = nodeList.get(i);
			
			String localName = node.getLocalName();
			
			if(useRightWord){
				String head = getHead(localName);
				if(!head.equals(localName)){
					compoundNames.add(localName);
					localName = head;
				}
			}
			
			List<NounSynset> synsets = doLookUp(localName);
			
			String definition;
			for (NounSynset synset : synsets) {
				if(!synsetDefinitions.containsKey(synset)){
					definition = synset.getDefinition();
					if(definition == null)
						LOG.error("null synset definition");
					definition = normalizer.normalize(definition);
					if(definition == null)
						LOG.error("null definition after normalization");
					normalizedDocuments.add(new AMStringWrapper(definition));
					synsetDefinitions.put(synset, definition);			
				}			
			}
		}
		return normalizedDocuments;
	}
	
	private void buildScoredSynsets(List<Node> nodeList, List<AMStringWrapper> documents, 
			HashMap<Node, List<ScoredSynset>> scoredSynsets) {
		Node node;
		String comment;
		for (int i = 0; i < nodeList.size(); i++) {
			node = nodeList.get(i);
			
			comment = documents.get(i).unwrap();
			
			LOG.debug("Computing synset scores for: " + node.getLocalName());
			LOG.debug("comment:" + node.getComment());
			LOG.debug("comment:" + comment);
			
			String localName = node.getLocalName();
			boolean compound = false;
			
			if(useRightWord){
				localName = getHead(localName);					
			}
			
			List<NounSynset> sourceSynsetList = doLookUp(localName);
			List<ScoredSynset> scoredList = new ArrayList<ScoredSynset>();
			
			//double score = 
			
			boolean atLeastOneHighSimilarity = false;
			
			for (NounSynset synset : sourceSynsetList) {
				String definition = synsetDefinitions.get(synset);
				
				Double sim = new Double(0);
				if(comment != null && definition != null){
					sim = tfidfClasses.getSimilarity(comment, definition);	
				}
				else{
					LOG.error("Problems with comments or definition");
					LOG.error(comment);
					LOG.error(definition);
				}
				
				LOG.debug("definition: " + synset.getDefinition());
				LOG.debug("definition: " + definition);
				LOG.debug("vectorSim:\t" + sim + "\t" + node.getLocalName());
				
				if(useNonZero){
					if(sim >= nonZeroThreshold){
						atLeastOneHighSimilarity = true;
					}
				}
				
				if(useAdditionalConstant)
					sim += additionalConstant;
				
				//TODO figure out how to use the similarity
				//System.out.println("sim:" + sim);
				scoredList.add(new ScoredSynset(synset, node.getLocalName(), sim));
			}
			
			double sum = 0;
			int size = scoredList.size();
			for (int j = 0; j < scoredList.size(); j++) {
				sum += scoredList.get(j).getScore();
			}
			//
			
			LOG.debug("sum:" + sum);
			
			if(useNonZero){
				if(atLeastOneHighSimilarity){
					for (int j = 0; j < scoredList.size(); j++) {
						if(scoredList.get(j).getScore() < nonZeroThreshold){
							scoredList.remove(scoredList.get(j));
							j--;
						}
					}
				}
			}
			
			
			//if(sum != 0) System.out.println("sum != 0");
			String weights = "[";
			for (int j = 0; j < scoredList.size(); j++) {
				double score = scoredList.get(j).getScore();
				weights += format.format(scoredList.get(j).getScore());
				if(j < scoredList.size() - 1) weights += ",";
				if(sum == 0) 
					scoredList.get(j).setScore((double)1 / size);
				else {
					scoredList.get(j).setScore(score / sum);
					LOG.debug(scoredList.get(j));
				}
			}
			weights += "]";	
			LOG.debug("weights: " + weights);
			
			weights = "[";
			for (int j = 0; j < scoredList.size(); j++) {
				weights += format.format(scoredList.get(j).getScore());
				if(j < scoredList.size() - 1) weights += ",";
			}
			weights += "]";	
			LOG.debug("weightsMod: " + weights);
			
			scoredSynsets.put(node, scoredList);
		}		
	}
	
	private void addHypernymsToScoredSynsets(){
		
		for (List<ScoredSynset> scoredSynsets: sourceScoredSynsets.values()) {
			for (ScoredSynset scoredSynset: scoredSynsets) {
				//log.debug(scoredSynset.getSynset());
				List<List<NounSynset>> sourceHypernyms = hypernymsLookup(scoredSynset.getSynset());
				//log.debug(sourceHypernyms);
				scoredSynset.setHypernymsByLevel(sourceHypernyms);			
			}
		}
		
		for (List<ScoredSynset> scoredSynsets: targetScoredSynsets.values()) {
			for (ScoredSynset scoredSynset: scoredSynsets) {
				List<List<NounSynset>> targetHypernyms = hypernymsLookup(scoredSynset.getSynset());
				scoredSynset.setHypernymsByLevel(targetHypernyms);			
			}
		}
	}
	
	
	
	
	public List<AMStringWrapper> createNormalizedDocuments(List<Node> nodeList, Normalizer normalizer){
		Node source;
		String comment;
		OntClass ontClass;
		List<OntClass> superClasses;
		List<AMStringWrapper> normalizedDocuments = new ArrayList<AMStringWrapper>();
		for (int i = 0; i < nodeList.size(); i++) {
			source = nodeList.get(i);
			comment = source.getComment();
			
			LOG.debug("Building virtual document for " + source.getLocalName());
			
			LOG.debug("comment: " + comment);
						
			ontClass = source.getResource().as(OntClass.class);
			superClasses = ontClass.listSuperClasses().toList();
			
			String supString = "";
			
			for (int j = 0; j < superClasses.size(); j++) {
				supString += Utilities.separateWords(superClasses.get(j).getLocalName()) + " ";
			}
			
			LOG.debug("superclasses: " + supString);
			
			if(useSuperclasses)
				comment += " " + supString;
						
			normalizer.addStopword(source.getLocalName());
			comment = normalizer.normalize(comment);
			normalizer.removeStopword(source.getLocalName());
						
			LOG.debug("normComment: " + comment);
			
			normalizedDocuments.add(new AMStringWrapper(comment));
		}
		return normalizedDocuments;
	}
	
	/**
	 * This method returns the list of corresponding WordNet Synsets for given a Node.
	 * @param localName
	 * @return
	 */
	private List<NounSynset> doLookUp(String localName)
	{	
		ArrayList<NounSynset> synonymSet = new ArrayList<NounSynset>();
				
		Synset[] synsets = WordNet.getSynsets(localName, SynsetType.NOUN);
		
		for (int i = 0; i < Math.min(synsets.length, wordnetSynsetsLimit); i++){
			Synset currentSynset = synsets[i];
			synonymSet.add( (NounSynset) currentSynset);
		}
		return synonymSet;
	}
	
	private List<List<NounSynset>> hypernymsLookup(NounSynset synset){
		List<List<NounSynset>> hypernymsByLevel = new ArrayList<List<NounSynset>>();
		
		List<NounSynset> synsets = new ArrayList<NounSynset>();
		synsets.add(synset);
		List<NounSynset> hypernyms = null;
				
		do{
			//System.out.println(synsets);
			//System.out.println("HYP:" + hypernyms);
			hypernyms = getHypernyms(synsets);
			if(hypernyms.size() > 0)
				hypernymsByLevel.add(hypernyms);
			synsets = hypernyms;
		}
		while(hypernyms.size() > 0);
		
		return hypernymsByLevel;
	}
	
	private List<NounSynset> getHypernyms(List<NounSynset> synsets){
		List<NounSynset> hypernyms = new ArrayList<NounSynset>();
		NounSynset synset;
		NounSynset[] hypernymsVector;
		for (int i = 0; i < synsets.size(); i++) {
			synset = synsets.get(i);
			hypernymsVector = synset.getHypernyms();
			for (int j = 0; j < hypernymsVector.length; j++) {
				hypernyms.add(hypernymsVector[j]);
			}
		}
		return hypernyms;
	}
	
	private double synsetsInHypernymsSimilarity(List<ScoredSynset> sourceList, List<ScoredSynset> targetList, Node sourceNode, Node targetNode, boolean sourceTarget) {
		double sim;
		ScoredSynset source;
		ScoredSynset target;
		List<List<NounSynset>> hypernymsByLevel;		
		List<NounSynset> hypernyms;
		
		double match = 0.0;
		double matchS = 0.0;
		double matchST = 0.0;
		double count = 0;
		double countST = 0;
		
		MatchingPair solution = null;
		
		if(sourceList.size() == 0 || targetList.size() == 0)
			return 0.0;
		
				
		boolean oneMatch = false;
		
		HashSet<Synset> hypernymsSet = new HashSet<Synset>();
		
		for (int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			
			for (int j = 0; j < targetList.size(); j++) {
				
				target = targetList.get(j);
				
				hypernymsByLevel = target.getHypernymsByLevel();
				
				sim = 1;
				
				for (int k = 0; k < hypernymsByLevel.size(); k++) {
					hypernyms = hypernymsByLevel.get(k);
					sim = sim * 0.9;
					
					for (int t = 0; t < hypernyms.size(); t++) {
						hypernymsSet.add(hypernyms.get(t));
					}
					
					if(hypernyms.contains(source.getSynset())){
						oneMatch = true;
						
						String report = "Match!!! " + source.getSynset() + "\tsim\t" + format.format(sim) + "\tsourceScore\t" + 
								format.format(source.getScore()) + "\ttargetScore\t" + format.format(target.getScore()) +
									"\t" + source.getName() + "\t" + target.getName();
						
						//match += sim * source.getScore() * target.getScore();
						
						count++;
						match += sim;
						matchS += sim * source.getScore();
						matchST += sim * source.getScore() * target.getScore();
						countST += source.getScore() * target.getScore();
						
						LOG.debug(report);
					}					
				}
			}
		}
		
		if(oneMatch){
			String report = "simFunctions\t" + count + "\t" + match + "\t" + matchS + "\t" + matchST 
					+ "\t" + sourceNode.getLocalName() + "\t" + targetNode.getLocalName();
			
			if(solution == null) 
				report += "\tNo"; 
			else report += "\tYes";
			
			LOG.debug(report);
		}
		
		if(sourceList.size() == 0)
			return 0.0;
		
		double retValue = 0.0;
		
		if(subclassFunction == SubclassSimilarityFunction.COUNT)
			retValue = count / 3;
		else if(subclassFunction == SubclassSimilarityFunction.DISTANCE)
			//retValue = match / 2;
			retValue = match;
		else if(subclassFunction == SubclassSimilarityFunction.DISTANCE_SOURCE)
			retValue = matchS * 3;
		else if(subclassFunction == SubclassSimilarityFunction.DISTANCE_SOURCE_TARGET)
			retValue = matchST * 5;
		else if(subclassFunction == SubclassSimilarityFunction.COUNT_SOURCE_TARGET)
			retValue = countST * 5;
		
		double hypernymsCount = hypernymsSet.size();
		
		if(normalizationFunction == NormalizationFunction.HYPERNYMS_COUNT)
			retValue /= hypernymsCount;
		else if(normalizationFunction == NormalizationFunction.HYPERNYMS_LOG){
			LOG.trace("Hypernyms count: " + hypernymsCount);
			double log = Math.log(hypernymsCount);
			LOG.trace(hypernymsSet);
			LOG.trace("log: " + log);
			retValue /= log;
			LOG.trace("sim: " + retValue);			
		}
			
		return retValue;
	}
	
	public void newMapping(Node sourceNode, Node targetNode, double sim, MappingRelation rel, String provenance){
		Mapping m = new Mapping(sourceNode, targetNode, sim, rel);
		m.setProvenance(provenance);
		classesMatrix.set(sourceNode.getIndex(), targetNode.getIndex(), m);
	}
	
}
