package am.app.mappingEngine.ontology.ontologyParser;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import am.app.Core;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.InstanceFormat;

public class OAEI2013TurtleDatasetTests {

	private static String root;
	
	private static OntologyDefinition defOriginal; // ontology definition for the 2013 original instance set
	
	private static OntologyDefinition[] defTestcases; // ontology definition for the 2013 original instance set
	
	private static Logger LOG = Logger.getLogger(OAEI2013TurtleDatasetTests.class);
	
	@BeforeClass
	public static void setupRoot() {
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
	
	@Test
	@Ignore("RDF files are not available")
	public void loadOriginal() throws Exception {
		
		OntoTreeBuilder builder = new OntoTreeBuilder(defOriginal);
		builder.build();
		
		InstanceDataset instances = builder.getInstances();
		
		assertNotNull(instances);
	}
	
	/**
	 * This test makes sure we can properly load all the RDFT_DATASET_2013 contest test cases.
	 */
	@Test
	@Ignore("RDF files are not available")
	public void loadTestCases() throws Exception {
		for( int i = 0; i < defTestcases.length; i++ ) {
			LOG.info("Loading " + defTestcases[i].instanceSourceFile);
			OntoTreeBuilder builder = new OntoTreeBuilder(defTestcases[i]);
			builder.build();
			InstanceDataset instances = builder.getInstances();
			assertNotNull(instances);
		}
	}

}
