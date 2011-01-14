/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Scanner;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.structuralMatchers.SimilarityFlooding;
import am.app.mappingEngine.structuralMatchers.SimilarityFloodingParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphVertex;
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
	 protected void align() throws Exception {
		progressDisplay.clearReport();
		
		// cannot align just one ontology (this is here to catch improper invocations)
		if( sourceOntology == null ) throw new NullPointerException("sourceOntology == null");   
		if( targetOntology == null ) throw new NullPointerException("targetOntology == null");
		
		// loading similarity matrices
		loadSimilarityMatrices();
		
		progressDisplay.appendToReport("Creating Wrapping Graphs...");
		WrappingGraph sourceGraph = new WrappingGraph(sourceOntology.getModel());
		WrappingGraph targetGraph = new WrappingGraph(targetOntology.getModel());
		if( DEBUG_FLAG ) System.out.println(sourceGraph.toString());
		if( DEBUG_FLAG ) System.out.println(targetGraph.toString());
		progressDisplay.appendToReport("done.\n");
		
		if(isSortEdges()){
			progressDisplay.appendToReport("Sorting Wrapping Graphs...");
			sourceGraph.sortEdges();
			targetGraph.sortEdges();
			if( !DEBUG_FLAG ) System.out.println(sourceGraph.toString());
			if( !DEBUG_FLAG ) System.out.println(targetGraph.toString());
			progressDisplay.appendToReport("done.\n");
		}
		
		progressDisplay.appendToReport("Creating Pairwise Connectivity Graph...");
		createFullPCG(sourceGraph, targetGraph);
		if( !DEBUG_FLAG ) {
			System.out.println(pcg);
			Iterator<PCGEdge> iEdge = pcg.edges();
			File f = new File("/home/nikiforos/Desktop/by_connComp");
			FileWriter fw = new FileWriter(f);
			while(iEdge.hasNext()){
				PCGEdge vert = iEdge.next();
				System.out.println(vert);
				fw.append(vert.toString() + "\n");
			}
			fw.close();
		}
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Creating Induced Propagation Graph...");
		createInducedPropagationGraph();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Computing Fixpoints...");
		computeFixpoint();
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Populating Similarity Matrices...");
		populateSimilarityMatrices(pcg, classesMatrix, propertiesMatrix);
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Computing Relative Similarities...");
		computeRelativeSimilarities(classesMatrix);
		computeRelativeSimilarities(propertiesMatrix);
		progressDisplay.appendToReport("done.\n");
		
	 }
	 
	 protected void createFullPCG(WrappingGraph sourceOnt, WrappingGraph targetOnt){
		 //old method
//		 super.createFullPCG(sourceOnt, targetOnt);
		 
		 //new method
		 createPCG(sourceOnt, targetOnt);
	 }

	private void createPCG(WrappingGraph sourceOnt, WrappingGraph targetOnt) {
		Iterator<WGraphVertex> sLocalItr = sourceOnt.vertices();
		Iterator<WGraphVertex> tLocalItr = targetOnt.vertices();

		System.out.println("Start");
		WGraphVertex sVertex = null, tVertex = null;
		// until all cells are covered
		while(sLocalItr.hasNext()){
			sVertex = sLocalItr.next();
			while(tLocalItr.hasNext()){
				tVertex = tLocalItr.next();
				System.out.println("NewPCGVertex");
				if(sVertex.getNodeType().equals(tVertex.getNodeType())){
					createPartialPCG(getPCGVertex(sVertex, tVertex));
					
				}
			}
			tLocalItr = targetOnt.vertices();
		}
		
	}
	 
}
