package batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ontology.DBPediaKBInstanceDataset;
import ontology.KnowledgeBaseInstanceDataset;

import evaluation.NYTEvaluator;

import am.app.mappingEngine.instanceMatcher.NYTConstants;
import am.app.mappingEngine.instanceMatchers.InstanceMatcherFedeNew;
import am.app.mappingEngine.instanceMatchers.StatementsInstanceMatcher;
import am.app.mappingEngine.instanceMatchers.TokenInstanceMatcher;
import am.app.mappingEngine.instanceMatchers.labelInstanceMatcher.LabelInstanceMatcher;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
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
	InstanceMatcherFedeNew matcher = new InstanceMatcherFedeNew();
	
	Logger log;
	
	double threshold = 0.01;
	
	public IMBatch() {
		log = Logger.getLogger(this.getClass());
	}
	
		
	public String singleFreebaseTest(String sourceFile, String alignmentFile, String referenceFile, double threshold, 
			String cacheFile, String outputFile) throws Exception{
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
		
		log.info("Building source ontology...");
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		log.info("Done");
		Ontology sourceOnt = builder.getOntology();
		
		builder = new OntoTreeBuilder(targetDef);
		builder.build();
		Ontology targetOnt = builder.getOntology();
		
		FreebaseInstanceDataset dataset = (FreebaseInstanceDataset) targetOnt.getInstances();
		dataset.setCacheFile(cacheFile);
		
		matcher.setUseInstanceSchemaMappings(false);
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(referenceFile);
		
		matcher.setReferenceAlignment(refPairs);
				
		matcher.setOutputFile(outputFile);
		
		matcher.match();
		
		dataset.persistCache();
					
		report += NYTEvaluator.evaluate(outputFile, referenceFile, threshold) + "\n";
		return report;
	}
	
	
	public String runGeoNamesTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = ""; 
		
		report += singleGeoNamesTest(cwd + NYTConstants.NYT_LOCATIONS,
				NYTConstants.REF_GEONAMES_LOCATION,
				threshold,
				"geonamesRDFCacheProcessed.ser", NYTConstants.GEONAMES_LOCATION_OUTPUT);
			
		return report;
	}
	
	public String singleGeoNamesTest(String sourceFile, String referenceFile, double threshold, String cacheFile, String outputFile) throws Exception{
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
		
		matcher.setUseInstanceSchemaMappings(false);
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(referenceFile);
		
		matcher.setReferenceAlignment(refPairs);
		
		matcher.setOutputFile(outputFile);
		
		matcher.match();
		
		dataset.persistCache();
		
		report += NYTEvaluator.evaluate("alignment.rdf", referenceFile, threshold) + "\n";
		
		return report;
		
	}
	
	public String dbpediaLocationsTest() throws Exception{
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
	
		String xmlFile = new File(System.getProperty("user.dir")).getParent() + "/Locations/locations.xml";
		
		KnowledgeBaseInstanceDataset instances = new KnowledgeBaseInstanceDataset(xmlFile, "dbp-geo");
		
		targetOnt.setInstances(instances);
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(NYTConstants.REF_DBP_LOCATIONS);
		matcher.setReferenceAlignment(refPairs);
		
		matcher.match();
		
		report += NYTEvaluator.evaluate("alignment.rdf", NYTConstants.REF_DBP_LOCATIONS, threshold) + "\n";
		
		System.out.println(report);
		
		return report;
		
	}
	
	
	public String runFreebaseTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = ""; 
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_LOCATIONS,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_LOCATION,
				threshold,
				"freebaseCacheLocations.ser", NYTConstants.FREEBASE_LOCATIONS_OUTPUT);
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_ORGANIZATIONS_ARTICLES, 
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", 
				NYTConstants.REF_FREEBASE_ORGANIZATION, 
				threshold, 
				"freebaseCacheOrganizations.ser", NYTConstants.FREEBASE_ORGANIZATIONS_OUTPUT);
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_PEOPLE_ARTICLES,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_PEOPLE,
				threshold,
				"freebaseCache.ser", NYTConstants.FREEBASE_LOCATIONS_OUTPUT);
		
		
		
		return report;
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
	
	public String runDBPediaTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = "";
		
		report += singleDBPediaTest(cwd + NYTConstants.NYT_LOCATIONS, 
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
				
		return report;
	}
	
	public String runDBPediaOrganizationsTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = "";
		
		report += singleDBPediaTest(cwd + NYTConstants.NYT_ORGANIZATIONS, 
				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf",
				NYTConstants.REF_DBP_ORGANIZATIONS,
				threshold,
				"dbpediaLocationsProcessed2.ser");	
		
		return report;
	}
	
	public String runFreebaseOrganizationsTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = ""; 
		
		//newFreebaseCacheOrganizationsNoType.ser
		//freebaseCacheOrganizations.ser
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_ORGANIZATIONS, 
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", 
				NYTConstants.REF_FREEBASE_ORGANIZATION, 
				threshold, 
				"freebaseOrgUntypedStopPar.ser", NYTConstants.FREEBASE_ORGANIZATIONS_OUTPUT);
		
		
		return report;
	}
	
	public String runFreebaseLocationsTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = ""; 
		
		//newFreebaseCacheLocationsNoType.ser
		//freebaseCacheLocations.ser
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_LOCATIONS,
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf",
				NYTConstants.REF_FREEBASE_LOCATION,
				threshold,
				"freebaseLocUntypedStopPar.ser", NYTConstants.FREEBASE_LOCATIONS_OUTPUT);
		
		return report;
	}
	
	private String runDBPediaApiTest() throws Exception {
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
	
	private String runDBPediaOnDiskSingleTest(String sourceFile, String alignmentFile, String referenceFile, double threshold, 
			String cacheUriFile, String cacheFile, String outputFile) throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
 		
		OntologyDefinition sourceDef = new OntologyDefinition();
		sourceDef.loadOntology = false;
		sourceDef.loadInstances = true;
		sourceDef.instanceSourceFile = sourceFile;
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
		
		//DBPediaKBInstanceDataset instances = new DBPediaKBInstanceDataset(xmlFile, datasetId);
		DBPediaKBInstanceDataset instances = new DBPediaKBInstanceDataset(cacheFile);
		
		
		instances.setUriCache(cacheUriFile);
		//instances.setCache(cacheFile);
		
		targetOnt.setInstances(instances);
		
		matcher.setSourceOntology(sourceOnt);
		matcher.setTargetOntology(targetOnt);
		matcher.setThreshold(threshold);
		
		List<MatchingPair> refPairs = AlignmentUtilities.getMatchingPairsOAEI(referenceFile);
				
		matcher.setReferenceAlignment(refPairs);
		
		matcher.setOutputFile(outputFile);
		
		matcher.match();
		
		instances.persistUriCache();
		instances.persistCache(cacheFile);
		
		report += NYTEvaluator.evaluate(outputFile, referenceFile, threshold) + "\n";
		
		return report;
		
	}
	
	public void runAllTests() throws Exception{
		Logger.getRootLogger().setLevel(Level.OFF);
				
		String results = "";
		

		results += runDBPediaOnDiskTest();
		results += runFreebaseLocationsTest() + "\n";
		results += runFreebaseOrganizationsTest() + "\n";
		results += runFreebasePeopleTest() + "\n";
		results += runGeoNamesTest() + "\n";
//		runDBPediaTest();
	
//		batch.runDBPediaTest();
		
//		batch.dbpediaLocationsTest();
	
//		batch.runDBPediaOrganizationsTest();
		
		System.out.println(results);
		
	}
	
	

	private String runDBpediaOnDiskLocationsTest() throws Exception{
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = ""; 
		
		report = runDBPediaOnDiskSingleTest(cwd + NYTConstants.NYT_LOCATIONS, 
				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf", 
				NYTConstants.REF_DBP_LOCATIONS, 
				threshold, 
				"dbpLocUriCache.ser", "dbpLocCache.ser", NYTConstants.DBP_LOCATION);
				
		return report;
	}
	
	private String runDBPediaOnDiskTest() throws Exception {
		String results = "";
		results += runDBpediaOnDiskLocationsTest();
		System.out.println(results);
		Runtime.getRuntime().gc();
		results += runDBpediaOnDiskOrganizationsTest();
		Runtime.getRuntime().gc();
		System.out.println(results);		
		results += runDBpediaOnDiskPeopleTest();
		Runtime.getRuntime().gc();
		System.out.println(results);		
		return results;
	}


	public String runDBpediaOnDiskPeopleTest() throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = ""; 
		
		report = runDBPediaOnDiskSingleTest(cwd + NYTConstants.NYT_PEOPLE, 
				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf", 
				NYTConstants.REF_DBP_PEOPLE, 
				threshold, 
				"dbpPeoUriCache.ser", "dbpPeoCache.ser", NYTConstants.DBPEDIA_PEOPLE_OUTPUT);
				
		return report;
	}
	
	public String runDBpediaOnDiskOrganizationsTest() throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
		
		String report = ""; 
		
		report = runDBPediaOnDiskSingleTest(cwd + NYTConstants.NYT_ORGANIZATIONS, 
				cwd + "OAEI2011/NYTMappings/nyt - dbpedia - schema mappings.rdf", 
				NYTConstants.REF_DBP_ORGANIZATIONS, 
				threshold, 
				"dbpOrgUriCache.ser", "dbpOrgCache.ser", NYTConstants.DBPEDIA_ORGANIZATION_OUTPUT);
				
		return report;
	}


	private String runFreebasePeopleTest() throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
		String report = ""; 
		
		//newFreebaseCacheOrganizationsNoType.ser
		//freebaseCacheOrganizations.ser
		
		report += singleFreebaseTest(cwd + NYTConstants.NYT_PEOPLE, 
				cwd + "OAEI2011/NYTMappings/nyt - freebase - schema mappings.rdf", 
				NYTConstants.REF_FREEBASE_PEOPLE, 
				threshold, 
				"freebasePeopleUntypedStopPar.ser", NYTConstants.FREEBASE_PEOPLE_OUTPUT);
		
		return report;
	}

	public static void main(String[] args) throws Exception {
		String cwd = System.getProperty("user.dir") + File.separator;
		
		//Logger.getLogger(DBPediaKBInstanceDataset.class).setLevel(Level.DEBUG);
		//Logger.getLogger(InstanceMatcherFedeNew.class).setLevel(Level.DEBUG);
		//Logger.getLogger(StatementsInstanceMatcher.class).setLevel(Level.DEBUG);
		//Logger.getLogger(LabelInstanceMatcher.class).setLevel(Level.DEBUG);
		//Logger.getLogger(TokenInstanceMatcher.class).setLevel(Level.DEBUG);
		
		
		IMBatch batch = new IMBatch();

//		Logger.getLogger(TokenInstanceMatcher.class).setLevel(Level.DEBUG);		
		
//		batch.runAllTests();
	
//		batch.runGeoNamesTest();
	
		batch.runDBPediaOnDiskTest();
	
		
//		batch.runFreebaseOrganizationsTest();
		
//		batch.runFreebasePeopleTest();
		
//		batch.runFreebaseLocationsTest();
		
//		batch.runFreebaseTest();
		
//		batch.runGeoNamesTest();
	
//		batch.runDBPediaTest();
		
//		batch.dbpediaLocationsTest();
	
//		batch.runDBPediaOrganizationsTest();
	
//		batch.runDBpediaOnDiskLocationsTest();
		
		//System.out.println(batch.runDBPediaApiTest());
	}

	
}
