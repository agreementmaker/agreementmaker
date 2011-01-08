/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.structuralMatchers.SimilarityFlooding;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;

/**
 * 
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
	public FullGraphMatcher(SimilarityFloodingMatcherParameters params_new) {
//		super(params_new);
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
		if( !DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Computing Fixpoints...");
		computeFixpoint();
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Creating Similarity Matrices...");
		populateSimilarityMatrices();
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Computing Relative Similarities...");
		computeRelativeSimilarities();
		progressDisplay.appendToReport("done.\n");
		
	 }
	 
}
