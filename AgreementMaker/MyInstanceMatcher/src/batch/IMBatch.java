package batch;

import java.io.File;

import evaluation.NYTEvaluator;

import am.app.mappingEngine.InstanceMatcherFede;
import am.app.mappingEngine.instanceMatcher.NYTConstants;
import am.app.ontology.Ontology;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.instance.FreebaseInstanceDataset;
import am.app.ontology.instance.endpoint.EndpointRegistry;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;

public class IMBatch {	
	String report = "";
		
	public void singleTest(String sourceFile, String alignmentFile, String referenceFile, double threshold, String cacheFile) throws Exception{
 		OntologyDefinition sourceDef = new OntologyDefinition();
		sourceDef.loadOntology = false;
		sourceDef.loadInstances = true;
		sourceDef.instanceSourceFile = sourceFile;
		sourceDef.instanceSource = DatasetType.DATASET;
		sourceDef.instanceSourceFormat = 0;
		sourceDef.loadSchemaAlignment = true;
		sourceDef.schemaAlignmentURI = alignmentFile;
		sourceDef.schemaAlignmentFormat = 0;
		sourceDef.sourceOrTarget = Ontology.SOURCE;
		
		OntologyDefinition targetDef = new OntologyDefinition();
		targetDef.loadOntology = false;
		targetDef.loadInstances = true;
		targetDef.instanceSource = DatasetType.ENDPOINT;
		targetDef.instanceEndpointType = EndpointRegistry.FREEBASE;
		targetDef.sourceOrTarget = Ontology.TARGET;
		
		System.out.println("Building source ontology...");
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		System.out.println("Done");
		Ontology sourceOnt = builder.getOntology();
		
		builder = new OntoTreeBuilder(targetDef);
		builder.build();
		Ontology targetOnt = builder.getOntology();
		
		FreebaseInstanceDataset dataset = (FreebaseInstanceDataset) targetOnt.getInstances();
		dataset.setCacheFile(cacheFile);
		
		InstanceMatcherFede matcher = new InstanceMatcherFede();
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		matcher.match();
		
		report += NYTEvaluator.evaluate("alignment.rdf", referenceFile) + "\n";
		
	}
	
	public void runFreebaseTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		double threshold = 0.65;
		
		singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, 
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", 
				NYTConstants.REF_FREEBASE_ORGANIZATION, 
				threshold, 
				"freebaseCacheOrganizations.ser");
		
		singleTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_PEOPLE,
				threshold,
				"freebaseCache.ser");
		
		singleTest(cwd + NYTConstants.NYT_LOCATIONS,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_LOCATION,
				threshold,
				"freebaseCacheLocations.ser");
	}
	
	public static void main(String[] args) throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
		
//		batch.singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, 
//				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", 
//				NYTConstants.REF_FREEBASE_ORGANIZATION, 
//				0.65, 
//				"freebaseCacheOrganizations.ser");
//		new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 1.0);
//		new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 1.1);
//		new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 1.2);
//		
		
		IMBatch batch = new IMBatch();
		
		batch.singleTest(cwd + NYTConstants.NYT_LOCATIONS,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_LOCATION,
				0.65,
				"freebaseCacheLocations.ser");
		
		//batch.runFreebaseTest();
		
		
		//batch.singleTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_PEOPLE, 0.65, "freebaseCache.ser");
		
		System.out.println(batch.report);

//		double start = 0.6;
//		double increment = 0.05;	
//		for (int i = 0; i < 10; i++) {
//			new IMBatch().singleTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_PEOPLE, start + i*increment);
//		}
		
		//new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 2);
		
	}

}
