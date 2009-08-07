package am.application.mappingEngine.oaei2009;

import java.util.HashMap;
import java.util.HashSet;

import am.Utility;
import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.AbstractMatcherParametersPanel;
import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.MatcherFactory;
import am.application.mappingEngine.MatchersRegistry;
import am.application.mappingEngine.Combination.CombinationMatcher;
import am.application.mappingEngine.Combination.CombinationParameters;
import am.application.mappingEngine.StringUtil.ISub;
import am.application.mappingEngine.StringUtil.Normalizer;
import am.application.mappingEngine.StringUtil.StringMetrics;
import am.application.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.application.mappingEngine.baseSimilarity.BaseSimilarityMatcherParametersPanel;
import am.application.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.application.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.application.mappingEngine.multiWords.MultiWordsParameters;
import am.application.mappingEngine.multiWords.MultiWordsParametersPanel;
import am.application.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.application.mappingEngine.parametricStringMatcher.ParametricStringParametersPanel;
import am.application.ontology.Node;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*; //all sim metrics are in here

public class OAEI2009matcher extends AbstractMatcher { 


	private Normalizer normalizer;
	
	public OAEI2009matcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = false;
	}
	
	
	public String getDescriptionString() {
		return "The method adopted in the OAEI2009 competition."; 
	}
	
	
	
	/* *******************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		OAEI2009parameters parameters =(OAEI2009parameters)param;
	}

	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */

    public void match() throws Exception {
    	matchStart();
    	
    	//FIRST LAYER: BSM + PSM + VMM
    	//BSM
    	System.out.println("Running BSM");
    	long startime = System.nanoTime()/1000000000;
    	AbstractMatcher bsm = MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity, 0);
    	bsm.setThreshold(threshold);
    	bsm.setMaxSourceAlign(maxSourceAlign);
    	bsm.setMaxTargetAlign(maxTargetAlign);
    	BaseSimilarityParameters bsmp = new BaseSimilarityParameters();
    	bsmp.initForOAEI2009();
    	bsm.setParam(bsmp);
    	//bsm.setPerformSelection(false);
		bsm.match();
    	long endtime = System.nanoTime()/1000000000;
    	long time = (endtime-startime);
		System.out.println("BSM completed in "+time);
    	
		
		
		//PSM
    	System.out.println("Running PSM");
    	startime = System.nanoTime()/1000000000;
    	AbstractMatcher psm = MatcherFactory.getMatcherInstance(MatchersRegistry.ParametricString, 1);
    	psm.setThreshold(threshold);
    	psm.setMaxSourceAlign(maxSourceAlign);
    	psm.setMaxTargetAlign(maxTargetAlign);
    	ParametricStringParameters psmp = new ParametricStringParameters();
    	psmp.initForOAEI2009();
    	psm.setParam(psmp);
    	//psm.setPerformSelection(false);
		psm.match();
        endtime = System.nanoTime()/1000000000;
    	time = (endtime-startime);
		System.out.println("PSM completed in "+time);
		
		//vmm
    	System.out.println("Running VMM");
    	startime = System.nanoTime()/1000000000;
    	AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 2);
    	vmm.setThreshold(threshold);
    	vmm.setMaxSourceAlign(maxSourceAlign);
    	vmm.setMaxTargetAlign(maxTargetAlign);
    	MultiWordsParameters vmmp = new MultiWordsParameters();
    	vmmp.initForOAEI2009();
    	vmm.setParam(vmmp);
    	//vmm.setPerformSelection(false);
		vmm.match();
        endtime = System.nanoTime()/1000000000;
    	time = (endtime-startime);
		System.out.println("VMM completed in "+time);
		
		
		
		//Second layer: LWC
		//LWC matcher
    	System.out.println("Running LWC");
    	startime = System.nanoTime()/1000000000;
    	AbstractMatcher lwc = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 3);
    	lwc.getInputMatchers().add(bsm);
    	lwc.getInputMatchers().add(psm);
    	lwc.getInputMatchers().add(vmm);
    	lwc.setThreshold(threshold);
    	lwc.setMaxSourceAlign(maxSourceAlign);
    	lwc.setMaxTargetAlign(maxTargetAlign);
        CombinationParameters   lwcp = new CombinationParameters();
    	lwcp.initForOAEI2009();
    	lwc.setParam(lwcp);
    	//lwc.setPerformSelection(false);
		lwc.match();
        endtime = System.nanoTime()/1000000000;
    	time = (endtime-startime);
		System.out.println("LWC completed in "+time);
		
		
		//Third layer: DSI
		//DSI
    	System.out.println("Running DSI");
    	startime = System.nanoTime()/1000000000;
    	AbstractMatcher dsi = MatcherFactory.getMatcherInstance(MatchersRegistry.DSI, 0);
    	dsi.getInputMatchers().add(lwc);
    	dsi.setThreshold(threshold);
    	dsi.setMaxSourceAlign(maxSourceAlign);
    	dsi.setMaxTargetAlign(maxTargetAlign);
    	DescendantsSimilarityInheritanceParameters dsip = new DescendantsSimilarityInheritanceParameters();
    	dsip.initForOAEI2009();
    	dsi.setParam(dsip);
    	//dsi.setPerformSelection(true);
		dsi.match();
        endtime = System.nanoTime()/1000000000;
    	time = (endtime-startime);
		System.out.println("DSI completed in "+time);
		
		
		//forth and fifth can also be only a unique layer I don't know
		//Forth layer: Lexical with wordnet
		//
		
		//Fifth layer: lexical with UMLS
		//

		//ULAS: when the lexical method is ready change these two lines
		//the final alignmentset must be the one of the last layer
		AbstractMatcher lastLayer = dsi;
		classesMatrix = lastLayer.getClassesMatrix();
		propertiesMatrix = lastLayer.getPropertiesMatrix();
		classesAlignmentSet = lastLayer.getClassAlignmentSet();
		propertiesAlignmentSet = lastLayer.getPropertyAlignmentSet();
		
		
    	
    	matchEnd();
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
    }

	      
}

