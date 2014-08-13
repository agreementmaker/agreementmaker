package am.extension.userfeedback.analysis;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import am.Utility;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.extension.multiUserFeedback.MatchingTasks2014;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.clustering.disagreement.SestCombinationMatchers;
import am.extension.userfeedback.experiments.UFLExperimentParameters;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.utility.referenceAlignment.AlignmentUtilities;

import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

/**
 * Analysis of the Initial Matchers.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class InitialMatcherAnalysis extends AnalysisBase {
	private static final Logger LOG = LogManager.getLogger(InitialMatcherAnalysis.class);
	
	/**
	 * How much room for improvement do we have from the initial matchers?
	 */
	
	@Test
	public void analyzeMatchingTasksInitialMatchers() {
		// setup a basic log4j configuration that logs to the console
		setupLogging();
	    
		// silence all the other loggers
		silenceNoisyLoggers(noisyLoggers);
		RDFDefaultErrorHandler.silent = true;
		
		LOG.info("Analysis of initial matchers at different thresholds for each test case.");
		LOG.info("The _noSC suffix indicates that subclass/superclass mappings were filtered from the reference alignment.");
		LOG.info("The conference tasks reference alignments contained no subclass/superclass mappings.");
		LOG.info("");
		LOG.info("LEGEND:");
		LOG.info("    th    : Threshold for mapping selection for the initial matchers.");
		LOG.info("    corr  : Number of correct mappings found by the initial matchers.");
		LOG.info("    found : Number of total mappings found by the initial matchers.");
		LOG.info("    ref   : Number of mappings in reference alignment.");
		LOG.info("    FP    : Number of false positive mappings (found - corr).");
		LOG.info("    FN    : Number of false negative (missed) mappings (ref - corr).");
		LOG.info("    totInc: Total number of incorrect mappings (FP + FN).");
		LOG.info("    Prec  : Precision.");
		LOG.info("    Rec   : Recall.");
		LOG.info("    FMeas : FMeasure.");
		LOG.info("");
		LOG.info("Experiment\t\tth\tcorr\tfound\tref\tFP\tFN\ttotInc\tPrec\tRec\tFMeas");
		LOG.info("");
		
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks301_noSC, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks302_noSC, 0.001);
		LOG.info("");
		LOG.info(" Experiment\t\tth\tcorr\tfound\tref\tFP\tFN\ttotInc\tPrec\tRec\tFMeas");
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks303_noSC, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 1.0);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.9);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.8);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.7);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.6);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.4);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.2);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.1);
		analyzeSestMatcher(MatchingTasks2014.benchmarks304_noSC, 0.001);
		LOG.info("");
		LOG.info(" Experiment\t\tth\tcorr\tfound\tref\tFP\tFN\ttotInc\tPrec\tRec\tFMeas");
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 1.0);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.9);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.8);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.7);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.6);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.4);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.2);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.1);
		analyzeSestMatcher(MatchingTasks2014.conferenceEdasIasted, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 1.0);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.9);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.8);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.7);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.6);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.4);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.2);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.1);
		analyzeSestMatcher(MatchingTasks2014.conferenceEkawIasted, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 1.0);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.9);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.8);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.7);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.6);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.4);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.2);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.1);
		analyzeSestMatcher(MatchingTasks2014.conferenceCmtEkaw, 0.001);
		LOG.info("");
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 1.0);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.9);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.8);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.7);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.6);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.4);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.2);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.1);
		analyzeSestMatcher(MatchingTasks2014.conferenceConfOfEkaw, 0.001);
	}

	private void analyzeSestMatcher(MatchingTaskPreset p, double threshold) {
		
		UFLExperimentSetup setup = new UFLExperimentSetup();
		
		setup.parameters = new UFLExperimentParameters();
		setup.parameters.setDoubleParameter(Parameter.IM_THRESHOLD, threshold);
		setup.parameters.setIntParameter(Parameter.NUM_ITERATIONS, 100);
		
		Ontology sourceOnt = OntoTreeBuilder.loadOWLOntology(p.getSourceOntology());
		Ontology targetOnt = OntoTreeBuilder.loadOWLOntology(p.getTargetOntology());
		
		MUExperiment exp = new MUExperiment(setup);
		exp.setSourceOntology(sourceOnt);
		exp.setTargetOntology(targetOnt);
		
		SestCombinationMatchers sest = new SestCombinationMatchers();
		sest.run(exp);
		
		Alignment<Mapping> initialAlignment = sest.getAlignment();
		Alignment<Mapping> referenceAlignment = AlignmentUtilities.getOAEIAlignment(p.getReference(), sourceOnt, targetOnt);
		
		ReferenceEvaluationData eval = ReferenceEvaluator.compare(initialAlignment, referenceAlignment);
		
		int falsePositiveMappings = eval.getFound() - eval.getCorrect();
		int falseNegativeMappings = eval.getExist() - eval.getCorrect();
		int totWrong = falsePositiveMappings + falseNegativeMappings;
		
		String initialTab = "\t";
		if( p.getName().length() <= "ConferenceEkawIa".length() ) initialTab = "\t\t";
		
		LOG.info(p.getName() + 
				initialTab + threshold + "\t" + eval.getCorrect() + "\t" + eval.getFound() + "\t" + eval.getExist() + 
				"\t" + falsePositiveMappings + "\t" + falseNegativeMappings + "\t" + totWrong + 
				"\t" + Utility.getOneDecimalPercentFromDouble(eval.getPrecision()) + 
				"\t" + Utility.getOneDecimalPercentFromDouble(eval.getRecall()) + 
				"\t" + Utility.getOneDecimalPercentFromDouble(eval.getFmeasure()));
		
		assertEquals(falsePositiveMappings, eval.getFound() - eval.getCorrect());
		assertEquals(falseNegativeMappings, eval.getExist() - eval.getCorrect());
	} 
}
