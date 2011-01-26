/**
 * 
 */
package am.app.mappingEngine.structuralMatchers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.SimilarityFloodingMatcherParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdgeData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGSimilarityMatrix;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertexData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PairwiseConnectivityGraph;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;
import am.app.ontology.Node;
import am.utility.DirectedGraphEdge;
import am.utility.Pair;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Similarity Flooding algorithm implementation.
 * @author Cosmin and Michele
 */
public abstract class SimilarityFlooding extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5964745776316412043L;

	protected static final boolean DEBUG_FLAG = false;
	
	protected PairwiseConnectivityGraph pcg;
	
	public static final double MAX_PC = 1.0; // maximum value for propagation coefficient
	public static final double DELTA = 0.01; // min value for differentiating two similarity vectors
	public static final int ROUND_MAX = 10; // maximum numbers of rounds for fixpoint computation
	
	protected boolean sortEdges = true;
	
//	File f = new File("/home/nikiforos/Desktop/at_once");
	File f = new File("/home/nikiforos/Desktop/by_connComp");
	protected FileWriter fw;
	
	/**
	 * given two nodes named origin and destination we have a list of the possible 
	 * directions of an edge connecting them
	 */
	private static enum Direction{ORIG2DEST, DEST2ORIG};

	public static enum EdgeDirection{IN, OUT};
	
	protected HashMap<String, PCGVertex> pairTable;
	protected HashMap<String, PCGEdge> edgesMap;

	/**
	 * 
	 */
	public SimilarityFlooding() {
		needsParam = true; // we need to display the parameters panel.

		pcg = new PairwiseConnectivityGraph();
		pairTable = new HashMap<String, PCGVertex>();
		edgesMap = new HashMap<String, PCGEdge>();
		
		 try {
			fw = new FileWriter(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param params_new
	 */
	public SimilarityFlooding(SimilarityFloodingParameters params_new) {
		needsParam = true; // we need to display the parameters panel.
		
		pcg = new PairwiseConnectivityGraph();
		pairTable = new HashMap<String, PCGVertex>();
		edgesMap = new HashMap<String, PCGEdge>();
		
		try {
			fw = new FileWriter(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Similarity Flooding Algorithm. 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * NOTE: we are using graphs instead of arrayList
	 */
	 protected abstract void align() throws Exception;
	 
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
	 protected void createFullPCG(WrappingGraph sourceOnt, WrappingGraph targetOnt){

		 Iterator<WGraphEdge> sourceIterator = sourceOnt.edges();
		 Iterator<WGraphEdge> targetIterator = targetOnt.edges();
		 
		 WGraphEdge sEdge = null;
		 WGraphEdge tEdge = null;
		 
			 while(sourceIterator.hasNext()){
				 sEdge = sourceIterator.next();
				 
				 while(targetIterator.hasNext()){
					 tEdge = targetIterator.next();
//					 System.out.println(sEdge.toString() + " " + tEdge.toString());
					 
					 // condition where we add a new element in the pairwise connectivity graph:
					 // comparison of predicates (now string labels)
					 if(sEdge.getObject().compareTo(tEdge.getObject()) > 0){
						// target property is smaller than source property (continue to cycle on target edges)
						continue; 
					 }
					 else if(sEdge.getObject().equals(tEdge.getObject())){
						 // target property is equal to source property (go compute)
						 if( ((SimilarityFloodingMatcherParameters)param).omitAnonymousNodes && 
								 ( sEdge.getOrigin().getObject().isAnon() || sEdge.getDestination().getObject().isAnon() ||
								   tEdge.getOrigin().getObject().isAnon() || tEdge.getDestination().getObject().isAnon() )  ) {
							// these nodes are anonymous
							// parameter is set to not insert anonymous nodes
							// do nothing
						 } else {
							 try{
					 				PCGVertex sourcePCGVertex = getPCGVertex((WGraphVertex)sEdge.getOrigin(), (WGraphVertex)tEdge.getOrigin());
						 			PCGVertex targetPCGVertex = getPCGVertex((WGraphVertex)sEdge.getDestination(), (WGraphVertex)tEdge.getDestination());
						 			PCGEdge pairEdge = new PCGEdge(sourcePCGVertex, targetPCGVertex, new PCGEdgeData(sEdge.getObject()));
//								 	System.out.println(sourcePCGVertex.toString() + " ---> " + pairEdge.getObject().getStProperty() + " ----> " + targetPCGVertex.toString());
						 			if(!sourcePCGVertex.isVisited()){
						 				sourcePCGVertex.setVisited(true);
						 				pcg.insertVertex(sourcePCGVertex);
						 			}
						 			if(!targetPCGVertex.isVisited()){
						 				targetPCGVertex.setVisited(true);
						 				pcg.insertVertex(targetPCGVertex);
						 			}
						 			pcg.insertEdge(sourcePCGVertex,  // vertex
								 		pairEdge,      		// edge
								 		targetPCGVertex		// vertex
								 		);
							 }
							 catch(com.hp.hpl.jena.rdf.model.ResourceRequiredException e){
								 e.printStackTrace();
							 }
						 }
						 
					 }
					 else{
						 // target property is greater than source property
						 // (since egdes are sorted we break the target cycle and go to the next source edge)
						 break;
					 }
				 }
				 targetIterator = targetOnt.edges();
			 }
	 }
	 
	protected boolean createPartialPCG(PCGVertex pcgV){

		if(pcgV.getObject().getStCouple().getLeft().getObject().toString().contains("numberOrVolume") &&
				pcgV.getObject().getStCouple().getRight().getObject().toString().contains("sbqgzga")){
			System.out.println();
		}
		
		 if(pcgV.isVisited()){
			 return false;	
		 }
		 else{
			 pcgV.setVisited(true);
//			 System.out.println(pcgV.toString() + " isVisited: " + pcgV.isVisited());

			 // for the Incoming edges
			 lookupEdges(pcgV.getObject().getStCouple().getLeft().edgesInList(),
					 pcgV.getObject().getStCouple().getRight().edgesInList(),
					 EdgeDirection.IN);
			 // for the Outgoing edges
			 lookupEdges(pcgV.getObject().getStCouple().getLeft().edgesOutList(),
					 pcgV.getObject().getStCouple().getRight().edgesOutList(),
					 EdgeDirection.OUT);
			 
			 return this.pcg.getVertices().size() > 0;
		 }
	 }
	 
	 // VERTICES OPERATIONS //
	 
	 /**
	  * Returns the PCGVertex associated with the source concept 's' and target concept 's'. 
	  * @param s
	  * @param t
	  * @return Returns the PCGVertex.
	  */	 
	 protected PCGVertex getPCGVertex(WGraphVertex s, WGraphVertex t){
		 
		 String key = new String(s.getObject().toString() + t.getObject().toString());
		 PCGVertex vert = pairTable.get(key);
//		 System.out.println(pairTable.get(pairToCheck) != null);
		 
		 // there wasn't already that vertex (create it)
		 if(vert == null){
			 vert = new PCGVertex(s, t);
			 
			 // add it to the map of nodes (we will always include it in the set of nodes to search in)
			 pairTable.put(key, vert);
		 }
		 return vert;
	 }

	 /**
	  * Reset PCGNodes 
	  */	 
	 protected void unsetVisitedPCGVert(HashMap<String, PCGVertex> vertMap){
		 
		 Iterator<PCGVertex> iVert = vertMap.values().iterator();
		 PCGVertex vert = null;
		 
		 while(iVert.hasNext()){
			 vert = iVert.next();
			 // add it to the list
			 vert.setVisited(false);
			 // add it to the map of nodes (we will always include it in the set of nodes to search in)
			 //pairTable.put(key, vert);
		 }
	 }
	 
	 protected void unsetInsertedPCGElements(HashMap<String, ?> elementsMap){
		 
		 Iterator<?> iterator = elementsMap.values().iterator();
		 Object nextElement = null;
		 
		 while(iterator.hasNext()){
			 nextElement = iterator.next();
			 // add it to the list
			 if(nextElement.getClass().equals(PCGEdge.class)){
				 PCGEdge edge = (PCGEdge) nextElement;
				 edge.setInserted(false);
			 }
			 else if(nextElement.getClass().equals(PCGEdge.class)){
				 PCGVertex vert = (PCGVertex) nextElement;
				 vert.setInserted(false);
			 }
		 }
	 }
	 
	 // EDGES OPERATIONS //
	 
	 protected void lookupEdges(ArrayList<DirectedGraphEdge<String, RDFNode>> s, ArrayList<DirectedGraphEdge<String, RDFNode>> t, EdgeDirection ed){
		 
		 Iterator<DirectedGraphEdge<String, RDFNode>> sourceIterator = s.iterator();
		 Iterator<DirectedGraphEdge<String, RDFNode>> targetIterator = t.iterator();
		 
		 WGraphEdge sEdge = null;
		 WGraphEdge tEdge = null;
		 int edgeComparison = 0;

		 while(sourceIterator.hasNext()){
			 sEdge = (WGraphEdge) sourceIterator.next();
			 
			 /*if(sourceIterator.hasNext()){
				 System.out.println();
			 }*/
			 
			 while(targetIterator.hasNext()){
				 tEdge = (WGraphEdge) targetIterator.next();

				 // comparing edges here
				 edgeComparison = compareEdges(sEdge, tEdge);
				 if(edgeComparison == 0){
					 System.out.println();
					 insertInPCG(sEdge, tEdge);
					 switch(ed){
					 case IN:
						 createPartialPCG(getPCGVertex((WGraphVertex) sEdge.getOrigin(), (WGraphVertex) tEdge.getOrigin()));
						 break;
					 case OUT:
						 createPartialPCG(getPCGVertex((WGraphVertex) sEdge.getDestination(), (WGraphVertex) tEdge.getDestination()));
						 break;
					 default:
						 try {
							 throw new Exception("Should not be here. Make sure EdgeDirection is provided and not null");
						 } catch (Exception e) {
							 e.printStackTrace();
						 }
					 }
					 
				 }
				 else{
					 // what to do in case prop are not equal
					 if(isSortEdges()){
						 // Conditions where we add a new element in the pairwise connectivity graph
						 // when egdes are sorted. Predicates comparison involves string comparison
						 if(edgeComparison > 0){
							// target property is smaller than source property (continue to cycle on target edges)
							continue; 
						 }
						 else{
							 // target property is greater than source property (break the cycle on target edges)
							 break;
						 }
					 }
					 else{
						 // Conditions where we add a new element in the pairwise connectivity graph
						 // when egdes are NOT sorted. Predicates comparison involves string comparison
						 continue;
					 }
				 }
			 }
			 targetIterator = t.iterator();
		 }
	 }
					
	 protected PCGEdge getEdge(PCGVertex pcgV, String edgeLabel, PCGVertex pcgV2) {
	 	PCGEdge edgeNew = null;
	 	try{
	 		edgeNew = edgesMap.get(pcgV.toString() + edgeLabel + pcgV2.toString());
//	 		System.out.println(edgeNew);
	 	} catch(Exception e){
	 		e.printStackTrace();
	 	}
	 	
		if (edgeNew == null) {

			// we don't have that edge, we create it
			edgeNew = new PCGEdge(pcgV, pcgV2, new PCGEdgeData(edgeLabel));
			edgesMap.put(pcgV.toString() + edgeLabel + pcgV2.toString(), edgeNew);
		}
		return edgeNew;
	}
	 
	 protected int compareEdges(WGraphEdge sEdge, WGraphEdge tEdge){
		 return sEdge.getObject().compareTo(tEdge.getObject());
	 }
	 
	 /**
	  * This method inserts a new triple (vertex, edge, vertex) in the PairwiseConnectivityGraph. 
	  * It checks for duplicates
	  */
	 private void insertInPCG(WGraphEdge sEdge, WGraphEdge tEdge) {

		 PCGVertex sourcePCGVertex = getPCGVertex((WGraphVertex)sEdge.getOrigin(), (WGraphVertex)tEdge.getOrigin());
		 PCGVertex targetPCGVertex = getPCGVertex((WGraphVertex)sEdge.getDestination(), (WGraphVertex)tEdge.getDestination());
		 PCGEdge pairEdge = getEdge(sourcePCGVertex, sEdge.getObject(), targetPCGVertex);

//		 try {
//				fw.append("About to insert " + sourcePCGVertex + " but isInserted:" + sourcePCGVertex.isInserted() + "\n");
//				fw.append("About to insert " + targetPCGVertex + " but isInserted:" + targetPCGVertex.isInserted() + "\n");
//				fw.append("About to insert " + pairEdge + " but isInserted:" + pairEdge.isInserted() + "\n");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		 if(!sourcePCGVertex.isInserted()){

//			 System.out.println(sourcePCGVertex);
			 pcg.insertVertex(sourcePCGVertex);
			 sourcePCGVertex.setInserted(true);
		 }
		 if(!targetPCGVertex.isInserted()){
//			 System.out.println(targetPCGVertex);
			 pcg.insertVertex(targetPCGVertex);
			 targetPCGVertex.setInserted(true);
		 }
		 if(!pairEdge.isInserted()){
//			 System.out.println(pairEdge);
			 pcg.insertEdge(pairEdge);
			 sourcePCGVertex.addOutEdge(pairEdge);
			 targetPCGVertex.addInEdge(pairEdge);
			 pairEdge.setInserted(true);
		 }

	 }
		
	 /* *********************************************************************************** */
	 /*		 				INDUCED PROPAGATION GRAPH OPERATIONS							*/
	 /* *********************************************************************************** */
	 
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
	 
	 /* *********************************************************************************** */
	 /*		 							COMPUTING THE FIXPOINT								*/
	 /* *********************************************************************************** */
	 
	 protected void computeFixpoint(){
		 int round = 0;
		 double maxSimilarity = 0.0;
		 
		 do {
			 // new round starts
			 round++;
			 
			 // update old value with new value of previous round
			 updateOldSimValues(pcg.vertices(), round);
			 
			 // compute fixpoint round and max value per that round
			 maxSimilarity = computeFixpointRound(pcg.vertices());
			 
			 // normalize all the similarity values of all nodes
			 normalizeSimilarities(pcg.vertices(), maxSimilarity);

			 // stop condition check: delta or maxRound
			 populateSimilarityMatrices(pcg, classesMatrix, propertiesMatrix);

			 Vector<Double> oldV = pcg.getSimValueVector(fw, round, true);
			 Vector<Double> newV = pcg.getSimValueVector(fw, round, false);
//			 Vector<Double> newV = propertiesMatrix.toSimilarityArray(propertiesMatrix.toMappingArray(fw, round));
//			 newV.addAll(classesMatrix.toSimilarityArray(classesMatrix.toMappingArray(fw, round)));

			 try {
					fw.append("old: " + oldV.size() + "\n");
					fw.append("new: " + newV.size() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

		 } while(!checkStopCondition(round, pcg.getSimValueVector(true), pcg.getSimValueVector(false)));
	 }
	 
	 private void updateOldSimValues(Iterator<PCGVertex> iVert, int round){
		 PCGVertex vert = null;
		 if(round != 1){
			 while(iVert.hasNext()){
				 // take the current vertex
				 vert = iVert.next();
				 vert.getObject().setOldSimilarityValue(vert.getObject().getNewSimilarityValue());
//				 if(true){System.out.println(vert.toString());}
			 }
		 }
	 }
	 
	 protected void updateOldSimValues(SimilarityMatrix matrixOld, SimilarityMatrix matrixNew, double round) {
		 Mapping current = null;
		 
		 for(int i = 0; i < matrixOld.getRows(); i++){
			 for(int j = 0; j < matrixOld.getColumns(); j++){
				 
				 current = matrixOld.get(i, j);
				 if(current != null){
					 current.setSimilarity(matrixNew.get(i,j).getSimilarity());
					 matrixOld.set(i, j, current);
				 }
			 }
		 }
	 }	
	 
	 protected double computeFixpointRound(Iterator<PCGVertex> iVert){
		 double maxSimilarity = 0.0, newSimilarity = 0.0;
		 PCGVertex vert = null;
		 PCGVertex maxV = null;
		 
		 while(iVert.hasNext()){
			 
			 // take the current vertex
			 vert = iVert.next();
//			 if(true){System.out.println(vert.toString());}
			 
			 // compute the new similarity value for that vertex
			 newSimilarity = computeFixpointPerVertex(vert);
			 
			 // store it inside the vertex
			 vert.getObject().setNewSimilarityValue(newSimilarity);
			 				
			 // track the maximum similarity
			 if(maxSimilarity < newSimilarity){
				 maxV = vert;
				 maxSimilarity = newSimilarity;
//				 System.out.println("maxSim: " + maxSimilarity + " ------------------- " + "maxV: " + maxV.toString() + "\n");
				 
			 }
//			 if(true){System.out.println(vert.toString());}
			 System.out.println("maxSim: " + maxSimilarity + " ------------------- " + "maxV: " + maxV.toString() + "\n");
		 }
		 return maxSimilarity;
	 }

	 /* *********************************************************************************** */
	 /* 							INDUCED PROPAGATION GRAPH OPERATIONS					*/
	 /* *********************************************************************************** */
	 
	 public void applyCoefficients(){
		 Iterator<PCGVertex> iVert = pcg.vertices();
		 PCGVertex currentVert = null;
		 while(iVert.hasNext()){
			 
			 // assigning outgoing propagation coefficients
			 HashMap<String, Integer> counter = new HashMap<String, Integer>();
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
			 HashMap<String, Integer> counter = new HashMap<String, Integer>();
			 
			 // counting phase (with ingoing edges)
			 computeQuantities(currentVert.edgesInIter(), counter, true);

			 // back-edge creation and weight assignment phase (with ingoing edges)
			 createBackedges(currentVert.edgesInIter(), counter);
		 }
	 }
	 
	 private void computeQuantities(Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> iVEdge,
			 						HashMap<String, Integer> counter,
			 						boolean computing){
		 PCGEdge currentEdge = null;
		 while(iVEdge.hasNext()){
			 
			 currentEdge = (PCGEdge) iVEdge.next();
			 String currentProp = currentEdge.getObject().getStProperty();
			 
			 if(computing){ // computing phase
				 if( counter.containsKey(currentProp) ) {
					 Integer i = counter.get(currentProp);
					 counter.put(currentProp, i + 1);
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
				HashMap<String, Integer> counter){
		 
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
				 
				 String currentProp = currentEdge.getObject().getStProperty();
				 double tmpPC = MAX_PC / counter.get(currentProp).doubleValue();
				 
				 PCGVertex originOfBackedge = (PCGVertex)currentEdge.getDestination();
				 PCGVertex destinationOfBackedge = (PCGVertex)currentEdge.getOrigin();
				 
				 PCGEdge backedge = new PCGEdge(   originOfBackedge,destinationOfBackedge, new PCGEdgeData(null, tmpPC) );
			
				 //adding new edge
				 originOfBackedge.addOutEdge(backedge);
				 destinationOfBackedge.addInEdge(backedge);
				 
				 pcg.insertEdge(backedge);
			 }
		 }
	 }
	 
	 protected boolean checkStopCondition(int round, Vector<Double> simVectBefore, Vector<Double> simVectAfter){
		return checkStopCondition(round, ROUND_MAX, simVectBefore, simVectAfter);
	 }
	 /**
	  * 
	  */
	 protected boolean checkStopCondition(int round, int max_round, Vector<Double> simVectBefore, Vector<Double> simVectAfter){
//		 System.out.println(round);
//		 System.out.println(simVectBefore.toString() );
//		 System.out.println(simVectAfter.toString() );
//		 System.out.println(simDistance(simVectBefore, simVectAfter) );
		 return ((round > max_round) || (simDistance(simVectBefore, simVectAfter) < DELTA));
	 }

	 protected double simDistance(Vector<Double> simVectBefore, Vector<Double> simVectAfter){
		 double simD = 0.0, diff = 0.0;
		 assert (simVectBefore.size() == simVectAfter.size()); // size of both vectors should always be the same
		 
		 // computing euclidean distance
		 for(int i = 0; i < simVectAfter.size(); i++){
			 diff = simVectBefore.get(i) - simVectAfter.get(i);
			 simD += (diff * diff);
		 }
//		 System.out.println("delta: " + Math.sqrt(simD));
		 try {
			 fw.append("before: " + simVectBefore + "\n");
			 fw.append("after: " + simVectAfter + "\n");
			 fw.append("delta: " + Math.sqrt(simD) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		 return Math.sqrt(simD);
	 }
	 
	 /**
	  * 
	  */
	 protected double computeFixpointPerVertex(PCGVertex pcgV){
		 return pcgV.getObject().getOldSimilarityValue()	// old value (correct, we are supposed to find the old value there)
		 			+ sumIncomings(pcgV.edgesInIter());		// sum of incoming regular edges values
		 //			+ sumBackedges(pcgV.edgesOutIter());	// sum of incoming backedge values
	 }
	 
	 protected double sumIncomings(Iterator<DirectedGraphEdge<PCGEdgeData, PCGVertexData>> inIter){
		 
		 double sum = 0.0, oldValue = 0.0, propCoeff = 0.0;
		 PCGEdge currEdge = null;
//		 System.out.println();
		 while(inIter.hasNext()){
			 currEdge = (PCGEdge) inIter.next();
			 // computing old sim value multiplied by the prop coefficient of the regular incoming edge
			 oldValue = currEdge.getOrigin().getObject().getOldSimilarityValue();
			 propCoeff = currEdge.getObject().getPropagationCoefficient();
			 
			 sum += (oldValue * propCoeff);
//			 try {
//				 fw.append(currEdge.getOrigin().toString() + " ---> " + currEdge.getDestination().toString() + "\n");
//				 fw.append(oldValue + " " + propCoeff + "\n");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			 System.out.println(currEdge.getOrigin().toString() + " ---> " + currEdge.getDestination().toString());
//			 System.out.println(oldValue + " " + propCoeff);
		 }
		 return sum;
	 }
	 
	 /**
	  * not called because sumIncomings is taking care of this too
	  * @param outIter
	  * @return
	  */
	 @Deprecated
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
		 
		 protected void normalizeSimilarities(Iterator<PCGVertex> iVert, double roundMax){
			 double nonNormSimilarity = 0.0, normSimilarity = 0.0;
			 PCGVertex currentVert = null;
			 while(iVert.hasNext()){
				 currentVert = iVert.next();
				 
				 // the value computed is stored in the new similarity value at this stage
				 nonNormSimilarity = currentVert.getObject().getNewSimilarityValue();
				 
				 // compute normalized value
				 normSimilarity = nonNormSimilarity / roundMax;
				 
				 // set normalized to new value
				 currentVert.getObject().setNewSimilarityValue(normSimilarity);
//				 System.out.println(currentVert.toString());
			 }
		 }
		 
		 protected void normalizeSimilarities(SimilarityMatrix localMatrix, double roundMax) {
			 double nonNormSimilarity = 0.0, normSimilarity = 0.0;
			 Mapping current = null;
			 
			 for(int i = 0; i < localMatrix.getRows(); i++){
				 for(int j = 0; j < localMatrix.getColumns(); j++){
					 
					 current = localMatrix.get(i, j);
					 if(current != null){
						// the value computed is stored in the new similarity value at this stage
						 nonNormSimilarity = current.getSimilarity();
						 
						 // compute normalized value
						 normSimilarity = nonNormSimilarity / roundMax;
						 
						 // set normalized to new value
						 localMatrix.get(i, j).setSimilarity(normSimilarity);
					 }
					 
				 }
			 }
		 }	
		 
		 /* *************************************************** */
		 /* 				RELATIVE SIMILARITIES 				*/
		 /* *************************************************** */
		 
		 /**
		  * working on similarity matrices only
		  */
		 protected void computeRelativeSimilarities(SimilarityMatrix matrix){
			 
			 double max = 0;
			 double oldValue = 0;
			 Mapping current = null;
			 
			 for(int i = 0; i < matrix.getRows(); i++){
				 max = matrix.getRowMaxValues(i, 1)[0].getSimilarity();
				 
				 for(int j = 0; j < matrix.getColumns(); j++){
					 
					 current = matrix.get(i, j);
					 if(current != null){
						 oldValue = current.getSimilarity();
						 matrix.get(i, j).setSimilarity(oldValue/max);
					 }
					 
				 }
			 }
		 }

	 /* *************************************************** */
	/* 				SIMILARITY MATRICES CREATION 			*/
	/* *************************************************** */
		 
	 protected void populateSimilarityMatrices(PairwiseConnectivityGraph pcg, SimilarityMatrix cMatrix, SimilarityMatrix pMatrix){
		 Iterator<PCGVertex> iVert = pcg.vertices();
		 PCGVertex currentVert = null;
		 while(iVert.hasNext()){
			 currentVert = iVert.next();
			 Mapping m = currentVert.toMapping(sourceOntology, targetOntology);
			 // take both source and target ontResources (values can be null, means not possible to take resources

			 if(m != null){
				 if(currentVert.representsClass()){
					 cMatrix.set(m.getSourceKey(), m.getTargetKey(), m);
				 }
				 else if(currentVert.representsProperty()){
					 pMatrix.set(m.getSourceKey(), m.getTargetKey(), m);
				 }
				 else{
					 // TODO: manage type error
				 }
			 }
			 else{
				 // manage here the case where you may be using the PCG MATRIX (only classes, properties use the old matrix
				 if(cMatrix.getClass().equals(PCGSimilarityMatrix.class)){
					 WGraphVertex row = currentVert.getObject().getStCouple().getLeft();
					 WGraphVertex col = currentVert.getObject().getStCouple().getRight();
					 if(row.getMatrixIndex() < ((PCGSimilarityMatrix)cMatrix).getRows()
							 && col.getMatrixIndex() < ((PCGSimilarityMatrix)cMatrix).getColumns()){
						 cMatrix.set(row.getMatrixIndex(),
								 		col.getMatrixIndex(),
								 		new Mapping(new Node(row.getMatrixIndex(), row.getObject().toString(), Node.OWLCLASS, sourceOntology.getID()),
								 					new Node(col.getMatrixIndex(), col.getObject().toString(), Node.OWLCLASS, targetOntology.getID()),
								 					currentVert.getObject().getNewSimilarityValue()));
					 }
				 }
				 continue;
			 }
		 }
	 }
		 
		 /*/ Creating plots for classes and properties
		 MatrixPlotPanel mp;
		 
		 mp = new MatrixPlotPanel( null, getClassesMatrix(), null);
		 mp.getPlot().draw(false);
		 JPanel plotPanelC = new JPanel();
		 plotPanelC.add(mp);
		 Core.getUI().addTab("MatrixPlot Class", null , plotPanelC , this.getName().toString());
		 
		 mp = new MatrixPlotPanel( null, getPropertiesMatrix(), null);
		 mp.getPlot().draw(false);
		 JPanel plotPanelP = new JPanel();
		 plotPanelP.add(mp);
		 Core.getUI().addTab("MatrixPlot Properties", null , plotPanelP , this.getName().toString());
		 try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		 
	/* *************************************************** */
	/* 					  SUPPORT FUNCTIONS 			   */
	/* *************************************************** */
	 
	 /**
	  * 
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
//				 System.out.println(currentEdge.getDestination() + " ---- " + destinatonVertex + "\n");
				 return true;
			 }
		 }
		 return false; 
	 }

	 /**
	 * @return the sortEdges
	 */
	public boolean isSortEdges() {
		return sortEdges;
	}

	/**
	 * @param sortEdges the sortEdges to set
	 */
	public void setSortEdges(boolean sortEdges) {
		this.sortEdges = sortEdges;
	}

	protected void loadSimilarityMatrices(){
		;
	}
	protected void loadSimilarityMatrices(WrappingGraph s, WrappingGraph t){
		;
	}

	protected abstract PCGVertexData selectInput(Pair<RDFNode, RDFNode> pair);

			
}
