/**
 * 
 */
package am.app.mappingEngine.oaei.oaei2011;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.oaei.OAEI_Track;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;

/**
 * 
 */
public class OAEI2011Matcher extends AbstractMatcher{
	
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


	private void setupSubMatcher( AbstractMatcher m, AbstractParameters p ) { setupSubMatcher(m, p, false); }
	
	private void setupSubMatcher( AbstractMatcher m, AbstractParameters p, boolean progressDelay ) {
		m.setParam(p);
		m.setUseProgressDelay(progressDelay);
		m.setProgressDisplay(getProgressDisplay());
		m.setSourceOntology(sourceOntology);
    	m.setTargetOntology(targetOntology);
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