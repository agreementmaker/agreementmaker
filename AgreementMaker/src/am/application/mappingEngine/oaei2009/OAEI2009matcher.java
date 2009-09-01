package am.application.mappingEngine.oaei2009;

import java.rmi.RemoteException;
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
import am.application.mappingEngine.Combination.CombinationParametersPanel;
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
		needsParam = true;
		param = new OAEI2009parameters();
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
    	long measure = 1000000;
		AbstractMatcher lastLayer;
		OAEI2009parameters parameters = (OAEI2009parameters)param;
    	
    	//FIRST LAYER: BSM PSM and VMM
    	//BSM
    	System.out.println("Running BSM");
    	long startime = System.nanoTime()/measure;
    	AbstractMatcher bsm = MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity, 0);
    	bsm.setThreshold(threshold);
    	bsm.setMaxSourceAlign(maxSourceAlign);
    	bsm.setMaxTargetAlign(maxTargetAlign);
    	BaseSimilarityParameters bsmp = new BaseSimilarityParameters();
    	bsmp.initForOAEI2009();
    	bsm.setParam(bsmp);
    	//bsm.setPerformSelection(false);
		bsm.match();
    	long endtime = System.nanoTime()/measure;
    	long time = (endtime-startime);
		System.out.println("BSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
    	
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
    	//psm.setPerformSelection(false);
		psm.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("PSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
		//vmm
    	System.out.println("Running VMM");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 2);
    	vmm.setThreshold(threshold);
    	vmm.setMaxSourceAlign(maxSourceAlign);
    	vmm.setMaxTargetAlign(maxTargetAlign);
    	MultiWordsParameters vmmp = new MultiWordsParameters();
    	vmmp.initForOAEI2009();
    	vmm.setParam(vmmp);
    	//vmm.setPerformSelection(false);
		vmm.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("VMM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
		
		
		//Second layer: LWC(VMM, PSM, BSM)
		//LWC matcher
    	System.out.println("Running LWC");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher lwc = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, 3);
    	lwc.getInputMatchers().add(psm);
    	lwc.getInputMatchers().add(vmm);
    	lwc.getInputMatchers().add(bsm);
    	lwc.setThreshold(threshold);
    	lwc.setMaxSourceAlign(maxSourceAlign);
    	lwc.setMaxTargetAlign(maxTargetAlign);
        CombinationParameters   lwcp = new CombinationParameters();
    	lwcp.initForOAEI2009();
    	lwc.setParam(lwcp);
    	//lwc.setPerformSelection(false);
		lwc.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("LWC completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		lastLayer = lwc;

		//Forth or fifth layer: DSI
		//DSI
    	System.out.println("Running DSI");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher dsi = MatcherFactory.getMatcherInstance(MatchersRegistry.DSI, 0);
    	dsi.getInputMatchers().add(lastLayer);
    	dsi.setThreshold(threshold);
    	dsi.setMaxSourceAlign(maxSourceAlign);
    	dsi.setMaxTargetAlign(maxTargetAlign);
    	DescendantsSimilarityInheritanceParameters dsip = new DescendantsSimilarityInheritanceParameters();
    	dsip.initForOAEI2009();
    	dsi.setParam(dsip);
    	//dsi.setPerformSelection(true);
		dsi.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("DSI completed in (h.m.s.ms) "+Utility.getFormattedTime(time));	
		lastLayer = dsi;
		
		if(parameters.useWordNet){
			//third layer wnl on input LWC (optimized mode)
	    	System.out.println("Running LexicalWordnet");
	    	startime = System.nanoTime()/measure;
	    	AbstractMatcher wnl = MatcherFactory.getMatcherInstance(MatchersRegistry.WordNetLexical, 2);
	    	wnl.setOptimized(true);
	    	wnl.addInputMatcher(lastLayer);
	    	wnl.setThreshold(threshold);
	    	wnl.setMaxSourceAlign(maxSourceAlign);
	    	wnl.setMaxTargetAlign(maxTargetAlign);
	    	wnl.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("WNL completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			lastLayer = wnl;
		}
		
		if(parameters.useUMLS){
			//Run UMLS matcher on unmapped nodes.
			System.out.println("Running UMLS");
			try{
		    	startime = System.nanoTime()/measure;
		    	AbstractMatcher umls = MatcherFactory.getMatcherInstance(MatchersRegistry.UMLSKSLexical, 4);
		    	umls.setOptimized(true);
		    	umls.getInputMatchers().add(lastLayer);
		    	umls.setThreshold(threshold);
		    	umls.setMaxSourceAlign(maxSourceAlign);
		    	umls.setMaxTargetAlign(maxTargetAlign);
		    	//umls.initForOAEI2009();
		    	umls.match();
		    	time = (endtime-startime);
				System.out.println("UMLS completed in (h.m.s.ms) "+Utility.getFormattedTime(time));	
				lastLayer = umls;
			}
			catch(RemoteException e){
				e.printStackTrace();
				System.out.println("Impossible to connect to the UMLS server. The ip address has to be registered at http://kscas-lhc.nlm.nih.gov/UMLSKS");
			}
		}
		
		classesMatrix = lastLayer.getClassesMatrix();
		propertiesMatrix = lastLayer.getPropertiesMatrix();
		classesAlignmentSet = lastLayer.getClassAlignmentSet();
		propertiesAlignmentSet = lastLayer.getPropertyAlignmentSet();
		
    	matchEnd();
    	System.out.println("OAEI2009 matcher completed in (h.m.s.ms) "+Utility.getFormattedTime(executionTime));
    	//System.out.println("Classes alignments found: "+classesAlignmentSet.size());
    	//System.out.println("Properties alignments found: "+propertiesAlignmentSet.size());
    }
    
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new OAEI2009parametersPanel();
		}
		return parametersPanel;
	}
	      
}

