package batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import evaluation.NYTEvaluator;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.BaseInstanceMatcher;
import am.app.mappingEngine.InstanceMatcherFede;
import am.app.mappingEngine.instanceMatcher.NYTConstants;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.ontology.Ontology;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.instance.DBPediaApiInstanceDataset;
import am.app.ontology.instance.FreebaseInstanceDataset;
import am.app.ontology.instance.GeoNamesInstanceDataset;
import am.app.ontology.instance.SparqlInstanceDataset;
import am.app.ontology.instance.endpoint.EndpointRegistry;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.utility.referenceAlignment.AlignmentUtilities;

public class IMBatch {	
	String report = "";
		
	//AbstractMatcher matcher = new BaseInstanceMatcher();
	AbstractMatcher matcher = new InstanceMatcherFede();
	
	
	public String singleFreebaseTest(String sourceFile, String alignmentFile, String referenceFile, double threshold, String cacheFile) throws Exception{
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
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(referenceFile);
		
		matcher.setReferenceAlignment(refPairs);
				
		matcher.match();
						
		report += NYTEvaluator.evaluate("alignment.rdf", referenceFile, threshold) + "\n";
		return report;
	}
	
	
	public void runGeoNamesTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		double threshold = 0.55;
		
		String report = ""; 
		
		report += singleGeoNamesTest(cwd + NYTConstants.NYT_LOCATIONS_ARTICLES,
				NYTConstants.REF_GEONAMES_LOCATION,
				threshold,
				"geonamesRDFCacheProcessed.ser");
			
		System.out.println(report);
	}
	
	public String singleGeoNamesTest(String sourceFile, String referenceFile, double threshold, String cacheFile) throws Exception{
 		OntologyDefinition sourceDef = new OntologyDefinition();
		sourceDef.loadOntology = false;
		sourceDef.loadInstances = true;
		sourceDef.instanceSourceFile = sourceFile;
		sourceDef.instanceSource = DatasetType.DATASET;
		sourceDef.instanceSourceFormat = 0;
		sourceDef.sourceOrTarget = Ontology.SOURCE;
		
		OntologyDefinition targetDef = new OntologyDefinition();
		targetDef.loadOntology = false;
		targetDef.loadInstances = true;
		targetDef.instanceSource = DatasetType.ENDPOINT;
		targetDef.instanceEndpointType = EndpointRegistry.GEONAMES;
		targetDef.sourceOrTarget = Ontology.TARGET;
		
		System.out.println("Building source ontology...");
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		System.out.println("Done");
		Ontology sourceOnt = builder.getOntology();
		
		builder = new OntoTreeBuilder(targetDef);
		builder.build();
		Ontology targetOnt = builder.getOntology();
		
		GeoNamesInstanceDataset dataset = (GeoNamesInstanceDataset) targetOnt.getInstances();
		dataset.setCacheFile(cacheFile);
		
		InstanceMatcherFede matcher = new InstanceMatcherFede();
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		matcher.match();
		
		report += NYTEvaluator.evaluate("alignment.rdf", referenceFile, threshold) + "\n";
		
		return report;
		
	}
	
	public String dbpediaLocationsTest() throws Exception{
 		double threshold = 0.5;
 		
 		String cwd = System.getProperty("user.dir") + File.separator;
				
		OntologyDefinition sourceDef = new OntologyDefinition();
		sourceDef.loadOntology = false;
		sourceDef.loadInstances = true;
		sourceDef.instanceSourceFile = cwd + NYTConstants.NYT_LOCATIONS;
		sourceDef.instanceSource = DatasetType.DATASET;
		sourceDef.instanceSourceFormat = 0;
		sourceDef.sourceOrTarget = Ontology.SOURCE;
		
		System.out.println("Building source ontology...");
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		System.out.println("Done");
		Ontology sourceOnt = builder.getOntology();
		
		Ontology targetOnt = new Ontology();
	
		KnowledgeBaseInstanceDataset instances = new KnowledgeBaseInstanceDataset("locations.xml", "dbp_geocoordinates");
		
		targetOnt.setInstances(instances);
		
		InstanceMatcherFede matcher = new InstanceMatcherFede();
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		matcher.match();
		
		//report += NYTEvaluator.evaluate("alignment.rdf", referenceFile, threshold) + "\n";
		
		return report;
		
	}
	
	
	public void runFreebaseTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		double threshold = 0.55;
		
		String report = ""; 
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_LOCATIONS,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_LOCATION,
				threshold,
				"freebaseCacheLocations.ser");
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, 
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", 
				NYTConstants.REF_FREEBASE_ORGANIZATION, 
				threshold, 
				"freebaseCacheOrganizations.ser");
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_PEOPLE,
				threshold,
				"freebaseCache.ser");
		
		
		
		System.out.println(report);
	}
	
	
	public String singleDBPediaTest(String sourceFile, String alignmentFile, String referenceFile, double threshold, String cacheFile) throws Exception{
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
		targetDef.instanceSourceFile = "http://dbpedia.org/sparql";
		targetDef.instanceEndpointType = EndpointRegistry.SPARQL;
		targetDef.sourceOrTarget = Ontology.TARGET;
		
		System.out.println("Building source ontology...");
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		System.out.println("Done");
		Ontology sourceOnt = builder.getOntology();
		
		builder = new OntoTreeBuilder(targetDef);
		builder.build();
		Ontology targetOnt = builder.getOntology();
		
		SparqlInstanceDataset dataset = (SparqlInstanceDataset) targetOnt.getInstances();
		dataset.setCacheFile(cacheFile);
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(referenceFile);
		
		matcher.setReferenceAlignment(refPairs);
		
		matcher.match();
		
		report += "Threshold\tPrecision\tRecall\tFmeasure\n";
		
		report += NYTEvaluator.evaluate("alignment.rdf", referenceFile, threshold) + "\n";
		
		return report;
	}
	
	public void runDBPediaTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		double threshold = 0.5;
		
		String report = "";
		
		report += singleDBPediaTest(cwd + NYTConstants.NYT_LOCATIONS_ARTICLES, 
				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf",
				NYTConstants.REF_DBP_LOCATIONS,
				threshold,
				"dbpediaLocationsRDFCache10000.ser");	
		
