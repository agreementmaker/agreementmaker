/**
 * 
 */
package am.matcher.structuralMatchers.similarityFlooding;

import java.util.Iterator;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.MatchingProgressListener;
import am.matcher.structuralMatchers.SimilarityFlooding;
import am.matcher.structuralMatchers.SimilarityFloodingParameters;
import am.matcher.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.matcher.structuralMatchers.similarityFlooding.utils.WGraphVertex;
import am.matcher.structuralMatchers.similarityFlooding.utils.WrappingGraph;

/**
 * 
 */
public abstract class FullGraphMatcher extends SimilarityFlooding {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7177905740269241391L;

	/**
	 * 
	 */
	public FullGraphMatcher() {
		super();
		needsParam = true; // we need to display the parameters panel.
	}

	/**
	 * @param params_new
	 */
	public FullGraphMatcher(SimilarityFloodingParameters params_new) {
		super(params_new);
		needsParam = true; // we need to display the parameters panel.
	}
	
	@Override 
	public AbstractMatcherParametersPanel getParametersPanel() { return new SimilarityFloodingParametersPanel(); };
	
	/**
	 * Similarity Flooding Algorithm. 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * NOTE: we are using graphs instead of arrayList
	 */
	 @Override
	 protected void align() throws Exception {
		for( MatchingProgressListener mpd : progressDisplays ) mpd.clearReport();
		
		// cannot align just one ontology (this is here to catch improper invocations)
		if( sourceOntology == null ) throw new NullPointerException("sourceOntology == null");   
		if( targetOntology == null ) throw new NullPointerException("targetOntology == null");
		
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Creating Wrapping Graphs...");
		WrappingGraph sourceGraph = new WrappingGraph(sourceOntology);
		WrappingGraph targetGraph = new WrappingGraph(targetOntology);
		if( DEBUG_FLAG ) System.out.println(sourceGraph.toString());
		if( DEBUG_FLAG ) System.out.println(targetGraph.toString());
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// loading similarity matrices (null values are permitted if WrappingGraphs are not used to build the Matrices)
		loadSimilarityMatrices(null, null);
		
		// PHASE 0: sorting edges (for optimization purposes)
		if(isSortEdges()){
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Sorting Wrapping Graphs...");
			sourceGraph.sortEdges();
			targetGraph.sortEdges();
			if( DEBUG_FLAG ) System.out.println(sourceGraph.toString());
			if( DEBUG_FLAG ) System.out.println(targetGraph.toString());
			for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
		}
		
		// PHASE 1: creating PCG
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Creating Pairwise Connectivity Graph...");
		createFullPCG(sourceGraph, targetGraph);
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 2: creating IPG
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Creating Induced Propagation Graph...");
		createInducedPropagationGraph();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 3: computing fixpoint
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Computing Fixpoints...");
		computeFixpoint();
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 4: update values in matrix
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Populating Similarity Matrices...");
		populateSimilarityMatrices(pcg, classesMatrix, propertiesMatrix);
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 5: compute relative similarities
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("Computing Relative Similarities...");
		computeRelativeSimilarities(classesMatrix);
		computeRelativeSimilarities(propertiesMatrix);
		for( MatchingProgressListener mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
//		try {
//			fw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	 }
	 
	 protected void createFullPCG(WrappingGraph sourceOnt, WrappingGraph targetOnt){
		 //old method
		 super.createFullPCG(sourceOnt, targetOnt);
		 
		 //new method
//		 createPCG(sourceOnt, targetOnt);
		
	 }

//	@SuppressWarnings("unused")
	private void createPCG(WrappingGraph sourceOnt, WrappingGraph targetOnt) {
		Iterator<WGraphVertex> sLocalItr = sourceOnt.vertices();
		Iterator<WGraphVertex> tLocalItr = targetOnt.vertices();

		// until all cells are covered
		while(sLocalItr.hasNext()){
			WGraphVertex sVertex = sLocalItr.next();
			while(tLocalItr.hasNext()){
				WGraphVertex tVertex = tLocalItr.next();
				
				if(sVertex.getNodeType().equals(tVertex.getNodeType())){
					createPartialPCG(getPCGVertex(sVertex, tVertex));
				}
			}
			tLocalItr = targetOnt.vertices();
		}
	}
	
	protected void computeFixpoint(){
		 int round = 0;
		 Vector<Double> oldV , newV;
		 do {
			 // new round starts
			 round++;
			 
			 // update old value with new value of previous round
			 updateOldSimValues(pcg.vertices(), round);
			 
			 // compute fixpoint round and max value per that round
			 double maxSimilarity = computeFixpointRound(pcg.vertices());
			 
			 // normalize all the similarity values of all nodes
			 normalizeSimilarities(pcg.vertices(), maxSimilarity);

			 // stop condition check: delta or maxRound

			 oldV = pcg.getSimValueVector(true);
			 newV = pcg.getSimValueVector(false);

//			 try {
//					fw.append("old: " + oldV.size() + "\n");
//					fw.append("new: " + newV.size() + "\n");
//				} catch (IOException e) {
//					e.printStackTrace();
//				}

		 } while(!checkStopCondition(round, oldV, newV));
	 }
	
	private void updateOldSimValues(Iterator<PCGVertex> iVert, int round){
		 if(round != 1){
			 while(iVert.hasNext()){
				 // take the current vertex
				 PCGVertex vert = iVert.next();
				 vert.getObject().setOldSimilarityValue(vert.getObject().getNewSimilarityValue());
			 }
		 }
	 }
	 
	protected void normalizeSimilarities(Iterator<PCGVertex> iVert, double roundMax){
		 while(iVert.hasNext()){
			 PCGVertex currentVert = iVert.next();
			 
			 // the value computed is stored in the new similarity value at this stage
			 double nonNormSimilarity = currentVert.getObject().getNewSimilarityValue();
			 
			 // compute normalized value
			 double normSimilarity = nonNormSimilarity / roundMax;
			 
			 // set normalized to new value
			 currentVert.getObject().setNewSimilarityValue(normSimilarity);
		 }
	 }
	 
}
