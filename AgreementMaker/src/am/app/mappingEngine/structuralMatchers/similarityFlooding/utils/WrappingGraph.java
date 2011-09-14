/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.EOntNodeType.EOntologyNodeType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.utility.DirectedGraph;

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

/**
 * @author michele
 *
 */
public class WrappingGraph extends DirectedGraph<WGraphEdge, WGraphVertex>{
	
	/**
	 * List of admitted properties: each element in the array has to be managed in the returnEdge method
	 * (either when new element is inserted or removed) 
	 */
	private static String[] admittedProperties = {
			RDFS.domain.toString(), RDFS.range.toString(),
    		RDFS.subClassOf.toString(), RDFS.subPropertyOf.toString(),
    		OWL.equivalentClass.toString(), OWL.equivalentProperty.toString(),
    		OWL.inverseOf.toString()
    		};
	
	private int classMatSize;
	private int propertyMatSize;
	private int currentIndexForClass;
	
	/**
	 * This is to keep track of the inserted vertexes
	 */
	private HashMap<RDFNode, WGraphVertex> nodesMap;
	
	// CONSTRUCTOR //
	
	public WrappingGraph(Ontology AMOntology) {
		super();
		classMatSize = 0;
		propertyMatSize = 0;
		currentIndexForClass = AMOntology.getClassesList().size();
		nodesMap = new HashMap<RDFNode, WGraphVertex>();
		buildGraph(AMOntology);
	}
	
	// GRAPH BUILDING METHODS //
	
