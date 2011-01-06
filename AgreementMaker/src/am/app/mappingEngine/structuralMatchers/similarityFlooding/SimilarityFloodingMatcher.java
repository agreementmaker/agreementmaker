/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JPanel;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdgeData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertexData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PairwiseConnectivityGraph;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.utility.DirectedGraphEdge;
import am.utility.Pair;
import am.visualization.matrixplot.MatrixPlotPanel;

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
	protected static final boolean DEBUG_FLAG = false;
	
	protected PairwiseConnectivityGraph pcg;
	
	public static final double MAX_PC = 1.0; // maximum value for propagation coefficient
	public static final double DELTA = 0.1; // min value for differentiating two similarity vectors
	public static final int MAX_ROUND = 10; // maximum numbers of rounds for fixpoint computation
	
	/**
	 * given two nodes named origin and destination we have a list of the possible 
	 * directions of an edge connecting them
	 */
	private enum Direction{ORIG2DEST, DEST2ORIG};
	
	private HashMap<String, PCGVertex> pairTable;

	/**
	 * 
	 */
	public SimilarityFloodingMatcher() {
		super();
		pairTable = new HashMap<String, PCGVertex>();
		needsParam = true; // we need to display the parameters panel.
	}

	/**
	 * @param params_new
	 */
	public SimilarityFloodingMatcher(SimilarityFloodingMatcherParameters params_new) {
		super(params_new);
		pairTable = new HashMap<String, PCGVertex>();
		needsParam = true; // we need to display the parameters panel.
	}
	
	@Override 
	public AbstractMatcherParametersPanel getParametersPanel() { return new SimilarityFloodingParametersPanel(); };
	
	/**
	 * Similarity Flooding Algorithm. 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * NOTE: we are using graphs instead of arrayList
	 */
	 protected void align() throws Exception {
		progressDisplay.clearReport();
		loadSimilarityMatrices();
		
		// cannot align just one ontology (this is here to catch improper invocations)
		if( sourceOntology == null ) throw new NullPointerException("sourceOntology == null");   
		if( targetOntology == null ) throw new NullPointerException("targetOntology == null");
		
		progressDisplay.appendToReport("Creating Wrapping Graphs...");
		WrappingGraph sourceGraph = new WrappingGraph(sourceOntology.getModel());
		WrappingGraph targetGraph = new WrappingGraph(targetOntology.getModel());
		if( DEBUG_FLAG ) System.out.println(sourceGraph.toString());
		if( DEBUG_FLAG ) System.out.println(targetGraph.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Sorting Wrapping Graphs...");
		sourceGraph.sortEdges();
		targetGraph.sortEdges();
		if( !DEBUG_FLAG ) System.out.println(sourceGraph.toString());
		if( !DEBUG_FLAG ) System.out.println(targetGraph.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Creating Pairwise Connectivity Graph...");
		createPairwiseConnectivityGraph(sourceGraph, targetGraph);
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Creating Induced Propagation Graph...");
		createInducedPropagationGraph();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Computing Fixpoints...");
		computeFixpoint();
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Creating Similarity Matrices...");
		createSimilarityMatrices();
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Computing Relative Similarities...");
		computeRelativeSimilarities();
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
	 protected void createPairwiseConnectivityGraph(WrappingGraph sourceOnt, WrappingGraph targetOnt){
		 pcg = new PairwiseConnectivityGraph();
		 
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
								 	String originKey = new String(sEdge.getOrigin().getObject().toString() + tEdge.getOrigin().getObject().toString());
								 	String destinationKey = new String(sEdge.getDestination().getObject().toString() + tEdge.getDestination().getObject().toString());
								 	
					 				PCGVertex sourcePCGVertex = getPCGVertex(originKey, sEdge.getOrigin().getObject(), tEdge.getOrigin().getObject());
						 			PCGVertex targetPCGVertex = getPCGVertex(destinationKey, sEdge.getDestination().getObject(), tEdge.getDestination().getObject());
						 			PCGEdge pairEdge = new PCGEdge(sourcePCGVertex, targetPCGVertex, new PCGEdgeData(sEdge.getObject()));
//								 	System.out.println(sourcePCGVertex.toString() + " ---> " + pairEdge.getObject().getStProperty() + " ----> " + targetPCGVertex.toString());		 
						 			insertEdgeInPCG(sourcePCGVertex,  // vertex
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
	 
	 protected void createPairwiseConnectivityGraphNew(WrappingGraph sourceOnt, WrappingGraph targetOnt){
		 pcg = new PairwiseConnectivityGraph();
		 
		 WGraphEdge[] sourceVector = (WGraphEdge[]) sourceOnt.getEdges().toArray(new WGraphEdge[sourceOnt.getEdges().size()]);
		 WGraphEdge[] targetVector = (WGraphEdge[]) targetOnt.getEdges().toArray(new WGraphEdge[targetOnt.getEdges().size()]);
		 
		 WGraphEdge sEdge = null;
		 WGraphEdge tEdge = null;
		 
		 int targetNewIndex = 0;
		 
		 for(int i = 0; i < sourceVector.length; i++){
			 sEdge = sourceVector[i];
			 
			 for(int j = targetNewIndex; j < targetVector.length; j++){
				 tEdge = targetVector[j];
				 
				 System.out.println(sEdge.toString());
				 System.out.println(tEdge.toString());
				 System.out.println();
				 
				 // condition where we add a new element in the pairwise connectivity graph:
				 // comparison of predicates (now string labels)
/*				 if(sEdge.getObject().compareTo(tEdge.getObject()) > 0){
					// target property is smaller than source property (continue to cycle on target edges)
					continue; 
				 }
				 else*/ if(sEdge.getObject().equals(tEdge.getObject())){
					 // target property is equal to source property (go compute)
					 if( ((SimilarityFloodingMatcherParameters)param).omitAnonymousNodes && 
							 ( sEdge.getOrigin().getObject().isAnon() || sEdge.getDestination().getObject().isAnon() ||
							   tEdge.getOrigin().getObject().isAnon() || tEdge.getDestination().getObject().isAnon() )  ) {
						// these nodes are anonymous
						// parameter is set to not insert anonymous nodes
						// do nothing
					 } else {
						 try{
							 	String originKey = new String(sEdge.getOrigin().getObject().toString() + tEdge.getOrigin().getObject().toString());
							 	String destinationKey = new String(sEdge.getDestination().getObject().toString() + tEdge.getDestination().getObject().toString());
							 	
				 				PCGVertex sourcePCGVertex = getPCGVertex(originKey, sEdge.getOrigin().getObject(), tEdge.getOrigin().getObject());
					 			PCGVertex targetPCGVertex = getPCGVertex(destinationKey, sEdge.getDestination().getObject(), tEdge.getDestination().getObject());
					 			PCGEdge pairEdge = new PCGEdge(sourcePCGVertex, targetPCGVertex, new PCGEdgeData(sEdge.getObject()));
//							 	System.out.println(sourcePCGVertex.toString() + " ---> " + pairEdge.getObject().getStProperty() + " ----> " + targetPCGVertex.toString());		 
					 			insertEdgeInPCG(sourcePCGVertex,  // vertex
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
					 // first condition is an array out of bound check
					 // (since egdes are sorted we break the target cycle and go to the next source edge)
					 if( ((i + 1) < sourceVector.length) && (sourceVector[i].equals(sourceVector[i+1])) ){
						 
					 }
					 else{
						 targetNewIndex = j;
					 }
					 break;
				 }

			 }
		 }
		 
	 }
	
	/**
	 * This method inserts a new "triple" in the PairwiseConnectivityGraph.
	 * A triple is a ( PCGVertex, PCGEdge, PCGVertex ).
	 */
	private void insertEdgeInPCG(PCGVertex sourcePCGVertex, PCGEdge pairEdge, PCGVertex targetPCGVertex) {
		
		sourcePCGVertex.addOutEdge(pairEdge);
		targetPCGVertex.addInEdge(pairEdge);
		
		pcg.insertEdge(pairEdge);
		
	}

	/**
	  * Returns the PCGVertex associated with the source concept 's' and target concept 's'. 
	  * @param key
	  * @param s
	  * @param t
	  * @return Returns null if no PCGVertex exists.
	  */	 
	 private PCGVertex getPCGVertex(String key, RDFNode s, RDFNode t){
		 
		 PCGVertex vert = pairTable.get(key);
//		 System.out.println(pairTable.get(pairToCheck) != null);
		 
		 // there was already a vertex in the table (do nothing)
		 if(pairTable.get(key) != null){
//			 	System.out.println("table has pair");
			}
		// there wasn't already that vertex (create it)
		else{
			vert = new PCGVertex(new PCGVertexData(new Pair<RDFNode, RDFNode>(s, t)));
//			System.out.println(vertNew.toString());
			// add it to the list
			pcg.insertVertex(vert);
			// add it to the map of nodes (we will always include it in the set of nodes to search in)
			pairTable.put(key, vert);
		}
		 return vert;
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
			 maxSimilarity = 0.0;
			 Iterator<PCGVertex> iVert = null;
			 PCGVertex vert = null;
//			 PCGVertex maxV = null;
			 
			 // update old value with new value of previous round
			 iVert = pcg.vertices();
			 if(round != 1){
				 while(iVert.hasNext()){
					 // take the current vertex
					 vert = iVert.next();
					 vert.getObject().setOldSimilarityValue(vert.getObject().getNewSimilarityValue());
//					 if(true){System.out.println(vert.toString());}
				 }
			 }

			 iVert = pcg.vertices();
			 while(iVert.hasNext()){
				 
				 // take the current vertex
				 vert = iVert.next();
//				 if(true){System.out.println(vert.toString());}
				 
				 // compute the new similarity value for that vertex
				 newSimilarity = computeFixpointPerVertex(vert);
				 
				 // store it inside the vertex
				 vert.getObject().setNewSimilarityValue(newSimilarity);
				 				
				 if(maxSimilarity <= newSimilarity){
//					 maxV = vert;
					 maxSimilarity = newSimilarity;
//					 System.out.println("maxSim: " + maxSimilarity + " ------------------- " + "maxV: " + maxV.toString() + "\n");
					 
				 }
//				 if(true){System.out.println(vert.toString());}
			 }
			 // normalize all the similarity values of all nodes (and updates oldSimilarities for next round
//			 System.out.println("maxSim: " + maxSimilarity + " ------------------- " + "maxV: " + maxV.toString() + "\n");
			 normalizeSimilarities(pcg.vertices(), maxSimilarity);
			 
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
	 
	 /**
	  * 
	  */
	 protected boolean checkBaseCase(int round, Vector<Double> simVectBefore, Vector<Double> simVectAfter){
//		 System.out.println(round);
//		 System.out.println(simVectBefore.toString() );
//		 System.out.println(simVectAfter.toString() );
//		 System.out.println(simDistance(simVectBefore, simVectAfter) );
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
//		 System.out.println("delta: " + Math.sqrt(simD));
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
		 
		 /* *************************************************** */
		 /* 				RELATIVE SIMILARITIES 				*/
		 /* *************************************************** */
		 
		 /**
		  * working on similarity matrices only
		  * TODO: (improvement) works on one SimilarityMatrix at a time given as parameter
		  */
		 protected void computeRelativeSimilarities(){
			 
			 double max = 0;
			 double oldValue = 0;
			 Mapping current = null;
			 
			 for(int i = 0; i < classesMatrix.getRows(); i++){
				 max = classesMatrix.getRowMaxValues(i, 1)[0].getSimilarity();
				 
				 for(int j = 0; j < classesMatrix.getColumns(); j++){
					 
					 current = classesMatrix.get(i, j);
					 if(current != null){
						 oldValue = current.getSimilarity();
						 classesMatrix.get(i, j).setSimilarity(oldValue/max);
					 }
					 
				 }
			 }
			 
			 for(int i = 0; i < propertiesMatrix.getRows(); i++){
				 max = propertiesMatrix.getRowMaxValues(i, 1)[0].getSimilarity();
				 
				 for(int j = 0; j < propertiesMatrix.getColumns(); j++){
					 
					 current = propertiesMatrix.get(i, j);
					 if(current != null){
						 oldValue = current.getSimilarity();
						 propertiesMatrix.get(i, j).setSimilarity(oldValue/max);
					 }
					 
				 }
			 }
			 
		 }

	 /* *************************************************** */
	/* 				SIMILARITY MATRICES CREATION 			*/
	/* *************************************************** */
	 
	 protected void createSimilarityMatrices(){
		 Iterator<PCGVertex> iVert = pcg.vertices();
		 PCGVertex currentVert = null;
		 while(iVert.hasNext()){
			 currentVert = iVert.next();
			 
			 // take both source and target ontResources (values can be null, means not possible to take resources
			 OntResource sourceRes = getOntResourceFromRDFNode(currentVert.getObject().getStCouple().getLeft());
			 OntResource targetRes = getOntResourceFromRDFNode(currentVert.getObject().getStCouple().getRight());
			 if(sourceRes != null && targetRes != null){
				
				 Mapping m;
				 // try to get the Node and check they belong to the same alignType
				 Node sourceClass = getNodefromOntResource(sourceOntology, sourceRes, alignType.aligningClasses);
				 Node targetClass = getNodefromOntResource(targetOntology, targetRes, alignType.aligningClasses);
				 // test if both nodes are classes
				 if(sourceClass == null || targetClass == null){
					 Node sourceProperty = getNodefromOntResource(sourceOntology, sourceRes, alignType.aligningProperties);
					 Node targetProperty = getNodefromOntResource(targetOntology, targetRes, alignType.aligningProperties);
					 // test if both nodes are properties
					 if(sourceProperty == null || targetProperty == null){
						 continue;
					 }
					 else{
						 // the necessary similarity value is stored in the newSimilarityValue var
						 m = new Mapping(sourceProperty, targetProperty, currentVert.getObject().getNewSimilarityValue());
						 propertiesMatrix.set(sourceProperty.getIndex(), targetProperty.getIndex(), m);
					 }
				 }
				 else{
					 // the necessary similarity value is stored in the newSimilarityValue var
					 m = new Mapping(sourceClass, targetClass, currentVert.getObject().getNewSimilarityValue());
					 classesMatrix.set(sourceClass.getIndex(), targetClass.getIndex(), m);
				 }
			 }
			 else{
				 continue;
			 }
			 
		 }
		 
		 // Creating plots for classes and properties
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

	 }
	 
	 
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
	 
	 protected OntResource getOntResourceFromRDFNode(RDFNode node){
		 // try to get the ontResource from them
		 if(node.canAs(OntResource.class)){
			 return node.as(OntResource.class);
		 }
		 else{
			 return null;
		 }
	 }
	 
	 protected boolean isOntResInSimMatrix(OntResource res, Ontology ont){
		 boolean isResourceClass = getNodefromOntResource(ont, res, alignType.aligningClasses) != null;
		 boolean isResourceProperty = getNodefromOntResource(ont, res, alignType.aligningProperties) != null;
		 return (isResourceClass || isResourceProperty);
	 }
	 
	 protected Node getNodefromOntResource(Ontology ont, OntResource res, alignType aType){
		 try{
			 return ont.getNodefromOntResource(res, aType);
		 }
		 catch(Exception eClass){
			 return null;
		 }
	 }

	 protected abstract void loadSimilarityMatrices();
	
	 protected abstract PCGVertexData selectInput(Pair<RDFNode, RDFNode> pair);

}
