package am.app.mappingEngine.ontology.ontologyParser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.BeforeClass;
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
	
	private static OntologyDefinition defTestcase01; // ontology definition for the 2013 original instance set
	
	@BeforeClass
	public static void setupRoot() {
		root = Core.getInstance().getRoot();
		
		defOriginal = new OntologyDefinition(false, null, null, null);
		defOriginal.loadInstances = true;
		defOriginal.instanceSourceType = DatasetType.DATASET;
		defOriginal.instanceSourceFormat = InstanceFormat.TURTLE;
		defOriginal.instanceSourceFile = root + File.separator + "InstanceMatching/IMEI/2013/RDFT_DATASET_2013/original.rdf";
		
		defTestcase01 = new OntologyDefinition(false, null, null, null);
		defTestcase01.loadInstances = true;
		defTestcase01.instanceSourceType = DatasetType.DATASET;
		defTestcase01.instanceSourceFormat = InstanceFormat.TURTLE;
		defTestcase01.instanceSourceFile = root + File.separator + "InstanceMatching/IMEI/2013/RDFT_DATASET_2013/contest/testcase01/contest/contest.rdf";
	}
	
	@Test
	public void loadOriginal() throws Exception {
		
		OntoTreeBuilder builder = new OntoTreeBuilder(defOriginal);
		builder.build();
		
		InstanceDataset instances = builder.getInstances();
		
		assertNotNull(instances);
	}
	
	@Test
	public void loadTestCase01() throws Exception {
		OntoTreeBuilder builder = new OntoTreeBuilder(defTestcase01);
		builder.build();
		
		InstanceDataset instances = builder.getInstances();
		
		assertNotNull(instances);
		
	}

}
