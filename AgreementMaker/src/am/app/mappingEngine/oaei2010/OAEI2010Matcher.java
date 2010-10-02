/**
 * 
 */
package am.app.mappingEngine.oaei2010;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.oaei2009.OAEI2009parameters;
import am.app.mappingEngine.oaei2009.OAEI2009parametersPanel;
import am.app.mappingEngine.oaei2010.OAEI2010MatcherParameters.Track;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;

/**
 * @author Michele Caci
 */
public class OAEI2010Matcher extends AbstractMatcher {


	public OAEI2010Matcher(){
		super();
		needsParam = true;
		param = new OAEI2010MatcherParameters(Track.AllMatchers); // should this be here?? Probably not.
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
		AbstractMatcher lastLayer;
		OAEI2010MatcherParameters parameters = (OAEI2010MatcherParameters)param;
		long startime = 0, endtime = 0, time = 0;
		
    	//FIRST LAYER: ASM, PSM and VMM
		
    	//ASM
		AbstractMatcher asm = null;
		if(parameters.usingASM){
			System.out.println("Running ASM");
	    	startime = System.nanoTime()/measure;
	    	asm = MatcherFactory.getMatcherInstance(MatchersRegistry.AdvancedSimilarity, 0);
	    	asm.setThreshold(threshold);
	    	asm.setMaxSourceAlign(maxSourceAlign);
	    	asm.setMaxTargetAlign(maxTargetAlign);
	    	asm.setSourceOntology(sourceOntology);
	    	asm.setTargetOntology(targetOntology);
	    	//asm.setPerformSelection(false);
			asm.match();
	    	endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("ASM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		
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
	    	psmp.initForOAEI2009();
	    	psm.setParam(psmp);
	    	psm.setSourceOntology(sourceOntology);
	    	psm.setTargetOntology(targetOntology);
	    	//psm.setPerformSelection(false);
			psm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("PSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		
		//VMM
		AbstractMatcher vmm = null;
		if(parameters.usingVMM){
			System.out.println("Running VMM");
	    	startime = System.nanoTime()/measure;
	    	vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 2);
	    	vmm.setThreshold(threshold);
	    	vmm.setMaxSourceAlign(maxSourceAlign);
	    	vmm.setMaxTargetAlign(maxTargetAlign);
	    	MultiWordsParameters vmmp = new MultiWordsParameters();
	    	vmmp.initForOAEI2009();
	    	vmm.setParam(vmmp);
	    	vmm.setSourceOntology(sourceOntology);
	    	vmm.setTargetOntology(targetOntology);
	    	//vmm.setPerformSelection(false);
			vmm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("VMM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		
		//Second layer: LWC(ASM, PSM, VMM)
		
		//LWC matcher
		AbstractMatcher lwc1 = null;
		if(parameters.usingLWC1){
	    	System.out.println("Running LWC");
	    	startime = System.nanoTime()/measure;
	    	lwc1 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 3);
	    	lwc1.getInputMatchers().add(asm);
	    	lwc1.getInputMatchers().add(psm);
	    	lwc1.getInputMatchers().add(vmm);
	    	lwc1.setThreshold(threshold);
	    	lwc1.setMaxSourceAlign(maxSourceAlign);
	    	lwc1.setMaxTargetAlign(maxTargetAlign);
	        CombinationParameters   lwcp = new CombinationParameters();
	    	lwcp.initForOAEI2009();
	    	lwc1.setParam(lwcp);
	    	lwc1.setSourceOntology(sourceOntology);
	    	lwc1.setTargetOntology(targetOntology);
	    	//lwc.setPerformSelection(false);
			lwc1.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("LWC2 completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		lastLayer = lwc1;
		
		

		//Third layer: GFM, FCM and LCM
		
		//GFM
		AbstractMatcher gfm = null;
		if(parameters.usingGFM){
	    	System.out.println("Running GFM");
	    	startime = System.nanoTime()/measure;
	    	gfm = MatcherFactory.getMatcherInstance(MatchersRegistry.GroupFinder, 4);
	    	gfm.getInputMatchers().add(lastLayer);
	    	gfm.setThreshold(threshold);
	    	gfm.setMaxSourceAlign(maxSourceAlign);
	    	gfm.setMaxTargetAlign(maxTargetAlign);
	    	gfm.setSourceOntology(sourceOntology);
	    	gfm.setTargetOntology(targetOntology);
	    	//gfm.setPerformSelection(true);
			gfm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("GFM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		//lastLayer = gfm;
		
		//FCM
		AbstractMatcher fcm = null;
		if(parameters.usingFCM){
	    	System.out.println("Running FCM");
	    	startime = System.nanoTime()/measure;
	    	//fcm = MatcherFactory.getMatcherInstance(MatchersRegistry.???, 5);
	    	fcm.getInputMatchers().add(lastLayer);
	    	fcm.setThreshold(threshold);
	    	fcm.setMaxSourceAlign(maxSourceAlign);
	    	fcm.setMaxTargetAlign(maxTargetAlign);
	    	fcm.setSourceOntology(sourceOntology);
	    	fcm.setTargetOntology(targetOntology);
	    	//fcm.setPerformSelection(true);
			fcm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("FCM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		
		//LCM		
		AbstractMatcher lcm = null;
		if(parameters.usingLCM){
	    	System.out.println("Running LCM");
	    	startime = System.nanoTime()/measure;
	    	//lcm = MatcherFactory.getMatcherInstance(MatchersRegistry.???, 6);
	    	lcm.getInputMatchers().add(lastLayer);
	    	lcm.setThreshold(threshold);
	    	lcm.setMaxSourceAlign(maxSourceAlign);
	    	lcm.setMaxTargetAlign(maxTargetAlign);
	    	lcm.setSourceOntology(sourceOntology);
	    	lcm.setTargetOntology(targetOntology);
	    	//gfm.setPerformSelection(true);
			lcm.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("LCM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		
		//Fourth layer: LWC2
		
		//LWC matcher
		AbstractMatcher lwc2 = null;
		if(parameters.usingLWC2){
	    	System.out.println("Running LWC");
	    	startime = System.nanoTime()/measure;
	    	lwc2 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 7);
	    	lwc2.getInputMatchers().add(gfm);
	    	lwc2.getInputMatchers().add(fcm);
	    	lwc2.getInputMatchers().add(lcm);
	    	lwc2.setThreshold(threshold);
	    	lwc2.setMaxSourceAlign(maxSourceAlign);
	    	lwc2.setMaxTargetAlign(maxTargetAlign);
	        CombinationParameters   lwcp = new CombinationParameters();
	    	lwcp.initForOAEI2009();
	    	lwc2.setParam(lwcp);
	    	lwc2.setSourceOntology(sourceOntology);
	    	lwc2.setTargetOntology(targetOntology);
	    	//lwc.setPerformSelection(false);
			lwc2.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("LWC2 completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		lastLayer = lwc2;
		
		classesMatrix = lastLayer.getClassesMatrix();
		propertiesMatrix = lastLayer.getPropertiesMatrix();
		classesAlignmentSet = lastLayer.getClassAlignmentSet();
		propertiesAlignmentSet = lastLayer.getPropertyAlignmentSet();
		
    	matchEnd();
    	System.out.println("OAEI2010-Conference matcher completed in (h.m.s.ms) "+Utility.getFormattedTime(executionTime));
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
	}

	
	
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new OAEI2010MatcherParametersPanel();
		}
		return parametersPanel;
	}
	
}