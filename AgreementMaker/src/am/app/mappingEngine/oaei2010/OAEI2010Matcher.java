/**
 * 
 */
package am.app.mappingEngine.oaei2010;

import java.util.ArrayList;

import am.Utility;
import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.oaei2010.OAEI2010MatcherParameters.Track;
import am.app.mappingEngine.oaei2010.conference.OAEI2010ConferenceMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;

/**
 * @author Michele Caci
 */
public class OAEI2010Matcher extends AbstractMatcher{
	private Lexicon sourceOntologyLexicon;
	private Lexicon targetOntologyLexicon;
	
	ArrayList<AbstractMatcher> matchers;	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2258529392257305604L;

	//This should be false in batch mode & using learning matcher / true for alignment evaluation
	boolean showAllMatchers = true;

	
	//Use ontology evaluation for adapting configuration
	boolean ontologyEvaluation = true;
	
	boolean syntacticActive = true;
	
	SimilarityMatrix syntacticClassMatrix;
	SimilarityMatrix syntacticPropMatrix;
	
	AbstractMatcher lastLayer;

	public OAEI2010Matcher(){
		super();
		needsParam = true;
		param = new OAEI2010MatcherParameters(Track.AllMatchers); // should this be here?? Probably not.
		matchers = new ArrayList<AbstractMatcher>();
	}
	
	public String getDescriptionString() {
		return "The method adopted in the OAEI2010 competition ";
	}
	
