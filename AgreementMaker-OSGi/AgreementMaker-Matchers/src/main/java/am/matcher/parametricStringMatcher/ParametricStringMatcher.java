package am.matcher.parametricStringMatcher;

import java.util.ArrayList;
import java.util.List;

import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.subconcept.SynonymTermLexicon;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.similarity.StringSimilarityMeasure;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;

public class ParametricStringMatcher extends AbstractMatcher { 


	/**
	 * 
	 */
	private static final long serialVersionUID = -9164286339430046976L;

	private transient Normalizer normalizer;
	
	private transient Lexicon sourceOntologyLexicon, targetOntologyLexicon;
	private transient Lexicon sourceWordNetLexicon, targetWordNetLexicon; 
	
	private StringSimilarityMeasure ssm;
	
	public ParametricStringMatcher() { super(); }
	public ParametricStringMatcher( ParametricStringParameters p ) { super(p); }
	
	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		needsParam = true;
		
		setName("Parametric String Matcher");
		setCategory(MatcherCategory.SYNTACTIC);
		
		//features
		
		// all the similarity measures do not overlap with eachother, so we can invoke multiple alignTwoNode() methods.
		addFeature(MatcherFeature.THREADED_MODE);
		addFeature(MatcherFeature.THREADED_OVERLAP);
	}
	
	@Override
	public String getDescriptionString() {
		return "Performs a local matching using a String Based technique.\n" +
				"Different concept strings are considered in the process.\n" +
				"The user can select a different weight to each concept string\n" +
				"Strings are preprocessed with cleaning, stemming, stop-words removing, and tokenization techniques.\n" +
				"Users can also select preprocessing preferences.\n" +
				"Different String similarity techniques are available to compare preprocessed strings.\n" +
				"A similarity matrix contains the similarity between each pair (sourceNode, targetNode).\n" +
				"A selection algorithm select valid alignments considering threshold and number of relations per node.\n"; 
	}
	
	
	
	/* *******************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	@Override
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		ParametricStringParameters parameters =(ParametricStringParameters)param;
		
		ssm = parameters.measure.getMeasure();
		
		//prepare the normalizer to preprocess strings
		initializeNormalizer();
		
		if( parameters.useLexicons ) {
			// build all the lexicons if they don't exist.			
			for( MatchingProgressListener mpd : progressDisplays ) mpd.setProgressLabel("Building Ontology Lexicon (1/2)");
			sourceOntologyLexicon = 
					Core.getLexiconStore().getLexicon(
							sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);
			targetOntologyLexicon = 
					Core.getLexiconStore().getLexicon(
							targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);
			
			for( MatchingProgressListener mpd : progressDisplays ) mpd.setProgressLabel("Building WordNet Lexicon (2/2)");
			
			sourceWordNetLexicon = 
					Core.getLexiconStore().getLexicon(
							sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
			targetWordNetLexicon = 
					Core.getLexiconStore().getLexicon(
							targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
			
			for( MatchingProgressListener mpd : progressDisplays ) mpd.setProgressLabel(null);
		}
		
	}

	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */
	
	@Override
	public Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix) {
		
		
		
		ParametricStringParameters parameters  = (ParametricStringParameters)param;
		double sim = 0d; // this must be set
		
		if( parameters.useLexicons ) { // lexicon code
		
/*			double weightOntSyn = parameters.lexOntSynonymWeight;
			double weightOntDef = parameters.lexOntDefinitionWeight;
			
			double weightWNSyn = parameters.lexWNSynonymWeight;
			double weightWNDef = parameters.lexWNDefinitionWeight;*/
			
			// lexicons have been built in beforeAlignOperations();
			
			// get the synsets for these OntResources
			OntResource sourceResource = source.getResource().as(OntResource.class);
			OntResource targetResource = target.getResource().as(OntResource.class);
			
			LexiconSynSet sourceOntSS = sourceOntologyLexicon.getSynSet(sourceResource);
			LexiconSynSet targetOntSS = targetOntologyLexicon.getSynSet(targetResource);
			
			LexiconSynSet sourceWNSS = sourceWordNetLexicon.getSynSet(sourceResource);
			LexiconSynSet targetWNSS = targetWordNetLexicon.getSynSet(targetResource);
			
			
			if( parameters.useBestLexSimilarity ) {
				// we are going to find the best similarity between all the synonyms.
				double maxSimilarity = 0.0;
				
				// compile a list of the source synonyms.
				List<String> sourceSynonymList = new ArrayList<String>();
				if( sourceOntSS != null && sourceOntSS.getSynonyms() != null ) sourceSynonymList.addAll( sourceOntSS.getSynonyms() ); // add all the ont synonyms for the source concept.
				if( sourceWNSS != null && sourceWNSS.getSynonyms() != null ) sourceSynonymList.addAll(sourceWNSS.getSynonyms() ); // add all the WordNet synonyms for the source concept.
				
				if( parameters.lexExtendSynonyms && sourceOntologyLexicon instanceof SynonymTermLexicon ) {
					// we are using extended synonyms.
					SynonymTermLexicon sourceSCSLexicon = (SynonymTermLexicon) sourceOntologyLexicon; 
					sourceSynonymList.addAll( sourceSCSLexicon.extendSynSet(sourceOntSS) );
				}
				
				
				// now, compile a list of the target synonyms.
				List<String> targetSynonymList = new ArrayList<String>();
				if( targetOntSS != null && targetOntSS.getSynonyms() != null ) targetSynonymList.addAll( targetOntSS.getSynonyms() ); // ont synonyms for target concept.
				if( targetWNSS != null && targetWNSS.getSynonyms() != null ) targetSynonymList.addAll( targetWNSS.getSynonyms() ); // wordnet synonyms for target concept.
				
				if( parameters.lexExtendSynonyms && targetOntologyLexicon instanceof SynonymTermLexicon ) {
					// using extended synonyms for the target
					SynonymTermLexicon targetSCSLexicon = (SynonymTermLexicon) targetOntologyLexicon;
					sourceSynonymList.addAll( targetSCSLexicon.extendSynSet(targetOntSS) );
				}
				
				
				// now, start comparing synonyms.
				for( String sourceSynonym : sourceSynonymList ) {
					for( String targetSynonym : targetSynonymList ) {
						double currentSynonymPairSimilarity = performStringSimilarity(sourceSynonym, targetSynonym);
						if( currentSynonymPairSimilarity > maxSimilarity ){ 
							maxSimilarity = currentSynonymPairSimilarity;
						}
					}
				}
				
				sim = maxSimilarity; // this is the best similarity
				
			} else {
				// we are using a weighted similarity (one weight for each lexicon)
				double ontSynWeight = parameters.lexOntSynonymWeight;
				double wnSynWeight = parameters.lexWNSynonymWeight;
				
				
				/********************************** Working with the ontology Lexicon ******************************/
				double maxOntSynSimilarity = 0.0d;
				
				if( ontSynWeight != 0.0d ) { // calculate only if the weight is not zero
				
					// 1. Compile the list of source and target synonyms.
					List<String> sourceOntSynonymList = new ArrayList<String>();
					if( sourceOntSS != null && sourceOntSS.getSynonyms() != null ) sourceOntSynonymList.addAll( sourceOntSS.getSynonyms() );
					if( parameters.lexExtendSynonyms && sourceOntologyLexicon instanceof SynonymTermLexicon ) {
						SynonymTermLexicon sourceSCSLexicon = (SynonymTermLexicon) sourceOntologyLexicon; 
						sourceOntSynonymList.addAll( sourceSCSLexicon.extendSynSet(sourceOntSS) );
					}
					
					List<String> targetOntSynonymList = new ArrayList<String>();
					if( targetOntSS != null && targetOntSS.getSynonyms() != null ) targetOntSynonymList.addAll( targetOntSS.getSynonyms() );
					if( parameters.lexExtendSynonyms && targetOntologyLexicon instanceof SynonymTermLexicon ) {
						SynonymTermLexicon targetSCSLexicon = (SynonymTermLexicon) targetOntologyLexicon; 
						targetOntSynonymList.addAll( targetSCSLexicon.extendSynSet(sourceOntSS) );
					}
					
					// calculate max similarity
					for( String sourceOntSynonym : sourceOntSynonymList ) {
						for( String targetOntSynonym : targetOntSynonymList ) {
							double currentOntSynonymPairSimilarity = performStringSimilarity(sourceOntSynonym, targetOntSynonym);
							if( currentOntSynonymPairSimilarity > maxOntSynSimilarity ) {
								maxOntSynSimilarity = currentOntSynonymPairSimilarity;
							}
						}
					}
				}
				
				
				/*********************************** Working with the WN Lexicon *********************************/
				double maxWNSynSimilarity = 0.0d;
				
				if( wnSynWeight != 0.0 ) { // calculate only if the weight is not zero
					// Compile a list of the source and target synonym.
					List<String> sourceWNSynonymList = new ArrayList<String>();
					if( sourceWNSS != null && sourceWNSS.getSynonyms() != null ) sourceWNSynonymList.addAll( sourceWNSS.getSynonyms() );
					List<String> targetWNSynonymList = new ArrayList<String>();
					if( targetWNSS != null && targetWNSS.getSynonyms() != null ) targetWNSynonymList.addAll( targetWNSS.getSynonyms() );
					
					// calculate max similarity
					for( String sourceWNSynonym : sourceWNSynonymList ) {
						for( String targetWNSynonym : targetWNSynonymList ) {
							double currentWNSynonymPairSimilarity = performStringSimilarity(sourceWNSynonym, targetWNSynonym);
							if( currentWNSynonymPairSimilarity > maxWNSynSimilarity ) {
								maxWNSynSimilarity = currentWNSynonymPairSimilarity;
							}
						}
					}
				}
				
				if( parameters.redistributeWeights ) {
					// redistributing the weights for things that have not been found.
					if( maxOntSynSimilarity == 0.0d ) {
						ontSynWeight = 0.0;
					}
					if( maxWNSynSimilarity == 0.0d ) {
						wnSynWeight = 0.0;
					}
				}
				
				
				double totalWeight = ontSynWeight + wnSynWeight;
				
				// applying weights
				double ontSim = ontSynWeight * maxOntSynSimilarity;
				double wnSim  = wnSynWeight * maxWNSynSimilarity;
				
				sim = (ontSim + wnSim) / totalWeight;
				
			}
			
			

			
			
		} else { // string similarity code based on ontology elements
			double localSim = 0;
			double labelSim = 0;
			double commentSim = 0;
			double seeAlsoSim = 0;
			double isDefBySim = 0;
			
			
			//i need to use local varables for weights  to modify them in case of weights redistribution but without modifying global parameters
			double localWeight = parameters.localWeight;
			double labelWeight = parameters.labelWeight;
			double commentWeight = parameters.commentWeight; 
			double seeAlsoWeight = parameters.seeAlsoWeight; 
			double isDefinedByWeight = parameters.isDefinedByWeight;
			
			//The redistrubution is implicit in the weighted average mathematical formula
			//i just need to put the weight equal to 0
			//but labels should be first redistributed to localnames and vice-versa
			//we set the weight of a feature to 0 if any of the two value is irrelevant
			if(parameters.redistributeWeights) {
				if(parameters.localWeight!=0){
					if(Utility.isIrrelevant(source.getLocalName()) || Utility.isIrrelevant(target.getLocalName())){
						//we should redistribute localname to label if label is relevant
						if(!Utility.isIrrelevant(source.getLabel()) && !Utility.isIrrelevant(target.getLabel())){
							labelWeight += localWeight;
						}
						localWeight = 0;
					}	
				}
				
				if(parameters.labelWeight!=0){
					if(Utility.isIrrelevant(source.getLabel()) || Utility.isIrrelevant(target.getLabel())){
						//we should redistribute label to localname if localname is relevant
						if(!Utility.isIrrelevant(source.getLocalName()) && !Utility.isIrrelevant(target.getLocalName())){
							localWeight+= labelWeight;
						}
						labelWeight = 0;
					}
				}
	
				if(parameters.commentWeight == 0 || Utility.isIrrelevant(source.getComment()) || Utility.isIrrelevant(target.getComment()))
					commentWeight = 0;
				if(parameters.seeAlsoWeight == 0 || Utility.isIrrelevant(source.getSeeAlsoLabel()) || Utility.isIrrelevant(target.getSeeAlsoLabel()))
					seeAlsoWeight = 0;
				if(parameters.isDefinedByWeight == 0 || Utility.isIrrelevant(source.getIsDefinedByLabel()) || Utility.isIrrelevant(target.getIsDefinedByLabel()))
					isDefinedByWeight = 0;
				
				
			}
			
			double totWeight = localWeight + labelWeight + commentWeight + seeAlsoWeight + isDefinedByWeight; //important to get total after the redistribution
			if(totWeight > 0) {
				if(localWeight > 0) {
					localSim =  performStringSimilarity(source.getLocalName(), target.getLocalName());
					localSim *= localWeight;
				}
				if(labelWeight > 0) {
					labelSim =  performStringSimilarity(source.getLabel(), target.getLabel());
					labelSim *= labelWeight;
				}
				if(commentWeight > 0) {
					commentSim = performStringSimilarity(source.getComment(), target.getComment());
					commentSim *= commentWeight;
				}
				if(seeAlsoWeight > 0) {
					seeAlsoSim = performStringSimilarity(source.getSeeAlsoLabel(), target.getSeeAlsoLabel());
					seeAlsoSim *= seeAlsoWeight;
				}
				if(isDefinedByWeight > 0) {
					isDefBySim = performStringSimilarity(source.getIsDefinedByLabel(), target.getIsDefinedByLabel());
					isDefBySim *= isDefinedByWeight;
				}
				//if( )
				
				sim = localSim + labelSim + commentSim + seeAlsoSim + isDefBySim;
				//Weighted average, this normalize everything so also if the sum of  weights is not one, the value is always between 0 and 1. 
				//this also automatically redistribute 0 weights.
				sim /= totWeight; 
			}
				
		}

		if( sim > 0d ) {
			return new Mapping(source, target, sim);
		}
		else {
			return null; // no similarity was found
		}
	}
	
	
	public double performStringSimilarity(String sourceString, String targetString) {
		
		if(sourceString == null || targetString == null )
			return 0; //this should never happen because we set string to empty string always
			
		//PREPROCESSING
		if( ((ParametricStringParameters)param).useNormalizer ) {
			sourceString = normalizer.normalize(sourceString);
			targetString = normalizer.normalize(targetString);
		}

		//usually empty strings shouldn't be compared, but if redistrubute weights is not selected 
		//in the redistribute weights case this can't happen because the code won't arrive till here
		if(sourceString.equals("")) 
			if(targetString.equals(""))
				return 1;
			else return 0;
		else if(targetString.equals(""))
			return 0;

		return ssm.getSimilarity(sourceString, targetString);
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new ParametricStringParametersPanel();
		}
		return parametersPanel;
	}

	public void initializeNormalizer() {
		normalizer = new Normalizer( (( ParametricStringParameters)param).normParameter );
	}
	
	public static void main(String[] args) throws Exception {
		testStrings("aim", "target");
	}
	
	public static void testStrings(String s1, String s2) throws Exception {
		Node source = new AMNode((Resource)null, 0, s1, "owl-propertynode", 0);
		Node target = new AMNode((Resource)null, 1, s2, "owl-propertynode", 0);

		

		ParametricStringMatcher p= new ParametricStringMatcher();
		Mapping mapping = p.alignTwoNodes(source, target,
				alignType.aligningProperties,null);
		System.out.println(mapping);

	}
}

