package am.matcher.oaei.imei2013;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.utility.MatchingPair;
import am.app.ontology.Ontology;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.InstanceFormat;
import am.matcher.lod.instanceMatchers.InstanceMatcherFedeNew;
import am.matcher.lod.instanceMatchers.InstanceMatcherFedeNewParameters;
import am.utility.RunTimer;

public class InstanceMatching {

	private static Logger LOG = Logger.getLogger(InstanceMatching.class);
	
	private String root;
	private OntologyDefinition defOriginal; // ontology definition for the 2013 original instance set
	private OntologyDefinition[] defTestcases; // ontology definition for the 2013 original instance set 
	
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
		
		for( int i = 0; i < defTestcases.length; i++ ) {
			defTestcases[i] = new OntologyDefinition(false, null, null, null);
			defTestcases[i].loadInstances = true;
			defTestcases[i].instanceSourceType = DatasetType.DATASET;
			defTestcases[i].instanceSourceFormat = InstanceFormat.TURTLE;
			defTestcases[i].instanceSourceFile = 
					root + File.separator + "InstanceMatching/IMEI/2013/RDFT_DATASET_2013/contest/testcase0" + (i+1) + "/contest/contest.rdf";
		}
		
	}
	
	public void runTest01() throws Exception {
		
		RunTimer timer = new RunTimer().start();
		
		LOG.info("Loading " + defOriginal.instanceSourceFile);
		Ontology sourceOnt = loadOntology(defOriginal);
		
		LOG.info("Loading " + defTestcases[0].instanceSourceFile);
		Ontology targetOnt = loadOntology(defTestcases[0]);
		
		
		InstanceMatcherFedeNewParameters params = new InstanceMatcherFedeNewParameters();
		params.threshold = 0.01;
		params.outputFilename = System.getProperty("java.io.tmpdir") + File.separator + "testcase01-output.rdf";
		
		InstanceMatcherFedeNew im = new InstanceMatcherFedeNew(params);
		
		im.setSourceOntology(sourceOnt);
		im.setTargetOntology(targetOnt);
		
		im.match();
		
		List<MatchingPair> matchingPairs = im.getInstanceAlignment();
		timer.stop();
		
		LOG.info("Matching completed in: " + timer.getFormattedRunTime());
		LOG.info("Found " + matchingPairs.size() + " instance mappings.");
		LOG.info(matchingPairs);
				
		if( im.getInstanceMatchingReport() != null ) {
			LOG.info(im.getInstanceMatchingReport().printTable());
		}
	}
	
	private Ontology loadOntology(OntologyDefinition def) throws Exception {
		OntoTreeBuilder builder = new OntoTreeBuilder(def);
		builder.build();
		return builder.getOntology();
	}
	
	
}
