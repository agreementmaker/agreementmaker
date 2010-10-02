package am.app.mappingEngine.parametricStringMatcher;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;

import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconSynSet;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
import am.app.lexicon.wordnet.WordNetLexiconBuilder;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.StringUtil.ISub;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.ontology.Node;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*; //all sim metrics are in here

public class ParametricStringMatcher extends AbstractMatcher { 


	private Normalizer normalizer;
	
	private Lexicon sourceOntologyLexicon, targetOntologyLexicon;
	private Lexicon sourceWordNetLexicon, targetWordNetLexicon; 
	
	public ParametricStringMatcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
	}
	
	public ParametricStringMatcher( ParametricStringParameters param_new ) {
		super(param_new);
	}
	
	
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
	
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		ParametricStringParameters parameters =(ParametricStringParameters)param;
		//prepare the normalizer to preprocess strings
		normalizer = new Normalizer(parameters.normParameter);
		
		if( parameters.useLexicons ) {
			// build all the lexicons if they don't exist.  TODO: Move this to the LexiconStore.
			sourceOntologyLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON );
			
			if( sourceOntologyLexicon == null ) {
				if( Core.DEBUG_PSM ) System.out.println("Building source ontology lexicon...");
				OntModel sourceModel = sourceOntology.getModel();
				
				// the synonym property, label property and definition property
				Property sourceSynonymProperty = sourceModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym");
				Property sourceLabelProperty = sourceModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
				Property sourceDefinitionProperty = sourceModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasDefinition");
				
				OntologyLexiconBuilder sourceOLB = new OntologyLexiconBuilder(sourceOntology, sourceLabelProperty, sourceSynonymProperty, sourceDefinitionProperty);
				
				sourceOntologyLexicon = sourceOLB.buildLexicon();
				sourceOntologyLexicon.settOntologyID(sourceOntology.getID());
				
				Core.getLexiconStore().registerLexicon(sourceOntologyLexicon); // register for reuse by other matchers
			}
			
			targetOntologyLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);
			
			if( targetOntologyLexicon == null ) {
				if( Core.DEBUG_PSM ) System.out.println("Building target ontology lexicon...");
				OntModel targetModel = targetOntology.getModel();
		
				// the synonym property, label property and definition property
				Property targetSynonymProperty = targetModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym");	
				Property targetLabelProperty = targetModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
				Property targetDefinitionProperty = targetModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasDefinition");
				
				// STEP 1: Let's build the lexicons for the ontology
				OntologyLexiconBuilder targetOLB = new OntologyLexiconBuilder(targetOntology, targetLabelProperty, targetSynonymProperty, targetDefinitionProperty);
	
				targetOntologyLexicon = targetOLB.buildLexicon();
				targetOntologyLexicon.settOntologyID(targetOntology.getID());
				
				Core.getLexiconStore().registerLexicon(targetOntologyLexicon);
			}
			
			sourceWordNetLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
			
			if( sourceWordNetLexicon == null ) { // no lexicon available we must build it
				if( Core.DEBUG_PSM ) System.out.println("Building source WordNet lexicon...");
				// STEP 1: Let's build the lexicons for the ontology
				WordNetLexiconBuilder sourceOLB = new WordNetLexiconBuilder(sourceOntology, sourceOntologyLexicon);
	
				sourceWordNetLexicon = sourceOLB.buildLexicon();
				sourceWordNetLexicon.settOntologyID(sourceOntology.getID());
				
				Core.getLexiconStore().registerLexicon(sourceOntologyLexicon);
			}
			
			targetWordNetLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
			if( targetWordNetLexicon == null ) {
				if( Core.DEBUG_PSM ) System.out.println("Building target WordNet lexicon...");
				WordNetLexiconBuilder targetOLB = new WordNetLexiconBuilder(targetOntology, targetOntologyLexicon);
				
				targetWordNetLexicon = targetOLB.buildLexicon();
				targetWordNetLexicon.settOntologyID( targetOntology.getID() );
				
				Core.getLexiconStore().registerLexicon(targetWordNetLexicon);
			}
		}
		
	}

	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */

	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		
		ParametricStringParameters parameters  = (ParametricStringParameters)param;
		double sim = 0.0d; // this must be set
		
		if( parameters.useLexicons ) { // lexicon code
		
			double weightOntSyn = parameters.lexOntSynonymWeight;
			double weightOntDef = parameters.lexOntDefinitionWeight;
			
			double weightWNSyn = parameters.lexWNSynonymWeight;
			double weightWNDef = parameters.lexWNDefinitionWeight;
			
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
				
				// now, compile a list of the target synonyms.
				List<String> targetSynonymList = new ArrayList<String>();
				if( targetOntSS != null && targetOntSS.getSynonyms() != null ) targetSynonymList.addAll( targetOntSS.getSynonyms() ); // ont synonyms for target concept.
				if( targetWNSS != null && targetWNSS.getSynonyms() != null ) targetSynonymList.addAll( targetWNSS.getSynonyms() ); // wordnet synonyms for target concept.
				
				
				// now, start comparing synonyms.
				for( String sourceSynonym : sourceSynonymList ) {
					for( String targetSynonym : targetSynonymList ) {
						double currentSynonymPairSimilarity = performStringSimilarity(sourceSynonym, targetSynonym);
						if( currentSynonymPairSimilarity > maxSimilarity ) maxSimilarity = currentSynonymPairSimilarity;
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
					List<String> targetOntSynonymList = new ArrayList<String>();
					if( targetOntSS != null && targetOntSS.getSynonyms() != null ) targetOntSynonymList.addAll( targetOntSS.getSynonyms() );
					
					// calculate max similarity
					for( String sourceOntSynonym : sourceOntSynonymList ) {
						for( String targetOntSynonym : targetOntSynonymList ) {
							double currentOntSynonymPairSimilarity = performStringSimilarity(sourceOntSynonym, targetOntSynonym);
							if( currentOntSynonymPairSimilarity > maxOntSynSimilarity ) maxOntSynSimilarity = currentOntSynonymPairSimilarity;
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
							if( currentWNSynonymPairSimilarity > maxWNSynSimilarity ) maxWNSynSimilarity = currentWNSynonymPairSimilarity;
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

		if( sim > 0.0d ) return new Alignment(source, target, sim);
		return null; // no similarity was found
	}
	
	
	public double performStringSimilarity(String sourceString, String targetString) {

		double sim = 0;
		if(sourceString == null || targetString == null )
			return 0; //this should never happen because we set string to empty string always
		
		else { //real string comparison
			ParametricStringParameters parameters  = (ParametricStringParameters)param;
			
			//PREPROCESSING
			String processedSource = normalizer.normalize(sourceString);
			String processedTarget = normalizer.normalize(targetString);
			
			//usually empty strings shouldn't be compared, but if redistrubute weights is not selected 
			//in the redistribute weights case this can't happen because the code won't arrive till here
			if(processedSource.equals("")) 
				if(processedTarget.equals(""))
					return 1;
				else return 0;
			else if(processedTarget.equals(""))
				return 0;
			
			//this could be done with registry enumeration techinque but is not worth it
			if(parameters.measure.equals(ParametricStringParameters.AMSUB)) {
				sim = StringMetrics.AMsubstringScore(processedSource,processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.AMSUB_AND_EDIT)) {
				Levenshtein lv = new Levenshtein();
				double lsim = lv.getSimilarity(processedSource, processedTarget);
				double AMsim = StringMetrics.AMsubstringScore(processedSource,processedTarget);
				sim = (0.65*AMsim)+(0.35*lsim); 
			}
			else if(parameters.measure.equals(ParametricStringParameters.EDIT)) {
				Levenshtein lv = new Levenshtein();
				sim = lv.getSimilarity(processedSource, processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.JARO)) {
				JaroWinkler jv = new JaroWinkler();
				sim =jv.getSimilarity(processedSource, processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.QGRAM)) {
				QGramsDistance q = new QGramsDistance();
				sim = q.getSimilarity(processedSource, processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.SUB)) {
				sim = StringMetrics.substringScore(processedSource,processedTarget);
			}
			else if(parameters.measure.equals(ParametricStringParameters.ISUB)) {
				sim = ISub.getSimilarity(processedSource,processedTarget);
			}
		}
		return sim;
	}
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new ParametricStringParametersPanel();
		}
		return parametersPanel;
	}
	      
}

