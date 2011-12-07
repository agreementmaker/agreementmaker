/**
 * 
 */
package am.app.mappingEngine.oaei.oaei2011;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import am.Utility;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralParameters;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.mappingEngine.boosting.BestMatchBoostingParameters;
import am.app.mappingEngine.mediatingMatcher.MediatingMatcherParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters.OAEI2011Configuration;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.ontology.Node;
import am.app.ontology.NodeHierarchy;
import am.app.ontology.Ontology;
import am.app.ontology.hierarchy.AlternateHierarchy;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.ontology.profiling.classification.OntologyClassificator;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;
import am.userInterface.MatchingProgressDisplay;

import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * The matching algorithm for OAEI 2011.
 * 
 * New features included in this year's matching algorithm:
 * 
 * 
 */
public class OAEI2011Matcher extends AbstractMatcher {
	
	private static final long serialVersionUID = -2258529392257305604L;
	
	//This should be false in batch mode & using learning matcher / true for alignment evaluation

	public OAEI2011Matcher(){
		super();
		needsParam = true;
	}
	
	@Override
	public String getDescriptionString() {
		return "The method adopted in the OAEI 2011 competition.  This algorithm chooses a matcher configuration automatically.";
	}
	
	
	public enum SubMatcherID {
		BSM, PSM, VMM, LSM,	MM,	IISM, GFM, LWC1, LWC2, LWC3
	}
	
	private Map<SubMatcherID, AbstractMatcher> matchersByID = new HashMap<SubMatcherID, AbstractMatcher>();
	
