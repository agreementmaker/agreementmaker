package am.app.mappingEngine.multiWords;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.wcohen.ss.api.StringWrapper;
import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
import am.app.lexicon.wordnet.WordNetLexiconBuilder;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.StringUtil.AMStringWrapper;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.ontology.Node;
import am.userInterface.vertex.Vertex;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*; //all sim metrics are in here
import simpack.measure.weightingscheme.StringTFIDF;

public class MultiWordsMatcher extends AbstractMatcher { 

	// Logger
	private static Logger log = Logger.getLogger(MultiWordsMatcher.class);
	

	private Normalizer normalizer;
	private ArrayList<String> sourceClassDocuments = new ArrayList<String>();
	private ArrayList<String> targetClassDocuments = new ArrayList<String>();
	private ArrayList<String> sourcePropDocuments = new ArrayList<String>();
	private ArrayList<String> targetPropDocuments = new ArrayList<String>();
	
	private ArrayList<StringWrapper> classCorpus = new ArrayList<StringWrapper>();
	private ArrayList<StringWrapper> propCorpus = new ArrayList<StringWrapper>();
	
	private StringTFIDF tfidfClasses;
	private StringTFIDF tfidfProperties;
	
	// Lexicons
	private Lexicon sourceOntologyLexicon, targetOntologyLexicon;
	private Lexicon sourceWordNetLexicon, targetWordNetLexicon; 
	
