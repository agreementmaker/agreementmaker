/**
 * 
 */
package am.matcher.dissimilar;

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
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.StringUtil.StringMetrics;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.mappingEngine.utility.OAEI_Track;
import am.app.ontology.Node;
import am.app.ontology.NodeHierarchy;
import am.app.ontology.Ontology;
import am.app.ontology.hierarchy.AlternateHierarchy;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.ontology.profiling.classification.OntologyClassifier;
import am.app.ontology.profiling.classification.OntologyClassifier.OAEI2011Configuration;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.app.ontology.profiling.manual.ManualProfilerMatchingParameters;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralParameters;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcherWeighted;
import am.matcher.asm.AdvancedSimilarityMatcher;
import am.matcher.asm.AdvancedSimilarityParameters;
import am.matcher.boosting.BestMatchBoosting;
import am.matcher.boosting.BestMatchBoostingParameters;
import am.matcher.bsm.BaseSimilarityMatcher;
import am.matcher.bsm.BaseSimilarityParameters;
import am.matcher.groupFinder.GroupFinderMatcher;
import am.matcher.mediatingMatcher.MediatingMatcher;
import am.matcher.mediatingMatcher.MediatingMatcherParameters;
import am.matcher.multiWords.MultiWordsMatcher;
import am.matcher.multiWords.MultiWordsParameters;
import am.matcher.parametricStringMatcher.ParametricStringMatcher;
import am.matcher.parametricStringMatcher.ParametricStringParameters;
import am.userInterface.MatchingProgressDisplay;
import am.matcher.*;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * The matching algorithm for OAEI 2011.
 * 
 * New features included in this year's matching algorithm:
 * 
 * 
 */
public class DissimilarMatcher extends AbstractMatcher {
	
	private static final long serialVersionUID = -2258529392257305644L;
	
	//This should be false in batch mode & using learning matcher / true for alignment evaluation

	public DissimilarMatcher(){
		super();
		needsParam = false;
		
		setName("Dissimilar Matcher");
		setCategory(MatcherCategory.HYBRID);
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
    	for( MatchingProgressDisplay mpd : progressDisplays ) mpd.ignoreComplete(true);
    	
    	AbstractMatcher finalResult = null;
    	finalResult = runGeneralPurpose();
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.ignoreComplete(false); 
    	
		if( finalResult != null ) {
			//finalResult.select();
			classesMatrix = finalResult.getClassesMatrix();
			propertiesMatrix = finalResult.getPropertiesMatrix();
			//classesAlignmentSet = finalResult.getClassAlignmentSet();
			//propertiesAlignmentSet = finalResult.getPropertyAlignmentSet();
			finalResult = null;
		}
    	matchEnd();
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
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
		
		lexParam.detectStandardProperties();
		
		Core.getLexiconStore().buildAll(lexParam);
		
		final DefaultMatcherParameters param = getParam();
		
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
			AbstractMatcher bsm = MatcherFactory.getMatcherInstance(BaseSimilarityMatcher.class);
			
			BaseSimilarityParameters bsmParam = 
					new BaseSimilarityParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			bsmParam.useDictionary = false;
			
			setupSubMatcher(bsm, bsmParam);
			runSubMatcher(bsm, "BSM 1/6");
			
			lwcInputMatchers.add(bsm);			
		}
		
		// ASM
		