	/** *****************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	@Override
	public void match() throws Exception {
    	matchStart();

    	sourceOntology.setSourceOrTarget( Ontology.SOURCE );
    	targetOntology.setSourceOrTarget( Ontology.TARGET );
    	
    	OAEI2011MatcherParameters p = (OAEI2011MatcherParameters) param;
    	
    	for( MatchingProgressDisplay mpd : progressDisplays ) mpd.ignoreComplete(true);
    	
    	AbstractMatcher finalResult = null;
    	if( p.automaticConfiguration ) {
    		finalResult = automaticConfiguration();
    	}
    	else {
    		switch( p.selectedConfiguration ) {
    		case LARGE_LEXICAL: {
    			finalResult = runLexicalBased();
    		}
    		break;
    		case GENERAL_PURPOSE: {
    			finalResult = runGeneralPurpose();
    		}
    		break;
    		case GENERAL_MULTI: {
    			finalResult = runMultiOntologyBased();
    		}
    		break;
    		case GENERAL_PURPOSE_ADVANCED: {
    			finalResult = runGeneralPurposeAdvanced();
    		}
    		break;
    		case LARGE_LEXICAL_WITH_LOCALNAMES: {
    			finalResult = runLexicalBasedWithLocalnames();
    		}
    		break;
    		}
    	}
		
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.ignoreComplete(false); 
    	
		if( finalResult != null ) {
			//finalResult.select();
			classesMatrix = finalResult.getClassesMatrix();
			propertiesMatrix = finalResult.getPropertiesMatrix();
			classesAlignmentSet = finalResult.getClassAlignmentSet();
			propertiesAlignmentSet = finalResult.getPropertyAlignmentSet();
			finalResult = null;
		}
		
    	matchEnd();
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
	}
	
	private AbstractMatcher automaticConfiguration() throws Exception {
		AbstractMatcher finalResult = null;
		OAEI2011Configuration conf = OntologyClassificator.classifiedOntologiesOAEI2011(sourceOntology, targetOntology);
			
		switch( conf ) {
		case LARGE_LEXICAL: {
			finalResult = runLexicalBased();
		}
		break;
		case GENERAL_PURPOSE: {
			finalResult = runGeneralPurpose();
		}
		break;
		case GENERAL_MULTI: {
			finalResult = runMultiOntologyBased();
		}
		break;
		case GENERAL_PURPOSE_ADVANCED: {
			finalResult = runGeneralPurposeAdvanced();
		}
		break;
		case LARGE_LEXICAL_WITH_LOCALNAMES: {
			finalResult = runLexicalBasedWithLocalnames();
		}
		break;
		default:{
			finalResult = runGeneralPurpose();
		}
		break;
		}
		
		
		return finalResult;
		//throw new Exception("Automatic configuration not implemented.");
	}
	
	private AbstractMatcher runGeneralPurpose() throws Exception {
		
		// Build the lexicons.
		LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
		lexParam.sourceOntology = sourceOntology;
		lexParam.targetOntology = targetOntology;
		
		lexParam.sourceUseLocalname = true;
		lexParam.targetUseLocalname = true;
		lexParam.sourceUseSCSLexicon = false;
		lexParam.targetUseSCSLexicon = false;
		
		lexParam.detectStandardProperties(sourceOntology);
		lexParam.detectStandardProperties(targetOntology);
		
		Core.getLexiconStore().buildAll(lexParam);
		
		// Ontology profiling
		ProfilerRegistry entry = ProfilerRegistry.ManualProfiler;
		OntologyProfiler profiler = null;
		Constructor<? extends OntologyProfiler> constructor = null;
			
		constructor = entry.getProfilerClass().getConstructor(Ontology.class, Ontology.class);
		//profiler = constructor.newInstance(Core.getInstance().getSourceOntology(), Core.getInstance().getTargetOntology());
		profiler = constructor.newInstance(sourceOntology, targetOntology);
		
		
		if(profiler!=null) {
			profiler.setName(entry);
			Core.getInstance().setOntologyProfiler(profiler);
		}
		
		ManualOntologyProfiler manualProfiler = (ManualOntologyProfiler) profiler;
		
		ManualProfilerMatchingParameters profilingMatchingParams = new ManualProfilerMatchingParameters();
		
		profilingMatchingParams.matchSourceClassLocalname = true;
		profilingMatchingParams.matchSourcePropertyLocalname = true;
		
		profilingMatchingParams.matchTargetClassLocalname = true;
		profilingMatchingParams.matchTargetPropertyLocalname = true;
		
		profilingMatchingParams.sourceClassAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getSourceClassAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.sourceClassAnnotations.add(currentProperty);
			}
		}
		
		profilingMatchingParams.sourcePropertyAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getSourcePropertyAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.sourcePropertyAnnotations.add(currentProperty);
			}
		}
		
		profilingMatchingParams.targetClassAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getTargetClassAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.targetClassAnnotations.add(currentProperty);
			}
		}
		
		profilingMatchingParams.targetPropertyAnnotations = new ArrayList<Property>();
		for( Property currentProperty : manualProfiler.getTargetPropertyAnnotations() ) {
			if( currentProperty.getLocalName().toLowerCase().contains("label") ) {
				profilingMatchingParams.targetPropertyAnnotations.add(currentProperty);
			}
		}
		
		manualProfiler.setMatchTimeParams(profilingMatchingParams);
		
		// BSM
		List<AbstractMatcher> lwcInputMatchers = new ArrayList<AbstractMatcher>();
		
		if( !isCancelled() ) {
			AbstractMatcher bsm = MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity, 0);
			
			BaseSimilarityParameters bsmParam = new BaseSimilarityParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			bsmParam.useDictionary = false;
			
			setupSubMatcher(bsm, bsmParam);
			runSubMatcher(bsm, "BSM 1/6");
			
			lwcInputMatchers.add(bsm);			
		}
		
		// PSM
		if( !isCancelled() ) {
			AbstractMatcher psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 0);
			
			ParametricStringParameters psmParam = new ParametricStringParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			psmParam.localWeight = 0.33;
			psmParam.labelWeight = 0.34d;
			psmParam.commentWeight = 0.33d;
			psmParam.seeAlsoWeight = 0.00d;
			psmParam.isDefinedByWeight = 0.00d;
			
			psmParam.useLexicons = false;
			psmParam.useBestLexSimilarity = false;
			psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
			psmParam.normParameter = new NormalizerParameter();
			psmParam.normParameter.setForOAEI2009();
			psmParam.redistributeWeights = true;
			
			setupSubMatcher(psm, psmParam);
			runSubMatcher(psm, "PSM 2/6");
			
			lwcInputMatchers.add(psm);			
		}
		
		// VMM
		if( !isCancelled() ) {
			AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 0);
			
			MultiWordsParameters vmmParam = new MultiWordsParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			vmmParam.measure = MultiWordsParameters.TFIDF;
			//only on concepts right now because it should be weighted differently
			vmmParam.considerInstances = true;
			vmmParam.considerNeighbors = false;
			vmmParam.considerConcept = true;
			vmmParam.considerClasses = false;
			vmmParam.considerProperties = false;
			vmmParam.ignoreLocalNames = true; 
			
			vmmParam.useLexiconSynonyms = true; // May change later.
			
			setupSubMatcher(vmm, vmmParam);
			runSubMatcher(vmm, "VMM 3/6");
			
			lwcInputMatchers.add(vmm);
		}
		
		// LSM
		if( !isCancelled() ) {
			AbstractMatcher lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSM, 0);
			
			LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lsmParam.useSynonymTerms = false;
			
			setupSubMatcher(lsm, lsmParam);
			runSubMatcher(lsm, "LSM 4/6");
			
			lwcInputMatchers.add(lsm);
		}
		
		// LWC
		AbstractMatcher lwc = null;
		if( !isCancelled() ) {
			lwc = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc.setInputMatchers(lwcInputMatchers);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc, lwcParam);
			runSubMatcher(lwc, "LWC 5/6");
			
		}
		
		if( !isCancelled() ) {
			AbstractMatcher iism = MatcherFactory.getMatcherInstance(MatchersRegistry.IISM, 0);
			
			iism.addInputMatcher(lwc);
			
			IterativeInstanceStructuralParameters iismParam = 
					new IterativeInstanceStructuralParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			iismParam.allBoost();
			iismParam.setConsiderIndividuals(true);
			iismParam.setPropertyUsageThreshold(0.6);
			iismParam.setPropertyValuesThreshold(0.5);
			iismParam.setRangeDomainThreshold(0.89);
			iismParam.setSuperclassThreshold(0.6);
			iismParam.setUsePropertyUsage(true);
			iismParam.setUsePropertyValues(true);
			iismParam.setUseRangeDomain(true);
			iismParam.setUseSuperclasses(true);
			
			setupSubMatcher(iism, iismParam);
			runSubMatcher(iism, "IISM 6/6");
			
			return iism;
		}
		
		return null;
	}
	
	private AbstractMatcher runGeneralPurposeAdvanced() throws Exception {
		
		// Build the lexicons.
		LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
		lexParam.sourceOntology = sourceOntology;
		lexParam.targetOntology = targetOntology;
		
		lexParam.sourceUseLocalname = true;
		lexParam.targetUseLocalname = true;
		lexParam.sourceUseSCSLexicon = false;
		lexParam.targetUseSCSLexicon = false;
		
		lexParam.detectStandardProperties(sourceOntology);
		lexParam.detectStandardProperties(targetOntology);
		
		Core.getLexiconStore().buildAll(lexParam);
		
		// ASM
		List<AbstractMatcher> lwcInputMatchers = new ArrayList<AbstractMatcher>();
		
		if( !isCancelled() ) {
			AbstractMatcher asm = MatcherFactory.getMatcherInstance(MatchersRegistry.AdvancedSimilarity, 0);
			
			AdvancedSimilarityParameters asmParam = new AdvancedSimilarityParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			asmParam.useLabels = false;
			
			setupSubMatcher(asm, asmParam);
			runSubMatcher(asm, "ASM 1/6");
			
			lwcInputMatchers.add(asm);			
		}
		
		// PSM
		if( !isCancelled() ) {
			AbstractMatcher psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 0);
			
			ParametricStringParameters psmParam = new ParametricStringParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			psmParam.localWeight = 0.33;
			psmParam.labelWeight = 0.34d;
			psmParam.commentWeight = 0.33d;
			psmParam.seeAlsoWeight = 0.00d;
			psmParam.isDefinedByWeight = 0.00d;
			
			psmParam.useLexicons = false;
			psmParam.useBestLexSimilarity = false;
			psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
			psmParam.normParameter = new NormalizerParameter();
			psmParam.normParameter.setForOAEI2009();
			psmParam.redistributeWeights = true;
			
			setupSubMatcher(psm, psmParam);
			runSubMatcher(psm, "PSM 2/6");
			
			lwcInputMatchers.add(psm);			
		}
		
		// VMM
		if( !isCancelled() ) {
			AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 0);
			
			MultiWordsParameters vmmParam = new MultiWordsParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			vmmParam.measure = MultiWordsParameters.TFIDF;
			//only on concepts right now because it should be weighted differently
			vmmParam.considerInstances = true;
			vmmParam.considerNeighbors = false;
			vmmParam.considerConcept = true;
			vmmParam.considerClasses = false;
			vmmParam.considerProperties = false;
			vmmParam.ignoreLocalNames = true; 
			
			vmmParam.useLexiconSynonyms = true; // May change later.
			
			setupSubMatcher(vmm, vmmParam);
			runSubMatcher(vmm, "VMM 3/6");
			
			lwcInputMatchers.add(vmm);
		}
		
		// LSM
		if( !isCancelled() ) {
			AbstractMatcher lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSM, 0);
			
			LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lsmParam.useSynonymTerms = false;
			
			setupSubMatcher(lsm, lsmParam);
			runSubMatcher(lsm, "LSM 4/6");
			
			lwcInputMatchers.add(lsm);
		}
		
		// LWC
		AbstractMatcher lwc = null;
		if( !isCancelled() ) {
			lwc = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc.setInputMatchers(lwcInputMatchers);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc, lwcParam);
			runSubMatcher(lwc, "LWC 5/6");
			
		}
		
		if( !isCancelled() ) {
			AbstractMatcher iism = MatcherFactory.getMatcherInstance(MatchersRegistry.IISM, 0);
			
			iism.addInputMatcher(lwc);
			
			IterativeInstanceStructuralParameters iismParam = 
					new IterativeInstanceStructuralParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			iismParam.allBoost();
			iismParam.setConsiderIndividuals(true);
			iismParam.setPropertyUsageThreshold(0.6);
			iismParam.setPropertyValuesThreshold(0.5);
			iismParam.setRangeDomainThreshold(0.89);
			iismParam.setSuperclassThreshold(0.6);
			iismParam.setUsePropertyUsage(true);
			iismParam.setUsePropertyValues(true);
			iismParam.setUseRangeDomain(true);
			iismParam.setUseSuperclasses(true);
			
			setupSubMatcher(iism, iismParam);
			runSubMatcher(iism, "IISM 6/6");
			
			return iism;
		}
		
		return null;
	}
	
	private AbstractMatcher runLexicalBased() throws Exception {
		
		// this threshold works best.
		setThreshold(0.73d);
		
		// allow for overrides
		// FIXME: Remove this.
		try {
			File thresholdFile = new File("threshold.txt");
			if( thresholdFile.exists() && thresholdFile.canRead()) {
				BufferedReader thresholdReader = new BufferedReader( new FileReader(thresholdFile) );
				String firstLine = thresholdReader.readLine();
				double threshold = Double.parseDouble(firstLine);
				if( threshold > 0 && threshold <= 1.0 ) setThreshold(threshold);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Build the lexicons.
		LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
		lexParam.sourceOntology = sourceOntology;
		lexParam.targetOntology = targetOntology;
		
		lexParam.sourceUseLocalname = false;
		lexParam.targetUseLocalname = false;
		lexParam.sourceUseSCSLexicon = true;
		lexParam.targetUseSCSLexicon = true;
		
		lexParam.detectStandardProperties(sourceOntology);
		lexParam.detectStandardProperties(targetOntology);
		
		Core.getLexiconStore().buildAll(lexParam);
		
		final List<AbstractMatcher> lwc1InputMatchers = new ArrayList<AbstractMatcher>();
		final List<AbstractMatcher> lwc2InputMatchers = new ArrayList<AbstractMatcher>();
		
		//ThreadGroup threadGroup = new ThreadGroup("OAEI2011");
		
		
		
		
		if( ((OAEI2011MatcherParameters)param).parallelExecution ) {
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.setIndeterminate(true);
			
			int availableProcessors = Runtime.getRuntime().availableProcessors() - param.threadedReservedProcessors;
			if( availableProcessors < 1 ) // this should not happen 
				availableProcessors = 1;  // but in case it does, we fix it.
			
			int totalSteps = 4;
			int currentStep = 0;
			
			ThreadGroup oaeiThreadGroup = new ThreadGroup("OAEI2011");
			
			int stages = totalSteps / availableProcessors;
			if( stages <= 0 ) stages = 1;
						
			for( int i = 0; i < stages; i++ ) {
				for( int j = 0; j < availableProcessors; j++ ) {
					final OAEI2011Matcher oaei = this;
					if( currentStep == 0 ) {
						// spawn PSM
						Thread psmThread = new Thread(oaeiThreadGroup, new Runnable() {
							
							@Override
							public void run() {
								try {
									oaei.runPSM(lwc2InputMatchers);
								} catch (Exception e) {
									e.printStackTrace();
									oaei.cancel(true);
								}
							}
						});
						
						for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Running PSM ...\n");
						psmThread.start();
						currentStep++;
					} 
					else if( currentStep == 1 ) {
						// spawn VMM
						Thread vmmThread = new Thread(oaeiThreadGroup, new Runnable() {
							
							@Override
							public void run() {
								try {
									oaei.runVMM(lwc2InputMatchers);
								} catch (Exception e) {
									e.printStackTrace();
									oaei.cancel(true);
								}
							}
						});
						
						for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Running VMM ...\n");
						vmmThread.start();
						currentStep++;
					}
					else if( currentStep == 2 ) {
						// spawn LSM
						Thread lsmThread = new Thread(oaeiThreadGroup, new Runnable() {
							
							@Override
							public void run() {
								try {
									oaei.runLSM(lwc1InputMatchers);
								} catch (Exception e) {
									e.printStackTrace();
									oaei.cancel(true);
								}
							}
						});
						
						for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Running LSM Weighted ...\n");
						lsmThread.start();
						currentStep++;
					} 
					else if( currentStep == 3 ) {
						// spawn MM
						Thread mmThread = new Thread(oaeiThreadGroup, new Runnable() {
							
							@Override
							public void run() {
								try {
									oaei.runMM(lwc1InputMatchers);
								} catch (Exception e) {
									e.printStackTrace();
									oaei.cancel(true);
								}
							}
						});
						
						for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Running MM ...\n");
						mmThread.start();
						currentStep++;
					}
				}
				
				// wait for the stage to end.
				while( oaeiThreadGroup.activeCount() > 0 ) {
					Thread.sleep(500);
				}
			}
			
			for( MatchingProgressDisplay mpd : progressDisplays ) {
				mpd.appendToReport("Finished running threads...\n");
				mpd.setIndeterminate(false);
			}
		}
		else { 
			// we are not running in threaded mode
			// sequentially run everything
			runPSM(lwc2InputMatchers);
			runVMM(lwc2InputMatchers);
			runLSM(lwc1InputMatchers);
			runMM(lwc1InputMatchers);
		}
		
		
		
		// LWC1 (LSM, MM)
		AbstractMatcher lwc1 = null;
		if( !isCancelled() ) {
			lwc1 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc1.setInputMatchers(lwc1InputMatchers);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc1, lwcParam);
			runSubMatcher(lwc1, "LWC (LSM, MM) 5/7");
			
			matchersByID.put(SubMatcherID.LWC1, lwc1);
			
		}
		
		// LWC2 (LSM, MM)
		AbstractMatcher lwc2 = null;
		if( !isCancelled() ) {
			lwc2 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc2.setInputMatchers(lwc2InputMatchers);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc2, lwcParam);
			runSubMatcher(lwc2, "LWC (PSM, VMM) 6/7");
			
			matchersByID.put(SubMatcherID.LWC2, lwc2);
		}
		
		// LWC3 (LSM, MM)
		AbstractMatcher lwc3 = null;
		if( !isCancelled() ) {
			lwc3 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc3.addInputMatcher(lwc1);
			lwc3.addInputMatcher(lwc2);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc3, lwcParam);
			runSubMatcher(lwc3, "LWC (PSM, VMM) 7/7");
			
			matchersByID.put(SubMatcherID.LWC3, lwc3);
		}
		
				
		return lwc3;
	}
	
	private void runPSM(List<AbstractMatcher> lwc2InputMatchers) throws Exception {
		// PSM
		if( isCancelled() ) return;
		
		AbstractMatcher psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 0);
		
		ParametricStringParameters psmParam = new ParametricStringParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
		
		psmParam.useLexicons = true;
		psmParam.useBestLexSimilarity = true;
		psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
		psmParam.normParameter = new NormalizerParameter();
		psmParam.normParameter.setForOAEI2009();
		psmParam.redistributeWeights = true;
		
		// threaded execution
		psmParam.threadedExecution = true;
		psmParam.threadedOverlap = true;
		psmParam.threadedReservedProcessors = 2;
		
		setupSubMatcher(psm, psmParam);
		psm.setPerformSelection(false);
		runSubMatcher(psm, "PSM 1/7");
		
		if( isCancelled() ) return;
		
		AbstractMatcher boost = MatcherFactory.getMatcherInstance(MatchersRegistry.Boost, 0);
		
		BestMatchBoostingParameters boostParam = new BestMatchBoostingParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
		boostParam.deepCopy = false;
		boostParam.boostPercent = 1.1;
		
		setupSubMatcher(boost, boostParam);
		
		boost.addInputMatcher(psm);
		boost.setPerformSelection(false);
		
		runSubMatcher(boost, "BOOST PSM 1/7");
		
		// now do the selection.
		psm.select();
		
		lwc2InputMatchers.add(psm);			
		
		matchersByID.put(SubMatcherID.PSM, psm);
		
	}
	
	// private method, used in large lexical
	private void runVMM(List<AbstractMatcher> lwc2InputMatchers) throws Exception {
		// VMM
		if( !isCancelled() ) {

			// find part of properties
			OntProperty sourcePartOf = null;
			OntProperty targetPartOf = null;
			
			for( Node property : sourceOntology.getPropertiesList() ) {
				if( property.getLocalName().toLowerCase().contains("partof") || 
					property.getLocalName().toLowerCase().contains("part_of") ) {
					
					sourcePartOf = property.getResource().as(OntProperty.class);
					break;
				}
			}
			
			for( Node property : targetOntology.getPropertiesList() ) {
				if( property.getLocalName().toLowerCase().contains("partof") || 
					property.getLocalName().toLowerCase().contains("part_of") ) {
					
					targetPartOf = property.getResource().as(OntProperty.class);
					break;
				}
			}

			
			AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 0);
			
			MultiWordsParameters vmmParam = new MultiWordsParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			vmmParam.measure = MultiWordsParameters.TFIDF;
			vmmParam.considerInstances = true;
			vmmParam.considerNeighbors = false;  // figure out if this helps.
			vmmParam.considerConcept = true;
			vmmParam.considerClasses = false;
			vmmParam.considerProperties = false;
			vmmParam.ignoreLocalNames = true; 
			
			vmmParam.useLexiconSynonyms = true; // May change later.
			vmmParam.considerSuperClass = true;
			
			// decide if we're using part of properties
			if( sourcePartOf != null ) {
				NodeHierarchy sourcePartOfHier = new AlternateHierarchy(sourceOntology, sourcePartOf);
				sourceOntology.addHierarchy(sourcePartOf, sourcePartOfHier);
				vmmParam.sourceAlternateHierarchy = sourcePartOf;
			}
			if( targetPartOf != null ) {
				NodeHierarchy targetPartOfHier = new AlternateHierarchy(targetOntology, targetPartOf);
				targetOntology.addHierarchy(targetPartOf, targetPartOfHier);
				vmmParam.targetAlternateHierarchy = targetPartOf;
			}
			
			setupSubMatcher(vmm, vmmParam);
			//if( param.threadedExecution ) vmm.setProgressDisplay(null); // don't spam the progress display if we're in threaded mode.
			runSubMatcher(vmm, "VMM 2/7");
			
			lwc2InputMatchers.add(vmm);
			
			matchersByID.put(SubMatcherID.VMM, vmm);
		}
	}
	
	private void runLSM(List<AbstractMatcher> lwc1InputMatchers) throws Exception {
		// LSM
		if( !isCancelled() ) {
			AbstractMatcher lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSMWeighted, 0);
			
			LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lsmParam.useSynonymTerms = true;
			
			setupSubMatcher(lsm, lsmParam);
			//if( param.threadedExecution ) lsm.setProgressDisplay(null); // don't spam the progress display if we're in threaded mode.
			runSubMatcher(lsm, "LSM Weighted 3/7");
			
			lwc1InputMatchers.add(lsm);
			
			matchersByID.put(SubMatcherID.LSM, lsm);
		}
	}
	
	private void runMM(List<AbstractMatcher> lwc1InputMatchers) throws Exception {
		// MM
		if( !isCancelled() ) {
			AbstractMatcher mm = MatcherFactory.getMatcherInstance(MatchersRegistry.BridgeMatcher, 0);
			
			MediatingMatcherParameters mmParam = new MediatingMatcherParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			mmParam.mediatingOntology = "lexicon/uberon/uberon.owl";
			
			setupSubMatcher(mm, mmParam);
			//if( ((OAEI2011MatcherParameters)param).parallelExecution ) mm.setProgressDisplay(null); // don't spam the progress display if we're in threaded mode.
			runSubMatcher(mm, "MM 4/7");
			
			lwc1InputMatchers.add(mm);
			
			matchersByID.put(SubMatcherID.MM, mm);
		}
	}
	
	private AbstractMatcher runLexicalBasedWithLocalnames() throws Exception {
		
		// Build the lexicons.
		LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
		lexParam.sourceOntology = sourceOntology;
		lexParam.targetOntology = targetOntology;
		
		lexParam.sourceUseLocalname = true;
		lexParam.targetUseLocalname = true;
		lexParam.sourceUseSCSLexicon = false;
		lexParam.targetUseSCSLexicon = false;
		
		lexParam.detectStandardProperties(sourceOntology);
		lexParam.detectStandardProperties(targetOntology);
		
		Core.getLexiconStore().buildAll(lexParam);
		
		//List<AbstractMatcher> lwc1InputMatchers = new ArrayList<AbstractMatcher>();
		List<AbstractMatcher> lwc2InputMatchers = new ArrayList<AbstractMatcher>();
		
		//ThreadGroup threadGroup = new ThreadGroup("LEXMATCH");
		
		// LSM
		AbstractMatcher lsm = null;
		if( !isCancelled() ) {
			lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSMWeighted, 0);
			
			LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lsmParam.useSynonymTerms = false;
			
			setupSubMatcher(lsm, lsmParam);
			runSubMatcher(lsm, "LSM Weighted 1/7");
			
			//lwc1InputMatchers.add(lsm);
		}
		
		/*// MM
		if( !isCancelled() ) {
			AbstractMatcher mm = MatcherFactory.getMatcherInstance(MatchersRegistry.BridgeMatcher, 0);
			
			MediatingMatcherParameters mmParam = new MediatingMatcherParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			mmParam.mediatingOntology = "lexicon/uberon/uberon.owl";
			mmParam.loadSourceBridge = true;
			mmParam.sourceBridge = "lexicon/uberon/uberon-mouse-alignment.rdf"; // FIXME: THIS ASSUMES MOUSE IS THE SOURCE ONTOLOGY!
			mmParam.loadTargetBridge = true;
			mmParam.targetBridge = "lexicon/uberon/uberon-human-alignment.rdf"; // FIXME: THIS ASSUMES HUMAN IS THE TARGET ONTOLOGY!
			
			setupSubMatcher(mm, mmParam);
			runSubMatcher(mm, "MM 2/7");
			
			lwc1InputMatchers.add(mm);
		}*/
		