	public MultiWordsMatcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
	}
	
	public MultiWordsMatcher( MultiWordsParameters param_new ) {
		super(param_new);
		
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
			// build all the lexicons if they don't exist.  TODO: Move this to the LexiconStore.
			sourceOntologyLexicon = Core.getLexiconStore().getSourceOntLexicon(sourceOntology);			
			targetOntologyLexicon = Core.getLexiconStore().getTargetOntLexicon(targetOntology);			
			sourceWordNetLexicon = Core.getLexiconStore().getSourceWNLexicon(sourceOntology, sourceOntologyLexicon);
			targetWordNetLexicon = Core.getLexiconStore().getTargetWNLexicon(targetOntology, targetOntologyLexicon);
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
		
		String multiWordsString = "";
		MultiWordsParameters mp = (MultiWordsParameters)param;
		
		//Add concept strings to the multiwordsstring
		if(mp.considerConcept) {
			multiWordsString = Utility.smartConcat(multiWordsString, getLabelAndOrNameString(node));
			multiWordsString = Utility.smartConcat(multiWordsString, node.getComment());
			multiWordsString = Utility.smartConcat(multiWordsString, node.getSeeAlsoLabel());
			multiWordsString = Utility.smartConcat(multiWordsString, node.getIsDefinedByLabel());
		}

		//add neighbors strings
		if(mp.considerNeighbors) {
			ArrayList<Vertex> duplicateList = node.getVertexList();
			//add child strings
			Vertex mainVertex = duplicateList.get(0);
			String childstring = "";
			Enumeration<Vertex> children = mainVertex.children();
			while(children.hasMoreElements()) {
				Vertex childVertex = (Vertex) children.nextElement();
				Node childNode = childVertex.getNode();
				String neighbourString = getLabelAndOrNameString(childNode);
				childstring = Utility.smartConcat(childstring, neighbourString);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, childstring);
			
			//for each father add father strings and create hashSet of siblings
			String parentsString = "";
			HashSet<Node> siblingNodes = new HashSet<Node>();
			
			for(int i = 0; i < duplicateList.size(); i++) {

				Vertex duplicateVertex = duplicateList.get(i);
				Vertex parentVertex = (Vertex)duplicateVertex.getParent();
				if(parentVertex!= null && !parentVertex.isFake()) {
					Node parentNode = parentVertex.getNode();
					String neighbourString = getLabelAndOrNameString(parentNode);;
					parentsString = Utility.smartConcat(parentsString, neighbourString);
					//create hashSet
					Enumeration<Vertex> siblings = parentVertex.children();
					while(siblings.hasMoreElements()) {
						Vertex sibVertex = (Vertex) siblings.nextElement();
						Node sibNode = sibVertex.getNode();
						if(!sibNode.equals(node))//aggiungo tutti i fratelli tranne me, i duplicati non vengono aggiunti perche � un hashset
							siblingNodes.add(sibNode);
					}
				}
				
			}
			multiWordsString = Utility.smartConcat(multiWordsString, parentsString);
			
			//add sibling string from the hashSet, i need to use hashset to avoid adding duplicates.
			Iterator<Node> it = siblingNodes.iterator();
			String siblingsString = "";
			while(it.hasNext()) {
				Node sibNode = it.next();
				String neighbourString = getLabelAndOrNameString(sibNode);
				siblingsString = Utility.smartConcat(siblingsString, neighbourString);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, siblingsString);
		}
		
		//add instances strings
		if(mp.considerInstances && typeOfNodes == alignType.aligningClasses) {
			String instancesString = "";
			Iterator<String> it = node.getIndividuals().iterator();
			while(it.hasNext()) {
				String ind = it.next();
				instancesString = Utility.smartConcat(instancesString, ind);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, instancesString);
		}
		
		//add properties declared by this class or classes declaring this properties
		if(mp.considerProperties && typeOfNodes == alignType.aligningClasses) {
			String propString = "";
			Iterator<String> it = node.getpropOrClassNeighbours().iterator();
			while(it.hasNext()) {
				String s = it.next();
				propString = Utility.smartConcat(propString, s);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, propString);
		}
			
	    //add classes declaring this properties
		if(mp.considerClasses && typeOfNodes == alignType.aligningProperties) {
			String classString = "";
			Iterator<String> it = node.getpropOrClassNeighbours().iterator();
			while(it.hasNext()) {
				String s = it.next();
				classString = Utility.smartConcat(classString, s);
			}
			multiWordsString = Utility.smartConcat(multiWordsString, classString);
		}
		
		// lexicons
		if( mp.useLexiconDefinitions ) {
			String definitions = new String();
			OntResource nodeResource = node.getResource().as(OntResource.class);
			
			if( node.getOntologyIndex() == sourceOntology.getIndex() ) {
				// look up the definition in the source lexicons
				LexiconSynSet sourceOntSS = sourceOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet sourceWNSS = sourceWordNetLexicon.getSynSet(nodeResource);
				if( sourceOntSS != null ) definitions = Utility.smartConcat(definitions, sourceOntSS.getGloss());
				if( sourceWNSS != null ) definitions = Utility.smartConcat(definitions, sourceWNSS.getGloss());
			} else if( node.getOntologyIndex() == targetOntology.getIndex() ) {
				// look up the definition in the target lexicons
				LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet targetWNSS = targetWordNetLexicon.getSynSet(nodeResource);
				if( targetOntSS != null ) definitions = Utility.smartConcat(definitions, targetOntSS.getGloss());
				if( targetWNSS != null ) definitions = Utility.smartConcat(definitions, targetWNSS.getGloss());
			} else {
				throw new Exception("Cannot find which ontology the node belongs to.");
			}
			
			if( !definitions.equals("") ) multiWordsString = Utility.smartConcat(multiWordsString, definitions);
			
		}
		
		if( mp.useLexiconSynonyms ) {
			String synonyms = new String();
			OntResource nodeResource = node.getResource().as(OntResource.class);
			
			if( node.getOntologyIndex() == sourceOntology.getIndex() ) {
				// look up the definition in the source lexicons
				LexiconSynSet sourceOntSS = sourceOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet sourceWNSS = sourceWordNetLexicon.getSynSet(nodeResource);
				String ss = makeSynonymsString(sourceOntSS, sourceWNSS); 
				if( !ss.isEmpty() ) synonyms = Utility.smartConcat(synonyms, ss);
			} else if( node.getOntologyIndex() == targetOntology.getIndex() ) {
				// look up the definition in the target lexicons
				LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(nodeResource);
				LexiconSynSet targetWNSS = targetWordNetLexicon.getSynSet(nodeResource);
				String ss = makeSynonymsString(targetOntSS, targetWNSS); 
				if( !ss.isEmpty() ) synonyms = Utility.smartConcat(synonyms, ss);
			} else {
				throw new Exception("Cannot find which ontology the node belongs to.");
			}
			
			if( !synonyms.isEmpty() ) multiWordsString = Utility.smartConcat(multiWordsString, synonyms);
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

	public Alignment alignTwoNodes(Node source, Node target,alignType typeOfNodes) {
		MultiWordsParameters mp = (MultiWordsParameters)param;
		double sim = 0;
		
		String sourceString;
		String targetString;
		 if(typeOfNodes == alignType.aligningClasses) {
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
		
		return new Alignment(source, target, sim);
		
	}
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new MultiWordsParametersPanel();
		}
		return parametersPanel;
	}
	
	
	
	
	



	      
}

