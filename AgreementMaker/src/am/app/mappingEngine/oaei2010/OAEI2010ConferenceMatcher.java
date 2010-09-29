/**
 * 
 */
package am.app.mappingEngine.oaei2010;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.oaei2009.OAEI2009parameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;

/**
 * @author Michele Caci
 *
 */
public class OAEI2010ConferenceMatcher extends AbstractMatcher{

	public OAEI2010ConferenceMatcher(){
		
	}
	
	public String getDescriptionString() {
		return "The method adopted in the OAEI2010 competition " +
				"for the conference track";
	}
	
	/** *****************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
	}
	
	public void match() throws Exception {
    	matchStart();
    	long measure = 1000000;
		AbstractMatcher lastLayer;
		OAEI2009parameters parameters = (OAEI2009parameters)param; // TODO: use OAEI2010 parameters
    	
    	//FIRST LAYER: ASM and PSM
		
    	//ASM
		
		long startime = 0, endtime = 0, time = 0;
		AbstractMatcher asm = null;

		System.out.println("Running ASM");
    	startime = System.nanoTime()/measure;
    	asm = MatcherFactory.getMatcherInstance(MatchersRegistry.AdvancedSimilarity, 0);
    	asm.setThreshold(threshold);
    	asm.setMaxSourceAlign(maxSourceAlign);
    	asm.setMaxTargetAlign(maxTargetAlign);
    	//BaseSimilarityParameters bsmp = new BaseSimilarityParameters(); // ASM doesn't need parameters yet
    	//bsmp.initForOAEI2009();
    	//asm.setParam(bsmp);
    	asm.setSourceOntology(sourceOntology);
    	asm.setTargetOntology(targetOntology);
    	//asm.setPerformSelection(false);
		asm.match();
    	endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("ASM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));

		
		//PSM
    	System.out.println("Running PSM");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 1);
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
		
		//Second layer: LWC(ASM, PSM)
		//LWC matcher
    	System.out.println("Running LWC");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher lwc = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 3);
    	lwc.getInputMatchers().add(asm);
    	lwc.getInputMatchers().add(psm);
    	lwc.setThreshold(threshold);
    	lwc.setMaxSourceAlign(maxSourceAlign);
    	lwc.setMaxTargetAlign(maxTargetAlign);
        CombinationParameters   lwcp = new CombinationParameters();
    	lwcp.initForOAEI2009();
    	lwc.setParam(lwcp);
    	lwc.setSourceOntology(sourceOntology);
    	lwc.setTargetOntology(targetOntology);
    	//lwc.setPerformSelection(false);
		lwc.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("LWC completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		lastLayer = lwc;

		//Third layer: GFM
    	System.out.println("Running GFM");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher gfm = MatcherFactory.getMatcherInstance(MatchersRegistry.GroupFinder, 0);
    	gfm.getInputMatchers().add(lastLayer);
    	gfm.setThreshold(threshold);
    	gfm.setMaxSourceAlign(maxSourceAlign);
    	gfm.setMaxTargetAlign(maxTargetAlign);
//    	DescendantsSimilarityInheritanceParameters dsip = new DescendantsSimilarityInheritanceParameters();
//    	dsip.initForOAEI2009();
//    	dsi.setParam(dsip);
    	gfm.setSourceOntology(sourceOntology);
    	gfm.setTargetOntology(targetOntology);
    	//gfm.setPerformSelection(true);
		gfm.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("DSI completed in (h.m.s.ms) "+Utility.getFormattedTime(time));	
		lastLayer = gfm;
		
		classesMatrix = lastLayer.getClassesMatrix();
		propertiesMatrix = lastLayer.getPropertiesMatrix();
		classesAlignmentSet = lastLayer.getClassAlignmentSet();
		propertiesAlignmentSet = lastLayer.getPropertyAlignmentSet();
		
    	matchEnd();
    	System.out.println("OAEI2010-Conference matcher completed in (h.m.s.ms) "+Utility.getFormattedTime(executionTime));
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
	}
}
