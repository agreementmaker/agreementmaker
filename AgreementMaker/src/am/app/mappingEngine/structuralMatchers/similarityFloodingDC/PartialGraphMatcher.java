package am.app.mappingEngine.structuralMatchers.similarityFloodingDC;

import java.util.HashMap;
import java.util.Iterator;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.structuralMatchers.SimilarityFlooding;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.SimilarityFloodingMatcherParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.SimilarityFloodingParametersPanel;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdgeData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertexData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PairwiseConnectivityGraph;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;
import am.utility.DirectedGraphEdge;
import am.utility.Pair;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class PartialGraphMatcher extends SimilarityFlooding {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674863017457273447L;
	
	private HashMap<String, PCGEdge> edgesMap;
	private enum EdgeDirection{IN, OUT};

	/**
	 * 
	 */
	public PartialGraphMatcher() {
		super();
		edgesMap = new HashMap<String, PCGEdge>();
	}

	/**
	 * @param params_new
	 */
	public PartialGraphMatcher(SimilarityFloodingMatcherParameters params_new) {
		edgesMap = new HashMap<String, PCGEdge>();
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
		 try{
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
		
		progressDisplay.appendToReport("Start Computation...");
		Iterator<WGraphVertex> sourceGraphIterator = sourceGraph.vertices();
		Iterator<WGraphVertex> targetGraphIterator = targetGraph.vertices();
		WGraphVertex s, t;
		int pairs = 0;
		while(sourceGraphIterator.hasNext()){
			s = sourceGraphIterator.next();
			while(targetGraphIterator.hasNext()){
				
				t = targetGraphIterator.next();

				pairs = startComputation(pairs, s, t);
				
			}
			targetGraphIterator = targetGraph.vertices();
		}
		progressDisplay.appendToReport("done.\n");
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		
	 }
	 
	/**
	 * 
	 */
	 private int startComputation(int pairs, WGraphVertex s, WGraphVertex t) {
		
		boolean pcgCreated = false;
		 
		progressDisplay.appendToReport("Creating Pairwise Connectivity Graph...");
		pcg = new PairwiseConnectivityGraph();
		pcgCreated = createPairwiseConnectivityGraph(s, t);
		pairs++;
		if(pcgCreated && pcg.numEdges() > 0 && pcg.numVertices() > 1){
			
			if( !DEBUG_FLAG ) System.out.println(pcg.toString());
//			progressDisplay.appendToReport("done.\n");
			
//			progressDisplay.appendToReport("Creating Induced Propagation Graph...");
			createInducedPropagationGraph();
			if( DEBUG_FLAG ) System.out.println(pcg.toString());
//			progressDisplay.appendToReport("done.\n");
			
//			progressDisplay.appendToReport("Computing Fixpoints...");
			computeFixpoint();
//			progressDisplay.appendToReport("done.\n");
			
//			progressDisplay.appendToReport("Creating Similarity Matrices...");
			populateSimilarityMatrices();
//			progressDisplay.appendToReport("done.\n");
			
//			progressDisplay.appendToReport("Computing Relative Similarities...");
			computeRelativeSimilarities();
//			progressDisplay.appendToReport("done.\n");
		}
		else{
			progressDisplay.appendToReport("PCG not created. moving to next couple.\n");
		}
		System.out.println("N° of cells filled: " + pairs);
		return pairs;
	 }	 
	 
	 protected boolean createPairwiseConnectivityGraph(WGraphVertex s, WGraphVertex t){
		 
		 String key = new String(s.getObject().toString() + t.getObject().toString());
		 PCGVertex pcgV = getPCGVertex(key, s.getObject(), t.getObject());
			
		 if(pcgV.isVisited()){
			return false;	
		 }
		 else{
			 pcgV.setVisited(true);
			 
			 lookForNodesInEdges(pcgV, s, t, EdgeDirection.IN);
			 lookForNodesInEdges(pcgV, s, t, EdgeDirection.OUT);
			 
			 return true;
		 }

	 }
	 
	 private void lookForNodesInEdges(PCGVertex pcgV, WGraphVertex s, WGraphVertex t, EdgeDirection ed){
		 Iterator<DirectedGraphEdge<String,RDFNode>> sourceIterator = null;
		 Iterator<DirectedGraphEdge<String,RDFNode>> targetIterator = null;
		 Iterator<DirectedGraphEdge<String,RDFNode>> targetStart = null;
		 
		 switch(ed){
		 case IN:
			 sourceIterator = s.edgesInIter();
			 targetIterator = t.edgesInIter();
			 targetStart = t.edgesInIter();
			 break;
		 case OUT:
			 sourceIterator = s.edgesOutIter();
			 targetIterator = t.edgesOutIter();
			 targetStart = t.edgesOutIter();
			 break;
		 default:
			 try {
				throw new Exception("Should not be here. Make sure EdgeDirection is provided and not null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 
		 WGraphEdge sEdge = null;
		 WGraphEdge tEdge = null;
		 String edgeLabel = null;
//		 System.out.println("source: " + sourceIterator.hasNext() + " target: " + targetIterator.hasNext());
		 while(sourceIterator.hasNext()){
			 sEdge = (WGraphEdge) sourceIterator.next();
			 
			 while(targetIterator.hasNext()){
				 tEdge = (WGraphEdge) targetIterator.next();
//				 System.out.println("source: " + sEdge.toString() + " target: " + tEdge.toString());
				 
				 if(sEdge.getObject().equals(tEdge.getObject())){
					 edgeLabel = sEdge.getObject();
					 // in addNewElementsToPCG I'll call the recursive step
					 switch(ed){
					 case IN:
						 addNewElementsToPCG(pcgV, (WGraphVertex) sEdge.getOrigin(), edgeLabel, (WGraphVertex) tEdge.getOrigin(), ed);
						 break;
					 case OUT:
						 addNewElementsToPCG(pcgV, (WGraphVertex) sEdge.getDestination(), edgeLabel, (WGraphVertex) tEdge.getDestination(), ed);
						 break;
					 default:
						 try {
							throw new Exception("Should not be here. Make sure EdgeDirection is provided and not null");
						} catch (Exception e) {
							e.printStackTrace();
						}
					 }
				 }
				 // base case: if failure and nothing is done
			 }
			 targetIterator = targetStart;
		 }
	 }
	 
	private PCGEdge getEdge(PCGVertex pcgV, String edgeLabel, PCGVertex pcgV2) {
		
		PCGEdge edgeNew = edgesMap.get(pcgV.toString() + edgeLabel + pcgV2.toString());
		
		if (edgeNew == null) {
			// we don't have that edge, we create it
			edgeNew = new PCGEdge(pcgV, pcgV2, new PCGEdgeData(edgeLabel));
			edgesMap.put(pcgV.toString() + edgeLabel + pcgV2.toString(), edgeNew);
			return edgeNew;
		}
		else{
			// we already have that edge (it would give a duplicate)
			return null;
		}
	}

	private void addNewElementsToPCG(PCGVertex pcgV, WGraphVertex s, String edgeLabel, WGraphVertex t, EdgeDirection ed) {
		
		String key = new String(s.getObject().toString() + t.getObject().toString());;
		PCGVertex secondPCGVertex = getPCGVertex(key, s.getObject(), t.getObject());
		PCGEdge edge = null;
//		System.out.println(pcgV + " ----- " + secondPCGVertex.toString());
		 
		// insertion
		switch(ed){
		 case IN:
			 if((edge = getEdge(secondPCGVertex, edgeLabel, pcgV)) != null){
				 insertEdgeInPCG(secondPCGVertex, edge, pcgV);
			 }
			 break;
		 case OUT:
			 if((edge = getEdge(pcgV, edgeLabel, secondPCGVertex)) != null){
				 insertEdgeInPCG(pcgV, edge, secondPCGVertex);
			 }
			 break;
		 default:
			 try {
				throw new Exception("Should not be here. Make sure EdgeDirection is provided and not null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		
		// recursive step
		createPairwiseConnectivityGraph(s, t);
		
	}

	@Override
	protected void loadSimilarityMatrices() {
		// load classesMatrix
		classesMatrix = new SimilarityMatrix(sourceOntology.getClassesList().size(),
				targetOntology.getClassesList().size(),
				alignType.aligningClasses);
		// load propertiesMatrix
		propertiesMatrix = new SimilarityMatrix(sourceOntology.getPropertiesList().size(),
				targetOntology.getPropertiesList().size(),
				alignType.aligningProperties);
	}
	
	

	@Override
	protected PCGVertexData selectInput(Pair<RDFNode, RDFNode> pair) {
		// TODO Auto-generated method stub
		return null;
	}
}
