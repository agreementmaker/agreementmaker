package am.extension.userfeedback.clustering.disagreement;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.persistance.PersistenceUtility;
import am.app.mappingEngine.utility.OAEI_Track;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.extension.batchmode.matchingTask.FluentMatchingTaskRunner;
import am.extension.batchmode.matchingTask.SimpleMatchingTaskRunner;
import am.extension.userfeedback.InitialMatchers;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.matcher.LexicalSynonymMatcher.LexicalSynonymMatcherParameters;
import am.matcher.asm.AdvancedSimilarityMatcher;
import am.matcher.asm.AdvancedSimilarityParameters;
import am.matcher.bsm.BaseSimilarityMatcher;
import am.matcher.bsm.BaseSimilarityParameters;
import am.matcher.dsi.DescendantsSimilarityInheritanceMatcher;
import am.matcher.dsi.DescendantsSimilarityInheritanceParameters;
import am.matcher.multiWords.MultiWordsMatcher;
import am.matcher.multiWords.MultiWordsParameters;
import am.matcher.parametricStringMatcher.ParametricStringMatcher;
import am.matcher.parametricStringMatcher.ParametricStringParameters;
import am.matcher.ssc.SiblingsSimilarityContributionMatcher;
import am.matcher.ssc.SiblingsSimilarityContributionParameters;
import am.ui.UIUtility;

/**
 * The SemanticStructural combination matcher. Used as the first step in the new
 * User Feedback Loop.
 * 
 * <p>
 * Relevant parameters to this module:
 * <ul>
 * 
 * <li>{@link Parameter#IM_THRESHOLD}: The threshold used for the selection step
 * of the matchers.</li>
 * 
 * <li>IM_XXX_LOADFILE: The file from which to load a previously computed XXX
 * result. Leave empty to compute the result instead of loading from a file. If
 * the file does not exist, it's the same as if the parameter is empty. <br>
 * {@link Parameter#IM_BSM_LOADFILE}, {@link Parameter#IM_ASM_LOADFILE},
 * {@link Parameter#IM_PSM_LOADFILE}, {@link Parameter#IM_PSM_LOADFILE}</li>
 * 
 * <li>IM_XXX_SAVEFILE: The file which to save a computed result to. This is
 * used for saving the result of a matcher, so that it may be loaded later.
 * Leave empty if you do not want the matching result to be saved. <br>
 * {@link Parameter#IM_BSM_SAVEFILE}, {@link Parameter#IM_ASM_SAVEFILE},
 * {@link Parameter#IM_PSM_SAVEFILE}, {@link Parameter#IM_PSM_SAVEFILE}</li>
 * </ul>
 * </p>
 * 
 * @author Francesco Loprete @date Jan 17th, 2014.
 * @author Cosmin Stroe (cstroe@gmail.com) @date Jan 28th, 2014.
 * 
 */
public class SestCombinationMatchers extends InitialMatchers {

	private static final Logger LOG = Logger.getLogger(SestCombinationMatchers.class);
	
	public SestCombinationMatchers() { super(); }
	
	@Override
	public List<MatchingTask> getComponentMatchers() {
		ArrayList<MatchingTask> l = new ArrayList<>();
		
		l.add(bsm);
		l.add(asm);
		l.add(psm);
		l.add(vmm);
		l.add(lsm);
		l.add(ssc);
		l.add(dsi);
		//l.add(m_iism);
		
		return l;
	}

	private BaseSimilarityParameters    param_bsm;  // the parameters that will be used for the BSM
	private AdvancedSimilarityParameters param_asm; // parameters for ASM
	private ParametricStringParameters 	param_psm;  // the parameters that will be used for the PSM
	private MultiWordsParameters		param_vmm;  // VMM parameters
	private LexicalSynonymMatcherParameters param_lsm; // LSM parameters
	private SiblingsSimilarityContributionParameters param_ssc; //SSC parameter
	private DescendantsSimilarityInheritanceParameters param_dsi; //DSI parameter
	private CombinationParameters		param_lwc;  // LWC params
	//private IterativeInstanceStructuralParameters param_iism;
	
	/********* MATCHERS **********/
	private BaseSimilarityMatcher		m_bsm;
	private AdvancedSimilarityMatcher	m_asm;
	private ParametricStringMatcher		m_psm;
	private MultiWordsMatcher			m_vmm;
	private LexicalSynonymMatcher       m_lsm;
	private SiblingsSimilarityContributionMatcher m_ssc;
	private DescendantsSimilarityInheritanceMatcher m_dsi;
	
	private MatchingTask bsm, asm, psm, vmm, lsm, ssc, dsi;
	
	private CombinationMatcher			m_lwc;
	private CombinationMatcher			m_lwc2;
	//private IterativeInstanceStructuralMatcher m_iism;
	
