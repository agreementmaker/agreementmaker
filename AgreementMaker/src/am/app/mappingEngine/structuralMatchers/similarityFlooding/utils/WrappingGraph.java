/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import am.utility.DirectedGraph;

/**
 * @author michele
 *
 */
public class WrappingGraph extends DirectedGraph<WGraphEdge, WGraphVertex>{
	
	private static String[] admittedProperties = {
			RDFS.domain.toString(), RDFS.range.toString(),
    		RDFS.subClassOf.toString(), RDFS.subPropertyOf.toString(),
    		OWL.equivalentClass.toString(), OWL.equivalentProperty.toString(),
    		OWL.inverseOf.toString()
    		};
	
	// CONSTRUCTOR //
	
	public WrappingGraph(OntModel jenaGraph) {
		buildGraph(jenaGraph);
	}
	
	// SUPPORT METHODS //
	
	private void buildGraph(OntModel jenaGraph){
		String queryString = composeQuery();
		Query query = QueryFactory.create(queryString);
		ResultSet results = executeQuery(query, jenaGraph);
		// TODO: complete
	}
	
    private ResultSet executeQuery(Query query, OntModel model){
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet results = qexec.execSelect();
		qexec.close();
		return results;
	}
    
    private String composeQuery(){
    	
    	// building the prefix part (with RDF and RDFS vocabulary)
    	String prefixes = "" +
			"PREFIX rdf: <" + RDF.getURI() + "> " +
			"PREFIX rdfs: <" + RDFS.getURI() + "> ";
    	
    	// building the rest of the query
    	String query = vertexQuery();
    	
    	
    	
    	return prefixes + query;
    }

    private String vertexQuery(){
    	String query = "";
    	String union = "UNION";
    	String variables[] = {"?x", "?y"};
    	
    	for(int i = 0; i < admittedProperties.length; i++){
    		
	    	// building the select part (and we want no duplicates)
	    	String select = "SELECT DISTINCT ?x" + " ";
	    	
	    	// building the where part
    	   	query += select + 
				"WHERE { " + variables[0] + // ?x
				" " + admittedProperties[i] + " " +
				variables[1] + // ?y
				" . } ";
    	   	if(i < admittedProperties.length - 1){
    	   		query += " " + union + " ";
    	   	}
    	}
    	return query;
    }
}
