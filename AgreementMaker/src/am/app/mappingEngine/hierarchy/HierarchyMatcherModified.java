package am.app.mappingEngine.hierarchy;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import simpack.measure.weightingscheme.StringTFIDF;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.LinkedOpenData.LODOntology;
import am.app.mappingEngine.LinkedOpenData.LODUtils;
import am.app.mappingEngine.StringUtil.AMStringWrapper;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.visualization.graphviz.wordnet.WordnetVisualizer;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.LocationMapper;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.wcohen.ss.api.StringWrapper;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

/*The aim of this matcher is to find the subClassOf and superClassOf relationships between the classes that have been mapped by equivalence
 relationship by the input matchers.*/
public class HierarchyMatcherModified extends AbstractMatcher
{
	private static final long serialVersionUID = -2288022546791830839L;
	private List<Node> sourceClasses;
	private List<Node> targetClasses;
	
	private WordNetDatabase WordNet;
		
	/*VARIABLES TO SET THE THRESHOLD*/
	double inputMatcherThreshold = 1.0;
	double EQUALITY_THRESHOLD_VALUE;
	double SUBCLASS_THRESHOLD_VALUE;
	double SUPERCLASS_THRESHOLD_VALUE;
	
	//int wordnetHypernymsLimit = 5;
	
	boolean useOtherOntologies = true;
	ArrayList<OntModel> otherOntologies;
	
	Logger log;
	private boolean disambiguate = true;
	
	boolean useProvenanceToDebug = true;
	
	int wordnetSynsetsLimit = 4;
	private int hypernymThreshold = 1;
	int hypernymsDepthLimit = 4;
	
	boolean writeWordnetFiles = false;
	
	WordnetVisualizer viz;
	
	StringTFIDF tfidfClasses;
	List<AMStringWrapper> sourceClassDocuments;
	List<AMStringWrapper> targetClassDocuments;
	List<AMStringWrapper> sourceSynsetsDocuments;
	List<AMStringWrapper> targetSynsetsDocuments;
	List<StringWrapper> classCorpus;
	
	HashMap<Synset, String> synsetDefinitions = new HashMap<Synset, String>();
	
