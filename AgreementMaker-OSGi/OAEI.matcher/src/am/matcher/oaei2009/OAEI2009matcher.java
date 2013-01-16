package am.matcher.oaei2009;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.PRAMatcher.PRAMatcher2;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.matcher.bsm.BaseSimilarityParameters;


//import uk.ac.shef.wit.simmetrics.similaritymetrics.*; //all sim metrics are in here

public class OAEI2009matcher extends AbstractMatcher { 

	
	//private transient Normalizer normalizer;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1267177915055140323L;

	public OAEI2009matcher() {
		// warning, param is not available at the time of the constructor
		super();
		needsParam = true;
		param = new OAEI2009parameters();
		
		setName("OAEI 2009");
		setCategory(MatcherCategory.HYBRID);
	}
	
	@Override
	public String getDescriptionString() {
		return "The method adopted in the OAEI2009 competition." +
				"For more details, please read OAEI2009 results for the AgreementMaker available at www.cs.uic.edu/Cruz/Publications#2009. "+
				"The configurations for the difference tracks are selected in the parameters panel. ";
	}
	
	
	
	/* *******************************************************************************************************
	 ************************ Init structures*************************************
	 * *******************************************************************************************************
	 */
	
	@Override
	public void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		//OAEI2009parameters parameters =(OAEI2009parameters)param;
	}

	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */
	@Override
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
	    	pra.setParameters(new DefaultMatcherParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign));
	    	BaseSimilarityParameters bsmp = new BaseSimilarityParameters();
	    	bsmp.initForOAEI2009();
	    	pra.setParameters(bsmp);
	    	pra.setSourceOntology(sourceOntology);
	    	pra.setTargetOntology(targetOntology);
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
	    	pra.setParameters(new DefaultMatcherParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign));
	    	BaseSimilarityParameters bsmp = new BaseSimilarityParameters();
	    	bsmp.initForOAEI2009();
	    	pra.setParameters(bsmp);
	    	pra.setSourceOntology(sourceOntology);
	    	pra.setTargetOntology(targetOntology);
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
    	ParametricStringParameters psmp = 
    			new ParametricStringParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
    	psmp.initForOAEI2009();
    	psm.setParameters(psmp);
    	psm.setSourceOntology(sourceOntology);
    	psm.setTargetOntology(targetOntology);
    	//psm.setPerformSelection(false);
		psm.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("PSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
		//vmm
    	System.out.println("Running VMM");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher vmm = MatcherFactory.getMatcherInstance(MatchersRegistry.MultiWords, 2);
    	
    	MultiWordsParameters vmmp = new MultiWordsParameters(param.threshold, param.maxSourceAlign, param.maxTargetAlign);
    	vmmp.initForOAEI2009();
    	vmm.setParameters(vmmp);
    	vmm.setSourceOntology(sourceOntology);
    	vmm.setTargetOntology(targetOntology);
    	//vmm.setPerformSelection(false);
		vmm.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("VMM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
		
		
		
		//Second layer: LWC(VMM, PSM, BSM)
		//LWC matcher
    	System.out.println("Running LWC");
    	startime = System.nanoTime()/measure;
    	int nextID = Core.getInstance().getMatcherInstances().size();
    	AbstractMatcher lwc = MatcherFactory.getMatcherInstance(MatchersRegistry.Combination, nextID);
    	lwc.getInputMatchers().add(psm);
    	lwc.getInputMatchers().add(vmm);
    	lwc.getInputMatchers().add(pra);
    	lwc.getParam().threshold = param.threshold;
    	lwc.setMaxSourceAlign(getMaxSourceAlign());
    	lwc.setMaxTargetAlign(getMaxTargetAlign());
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

		// Original code
		//Forth or fifth layer: DSI
		//DSI
    	System.out.println("Running DSI");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher dsi = MatcherFactory.getMatcherInstance(MatchersRegistry.DSI, 0);
    	dsi.getInputMatchers().add(lastLayer);
    	dsi.getParam().threshold = param.threshold;
    	dsi.setMaxSourceAlign(getMaxSourceAlign());
    	dsi.setMaxTargetAlign(getMaxTargetAlign());
    	DescendantsSimilarityInheritanceParameters dsip = new DescendantsSimilarityInheritanceParameters();
    	dsip.initForOAEI2009();
    	dsi.setParameters(dsip);
    	dsi.setSourceOntology(sourceOntology);
    	dsi.setTargetOntology(targetOntology);
    	//dsi.setPerformSelection(true);
		dsi.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("DSI completed in (h.m.s.ms) "+Utility.getFormattedTime(time));	
		lastLayer = dsi;
		
		
		//Forth or fifth layer: BSS
		/*/BSS
    	System.out.println("Running BSS");
    	startime = System.nanoTime()/measure;
    	AbstractMatcher bss = MatcherFactory.getMatcherInstance(MatchersRegistry.BSS, 0);
    	bss.getInputMatchers().add(lastLayer);
    	bss.setThreshold(getThreshold());
    	bss.setMaxSourceAlign(getMaxSourceAlign());
    	bss.setMaxTargetAlign(getMaxTargetAlign());
    	bss.setSourceOntology(sourceOntology);
    	bss.setTargetOntology(targetOntology);
    	/* to modify and create if ever BSS will need parameters
    	DescendantsSimilarityInheritanceParameters dsip = new DescendantsSimilarityInheritanceParameters();
    	dsip.initForOAEI2009();
    	dsi.setParam(dsip);
    	dsi.setPerformSelection(true);
    	
		bss.match();
        endtime = System.nanoTime()/measure;
    	time = (endtime-startime);
		System.out.println("SSM completed in (h.m.s.ms) "+Utility.getFormattedTime(time));	
		lastLayer = bss;
*/
		if(parameters.useWordNet){
			//third layer wnl on input LWC (optimized mode)
	    	System.out.println("Running LexicalWordnet");
	    	startime = System.nanoTime()/measure;
	    	AbstractMatcher wnl = MatcherFactory.getMatcherInstance(MatchersRegistry.WordNetLexical, 2);
	    	wnl.setOptimized(true);
	    	wnl.addInputMatcher(lastLayer);
	    	wnl.getParam().threshold = param.threshold;
	    	wnl.setMaxSourceAlign(getMaxSourceAlign());
	    	wnl.setMaxTargetAlign(getMaxTargetAlign());
	    	wnl.setSourceOntology(sourceOntology);
	    	wnl.setTargetOntology(targetOntology);
	    	wnl.match();
	        endtime = System.nanoTime()/measure;
	    	time = (endtime-startime);
			System.out.println("WNL completed in (h.m.s.ms) "+Utility.getFormattedTime(time));
			lastLayer = wnl;
		}
		
		if(parameters.useUMLS){
			/*
			 * COMMENTED OUT IN ORDER TO COMPUTE THE JAR FILE WITHOUT THE KSS LIBRARY
			//Run UMLS matcher on unmapped nodes.
			System.out.println("Running UMLS");
			try{
		    	startime = System.nanoTime()/measure;
		    	AbstractMatcher umls = MatcherFactory.getMatcherInstance(MatchersRegistry.UMLSKSLexical, 4);
		    	umls.setOptimized(true);
		    	umls.getInputMatchers().add(lastLayer);
		    	umls.setThreshold(getThreshold());
		    	umls.setMaxSourceAlign(getMaxSourceAlign());
		    	umls.setMaxTargetAlign(getMaxTargetAlign());
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
			*/
		}
		
		System.out.println("name: "+parameters.partialReferenceFile);
		System.out.println("format: "+parameters.format);
		if( parameters.trackName == OAEI2009parameters.ANATOMY_PRI ){
	    	startime = System.nanoTime()/measure;
	    	AbstractMatcher praIntegration = MatcherFactory.getMatcherInstance(MatchersRegistry.PRAintegration, 0);
	    	praIntegration.getInputMatchers().add(lastLayer);
	    	praIntegration.getParam().threshold = param.threshold;
	    	praIntegration.setMaxSourceAlign(getMaxSourceAlign());
	    	praIntegration.setMaxTargetAlign(getMaxTargetAlign());
	    	//praIntegration uses the same parameters of ReferenceAlignmentMatcher
	    	ReferenceAlignmentParameters par = new ReferenceAlignmentParameters();
	    	par.fileName = parameters.partialReferenceFile;
	    	par.format = parameters.format;
	    	praIntegration.setParam(par);
	    	praIntegration.setSourceOntology(sourceOntology);
	    	praIntegration.setTargetOntology(targetOntology);
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
			
			pra2.getParam().threshold = param.threshold;
			pra2.setMaxSourceAlign(getMaxSourceAlign());
			pra2.setMaxTargetAlign(getMaxTargetAlign());
			pra2.setSourceOntology(sourceOntology);
			pra2.setTargetOntology(targetOntology);
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
    
    @Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new OAEI2009parametersPanel();
		}
		return parametersPanel;
	}
	      
}

