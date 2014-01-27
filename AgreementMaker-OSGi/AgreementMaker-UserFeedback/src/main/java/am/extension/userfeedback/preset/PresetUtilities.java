package am.extension.userfeedback.preset;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import am.app.Core;
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
	public final static String BENCHMARKS_FILTERED_RA = "UFL/FilteredAlignments";
	public final static String ANATOMY_DIR = "OAEI/2013/anatomy";

	public final static String ANATOMY_TASK_NAME = "Anatomy";
	
	// the number of the target ontology will be appened to this name.
	// for example, Task 101-304 will be called BENCHMARK_TASK_BASENAME + "304".
	public final static String BENCHMARK_TASK_BASENAME = "Benchmark ";
	
	public static String[] EXPERIMENT2 = new String[42]; 
	
	public static void createAllMatchingTasks() {
		//createAnatomyTask();
		//createBenchmarkTasksAll();
		createBenchmarkTasks30xFileredRA();
	}

	public static List<MatchingTaskPreset> createAnatomyTask() {
		final String root = Core.getInstance().getRoot();
		MatchingTaskPreset anatomy = 
				new MatchingTaskPreset(
						ANATOMY_TASK_NAME, 
						root + ANATOMY_DIR + File.separator + "mouse.owl", 
						root + ANATOMY_DIR + File.separator + "human.owl",
						root + ANATOMY_DIR + File.separator + "reference.rdf");
		
		List<MatchingTaskPreset> tasks = new LinkedList<>();
		tasks.add(anatomy);
		return tasks;
	}
	
	public static List<MatchingTaskPreset> createBenchmarkTasksAll() {
		final String root = Core.getInstance().getRoot();
		
		File benchmarksDir = new File(root + BENCHMARKS_DIR);
		
		if( !benchmarksDir.exists() ) {
			throw new AssertionError("Directory does not exist.", new FileNotFoundException(benchmarksDir.toString()));
		}
		
		// List all subdirectories in the benchmarks directory.
		File[] benchmarkTestCases =  benchmarksDir.listFiles(new FileFilter() {
			@Override public boolean accept(File pathname) {
				return pathname.isDirectory();
			}			
		});
		
		List<MatchingTaskPreset> tasks = new LinkedList<>();
		
		for( File testCase : benchmarkTestCases ) {
			MatchingTaskPreset currentTask = new MatchingTaskPreset(
					BENCHMARK_TASK_BASENAME + testCase.getName(),
					root + BENCHMARKS_DIR + File.separator + "101" + File.separator + "onto.rdf",
					root + BENCHMARKS_DIR + File.separator + testCase.getName() + File.separator + "onto.rdf",
					root + BENCHMARKS_FILTERED_RA + File.separator + testCase.getName() + File.separator + "refalign.rdf");
			
			tasks.add(currentTask);
		}
		return tasks;
	}
	
	private static List<MatchingTaskPreset> createBenchmarkTasks30xFileredRA() {
		final String root = Core.getInstance().getRoot();
		
		File benchmarksDir = new File(root + BENCHMARKS_DIR);
		
		if( !benchmarksDir.exists() ) {
			throw new AssertionError("Directory does not exist.", new FileNotFoundException(benchmarksDir.toString()));
		}
		
		// which directories we accept
		final Set<String> whiteList = new HashSet<>();
		whiteList.add("301");
		whiteList.add("302");
		whiteList.add("303");
		whiteList.add("304");
		
		// List all sub-directories in the benchmarks directory.
		File[] benchmarkTestCases =  benchmarksDir.listFiles(new FileFilter() {
			@Override public boolean accept(File pathname) {
				return pathname.isDirectory() && whiteList.contains(pathname.getName());
			}			
		});
		
		Arrays.sort(benchmarkTestCases, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}
			
		});
		
		List<MatchingTaskPreset> presets = new LinkedList<>();
		for( File testCase : benchmarkTestCases ) {
			MatchingTaskPreset currentTask = new MatchingTaskPreset(
					BENCHMARK_TASK_BASENAME + testCase.getName(),
					benchmarksDir + File.separator + "101" + File.separator + "onto.rdf",
					benchmarksDir + File.separator + testCase.getName() + File.separator + "onto.rdf",
					root + BENCHMARKS_FILTERED_RA + File.separator + testCase.getName() + File.separator + "refalign.rdf");
			
			presets.add(currentTask);
		}
		
		return presets;
	}
	
	public static List<ExperimentPreset> createExperiments2() {

		List<ExperimentPreset> runs = new LinkedList<>();
		
		int i = 0;
//		for (int z=0;z<25;z++)
//		{
			i=0;
			for(int k=0;k<6;k++)
				for (int j=0;j<7;j++)
				{
					double er=j*0.05;
					double rr=k*0.1;
					UFLExperimentSetup setup = createEmptyUFLSetup();
					setup.parameters.setIntParameter(Parameter.NUM_ITERATIONS, 100);
					setup.parameters.setIntParameter(Parameter.NUM_USERS, 10);
					setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, er);
					setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, rr);
					setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, false);
					setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
					ExperimentPreset exp = new ExperimentPreset(ServerFeedbackPropagation.PROPAGATION_NONE+"."+(i%7)+"."+(int)(er*100)+"."+(int)(rr*100), setup);
					runs.add(exp);
					i++;
				}