	private MatchingProgressListener progressDisplay;
		
	private UFLExperimentParameters eparam;
	
	private boolean p = false;
	
	private SelectionAlgorithm selector;
	
	@Override
	public void run(UFLExperiment experiment) {
				
		if( experiment.getSourceOntology() == null || experiment.getTargetOntology() == null ) {
			UIUtility.displayErrorPane("The experiment must define a pair of ontologies before the matching can start.", "Ontologies not loaded");
			return;
		}
		
		this.exp = experiment;
		this.eparam = experiment.setup.parameters;
		
		try {
			progressDisplay = experiment.gui;
			initializeVariables(experiment.getSourceOntology(), experiment.getTargetOntology());
			
			// Run Matchers.  TODO: Run Multiple Threads.
			p = false;
			if( progressDisplay != null ) p = true;	
			if(p) progressDisplay.ignoreComplete(true);

			FluentMatchingTaskRunner runner;
			
			progressDisplay.setProgressLabel("BSM (1/5)");
			exp.info("Running BSM..."); LOG.info("Running BSM...");
			runner = new SimpleMatchingTaskRunner();
			runner.with(param_bsm)
			      .and(m_bsm)
			      .and(new DefaultSelectionParameters())
			      .and(selector)
			      .matching(exp.getSourceOntology(), exp.getTargetOntology());
			runner.run();
			bsm = runner.getTask();
			
			progressDisplay.setProgressLabel("ASM (2/5)");
			exp.info("Running ASM..."); LOG.info("Running ASM...");
			if (m_asm != null) {
				runner = new SimpleMatchingTaskRunner();
				runner.with(param_asm)
				      .and(m_asm)
				      .and(new DefaultSelectionParameters())
				      .and(selector)
				      .matching(exp.getSourceOntology(),  exp.getTargetOntology());
				runner.run();
				asm = runner.getTask();
			}
			
			exp.info("Running PSM..."); LOG.info("Running PSM...");
			progressDisplay.setProgressLabel("PSM (3/5)");
			runner = new SimpleMatchingTaskRunner();
			runner.with(param_psm)
			      .and(m_psm)
			      .and(new DefaultSelectionParameters())
			      .and(selector)
			      .matching(exp.getSourceOntology(),  exp.getTargetOntology());
			runner.run();
			psm = runner.getTask();
			
			exp.info("Running VMM..."); LOG.info("Running VMM...");
			progressDisplay.setProgressLabel("VMM (4/5)");
			runner = new SimpleMatchingTaskRunner();
			runner.with(param_vmm)
			      .and(m_vmm)
			      .and(new DefaultSelectionParameters())
			      .and(selector)
			      .matching(exp.getSourceOntology(),  exp.getTargetOntology());
			runner.run();
			vmm = runner.getTask();
			
			exp.info("Running LSM..."); LOG.info("Running LSM...");
			progressDisplay.setProgressLabel("LSM");
			runner = new SimpleMatchingTaskRunner();
			runner.with(param_lsm)
			      .and(m_lsm)
			      .and(new DefaultSelectionParameters())
			      .and(selector)
			      .matching(exp.getSourceOntology(),  exp.getTargetOntology());
			runner.run();
			lsm = runner.getTask();

			
			runMatcher(m_bsm, Parameter.IM_BSM_LOADFILE, Parameter.IM_BSM_SAVEFILE);
			runMatcher(m_asm, Parameter.IM_ASM_LOADFILE, Parameter.IM_ASM_SAVEFILE);
			runMatcher(m_psm, Parameter.IM_PSM_LOADFILE, Parameter.IM_PSM_SAVEFILE);
			runMatcher(m_vmm, Parameter.IM_VMM_LOADFILE, Parameter.IM_VMM_SAVEFILE);
			runMatcher(m_lsm);
				
			m_lwc.addInputMatcher(m_bsm);
			m_lwc.addInputMatcher(m_asm);
			m_lwc.addInputMatcher(m_psm);
			m_lwc.addInputMatcher(m_vmm);
			m_lwc.addInputMatcher(m_lsm);
			runMatcher(m_lwc);
			
			m_dsi.addInputMatcher(m_lwc);
			runMatcher(m_dsi);
			
			m_ssc.addInputMatcher(m_lwc);
			runMatcher(m_ssc);
				
//			exp.info("Running LWC..."); LOG.info("Running LWC...");
//			progressDisplay.setProgressLabel("LWC (5/5)");
//			m_lwc2.addInputMatcher(m_lwc);
//			m_lwc2.addInputMatcher(m_dsi);
//			m_lwc2.addInputMatcher(m_ssc);
//			m_lwc2.match();
			
//			m_iism.addInputMatcher(m_lwc);
//			m_iism.match();
				
			if(p) progressDisplay.ignoreComplete(false);
			
			done();
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
				
	}
	
	private void runMatcher(AbstractMatcher matcher) throws Exception {
		runMatcher(matcher, null, null);
	}
	
	private void runMatcher(AbstractMatcher matcher, Parameter loadFileParameter, Parameter saveFileParameter) throws Exception {
		exp.info("Running " + matcher.getName() + "...");
		LOG.info("Running " + matcher.getName() + "...");
		if(p) progressDisplay.setProgressLabel(matcher.getName());

		String matcherLoadFile = eparam.getParameter(loadFileParameter);
		MatcherResult result = PersistenceUtility.loadMatcherResult(matcherLoadFile);
		if( result != null ) { 
			result.setSourceOntology(matcher.getSourceOntology());
			result.setTargetOntology(matcher.getTargetOntology());
			matcher.setResult(result);
			
			exp.info("Loaded " + matcher.getName() + " result from: " + matcherLoadFile);
			LOG.info("Loaded " + matcher.getName() + " result from: " + matcherLoadFile);
			return; // if we loaded the result, we don't need to match or save the result.
		}
		
		// we have no result, match
		matcher.match();

		// save the result.		
		if( saveFileParameter != null ) {
			String matcherSaveFile = eparam.getParameter(saveFileParameter);
			if( matcherSaveFile != null ) {
				exp.info("Saving " + matcher.getName() + " result to: " + matcherSaveFile);
				LOG.info("Saving " + matcher.getName() + " result to: " + matcherSaveFile);
				PersistenceUtility.saveMatcherResult(matcher.getResult(), matcherSaveFile);
			}
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
		param_ssc= new SiblingsSimilarityContributionParameters();
		param_dsi=new DescendantsSimilarityInheritanceParameters();
		//param_iism = new IterativeInstanceStructuralParameters();
		
		//threshold
		param_bsm.threshold =
		param_asm.threshold =
		param_psm.threshold =
		param_vmm.threshold =
		param_lsm.threshold =
		param_lwc.threshold = 
		param_dsi.threshold =
		param_ssc.threshold = eparam.getDoubleParameter(Parameter.IM_THRESHOLD);
		
		
		// BSM
		param_bsm.useDictionary = false;
		param_bsm.useProfiling = false;
		param_bsm.useLocalname = true;
		param_bsm.useLabel = true;
		param_bsm.useNorm2 = true;
		param_bsm.useNorm3 = true;
		m_bsm = new BaseSimilarityMatcher(param_bsm);
		m_bsm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)
			m_bsm.addProgressDisplay(progressDisplay);
		
		// ASM
		param_asm.useDictionary = false;
		param_asm.useProfiling = false;
		param_asm.useLabel = true;
		m_asm = new AdvancedSimilarityMatcher(param_asm);
		m_asm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_asm.addProgressDisplay(progressDisplay);
		
		// PSM
		param_psm.initForOAEI2010(OAEI_Track.Benchmarks);  // use the OAEI 2010 settings
		m_psm = new ParametricStringMatcher( param_psm );
		m_psm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_psm.addProgressDisplay(progressDisplay);
		
		// VMM
		try {
			param_vmm.initForOAEI2010(OAEI_Track.Benchmarks); // use the OAEI 2010 settings for this also.
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			param_vmm.initForOAEI2009();
		}  
		m_vmm = new MultiWordsMatcher( param_vmm );
		m_vmm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_vmm.addProgressDisplay(progressDisplay);
		
		param_lsm.useSynonymTerms = false;
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
		
		//DSI
		param_dsi.MCP=0.8;
		m_dsi= new DescendantsSimilarityInheritanceMatcher();
		m_dsi.setParameters(param_dsi);
		m_dsi.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_dsi.addProgressDisplay(progressDisplay);
		
		
		//SSC
		param_ssc.MCP=0.8;
		m_ssc= new SiblingsSimilarityContributionMatcher(param_ssc);
		m_ssc.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)m_ssc.addProgressDisplay(progressDisplay);
		
//		//LWC2
//		m_lwc2 = new CombinationMatcher( param_lwc );
//		m_lwc2.setOntologies(sourceOntology, targetOntology);
//		if (progressDisplay!=null)m_lwc2.addProgressDisplay(progressDisplay);
		
		
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
		
		selector = new MwbmSelection();
	}

	@Override public Alignment<Mapping> getAlignment() { 
		return getFinalMatcher().getAlignment(); }
	
	@Override public Alignment<Mapping> getClassAlignment() { 
		return getFinalMatcher().getClassAlignmentSet(); }
	
	@Override public Alignment<Mapping> getPropertyAlignment() { 
		return getFinalMatcher().getPropertyAlignmentSet(); }
	
	@Override public AbstractMatcher getFinalMatcher() {
		return m_lwc; //return m_lwc2;
	}
}
