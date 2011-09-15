package am.app.mappingEngine.multiWords.newMW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import simpack.measure.weightingscheme.StringTFIDF;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.StringUtil.AMStringWrapper;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.ontology.Node;
import am.app.ontology.profiling.OntologyProfiler;
import am.utility.Pair;

import com.hp.hpl.jena.ontology.OntResource;
import com.wcohen.ss.api.StringWrapper;

public class NewMultiWordsMatcher extends AbstractMatcher { 

	/**
	 * 
	 */
	private static final long serialVersionUID = -8492028869952801951L;

	// Logger
	//private static Logger log = Logger.getLogger(MultiWordsMatcher.class);
	

	private transient Normalizer normalizer;
	private List<String> sourceClassDocuments = new ArrayList<String>();
	private List<String> targetClassDocuments = new ArrayList<String>();
	private List<String> sourcePropDocuments = new ArrayList<String>();
	private List<String> targetPropDocuments = new ArrayList<String>();
	
	private transient List<StringWrapper> classCorpus = new ArrayList<StringWrapper>();
	private transient List<StringWrapper> propCorpus = new ArrayList<StringWrapper>();
	
	private transient StringTFIDF tfidfClasses;
	private transient StringTFIDF tfidfProperties;
	
	// Lexicons
	private transient Lexicon sourceOntologyLexicon, targetOntologyLexicon;
	private transient Lexicon sourceWordNetLexicon, targetWordNetLexicon; 
	
	//provenance string vars here
	String provenanceString;
	String mWS;//multiword string that will be added to the provenance string
	
