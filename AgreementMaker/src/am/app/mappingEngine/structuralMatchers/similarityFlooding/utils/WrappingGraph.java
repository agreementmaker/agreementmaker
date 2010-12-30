/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import java.util.HashMap;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import am.utility.DirectedGraph;

/**
 * @author michele
 *
 */
public class WrappingGraph extends DirectedGraph<WGraphEdge, WGraphVertex>{
	
	/**
	 * List of admitted properties
	 */
	private static String[] admittedProperties = {
			RDFS.domain.toString(), RDFS.range.toString(),
    		RDFS.subClassOf.toString(), RDFS.subPropertyOf.toString(),
    		OWL.equivalentClass.toString(), OWL.equivalentProperty.toString(),
    		OWL.inverseOf.toString()
    		};
	/**
	 * To discriminate which kind of data we want to extract to build the graph
	 */
	private enum TypeExtracted{ EDGES, VERTEXES }
	
	/**
	 * This is to keep track of the inserted vertexes
	 */
	private HashMap<RDFNode, WGraphVertex> nodesMap;
	
	// CONSTRUCTOR //
	
	public WrappingGraph(OntModel jenaGraph) {
		super();
		nodesMap = new HashMap<RDFNode, WGraphVertex>();
		buildGraph(jenaGraph);
	}
	
	// GRAPH BUILDING METHODS //
	
