package am.app.mappingEngine.multiWords;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.StringUtil.AMStringWrapper;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.NodeHierarchy;

import com.hp.hpl.jena.ontology.OntResource;
import com.wcohen.ss.api.StringWrapper;

public class MultiWordsMatcher extends AbstractMatcher { 

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
	
	public MultiWordsMatcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
		if(param.storeProvenance){provenanceString="\t********Vector-Based MultiWords Matcher********\n";}
		
		setName("Vector-based Multi-words Matcher");
		setCategory(MatcherCategory.SYNTACTIC);
		
		addFeature(MatcherFeature.MAPPING_PROVENANCE);
	}
	
	public MultiWordsMatcher( MultiWordsParameters param_new ) {
		super(param_new);
		if(param.storeProvenance){provenanceString="\t********Vector-Based MultiWords Matcher********\n";}
		
		setName("Vector-based Multi-words Matcher");
		setCategory(MatcherCategory.SYNTACTIC);
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
		MultiWordsParameters parameters =(MultiWordsParameters)param;
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
			 if(((MultiWordsParameters)param).measure.equals(MultiWordsParameters.TFIDF)){
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
			 if(((MultiWordsParameters)param).measure.equals(MultiWordsParameters.TFIDF)){
				 tfidfProperties = new StringTFIDF(propCorpus);
				 
			 }
		}
		
			

	}
	
	private List<String> createDocumentsFromNodeList( List<Node> nodeList, alignType typeOfNodes) throws Exception {
		List<String> documents = new ArrayList<String>();
		
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

		MultiWordsParameters mp = (MultiWordsParameters)param;
		
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
		//Catia: added just for children
		if(mp.considerSubclasses) {
			if( param.storeProvenance ) mWS+="considering subclasses:\n";
			
			String neighbourString = "";
			HashSet<Node> neighborNodes = new HashSet<Node>(); // use a hashset to avoid duplicates
			
			// add child strings
			List<Node> children = node.getChildren();
			for( Node child : children ) { neighborNodes.add(child); }
			
			for( Node neighbor : neighborNodes ) { 
				neighbourString = Utility.smartConcat(neighbourString, getLabelAndOrNameString(neighbor));
			}
			multiWordsString = Utility.smartConcat(multiWordsString, neighbourString);
			
			if( param.storeProvenance ) mWS+="\tsubclasses string: "+neighbourString+"\n";
			
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
				LexiconSynSet sourceWNSS = sourceWordNetLexicon.getSynSet(nodeResource);
				String ss = makeSynonymsString(sourceOntSS, sourceWNSS); 
				if( !ss.isEmpty() ) synonyms = Utility.smartConcat(synonyms, ss);
			} else if( node.getOntologyID() == targetOntology.getID() ) {
				// look up the definition in the target lexicons
				LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet targetWNSS = targetWordNetLexicon.getSynSet(nodeResource);
				String ss = makeSynonymsString(targetOntSS, targetWNSS); 
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
		
		if( node.getOntologyID() == sourceOntology.getID() && mp.sourceAlternateHierarchy != null ) {
			NodeHierarchy hierarchy = sourceOntology.getHierarchy(mp.sourceAlternateHierarchy);
			Set<Node> hierarchyParents = hierarchy.getParents(node);
			if( hierarchyParents != null )
			for( Node parent : hierarchyParents ) {
				String parentLabel = parent.getLabel();
				multiWordsString = Utility.smartConcat(multiWordsString, parentLabel );
			}
		}
		
		if( node.getOntologyID() == targetOntology.getID() && mp.targetAlternateHierarchy != null ) {
			NodeHierarchy hierarchy = targetOntology.getHierarchy(mp.targetAlternateHierarchy);
			Set<Node> hierarchyParents = hierarchy.getParents(node);
			if( hierarchyParents != null )
			for( Node parent : hierarchyParents ) {
				String parentLabel = parent.getLabel();
				multiWordsString = Utility.smartConcat(multiWordsString, parentLabel );
			}
		}
		
		//Catia: for alternate hierarchy children
		if( node.getOntologyID() == sourceOntology.getID() && mp.sourceAlternateHierarchy != null && mp.sourceAlternateChildren) {
			NodeHierarchy hierarchy = sourceOntology.getHierarchy(mp.sourceAlternateHierarchy);
			Set<Node> hierarchyChildren = hierarchy.getChildren(node);
			if( hierarchyChildren != null )
			for( Node child : hierarchyChildren ) {
				String childLabel = child.getLabel();
				multiWordsString = Utility.smartConcat(multiWordsString, childLabel );
			}
		}
		
		if( node.getOntologyID() == targetOntology.getID() && mp.targetAlternateHierarchy != null && mp.targetAlternateChildren) {
			NodeHierarchy hierarchy = targetOntology.getHierarchy(mp.targetAlternateHierarchy);
			Set<Node> hierarchyChildren = hierarchy.getChildren(node);
			if( hierarchyChildren != null )
			for( Node child : hierarchyChildren ) {
				String childLabel = child.getLabel();
				multiWordsString = Utility.smartConcat(multiWordsString, childLabel );
			}
		}
		
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
		MultiWordsParameters mp = (MultiWordsParameters)param;
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
	
	@Override
	public Mapping alignTwoNodes(Node source, Node target,alignType typeOfNodes, SimilarityMatrix matrix) {
		MultiWordsParameters mp = (MultiWordsParameters)param;
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
		if(mp.measure.equals(MultiWordsParameters.COSINE)) {
			CosineSimilarity measure = new CosineSimilarity();
			sim = measure.getSimilarity(sourceString, targetString);
		}
		else 	if(mp.measure.equals(MultiWordsParameters.JACCARD)) {
			JaccardSimilarity measure = new JaccardSimilarity();
			sim = measure.getSimilarity(sourceString, targetString); 
		}
		else 	if(mp.measure.equals(MultiWordsParameters.EUCLIDEAN)) {
			EuclideanDistance measure = new EuclideanDistance();
			sim = measure.getSimilarity(sourceString, targetString); 
		}
		else 	if(mp.measure.equals(MultiWordsParameters.DICE)) {
			DiceSimilarity measure = new DiceSimilarity();
			sim = measure.getSimilarity(sourceString, targetString); 
		}
		else 	if(mp.measure.equals(MultiWordsParameters.TFIDF)) {
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
			provenanceString+="similarity metric used: "+((MultiWordsParameters)param).measure+"\n";
			provenanceString+=mWS;
			pmapping.setProvenance(provenanceString);
		}
		return pmapping;
		
	}
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new MultiWordsParametersPanel();
		}
		return parametersPanel;
	}
	
	