	public NewMultiWordsMatcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
		if(param.storeProvenance){provenanceString="\t********Vector-Based MultiWords Matcher********\n";}
		addFeature(MatcherFeature.ONTOLOGY_PROFILING);
		addFeature(MatcherFeature.ONTOLOGY_PROFILING_CLASS_ANNOTATION_FIELDS);
		addFeature(MatcherFeature.ONTOLOGY_PROFILING_PROPERTY_ANNOTATION_FIELDS);
		addFeature(MatcherFeature.MAPPING_PROVENANCE);
	}
	
	public NewMultiWordsMatcher( NewMultiWordsParameters param_new ) {
		super(param_new);
		if(param.storeProvenance){provenanceString="\t********Vector-Based MultiWords Matcher********\n";}
		// features supported
		addFeature(MatcherFeature.ONTOLOGY_PROFILING);
		addFeature(MatcherFeature.ONTOLOGY_PROFILING_CLASS_ANNOTATION_FIELDS);
		addFeature(MatcherFeature.ONTOLOGY_PROFILING_PROPERTY_ANNOTATION_FIELDS);
		addFeature(MatcherFeature.MAPPING_PROVENANCE);
	}
	
	
	public String getDescriptionString() {
		return "Performs a local matching using a Multi words String Based technique.\n" +
				"Different concept and neighbouring strings are considered in the process.\n" +
				"A multi words string is built and preprocessed with cleaning, stemming, stop-words removing, and tokenization techniques.\n" +
				"Differnt token based vector space similarity techniques are available to compare preprocessed strings.\n" +
				"A similarity matrix contains the similarity between each pair (sourceNode, targetNode).\n" +
				"A selection algorithm select valid alignments considering threshold and number of relations per node.\n"; 
	}
	
	
	
	/* *******************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		NewMultiWordsParameters parameters =(NewMultiWordsParameters)param;
		//prepare the normalizer to preprocess strings
		normalizer = new Normalizer(parameters.normParameter);
		
		// lexicon support.
		
		if( parameters.useLexiconDefinitions || parameters.useLexiconSynonyms ) {
			// build all the lexicons if they don't exist. 
			sourceOntologyLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
			targetOntologyLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
			sourceWordNetLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
			targetWordNetLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
		}
		
		
		if(alignClass) {
			//Class corpus is the list of documents from source and target. Each node consists of one document containing many terms: localname, label, all terms from comment and so on...
			sourceClassDocuments = createDocumentsFromNodeList(sourceOntology.getClassesList(), alignType.aligningClasses);
			targetClassDocuments = createDocumentsFromNodeList(targetOntology.getClassesList(), alignType.aligningClasses);
			classCorpus = new ArrayList<StringWrapper>();
		
			//Create the corpus of documents
			//the TFIDF requires a corpus that is the list of total documents
			//each node consist of one document
			//Each document string must be wrapped in a StringWrapper
			Iterator<String> it = sourceClassDocuments.iterator();
			 while(it.hasNext()) {
				 String s = it.next();
				 AMStringWrapper sw = new AMStringWrapper(s);
				 classCorpus.add(sw);
			 }
			 it = targetClassDocuments.iterator();
			 while(it.hasNext()) {
				 String s = it.next();
				 AMStringWrapper sw = new AMStringWrapper(s);
				 classCorpus.add(sw);
			 }
			 if(((NewMultiWordsParameters)param).measure.equals(NewMultiWordsParameters.TFIDF)){
				 tfidfClasses = new StringTFIDF(classCorpus);
			 }
		}
		
		if(alignProp) {
			sourcePropDocuments = createDocumentsFromNodeList(sourceOntology.getPropertiesList(),alignType.aligningProperties);
			targetPropDocuments = createDocumentsFromNodeList(targetOntology.getPropertiesList(),alignType.aligningProperties);
			propCorpus = new ArrayList<StringWrapper>();
			
			//Create the corpus of documents
			//the TFIDF requires a corpus that is the list of total documents
			//each node consist of one document
			//Each document string must be wrapped in a StringWrapper
			Iterator<String> it = sourcePropDocuments.iterator();
			 while(it.hasNext()) {
				 String s = it.next();
				 AMStringWrapper sw = new AMStringWrapper(s);
				 propCorpus.add(sw);
			 }
			 it = targetPropDocuments.iterator();
			 while(it.hasNext()) {
				 String s = it.next();
				 AMStringWrapper sw = new AMStringWrapper(s);
				 propCorpus.add(sw);
			 }
			 if(((NewMultiWordsParameters)param).measure.equals(NewMultiWordsParameters.TFIDF)){
				 tfidfProperties = new StringTFIDF(propCorpus);
				 
			 }
		}
		
			

	}
	
	private List<String> createDocumentsFromNodeList( List<Node> nodeList, alignType typeOfNodes) throws Exception {
		List<String> documents = new ArrayList<String>();
		
		for( Node node : nodeList ) {
			String document = createVirtualDocument(node,typeOfNodes) ;
			String normDocument = normalizer.normalize(document);
			documents.add(normDocument);
		}
		return documents;
	}
	
	/**
	 * Here we are given a concept node, and we have to create the "virtual document" or "bag of words" string
	 * for this concept.
	 * 
	 * 0. Start with an empty string.
	 * 1. Create ego-net string of current node.
	 * 		1a. Add annotation profiling strings.
	 * 		1b. Add lexicon synonyms.
	 * 		1c. Add lexicon definitions.
	 * 2. Add ego-net string of parents.
	 * 3. Add ego-net string of siblings.
	 * 4. Add ego-net string of children.
	 * 5. Include instance information.
	 * 6. If concept is a class -> add ego-net strings of properties declared by this class.
	 * 7. If concept is a property -> add ego-net strings of classes declaring this property.
	 */
	private String createVirtualDocument(Node node, alignType typeOfNodes) throws Exception {
		
		mWS = new String();
		String multiWordsString = "";

		NewMultiWordsParameters mp = (NewMultiWordsParameters)param;
		
		multiWordsString = Utility.smartConcat(multiWordsString, createEgoNetString(node) );

		
		if( mp.includeParents ) {
			if( param.storeProvenance ) mWS+="considering parents.\n";
			
			for( Node parentNode : node.getParents() ) {
				if( !parentNode.isRoot() )
				multiWordsString = Utility.smartConcat(multiWordsString, createEgoNetString(parentNode) );
			}
		}
		
		if( mp.includeSiblings ) {
			if( param.storeProvenance ) mWS+="considering siblings.\n";
			
			for( Node siblingNode : node.getSiblings() ) {
				multiWordsString = Utility.smartConcat(multiWordsString, createEgoNetString(siblingNode));
			}
		}
		
		if( mp.includeChildren ) {
			if( param.storeProvenance ) mWS+="considering children.\n";
			
			for( Node childNode : node.getChildren() ) {
				multiWordsString = Utility.smartConcat(multiWordsString, createEgoNetString(childNode) );
			}
		}
		
		//add instances strings
		if(mp.considerInstances && typeOfNodes == alignType.aligningClasses) {
			if( param.storeProvenance ) mWS+="considering instances:\n";
			String instancesString = "";
			Iterator<String> it = node.getIndividuals().iterator();
			while(it.hasNext()) {
				String ind = it.next();
				instancesString = Utility.smartConcat(instancesString, ind);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, instancesString);
			if( param.storeProvenance ) mWS+="\tinstances string: "+instancesString+"\n";
		}
		
		//add properties declared by this class or classes declaring this properties
		if(mp.considerProperties && typeOfNodes == alignType.aligningClasses) {
			if( param.storeProvenance ) mWS+="considering properties:\n";
			String propString = "";
			Iterator<String> it = node.getpropOrClassNeighbours().iterator();
			while(it.hasNext()) {
				String s = it.next();
				propString = Utility.smartConcat(propString, s);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, propString);
			if( param.storeProvenance ) mWS+="\tproperties string: "+propString+"\n";
		}
			
	    //add classes declaring this properties
		if(mp.considerClasses && typeOfNodes == alignType.aligningProperties) {
			if( param.storeProvenance ) mWS+="considering classess:\n";
			String classString = "";
			Iterator<String> it = node.getpropOrClassNeighbours().iterator();
			while(it.hasNext()) {
				String s = it.next();
				classString = Utility.smartConcat(classString, s);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, classString);
			if( param.storeProvenance ) mWS+="\tclass string: "+classString+"\n";
		}
		
		// lexicons
		
		
		
		
		/*if( mp.considerSuperClass ) {
			if( param.storeProvenance ) mWS+="considering super class:\n";
			List<Node> parent = node.getParents();
			if( param.storeProvenance ) mWS+="\tsuper class parents: \n";
			for( Node par : parent ) {
				multiWordsString = Utility.smartConcat(multiWordsString, par.getLabel() );
				if( param.storeProvenance ) mWS+="\t\t "+par.getLabel()+"\n";
			}
			
		}*/
		
		return multiWordsString;
		
	}

	/**
	 * Create an ego net string.  An ego net string uses strings that 
	 * are directly defined on the given node.
	 */
	private String createEgoNetString( Node node ) throws Exception {
		
		NewMultiWordsParameters mp = (NewMultiWordsParameters)param;
		
		// 1. Add annotation profiler string.
		
		String egoNetString = new String();
		
		OntologyProfiler profiler = Core.getInstance().getOntologyProfiler();
		
		if( profiler != null ) {
			// we are using ontology profiling

			List<String> annList = profiler.getAnnotations(node);
			
			for( String currentWord : annList ) {
				egoNetString = Utility.smartConcat(egoNetString, currentWord);
			}
		}
		
		// 2. Add lexicon synonyms
		
		if( mp.useLexiconSynonyms ) {
			if( param.storeProvenance ) mWS+="considering lexicon synonyms:\n";
			String synonyms = null;
			OntResource nodeResource = node.getResource().as(OntResource.class);
			
			if( node.getOntologyID() == sourceOntology.getID() ) {
				// look up the definition in the source lexicons
				LexiconSynSet sourceOntSS = sourceOntologyLexicon.getSynSet(nodeResource);
				synonyms = makeSynonymsString(sourceOntSS); 
			} 
			else if( node.getOntologyID() == targetOntology.getID() ) {
				// look up the definition in the target lexicons
				LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(nodeResource);
				synonyms = makeSynonymsString(targetOntSS); 
			} 
			else {
				throw new Exception("Cannot find which ontology the node belongs to.");
			}
			
			if( !synonyms.isEmpty() ) egoNetString = Utility.smartConcat(egoNetString, synonyms);
			if( param.storeProvenance ) mWS+="\tsynonyms: "+synonyms+"\n";
			
		}
		
		// 3. Add lexicon definitions 
		
		if( mp.useLexiconDefinitions ) {
			if( param.storeProvenance ) mWS+="considering lexicon definitions:\n";
			String definitions = new String();
			OntResource nodeResource = node.getResource().as(OntResource.class);
			
			if( node.getOntologyID() == sourceOntology.getIndex() ) {
				// look up the definition in the source lexicons
				LexiconSynSet sourceOntSS = sourceOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet sourceWNSS = sourceWordNetLexicon.getSynSet(nodeResource);
				if( sourceOntSS != null ) definitions = Utility.smartConcat(definitions, sourceOntSS.getGloss());
				if( sourceWNSS != null ) definitions = Utility.smartConcat(definitions, sourceWNSS.getGloss());
			} else if( node.getOntologyID() == targetOntology.getIndex() ) {
				// look up the definition in the target lexicons
				LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet targetWNSS = targetWordNetLexicon.getSynSet(nodeResource);
				if( targetOntSS != null ) definitions = Utility.smartConcat(definitions, targetOntSS.getGloss());
				if( targetWNSS != null ) definitions = Utility.smartConcat(definitions, targetWNSS.getGloss());
			} else {
				throw new Exception("Cannot find which ontology the node belongs to.");
			}
			
			if( !definitions.equals("") ) egoNetString = Utility.smartConcat(egoNetString, definitions);
			if( param.storeProvenance ) mWS+="\tdefinitions: "+definitions+"\n";
		}
		
		return egoNetString;
	}
	
	private String makeSynonymsString(LexiconSynSet ontSS) {
		String synonymsString = new String();
		
		if( ontSS != null )
		for( String ontSyn : sourceOntologyLexicon.extendSynSet(ontSS) ) {
			synonymsString = Utility.smartConcat(synonymsString, ontSyn);
		}
		
		return synonymsString;
	}

	/*private String getLabelAndOrNameString(Node node) {
		String result = "";
		NewMultiWordsParameters mp = (NewMultiWordsParameters)param;
		//Add concept strings to the multiwordsstring
		if(!mp.ignoreLocalNames) { 
			//localname sometimes are just irrelevant codes so this boolean value should be false
			//often are equal to label so label and local must be considered once
				if(!node.getLocalName().equalsIgnoreCase(node.getLabel())) {
					result = Utility.smartConcat(result, node.getLocalName());
				}
			}
			result = Utility.smartConcat(result, node.getLabel());
		return result;
	}*/


	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */

	public Mapping alignTwoNodes(Node source, Node target,alignType typeOfNodes) {
		NewMultiWordsParameters mp = (NewMultiWordsParameters)param;
		double sim = 0;
		
		String sourceString;
		String targetString;
		 if(typeOfNodes == alignType.aligningClasses) {
			 //System.out.println(source.getIndex()-1);
			 //System.out.println(target.getIndex()-1);
			 sourceString = sourceClassDocuments.get(source.getIndex());
			 targetString = targetClassDocuments.get(target.getIndex());
		 }
		 else {
			 sourceString = sourcePropDocuments.get(source.getIndex());
			 targetString = targetPropDocuments.get(target.getIndex());
		 }
		
		//calculate similarity
		if(mp.measure.equals(NewMultiWordsParameters.COSINE)) {
			CosineSimilarity measure = new CosineSimilarity();
			sim = measure.getSimilarity(sourceString, targetString);
		}
		else 	if(mp.measure.equals(NewMultiWordsParameters.JACCARD)) {
			JaccardSimilarity measure = new JaccardSimilarity();
			sim = measure.getSimilarity(sourceString, targetString); 
		}
		else 	if(mp.measure.equals(NewMultiWordsParameters.EUCLIDEAN)) {
			EuclideanDistance measure = new EuclideanDistance();
			sim = measure.getSimilarity(sourceString, targetString); 
		}
		else 	if(mp.measure.equals(NewMultiWordsParameters.DICE)) {
			DiceSimilarity measure = new DiceSimilarity();
			sim = measure.getSimilarity(sourceString, targetString); 
		}
		else 	if(mp.measure.equals(NewMultiWordsParameters.TFIDF)) {
			 StringTFIDF tfidf;
			 if(typeOfNodes == alignType.aligningClasses) {
				 tfidf = tfidfClasses;
			 }
			 else tfidf = tfidfProperties;
			 
			 
			 //calculate similarity
			 sim = tfidf.getSimilarity(sourceString, targetString);
			
		}
		
		Mapping pmapping=new Mapping(source, target, sim);
		if(param.storeProvenance && sim > param.threshold){
			provenanceString+="sim(\""+source+"\",\""+target+"\") = "+sim+"\n";
			provenanceString+="similarity metric used: "+((NewMultiWordsParameters)param).measure+"\n";
			provenanceString+=mWS;
			pmapping.setProvenance(provenanceString);
		}
		return pmapping;
		
	}
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new NewMultiWordsParametersPanel();
		}
		return parametersPanel;
	}
	
	
	
	
	



	      
}