	/**
	 * main function: building the graph
	 */
	private void buildGraph(OntModel jenaGraph){
		
		// VERTEXES first
		String queryString = composeQuery(TypeExtracted.VERTEXES);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, jenaGraph);
		ResultSet results = qexec.execSelect();
		processResults(results, TypeExtracted.VERTEXES);
//		qexec.close();
		
		
		// EDGES then
		queryString = composeQuery(TypeExtracted.EDGES);
		query = QueryFactory.create(queryString);
		qexec = QueryExecutionFactory.create(query, jenaGraph);
		results = qexec.execSelect();
		processResults(results, TypeExtracted.EDGES);
		qexec.close();
		
	}
	
	/**
	 * process the queries to get the edges and the vertexes
	 * @param results
	 * @param t
	 */
    private void processResults(ResultSet results, TypeExtracted t) {
    	
		QuerySolution soln;
    	switch(t){
    	case EDGES:
    		while(results.hasNext()){
    			soln = results.nextSolution();
    			
    			WGraphVertex origin = returnVert(soln.get("x"));
    			WGraphVertex destination = returnVert(soln.get("z"));
    			
    			// excluding anonymous nodes: they don't have a URI starting with http
    			if( !(origin.getObject().isAnon() || destination.getObject().isAnon()) ){
    				WGraphEdge edgeNew = returnEdge(origin, destination, soln.get("y"));
        			
        			// actual insertion
        			this.insertEdge(edgeNew);
        			
        			origin.addOutEdge(edgeNew);
        			destination.addInEdge(edgeNew);
    			}
    		}
    		break;
    	case VERTEXES:
    		while(results.hasNext()){
    			soln = results.nextSolution();
    			
    			// create the node
    			WGraphVertex vertexNew = new WGraphVertex(soln.get("x"));
    			this.insertVertex(vertexNew);
    			// insert the node in the table (for future lookup)
    			nodesMap.put(soln.get("x"), vertexNew);
    		}
    		break;
		default:
			new Exception("Unexpected state: check why neither nodes nor edges are considered.\n");
    	}
	}
    
    /**
     * create the queries for edges and nodes
     * @param t
     * @return
     */
    private String composeQuery(TypeExtracted t){
    	
    	// building the prefix part (with RDF and RDFS vocabulary)
    	String prefixes = "" +
			"PREFIX rdf: <" + RDF.getURI() + "> " +
			"PREFIX rdfs: <" + RDFS.getURI() + "> ";
    	
    	// building the rest of the query
    	String query = prefixes;
    	switch(t){
	    	case EDGES:
	    		query += edgesQuery();
	    		break;
	    	case VERTEXES:
	    		query += vertexQuery();
	    		break;
    		default:
    			new Exception("Unexpected state: should always be able to tell if edges or vertexes are processed");
    			break;
    	}
//    	System.out.println(query);
    	return query;
    }

    // SPARQL QUERIES (to get edges and vertexes) //
    
    private String vertexQuery(){
    	String variables[] = {"?x", "?y"};
    	
    	// building the select part (and we want no duplicates)
    	String select = "SELECT DISTINCT ?x" + " ";
    	
    	// building the where part
    	String clauses = "";    	
    	for(int i = 0; i < admittedProperties.length; i++){
    		
	    	// building the where part
    		clauses +=
				" { " + variables[0] + // the x var
				" <" + admittedProperties[i] + "> " + // one of the properties we want
				variables[1] + " . } "; // the y var
    		
    		if(i < admittedProperties.length - 1){
    			clauses += " UNION ";
    	   	}
    	}
    	
    	String where = "WHERE { " + clauses + " } ";
    	
    	return select + where;
    }
    
    private String edgesQuery(){
//    	String variables[] = {"?x", "?y", "?z"};
    	
    	// building the select part (and we want no duplicates)
    	String select = "SELECT DISTINCT ?x ?y ?z" + " ";
    	
    	// building the where part
    	String clauses = "";
    	for(int i = 0; i < admittedProperties.length; i++){
    		clauses += 
    			" { " +
	    		" ?x ?y ?z . "  +
	    		" ?x <" + admittedProperties[i] + "> ?z . " +
	    		" } ";
    		
    		if(i == 3){ // adding the classes inherited from an anonymous nodes
    			clauses += " UNION { " +
    					" ?x <" + RDFS.subClassOf + "> ?b . " +
    					" ?b <" + RDF.type + "> <" + OWL.Restriction + "> . " +
    					" ?b <" + OWL.onProperty + "> ?z . } ";
    		}

    		if(i < admittedProperties.length - 1){
    			clauses += " UNION ";
    	   	}
    	}
    	
    	String where = "WHERE { " + clauses + " } ";
    	
    	return select + where;
    }
    
    // CHECKS FOR VERTEXES AND EDGE MANIPULATION //

	private WGraphEdge returnEdge(WGraphVertex origin,
			WGraphVertex destination, RDFNode rdfNode) {
		if(rdfNode == null){ // coming from the restriction
			return new WGraphEdge(origin, destination, "hasRestrictedProperty"); // very similar to hasProperty
		}
		else if(rdfNode.equals(RDFS.domain)){
			return new WGraphEdge(destination, origin, "hasProperty");
		}
		else if(rdfNode.equals(RDFS.range)){
			return new WGraphEdge(origin, destination, "range");
		}
		else if(rdfNode.equals(RDFS.subClassOf)){
			return new WGraphEdge(destination, origin, "hasSubclass");
		}
		else if(rdfNode.equals(RDFS.subPropertyOf)){
			return new WGraphEdge(destination, origin, "hasSubProperty");
		}
		else if(rdfNode.equals(OWL.equivalentClass)){
			return new WGraphEdge(origin, destination, "equivalentClass");
		}
		else if(rdfNode.equals(OWL.equivalentProperty)){
			return new WGraphEdge(origin, destination, "equivalentProperty");
		}
		else if(rdfNode.equals(OWL.inverseOf)){
			return new WGraphEdge(origin, destination, "inverseProperty");
		}
		else{
			new Exception("Unexpected state: we should always get one of the admitted properties");
			return null;
		}
	}

	private WGraphVertex returnVert(RDFNode rdfNode) {
		if(nodesMap.get(rdfNode) != null){
			// there was already a node with that rdfNode
			return nodesMap.get(rdfNode);
		}
		else{
			// there wasn't already that node so
			
			// we create it
			WGraphVertex vertNew = new WGraphVertex(rdfNode);
			// we add it to the list
			this.insertVertex(vertNew);
			// we add it to the map of nodes (we will always include it in the set of nodes to search in)
			nodesMap.put(rdfNode, vertNew);
			// and we return it
			return vertNew;
		}
	}
}
