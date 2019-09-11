package am.extension.userfeedback.preset;

import static am.Utility.fileExists;

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
/**
 * This class is for creating UFL experiment batch files.
 * 
 * @author Cosmin Stroe (cstroe@gmail.com)
 *
 */
public class PresetUtilities {
	
	public static final String ANATOMY_DIR            = "OAEI/2013/anatomy";
	public static final String BENCHMARKS_DIR         = "OAEI/2013/benchmarks";
	public static final String BENCHMARKS_FILTERED_RA = "UFL/FilteredAlignments";

	/**
	 * @return The OAEI 2013 Anatomy Track (stored in AM_ROOT).
	 */
	public static List<MatchingTaskPreset> createAnatomyTask() {
		final String root = Core.getInstance().getRoot();
		final String ANATOMY_TASK_NAME = "Anatomy";
		
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
	
	/**
	 * @param basename
	 *            The base name for the experiment names. The number of the
	 *            target ontology will be appended to this name. For example,
	 *            Task 101-304 will be called basename + "304".
	 * @return The set of the OAEI 2013 Benchmarks tasks.
	 */
	public static List<MatchingTaskPreset> createBenchmarkTasksAll(String basename) {
		return createBenchmarkTasksAll(basename, BENCHMARKS_DIR, BENCHMARKS_DIR, null);
	}
	
	/**
	 * @param basename
	 *            The base name for the experiment names. The number of the
	 *            target ontology will be appended to this name. For example,
	 *            Task 101-304 will be called basename + "304".
	 * @return The 301 - 304 Benchmarks test cases from OAEI 2013, using a
	 *         filtered reference alignment (subclass mappings removed).
	 */
	public static List<MatchingTaskPreset> createBenchmarkTasks30xFileredRA(String basename) {
		// which directories we accept
		final Set<String> whiteList = new HashSet<>();
		whiteList.add("301");
		whiteList.add("302");
		whiteList.add("303");
		whiteList.add("304");
		
		return createBenchmarkTasksAll(basename, BENCHMARKS_DIR, BENCHMARKS_FILTERED_RA, whiteList);
	}
	
	private static List<MatchingTaskPreset> createBenchmarkTasksAll(String basename, String BENCHMARKS_DIR, String REFERENCE_DIR, final Set<String> directoryWhiteList) {
		final String root = Core.getInstance().getRoot();
		final File benchmarksDir = new File(root + BENCHMARKS_DIR);
		
		if( !benchmarksDir.exists() ) {
			throw new AssertionError("Directory does not exist.", new FileNotFoundException(benchmarksDir.toString()));
		}
		
		// List all subdirectories in the benchmarks directory.
		File[] benchmarkTestCases =  benchmarksDir.listFiles(new FileFilter() {
			@Override public boolean accept(File pathname) {
				boolean isDir = pathname.isDirectory();
				
				if( isDir && directoryWhiteList == null ) 
					return true; 
				
				if( isDir && directoryWhiteList.contains(pathname.getName()) )
					return true;
				
				return false;
			}			
		});
		
		// sort by name
		Arrays.sort(benchmarkTestCases, new Comparator<File>() {
			@Override public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}
		});
		
		List<MatchingTaskPreset> tasks = new LinkedList<>();
		
		String sourceFile = root + BENCHMARKS_DIR + File.separator + "101" + File.separator + "onto.rdf";
		if( !fileExists(sourceFile) ) 
			throw new AssertionError("Source ontology does not exist.", new FileNotFoundException(sourceFile));
		
		for( File testCase : benchmarkTestCases ) 
		{
			String targetFile = root + BENCHMARKS_DIR + File.separator + testCase.getName() + File.separator + "onto.rdf";
			if( !fileExists(targetFile) ) 
				throw new AssertionError("Target ontology does not exist.", new FileNotFoundException(targetFile));
			
			String referenceAlignment = root + REFERENCE_DIR + File.separator + testCase.getName() + File.separator + "refalign.rdf";
			if( !fileExists(referenceAlignment) ) 
				throw new AssertionError("Reference alignment does not exist.", new FileNotFoundException(referenceAlignment));

			MatchingTaskPreset currentTask = new MatchingTaskPreset(
					basename + testCase.getName(),
					sourceFile,
					targetFile,
					referenceAlignment);
			
			tasks.add(currentTask);
		}
		return tasks;
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
		
		setup.im  = InitialMatcherRegistry.SemanticStructuralCombination;
		setup.fli = LoopInizializationRegistry.ServerDataInizialization;
		setup.cs  = CandidateSelectionRegistry.ServerMultiStrategy;
		setup.cse = CSEvaluationRegistry.MultiplexCSE;
		setup.uv  = UserValidationRegistry.PESimulatedClient;
		setup.fa  = FeedbackAggregationRegistry.ServerFeedbackAggregation;
		setup.fp  = FeedbackPropagationRegistry.ServerFeedbackPropagation;
		setup.pe  = PropagationEvaluationRegistry.ServerPropagationEvaluation;
		setup.sf  = SaveFeedbackRegistry.MultiUserSaveFeedback;
		
		setup.parameters = new UFLExperimentParameters();
		
		// default parameters
		setup.parameters.setIntParameter(		Parameter.NUM_USERS, 10);
		setup.parameters.setIntParameter(		Parameter.NUM_ITERATIONS, 100);
		setup.parameters.setDoubleParameter(    Parameter.IM_THRESHOLD, 0.4);
		
		return setup;
	}

	
	/**
	 * Create the batch mode runs from a list of tasks and experiments.
	 */
	public static List<Pair<MatchingTaskPreset,ExperimentPreset>> createRuns(List<MatchingTaskPreset> tasks, List<ExperimentPreset> experiments) {
		List<Pair<MatchingTaskPreset,ExperimentPreset>> runs = new LinkedList<>();
			
		for( MatchingTaskPreset mtp : tasks ) {
			for( ExperimentPreset exppset : experiments ) {
				final String logFile = "settings/tmp/uflLog." + mtp.getName() +  "." + exppset.getName() + ".txt";
				try {
					ExperimentPreset exp = exppset.clone();
					exp.getExperimentSetup().parameters.setParameter(Parameter.LOGFILE, logFile);
					runs.add( new Pair<MatchingTaskPreset, ExperimentPreset>(mtp.clone(), exp) );
				} catch (CloneNotSupportedException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return runs;
	}
	
	
	
	
	public static void main(String[] args) {
		// create the matching tasks
		List<MatchingTaskPreset> tasks = createBenchmarkTasks30xFileredRA("Benchmark ");
		List<MatchingTaskPreset> oneTask = new LinkedList<>();
		oneTask.add(tasks.get(0));
		List<ExperimentPreset> experiments = createExperimentsBaseline();
		List<ExperimentPreset> oneExp = new LinkedList<>();
		oneExp.add(experiments.get(0));
		List<Pair<MatchingTaskPreset,ExperimentPreset>> runs = createRuns(oneTask,oneExp);
		
		PresetStorage.saveBatchModeRunsToXML(runs, "UFL/experiments/301-static-cs.xml");
	}
}
