package am.extension.userfeedback.clustering.disagreement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.mappingEngine.utility.OAEI_Track;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.extension.userfeedback.InitialMatchers;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.matcher.asm.AdvancedSimilarityMatcher;
import am.matcher.asm.AdvancedSimilarityParameters;
import am.matcher.bsm.BaseSimilarityMatcher;
import am.matcher.bsm.BaseSimilarityParameters;
import am.matcher.multiWords.MultiWordsMatcher;
import am.matcher.multiWords.MultiWordsParameters;
import am.matcher.parametricStringMatcher.ParametricStringMatcher;
import am.matcher.parametricStringMatcher.ParametricStringParameters;
import am.ui.UIUtility;

public class LargeOntologyOrthoMatchers extends InitialMatchers {
private static final Logger LOG = Logger.getLogger(OrthoCombinationMatchers.class);
	
	@Override
	public List<AbstractMatcher> getComponentMatchers() {
		ArrayList<AbstractMatcher> l = new ArrayList<AbstractMatcher>();
		
		l.add(m_bsm);
		l.add(m_asm);
		l.add(m_psm);
		l.add(m_vmm);
		l.add(m_lsm);
		//l.add(m_iism);
		
		return l;
	}

	private BaseSimilarityParameters    param_bsm;  // the parameters that will be used for the BSM
	private AdvancedSimilarityParameters param_asm; // parameters for ASM
	private ParametricStringParameters 	param_psm;  // the parameters that will be used for the PSM
	private MultiWordsParameters		param_vmm;  // VMM parameters
	private LexicalSynonymMatcherParameters param_lsm; // LSM parameters
	
	private CombinationParameters		param_lwc;  // LWC params
	//private IterativeInstanceStructuralParameters param_iism;
	
	/********* MATCHERS **********/
	private BaseSimilarityMatcher		m_bsm;
	private AdvancedSimilarityMatcher	m_asm;
	private ParametricStringMatcher		m_psm;
	private MultiWordsMatcher			m_vmm;
	private LexicalSynonymMatcher       m_lsm;
	
	private CombinationMatcher			m_lwc;
	//private IterativeInstanceStructuralMatcher m_iism;
	
	private MatchingProgressListener progressDisplay;
	
