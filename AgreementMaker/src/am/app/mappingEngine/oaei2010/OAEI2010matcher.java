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
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;

/**
 * @author Michele Caci
 */
public class OAEI2010matcher extends AbstractMatcher {

	// values for running different combination
	OAEI2010parameters parameters;
	
	public OAEI2010matcher(){
		needsParam = true;
		parameters = (OAEI2010parameters)getParametersPanel().getParameters();
		
	}
	
	public String getDescriptionString() {
		return "The method adopted in the OAEI2010 competition ";
	}
	
	/** *****************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
	}
	
	/** *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */
	
	public void match() throws Exception {
    	matchStart();
    	long measure = 1000000;
		AbstractMatcher lastLayer;
		long startime = 0, endtime = 0, time = 0;
		
    	//FIRST LAYER: ASM, PSM, VMM and IC1
		
    	//ASM
		AbstractMatcher asm = null;
		if(((OAEI2010parameters)param).isUsingASM()){
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
			//lastLayer = asm;
		}
		
		//PSM
		AbstractMatcher psm = null;
		if(((OAEI2010parameters)param).isUsingPSM()){
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
			//lastLayer = psm;
		}
		
		//VMM
		AbstractMatcher vmm = null;
		if(((OAEI2010parameters)param).isUsingVMM()){
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
			//lastLayer = vmm;
		}
		
		//IM1
		AbstractMatcher im1 = null;
		if(((OAEI2010parameters)param).isUsingIM1()){
			System.out.println("Running IM1");
	    	startime = System.nanoTime()/measure;
	    	im1 = MatcherFactory.getMatcherInstance(MatchersRegistry.IM, 3);
	    	im1.setThreshold(threshold);
	    	im1.setMaxSourceAlign(maxSourceAlign);
	    	im1.setMaxTargetAlign(maxTargetAlign);
	    	im1.setSourceOntology(sourceOntology);
	    	im1.setTargetOntology(targetOntology);
	    	//vmm.setPerformSelection(false);
			im1.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("IM1 completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			//lastLayer = im1;
		}
		
		//Second layer: LWC(ASM, PSM, VMM, IM1)
		
		//LWC matcher
		AbstractMatcher lwc1 = null;
		if(((OAEI2010parameters)param).isUsingLWC1()){
	    	System.out.println("Running LWC");
	    	startime = System.nanoTime()/measure;
	    	lwc1 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 4);
	    	if(((OAEI2010parameters)param).isUsingASM()){
	    		lwc1.getInputMatchers().add(asm);
	    	}
	    	if (((OAEI2010parameters)param).isUsingPSM()) {
				lwc1.getInputMatchers().add(psm);
			}
			if (((OAEI2010parameters)param).isUsingVMM()) {
				lwc1.getInputMatchers().add(vmm);
			}
			if (((OAEI2010parameters)param).isUsingIM1()) {
				lwc1.getInputMatchers().add(im1);
			}
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
			System.out.println("LWC completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		}
		lastLayer = lwc1;
		
		//Third layer: GFM, FCM and IM2
		
		//GFM
		AbstractMatcher gfm = null;
		if(((OAEI2010parameters)param).isUsingGFM()){
	    	System.out.println("Running GFM");
	    	startime = System.nanoTime()/measure;
	    	gfm = MatcherFactory.getMatcherInstance(MatchersRegistry.GroupFinder, 5);
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
			//lastLayer = gfm;
		}
		
		
		//FCM
		AbstractMatcher fcm = null;
		if(((OAEI2010parameters)param).isUsingFCM()){
	    	System.out.println("Running FCM");
	    	startime = System.nanoTime()/measure;
	    	fcm = MatcherFactory.getMatcherInstance(MatchersRegistry.FCM, 6);
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
			//lastLayer = fcm;
		}
		
		//IM2		
		AbstractMatcher im2 = null;
		if(((OAEI2010parameters)param).isUsingIM2()){
	    	System.out.println("Running IM2");
	    	startime = System.nanoTime()/measure;
	    	im2 = MatcherFactory.getMatcherInstance(MatchersRegistry.IM, 7);
	    	im2.getInputMatchers().add(lastLayer);
	    	im2.setThreshold(threshold);
	    	im2.setMaxSourceAlign(maxSourceAlign);
	    	im2.setMaxTargetAlign(maxTargetAlign);
	    	im2.setSourceOntology(sourceOntology);
	    	im2.setTargetOntology(targetOntology);
	    	//im2.setPerformSelection(true);
			im2.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("IM2 completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			//lastLayer = im2;
		}
		
		//Fourth layer: LWC2
		
		//LWC2 matcher
		AbstractMatcher lwc2 = null;
		if(((OAEI2010parameters)param).isUsingLWC2()){
			System.out.println("Running LWC2");
	    	startime = System.nanoTime()/measure;
	    	lwc2 = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 8);
	    	if (((OAEI2010parameters)param).isUsingGFM()) {
				lwc2.getInputMatchers().add(gfm);
			}
			if (((OAEI2010parameters)param).isUsingFCM()) {
				lwc2.getInputMatchers().add(fcm);
			}
			if (((OAEI2010parameters)param).isUsingIM2()) {
				lwc2.getInputMatchers().add(im2);
			}
			lwc2.setThreshold(threshold);
	    	lwc2.setMaxSourceAlign(maxSourceAlign);
	    	lwc2.setMaxTargetAlign(maxTargetAlign);
	        CombinationParameters lwc2p = new CombinationParameters();
	    	lwc2p.initForOAEI2009();
	    	lwc2.setParam(lwc2p);
	    	lwc2.setSourceOntology(sourceOntology);
	    	lwc2.setTargetOntology(targetOntology);
	    	//lwc.setPerformSelection(false);
			lwc2.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("LWC2 completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			lastLayer = lwc2;
		}
		
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
			parametersPanel = new OAEI2010parametersPanel();
		}
		return parametersPanel;
	}
    	
}
