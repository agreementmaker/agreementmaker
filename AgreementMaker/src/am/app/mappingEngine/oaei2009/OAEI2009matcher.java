package am.app.mappingEngine.oaei2009;

import java.rmi.RemoteException;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.PRAMatcher.PRAMatcher2;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;


//import uk.ac.shef.wit.simmetrics.similaritymetrics.*; //all sim metrics are in here

public class OAEI2009matcher extends AbstractMatcher { 

	
	//private Normalizer normalizer;
	
	public OAEI2009matcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
		param = new OAEI2009parameters();
	}
	
	
	public String getDescriptionString() {
		return "The method adopted in the OAEI2009 competition." +
				"For more details, please read OAEI2009 results for the AgreementMaker available at www.cs.uic.edu/Cruz/Publications#2009. "+
				"The configurations for the difference tracks are selected in the parameters panel. ";
	}
	
	
	
	/* *******************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		//OAEI2009parameters parameters =(OAEI2009parameters)param;
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
		
		long startime = 0, endtime = 0, time = 0;
		AbstractMatcher pra = null;
		
		
		if( parameters.trackName == OAEI2009parameters.ANATOMY_PRA ) {
			// We are running anatomy with PRA.  Run PRA instead of BSM.
			//AbstractMatcher refAlign = MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
			Core core = Core.getInstance();
			ReferenceAlignmentMatcher myMatcher = (ReferenceAlignmentMatcher)core.getMatcherInstance( MatchersRegistry.ImportAlignment );
			if( myMatcher == null ) {
				// we are running from the command line, we have to load the partial reference file.
				
				ReferenceAlignmentParameters par = new ReferenceAlignmentParameters();
		    	par.fileName = parameters.partialReferenceFile;
		    	par.format = parameters.format;
		    	
		    	
				ReferenceAlignmentMatcher refAlign = (ReferenceAlignmentMatcher) MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
				refAlign.setParam(par);
				refAlign.match();
				
				myMatcher = refAlign;
			}
			
	    	System.out.println("Running PRA.");
	    	startime = System.nanoTime()/measure;
	    	//AbstractMatcher pra = MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity, 0);
	    	pra = MatcherFactory.getMatcherInstance(MatchersRegistry.PRAMatcher, 0);
	    	pra.getInputMatchers().add(myMatcher);
	    	pra.setThreshold(threshold);
	    	pra.setMaxSourceAlign(maxSourceAlign);
	    	pra.setMaxTargetAlign(maxTargetAlign);
	    	BaseSimilarityParameters bsmp = new BaseSimilarityParameters();
	    	bsmp.initForOAEI2009();
	    	pra.setParam(bsmp);
	    	//bsm.setPerformSelection(false);
			pra.match();
	    	endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("PRAMatcher completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
	    	

		} else {
			// We are NOT running Anatomy with PRA.  Run BSM.
	
			System.out.println("Running BSM");
	    	startime = System.nanoTime()/measure;
	    	pra = MatcherFactory.getMatcherInstance(MatchersRegistry.BaseSimilarity, 0);
	    	pra.setThreshold(threshold);
	    	pra.setMaxSourceAlign(maxSourceAlign);
	    	pra.setMaxTargetAlign(maxTargetAlign);
	    	BaseSimilarityParameters bsmp = new BaseSimilarityParameters();
	    	bsmp.initForOAEI2009();
	    	pra.setParam(bsmp);
	    	//bsm.setPerformSelection(false);
			pra.match();
	    	endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("BSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
	    	

		}
		
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
    	lwc.getInputMatchers().add(pra);
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
		
		System.out.println("name: "+parameters.partialReferenceFile);
		System.out.println("format: "+parameters.format);
		if( parameters.trackName == OAEI2009parameters.ANATOMY_PRI ){
	    	startime = System.nanoTime()/measure;
	    	AbstractMatcher praIntegration = MatcherFactory.getMatcherInstance(MatchersRegistry.PRAintegration, 0);
	    	praIntegration.getInputMatchers().add(lastLayer);
	    	praIntegration.setThreshold(threshold);
	    	praIntegration.setMaxSourceAlign(maxSourceAlign);
	    	praIntegration.setMaxTargetAlign(maxTargetAlign);
	    	//praIntegration uses the same parameters of ReferenceAlignmentMatcher
	    	ReferenceAlignmentParameters par = new ReferenceAlignmentParameters();
	    	par.fileName = parameters.partialReferenceFile;
	    	par.format = parameters.format;
	    	praIntegration.setParam(par);
	    	//umls.initForOAEI2009();
	    	praIntegration.match();
	    	time = (endtime-startime);
			System.out.println("PRA integration completed in (h.m.s.ms) "+Utility.getFormattedTime(time));	
			lastLayer = praIntegration;
		}
		else if( parameters.trackName == OAEI2009parameters.ANATOMY_PRA ) {
			PRAMatcher2 pra2 = (PRAMatcher2) MatcherFactory.getMatcherInstance(MatchersRegistry.PRAMatcher2, 0);
			pra2.addInputMatcher(lastLayer);
			pra2.addInputMatcher(pra);
			
			pra2.setThreshold(threshold);
			pra2.setMaxSourceAlign(maxSourceAlign);
			pra2.setMaxTargetAlign(maxTargetAlign);
			
			pra2.match();
			
			lastLayer = pra2;
			
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

