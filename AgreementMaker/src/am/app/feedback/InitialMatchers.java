package am.app.feedback;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.Combination.CombinationMatcher;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.mappingEngine.multiWords.MultiWordsMatcher;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;

public class InitialMatchers extends AbstractMatcher {

	/******* PARAMETERS ************/
	private BaseSimilarityParameters    param_bsm = null;  // the parameters that will be used for the BSM
	private ParametricStringParameters 	param_psm = null;  // the parameters that will be used for the PSM
	private MultiWordsParameters		param_vmm = null;  // VMM parameters
	
	private CombinationParameters		param_lwc = null;  // LWC params
	
	
	/********* MATCHERS **********/
	private BaseSimilarityMatcher		m_bsm = null;
	private ParametricStringMatcher		m_psm = null;
	private MultiWordsMatcher			m_vmm = null;
	
	private CombinationMatcher			m_lwc = null;
	
	
	protected void initializeVariables() {
		super.initializeVariables();
		
		// Initialize the parameters for all the matchers that will be used
		
		param_bsm = new BaseSimilarityParameters();
		param_psm = new ParametricStringParameters();
		param_vmm = new MultiWordsParameters();
		
		param_lwc = new CombinationParameters();
		
		
		// BSM
		param_bsm.useDictionary = false;
		m_bsm = new BaseSimilarityMatcher(param_bsm);		
		m_bsm.setPerformSelection(false);
		
		// PSM
		param_psm.initForOAEI2009();  // use the OAEI 2009 settings
		m_psm = new ParametricStringMatcher( param_psm );
		m_psm.setPerformSelection(false);
		
		// VMM
		param_vmm.initForOAEI2009();  // use the OAEI 2009 settings for this also.
		m_vmm = new MultiWordsMatcher( param_vmm );
		m_vmm.setPerformSelection(false);
		
		// LWC
		param_lwc.initForOAEI2009();  // use the OAEI 2009 settings for this also (Quality Evaluation = Local Confidence)
		m_lwc = new CombinationMatcher( param_lwc );
		
		// TODO: The next two lines to be done right before we add this matcher to the control panel.
		//int lastIndex = Core.getInstance().getMatcherInstances().size();
		//m_lwc.setIndex(lastIIndex);
	}
	
	public String getDescriptionString() {
		return "This is the Initial Matchers block for the User Feedback Loop."; 
	}
	
	public void match() throws Exception {
		matchStart();
		
		// run the matcher stack
		m_bsm.match();
		m_psm.match();
		m_vmm.match();
		
		m_lwc.addInputMatcher(m_bsm);
		m_lwc.addInputMatcher(m_psm);
		m_lwc.addInputMatcher(m_vmm);
		
		m_lwc.match();	
		
		
		classesMatrix = m_lwc.getClassesMatrix();
		propertiesMatrix = m_lwc.getPropertiesMatrix();
		classesAlignmentSet = m_lwc.getClassAlignmentSet();
		propertiesAlignmentSet = m_lwc.getPropertyAlignmentSet();
		
		m_bsm = null; 
		m_psm = null;
		m_vmm = null;
		m_lwc = null;
		
		matchEnd();
	}

	
	protected void matchStart() {
		m_bsm.setThreshold(threshold);
		m_psm.setThreshold(threshold);
		m_vmm.setThreshold(threshold);
		m_lwc.setThreshold(threshold);
		
		m_bsm.setMaxSourceAlign(maxSourceAlign);
		m_psm.setMaxSourceAlign(maxSourceAlign);
		m_vmm.setMaxSourceAlign(maxSourceAlign);
		m_lwc.setMaxSourceAlign(maxSourceAlign);
		
		m_bsm.setMaxTargetAlign(maxSourceAlign);
		m_psm.setMaxTargetAlign(maxSourceAlign);
		m_vmm.setMaxTargetAlign(maxSourceAlign);
		m_lwc.setMaxTargetAlign(maxSourceAlign);
	}
	
	// doInBackground is inherited

}
