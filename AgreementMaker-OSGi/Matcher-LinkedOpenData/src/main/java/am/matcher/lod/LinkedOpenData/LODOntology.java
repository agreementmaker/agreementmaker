package am.matcher.lod.LinkedOpenData;

import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;

public enum LODOntology {
	
	DBPEDIA("http://dbpedia.org/ontology/", "LOD/dbpedia_3.5.1.owl", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	MUSIC_ONTOLOGY("http://purl.org/ontology/mo/", "LOD/musicontology_NoImports.rdfs", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	SW_CONFERENCE("http://data.semanticweb.org/ns/swc/ontology#", "LOD/swc_2009-05-09_noDeprecated.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML ),
	BBC_PROGRAM("http://purl.org/ontology/po/", "LOD/BBC Program - 2009-09-07.n3", OntologyLanguage.OWL, OntologySyntax.N3),
	GEONAMES("http://www.geonames.org/ontology", "LOD/geonames_v2.2.1.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	FOAF("http://xmlns.com/foaf/0.1/", "LOD/foaf.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	SIOC("http://rdfs.org/sioc/ns#", "LOD/ns_noDeprecated.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	AKT_PORTAL("http://www.aktors.org/ontology/portal", "LOD/portal.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	BBC_PROGRAM_OLD("http://purl.org/ontology/po/", "LOD/OldOntologies/BBCProgram.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	DBPEDIA_OLD("http://dbpedia.org/ontology/", "LOD/OldOntologies/dbpedia.owl", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	MUSIC_ONTOLOGY_OLD("http://purl.org/ontology/mo/", "LOD/OldOntologies/musicontology.rdfs", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	GEONAMES_OLD("http://www.geonames.org/ontology", "LOD/OldOntologies/ontology_v2.0_Lite.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML),
	
	;
	
	private String uri;
	private String filename;
	private OntologyLanguage lang;
	private OntologySyntax syntax;
	
	private LODOntology(String uri, String filename, OntologyLanguage lang, OntologySyntax syntax) {
		this.uri = uri;
		this.filename = filename;
		this.lang = lang;
		this.syntax = syntax;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public OntologyLanguage getLang() {
		return lang;
	}
	
	public OntologySyntax getSyntax() {
		return syntax;
	}
}
