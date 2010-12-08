/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdgeData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertexData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PairwiseConnectivityGraph;
import am.app.ontology.Node;
import am.utility.DirectedGraphEdge;
import am.utility.Pair;

/**
 * Similarity Flooding algorithm implementation.
 * @author Cosmin and Michele
 *
 */
public abstract class SimilarityFloodingMatcher extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3749229483504509029L;
	protected static final boolean DEBUG_FLAG = true;
	
	protected PairwiseConnectivityGraph pcg;
	
	public static final double MAX_PC = 1.0; // maximum value for propagation coefficient
	public static final double DELTA = 0.01; // min value for differentiating two similarity vectors
	public static final int MAX_ROUND = 100; // maximum numbers of rounds for fixpoint computation
	
	/**
	 * given two nodes named origin and destination we have a list of the possible 
	 * directions of an edge connecting them
	 */
	private enum Direction{ORIG2DEST, DEST2ORIG};

	/**
	 * 
	 */
	public SimilarityFloodingMatcher() {
		super();
		needsParam = true; // we need to display the parameters panel.
	}

	/**
	 * @param params_new
	 */
	public SimilarityFloodingMatcher(SimilarityFloodingMatcherParameters params_new) {
		super(params_new);
	}
	
	@Override 
	public AbstractMatcherParametersPanel getParametersPanel() { return new SimilarityFloodingParametersPanel(); };
	
	/**
	 * Similarity Flooding Algorithm. 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * @author michele
	 * NOTE: we are using graphs instead of arrayList
	 */
	 protected void align() throws Exception {
		progressDisplay.clearReport();
		 
		// cannot align just one ontology (this is here to catch improper invocations)
		if( sourceOntology == null ) throw new NullPointerException("sourceOntology == null");   
		if( targetOntology == null ) throw new NullPointerException("targetOntology == null");
		
		progressDisplay.appendToReport("Creating Pairwise Connectivity Graph...");
		createPairwiseConnectivityGraph();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Creating Induced Propagation Graph...");
		createInducedPropagationGraph();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Computing Fixpoints...");
		computeFixpoint();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Creating Similarity Matrices...");
		loadSimilarityMatrices();
		createSimilarityMatrices();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
	 }
	 
	 /* *********************************************************************************** */
	 /* 							PAIRWISE CONNECTIVITY GRAPH OPERATIONS					*/
	 /* *********************************************************************************** */
	 
	 /**
	  * This method creates the Pairwise Connectivity Graph.
	  * 
	  * Given the graphs for the source and target ontologies, A and B respectively,
	  * and elements x, y in A and x', y' in B, we construct the PCG in this way:
	  *  
	  *   PCGVertex    PCGVertex
	  *      \/           \/
	  *  ( (x, y) , p, (x', y') ) in PCG(A, B) <==> (x, p, x') in A and (y, p, y') in B
	  *            /\
	  *  	     PCGEdge
	  */
	 protected void createPairwiseConnectivityGraph(){
		 
		 pcg = new PairwiseConnectivityGraph();
		 
		 OntModel localSource = sourceOntology.getModel();
		 OntModel localTarget = targetOntology.getModel();
		 
		 StmtIterator sStmtIterator = localSource.listStatements();
		 StmtIterator tStmtIterator = localTarget.listStatements();
		 
		 Statement sStmt = null;
		 Statement tStmt = null;

		 /**
		  * We hash the source concept into the first hashtable, which returns a second hash table.
		  * Then we hash the target concept into the second hash table and that returns the PCGVertex corresponding
		  * to the source and target concepts.
		  *  
		  * 
		  * Maybe linear probing or double hashing (http://en.wikipedia.org/wiki/Double_hashing) would be a better solution.
		  * Actually, a double indexed HashTable (two keys for get() put() operations), along with standard linear probing 
		  * and double hashing as a fall back would be the better solution.
		  * 
		  */
		 
		 HashMap<RDFNode, HashMap<RDFNode, PCGVertex>> firstHashTable = new HashMap<RDFNode, HashMap<RDFNode, PCGVertex>>();
		 
		 while(sStmtIterator.hasNext()){
			 sStmt = sStmtIterator.next();
			 
			 while(tStmtIterator.hasNext()){
				 tStmt = tStmtIterator.next();
				 
				 // condition where we add a new element in the pairwise connectivity graph:
				 // comparison of predicates
				 if(sStmt.getPredicate().equals(tStmt.getPredicate())){
					 
					 
					 if( ((SimilarityFloodingMatcherParameters)param).omitAnonymousNodes && 
							 ( sStmt.getSubject().isAnon() || sStmt.getObject().isAnon() ||
							   tStmt.getSubject().isAnon() || tStmt.getObject().isAnon() )  ) {
						// these nodes are anonymous
						// parameter is set to not insert anonymous nodes
						// do nothing
					 } else {
						 
						 // check to see if we have a node already for the source vertex and for the target vertex.
						 
						 PCGVertex sourcePCGVertex = getPCGVertex(firstHashTable, sStmt.getSubject(), tStmt.getSubject() );
						 PCGVertex targetPCGVertex = getPCGVertex(firstHashTable, sStmt.getObject(),  tStmt.getObject() );
						 						 
						 insertInPCG(sourcePCGVertex,  // vertex
					 		sStmt.getPredicate(),      // edge
					 		targetPCGVertex,           // vertex
					 		sStmt.getSubject(), tStmt.getSubject(), sStmt.getObject(), tStmt.getObject(), firstHashTable );
					 }
					 
				 }
				 
			 }
			 tStmtIterator = localTarget.listStatements();
		 }
	 }

	 
	/**
	 * This method inserts a new "triple" in the PairwiseConnectivityGraph.
	 * A triple is a ( PCGVertex, PCGEdge, PCGVertex ).
	 * 
	 * @param sourcePCGVertex (x, y)
	 * @param predicate		  p
	 * @param targetPCGVertex (x', y')
	 * @param sourceSubject   x
	 * @param targetSubject   y
	 * @param sourceObject    x'
	 * @param targetObject    y'
	 * @param firstHashTable
	 */
	private void insertInPCG(
			PCGVertex sourcePCGVertex, Property predicate, PCGVertex targetPCGVertex,
			RDFNode sourceSubject, RDFNode targetSubject, RDFNode sourceObject, RDFNode targetObject,
			HashMap<RDFNode, HashMap<RDFNode, PCGVertex>> firstHashTable ) {
		
		boolean sourceVertexCreated;
		boolean targetVertexCreated;
		
		if( sourcePCGVertex == null ) {
			sourceVertexCreated = true; // we are creating a new sourcePCGVertex, make sure we insert it in the graph.
			// the source PCGVertex does not exist, create it.
			Pair<RDFNode, RDFNode> sourcePair = new Pair<RDFNode, RDFNode>(sourceSubject, targetSubject);
			PCGVertexData vertexData = new PCGVertexData( sourcePair );
			sourcePCGVertex = new PCGVertex(vertexData);
			
			// add source vertex to the hash table.
			HashMap<RDFNode, PCGVertex> secondHashTable = firstHashTable.get(sourceSubject);
			if( secondHashTable == null ) {
				// second hash table does not exist.  create it.
				secondHashTable = new HashMap<RDFNode, PCGVertex>();
				
				// add it to the first hash table.
				firstHashTable.put(sourceSubject, secondHashTable);
			}
			
			secondHashTable.put(targetSubject, sourcePCGVertex);
			
		} else {
			sourceVertexCreated = false; // the sourcePCGVertex exists already. do not insert it again into the graph.			
		}
		
		if( targetPCGVertex == null ) {
			targetVertexCreated = true; // we are creating a new targetPCGVertex, make sure we insert it in the graph.
			
			// the target PCGVertex does not exist, create it.
			Pair<RDFNode, RDFNode> targetPair = new Pair<RDFNode, RDFNode>(sourceObject, targetObject);
			PCGVertexData vertexData = new PCGVertexData( targetPair );
			targetPCGVertex = new PCGVertex(vertexData);
			
			// add the target vertex to the hash table.
			HashMap<RDFNode, PCGVertex> secondHashTable = firstHashTable.get(sourceObject);
			if( secondHashTable == null ) {
				// second hash table does not exist. create it.
				secondHashTable = new HashMap<RDFNode, PCGVertex>();
				
				// add it to the first hash table
				firstHashTable.put(targetObject, secondHashTable);
			}
			
			secondHashTable.put(targetObject, targetPCGVertex);
		} else {
			targetVertexCreated = false; // the targetPCGVertex exists already. do not insert it again into the graph.
		}
		
		
		// create the edge and insert it into the graph.
		
		PCGEdgeData edgeData = new PCGEdgeData(predicate);
		PCGEdge pairEdge = new PCGEdge(sourcePCGVertex, targetPCGVertex, edgeData);
		
		sourcePCGVertex.addOutEdge(pairEdge);
		targetPCGVertex.addInEdge(pairEdge);
		 
		if( sourceVertexCreated ) pcg.insertVertex(sourcePCGVertex);
		if( targetVertexCreated ) pcg.insertVertex(targetPCGVertex);
		pcg.insertEdge(pairEdge);
		
	}

	/**
	  * Returns the PCGVertex associated with the sourceSubject and targetSubject. 
	  * @param firstHashTable  The first hashtable. (described in createPairwiseConnectivityGraph())
	  * @param sourceSubject
	  * @param targetSubject
	  * @return Returns null if no PCGVertex exists.
	  */
	 private PCGVertex getPCGVertex(
			HashMap<RDFNode, HashMap<RDFNode, PCGVertex>> firstHashTable,
			RDFNode sourceSubject, RDFNode targetSubject) {
		
		HashMap<RDFNode, PCGVertex> secondHashTable = firstHashTable.get(sourceSubject);
		
		if( secondHashTable == null ) return null;
		
		PCGVertex existingVertex = secondHashTable.get(targetSubject); 
		
		return existingVertex;  // can be null
	}

	/**
	  * This method creates the Induced Propagation Graph.]
	  * NOTE! This is done in place using the Pairwise Connectivity Graph as the base.
	  * In other words, a new graph structure is NOT created.  Two operations are done:
	  * 
	  * 1) Existing edges (forward edges) are annotated with a propagation coefficient (edge weight).
	  * 2) Back edges are created and annotated with a propagation coefficient.
	  *  
	  */
	 protected void createInducedPropagationGraph(){
		 applyCoefficients();
		 createBackwardEdges();
	 }
	 
	 /**
	  * 
	  */
	 protected void computeFixpoint(){
		 int round = 0;
		 double maxSimilarity = 0.0, newSimilarity = 0.0;
		 
		 // base case checked: stop computation. Either we reached the fixpoint or we overcame the limit;
		 do {
			 // new round: computing new similarities
			 round++;
			 Iterator<PCGVertex> iVert = pcg.vertices();
			 PCGVertex vert = null;
			 while(iVert.hasNext()){
				 // take the current vertex
				 vert = iVert.next();
				 // update old value with new value of previous round
				 // vert.getObject().setOldSimilarityValue(vert.getObject().getNewSimilarityValue()); 
				 // compute the new similarity value for that vertex
				 newSimilarity = computeFixpointPerVertex(vert);
				 // store it inside the vertex
				 vert.getObject().setNewSimilarityValue(newSimilarity);
				 // update the maximum value
				 maxSimilarity = Math.max(newSimilarity, maxSimilarity);
			 }
			 // normalize all the similarity values of all nodes (and updates oldSimilarities for next round
			 normalizeSimilaries(pcg.vertices(), maxSimilarity);
		 } while(!checkBaseCase(round, pcg.getSimValueVector(true), pcg.getSimValueVector(false)));
	 }
	 
	 /* *********************************************************************************** */
	 /* 							INDUCED PROPAGATION GRAPH OPERATIONS					*/
	 /* *********************************************************************************** */
	 
	 public void applyCoefficients(){
		 Iterator<PCGVertex> iVert = pcg.vertices();
		 PCGVertex currentVert = null;
		 while(iVert.hasNext()){
			 
			 // assigning outgoing propagation coefficients
			 HashMap<Property, Integer> counter = new HashMap<Property, Integer>();
			 currentVert = iVert.next();
			 
			 // counting phase (with outgoing edges)
			 computeQuantities(currentVert.edgesOutIter(), counter, true);
			 // dividing phase (with outgoing edges)
			 computeQuantities(currentVert.edgesOutIter(), counter, false);
		 }
	 }
	 
	 public void createBackwardEdges(){
		 
		 Iterator<PCGVertex> iVert = pcg.vertices();
		 PCGVertex currentVert = null;
		 while(iVert.hasNext()){
			 currentVert = iVert.next();
			 // creating duplicate outgoing edges for ingoing ones
			 HashMap<Property, Integer> counter = new HashMap<Property, Integer>();
			 
			 // counting phase (with ingoing edges)
			 computeQuantities(currentVert.edgesInIter(), counter, true);

			 // back-edge creation and weight assignment phase (with ingoing edges)
			 createBackedges(currentVert.edgesInIter(), counter);
		 }
	 }
	 
	 private void computeQuantities(Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> iVEdge,
			 						HashMap<Property, Integer> counter,
			 						boolean computing){
		 PCGEdge currentEdge = null;
		 while(iVEdge.hasNext()){
			 if(!computing){
				 System.out.print("");
			 }
			 currentEdge = (PCGEdge) iVEdge.next();
			 Property currentProp = currentEdge.getObject().getStProperty();
			 
			 if(computing){ // computing phase
				 if( counter.containsKey(currentProp) ) {
					 Integer i = counter.get(currentProp);
					 i++;
				 } else {
					 counter.put(currentProp, new Integer(1) );
				 }
			 }
			 else{ // dividing phase
				 currentEdge.getObject().setPropagationCoefficient( MAX_PC / counter.get(currentProp).doubleValue() );
			 }
		 }
	 }
	 
	 private void createBackedges(Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> iVEdge,
				HashMap<Property, Integer> counter){
		 
		 PCGEdge currentEdge = null;
		 
		 while(iVEdge.hasNext()){
			 currentEdge = (PCGEdge) iVEdge.next();
			 // check if back-edge exists
			 if(checkEdge(  (PCGVertex)currentEdge.getOrigin(),   // neighbor (orig)
					 		(PCGVertex)currentEdge.getDestination(),  // current vertex (dest)
					 		Direction.DEST2ORIG)
					 		){
				 continue; // so far ineffective because it goes anyway to the next iteration
			 }
			 else{
				 
				 Property currentProp = currentEdge.getObject().getStProperty();
				 double tmpPC = MAX_PC / counter.get(currentProp).doubleValue();
				 
				 PCGVertex originOfBackedge = (PCGVertex)currentEdge.getDestination();
				 PCGVertex destinationOfBackedge = (PCGVertex)currentEdge.getOrigin();
				 
				 PCGEdge backedge = new PCGEdge(   originOfBackedge,destinationOfBackedge, new PCGEdgeData(null, tmpPC) );
				 
				 originOfBackedge.addOutEdge(backedge);
				 destinationOfBackedge.addInEdge(backedge);
				 
				 //adding new edge
				 pcg.insertEdge(backedge);
			 }
		 }
	 }
	 
	 /**
	  * 
	  */
	 protected boolean checkBaseCase(int round, Vector<Double> simVectBefore, Vector<Double> simVectAfter){
//		 System.out.println(round > MAX_ROUND);
		 System.out.println(simDistance(simVectBefore, simVectAfter) );
		 return ((round > MAX_ROUND) || (simDistance(simVectBefore, simVectAfter) < DELTA));
	 }

	 protected double simDistance(Vector<Double> simVectBefore, Vector<Double> simVectAfter){
		 double simD = 0.0, diff = 0.0;
		 assert (simVectBefore.size() == simVectAfter.size()); // size of both vectors should always be the same
		 
		 // computing euclidean distance
		 for(int i = 0; i < simVectAfter.size(); i++){
			 diff = simVectBefore.get(i) - simVectAfter.get(i);
			 simD += (diff * diff);
		 }
		 return Math.sqrt(simD);
	 }
	 
	 /**
	 * @return 
	  * 
	  */
	 protected double computeFixpointPerVertex(PCGVertex pcgV){
		 return pcgV.getObject().getOldSimilarityValue()	// old value
		 			+ sumIncomings(pcgV.edgesInIter());		// sum of incoming regular edges values
		 //			+ sumBackedges(pcgV.edgesOutIter());	// sum of incoming backedge values
	 }
	 
	 protected double sumIncomings(Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> inIter){
		 double sum = 0.0, oldValue = 0.0, propCoeff = 0.0;
		 PCGEdge currEdge = null;
		 while(inIter.hasNext()){
			 currEdge = (PCGEdge) inIter.next();
			 // computing old sim value multiplied by the prop coefficient of the regular incoming edge
			 oldValue = currEdge.getOrigin().getObject().getOldSimilarityValue();
			 propCoeff = currEdge.getObject().getPropagationCoefficient();
			 sum += oldValue * propCoeff;
		 }
		 return sum;
	 }
	 
	 protected double sumBackedges(Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> outIter){
		 double sum = 0.0, oldValue = 0.0, propCoeff = 0.0;
		 PCGVertex origin, destination;
		 
		 while(outIter.hasNext()){
			 PCGEdge currentBackedge = (PCGEdge) outIter.next();
			 origin = (PCGVertex) currentBackedge.getOrigin();
			 destination = (PCGVertex) currentBackedge.getDestination();
			 
			 // looking for the backedge: check if the destination of the backedge is the stored origin
			 PCGEdge backEdge = null;
			 Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> destIter = destination.edgesOutIter();
			 
			 while(destIter.hasNext()){
				 backEdge = (PCGEdge) destIter.next();
				 if(backEdge.getDestination().equals(origin)){
					 break;
				 }
			 }
			 // computing old sim value multiplied by the prop coefficient of the back edge
			 oldValue = backEdge.getOrigin().getObject().getOldSimilarityValue();
			 propCoeff = backEdge.getObject().getPropagationCoefficient();
			 sum += oldValue * propCoeff;
		 }
		 return sum;
	 }
	 
	 /* *************************************************** */
	 /* 				 NORMALIZATION PHASE 			   	*/
	 /* *************************************************** */
		 
		 protected void normalizeSimilaries(Iterator<PCGVertex> iVert, double maxSimilarity){
			 double nonNormSimilarity = 0.0, normSimilarity = 0.0;
			 PCGVertex currentVert = null;
			 while(iVert.hasNext()){
				 currentVert = iVert.next();
				 nonNormSimilarity = currentVert.getObject().getNewSimilarityValue();
				 
				 // compute normalized value
				 normSimilarity = nonNormSimilarity / maxSimilarity;
				 
				 // set normalized to new value
				 currentVert.getObject().setNewSimilarityValue(normSimilarity);
			 }
		 }

	 /* *************************************************** */
	/* 					  SIMILARITY MATRICES CREATION 		*/
	/* *************************************************** */
	 
	 protected void createSimilarityMatrices(){
		 Iterator<PCGVertex> iVert = pcg.vertices();
		 PCGVertex currentVert = null;
		 while(iVert.hasNext()){
			 currentVert = iVert.next();
			 
			 // take both source and target nodes
			 RDFNode sourceNode = currentVert.getObject().getStCouple().getLeft();
			 RDFNode targetNode = currentVert.getObject().getStCouple().getRight();
			 
			 OntResource sourceRes, targetRes;
			 // try to get the ontResource from them
			 if(sourceNode.canAs(OntResource.class) && targetNode.canAs(OntResource.class)){
				 sourceRes = sourceNode.as(OntResource.class);
				 targetRes = targetNode.as(OntResource.class);
				 
				// try to get the Node and check they belong to the same alignType
				 Node source, target;
				 Mapping m;
				 try{
					 source = sourceOntology.getNodefromOntResource(sourceRes, alignType.aligningClasses);
					 target = targetOntology.getNodefromOntResource(targetRes, alignType.aligningClasses);
					 m = new Mapping(source, target, currentVert.getObject().getNewSimilarityValue());
					 classesMatrix.set(source.getIndex(), target.getIndex(), m);
				 }
				 catch(Exception eClass){
					 try{
						 source = sourceOntology.getNodefromOntResource(sourceRes, alignType.aligningProperties);
						 target = targetOntology.getNodefromOntResource(targetRes, alignType.aligningProperties);
						 m = new Mapping(source, target, currentVert.getObject().getNewSimilarityValue());
						 propertiesMatrix.set(source.getIndex(), target.getIndex(), m);
					 }
					 catch(Exception eProp){
						 continue;
					 }
				 }
			 }
			 else{
				 continue;
			 }
			 
		 }
			 

	 }
	 
	 
	/* *************************************************** */
	/* 					  SUPPORT FUNCTIONS 			   */
	/* *************************************************** */
	 
	 /**
	  * T
	  */
	 private boolean checkEdge(PCGVertex o, PCGVertex d, Direction dir){
		 PCGVertex originVertex, destinatonVertex;
		 if(dir == Direction.ORIG2DEST){
			 originVertex = o;
			 destinatonVertex = d;
		 }
		 else{   // DEST2ORIG
			 originVertex = d;
			 destinatonVertex = o;
		 }
		 
		 // we are checking that one of the edges coming out the origin matches with the destination
		 Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> iVEdge = originVertex.edgesOutIter();
		 PCGEdge currentEdge = null;
		 while(iVEdge.hasNext()){
			 currentEdge = (PCGEdge) iVEdge.next();
			 if(currentEdge.getDestination().equals(destinatonVertex)){
				 System.out.println(currentEdge.getDestination() + " ---- " + destinatonVertex + "\n");
				 return true;
			 }
		 }
		 return false; 
	 }
	 

	protected abstract void loadSimilarityMatrices();
	
	protected abstract PCGVertexData selectInput(Pair<RDFNode, RDFNode> pair);

}