		/*		
		if( !isCancelled() ) {
			AbstractMatcher asm = MatcherFactory.getMatcherInstance(AdvancedSimilarityMatcher.class);
					
			AdvancedSimilarityParameters asmParam = 
					new AdvancedSimilarityParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			asmParam.useLabels = false;
					
			setupSubMatcher(asm, asmParam);
			runSubMatcher(asm, "ASM 2/6");
					
			lwcInputMatchers.add(asm);			
		}
		*/	
		// PSM
		if( !isCancelled() ) {
			AbstractMatcher psm = MatcherFactory.getMatcherInstance(ParametricStringMatcher.class);
			
			ParametricStringParameters psmParam = 
					new ParametricStringParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			
			psmParam.localWeight = 0.33;
			psmParam.labelWeight = 0.34d;
			psmParam.commentWeight = 0.33d;
			psmParam.seeAlsoWeight = 0.00d;
			psmParam.isDefinedByWeight = 0.00d;
			
			psmParam.useLexicons = false;
			psmParam.useBestLexSimilarity = false;
			psmParam.measure = StringMetrics.AMSUB_AND_EDIT;
			psmParam.normParameter = new NormalizerParameter();
			psmParam.normParameter.setForOAEI2009();
			psmParam.redistributeWeights = true;
			
			setupSubMatcher(psm, psmParam);
			runSubMatcher(psm, "PSM 3/6");
			
			lwcInputMatchers.add(psm);			
		}
		
		// VMM
		if( !isCancelled() ) {
			AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MultiWordsMatcher.class);
			
			MultiWordsParameters vmmParam = 
					new MultiWordsParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			
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
			runSubMatcher(vmm, "VMM 4/6");
			
			lwcInputMatchers.add(vmm);
		}
		
		// LSM
		if( !isCancelled() ) {
			AbstractMatcher lsm = MatcherFactory.getMatcherInstance(LexicalSynonymMatcher.class);
			
			LexicalSynonymMatcherParameters lsmParam = 
					new LexicalSynonymMatcherParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			lsmParam.useSynonymTerms = false;
			
			setupSubMatcher(lsm, lsmParam);
			runSubMatcher(lsm, "LSM 5/6");
			
			lwcInputMatchers.add(lsm);
		}
		
		// LWC
		AbstractMatcher lwc = null;
		if( !isCancelled() ) {
			lwc = MatcherFactory.getMatcherInstance(CombinationMatcher.class);
			
			lwc.setInputMatchers(lwcInputMatchers);
			
			CombinationParameters lwcParam = 
					new CombinationParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			lwcParam.combinationType = CombinationParameters.AVERAGECOMB;
			lwcParam.qualityEvaluation = true;
			lwcParam.manualWeighted = false;
			lwcParam.quality = QualityMetricRegistry.INTRA_COUPLING;
			
			setupSubMatcher(lwc, lwcParam);
			runSubMatcher(lwc, "LWC 6/6");
			return lwc;
		}
		//IISM
		/*
		if( !isCancelled() ) {
			AbstractMatcher iism = MatcherFactory.getMatcherInstance(IterativeInstanceStructuralMatcher.class);
			
			iism.addInputMatcher(lwc);
			
			IterativeInstanceStructuralParameters iismParam = 
					new IterativeInstanceStructuralParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
			
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
		*/
		return null;
	}
	
	
	
	
	/* ********************************* COMMON METHODS *********************************** */
	
	private void setupSubMatcher( AbstractMatcher m, DefaultMatcherParameters p ) { setupSubMatcher(m, p, true); }
	
	private void setupSubMatcher( AbstractMatcher m, DefaultMatcherParameters p, boolean progressDelay ) {
		m.setParameters(p);
		m.setSourceOntology(sourceOntology);
    	m.setTargetOntology(targetOntology);
		for( MatchingProgressDisplay mpd : progressDisplays ) m.addProgressDisplay(mpd);
		m.setUseProgressDelay(progressDelay);
		m.setPerformSelection(true);
	}
	
	private void runSubMatcher(AbstractMatcher m, String label) throws Exception {
		long startime = 0, endtime = 0, time = 0;
		long measure = 1000000;
		
		//DissimilarMatcherParameters p = (DissimilarMatcherParameters) param;
		
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
		/*
		if(p.showIntermediateMatchers && !m.isCancelled()) {
			MatchingTask mt = new MatchingTask(m, m.getParam(), 
					new MwbmSelection(), new DefaultSelectionParameters());
			
			Core.getInstance().addMatchingTask(mt);
		}*/
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new DissimilarMatcherParametersPanel();
		}
		return parametersPanel;
	}
	
	public AbstractMatcher getSubMatcherByID(SubMatcherID id) {
		return matchersByID.get(id);
	}
	
}