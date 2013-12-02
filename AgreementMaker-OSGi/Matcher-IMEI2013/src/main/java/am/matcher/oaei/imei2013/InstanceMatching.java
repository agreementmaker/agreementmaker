package am.matcher.oaei.imei2013;

import java.io.File;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.InstanceFormat;
import am.matcher.lod.instanceMatchers.InstanceMatcherFedeNew;
import am.matcher.lod.instanceMatchers.InstanceMatcherFedeNewParameters;

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
		
		
		LOG.info("Loading " + defOriginal.instanceSourceFile);
		InstanceDataset sourceInstances = loadInstances(defOriginal);
		
		LOG.info("Loading " + defTestcases[0].instanceSourceFile);
		InstanceDataset targetInstances = loadInstances(defTestcases[0]);
		
		
		InstanceMatcherFedeNewParameters params = new InstanceMatcherFedeNewParameters();
		params.outputFilename = null;
		
	}
	
	private InstanceDataset loadInstances(OntologyDefinition def) throws Exception {
		OntoTreeBuilder builder = new OntoTreeBuilder(def);
		builder.build();
		return builder.getInstances();
	}
	
	
}