//		report += singleDBPediaTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES, 
//				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf",
//				NYTConstants.REF_DBP_PEOPLE,
//				threshold,
//				"dbpediaPeopleRDFCache.ser");	

//		report += singleDBPediaTest(cMaxHitswd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, 
//				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf",
//				NYTConstants.REF_DBP_ORGANIZATIONS,
//				threshold,
//				"dbpediaOrganizationsRDFCache.ser");	
		
//		singleDBPediaTest(cwd + NYTConstants.NYT_LOCATIONS, 
//				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf",
//				NYTConstants.REF_DBP_LOCATIONS,
//				threshold,
//				"dbpediaLocationsRDFCache10000.ser");	
				
		System.out.println(report);
	}
	
	public void runDBPediaOrganizationsTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		double threshold = 0.5;
		
		String report = "";
		
		report += singleDBPediaTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, 
				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf",
				NYTConstants.REF_DBP_ORGANIZATIONS,
				threshold,
				"dbpediaLocationsProcessed2.ser");	
		
		System.out.println(report);
	}
	
	public void runFreebaseOrganizationsTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		double threshold = 0.55;
		
		String report = ""; 
		
		//newFreebaseCacheOrganizationsNoType.ser
		//freebaseCacheOrganizations.ser
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, 
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", 
				NYTConstants.REF_FREEBASE_ORGANIZATION, 
				threshold, 
				"newFreebaseCacheOrganizationsNoType.ser");
		
		
		System.out.println(report);
	}
	
	public void runFreebaseLocationsTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		double threshold = 0.55;
		
		String report = ""; 
		
		//newFreebaseCacheLocationsNoType.ser
		//freebaseCacheLocations.ser
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_LOCATIONS,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_LOCATION,
				threshold,
				"freebaseCacheLocations.ser");
		
		//System.out.println(report);
	}
	
	private String runDBPediaApiTest() throws Exception {
		double threshold = 0.5;
		
		String cwd = System.getProperty("user.dir") + File.separator;
 		
		//String alignmentFile = "OAEI2011/NYTMappings/nyt - dbpediaapi - schema mappings.rdf";
		String referenceFile = cwd + NYTConstants.REF_DBP_ORGANIZATIONS;
		
				
		OntologyDefinition sourceDef = new OntologyDefinition();
		sourceDef.loadOntology = false;
		sourceDef.loadInstances = true;
		sourceDef.instanceSourceFile = cwd + NYTConstants.NYT_ORGANIZATIONS;
		sourceDef.instanceSource = DatasetType.DATASET;
		sourceDef.instanceSourceFormat = 0;
		sourceDef.loadSchemaAlignment = true;
		//sourceDef.schemaAlignmentURI = alignmentFile;
		sourceDef.schemaAlignmentFormat = 0;
		sourceDef.sourceOrTarget = Ontology.SOURCE;
		
		System.out.println("Building source ontology...");
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		System.out.println("Done");
		Ontology sourceOnt = builder.getOntology();
		
		Ontology targetOnt = new Ontology();
	
		DBPediaApiInstanceDataset instances = new DBPediaApiInstanceDataset();
		
		targetOnt.setInstances(instances);
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(referenceFile);
				
		matcher.setReferenceAlignment(refPairs);
		
		matcher.match();
		
		instances.persistCache();
		
		report += NYTEvaluator.evaluate("alignment.rdf", referenceFile, threshold) + "\n";
		
		return report;
		
	}
	
	private String runDBPediaOnDiskTest() throws Exception {
		double threshold = 0.5;
		
		String cwd = System.getProperty("user.dir") + File.separator;
 		
		//String alignmentFile = "OAEI2011/NYTMappings/nyt - dbpediaapi - schema mappings.rdf";
		String referenceFile = cwd + NYTConstants.REF_DBP_LOCATIONS;
		
				
		OntologyDefinition sourceDef = new OntologyDefinition();
		sourceDef.loadOntology = false;
		sourceDef.loadInstances = true;
		sourceDef.instanceSourceFile = cwd + NYTConstants.NYT_LOCATIONS;
		sourceDef.instanceSource = DatasetType.DATASET;
		sourceDef.instanceSourceFormat = 0;
		sourceDef.loadSchemaAlignment = true;
		//sourceDef.schemaAlignmentURI = alignmentFile;
		sourceDef.schemaAlignmentFormat = 0;
		sourceDef.sourceOrTarget = Ontology.SOURCE;
		
		System.out.println("Building source ontology...");
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		System.out.println("Done");
		Ontology sourceOnt = builder.getOntology();
		
		Ontology targetOnt = new Ontology();
	
		String xmlFile = new File(System.getProperty("user.dir")).getParent() + "/Datasets/dbpedia.xml";
		String datasetId = "dbp_labels";
		
		DBPediaKBInstanceDataset instances = new DBPediaKBInstanceDataset(xmlFile, datasetId);
		
		targetOnt.setInstances(instances);
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(referenceFile);
				
		matcher.setReferenceAlignment(refPairs);
		
		matcher.match();
		
		//instances.persistCache();
		
		report += NYTEvaluator.evaluate("alignment.rdf", referenceFile, threshold) + "\n";
		
		return report;
		
	}
	
	public static void main(String[] args) throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
		
		IMBatch batch = new IMBatch();

		batch.runFreebaseOrganizationsTest();
		
//		batch.runFreebaseTest();
		
//		batch.runGeoNamesTest();
	
//		batch.runDBPediaTest();
		
//		batch.dbpediaLocationsTest();
	
//		batch.runDBPediaOrganizationsTest();
	
//		System.out.println(batch.runDBPediaOnDiskTest());
		
		//System.out.println(batch.runDBPediaApiTest());
	}

}
