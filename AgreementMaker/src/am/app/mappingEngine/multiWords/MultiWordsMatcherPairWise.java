package am.app.mappingEngine.multiWords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import simpack.measure.weightingscheme.StringTFIDF;
import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.EuclideanDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.OverlapCoefficient;
import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.MappedNodes;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.StringUtil.AMStringWrapper;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Node;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.wcohen.ss.api.StringWrapper;

public class MultiWordsMatcherPairWise extends AbstractMatcher { 

	/**
	 * 
	 */
	private static final long serialVersionUID = -8492028869952801951L;

	// Logger
	//private static Logger log = Logger.getLogger(MultiWordsMatcher.class);

	// The hashmap and the list of string are used to optimize the LSM when running with SCS enabled.
	private HashMap<Node,List<String>> sourceExtendedSynSets;
	private HashMap<Node,List<String>> targetExtendedSynSets;
	private List<String> extendedSingle;
	private boolean sourceIsLarger = false;  // TODO: Figure out a better way to do this.

	// use this to save time.
	private LexiconSynSet sourceSet;  // using this field variable gives a 3% speed boost to LSM without SCS.


	private transient Normalizer normalizer;
	private ArrayList<String> sourceClassDocuments = new ArrayList<String>();
	private ArrayList<String> targetClassDocuments = new ArrayList<String>();
	private ArrayList<String> sourcePropDocuments = new ArrayList<String>();
	private ArrayList<String> targetPropDocuments = new ArrayList<String>();

	private transient ArrayList<StringWrapper> classCorpus = new ArrayList<StringWrapper>();
	private transient ArrayList<StringWrapper> propCorpus = new ArrayList<StringWrapper>();

	private transient StringTFIDF tfidfClasses;
	private transient StringTFIDF tfidfProperties;

	// Lexicons
	private transient Lexicon sourceOntologyLexicon, targetOntologyLexicon;
	private transient Lexicon sourceWordNetLexicon, targetWordNetLexicon; 

	//provenance string vars here
	String provenanceString;
	String mWS;//multiword string that will be added to the provenance string