	/** *****************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	public void match() throws Exception {
    	matchStart();
    	long measure = 1000000;
		OAEI2010MatcherParameters parameters = (OAEI2010MatcherParameters)param;
		long startime = 0, endtime = 0, time = 0;

		AbstractMatcher finalResult = null;
		
		switch( parameters.currentTrack ) {
		case Anatomy:
			finalResult = runAnatomy();
			break;
		case Benchmarks:
			finalResult = runBenchmarks();
			break;
		case Conference:
			OAEI2010ConferenceMatcher runConference = new OAEI2010ConferenceMatcher();

			runConference.setSourceOntology(sourceOntology);
			runConference.setTargetOntology(targetOntology);
			runConference.setMaxSourceAlign(maxSourceAlign);
			runConference.setMaxTargetAlign(maxTargetAlign);
			runConference.setThreshold(threshold);

			runConference.match();
			
			finalResult = runConference;
			break;
		default:
			
		}
		
		classesMatrix = finalResult.getClassesMatrix();
		propertiesMatrix = finalResult.getPropertiesMatrix();
		classesAlignmentSet = finalResult.getClassAlignmentSet();
		propertiesAlignmentSet = finalResult.getPropertyAlignmentSet();
		
    	matchEnd();
    	System.out.println("OAEI2010-Conference matcher completed in (h.m.s.ms) "+Utility.getFormattedTime(executionTime));
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
	}

/************************************************ BENCHMARKS *******************************************************
 *Run the BenchMarks track.
 * @return
 * @throws Exception
 *******************************************************************************************************************/
	private AbstractMatcher runBenchmarks() throws Exception {
		long startime = 0, endtime = 0, time = 0;
		long measure = 1000000;
		OAEI2010MatcherParameters parameters = (OAEI2010MatcherParameters)param;
		
		
		//ASM
		AbstractMatcher asm = null;
		if(parameters.usingASM){
			System.out.println("Running ASM");
		   	startime = System.nanoTime()/measure;
		   	asm = MatcherFactory.getMatcherInstance(MatchersRegistry.AdvancedSimilarity, 0);
		   	
		   	matchers.add(asm);
		   	
		   	asm.setThreshold(threshold);
		   	asm.setMaxSourceAlign(1);
		   	asm.setMaxTargetAlign(1);
		   	asm.setSourceOntology(sourceOntology);
		   	asm.setTargetOntology(targetOntology);
		   	//asm.setPerformSelection(false);
			asm.match();
		   	endtime = System.nanoTime()/measure;
		   	time = (endtime-startime);
			System.out.println("ASM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
			//showAllMatchers should be false in batch mode & using learning matcher / true for alignment evaluation
			if(showAllMatchers) Core.getUI().getControlPanel().getTablePanel().addMatcher(asm);
				
		}
		
		//PSM
		AbstractMatcher psm = null;
		if(parameters.usingPSM){
			System.out.println("Running PSM");
		   	startime = System.nanoTime()/measure;
		   	psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString,1);
		   	
		   	matchers.add(psm);
		   	
		   	psm.setThreshold(threshold);
		   	psm.setMaxSourceAlign(1);
		   	psm.setMaxTargetAlign(1);
		   	ParametricStringParameters psmp = new ParametricStringParameters();
		   	psmp.initForOAEI2010(parameters.currentTrack);
		   	
		   	//System.out.println("WEIGHTS: loc="+psmp.localWeight+" lab="+psmp.labelWeight+" comm:"+psmp.commentWeight);
		   		    	
		   	psm.setParam(psmp);
		   	psm.setSourceOntology(sourceOntology);
		   	psm.setTargetOntology(targetOntology);
		   	//psm.setPerformSelection(false);
			psm.match();
		    endtime = System.nanoTime()/measure;
		   	time = (endtime-startime);
		   	System.out.println("PSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			
			if(showAllMatchers) Core.getUI().getControlPanel().getTablePanel().addMatcher(psm);
		}
			
		//VMM
		AbstractMatcher vmm = null;
		if(parameters.usingVMM){
			System.out.println("Running VMM");
		   	startime = System.nanoTime()/measure;
		   	vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 2);
		   	
		   	matchers.add(vmm);
		   	
		   	vmm.setThreshold(threshold);
		   	vmm.setMaxSourceAlign(1);
		   	vmm.setMaxTargetAlign(1);
		   	MultiWordsParameters vmmp = new MultiWordsParameters();
		   	vmmp.initForOAEI2010(parameters.currentTrack);
		   	
		   	
		   	vmm.setParam(vmmp);
		   	vmm.setSourceOntology(sourceOntology);
		   	vmm.setTargetOntology(targetOntology);
		   	//vmm.setPerformSelection(false);
			vmm.match();
		    endtime = System.nanoTime()/measure;
		   	time = (endtime-startime);
			System.out.println("VMM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			
			if(showAllMatchers) Core.getUI().getControlPanel().getTablePanel().addMatcher(vmm);
		}
			
		//LSM .. maybe take out of Benchmarks track.
		AbstractMatcher lsm = null;
		if(parameters.usingVMM){
			System.out.println("Running VMM");
		   	startime = System.nanoTime()/measure;
		   	lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSM, 2);
		   	
		   	matchers.add(lsm);
		   	
		   	lsm.setThreshold(threshold);
		   	lsm.setMaxSourceAlign(1);
		   	lsm.setMaxTargetAlign(1);
		   	//MultiWordsParameters lsmp = new MultiWordsParameters();
		   	//lsmp.initForOAEI2009();
		   	//lsm.setParam(lsmp);
		   	lsm.setSourceOntology(sourceOntology);
		   	lsm.setTargetOntology(targetOntology);
		   	//lsm.setPerformSelection(false);
		   	lsm.match();
		    endtime = System.nanoTime()/measure;
		    time = (endtime-startime);
			System.out.println("VMM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			
			if(showAllMatchers) Core.getUI().getControlPanel().getTablePanel().addMatcher(lsm);
		}
			
		//Second layer: LWC(ASM, PSM, VMM, LSM)
		
		//LWC matcher
		AbstractMatcher lwc1 = null;
		if(parameters.usingLWC1){
		   	System.out.println("Running LWC");
		   	startime = System.nanoTime()/measure;
		   	lwc1 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 3);
		    	
		   	matchers.add(lwc1);
		  	
		   	lwc1.getInputMatchers().add(asm);
		   	lwc1.getInputMatchers().add(psm);
		   	lwc1.getInputMatchers().add(vmm);
		   	lwc1.setThreshold(threshold);
		   	lwc1.setMaxSourceAlign(1);
		   	lwc1.setMaxTargetAlign(1);
		    CombinationParameters   lwcp = new CombinationParameters();
		  	lwcp.initForOAEI2010(parameters.currentTrack, true);
		   	lwc1.setParam(lwcp);
		   	lwc1.setSourceOntology(sourceOntology);
		   	lwc1.setTargetOntology(targetOntology);
		   	//lwc1.setPerformSelection(false);
			lwc1.match();
		    endtime = System.nanoTime()/measure;
		   	time = (endtime-startime);
			System.out.println("LWC2 completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
			syntacticClassMatrix = lwc1.getClassesMatrix();
			syntacticPropMatrix = lwc1.getPropertiesMatrix();
				
				
			if(showAllMatchers) Core.getUI().getControlPanel().getTablePanel().addMatcher(lwc1);
		}
		lastLayer = lwc1;
		
		//Third layer: IISM
		
		//FCM
		AbstractMatcher iism = null;
		if(parameters.usingIISM){
	    	System.out.println("Running IISM");
	    	startime = System.nanoTime()/measure;
	    	iism = MatcherFactory.getMatcherInstance(MatchersRegistry.IISM, 5);
	    	
	    	matchers.add(iism);
	    	
	    	if(lastLayer!=null)
	    		iism.getInputMatchers().add(lastLayer);
	    	
	    	iism.setThreshold(threshold);
	    	
	    	((IterativeInstanceStructuralMatcher)iism).setForOAEI2010();
	    		    	
	    	iism.setMaxSourceAlign(1);
	    	iism.setMaxTargetAlign(1);
	    	
	    	iism.setSourceOntology(sourceOntology);
	    	iism.setTargetOntology(targetOntology);
	    	//fcm.setPerformSelection(false);
			iism.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("IISM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
			if(showAllMatchers) Core.getUI().getControlPanel().getTablePanel().addMatcher(iism);
		}
		return iism;
	}
	
	private AbstractMatcher runAnatomy() throws Exception {
	
		
		long startime = 0, endtime = 0, time = 0;
		long measure = 1000000;
		OAEI2010MatcherParameters parameters = new OAEI2010MatcherParameters(Track.Anatomy);
		//((OAEI2010MatcherParameters)param).initBooleansForOAEI2010(Track.Conference);
		
		
		//LSM
		AbstractMatcher lsm = null;
		if(parameters.usingLSM){
			System.out.println("Running LSM");
	    	startime = System.nanoTime()/measure;
	    	lsm = MatcherFactory.getMatcherInstance(MatchersRegistry.LSM, 0);
	    	lsm.setThreshold(threshold);
	    	lsm.setMaxSourceAlign(maxSourceAlign);
	    	lsm.setMaxTargetAlign(maxTargetAlign);
	    	//MultiWordsParameters lsmp = new MultiWordsParameters();
	    	//lsmp.initForOAEI2009();
	    	//lsm.setParam(lsmp);
	    	lsm.setSourceOntology(sourceOntology);
	    	lsm.setTargetOntology(targetOntology);
	    	lsm.setProgressDisplay(getProgressDisplay());
	    	//lsm.setPerformSelection(false);
			lsm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("LSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			matchers.add(lsm);
		}
		lsm.setProgressDisplay(null);
		
		//PSM
		AbstractMatcher psm = null;
		if(parameters.usingPSM){
			System.out.println("Running PSM");
	    	startime = System.nanoTime()/measure;
	    	psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 1);
	    	psm.setThreshold(threshold);
	    	psm.setMaxSourceAlign(maxSourceAlign);
	    	psm.setMaxTargetAlign(maxTargetAlign);
	    	ParametricStringParameters psmp = new ParametricStringParameters();
	    	psmp.initForOAEI2010(parameters.currentTrack);
	    	psm.setParam(psmp);
	    	psm.setSourceOntology(sourceOntology);
	    	psm.setTargetOntology(targetOntology);
	    	psm.setProgressDisplay(getProgressDisplay());
	    	//psm.setPerformSelection(false);
			psm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("PSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			matchers.add(psm);
		}
		psm.setProgressDisplay(null);
		
		//VMM
		AbstractMatcher vmm = null;
		if(parameters.usingVMM ){
			System.out.println("Running VMM");
	    	startime = System.nanoTime()/measure;
	    	vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 2);
	    	vmm.setThreshold(threshold);
	    	vmm.setMaxSourceAlign(maxSourceAlign);
	    	vmm.setMaxTargetAlign(maxTargetAlign);
	    	MultiWordsParameters vmmp = new MultiWordsParameters();
	    	vmmp.initForOAEI2010(parameters.currentTrack);
	    	vmm.setParam(vmmp);
	    	vmm.setSourceOntology(sourceOntology);
	    	vmm.setTargetOntology(targetOntology);
	    	//vmm.setPerformSelection(false);
	    	vmm.setProgressDisplay(getProgressDisplay());
			vmm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("VMM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			matchers.add(vmm);
		}
		vmm.setProgressDisplay(null);
		
		//Second layer: LWC(PSM, VMM, LSM)
		
		//LWC matcher
		AbstractMatcher lwc1 = null;
		if(parameters.usingLWC1){
	    	System.out.println("Running LWC");
	    	startime = System.nanoTime()/measure;
	    	lwc1 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 3);
	    	lwc1.getInputMatchers().add(psm);
	    	lwc1.getInputMatchers().add(vmm);
	    	lwc1.getInputMatchers().add(lsm);
	    	lwc1.setThreshold(threshold);
	    	lwc1.setMaxSourceAlign(maxSourceAlign);
	    	lwc1.setMaxTargetAlign(maxTargetAlign);
	        CombinationParameters   lwcp = new CombinationParameters();
	    	lwcp.initForOAEI2010(parameters.currentTrack, true);
	    	lwc1.setParam(lwcp);
	    	lwc1.setSourceOntology(sourceOntology);
	    	lwc1.setTargetOntology(targetOntology);
	    	//lwc1.setPerformSelection(false);
	    	lwc1.setProgressDisplay(getProgressDisplay());
			lwc1.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("LWC completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			matchers.add(lwc1);
		}
		//AbstractMatcher lastLayer = lwc1;
		
		lwc1.setProgressDisplay(null);
	
		return lwc1;
	}
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new OAEI2010MatcherParametersPanel();
		}
		return parametersPanel;
	}
}