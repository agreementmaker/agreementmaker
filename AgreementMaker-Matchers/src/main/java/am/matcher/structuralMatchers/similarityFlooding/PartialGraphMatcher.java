package am.matcher.structuralMatchers.similarityFlooding;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import am.AMException;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.NodeUtility;
import am.matcher.structuralMatchers.SimilarityFlooding;
import am.matcher.structuralMatchers.SimilarityFloodingParameters;
import am.matcher.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.matcher.structuralMatchers.similarityFlooding.utils.PCGSimilarityMatrix;
import am.matcher.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.matcher.structuralMatchers.similarityFlooding.utils.PairwiseConnectivityGraph;
import am.matcher.structuralMatchers.similarityFlooding.utils.WGraphVertex;
import am.matcher.structuralMatchers.similarityFlooding.utils.WrappingGraph;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class PartialGraphMatcher extends SimilarityFlooding {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674863017457273447L;
	
	private int round = 0;
	
	// intialized together with classes and properties matrix (loadSimilarityMatrix())
	private SimilarityMatrix prevRoundClasses;
	private SimilarityMatrix prevRoundProperties;
	Vector<Double> cOldVect, cNewVect;
	
	/**
	 * 
	 */
	public PartialGraphMatcher() {
		super();
		cOldVect = new Vector<Double>();
		cNewVect = new Vector<Double>();
		sortEdges = false;
	}

	/**
	 * @param params_new
	 */
	public PartialGraphMatcher(SimilarityFloodingParameters params_new) {
		super(params_new);
		cOldVect = new Vector<Double>();
		cNewVect = new Vector<Double>();
		sortEdges = false;
	}
	
	@Override 
	public AbstractMatcherParametersPanel getParametersPanel() { return new SimilarityFloodingParametersPanel(); };
	
	/**
	 * Similarity Flooding Algorithm. 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * NOTE: we are using graphs instead of arrayList
	 */
	@Override
	 protected void align() {

		try {
			for( MatchingProgressListener mpd : progressDisplays ) mpd.clearReport();
			
			// cannot align just one ontology (this is here to catch improper invocations)
			if (sourceOntology == null) throw new NullPointerException("sourceOntology == null");
			if (targetOntology == null) throw new NullPointerException("targetOntology == null");
			
			// starting phase: create wrapping graphs
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Creating Wrapping Graphs...");
			WrappingGraph sourceGraph = new WrappingGraph(sourceOntology);
			WrappingGraph targetGraph = new WrappingGraph(targetOntology);
			if (DEBUG_FLAG) System.out.println(sourceGraph.toString());
			if (DEBUG_FLAG) System.out.println(targetGraph.toString());
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
			
			// load the matrices
			loadSimilarityMatrices(sourceGraph, targetGraph);
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Start Computation...");
			do {
				// new round starts
				round++;
				System.out.println("----------- ROUND #" + round + " ---------------\n");

				// phase 0: CLEAN all the data used before, matrix and WGraph survive and update old matrices
				System.out.println("----------- PHASE:" + round + ".0" + " ---------------");
				pairTable = new HashMap<String, PCGVertex>();
				edgesMap = new HashMap<String, PCGEdge>();
				cOldVect = new Vector<Double>();
				cNewVect = new Vector<Double>();
				updateOldSimValues(prevRoundClasses, classesMatrix);
				updateOldSimValues(prevRoundProperties, propertiesMatrix);

				// phase 1 to 6
				executeRoundOperations(sourceGraph, targetGraph);

				// phase 7: get global max similarity and normalize all values
				System.out.println("----------- PHASE:" + round + ".7" + " ---------------");
				normalizeSimilarities(cNewVect, getGlobalMaxSimilarity(cNewVect));

				//if( DEBUG_FLAG && round == 1 )System.out.println(cOldVect.toString());
				//if( DEBUG_FLAG && round == 1 )System.out.println(cNewVect.toString());

				// phase 8: check stop condition
			} while (!checkStopCondition(round, cOldVect, cNewVect));
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
			
			// phase 9: get back the classesMatrix
			classesMatrix = new ArraySimilarityMatrix(classesMatrix);
			
			// phase 10: compute relative similarities (at the very end)
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Computing Relative Similarities...");
			computeRelativeSimilarities(classesMatrix);
			computeRelativeSimilarities(propertiesMatrix);
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try { fw.close(); } catch (IOException e) { e.printStackTrace(); }
	 }
	 
	private void executeRoundOperations(WrappingGraph s, WrappingGraph t){
		Iterator<WGraphVertex> sLocalItr = s.vertices(), tLocalItr = t.vertices();
		int op = 0;
		
		// until all cells are covered
		while(sLocalItr.hasNext()){
			WGraphVertex sVertex = sLocalItr.next();
			while(tLocalItr.hasNext()){
				WGraphVertex tVertex = tLocalItr.next();
				op++;

				if(sVertex.getNodeType().equals(tVertex.getNodeType())){
					
					// phase 1: get a pcg vertex and create the connected component of the pcg associated to it
					System.out.println("----------- PHASE:" + round + ".1." + op + " ---------------");
					pcg = new PairwiseConnectivityGraph(); // clean out old PCG
					PCGVertex pcgV = getPCGVertex(sVertex, tVertex);
					boolean pcgCreated = createPairwiseConnectivityGraph(pcgV); 
						
					if(pcgCreated){
						
						// phase 2: grab matrix values (to do from the second round on)
						System.out.println("----------- PHASE:" + round + ".2." + op + " ---------------");
						if(round > 1) {grabMatrixValues(pcg.vertices());}
						
						// phase 3: create associated IPG
						System.out.println("----------- PHASE:" + round + ".3." + op + " ---------------");
						createInducedPropagationGraph();

						// phase 4: one round of the fixpoint
						System.out.println("----------- PHASE:" + round + ".4." + op + " ---------------");
						computeFixpointRound(pcg.vertices());
						
						// phase 5: translate results in matrix
						System.out.println("----------- PHASE:" + round + ".5." + op + " ---------------");
						populateSimilarityMatrices(pcg, classesMatrix, propertiesMatrix);
						
						// phase 6: prepare vectors for the delta calculation
						System.out.println("----------- PHASE:" + round + ".6." + op + " ---------------");
						cOldVect.addAll(pcg.getSimValueVector(true));
						cNewVect.addAll(pcg.getSimValueVector(false));
					}
				}
			}
			tLocalItr = t.vertices();
		}
	}
	 
	// PHASE 0: CLEAN all the data used before, matrix and WGraph survive and update old matrices //
	protected void updateOldSimValues(SimilarityMatrix matrixOld, SimilarityMatrix matrixNew) {
		 for(int i = 0; i < matrixOld.getRows(); i++){
			 for(int j = 0; j < matrixOld.getColumns(); j++){
				 
				 Mapping current = matrixOld.get(i, j);
				 if(current != null){
					 current.setSimilarity(matrixNew.get(i,j).getSimilarity());
					 matrixOld.set(i, j, current);
				 }
			 }
		 }
	 }
	
	 // PHASE 1: get a pcg vertex and inserts it in the pcg //
	 @Override
	 protected PCGVertex getPCGVertex(WGraphVertex s, WGraphVertex t){
		 return super.getPCGVertex(s, t);
	 }
	 
	 // PHASE 2: compute pcg graph on that vertex //
	 protected boolean createPairwiseConnectivityGraph(PCGVertex pcgV){
		 return super.createPartialPCG(pcgV);
	 }
	 
	// PHASE 3: grab matrix values	//
	private void grabMatrixValues(Iterator<PCGVertex> iVert) {
		
		while(iVert.hasNext()){
			 
			// take the current vertex
			PCGVertex vert = iVert.next();
			
			// take the RDFNodes associated to vert
			RDFNode s = vert.getObject().getStCouple().getLeft().getObject();
			RDFNode t = vert.getObject().getStCouple().getRight().getObject();
			 
			// take both source and target ontResources (values can be null, means not possible to take resources
			 OntResource sourceRes = NodeUtility.getOntResourceFromRDFNode(s);
			 OntResource targetRes = NodeUtility.getOntResourceFromRDFNode(t);
			 if(sourceRes != null && targetRes != null){
				
				 // try to get the Node and check they belong to the same alignType
				 Node sourceClass = NodeUtility.getNodefromOntResource(sourceOntology, sourceRes, alignType.aligningClasses);
				 Node targetClass = NodeUtility.getNodefromOntResource(targetOntology, targetRes, alignType.aligningClasses);
				 // test if both nodes are classes
				 if(sourceClass == null || targetClass == null){
					 Node sourceProperty = NodeUtility.getNodefromOntResource(sourceOntology, sourceRes, alignType.aligningProperties);
					 Node targetProperty = NodeUtility.getNodefromOntResource(targetOntology, targetRes, alignType.aligningProperties);
					 // test if both nodes are properties
					 if(sourceProperty == null || targetProperty == null){
						 continue;
					 }
					 else{
						 // put the similarity value in the current pcgVertex from the properties matrix
						 vert.getObject().setOldSimilarityValue(propertiesMatrix.getSimilarity(sourceProperty.getIndex(), targetProperty.getIndex()));
					 }
				 }
				 else{
					 // put the similarity value in the current pcgVertex from the properties matrix
					 vert.getObject().setOldSimilarityValue(classesMatrix.getSimilarity(sourceClass.getIndex(), targetClass.getIndex()));
				 }
			 }
			 else{
				 continue;
			 }
			
		 }
	}
		
	 // PHASE 4: one round of the fixpoint 		//
	 @Override
	 protected double computeFixpointRound(Iterator<PCGVertex> iVert){
		 return super.computeFixpointRound(iVert);
	 }
	 
	 // PHASE 5: translate results in matrix	//
	 protected void populateSimilarityMatrices(PairwiseConnectivityGraph pcg, SimilarityMatrix cMatrix, SimilarityMatrix pMatrix){
		 super.populateSimilarityMatrices(pcg, cMatrix, pMatrix);
	}
	 
	 // PHASE 6: get global max similarity		//
	 private double getGlobalMaxSimilarity(Vector<Double> vect){
		 double max = 0;
		 for(int i = 0; i < vect.size(); i++){
			 if(max < vect.get(i)){
				 max = vect.get(i);
			 }
		 }
		 return max;
	 }
	
	 // PHASE 7: normalize all values 			//
	 protected void normalizeSimilarities(Vector<Double> vect, double roundMax){
		 for(int i = 0; i < vect.size(); i++){
			 double max = vect.get(i);
			 vect.set(i, max/roundMax);
		 }
	 }	
	 
	 // INHERITED FUNCTIONS //
	 
	@Override
	protected void loadSimilarityMatrices(WrappingGraph s, WrappingGraph t) {
		// load classesMatrix
		try {
			classesMatrix = new PCGSimilarityMatrix(s, t, alignType.aligningClasses);
			prevRoundClasses = new PCGSimilarityMatrix((PCGSimilarityMatrix) classesMatrix);
		} catch( AMException e ) {
			e.printStackTrace();
		}
		
		// load propertiesMatrix
		try {
			propertiesMatrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, alignType.aligningProperties);
			prevRoundProperties = new ArraySimilarityMatrix(propertiesMatrix);
		} catch (AMException e) {
			e.printStackTrace();
		}
		
//		try {
//			fw.append("classM size" + classesMatrix.getRows() + "-" + classesMatrix.getColumns() + "................\n");
//			fw.append("propM size" + propertiesMatrix.getRows() + "-" + propertiesMatrix.getColumns() + "................\n");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