	public MultiWordsMatcherPairWise() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
		if(param.storeProvenance){provenanceString="\t********Vector-Based MultiWords Matcher********\n";}
		addFeature(MatcherFeature.MAPPING_PROVENANCE);
	}

	public MultiWordsMatcherPairWise( MultiWordsPairWiseParameters param_new ) {
		super(param_new);
		if(param.storeProvenance){provenanceString="\t********Vector-Based MultiWords Matcher********\n";}
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
		MultiWordsPairWiseParameters parameters =(MultiWordsPairWiseParameters)param;
		//prepare the normalizer to preprocess strings
		normalizer = new Normalizer(parameters.normParameter);
		//minInputMatchers = 1;
		// lexicon support.


		if( parameters.useLexiconDefinitions || parameters.useLexiconSynonyms ) {

			sourceOntologyLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
			targetOntologyLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			


			// build all the lexicons if they don't exist. 
			//			sourceOntologyLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
			//			targetOntologyLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);			
			//			sourceWordNetLexicon = Core.getLexiconStore().getLexicon(sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
			//			targetWordNetLexicon = Core.getLexiconStore().getLexicon(targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
			sourceWordNetLexicon = null;
			targetWordNetLexicon = null;
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
			if(((MultiWordsPairWiseParameters)param).measure.equals(MultiWordsPairWiseParameters.TFIDF)){
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
			if(((MultiWordsPairWiseParameters)param).measure.equals(MultiWordsPairWiseParameters.TFIDF)){
				tfidfProperties = new StringTFIDF(propCorpus);

			}
		}



	}

	private ArrayList<String> createDocumentsFromNodeList(ArrayList<Node> nodeList, alignType typeOfNodes) throws Exception {
		ArrayList<String> documents = new ArrayList<String>();

		for( Node node : nodeList ) {
			String document = createMultiWordsString(node,typeOfNodes) ;
			String normDocument = normalizer.normalize(document);
			documents.add(normDocument);
		}
		return documents;
	}

	@SuppressWarnings("unchecked")
	private String createMultiWordsString(Node node, alignType typeOfNodes) throws Exception {

		mWS = new String();
		String multiWordsString = "";

		MultiWordsPairWiseParameters mp = (MultiWordsPairWiseParameters)param;

		//Add concept strings to the multiwordsstring
		if(mp.considerConcept) {
			multiWordsString = Utility.smartConcat(multiWordsString, getLabelAndOrNameString(node));
			multiWordsString = Utility.smartConcat(multiWordsString, node.getComment());
			multiWordsString = Utility.smartConcat(multiWordsString, node.getSeeAlsoLabel());
			multiWordsString = Utility.smartConcat(multiWordsString, node.getIsDefinedByLabel());

			if( param.storeProvenance ) {
				mWS+="considering Concept:\n";
				mWS+="\tlabel and/or name: "+getLabelAndOrNameString(node)+"\n";
				mWS+="\tcomment: "+node.getComment()+"\n";
				mWS+="\tSee also label: "+node.getSeeAlsoLabel()+"\n";
				mWS+="\tis defined by label: "+node.getIsDefinedByLabel()+"\n";
			}
		}

		//add neighbors strings
		if(mp.considerNeighbors) {
			if( param.storeProvenance ) mWS+="considering neighbors:\n";

			String neighbourString = "";
			HashSet<Node> neighborNodes = new HashSet<Node>(); // use a hashset to avoid duplicates

			// add child strings
			List<Node> children = node.getChildren();
			for( Node child : children ) { neighborNodes.add(child); }


			// add father nodes and the father's children (siblings)
			List<Node> parents = node.getParents();
			for( Node parent : parents ) {
				for( Node sibling : parent.getChildren() ) {
					neighborNodes.add(sibling);
				}
			}

			for( Node neighbor : neighborNodes ) { 
				neighbourString = Utility.smartConcat(neighbourString, getLabelAndOrNameString(neighbor));
			}
			multiWordsString = Utility.smartConcat(multiWordsString, neighbourString);

			if( param.storeProvenance ) mWS+="\tneighbour string: "+neighbourString+"\n";

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
		if( mp.useLexiconDefinitions ) {
			if( param.storeProvenance ) mWS+="considering lexicon definitions:\n";
			String definitions = new String();
			OntResource nodeResource = node.getResource().as(OntResource.class);

			if( node.getOntologyID() == sourceOntology.getIndex() ) {
				// look up the definition in the source lexicons
				LexiconSynSet sourceOntSS = sourceOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet sourceWNSS = null; //sourceWordNetLexicon.getSynSet(nodeResource);
				if( sourceOntSS != null ) definitions = Utility.smartConcat(definitions, sourceOntSS.getGloss());
				//if( sourceWNSS != null ) definitions = Utility.smartConcat(definitions, sourceWNSS.getGloss());
			} else if( node.getOntologyID() == targetOntology.getIndex() ) {
				// look up the definition in the target lexicons
				LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet targetWNSS = null; //targetWordNetLexicon.getSynSet(nodeResource);
				if( targetOntSS != null ) definitions = Utility.smartConcat(definitions, targetOntSS.getGloss());
				if( targetWNSS != null ) definitions = Utility.smartConcat(definitions, targetWNSS.getGloss());
			} else {
				throw new Exception("Cannot find which ontology the node belongs to.");
			}

			if( !definitions.equals("") ) multiWordsString = Utility.smartConcat(multiWordsString, definitions);
			if( param.storeProvenance ) mWS+="\tdefinitions: "+definitions+"\n";
		}

		if( mp.useLexiconSynonyms ) {
			if( param.storeProvenance ) mWS+="considering lexicon synonyms:\n";
			String synonyms = new String();
			OntResource nodeResource = node.getResource().as(OntResource.class);

			if( node.getOntologyID() == sourceOntology.getID() ) {
				// look up the definition in the source lexicons
				LexiconSynSet sourceOntSS = sourceOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet sourceWNSS = null; //sourceWordNetLexicon.getSynSet(nodeResource);

				String ss = makeSynonymsString(sourceOntSS, sourceWNSS); 
				//System.out.println(ss+"\n");


				if( !ss.isEmpty() ) synonyms = Utility.smartConcat(synonyms, ss);
			} else if( node.getOntologyID() == targetOntology.getID() ) {
				// look up the definition in the target lexicons
				LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet targetWNSS = null; //targetWordNetLexicon.getSynSet(nodeResource);

				String ss = makeSynonymsString(targetOntSS, targetWNSS); 
				//System.out.println(ss+"\n");

				if( !ss.isEmpty() ) synonyms = Utility.smartConcat(synonyms, ss);
			} else {
				throw new Exception("Cannot find which ontology the node belongs to.");
			}

			if( !synonyms.isEmpty() ) multiWordsString = Utility.smartConcat(multiWordsString, synonyms);
			if( param.storeProvenance ) mWS+="\tsynonyms: "+synonyms+"\n";
		}

		if( mp.considerSuperClass ) {

			if( param.storeProvenance ) mWS+="considering super class:\n";
			List<Node> parent = node.getParents();
			if( param.storeProvenance ) mWS+="\tsuper class parents: \n";
			for( Node par : parent ) {
				multiWordsString = Utility.smartConcat(multiWordsString, par.getLabel() );
				if( param.storeProvenance ) mWS+="\t\t "+par.getLabel()+"\n";


			}

		}
		System.out.println(multiWordsString+"\n");
		return multiWordsString;

	}

	private String makeSynonymsString(LexiconSynSet ontSS,
			LexiconSynSet WNSS) {
		String synonymsString = new String();

		if( ontSS != null )
			for( String ontSyn : ontSS.getSynonyms() ) {
				synonymsString = Utility.smartConcat(synonymsString, ontSyn);
			}

		if( WNSS != null )
			for( String WNSyn : WNSS.getSynonyms() ) {
				synonymsString = Utility.smartConcat(synonymsString, WNSyn);
			}

		return synonymsString;
	}

	private String getLabelAndOrNameString(Node node) {
		String result = "";
		MultiWordsPairWiseParameters mp = (MultiWordsPairWiseParameters)param;
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
	}


	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */

	/**
	 * Method updated to handle ST. - Catia.
	 * 
	 */
	@Override
	protected SimilarityMatrix alignNodesOneByOne(ArrayList<Node> sourceList,
			ArrayList<Node> targetList, alignType typeOfNodes) throws Exception {
		System.out.println(inputMatchers.size());
		if(param.completionMode && inputMatchers != null && inputMatchers.size() > 0){ 

			//run in optimized mode by mapping only concepts that have not been mapped in the input matcher
			if(typeOfNodes.equals(alignType.aligningClasses)){
				return alignUnmappedNodes(sourceList, targetList, inputMatchers.get(0).getClassesMatrix(), inputMatchers.get(0).getClassAlignmentSet(), alignType.aligningClasses);
			}
			else{
				return alignUnmappedNodes(sourceList, targetList, inputMatchers.get(0).getPropertiesMatrix(), inputMatchers.get(0).getPropertyAlignmentSet(), alignType.aligningProperties);
			}
		}

		else{
			//run as a generic matcher who maps all concepts by doing a quadratic number of comparisons
			SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation);
			Node source;
			Node target;
			// create the hashmaps
			sourceExtendedSynSets = new HashMap<Node,List<String>>();
			targetExtendedSynSets = new HashMap<Node,List<String>>();

			for( Node currentClass : sourceList ) {
				OntResource currentOR = currentClass.getResource().as(OntResource.class);
				LexiconSynSet currentSet = sourceOntologyLexicon.getSynSet(currentOR);
				if( currentSet == null ) continue;
				List<String> currentExtension = sourceOntologyLexicon.extendSynSet(currentSet);
				currentExtension.addAll(currentSet.getSynonyms());
				sourceExtendedSynSets.put(currentClass, currentExtension);
				if( this.isCancelled() ) return null;
			}

			for( Node currentClass : targetList ) {
				OntResource currentOR = currentClass.getResource().as(OntResource.class);
				LexiconSynSet currentSet = targetOntologyLexicon.getSynSet(currentOR);
				if( currentSet == null ) continue;
				List<String> currentExtension = targetOntologyLexicon.extendSynSet(currentSet);
				currentExtension.addAll(currentSet.getSynonyms());
				targetExtendedSynSets.put(currentClass, currentExtension);
				if( this.isCancelled() ) return null;
			}


			// iterate through the larger ontology
			for( int i = 0; i < sourceList.size(); i++ ) {
				source = sourceList.get(i);
				System.out.print("\n"+i+"/"+sourceList.size()+":"+source.getLocalName()+" "+source.getLabel());


				for( int j = 0; j < targetList.size(); j++ ) {
					target = targetList.get(j);
					if( !this.isCancelled() ) {
						Mapping alignment = alignTwoNodes(source, target, typeOfNodes);
						if(alignment.getSimilarity()>=param.threshold)
							matrix.set(i,j,alignment);


						if( isProgressDisplayed() ) {
							stepDone(); // we have completed one step
							if(alignment != null && alignment.getSimilarity() >= param.threshold ) tentativealignments++; // keep track of possible alignments for progress display
						}
					}
				}
				if( isProgressDisplayed() ) { updateProgress(); }
			}

			return matrix;

		}

	}



	protected SimilarityMatrix alignUnmappedNodes(ArrayList<Node> sourceList,
			ArrayList<Node> targetList, SimilarityMatrix inputMatrix,
			Alignment<Mapping> inputAlignmentSet, alignType typeOfNodes)
					throws Exception {

		MappedNodes mappedNodes = new MappedNodes(sourceList, targetList, inputAlignmentSet, param.maxSourceAlign, param.maxTargetAlign);
		System.out.println(mappedNodes.getMappedSources().length+" sources already mapped");
		System.out.println(mappedNodes.getMappedTargets().length+" targets already mapped");
		SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation);
		matrix=inputMatrix;
		Node source;
		Node target;

		Mapping inputAlignment;

		// create the hashmaps
		sourceExtendedSynSets = new HashMap<Node,List<String>>();
		targetExtendedSynSets = new HashMap<Node,List<String>>();

		for( Node currentClass : sourceList ) {
			OntResource currentOR = currentClass.getResource().as(OntResource.class);
			LexiconSynSet currentSet = sourceOntologyLexicon.getSynSet(currentOR);
			if( currentSet == null ) continue;
			List<String> currentExtension = sourceOntologyLexicon.extendSynSet(currentSet);
			currentExtension.addAll(currentSet.getSynonyms());
			sourceExtendedSynSets.put(currentClass, currentExtension);
			if( this.isCancelled() ) return null;
		}

		for( Node currentClass : targetList ) {
			OntResource currentOR = currentClass.getResource().as(OntResource.class);
			LexiconSynSet currentSet = targetOntologyLexicon.getSynSet(currentOR);
			if( currentSet == null ) continue;
			List<String> currentExtension = targetOntologyLexicon.extendSynSet(currentSet);
			currentExtension.addAll(currentSet.getSynonyms());
			targetExtendedSynSets.put(currentClass, currentExtension);
			if( this.isCancelled() ) return null;
		}


		// iterate through the larger ontology
		for( int i = 0; i < sourceList.size(); i++ ) {
			source = sourceList.get(i);
			System.out.print("\n"+i+"/"+sourceList.size()+":"+source.getLocalName()+" "+source.getLabel());
			if(!mappedNodes.isSourceMapped(source)){
				
				for( int j = 0; j < targetList.size(); j++ ) {
					target = targetList.get(j);
					if( !this.isCancelled() ) {
						Mapping alignment = null;


						if(!mappedNodes.isTargetMapped(target)){

							alignment = alignTwoNodes(source, target, typeOfNodes);
							if(alignment.getSimilarity()>=param.threshold)
								matrix.set(i,j,alignment);
						}


						if( isProgressDisplayed() ) {
							stepDone(); // we have completed one step
							if(!mappedNodes.isSourceMapped(source) && !mappedNodes.isTargetMapped(target) && alignment != null && alignment.getSimilarity() >= param.threshold ) tentativealignments++; // keep track of possible alignments for progress display
						}
					}
				}
			}
			else{
				System.out.print(" mapped");
			}
			if( isProgressDisplayed() ) { updateProgress(); }
		}

		return matrix;

	}



	public Mapping alignTwoNodes(Node source, Node target,alignType typeOfNodes) {
		MultiWordsPairWiseParameters mp = (MultiWordsPairWiseParameters)param;
		double sim = 0;


		List<String> sourceExtendedSynonyms= new ArrayList<String>(), targetExtendedSynonyms= new ArrayList<String>();
		List<String> sourceExtendedParents = new ArrayList<String>(), targetExtendedParents=new ArrayList<String>();


		sourceExtendedSynonyms = sourceExtendedSynSets.get(source);
		targetExtendedSynonyms = targetExtendedSynSets.get(target);


		//System.out.println(target.getLocalName()+" "+target.getLabel()+" - "+source.getLocalName()+" "+source.getLabel()+":");


		if(mp.considerSuperClass){

			//get parents synonyms for source
			List<Node> parent = source.getParents();
			if( param.storeProvenance ) mWS+="\tsuper class parents: \n";
			if(parent!=null){
				for( Node par : parent ) {
					if(!par.getLabel().equals("")){
						sourceExtendedParents.addAll(sourceExtendedSynSets.get(par));
					}
				}
			}
			//get parents synonyms for target
			parent = target.getParents();
			if( param.storeProvenance ) mWS+="\tsuper class parents: \n";
			if(parent !=null){
				for( Node par : parent ) {
					if(!par.getLabel().equals("")){
						targetExtendedParents.addAll(targetExtendedSynSets.get(par));

					}
				}

			}
		}

		double maxSim=0.0;
		//		String sourceString="";
		//		String targetString="";
		//
		//		ArrayList<String> sourceWords = new ArrayList();
		//		ArrayList<String> targetWords = new ArrayList();
		//
		//		if(sourceExtendedSynonyms!=null){
		//
		//			for(int i=0; i<sourceExtendedSynonyms.size();i++){
		//				String[] ss=sourceExtendedSynonyms.get(i).split(" ");
		//				for(int a=0; a<ss.length;a++){
		//					if(!sourceWords.contains(ss[a]))
		//						sourceWords.add(ss[a]);
		//				}
		//			}
		//			if(mp.considerSuperClass){
		//				for(int j=0; j<sourceExtendedParents.size();j++){	
		//					String[] ss=sourceExtendedParents.get(j).split(" ");
		//					for(int a=0; a<ss.length;a++){
		//						if(!sourceWords.contains(ss[a]))
		//							sourceWords.add(ss[a]);
		//					}
		//				}
		//
		//			}
		//
		//			for(int x=0; x<sourceWords.size();x++){
		//				sourceString+=sourceWords.get(x)+" ";
		//			}
		//		}
		//		if(targetExtendedSynonyms!=null){
		//
		//
		//			for(int i=0; i<targetExtendedSynonyms.size();i++){
		//				String[] ss=targetExtendedSynonyms.get(i).split(" ");
		//				for(int a=0; a<ss.length;a++){
		//					if(!targetWords.contains(ss[a]))
		//						targetWords.add(ss[a]);
		//				}
		//
		//			}
		//			if(mp.considerSuperClass){
		//				for(int j=0; j<targetExtendedParents.size();j++){
		//					String[] ss=targetExtendedParents.get(j).split(" ");
		//					for(int a=0; a<ss.length;a++){
		//						if(!targetWords.contains(ss[a]))
		//							targetWords.add(ss[a]);
		//					}
		//
		//
		//				}
		//			}
		//			for(int x=0; x<targetWords.size();x++){
		//				targetString+=targetWords.get(x)+" ";
		//			}
		//
		//		}




		//		String sourceString="";
		//		String targetString="";
		//		if(sourceExtendedSynonyms!=null){
		//
		//			for(int i=0; i<sourceExtendedSynonyms.size();i++){
		//				sourceString+=" "+sourceExtendedSynonyms.get(i);
		//			}
		//			if(mp.considerSuperClass){
		//				for(int j=0; j<sourceExtendedParents.size();j++){
		//					sourceString+=" "+sourceExtendedParents.get(j);
		//				}
		//
		//			}
		//		}
		//		if(targetExtendedSynonyms!=null){
		//
		//
		//			for(int i=0; i<targetExtendedSynonyms.size();i++){
		//				targetString+=" "+targetExtendedSynonyms.get(i);
		//			}
		//			if(mp.considerSuperClass){
		//				for(int j=0; j<targetExtendedParents.size();j++){
		//					targetString+=" "+targetExtendedParents.get(j);
		//
		//				}
		//			}
		//
		//
		//		}		


		//make all combinations of labels, synonyms 
		List<String> sourceStrings=new ArrayList<String>();
		List<String> targetStrings=new ArrayList<String>();

		//lists to be able to check if there are shared words
		String[] sourceWords;
		String[] targetWords;

		if(typeOfNodes == alignType.aligningClasses) {

			sourceWords =sourceClassDocuments.get(source.getIndex()).split(" ");
			targetWords = targetClassDocuments.get(target.getIndex()).split(" ");
		}
		else {
			sourceWords = sourcePropDocuments.get(source.getIndex()).split(" ");
			targetWords = targetPropDocuments.get(target.getIndex()).split(" ");
		}

		boolean shared=false;
		for(int i=0; i<sourceWords.length;i++){
			for(int j=0; j<targetWords.length;j++){
				if(sourceWords[i].equalsIgnoreCase(targetWords[j])){
					shared=true;
					break;
				}
			}
		}

		if(shared==true){

			if(sourceExtendedSynonyms!=null){

				for(int i=0; i<sourceExtendedSynonyms.size();i++){
					String a=sourceExtendedSynonyms.get(i);

					if(mp.considerSuperClass){
						for(int j=0; j<sourceExtendedParents.size();j++){
							String b=sourceExtendedParents.get(j);
							String s=a+" "+b;
							sourceStrings.add(s);
						}
					}
					
						sourceStrings.add(a);
				}
			}
			if(targetExtendedSynonyms!=null){


				for(int i=0; i<targetExtendedSynonyms.size();i++){
					String a=targetExtendedSynonyms.get(i);
					if(mp.considerSuperClass){
						for(int j=0; j<targetExtendedParents.size();j++){
							String b=targetExtendedParents.get(j);
							String s=a+" "+b;
							targetStrings.add(s);

						}
					}
					
						targetStrings.add(a);
				}
			}





			for(int a=0; a<sourceStrings.size();a++){
				for(int b=0; b<targetStrings.size();b++){
					String sourceString=sourceStrings.get(a);
					String targetString=targetStrings.get(b);


					//calculate similarity
					if(mp.measure.equals(MultiWordsPairWiseParameters.COSINE)) {
						CosineSimilarity measure = new CosineSimilarity();
						sim = measure.getSimilarity(sourceString, targetString);
					}
					else 	if(mp.measure.equals(MultiWordsPairWiseParameters.JACCARD)) {
						JaccardSimilarity measure = new JaccardSimilarity();
						sim = measure.getSimilarity(sourceString, targetString); 
					}
					else 	if(mp.measure.equals(MultiWordsPairWiseParameters.EUCLIDEAN)) {
						EuclideanDistance measure = new EuclideanDistance();
						sim = measure.getSimilarity(sourceString, targetString); 
					}
					else 	if(mp.measure.equals(MultiWordsPairWiseParameters.DICE)) {
						DiceSimilarity measure = new DiceSimilarity();
						sim = measure.getSimilarity(sourceString, targetString); 
					}
					else 	if(mp.measure.equals(MultiWordsPairWiseParameters.TFIDF)) {
						StringTFIDF tfidf;
						if(typeOfNodes == alignType.aligningClasses) {
							tfidf = tfidfClasses;
						}
						else tfidf = tfidfProperties;


						//calculate similarity
						sim = tfidf.getSimilarity(sourceString, targetString);

					}
					else if (mp.measure.equals(MultiWordsPairWiseParameters.Overlap)) {
						OverlapCoefficient measure = new OverlapCoefficient();
						sim=measure.getSimilarity(sourceString, targetString);
					}
					//System.out.println(sourceString+" --- "+targetString+" : "+sim);
					if(sim>maxSim)
						maxSim=sim;

				}
			}
		}

		if(maxSim>=param.threshold)
			System.out.print(" ->"+target.getLabel()+" "+maxSim+"\n");


		Mapping pmapping=new Mapping(source, target, maxSim);
		if(param.storeProvenance && maxSim > param.threshold){
			provenanceString+="sim(\""+source+"\",\""+target+"\") = "+maxSim+"\n";
			provenanceString+="similarity metric used: "+((MultiWordsPairWiseParameters)param).measure+"\n";
			provenanceString+=mWS;
			pmapping.setProvenance(provenanceString);
			System.out.println(provenanceString);
		}
		return pmapping;

	}

	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new MultiWordsPairWiseParametersPanel(inputMatchers);
		}
		return parametersPanel;
	}









}