//		}
			return runs;
	}
	
	public static List<ExperimentPreset> createExperimentsBaseline() {

		String[] EXPERIMENT = new String[] { 
			"00.ER=10% RR=0% CS=static, PE=none",
			"01.ER=10% RR=0% CS=static, PE=euzero",
			"02.ER=10% RR=0% CS=static, PE=log",
			"03.ER=10% RR=0% CS=static, PE=regression",
			
			"04.ER=10% RR=20% CS=dynamic, PE=none",
			"05.ER=10% RR=20% CS=dynamic, PE=euzero",
			"06.ER=10% RR=20% CS=dynamic, PE=log",
			"07.ER=10% RR=20% CS=dynamic PE=regression",
			
			"08.ER=10% RR=40% CS=dynamic, PE=none",
			"09.ER=10% RR=40% CS=dynamic, PE=euzero",
			"10.ER=10% RR=40% CS=dynamic, PE=log",
			"11.ER=10% RR=40% CS=dynamic, PE=regression" };
		
		for(int i = 0; i < EXPERIMENT.length; i++) {
			EXPERIMENT[i] = Integer.toString(i); 
		}
		
		final double error_rate = 0.1;
		final double[] revalidation_rates = {0.0, 0.2, 0.4};
		
		int i = 0;
		List<ExperimentPreset> experiments = new LinkedList<>();
		
		{   // 00
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[0]);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 01
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[0]);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 02
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[0]);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 03
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[0]);
			setup.parameters.setBooleanParameter(	Parameter.STATIC_CANDIDATE_SELECTION, true);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}
		
		
		{   // 04
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[1]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 05
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[1]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 06
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[1]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 07
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[1]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}
		
		{   // 08
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[2]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 09
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[2]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 10
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[2]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}{  // 11
			UFLExperimentSetup setup = createEmptyUFLSetup();
			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rates[2]);
			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
			experiments.add(exp);
		}
		
//		{   // 12
//			UFLExperimentSetup setup = createEmptyUFLSetup();
//			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
//			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rate);
//			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_NONE);
//			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
//			PresetStorage.addExperimentPreset(exp);
//		}{  // 13
//			UFLExperimentSetup setup = createEmptyUFLSetup();
//			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
//			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rate);
//			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_EUCLIDEAN);
//			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
//			PresetStorage.addExperimentPreset(exp);
//		}{  // 14
//			UFLExperimentSetup setup = createEmptyUFLSetup();
//			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
//			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rate);
//			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_LOG);
//			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
//			PresetStorage.addExperimentPreset(exp);
//		}{  // 15
//			UFLExperimentSetup setup = createEmptyUFLSetup();
//			setup.parameters.setDoubleParameter(	Parameter.ERROR_RATE, error_rate);
//			setup.parameters.setDoubleParameter(	Parameter.REVALIDATION_RATE, revalidation_rate);
//			setup.parameters.setParameter(			Parameter.PROPAGATION_METHOD, ServerFeedbackPropagation.PROPAGATION_REGRESSION);
//			ExperimentPreset exp = new ExperimentPreset(EXPERIMENT[i++], setup);
//			PresetStorage.addExperimentPreset(exp);
//		}
		
		return experiments;
		
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

	
	/**
	 * Create the batch mode runs from a list of tasks and experiments.
	 */
	private static List<Pair<MatchingTaskPreset,ExperimentPreset>> createRuns(List<MatchingTaskPreset> tasks, List<ExperimentPreset> experiments) {
		List<Pair<MatchingTaskPreset,ExperimentPreset>> runs = new LinkedList<>();
			
		for( MatchingTaskPreset mtp : tasks ) {
			for( ExperimentPreset exppset : experiments ) {
				final String logFile = "settings/tmp/uflLog." + mtp.getName() +  "." + exppset.getName() + ".txt";
				try {
					ExperimentPreset exp = exppset.clone();
					exp.getExperimentSetup().parameters.setParameter(Parameter.LOGFILE, logFile);
					runs.add( new Pair<MatchingTaskPreset, ExperimentPreset>(mtp.clone(), exp) );
				} catch (CloneNotSupportedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		return runs;
	}
	
	
	
	
	public static void main(String[] args) {
		// create the matching tasks
		List<MatchingTaskPreset> tasks = createBenchmarkTasks30xFileredRA();
		List<ExperimentPreset> experiments = createExperimentsBaseline();
		List<Pair<MatchingTaskPreset,ExperimentPreset>> runs = createRuns(tasks,experiments);
		
		PresetStorage.saveBatchModeRunsToXML(runs, "UFL/experiments/baseline.xml");
		runs = PresetStorage.loadBatchModeRunsFromXML("UFL/experiments/baseline.xml");
		PresetStorage.saveBatchModeRunsToXML(runs, "UFL/experiments/baseline-copy.xml");
	}
}