	@Override
	public void run(UFLExperiment experiment) {
				
		if( experiment.getSourceOntology() == null || experiment.getTargetOntology() == null ) {
			UIUtility.displayErrorPane("The experiment must define a pair of ontologies before the matching can start.", "Ontologies not loaded");
			return;
		}
		
		this.exp = experiment;
		
		try {
			
			progressDisplay = experiment.gui;
			initializeVariables(experiment.getSourceOntology(), experiment.getTargetOntology());
			
			// TODO: Run Multiple Threads.
			// run matchers.
			if( progressDisplay != null ) {	
				progressDisplay.ignoreComplete(true);
				
				progressDisplay.setProgressLabel("BSM (1/5)");
				exp.info("Running BSM..."); LOG.info("Running BSM...");
				m_bsm.match();
				
				progressDisplay.setProgressLabel("ASM (2/5)");
				exp.info("Running ASM..."); LOG.info("Running ASM...");
				if( m_asm != null ) 
					m_asm.match();
				
				exp.info("Running PSM..."); LOG.info("Running PSM...");
				progressDisplay.setProgressLabel("PSM (3/5)");
				m_psm.match();
				
				exp.info("Running VMM..."); LOG.info("Running VMM...");
				progressDisplay.setProgressLabel("VMM (4/5)");
				m_vmm.match();
				
				exp.info("Running LSM..."); LOG.info("Running LSM...");
				progressDisplay.setProgressLabel("LSM");
				m_lsm.match();
				
				exp.info("Running LWC..."); LOG.info("Running LWC...");
				progressDisplay.setProgressLabel("LWC (5/5)");
				m_lwc.addInputMatcher(m_bsm);
				if( m_asm != null ) m_lwc.addInputMatcher(m_asm);
				m_lwc.addInputMatcher(m_psm);
				m_lwc.addInputMatcher(m_vmm);
				m_lwc.addInputMatcher(m_lsm);
				m_lwc.match();
				
//				m_iism.addInputMatcher(m_lwc);
//				m_iism.match();
				
				progressDisplay.ignoreComplete(false);
			} else {
				m_bsm.match();
				m_asm.match();
				m_psm.match();
				m_vmm.match();
				m_lsm.match();
				
				m_lwc.addInputMatcher(m_bsm);
				m_lwc.addInputMatcher(m_asm);
				m_lwc.addInputMatcher(m_psm);
				m_lwc.addInputMatcher(m_vmm);
				m_lwc.addInputMatcher(m_lsm);
				m_lwc.match();
				
//				m_iism.addInputMatcher(m_lwc);
//				m_iism.match();
			}
			
			done();
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
				
	}
	
	private void initializeVariables( Ontology sourceOntology, Ontology targetOntology ) {
		// Initialize the parameters for all the matchers that will be used
		param_bsm = new BaseSimilarityParameters();
		param_asm = new AdvancedSimilarityParameters();
		param_psm = new ParametricStringParameters();
		param_vmm = new MultiWordsParameters();
		param_lsm = new LexicalSynonymMatcherParameters();
		
		param_lwc = new CombinationParameters();
		//param_iism = new IterativeInstanceStructuralParameters();
		
		//threshold
		param_bsm.threshold =
		param_asm.threshold =
		param_psm.threshold =
		param_vmm.threshold =
		param_lsm.threshold =
		param_lwc.threshold = 0.4;
		
		
		// BSM
		param_bsm.useDictionary = false;
		param_bsm.useProfiling = false;
		param_bsm.useLocalname = false;
		param_bsm.useNorm2 = false;
		param_bsm.useNorm3 = false;
		param_bsm.threadedExecution = true;
		param_bsm.threadedOverlap = true;
		m_bsm = new BaseSimilarityMatcher(param_bsm);
		m_bsm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)
			m_bsm.addProgressDisplay(progressDisplay);
		
		// ASM
		param_asm.useDictionary = false;
		param_asm.useProfiling = false;
		param_asm.useLabel = true;
		param_asm.threadedExecution = true;
		param_asm.threadedOverlap = false;
		m_asm = new AdvancedSimilarityMatcher(param_asm);
		m_asm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_asm.addProgressDisplay(progressDisplay);
		
		// PSM
		param_psm.initForOAEI2010(OAEI_Track.Anatomy);  // use the OAEI 2010 settings
		param_psm.useNormalizer = false;
		param_psm.threadedExecution = true;
		param_psm.threadedOverlap = true;
		m_psm = new ParametricStringMatcher( param_psm );
		m_psm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_psm.addProgressDisplay(progressDisplay);
		
		// VMM
		try {
			param_vmm.initForOAEI2010(OAEI_Track.Anatomy); // use the OAEI 2010 settings for this also.
			param_vmm.threadedExecution = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			param_vmm.initForOAEI2009();
		}  
		m_vmm = new MultiWordsMatcher( param_vmm );
		m_vmm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_vmm.addProgressDisplay(progressDisplay);
		
		param_lsm.useSynonymTerms = false;
		param_lsm.threadedExecution = true;
		m_lsm = new LexicalSynonymMatcher( param_lsm );
		m_lsm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_lsm.addProgressDisplay(progressDisplay);
				
		// LWC
		try {
			param_lwc.initForOAEI2010(OAEI_Track.Benchmarks,true); // use the OAEI 2010 settings for this also (Quality Evaluation = Local Confidence)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		m_lwc = new CombinationMatcher( param_lwc );
		m_lwc.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_lwc.addProgressDisplay(progressDisplay);
		
//		// IISM
//		param_iism.setForOAEI2010();
//		m_iism = new IterativeInstanceStructuralMatcher();
//		m_iism.setParameters(param_iism);
//		m_iism.setOntologies(sourceOntology, targetOntology);
//		if (progressDisplay!=null)m_iism.addProgressDisplay(progressDisplay);
		
		// Initialize the OntologyProfiling algorithm because The BSM needs an ontology profiler.
		if( Core.getInstance().getOntologyProfiler() == null ) {
			try {
				Core.getInstance().setOntologyProfiler(
					ManualOntologyProfiler.createOntologyProfiler(sourceOntology, targetOntology));
			}
			catch(IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		// Initialize the LexiconStore because it's required by the PSM
		if( Core.getLexiconStore().getParameters() == null ) {
			LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
			lexParam.sourceOntology = sourceOntology;
			lexParam.targetOntology = targetOntology;
			lexParam.detectStandardProperties();
			Core.getLexiconStore().setParameters(lexParam);
		}
	}

	@Override public Alignment<Mapping> getAlignment() { 
		return getFinalMatcher().getAlignment(); }
	
	@Override public Alignment<Mapping> getClassAlignment() { 
		return getFinalMatcher().getClassAlignmentSet(); }
	
	@Override public Alignment<Mapping> getPropertyAlignment() { 
		return getFinalMatcher().getPropertyAlignmentSet(); }
	
	@Override public AbstractMatcher getFinalMatcher() {
		return m_lwc;
	}
}
