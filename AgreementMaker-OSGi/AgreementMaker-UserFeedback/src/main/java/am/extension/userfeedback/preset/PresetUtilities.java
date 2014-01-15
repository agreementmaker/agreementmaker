package am.extension.userfeedback.preset;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.MatchingTask;
import am.extension.multiUserFeedback.propagation.ServerFeedbackPropagation;
import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackAggregationRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;
import am.extension.userfeedback.experiments.UFLExperimentParameters;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.utility.Pair;

public class PresetUtilities {

	public final static String BENCHMARKS_DIR = "OAEI/2013/benchmarks";
	public final static String ANATOMY_DIR = "OAEI/2013/anatomy";
	
	public final static String ANATOMY_TASK_NAME = "Anatomy";
	
	// the number of the target ontology will be appened to this name.
	// for example, Task 101-304 will be called BENCHMARK_TASK_BASENAME + "304".
	public final static String BENCHMARK_TASK_BASENAME = "Benchmark ";
	
	public static final String[] EXPERIMENT = new String[] { 
		"00 - ER=5% RR=0% CS=static, PE=none",
		"01 - ER=5% RR=0% CS=static, PE=euzero",
		"02 - ER=5% RR=0% CS=static, PE=log",
		"03 - ER=5% RR=0% CS=static, PE=regression",
		
		"04 - ER=5% RR=0% CS=dynamic, PE=none",
		"05 - ER=5% RR=0% CS=dynamic, PE=euzero",
		"06 - ER=5% RR=0% CS=dynamic, PE=log",
		"07 - ER=5% RR=0% CS=dynamic PE=regression",
		
		"08 - ER=5% RR=5% CS=dynamic, PE=none",
		"09 - ER=5% RR=5% CS=dynamic, PE=euzero",
		"10 - ER=5% RR=5% CS=dynamic, PE=log",
		"11 - ER=5% RR=5% CS=dynamic, PE=regression",
		
		"12 - ER=5% RR=10% CS=dynamic, PE=none",
		"13 - ER=5% RR=10% CS=dynamic, PE=euzero",
		"14 - ER=5% RR=10% CS=dynamic, PE=log",
		"15 - ER=5% RR=10% CS=dynamic, PE=regression" };
	
	public static void createAllMatchingTasks() {
		createAnatomyTask();
		createBenchmarkTasks();
	}

	private static void createAnatomyTask() {
		final String root = Core.getInstance().getRoot();
		MatchingTaskPreset anatomy = 
				new MatchingTaskPreset(
						ANATOMY_TASK_NAME, 
						root + ANATOMY_DIR + File.separator + "mouse.owl", 
						root + ANATOMY_DIR + File.separator + "human.owl",
						root + ANATOMY_DIR + File.separator + "reference.rdf");
		
		PresetStorage.addMatchingTaskPreset(anatomy);
	}
	
	private static void createBenchmarkTasks() {
		final String root = Core.getInstance().getRoot();
		
		File benchmarksDir = new File(root + BENCHMARKS_DIR);
		
		// List all subdirectories in the benchmarks directory.
		File[] benchmarkTestCases =  benchmarksDir.listFiles(new FileFilter() {
			@Override public boolean accept(File pathname) {
				return pathname.isDirectory();
			}			
		});
		
		for( File testCase : benchmarkTestCases ) {
			MatchingTaskPreset currentTask = new MatchingTaskPreset(
					BENCHMARK_TASK_BASENAME + testCase.getName(),
					root + BENCHMARKS_DIR + File.separator + "101" + File.separator + "onto.rdf",
					root + BENCHMARKS_DIR + File.separator + testCase.getName() + File.separator + "onto.rdf",
					root + BENCHMARKS_DIR + File.separator + testCase.getName() + File.separator + "refalign.rdf");
			
			PresetStorage.addMatchingTaskPreset(currentTask);
		}
		
	}
	
	public static void createExperiments() {

		int i = 0;
		
		{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}
		
		
		{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}
		
		{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.05d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.05d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.05d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.05d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}
		
		{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.1d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.1d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.1d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.1d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			PresetStorage.addExperimentPreset(exp);
		}{
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.im = InitialMatcherRegistry.LargeOrthoCombination;
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, 0d);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, 0.1d);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset("LargeOntology", setup);
			PresetStorage.addExperimentPreset(exp);
		}
		
		
		
	}
	
	
	private static UFLExperimentSetup createEmptyUFLSetup()
	{
		UFLExperimentSetup setup = new UFLExperimentSetup();
		
		setup.exp = ExperimentRegistry.ServerExperiment;
		
		setup.im  = InitialMatcherRegistry.OrthoCombination;
		setup.fli = LoopInizializationRegistry.ServerDataInizialization;
		setup.cs  = CandidateSelectionRegistry.ServerMultiStrategy;
		setup.cse = CSEvaluationRegistry.PrecisionRecallEval;
		setup.uv  = UserValidationRegistry.FakeClient;
		setup.fa  = FeedbackAggregationRegistry.ServerFeedbackAggregation;
		setup.fp  = FeedbackPropagationRegistry.ServerFeedbackPropagation;
		setup.pe  = PropagationEvaluationRegistry.ServerPropagationEvaluation;
		setup.sf  = SaveFeedbackRegistry.MultiUserSaveFeedback;
		
		setup.parameters = new UFLExperimentParameters();
		
		// default parameters
		setup.parameters.setIntParameter(		Parameter.NUM_USERS, 10);
		setup.parameters.setIntParameter(		Parameter.NUM_ITERATIONS, 100);
		
		return setup;
	}

	
	private static void createRunFile() {
		// create the runs
		List<Pair<MatchingTaskPreset,ExperimentPreset>> runs = new LinkedList<>();
		
		List<MatchingTaskPreset> tasks = new LinkedList<>();
		tasks.add( PresetStorage.getMatchingTaskPreset(BENCHMARK_TASK_BASENAME + "301") );
		tasks.add( PresetStorage.getMatchingTaskPreset(BENCHMARK_TASK_BASENAME + "302") );
		tasks.add( PresetStorage.getMatchingTaskPreset(BENCHMARK_TASK_BASENAME + "303") );
		tasks.add( PresetStorage.getMatchingTaskPreset(BENCHMARK_TASK_BASENAME + "304") );
		
		for( MatchingTaskPreset mtp : tasks ) {
			for( String exp : EXPERIMENT ) {
				ExperimentPreset exppset = PresetStorage.getExperimentPreset(exp);
				final String[] mtNameSplit = mtp.getName().split(" ");
				final String[] expNameSplit = exppset.getName().split(" ");
				final String logFile = "settings/tmp/uflLog.baseline." + mtNameSplit[1] + "." + expNameSplit[0] + ".txt"; 
				exppset.getExperimentSetup().parameters.setParameter(Parameter.LOGFILE, logFile);
				runs.add( new Pair<MatchingTaskPreset, ExperimentPreset>(mtp, exppset) );
			}
		}
		
		PresetStorage.saveBatchModeRuns(runs, "ESWC2014-Benchmark-Baseline.runs.bz2");
	}
	
	public static void main(String[] args) {
		// create the matching tasks
		PresetStorage.resetMatchingTaskPresets();
		PresetUtilities.createAllMatchingTasks();
		
		// create the experiment setups
		PresetStorage.resetExperimentPresets();
		PresetUtilities.createExperiments();
		
		PresetUtilities.createRunFile();
	}
}
