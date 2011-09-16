package batch;

import java.io.File;

import evaluation.NYTEvaluator;

import am.app.mappingEngine.InstanceMatcherFede;
import am.app.mappingEngine.instanceMatcher.NYTConstants;
import am.app.ontology.Ontology;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.instance.endpoint.EndpointRegistry;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;

public class IMBatch {	
	
	public void singleTest(String sourceFile, String alignmentFile, String referenceFile, double threshold) throws Exception{
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
		
		InstanceMatcherFede matcher = new InstanceMatcherFede();
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		matcher.match();
		
		NYTEvaluator.evaluate("alignment.rdf", referenceFile);
		
	}
	
	public static void main(String[] args) throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
		//new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 0.8);
//		new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 1.0);
//		new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 1.1);
//		new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 1.2);
//		
		
		new IMBatch().singleTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_PEOPLE, 0.8);
//	
		
//		double start = 0.8;
//		double increment = 0.1;
//		
//		for (int i = 0; i < 7; i++) {
//			new IMBatch().singleTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_PEOPLE, start + i*increment);
//		}
		
		//new IMBatch().singleTest(cwd + NYTConstants.NYT_ORGANIZATIONS, cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", NYTConstants.REF_FREEBASE_ORGANIZATION, 2);
		
	}

}
