package am.extension.userfeedback.preset;

import java.io.File;
import java.io.FileFilter;

import am.app.Core;

public class PresetUtilities {

	public final static String BENCHMARKS_DIR = "OAEI/2013/benchmarks";
	public final static String ANATOMY_DIR = "OAEI/2013/anatomy";
	
	public final static String ANATOMY_TASK_NAME = "Anatomy";
	
	// the number of the target ontology will be appened to this name.
	// for example, Task 101-304 will be called BENCHMARK_TASK_BASENAME + "304".
	public final static String BENCHMARK_TASK_BASENAME = "Benchmark ";
	
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
	
	public static void main(String[] args) {
		PresetUtilities.createAllMatchingTasks();
	}
}