//	///Catia TEST

	///Catia TEST
		@Override
		protected Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix) {
			Alignment<Mapping> aset = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());

			//double[][] similarityMatrix = matrix.getCopiedSimilarityMatrix(); //hungarian alg needs a double matrix

			int rows = matrix.getRows();
			int cols = matrix.getColumns();
			ArrayList<Mapping> bestmatchesRows = new ArrayList<Mapping>();
			ArrayList<Mapping> bestmatchesCols = new ArrayList<Mapping>();

			for(int i=0;i<rows;i++){
				Mapping m=matrix.getRowMaxValues(i, 1)[0];
				bestmatchesRows.add(m);
			}
			for(int j=0;j<cols;j++){
				Mapping m=matrix.getColMaxValues(j, 1)[0];
				bestmatchesCols.add(m);
			}

			for(int i=0; i<bestmatchesRows.size();i++){
				for(int j=0; j<bestmatchesCols.size();j++){
					if(	bestmatchesRows.get(i).getEntity1()!=null && bestmatchesRows.get(i).getEntity2()!=null && bestmatchesCols.get(j).getEntity2()!=null && bestmatchesCols.get(j).getEntity1()!=null ){
						if(bestmatchesRows.get(i).equals(bestmatchesCols.get(j))){

							
							Mapping newMap = matrix.get(i, j);
							double newSim=newMap.getSimilarity()*1.2;
							if(newSim>1.0)
								newSim=1.0;
							newMap.setSimilarity(newSim);
							matrix.set(i, j, newMap);
						}

					}
				}
			}

			aset=super.oneToOneMatching(matrix);

			return aset;
		}



	      
}

