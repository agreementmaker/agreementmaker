package am.extension.userfeedback.clustering.disagreement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import am.Utility;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.mappingEngine.utility.OAEI_Track;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.extension.userfeedback.ExecutionSemantics;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralMatcher;
import am.matcher.IterativeInstanceStructuralMatcher.IterativeInstanceStructuralParameters;
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

/**
 * The orthogonal combination matcher.  Used as the first step in the new
 * User Feedback Loop.
 * 
 * @author Cosmin Stroe - Jan 29th, 2011.
 *
 */
public class OrthoCombinationMatcher extends ExecutionSemantics {

	public OrthoCombinationMatcher() { super(); }
	
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
	
	private UFLExperiment experiment;
	
	@Override
	public void run(UFLExperiment experiment) {
				
		if( experiment.getSourceOntology() == null || experiment.getTargetOntology() == null ) {
			Utility.displayErrorPane("The experiment must define a pair of ontologies before the matching can start.", "Ontologies not loaded");
			return;
		}
		
		this.experiment = experiment;
		
		try {
			
			progressDisplay = experiment.gui;
			initializeVariables(experiment.getSourceOntology(), experiment.getTargetOntology());
			
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
				
				progressDisplay.setProgressLabel("LSM");
				m_lsm.match();
				
				progressDisplay.setProgressLabel("LWC (5/5)");
				m_lwc.addInputMatcher(m_bsm);
				m_lwc.addInputMatcher(m_asm);
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
						param_psm.threshold=
								param_vmm.threshold=
										param_lsm.threshold=
										param_lwc.threshold=0.4;
		
		
		// BSM
		param_bsm.useDictionary = false;
		m_bsm = new BaseSimilarityMatcher(param_bsm);
		m_bsm.setOntologies(sourceOntology, targetOntology);
		if (progressDisplay!=null)
			m_bsm.addProgressDisplay(progressDisplay);
		
		// ASM
		param_asm.initForOAEI2009();
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
	
	@Override
	protected void done() {
		
		//Logger log = Logger.getLogger(this.getClass().toString());
		
		UFLExperiment log = experiment;
		
		
		
		// output the reference alignment
		Alignment<Mapping> referenceAlignment = experiment.getReferenceAlignment();
		
		//FIXME: We should not be looking at the reference alignment here.
		if( referenceAlignment != null ) {
			log.info("Referene alignment has " + referenceAlignment.size() + " mappings.");
			for( int i = 0; i < referenceAlignment.size(); i++ ) {
				Mapping currentMapping = referenceAlignment.get(i);
				log.info( i + ". " + currentMapping.toString() );
			}
			
			log.info("");
		}
		
		// save to log file the alignment we start with.
		
		Alignment<Mapping> finalAlignment = getFinalMatcher().getAlignment();
		Alignment<Mapping> classAlignment = getFinalMatcher().getClassAlignmentSet();
		Alignment<Mapping> propertiesAlignment = getFinalMatcher().getPropertyAlignmentSet();
		
		log.info("Initial matchers have finished running.");
		log.info("Alignment contains " + finalAlignment.size() + " mappings. " + 
				  classAlignment.size() + " class mappings, " + propertiesAlignment.size() + " property mappings.");
		
		log.info("Class mappings:");
		for( int i = 0; i < classAlignment.size(); i++ ) {
			Mapping currentMapping = classAlignment.get(i);
			boolean mappingCorrect = false;
			
			if( referenceAlignment != null && 
					referenceAlignment.contains(currentMapping.getEntity1(),currentMapping.getEntity2(), currentMapping.getRelation()) ) {
				mappingCorrect = true;
			}
			
			String mappingAnnotation = "X";
			if( mappingCorrect || referenceAlignment == null ) mappingAnnotation = " ";
			
			log.info( i + ". " + mappingAnnotation + " " + currentMapping.toString() );
		}
		
		log.info("");
		
		log.info("Property mappings:");
		for( int i = 0; i < propertiesAlignment.size(); i++ ) {
			Mapping currentMapping = propertiesAlignment.get(i);
			boolean mappingCorrect = false;
			
			if( referenceAlignment != null && 
					referenceAlignment.contains(currentMapping.getEntity1(), currentMapping.getEntity2(), currentMapping.getRelation()) ) {
				mappingCorrect = true;
			}
			
			String mappingAnnotation = "X";
			if( mappingCorrect || referenceAlignment == null ) mappingAnnotation = " ";
			
			log.info( i + ". " + mappingAnnotation + " " + currentMapping.toString() );
		}
		
		log.info("");
		
		if( referenceAlignment != null ) {
			log.info("Missed mappings:");
			int missedMappingNumber = 0;
			for( Mapping referenceMapping : referenceAlignment ) {
				if( !finalAlignment.contains(referenceMapping.getEntity1(), referenceMapping.getEntity2(), referenceMapping.getRelation()) ) {
					log.info( missedMappingNumber + ". " + referenceMapping );
					missedMappingNumber++;
				}
			}
			
			log.info("");
		}
		
		super.done();
	}
}
