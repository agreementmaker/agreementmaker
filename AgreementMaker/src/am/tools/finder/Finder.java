package am.tools.finder;

import java.util.List;

import am.tools.finder.DBPediaFinder.QueryType;

import com.hp.hpl.jena.ontology.OntClass;

public interface Finder {
	public void initialize();
	public List<OntClass> search(String term);
	public String getQuery(String URI, QueryType type);
	public String query(String urlStr);
	public String executeQuery(String query);
}