	HashMap<Node, List<ScoredNounSynset>> sourceScoredSynsets = new HashMap<Node, List<ScoredNounSynset>>();
	HashMap<Node, List<ScoredNounSynset>> targetScoredSynsets = new HashMap<Node, List<ScoredNounSynset>>();
	
	
	public HierarchyMatcherModified()
	{
		super();
		/*minInputMatchers & maxInputMatcher ensures that the user gives the input */
		minInputMatchers = 1;
		maxInputMatchers = 1;
		
		param = new AbstractParameters();
		
		/* maxSourceAlign and maxTargetAlign set the cardinality of the alignments to many to many that is 
		 one concept in source can be aligned to more than one concept in target*/
		
		log = Logger.getLogger(HierarchyMatcherModified.class);
		
		log.setLevel(Level.ERROR);
		
		if(writeWordnetFiles)
			viz = new WordnetVisualizer();
		
		initWordnet();	
	}
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		param.maxSourceAlign = ANY_INT;
		param.maxTargetAlign = ANY_INT;
	}
	
	private void buildCommentVectors() {
		System.out.println("Building comments vectors");
		
		NormalizerParameter param = new NormalizerParameter();
		param.normalizeBlank = true;
		param.normalizeDiacritics = true;
		param.normalizePunctuation = true;
		param.normalizeDigit = true;
		param.normalizeSlashes = true;
		param.removeStopWords = true;
		param.stem = true;
				
		Normalizer normalizer = new Normalizer(param);
		
		sourceClassDocuments = createNormalizedDocuments(sourceClasses, normalizer);
		targetClassDocuments = createNormalizedDocuments(targetClasses, normalizer);
		sourceSynsetsDocuments = buildSynsetCorpus(sourceClasses, normalizer);		
		targetSynsetsDocuments = buildSynsetCorpus(sourceClasses, normalizer);		
		
		List<StringWrapper> classCorpus = new ArrayList<StringWrapper>();
		classCorpus.addAll(sourceClassDocuments);
		classCorpus.addAll(targetClassDocuments);
		classCorpus.addAll(sourceSynsetsDocuments);
		classCorpus.addAll(targetSynsetsDocuments);
		//Create the corpus of documents
		//the TFIDF requires a corpus that is the list of total documents
		//each node consist of one document
		tfidfClasses = new StringTFIDF(classCorpus);
		
		buildScoredSynsets(sourceClasses, sourceClassDocuments);
		
		
		
	}
	
	private void buildScoredSynsets(List<Node> nodeList, List<AMStringWrapper> documents) {
		Node node;
		String comment;
		for (int i = 0; i < nodeList.size(); i++) {
			node = nodeList.get(i);
			System.out.println(node);
			
			comment = documents.get(i).unwrap();
						
			
			List<NounSynset> sourceSynsetList = doLookUp(node);
			List<ScoredNounSynset> scoredSynsets = new ArrayList<ScoredNounSynset>();
			
			//double score = 
			
			for (NounSynset synset : sourceSynsetList) {
				String definition = synsetDefinitions.get(synset);
				
				Double sim = tfidfClasses.getSimilarity(comment, definition);
				
				System.out.println("comment:" + comment);
				System.out.println("definition: " + definition);
				System.out.println(sim);
				
			}
		}		
	}

	public List<AMStringWrapper> createNormalizedDocuments(List<Node> nodeList, Normalizer normalizer){
		Node source;
		String comment;
		List<AMStringWrapper> normalizedDocuments = new ArrayList<AMStringWrapper>();
		for (int i = 0; i < nodeList.size(); i++) {
			source = nodeList.get(i);
			comment = source.getComment();
			System.out.println(comment);
			comment = normalizer.normalize(comment);
			normalizedDocuments.add(new AMStringWrapper(comment));
			System.out.println(comment);
		}
		return normalizedDocuments;
	}
	
	public List<AMStringWrapper> buildSynsetCorpus(List<Node> nodeList, Normalizer normalizer){
		Node node;
		List<AMStringWrapper> normalizedDocuments = new ArrayList<AMStringWrapper>();
		for (int i = 0; i < nodeList.size(); i++) {
			node = nodeList.get(i);
						
			List<NounSynset> synsets = doLookUp(node);
			
			String definition;
			for (NounSynset synset : synsets) {
				if(!synsetDefinitions.containsKey(synset)){
					definition = synset.getDefinition();
					definition = normalizer.normalize(definition);
					normalizedDocuments.add(new AMStringWrapper(definition));
					synsetDefinitions.put(synset, definition);			
				}			
			}
		}
		return normalizedDocuments;
	}

	/**
	 * Initialize the WordNet Interface (JAWS)
	 */
	private void initWordnet(){
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate 
		try{
			WordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ){
			Utility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
	}
	
	
	@Override
	protected void align() throws Exception{
		/*miscellaneous variables used in  the method*/
		log.info("align " + sourceOntology + " " + targetOntology);
		
		//log.setLevel(Level.DEBUG);
		
		if( sourceOntology == null || targetOntology == null )
			return;  // cannot align just one ontology 
		
		/*it gets the inputMatches being used for this Algo and initializes the variables
		 * classesMatrix , propertiesMatrix */
		getInputMatcher();
		
		//System.out.println("INSTANCE: " + classesMatrix.getClass());
		
		//printMatrix(classesMatrix);
		
		/*maintains a list of nodes in the ontology*/
		sourceClasses = sourceOntology.getClassesList();
		targetClasses = targetOntology.getClassesList();
		
		/*First Iteration that uses the similarity values received in the input matchers
		 * It calculates all the subClassOf and superClassOf relationship based on the equivalence 
		 * relationship received form the first Matcher*/
		log.info("Using input matrix...");
		useInputMatrix();
		
		buildCommentVectors();		
		
		log.info("Compound words analysis...");
		compoundWordsAnalysis();
		
		log.info("Using input matrix...");
		wordnedMediatorStep();
		
		//computeTransitiveClosure();

		if(useOtherOntologies == true){
			log.info("Initializing other ontologies...");
			initOtherOntologies();
			log.info("Using other ontologies...");
			useOtherOntologies(sourceClasses, targetClasses, false);
			//useOtherOntologies(targetClasses, sourceClasses, true);
		}
		
		log.info("Filtering equality mappings...");
		filterEqualityMappings();
		
		//log.info("Filtering external mappings...");
		//filterExternalMappings();
		
		
		if(writeWordnetFiles){
			for (int i = 0; i < sourceClasses.size(); i++) {
				viz.saveGraphOnFile(sourceClasses.get(i).getLocalName());
			}
			for (int i = 0; i < targetClasses.size(); i++) {
				viz.saveGraphOnFile(targetClasses.get(i).getLocalName());
			}
		}
			
		//printMatrix(classesMatrix);
		
	}
	
	

	private void wordnedMediatorStep() {
		/*This is the second level of relationship mappings being done
		 * based on wordnet*/
		/*Get the synonym set of Source*/
				
		for (int i = 0; i < sourceClasses.size(); i++){
			Node sourceNode = sourceClasses.get(i);
			log.debug("Source: " + sourceNode.getUri());
			
			Mapping m;
			
			List<NounSynset> sourceSynsetList = doLookUp(sourceNode);
			List<NounSynset> dis = disambiguate(sourceNode, sourceSynsetList);
			
			if(disambiguate  && dis.size() > 0){
				log.debug("Disambiguated!");
				sourceSynsetList = dis;
			}
						
			List<NounSynset> sourceHypernymList = buildHypernymList(sourceSynsetList);
			
			log.debug("Source synsets: " + sourceSynsetList);
			log.debug("Source disambiguated: " + dis);
			log.debug("Source hypernyms: " + sourceHypernymList.toString().replaceAll(",", "\n"));
			
			for(int j =0 ; j<targetClasses.size();j++){
				Node targetNode = targetClasses.get(j);
				
				log.debug("target: " + targetNode.getUri());
				
				//if(targetNode.getLocalName().equals("Event")) continue;
				
				
				//////////////////////////////////
				
				//System.out.println(sourceClassDocuments.get(i).unwrap());	
				
				//System.out.println(sourceClassDocuments.get(j).unwrap());	
				
				
				/////////////////////////////////
				
				List<NounSynset> targetSynsetList = doLookUp(targetNode);
				List<NounSynset> targetDis = disambiguate(targetNode, targetSynsetList);
				
				if(disambiguate && targetDis.size() > 0){
					log.debug("Disambiguated!");
					log.debug(targetDis);
					targetSynsetList = targetDis;
				}
				
				List<NounSynset> targetHypernymList = buildHypernymList(targetSynsetList);
				log.debug("target hypernyms: " + targetHypernymList.toString().replaceAll(",", "\n"));
				int matchingSynsets = synsetIsContainedBy( sourceSynsetList, targetHypernymList );				
				double sim = wordnetHypernymSimilarity(matchingSynsets, sourceSynsetList.size(), targetHypernymList.size());
				
				double threshold = 2.0;
				
				if( sim >= threshold ) {
					// source > target
					/*The similarity is set to be less than 0.80d as I do not want to
					 * overwrite alread established relationships*/
					if( classesMatrix.get( sourceNode.getIndex(), targetNode.getIndex()) == null || 
						classesMatrix.getSimilarity( sourceNode.getIndex(), targetNode.getIndex()) < 0.80d ) {
						log.debug("mapping >: "+sourceNode.getLocalName() + " " + targetNode.getLocalName() + sourceSynsetList);
						newMapping(sourceNode, targetNode, 0.89d, MappingRelation.SUPERCLASS, "Wordnet mediator ST " + 
								matchingSynsets + " " + sourceSynsetList.size() + " " + sourceHypernymList.size());
					}
				} else{
					//matchingSynsets = synsetIsContainedBy(targetSynsetList, sourceHypernymList);
					matchingSynsets = synsetIsContainedBy(targetSynsetList, sourceHypernymList);
									
					sim = wordnetHypernymSimilarity(matchingSynsets, targetSynsetList.size(), sourceHypernymList.size());
					
					if ( matchingSynsets >= threshold ) {
						//source < target
						if( classesMatrix.get( sourceNode.getIndex(), targetNode.getIndex()) == null || 
								classesMatrix.getSimilarity( sourceNode.getIndex(), targetNode.getIndex()) < 0.80d ) {
								log.debug("mapping <: "+sourceNode.getLocalName() + " " + targetNode.getLocalName() + targetSynsetList);
								newMapping(sourceNode, targetNode, 0.89d, MappingRelation.SUBCLASS, "Wordnet mediator TS " + 
										matchingSynsets + " " + targetSynsetList.size() + " " + sourceHypernymList.size());
						}
					}
				}
			}
		}	
	}

	private double wordnetHypernymSimilarity(int matchingSynsets,
			int synsetSize, int hypernymsSize) {
		//return matchingSynsets / Math.log(hypernymsSize);
		return matchingSynsets;
	}

	private void useInputMatrix() {
		Node source=null;
		Node target=null;
		double similarityValue;
		Mapping m;
		for (int i = 0; i < sourceClasses.size(); i++){
			for(int j =0 ; j < targetClasses.size(); j++){
				m = classesMatrix.get(i, j);
				if(m == null) continue;
				similarityValue = classesMatrix.getSimilarity(i, j);
				/*Right now the similarity value is kept one as a pessimist approach
				 * that anything less than one will yield wrong results. It has been verified by
				 * runs of AM*/
				if(m.getRelation().equals(MappingRelation.EQUIVALENCE) && similarityValue >= inputMatcherThreshold)
				{
					System.out.println(classesMatrix.get(i,j));
					m.setProvenance("Input matcher");
					classesMatrix.set(i, j, m);
					source = sourceClasses.get(i);
					target = targetClasses.get(j);
					System.out.println(source.getLocalName() + " " + similarityValue + " " +inputMatcherThreshold);	
					matchManySourceTarget(source,target);
					matchManyTargetSource(source,target);
				}
			}
		}
	}

	private void compoundWordsAnalysis() {
		String name;
		String[] sourceSplitted;
		Node sNode;
		Node tNode;
		for (int i = 0; i < sourceClasses.size(); i++) {
			sNode = sourceClasses.get(i);
			name = Utilities.separateWords(sNode.getLocalName());
			sourceSplitted = name.split(" ");
			
			if(sourceSplitted.length != 2) continue;
			
			for (int j = 0; j < targetClasses.size(); j++) {
				tNode = targetClasses.get(j);
				if(sourceSplitted[1].equals(tNode.getLocalName())){
					newMapping(sNode, tNode, 1.0d, MappingRelation.SUBCLASS, "Compound word analysis");
				}
			}
		}
		
		String[] targetSplitted;
		for (int i = 0; i < targetClasses.size(); i++) {
			tNode = targetClasses.get(i);
			name = Utilities.separateWords(tNode.getLocalName());
			targetSplitted = name.split(" ");
			
			if(targetSplitted.length != 2) continue;
			
			for (int j = 0; j < sourceClasses.size(); j++) {
				sNode = sourceClasses.get(j);
				if(targetSplitted[1].equals(sNode.getLocalName())){
					newMapping(sNode, tNode, 1.0d, MappingRelation.SUPERCLASS, "Compound word analysis");
				}
			}
		}
		
	}

	private void initOtherOntologies() {
		Ontology ontology;
		otherOntologies = new ArrayList<OntModel>();
		
		LocationMapper mapper = null;
		if(param != null && param instanceof HierarchyMatcherModifiedParameters){
			mapper = ((HierarchyMatcherModifiedParameters)param).mapper;
		}
		
		if(!sourceOntology.getURI().equals(LODOntology.FOAF.getUri()) &&
				!targetOntology.getURI().equals(LODOntology.FOAF.getUri())){
			log.debug("Opening FOAF");
			ontology = OntoTreeBuilder.loadOWLOntology(new File(LODOntology.FOAF.getFilename()).getPath(), mapper);	
			//ontology = LODUtils.openOntology(new File(LODOntologies.FOAF).getPath(), LODUtils.getLocationMapper());
			otherOntologies.add(ontology.getModel());	
		}
			
		if(!sourceOntology.getURI().equals(LODOntology.SW_CONFERENCE.getUri()) &&
				!targetOntology.getURI().equals(LODOntology.SW_CONFERENCE.getUri())){
			log.debug("Opening SWC");
			ontology = OntoTreeBuilder.loadOWLOntology(new File(LODOntology.SW_CONFERENCE.getFilename()).getPath(), mapper);
			//ontology = LODUtils.openOntology(new File(LODOntologies.SW_CONFERENCE).getPath(), LODUtils.getLocationMapper());
			otherOntologies.add(ontology.getModel());
		}
	}

	/**
	 * This method uses information taken from other LOD ontologies to improve the match.
	 * The idea is that we can use sub and superclasses of standard classes in LOD famous ontologies to
	 * get more information to use in the matching process. 
	 */
	private void useOtherOntologies(List<Node> sourceClasses, List<Node> targetClasses, boolean invert) {
		OntModel ontology;
		Node source;
		Node target;
		for (int i = 0; i < sourceClasses.size(); i++) {
			source = sourceClasses.get(i);
			
			for (int j = 0; j < otherOntologies.size(); j++) {
				ontology = otherOntologies.get(j);
				
				for (OntClass cl: ontology.listClasses().toList()) {
					
					if(source.getUri().equals(cl.getURI())){
						log.debug("WOW Let's study class " + cl.getURI());
						
						log.debug(cl.listSuperClasses().toList());
						
						//Subclasses of the class can help generate superclass mappings
						for(OntClass subclass : cl.listSubClasses().toList()){
							
							
							
							for(int t = 0; t < targetClasses.size(); t++){
								target = targetClasses.get(t);
								
								if(target.getLocalName().toLowerCase().equals(subclass.getLocalName().toLowerCase())){
									//create superclass mapping source -> target
									log.info("CREATING MAPPING "+source.getLocalName() + " " + target.getLocalName());
									Mapping m;
									if(!invert) newMapping(source, target, 1.0, MappingRelation.SUPERCLASS, "External ontologies analysis");
									else newMapping(target, source, 1.0, MappingRelation.SUPERCLASS, "External ontologies analysis");
									
									//also subclasses of target are subclasses of source:
									for(OntClass child: listAllSubclasses(target.getResource().as(OntClass.class), null)){
										int index = getIndex(targetClasses, child.getURI());
										newMapping(source, targetClasses.get(index), 1.0, MappingRelation.SUPERCLASS, "External ontologies analysis");
									}
								}
							}
						}
					}
				}		
			}
		}
	}

	private void filterEqualityMappings() {
		Mapping m;
		for (int i = 0; i < classesMatrix.getRows(); i++) {
			for (int j = 0; j < classesMatrix.getColumns(); j++) {
				if(classesMatrix.get(i, j) != null){
					m = classesMatrix.get(i, j);
					
					if(m.getEntity1().getLocalName().equals(m.getEntity2().getLocalName()) && m.getRelation() != MappingRelation.EQUIVALENCE)
						m.setRelation(MappingRelation.EQUIVALENCE);
						classesMatrix.set(i, j, m);
					
					if(m.getRelation() == MappingRelation.EQUIVALENCE && m.getSimilarity() < inputMatcherThreshold )
						classesMatrix.set(i, j, null);
				}
			}
		}	
	}

	private List<NounSynset> disambiguate(Node sourceNode, List<NounSynset> sourceSynsetList) {
		//log.debug("DISAMBIGUATE");
		Set<Node> descendantsSet = sourceNode.getDescendants();
		List<Node> descendants = new ArrayList<Node>(descendantsSet);
		
		List<NounSynset> hypernymList;
		List<NounSynset> contained = new ArrayList<NounSynset>();
		for (int i = 0; i < descendants.size(); i++) {
			List<NounSynset> descendantSynsets = doLookUp(descendants.get(i));
			hypernymList = buildHypernymList(descendantSynsets);
			for (int j = 0; j < hypernymList.size(); j++) {
				if(sourceSynsetList.contains(hypernymList.get(j)) && !contained.contains(hypernymList.get(j)))
					contained.add(hypernymList.get(j));
			}
		}			
		return contained;
	}

	
	
//	private void extendToChildren(OntClass node, OntClass , MappingRelation relation, double similarity){
//		List<OntClass> subclasses = node.listSubClasses().toList();
//		
//	}
//	
	private List<OntClass> listAllSubclasses(OntClass node, List<OntClass> result){
		if(result == null) result = new ArrayList<OntClass>();
		List<OntClass> subclasses = node.listSubClasses().toList();
		result.addAll(subclasses);
		for(OntClass cl: subclasses)
			listAllSubclasses(cl, result);
		return result;
	}
	

	private int synsetIsContainedBy(List<NounSynset> sourceSynsetList,
			List<NounSynset> hypernymList) {
		
		int count = 0;
		
		for( NounSynset currentHypernym : hypernymList ) {
			if( sourceSynsetList.contains(currentHypernym) ){
					log.debug("HYP:" + currentHypernym.toString().replaceAll(",", "\n"));
					count++;
			}
		}
//		for( ArrayList<NounSynset> currentHypernymList : hypernymList ) {
//			for( NounSynset currentHypernym : currentHypernymList ) {
//				if( sourceSynsetList.contains(currentHypernym) ){
//					log.debug("HYP:" + currentHypernym.toString().replaceAll(",", "\n"));
//					count++;
//				}
//			}
//		}
		return count;
	}
	
	
	private List<NounSynset> buildHypernymList(List<NounSynset> nodeLookupList) {
		ArrayList<NounSynset> retVal = new ArrayList<NounSynset>();
		ArrayList<NounSynset> hypernyms;	
		
		for(int i=0; i < nodeLookupList.size() ; i++){
			hypernyms = doHypernymLookup(nodeLookupList.get(i), 1);
			retVal.addAll(hypernyms);
		}
		
		return retVal;
	}
	
	/**
	 * This method compiles a list of NounSynsets which are the hypernyms of all the passed in NounSynsets.
	 * @param hypernymLookupList
	 * @return
	 */
	private ArrayList<NounSynset> doHypernymLookup(NounSynset node, int depth) {
		ArrayList<NounSynset> hypernymSet = new ArrayList<NounSynset>();
		// lookup
		NounSynset[] hypernyms = node.getHypernyms();
		for (int i = 0; i < hypernyms.length; i++){
			hypernymSet.add( hypernyms[i] );
			if(depth < hypernymsDepthLimit)
				hypernymSet.addAll(doHypernymLookup(hypernyms[i], depth + 1));			
		}
		return hypernymSet;
	}
	
	/**
	 * This method returns the list of corresponding WordNet Synsets for given a Node.
	 * @param conceptNode
	 * @return
	 */
	private List<NounSynset> doLookUp(Node conceptNode)
	{	
		ArrayList<NounSynset> synonymSet = new ArrayList<NounSynset>();
		String localName = conceptNode.getLocalName();
		Synset[] synsets = WordNet.getSynsets(localName, SynsetType.NOUN);
		for (int i = 0; i < Math.min(synsets.length, wordnetSynsetsLimit); i++){
			Synset currentSynset = synsets[i];
			synonymSet.add( (NounSynset) currentSynset);
		}
		return synonymSet;
	}
	
	@Override
	public int getDefaultMaxSourceRelations() {
		return AbstractMatcher.ANY_INT;
	}
	
	@Override
	public int getDefaultMaxTargetRelations() {
		return AbstractMatcher.ANY_INT;
	}
	
	private void getInputMatcher()
	{
		if(inputMatchers.size()>0)
		{
			//log.debug("got the matchers");
			AbstractMatcher input = inputMatchers.get(0);
			classesMatrix = input.getClassesMatrix();
			propertiesMatrix = input.getPropertiesMatrix();
		}
	}

	
/*This function maps many Target Concepts to a source concept*/	
	private void matchManyTargetSource(Node source,Node target)
	{
		System.err.println("SD " + source.getUri() + " " + target.getUri());
		
		Node superClassOfTarget;
		Node superClassOfSource;
		
		/*GET THE INDEX OF THE INPUT PARAMETERS OF THE METHOD*/
		int indexSource = source.getIndex();
		int indexTarget = target.getIndex();
		
		
		ExtendedIterator targetIterator;
		ExtendedIterator targetSubClassIterator;
		OntClass ontClasstarget;
		OntClass subClass;
		
		/*THIS ARRAY LIST COLLECTS THE FIRST LEVEL OF SUB-CLASSES*/
		ArrayList<OntClass> targetSubClasses = new ArrayList<OntClass>();
		ArrayList<OntClass> targetSubClassesLevel2 = new ArrayList<OntClass>();
		
		superClassOfTarget = getSuperClass(target, targetClasses);
		superClassOfSource = getSuperClass(source, sourceClasses);
		
		System.err.println("SUPERCLASSES " + superClassOfSource + " " + superClassOfTarget);
		
		/*CONVERTING THE TARGET NODE INTO OntClass*/
		ontClasstarget = (OntClass)targetClasses.get(indexTarget).getResource().as(OntClass.class);
		targetIterator = ontClasstarget.listSubClasses();
		while(targetIterator.hasNext()){
			targetSubClasses.add((OntClass)targetIterator.next());
		}
		
		System.out.println(ontClasstarget.getURI());
		System.err.println("SUBCLASSES T " + targetSubClasses);
		
		
		/*MATCHING THE FIRST LEVEL OF SUB-CLASSES*/
		for (int k = 0; k < targetSubClasses.size(); k++) 
		{
			int row = getIndex(targetClasses,targetSubClasses.get(k).getURI());
			Node targetTemp = (Node)targetClasses.get(row);
			if(row != -1){
				newMapping(source,targetTemp,0.85d,MappingRelation.SUPERCLASS, "From equality mapping 1TS A");
			}
			/*IF THE SOURCE HAS A SUPER CLASS IT WILL HAVE THE SAME subClassOf RELATIONSHIP WITH THE TARGET CONCEPT*/
			if(superClassOfSource != null){
				newMapping(superClassOfSource, targetTemp, 0.85d, MappingRelation.SUPERCLASS, "From equality mapping 1TS B");
			}
		}
		
		/*IF SOME FIRST LEVEL OF SUBCLASSES OF TARGET HAVE THE SECOND LEVEL C subClassOf B subClassOf A WE ARE AT LEVEL B AND GOING TO LEVEL C*/
		if (targetSubClasses.size() > 0)
		{
				for (int i = 0; i < targetSubClasses.size(); i++)
				{
					subClass = targetSubClasses.get(i);
					targetSubClassIterator = subClass.listSubClasses();
					
					while(targetSubClassIterator.hasNext()){						
						OntClass temp = (OntClass)targetSubClassIterator.next();
						targetSubClassesLevel2.add(temp);
						int index1 = getIndex(targetClasses,temp.getURI());
						Node temp1 = targetClasses.get(index1);
						/*HERE THE SECOND LEVEL OF SUBCLASSES ARE BEING MATCHED*/
						newMapping(source,temp1,0.85d,MappingRelation.SUPERCLASS, "From equality mapping 2TS");
						if(superClassOfSource != null){
							newMapping(superClassOfSource,temp1,0.85d,MappingRelation.SUPERCLASS, "From equality mapping 2TS");
						}
					}
				}
		}
		/*IF THE THIRD  LEVEL GOES TO THE FOURTH  LEVEL AS IN D subClassOf C subClassOf B subClassOf A so I attempt to match D to A*/
		
		if (targetSubClassesLevel2.size() > 0 )
		{
				for (int i = 0; i < targetSubClassesLevel2.size(); i++)
				{
					subClass = targetSubClassesLevel2.get(i);
					targetSubClassIterator = subClass.listSubClasses();
					while(targetSubClassIterator.hasNext())
					{
						OntClass temp = (OntClass)targetSubClassIterator.next();
						int index1 = getIndex(targetClasses,temp.getURI());
						Node temp1 = targetClasses.get(index1);
						newMapping(source,temp1,0.85d,MappingRelation.SUPERCLASS, "From equality mapping 3TS");
						if(superClassOfSource != null)
						{
							newMapping(superClassOfSource,temp1,0.85d,MappingRelation.SUPERCLASS, "From equality mapping 3TS");
						}
					}
					
				}
		}
		
		/*FINALLY MATCHING THE SUPER CLASS TO THE TARGET CONCEPT WITH subClassOf relationship*/
		if(superClassOfTarget != null)
		{
			//newMapping(source,superClassOfTarget,0.85d,MappingRelation.SUPERCLASS, "From equality mapping F");
		}
		
	}
	private void matchManySourceTarget(Node source,Node target)
	{
		Node superClassOfSource;
		Node superClassOfTarget;
		
		List<OntClass> sourceSubClasses;
		ArrayList<OntClass> sourceSubClassesLevel2 = new ArrayList<OntClass>();
		
		int indexSource = source.getIndex();
		int indexTarget = target.getIndex();
		
		OntClass OntClasssource;
		OntClass subClass;
		OntClass temp;
		ExtendedIterator sourceIterator;
		ExtendedIterator sourceSubClassIterator;
		
		superClassOfSource = getSuperClass(source,sourceClasses);
		superClassOfTarget = getSuperClass(target,targetClasses);
		
		OntClasssource = (OntClass)sourceClasses.get(indexSource).getResource().as(OntClass.class);
		sourceSubClasses = OntClasssource.listSubClasses().toList();
		
		/* A node of target ontology has an EQUIVALENCE relation with source concept S
		 * B subClassOf A  here we are matching all B to S by subClassOf relationship */
		for (int k = 0; k < sourceSubClasses.size(); k++) 
		{
			int row = getIndex(sourceClasses,sourceSubClasses.get(k).getURI());
			Node sourceTemp = (Node)sourceClasses.get(row);
			newMapping(sourceTemp,target,0.85d,MappingRelation.SUBCLASS, "From equality mapping 1ST");
			if(superClassOfTarget != null){
				newMapping(sourceTemp, superClassOfTarget, 1.0, MappingRelation.SUBCLASS, "From equality mapping 1ST");
			}
		}
		
		/*IF SOME FIRST LEVEL OF SUBCLASSES HAVE THE SECOND LEVEL C subClassOf B subClassOf A WE ARE AT LEVEL B AND GOING TO LEVEL C to A*/
		if (sourceSubClasses.size() > 0)
		{
			//log.debug("I will print second level of subclasses if present");
			for (int i = 0; i < sourceSubClasses.size(); i++)
			{
				subClass = sourceSubClasses.get(i);
				sourceSubClassIterator = subClass.listSubClasses();
				while(sourceSubClassIterator.hasNext())
				{
					temp = (OntClass)sourceSubClassIterator.next();
					sourceSubClassesLevel2.add(temp);
					int index1 = getIndex(sourceClasses,temp.getURI());
					Node temp1 = sourceClasses.get(index1);
					
					newMapping(temp1, target, 1.0, MappingRelation.SUBCLASS, "From equality mapping 2ST");
					
					if (superClassOfTarget != null){
						newMapping(temp1, superClassOfTarget, 1.0, MappingRelation.SUBCLASS, "From equality mapping 2ST");
					}
				}
			}
			
		}
		
		/*IF SOME SECOND LEVEL OF SUBCLASSES HAVE THE THIRD  LEVEL D subClassOf C subClassOf B subClassOf A WE ARE AT LEVEL C AND GOING TO LEVEL D and matching D to A*/
		if (sourceSubClassesLevel2.size() > 0)
		{
			
			for (int i = 0; i < sourceSubClassesLevel2.size(); i++)
			{
				subClass = sourceSubClassesLevel2.get(i);
				sourceSubClassIterator = subClass.listSubClasses();
				while(sourceSubClassIterator.hasNext())
				{
					temp = (OntClass)sourceSubClassIterator.next();
					int index1 = getIndex(sourceClasses,temp.getURI());
					Node temp1 = sourceClasses.get(index1);
					//newMapping(temp1, target, 1.0, MappingRelation.SUBCLASS, "From equality mapping 3ST");
					if (superClassOfTarget != null){
						//newMapping(temp1, superClassOfTarget, 1.0, MappingRelation.SUBCLASS, "From equality mapping 3ST");
					}
				}
			}
			
		}
			
		if(superClassOfSource != null){
			//log.debug("I have matcher "+source.getLocalName() +" TO "+superClassOfSource.getLocalName());
			classesMatrix.set(superClassOfSource.getIndex(),target.getIndex(),new Mapping(superClassOfSource,target,0.85d,MappingRelation.SUPERCLASS));
		}
	}
	
	private Node getSuperClass(Node Target, List<Node> list)
	{
		int indexTarget = Target.getIndex();
		List<OntClass> targetSuperClasses = new ArrayList<OntClass>();
		//targetSuperClasses = null;
		OntClass OntClassTarget = (OntClass)list.get(indexTarget).getResource().as(OntClass.class);
		/*ExtendedIterator targetIterator = OntClassTarget.listSuperClasses();
		while(targetIterator.hasNext())
		{
			targetSuperClasses.add((OntClass)targetIterator.next());
		}*/
		
		targetSuperClasses = OntClassTarget.listSuperClasses().toList();
		
		for (int k = 0; k < targetSuperClasses.size(); k++) 
		{
			int row1 = getIndex(list,targetSuperClasses.get(k).getURI());
			if(row1 != -1)
			{
				Node targetTemp = (Node)list.get(row1);
				//log.debug("Node being returned is "+targetTemp.getLocalName());
				return targetTemp;
			}
		}
		return null;
	}	
	
	public static int getIndex(List<Node> list, String uri){
		for (int i = 0; i < list.size(); i++){
			if(list.get(i).getUri().equals(uri))
				return i;
		}
		return -1;
	}
	
	private void computeTransitiveClosure() {
		Mapping m;
		for (int i = 0; i < classesMatrix.getRows(); i++) {
			for (int j = 0; j < classesMatrix.getColumns(); j++) {
				 m = classesMatrix.get(i, j);
				 if(m == null) continue;
				 if(m.getRelation() == MappingRelation.SUBCLASS){
					 OntClass source = sourceClasses.get(m.getSourceKey()).getResource().as(OntClass.class);
					 List<OntClass> subclasses = listAllSubclasses(source, null);
					 for(OntClass cl: subclasses){
						 int index = getIndex(sourceClasses, cl.getURI());
						 Mapping mapping = new Mapping(sourceClasses.get(index), m.getEntity2(), m.getSimilarity(), m.getRelation());
						 classesMatrix.set(index, j, mapping);
					 }
				 }
				 else if(m.getRelation() == MappingRelation.SUPERCLASS){
					 OntClass target = targetClasses.get(m.getTargetKey()).getResource().as(OntClass.class);
					 List<OntClass> subclasses = listAllSubclasses(target, null);
					 for(OntClass cl: subclasses){
						 int index = getIndex(targetClasses, cl.getURI());
						 Mapping mapping = new Mapping(m.getEntity1(), targetClasses.get(index), m.getSimilarity(), m.getRelation());
						 classesMatrix.set(i, index, mapping);
					 }
				 }
			}
		}		
	}
	
	public void printMatrix(SimilarityMatrix matrix){
		Mapping m;
		double sim;
		DecimalFormat df = new DecimalFormat("0.00");
		for (int i = 0; i < matrix.getRows(); i++) {
			for (int j = 0; j < matrix.getColumns(); j++) {
				m = matrix.get(i, j);
				if(m == null) sim = 0.0;
				else {
					sim = m.getSimilarity();
					System.out.println(m.getProvenance());
				}
				//System.out.print(df.format(sim) + " ");
				
			}
			System.out.println();
		}
	}
	
	public void newMapping(Node sourceNode, Node targetNode, double sim, MappingRelation rel, String provenance){
		Mapping m = new Mapping(sourceNode, targetNode, sim, rel);
		
		if(useProvenanceToDebug){
			provenance += "|" + sourceNode.getComment() + "|" + targetNode.getComment() 
					+ "|" + LODUtils.superclassesString(sourceNode) + "|" + LODUtils.superclassesString(targetNode);
		}
		m.setProvenance(provenance);
		classesMatrix.set(sourceNode.getIndex(), targetNode.getIndex(), m);
	}
	
	private void filterExternalMappings() {
		
		String sourceURI = sourceOntology.getURI();
		String targetURI = targetOntology.getURI();		
		
		if(sourceURI == null || sourceURI.isEmpty())
			sourceURI = "http://purl.org/ontology/mo/";
		
		System.out.println("SourceOntology: " + sourceURI);
		System.out.println("TargetOntology: " + targetURI);
		
		
		System.out.println("SOURCE");
		for (int i = 0; i < sourceClasses.size(); i++) {
			System.out.println(sourceClasses.get(i).getUri());			
		}
		System.out.println("TARGET");
		for (int j = 0; j < targetClasses.size(); j++) {
			System.out.println(targetClasses.get(j).getUri());		
		}
		
		for (int i = 0; i < sourceClasses.size(); i++) {
			for (int j = 0; j < targetClasses.size(); j++) {
				if(classesMatrix.get(i, j) == null) continue;
				if(!sourceClasses.get(i).getUri().startsWith(sourceURI)	&&
						!targetClasses.get(j).getUri().startsWith(targetOntology.getURI())){
					System.out.println("Removing mapping: " + classesMatrix.get(i,j));
					classesMatrix.set(i, j, null);
				}
					
			}
		}
		
	}
}