	private void buildGraph(Ontology ontology){
		
		// VERTICES first
		List<Node> setC = ontology.getClassesList();
		List<Node> setP = ontology.getPropertiesList();
		
		classMatSize = setC.size();
		propertyMatSize = setP.size();
		
		for(int i = 0; i < classMatSize; i++){
			// create the node
			WGraphVertex vertexNew = new WGraphVertex(setC.get(i).getResource(), setC.get(i).getIndex());
			this.insertVertex(vertexNew);
			// insert the node in the table (for future lookup)
			nodesMap.put(setC.get(i).getResource(), vertexNew);
		}
		for(int i = 0; i < propertyMatSize; i++){
			// create the node
			WGraphVertex vertexNew = new WGraphVertex(setP.get(i).getResource(), setP.get(i).getIndex());
			this.insertVertex(vertexNew);
			// insert the node in the table (for future lookup)
			nodesMap.put(setP.get(i).getResource(), vertexNew);
		}
		
/*		VERTICES first, old method
		String queryString = composeQuery(EGraphNodeType.VERTEXES);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, jenaGraph);
		ResultSet results = qexec.execSelect();
		processResults(results, EGraphNodeType.VERTEXES);
*/		
		// EDGES then
		String queryString = composeQuery(EGraphNodeType.EDGES);
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, ontology.getModel());
		ResultSet results = qexec.execSelect();
		processResults(results, EGraphNodeType.EDGES);
		qexec.close();
	}
	
	/**
	 * process the queries to get the edges and the vertexes
	 * @param results
	 * @param t
	 */
    private void processResults(ResultSet results, EGraphNodeType t) {
    	
		QuerySolution soln;
    	switch(t){
    	case EDGES:
    		while(results.hasNext()){
    			soln = results.nextSolution();
    			
    			RDFNode originNode = soln.get("x");
    			RDFNode destinationNode = soln.get("z");
    			
    			if(!originNode.isAnon() && !destinationNode.isAnon()){
    				WGraphVertex origin = returnVert(originNode);
        			WGraphVertex destination = returnVert(destinationNode);
    				WGraphEdge edgeNew = returnEdge(origin, destination, soln.get("y"));
        			
    				if(edgeNew != null){

        				origin = (WGraphVertex) edgeNew.getOrigin();
        				destination = (WGraphVertex) edgeNew.getDestination();
    					
	        			// actual insertion
	        			this.insertEdge(edgeNew);
	        			
	        			origin.addOutEdge(edgeNew);
	        			destination.addInEdge(edgeNew);
    				}
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
    private String composeQuery(EGraphNodeType t){
    	
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
    
    public void sortEdges(){
    	Collections.sort(this.edges);
    }

	private WGraphEdge returnEdge(WGraphVertex origin, WGraphVertex destination, RDFNode rdfNode) {
		if(rdfNode == null){ // coming from the restriction
			return null; // not managed yet
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

	/**
	 * to be used ONLY to build the wrapping graph
	 * @param rdfNode
	 * @return
	 */
	private WGraphVertex returnVert(RDFNode rdfNode) {
		if(nodesMap.get(rdfNode) != null){
			// there was already a node with that rdfNode
			return nodesMap.get(rdfNode);
		}
		else{
			// there wasn't already that node so
			
			// we create it
			WGraphVertex vertNew = new WGraphVertex(rdfNode, currentIndexForClass);
			currentIndexForClass++;
			
			// we add it to the list
			this.insertVertex(vertNew);
			// we add it to the map of nodes (we will always include it in the set of nodes to search in)
			nodesMap.put(rdfNode, vertNew);
			// and we return it
			return vertNew;
		}
	}
	
	// COMPUTE DIFFERENCE WG-ONTOLOGY
	
	public List<Node> diff(Ontology o, EOntologyNodeType tp){
		List<Node> ontNodes = null;
		List<Node> wgNodes = null;
		
		List<Node> diff = new ArrayList<Node>();
		
		switch(tp){
    	case CLASS:
    		ontNodes = o.getClassesList();
    		wgNodes = this.createNodesList(o, EOntologyNodeType.CLASS);
    		break;
    	case PROPERTY:
    		ontNodes = o.getPropertiesList();
    		wgNodes = this.createNodesList(o, EOntologyNodeType.PROPERTY);
    		break;
		default:
			new Exception("Unexpected state: should always be able to tell if edges or vertexes are processed");
			break;
		}
		
		Collections.sort(ontNodes);
		for(int i = 0; i < wgNodes.size(); i++){
			System.out.println(wgNodes.get(i));
		}
		Collections.sort(wgNodes);
		int wgIndex = 0, ontIndex = 0;
		for(wgIndex = 0; wgIndex < wgNodes.size(); wgIndex++){
			if(wgNodes.get(wgIndex).compareTo(ontNodes.get(ontIndex)) < 0){ // wg(i) < ont(i)
				diff.add(wgNodes.get(wgIndex));
			}
			else if(wgNodes.get(wgIndex).compareTo(ontNodes.get(ontIndex)) == 0){ // wg(i) = ont(i)
				ontIndex++;
			}
			else{ // wgNodes.get(i).compareTo(wgNodes.get(i)) > 0 ---> wg(i) > ont(i)
				// to be checked but should be an impossible situation since both of the arrays are sorted
			}
		}
		
		return diff;
	}

	public ArrayList<Node> createNodesList(Ontology ont, EOntologyNodeType tp) {
		ArrayList<Node> wgNodes = new ArrayList<Node>();
		ArrayList<WGraphVertex> wgVertices = this.getVertices();
		
		int key = 0;
		switch(tp){
    	case CLASS:
    		key = ont.getClassesList().size();
    		break;
    	case PROPERTY:
    		key = ont.getPropertiesList().size();
    		break;
		default:
			new Exception("Unexpected state: should always be able to tell if classes or properties are processed");
			break;
		}
		
		for(int i = 0; i < wgVertices.size(); i++){
			if(wgVertices.get(i).getObject().isResource()){
				Node n = null;
				switch(tp){
		    	case CLASS:
		    		if(wgVertices.get(i).getNodeType().equals(EOntologyNodeType.CLASS)){
		    			n = Node.getNodefromRDFNode(ont, wgVertices.get(i).getObject(), alignType.aligningClasses);
						if(n == null){
							n = new Node(key, wgVertices.get(i).getObject().toString(), Node.OWLCLASS, ont.getIndex());
							wgVertices.get(i).setMatrixIndex(key);
							key++;
						}
						else{
							wgVertices.get(i).setMatrixIndex(n.getIndex());
						}
		    		}
		    		break;
		    	case PROPERTY:
		    		if(wgVertices.get(i).getNodeType().equals(EOntologyNodeType.PROPERTY)){
			    		n = Node.getNodefromRDFNode(ont, wgVertices.get(i).getObject(), alignType.aligningProperties);
						if(n == null){
							n = new Node(key, wgVertices.get(i).getObject().toString(), Node.OWLPROPERTY, ont.getIndex());
							wgVertices.get(i).setMatrixIndex(key);
							key++;
			    		}
						else{
							wgVertices.get(i).setMatrixIndex(n.getIndex());
						}
		    		}
		    		break;
				default:
					new Exception("Unexpected state: should always be able to tell if classes or properties are processed");
					break;
				}
				if(n != null){
					wgNodes.add(n);
				}
			}
		}
		return wgNodes;
	}

	/**
	 * @param classMatSize the classMatSize to set
	 */
	public void setClassMatSize(int classMatSize) {
		this.classMatSize = classMatSize;
	}

	/**
	 * @return the classMatSize
	 */
	public int getClassMatSize() {
		return classMatSize;
	}

	/**
	 * @param propertyMatSize the propertyMatSize to set
	 */
	public void setPropertyMatSize(int propertyMatSize) {
		this.propertyMatSize = propertyMatSize;
	}

	/**
	 * @return the propertyMatSize
	 */
	public int getPropertyMatSize() {
		return propertyMatSize;
	}

	/**
	 * @param currentIndexForClass the currentIndexForClass to set
	 */
	public void setCurrentIndexForClass(int currentIndexForClass) {
		this.currentIndexForClass = currentIndexForClass;
	}

	/**
	 * @return the currentIndexForClass
	 */
	public int getCurrentIndexForClass() {
		return currentIndexForClass;
	}
	
}
