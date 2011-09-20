/**
 * 
 */
package am.app.mappingEngine.oaei.oaei2011;

import java.util.ArrayList;
import java.util.List;

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
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.ontology.Ontology;

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
	boolean showAllMatchers = true;

	public OAEI2011Matcher(){
		super();
		needsParam = true;
	}
	
	public String getDescriptionString() {
		return "The method adopted in the OAEI 2011 competition.  This algorithm chooses a matcher configuration automatically.";
	}
	
	/** *****************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	public void match() throws Exception {
    	matchStart();

    	sourceOntology.setSourceOrTarget( Ontology.SOURCE );
    	targetOntology.setSourceOrTarget( Ontology.TARGET );
    	
    	AbstractMatcher finalResult = runDefault();
		
		if( finalResult != null ) {
			classesMatrix = finalResult.getClassesMatrix();
			propertiesMatrix = finalResult.getPropertiesMatrix();
			classesAlignmentSet = finalResult.getClassAlignmentSet();
			propertiesAlignmentSet = finalResult.getPropertyAlignmentSet();
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
		lexParam.targetUseLocalname = false;
		lexParam.sourceUseSCSLexicon = false;
		lexParam.targetUseSCSLexicon = false;
		
		lexParam.detectStandardProperties(sourceOntology);
		lexParam.detectStandardProperties(targetOntology);
		
		Core.getLexiconStore().buildAll(lexParam);
		
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
			runSubMatcher(iism, "IISM 5/6");
			
			return iism;
		}
		
		return null;
	}
	
	private AbstractMatcher runLexicalBased() throws Exception {
		
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
		
		// LSM
		List<AbstractMatcher> lwcInputMatchers = new ArrayList<AbstractMatcher>();
		
		if( !isCancelled() ) {
			AbstractMatcher lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSMWeighted, 0);
			
			LexicalSynonymMatcherParameters lsmParam = new LexicalSynonymMatcherParameters(getThreshold(), getMaxSourceAlign(), getMaxTargetAlign());
			lsmParam.useSynonymTerms = true;
			
			setupSubMatcher(lsm, lsmParam);
			runSubMatcher(lsm, "LSM 1/7");
			
			lwcInputMatchers.add(lsm);
		}
		
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
			
			setupSubMatcher(psm, psmParam);
			runSubMatcher(psm, "PSM 2/7");
			
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
			runSubMatcher(iism, "IISM 5/6");
			
			return iism;
		}
		
		return null;
	}
	

	/************************************************ BENCHMARKS *******************************************************
	 *Run the BenchMarks track.
	 * @return
	 * @throws Exception
	 *******************************************************************************************************************/
	private AbstractMatcher runDefault() throws Exception {
				
		if(getProgressDisplay()!=null) getProgressDisplay().ignoreComplete(true);
		
		//ASM
		AbstractMatcher asm = null;
		if( !isCancelled() ){
		   	asm = MatcherFactory.getMatcherInstance(MatchersRegistry.AdvancedSimilarity, 0);
		   	setupSubMatcher(asm, new AdvancedSimilarityParameters(getThreshold(),1,1));
		   	runSubMatcher(asm, "Submatcher: ASM");
		}
		
		//PSM
		AbstractMatcher psm = null;
		if( !isCancelled() ){
		   	psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString,1);
		   	setupSubMatcher(psm, new ParametricStringParameters(getThreshold(), 1, 1).initForOAEI2010(OAEI_Track.Anatomy));
		   	runSubMatcher(psm, "Submatcher: PSM");
		}
			
		//VMM
		AbstractMatcher vmm = null;
		if( !isCancelled() ){
			vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 2);
		   	setupSubMatcher(vmm, new MultiWordsParameters(getThreshold(), 1, 1).initForOAEI2010(OAEI_Track.Anatomy));
		   	runSubMatcher(vmm, "Submatcher: VMM");
		}
		
		//LSM
		AbstractMatcher lsm = null;
		if( !isCancelled() ){
		   	lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSM, 2);
		   	setupSubMatcher(lsm, new LexicalSynonymMatcherParameters(getThreshold(), 1, 1));
		   	runSubMatcher(lsm, "Submatcher: LSM");
		}
			
		//Second layer: LWC(ASM, PSM, VMM, LSM)
		
		//LWC matcher
		AbstractMatcher lwc1 = null;
		if( !isCancelled() ){
		   	lwc1 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 3);
		   	lwc1.getInputMatchers().add(asm);
		   	lwc1.getInputMatchers().add(psm);
		   	lwc1.getInputMatchers().add(vmm);
		   	setupSubMatcher(lwc1, new CombinationParameters(getThreshold(),1,1).initForOAEI2010(OAEI_Track.Anatomy, true), true);
		   	runSubMatcher(lwc1, "LWC( ASM, PSM, VMM, LSM )");
		
		}
		
		//Third layer: IISM
		
		/*//FCM
		AbstractMatcher iism = null;
		if(parameters.usingIISM && !isCancelled()){
	    	iism = MatcherFactory.getMatcherInstance(MatchersRegistry.IISM, 5);
	    	if(lwc1!=null) iism.getInputMatchers().add(lwc1);
	    	setupSubMatcher(iism, new IterativeInstanceStructuralParameters(getThreshold(),1,1).setForOAEI2010());
	    	runSubMatcher(iism, "Submatcher: IISM");
	    }*/
		
		if(getProgressDisplay()!=null) getProgressDisplay().ignoreComplete(false);
		
		//return iism;
		return lwc1;
	}


	private void setupSubMatcher( AbstractMatcher m, AbstractParameters p ) { setupSubMatcher(m, p, true); }
	
	private void setupSubMatcher( AbstractMatcher m, AbstractParameters p, boolean progressDelay ) {
		m.setParam(p);
		m.setSourceOntology(sourceOntology);
    	m.setTargetOntology(targetOntology);
		m.setProgressDisplay(getProgressDisplay());
		m.setUseProgressDelay(progressDelay);
		m.setPerformSelection(false);
	}
	
	private void runSubMatcher(AbstractMatcher m, String label) throws Exception {
		long startime = 0, endtime = 0, time = 0;
		long measure = 1000000;
		
		if( Core.DEBUG ) System.out.println("Running " + m.getRegistryEntry().getMatcherShortName() );
		startime = System.nanoTime()/measure;
		
		if(getProgressDisplay()!=null) getProgressDisplay().setProgressLabel(label);
		m.setProgressDisplay(getProgressDisplay());
		m.match();
		m.setProgressDisplay(null);
		if( m.isCancelled() ) { cancel(true); } // the user canceled the matching process  
		
		endtime = System.nanoTime()/measure;
	    time = (endtime-startime);
		if( Core.DEBUG ) System.out.println(m.getRegistryEntry().getMatcherShortName() + " completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
		//if(showAllMatchers && !m.isCancelled()) Core.getUI().getControlPanel().getTablePanel().addMatcher(m);
	}
	
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new OAEI2011MatcherParametersPanel();
		}
		return parametersPanel;
	}
}