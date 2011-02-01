package am.app.userfeedbackloop.disagreementclustering;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Combination.CombinationMatcher;
import am.app.mappingEngine.Combination.CombinationParameters;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.mappingEngine.multiWords.MultiWordsMatcher;
import am.app.mappingEngine.multiWords.MultiWordsParameters;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringParameters;
import am.app.userfeedbackloop.ExecutionSemantics;
import am.userInterface.MatchingProgressDisplay;

/**
 * The orthogonal combination matcher.  Used as the first step in the new
 * User Feedback Loop.
 * 
 * @author Cosmin Stroe - Jan 29th, 2011.
 *
 */
public class OrthoCombinationMatcher extends ExecutionSemantics {

	private static final long serialVersionUID = -9089694302091522666L;

	public OrthoCombinationMatcher() { super(); } // super() calls initializeVariables();
	
	@Override
	public List<AbstractMatcher> getComponentMatchers() {
		ArrayList<AbstractMatcher> l = new ArrayList<AbstractMatcher>();
		
		l.add(m_bsm);
		l.add(m_asm);
		l.add(m_psm);
		l.add(m_vmm);
		
		return l;
	}

	private BaseSimilarityParameters    param_bsm;  // the parameters that will be used for the BSM
	private AdvancedSimilarityParameters param_asm; // parameters for ASM
	private ParametricStringParameters 	param_psm;  // the parameters that will be used for the PSM
	private MultiWordsParameters		param_vmm;  // VMM parameters
	
	private CombinationParameters		param_lwc;  // LWC params
	
	
	/********* MATCHERS **********/
	private BaseSimilarityMatcher		m_bsm;
	private AdvancedSimilarityMatcher	m_asm;
	private ParametricStringMatcher		m_psm;
	private MultiWordsMatcher			m_vmm;
	
	private CombinationMatcher			m_lwc;
	
	MatchingProgressDisplay progressDisplay;
	
	@Override
	public void run() {
		try {
			// TODO: Run Multiple Threads.
			// run matchers.
			if( progressDisplay != null ) {	
				progressDisplay.ignoreComplete(true);
				
				progressDisplay.setProgressLabel("BSM (1/5)");
				m_bsm.match();
				
				progressDisplay.setProgressLabel("ASM (2/5)");
				m_asm.match();
				
				progressDisplay.setProgressLabel("PSM (3/5)");
				m_psm.match();
				
				progressDisplay.setProgressLabel("VMM (4/5)");
				m_vmm.match();
				
				progressDisplay.setProgressLabel("LWC (5/5)");
				m_lwc.addInputMatcher(m_bsm);
				m_lwc.addInputMatcher(m_asm);
				m_lwc.addInputMatcher(m_psm);
				m_lwc.addInputMatcher(m_vmm);
				m_lwc.match();
				
				progressDisplay.ignoreComplete(false);
			} else {
				m_bsm.match();
				m_asm.match();
				m_psm.match();
				m_vmm.match();
				m_lwc.addInputMatcher(m_bsm);
				m_lwc.addInputMatcher(m_asm);
				m_lwc.addInputMatcher(m_psm);
				m_lwc.addInputMatcher(m_vmm);
				m_lwc.match();
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
				
	}
	
	private void initializeVariables() {		
		// Initialize the parameters for all the matchers that will be used
		param_bsm = new BaseSimilarityParameters();
		param_asm = new AdvancedSimilarityParameters();
		param_psm = new ParametricStringParameters();
		param_vmm = new MultiWordsParameters();
		
		param_lwc = new CombinationParameters();
		
		
		// BSM
		param_bsm.useDictionary = false;
		m_bsm = new BaseSimilarityMatcher(param_bsm);		
		m_bsm.setPerformSelection(false);
		m_bsm.setProgressDisplay(progressDisplay);
		
		// ASM
		param_asm.initForOAEI2009();
		m_asm = new AdvancedSimilarityMatcher(param_asm);
		m_asm.setPerformSelection(false);
		m_asm.setProgressDisplay(progressDisplay);
		
		// PSM
		param_psm.initForOAEI2009();  // use the OAEI 2009 settings
		m_psm = new ParametricStringMatcher( param_psm );
		m_psm.setPerformSelection(false);
		m_psm.setProgressDisplay(progressDisplay);
		
		// VMM
		param_vmm.initForOAEI2009();  // use the OAEI 2009 settings for this also.
		m_vmm = new MultiWordsMatcher( param_vmm );
		m_vmm.setPerformSelection(false);
		m_vmm.setProgressDisplay(progressDisplay);
		
		// LWC
		param_lwc.initForOAEI2009();  // use the OAEI 2009 settings for this also (Quality Evaluation = Local Confidence)
		m_lwc = new CombinationMatcher( param_lwc );
		m_lwc.setProgressDisplay(progressDisplay);

	}

	@Override
	public Alignment<Mapping> getAlignment() {
		return m_lwc.getAlignment();
	}
}
