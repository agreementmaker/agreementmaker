package am.app.mappingEngine.instanceMatcher;

public class NYTConstants {
	
		//Property URIs
		public static String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
		public static String NYT_TOPICPAGE = "http://data.nytimes.com/elements/topicPage";
		public static String FOAF_NAME = "http://xmlns.com/foaf/0.1/name";
		public static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";
		public static String SKOS_PREFLABEL = "http://www.w3.org/2004/02/skos/core#prefLabel";
		
		public static String NYT_URI = "http://data.nytimes.com/elements/";
		public static String orgKeywordsURI = "http://data.nytimes.com/elements/organizationKeywords";
		public static String peopleKeywordsURI = "http://data.nytimes.com/elements/peopleKeywords";
		public static String desKeywordsURI = "http://data.nytimes.com/elements/descriptionKeywords";
		public static String hasArticleURI = "http://data.nytimes.com/elements/hasArticle";
		public static String titleURI = "http://data.nytimes.com/elements/title";
		public static String variantsNumberURI = "http://data.nytimes.com/elements/number_of_variants";
		
		
		//Datasets on disk
		public static String NYT_PEOPLE = "OAEI2011/NYTDatasets/peopleKeywords.rdf";
		public static String NYT_PEOPLE_ARTICLES = "OAEI2011/NYTDatasets/peopleArticles.rdf";
		public static String NYT_LOCATIONS = "OAEI2011/NYTDatasets/locationsKeywords.rdf";
		public static String NYT_LOCATIONS_ARTICLES = "OAEI2011/NYTDatasets/locationsArticles.rdf";
		public static String NYT_ORGANIZATIONS = "OAEI2011/NYTDatasets/organizationsKeywords.rdf";
		public static String NYT_ORGANIZATIONS_ARTICLES = "OAEI2011/NYTDatasets/organizationsArticles.rdf";
		
		//Output alignment files
		public static String FREEBASE_PEOPLE_OUTPUT = "NYTbatch/FreebasePeople.rdf";
		public static String FREEBASE_LOCATIONS_OUTPUT = "NYTbatch/FreebaseLocations.rdf";
		public static String FREEBASE_ORGANIZATIONS_OUTPUT = "NYTbatch/FreebaseOrganizations.rdf";
		public static String DBPEDIA_PEOPLE_OUTPUT = "NYTbatch/DBpediaPeople.rdf";
		public static String DBPEDIA_LOCATION_OUTPUT = "NYTbatch/DBpediaLocations.rdf";
		public static String DBPEDIA_ORGANIZATION_OUTPUT = "NYTbatch/DBpediaOrganizations.rdf";
		public static String GEONAMES_LOCATION_OUTPUT = "NYTbatch/GeoNamesLocations.rdf";
	
		//Target ontologies
		public static String DBPEDIA = "DBPedia";
		public static String FREEBASE = "Freebase";

		public static String DBP_ENDPOINT = "http://dbpedia.org/sparql";
				
		//KB files
		public static String DBP_PERSON_XML = "people.xml";
		
		//Ids
		public static String DBP_PERSON = "dbp_persondata";
		public static String FRB_PERSON = "freebase_person";
		public static String DBP_ORGANIZATION = "dbp_org";
		public static String FRB_ORGANIZATION = "freebase_org";
		public static String DBP_LOCATION = "dbp_loc";
		public static String FRB_LOCATION = "freebase_loc";
		
		//References
		public static String REF_DBP_PEOPLE = "OAEI2011/NYTReference/nyt-dbpedia-people-mappings.rdf";
		public static String REF_DBP_LOCATIONS = "OAEI2011/NYTReference/nyt-dbpedia-locations-mappings.rdf";
		public static String REF_DBP_ORGANIZATIONS = "OAEI2011/NYTReference/nyt-dbpedia-organizations-mappings.rdf";
		public static String REF_FREEBASE_PEOPLE = "OAEI2011/NYTReference/nyt-freebase-people-mappings.rdf";
		public static String REF_FREEBASE_LOCATION = "OAEI2011/NYTReference/nyt-freebase-locations-mappings.rdf";
		public static String REF_FREEBASE_ORGANIZATION = "OAEI2011/NYTReference/nyt-freebase-organizations-mappings.rdf";
		public static String REF_GEONAMES_LOCATION = "OAEI2011/NYTReference/nyt-geonames-mappings.rdf";
		

}
