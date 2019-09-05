package misc;

public class NYTConstants {
	
	//Property URIs
	public static String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static String NYT_TOPICPAGE = "http://data.nytimes.com/elements/topicPage";
	public static String FOAF_NAME = "http://xmlns.com/foaf/0.1/name";
	public static String SKOS_CONCEPT = "http://www.w3.org/2004/02/skos/core#Concept";
	public static String SKOS_PREFLABEL = "http://www.w3.org/2004/02/skos/core#prefLabel";
	
	//Datasets on disk
	public static String NYT_PEOPLE = "OAEI2011/NYTDatasets/people.rdf";
	public static String NYT_LOCATIONS = "OAEI2011/NYTDatasets/locations.rdf";
	public static String NYT_ORGANIZATIONS = "OAEI2011/NYTDatasets/organizations.rdf";
	
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
	public static String REF_FREEBASE_PEOPLE = "OAEI2011/NYTReference/nyt-freebase-people-mappings.rdf";
	public static String REF_FREEBASE_LOCATION = "OAEI2011/NYTReference/nyt-freebase-locations-mappings.rdf";
	public static String REF_FREEBASE_ORGANIZATION = "OAEI2011/NYTReference/nyt-freebase-organizations-mappings.rdf";
	
	
}
