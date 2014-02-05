package am.extension.userfeedback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import am.app.Core;
import am.extension.multiUserFeedback.evaluation.ServerFeedbackEvaluation;
import am.extension.multiUserFeedback.evaluation.ServerFeedbackEvaluation.ServerFeedbackEvaluationData;
import am.extension.userfeedback.common.UFLExperimentRunner;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.preset.ExperimentPreset;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.extension.userfeedback.preset.PresetStorage;
import am.utility.Pair;

import com.thoughtworks.xstream.XStream;

public class UFLTests {

	private static final Logger LOG = LogManager.getLogger(UFLTests.class);

	private static final String ADVIS_AVG = "UFL/experiments/ADVIS_AVG_strategy.xml";
	
	private static final String BM301_CS_STATIC = "UFL/experiments/301-static-cs.xml";
	private static final String BM302_CS_STATIC = "UFL/experiments/302-static-cs.xml";
	private static final String BM303_CS_STATIC = "UFL/experiments/303-static-cs.xml";
	private static final String BM304_CS_STATIC = "UFL/experiments/304-static-cs.xml";
	
	private static final String BM301_CS_STATIC_DATA = "UFL/experiments/UnitTestData/301-static-cs.data.xml";
	private static final String BM302_CS_STATIC_DATA = "UFL/experiments/UnitTestData/302-static-cs.data.xml";
	private static final String BM303_CS_STATIC_DATA = "UFL/experiments/UnitTestData/303-static-cs.data.xml";
	private static final String BM304_CS_STATIC_DATA = "UFL/experiments/UnitTestData/304-static-cs.data.xml";
	
	private static final boolean CREATE_FILES = false;
	
	@Test
	public void testBenchmarks301() {
		String root = Core.getInstance().getRoot();
		if(CREATE_FILES) createDataFile(root, BM301_CS_STATIC, BM301_CS_STATIC_DATA);
		else checkDataFile(root, BM301_CS_STATIC, BM301_CS_STATIC_DATA);
	}
	
	@Test
	public void testBenchmarks302() {
		String root = Core.getInstance().getRoot();
		if(CREATE_FILES) createDataFile(root, BM302_CS_STATIC, BM302_CS_STATIC_DATA);
		else checkDataFile(root, BM302_CS_STATIC, BM302_CS_STATIC_DATA);
	}
	
	@Test
	public void testBenchmarks303() {
		String root = Core.getInstance().getRoot();
		if(CREATE_FILES) createDataFile(root, BM303_CS_STATIC, BM303_CS_STATIC_DATA);
		else checkDataFile(root, BM303_CS_STATIC, BM303_CS_STATIC_DATA);
	}
	
	@Test
	public void testBenchmarks304() {
		String root = Core.getInstance().getRoot();
		if(CREATE_FILES) createDataFile(root, BM304_CS_STATIC, BM304_CS_STATIC_DATA);
		else checkDataFile(root, BM304_CS_STATIC, BM304_CS_STATIC_DATA);
	}
	
	/**
	 * This method should be used when creating a new data file, which will be
	 * unit tested against later.
	 */
	public void createDataFile(String root, String experimentFile, String dataFile) {
		List<Pair<MatchingTaskPreset, ExperimentPreset>> runs = 
				PresetStorage.loadBatchModeRunsFromXML(root + experimentFile);
		
		assertEquals(1, runs.size());
		
		Pair<MatchingTaskPreset, ExperimentPreset> run = runs.get(0);
		
		UFLExperimentRunner runner = new UFLExperimentRunner(run);
		runner.run();
		
		@SuppressWarnings("rawtypes")
		PropagationEvaluation pe = runner.getExperiment().propagationEvaluation;
		
		assertTrue( (pe instanceof ServerFeedbackEvaluation) );
		
		ServerFeedbackEvaluationData data = ((ServerFeedbackEvaluation) pe).getData();
		
		XStream xs = new XStream();
		
		try {
			String fileName = root + dataFile;
			FileOutputStream fos = new FileOutputStream(fileName);
			xs.toXML(data,fos);
			fos.close();
			LOG.info("Saved experiment data: " + fileName);
		}
		catch (IOException ioex) {
			LOG.error(ioex);
			fail();
		}
	}
	
	public void checkDataFile(String root, String experimentFile, String dataFile) {
		List<Pair<MatchingTaskPreset, ExperimentPreset>> runs = 
				PresetStorage.loadBatchModeRunsFromXML(root + experimentFile);
		
		assertEquals(1, runs.size());
		
		Pair<MatchingTaskPreset, ExperimentPreset> run = runs.get(0);
		
		UFLExperimentRunner runner = new UFLExperimentRunner(run);
		runner.run();
		
		@SuppressWarnings("rawtypes")
		PropagationEvaluation pe = runner.getExperiment().propagationEvaluation;
		
		assertTrue( (pe instanceof ServerFeedbackEvaluation) );
		
		ServerFeedbackEvaluationData data = ((ServerFeedbackEvaluation) pe).getData();
		
		XStream xs = new XStream();
		
		ServerFeedbackEvaluationData testData = null;
		
		try {
			String fileName = root + dataFile;
			File inputFile = new File(fileName);
			assertTrue(inputFile.exists());
			testData = (ServerFeedbackEvaluationData) xs.fromXML(inputFile);
		}
		catch (Exception ioex) {
			LOG.error(ioex);
			fail();
		}
		
		assertTrue(data.equals(testData));
	}
}