		// PSM
		if( !isCancelled() ) {
			AbstractMatcher psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 0);
			
			ParametricStringParameters psmParam = new ParametricStringParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			psmParam.useLexicons = true;
			psmParam.useBestLexSimilarity = true;
			psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
			psmParam.normParameter = new NormalizerParameter();
			psmParam.normParameter.setForOAEI2009();
			psmParam.redistributeWeights = true;
			
			psmParam.threadedExecution = true;
			psmParam.threadedOverlap = true;
			
			setupSubMatcher(psm, psmParam);
			runSubMatcher(psm, "PSM 3/7");
			
			lwc2InputMatchers.add(psm);			
		}
		
		// VMM
		if( !isCancelled() ) {
			AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 0);
			
			MultiWordsParameters vmmParam = new MultiWordsParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			
			vmmParam.measure = MultiWordsParameters.TFIDF;
			vmmParam.considerInstances = true;
			vmmParam.considerNeighbors = false;  // figure out if this helps.
			vmmParam.considerConcept = true;
			vmmParam.considerClasses = false;
			vmmParam.considerProperties = false;
			vmmParam.ignoreLocalNames = false; 
			
			vmmParam.useLexiconSynonyms = true; // May change later.
			vmmParam.considerSuperClass = true;
			
			setupSubMatcher(vmm, vmmParam);
			runSubMatcher(vmm, "VMM 4/7");
			
			lwc2InputMatchers.add(vmm);
		}
		
		/*// LWC1 (LSM, MM)
		AbstractMatcher lwc1 = null;
		if( !isCancelled() ) {
			lwc1 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc1.setInputMatchers(lwc1InputMatchers);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc1, lwcParam);
			runSubMatcher(lwc1, "LWC (LSM, MM) 5/7");
			
		}*/
		
		// LWC2 (PSM, VMM)
		AbstractMatcher lwc2 = null;
		if( !isCancelled() ) {
			lwc2 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc2.setInputMatchers(lwc2InputMatchers);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc2, lwcParam);
			runSubMatcher(lwc2, "LWC (PSM, VMM) 6/7");
			
		}
		
		// LWC3 (Final)
		AbstractMatcher lwc3 = null;
		if( !isCancelled() ) {
			lwc3 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
			
			lwc3.addInputMatcher(lsm);
			lwc3.addInputMatcher(lwc2);
			
			CombinationParameters lwcParam = new CombinationParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
			setupSubMatcher(lwc3, lwcParam);
			runSubMatcher(lwc3, "LWC (PSM, VMM) 7/7");
			
		}
		
				
		return lwc3;
	}

	private AbstractMatcher runMultiOntologyBased() throws Exception {
		//FIRST LAYER: ASM and PSM
		//ASM

		AbstractMatcher asm = MatcherFactory.getMatcherInstance(MatchersRegistry.AdvancedSimilarity, 0);
		setupSubMatcher(asm, new AdvancedSimilarityParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign()));
		runSubMatcher(asm, "Submatcher 1/4: ASM");
		
		AbstractMatcher psm = null; 
		if( !isCancelled() ) {
			psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 0);
		
			ParametricStringParameters psmParam = new ParametricStringParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			psmParam.localWeight = 0.5d;
			psmParam.labelWeight = 0.5d;
			psmParam.commentWeight = 0.5d;
			psmParam.seeAlsoWeight = 0.0d;
			psmParam.isDefinedByWeight = 0.0d;
			
			psmParam.useLexicons = false;
			psmParam.useBestLexSimilarity = false;
			
			psmParam.measure = ParametricStringParameters.AMSUB_AND_EDIT;
			psmParam.normParameter = new NormalizerParameter();
			psmParam.normParameter.setForOAEI2009();
			psmParam.redistributeWeights = true;
			
			setupSubMatcher(psm, psmParam);
			runSubMatcher(psm, "Submatcher 2/4: PSM");

		}
		
		//Second layer: LWC(ASM, PSM)
		//LWC matcher
		AbstractMatcher lwc = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 0);
		lwc.getInputMatchers().add(asm);
		lwc.getInputMatchers().add(psm);
		setupSubMatcher(lwc, new CombinationParameters(getThreshold(),getMaxSourceAlign(), getMaxTargetAlign()).initForOAEI2010(OAEI_Track.Conference, true));
		runSubMatcher(lwc, "Submatcher 3/4: LWC( ASM, PSM)");

		//Third layer: GFM
		AbstractMatcher gfm = MatcherFactory.getMatcherInstance(MatchersRegistry.GroupFinder, 0);
		gfm.getInputMatchers().add(lwc);
		setupSubMatcher(gfm, new AbstractParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign()));
		runSubMatcher(gfm, "Submatcher 4/4: GFM( LWC )");
		//return gfm;

		return gfm;
	}
	
	/* ********************************* COMMON METHODS *********************************** */
	
	private void setupSubMatcher( AbstractMatcher m, AbstractParameters p ) { setupSubMatcher(m, p, true); }
	
	private void setupSubMatcher( AbstractMatcher m, AbstractParameters p, boolean progressDelay ) {
		m.setParam(p);
		m.setSourceOntology(sourceOntology);
    	m.setTargetOntology(targetOntology);
		for( MatchingProgressDisplay mpd : progressDisplays ) m.addProgressDisplay(mpd);
		m.setUseProgressDelay(progressDelay);
		m.setPerformSelection(true);
	}
	
	private void runSubMatcher(AbstractMatcher m, String label) throws Exception {
		long startime = 0, endtime = 0, time = 0;
		long measure = 1000000;
		
		OAEI2011MatcherParameters p = (OAEI2011MatcherParameters) param;
		
		if( Core.DEBUG ) System.out.println("Running " + m.getRegistryEntry().getMatcherShortName() );
		startime = System.nanoTime()/measure;
		
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.setProgressLabel(label);
		//m.setProgressDisplay(getProgressDisplay());
		m.match();
		//m.setProgressDisplay(null);
		if( m.isCancelled() ) { cancel(true); } // the user canceled the matching process  
		
		endtime = System.nanoTime()/measure;
	    time = (endtime-startime);
		if( Core.DEBUG ) System.out.println(m.getRegistryEntry().getMatcherShortName() + " completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
		if(p.showIntermediateMatchers && !m.isCancelled()) 
			Core.getInstance().addMatcherInstance(m);
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new OAEI2011MatcherParametersPanel();
		}
		return parametersPanel;
	}
	
	public AbstractMatcher getSubMatcherByID(SubMatcherID id) {
		return matchersByID.get(id);
	}
	
}