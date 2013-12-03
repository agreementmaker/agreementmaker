package am.matcher.oaei.imei2013;

import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.MatchingPairAlignment;
import am.app.ontology.Ontology;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.InstanceFormat;
import am.matcher.lod.instanceMatchers.InstanceMatcherFedeNew;
import am.matcher.lod.instanceMatchers.InstanceMatcherFedeNewParameters;
import am.output.alignment.oaei.OAEIAlignmentFormat;
import am.output.alignment.oaei.TSVAlignmentFormat;
import am.utility.RunTimer;

public class InstanceMatching {

	private static Logger LOG = Logger.getLogger(InstanceMatching.class);
	
	private String root;
	private OntologyDefinition defOriginal; // ontology definition for the 2013 original instance set
	private OntologyDefinition[] defTestcases; // ontology definition for the 2013 original instance set 
	private String[] refAlignments; // the reference alignments for the test cases.
	
	public InstanceMatching() {
		setupRoot();
	}
	
	private void setupRoot() {
		root = Core.getInstance().getRoot();
		
		defOriginal = new OntologyDefinition(false, null, null, null);
		defOriginal.loadInstances = true;
		defOriginal.instanceSourceType = DatasetType.DATASET;
		defOriginal.instanceSourceFormat = InstanceFormat.TURTLE;
		defOriginal.instanceSourceFile = root + File.separator + "InstanceMatching/IMEI/2013/RDFT_DATASET_2013/original.rdf";
		
		defTestcases = new OntologyDefinition[5];
		refAlignments = new String[5];
		
		for( int i = 0; i < defTestcases.length; i++ ) {
			defTestcases[i] = new OntologyDefinition(false, null, null, null);
			defTestcases[i].loadInstances = true;
			defTestcases[i].instanceSourceType = DatasetType.DATASET;
			defTestcases[i].instanceSourceFormat = InstanceFormat.TURTLE;
			defTestcases[i].instanceSourceFile = 
					root + File.separator + "InstanceMatching/IMEI/2013/RDFT_DATASET_2013/contest/testcase0" + (i+1) + "/contest/contest.rdf";
			refAlignments[i] =
					root + File.separator + "InstanceMatching/IMEI/2013/RDFT_DATASET_2013/contest/testcase0" + (i+1) + "/contest/testcase0" + (i+1) + "_reference.tsv";
		}
		
	}
	
	/**
	 * 
	 * @param testNum 1 to 5 (there are 5 tests)
	 * @throws Exception
	 */
	public void runTest(int testNum) throws Exception {
		if( testNum < 1 || testNum > 5 ) throw new RuntimeException("Invalid testNum: " + testNum);
		
		LOG.info("Running IMEI 2013 Testcase 0" + testNum);
		
		RunTimer timer = new RunTimer().start();
		
		LOG.info("Loading " + defOriginal.instanceSourceFile);
		Ontology sourceOnt = loadOntology(defOriginal);
		
		LOG.info("Loading " + defTestcases[testNum-1].instanceSourceFile);
		Ontology targetOnt = loadOntology(defTestcases[testNum-1]);
		
		
		InstanceMatcherFedeNewParameters params = new InstanceMatcherFedeNewParameters();
		params.threshold = 0.01;
		params.outputFilename = System.getProperty("java.io.tmpdir") + File.separator + "testcase0" + testNum + "-output.rdf";
		
		InstanceMatcherFedeNew im = new InstanceMatcherFedeNew(params);
		
		im.setSourceOntology(sourceOnt);
		im.setTargetOntology(targetOnt);
		
		im.match();
		
		MatchingPairAlignment computedAlignment = im.getInstanceAlignment();
		timer.stop();
		
		LOG.info("Matching completed in: " + timer.getFormattedRunTime());
		LOG.info("Found " + computedAlignment.size() + " instance mappings.");
		LOG.info(computedAlignment);
				
		if( im.getInstanceMatchingReport() != null ) {
			LOG.info(im.getInstanceMatchingReport().printTable());
		}
		
		evalTest(testNum, params.outputFilename);
	}
	
	/**
	 * 
	 * @param testNum 1 - 5 (there are 5 tests)
	 * @param alignmentFile The alignment output for this test.
	 * @throws Exception
	 */
	public void evalTest(int testNum, String alignmentFile) throws Exception {
		if( testNum < 1 || testNum > 5 ) throw new RuntimeException("Invalid testNum: " + testNum);
		
		MatchingPairAlignment computedAlignment = 
				OAEIAlignmentFormat.readAlignmentToMatchingPairAlignment(new FileReader(new File(alignmentFile)));
		
		TSVAlignmentFormat refReader = new TSVAlignmentFormat(null, null);
		MatchingPairAlignment referenceAlignment = refReader.readMatchingPairs(new FileReader(new File(refAlignments[testNum-1])));
		
		int intersection = referenceAlignment.intersection(computedAlignment);
		double prec = intersection / (double)computedAlignment.size(); 
		double rec = intersection / (double)referenceAlignment.size();
		double fmeas = (2 * prec * rec) / (prec + rec);
		
		LOG.info("Testcase " + testNum);
		LOG.info("Precision: \t" + intersection + " / " + computedAlignment.size() + " = " + Utility.getOneDecimalPercentFromDouble(prec));	
		LOG.info("Recall:    \t" + intersection + " / " + referenceAlignment.size() + " = " + Utility.getOneDecimalPercentFromDouble(rec));
		LOG.info("F-Measure: \t" + Utility.getOneDecimalPercentFromDouble(fmeas));
	}
	
	private Ontology loadOntology(OntologyDefinition def) throws Exception {
		OntoTreeBuilder builder = new OntoTreeBuilder(def);
		builder.build();
		return builder.getOntology();
	}
}
