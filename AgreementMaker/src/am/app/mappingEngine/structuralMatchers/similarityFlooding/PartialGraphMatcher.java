package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.structuralMatchers.SimilarityFlooding;
import am.app.mappingEngine.structuralMatchers.SimilarityFloodingParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGEdgeData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PCGVertexData;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.PairwiseConnectivityGraph;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;
import am.app.ontology.Node;
import am.utility.DirectedGraphEdge;
import am.utility.Pair;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class PartialGraphMatcher extends SimilarityFlooding {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674863017457273447L;
	
	private int round = 0;
	
	// intialized when initialized the classes and properties matrix (loadSimilarityMatrix())
	private SimilarityMatrix prevRoundClasses;
	private SimilarityMatrix prevRoundProperties;
	
	/**
	 * 
	 */
	public PartialGraphMatcher() {
		super();
	}

	/**
	 * @param params_new
	 */
	public PartialGraphMatcher(SimilarityFloodingParameters params_new) {
		super(params_new);
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

		progressDisplay.clearReport();
		
		// cannot align just one ontology (this is here to catch improper invocations)
		if( sourceOntology == null ) throw new NullPointerException("sourceOntology == null");   
		if( targetOntology == null ) throw new NullPointerException("targetOntology == null");

		// load the matrices
		loadSimilarityMatrices();
		
		// starting phase: create wrapping graphs
		progressDisplay.appendToReport("Creating Wrapping Graphs...");
		WrappingGraph sourceGraph = new WrappingGraph(sourceOntology.getModel());
		WrappingGraph targetGraph = new WrappingGraph(targetOntology.getModel());
		if( !DEBUG_FLAG ) System.out.println(sourceGraph.toString());
		if( !DEBUG_FLAG ) System.out.println(targetGraph.toString());
		progressDisplay.appendToReport("done.\n");
		
		progressDisplay.appendToReport("Start Computation...");

		Vector<Double> cOldVect, cNewVect;
		do{
			// new round starts
			round++;
			if( round == 2 )System.out.println("----------- ROUND #" + round + " ---------------\n");
			
			// phase 0: CLEAN all the data used before, matrix and WGraph survive and update old matrices
//			if( round == 1 )System.out.println("----------- PHASE:" + round + ".0" + " ---------------");
			pairTable = new HashMap<String, PCGVertex>();
			edgesMap = new HashMap<String, PCGEdge>();
			updateOldSimValues(prevRoundClasses, classesMatrix, round);
			updateOldSimValues(prevRoundProperties, propertiesMatrix, round);			
			
			// phase 1 to 5
			executeRoundOperations(sourceGraph, targetGraph);
			
			// phase 6: get global max similarity
//			if( round == 1 )System.out.println("----------- PHASE:" + round + ".6" + " ---------------");
			double roundMax = getGlobalMaxSimilarity(classesMatrix, propertiesMatrix);
			
			// phase 7: normalize all values
//			if( round == 1 )System.out.println("----------- PHASE:" + round + ".7" + " ---------------");
			normalizeSimilarities(classesMatrix, roundMax);
			normalizeSimilarities(propertiesMatrix, roundMax);
			
			// phase 8: prepare Vectors for delta check
//			if( round == 1 )System.out.println("----------- PHASE:" + round + ".8" + " ---------------");
			Vector<Double> pVect = prevRoundProperties.toSimilarityArray(prevRoundProperties.toMappingArray());
			
			cOldVect = prevRoundClasses.toSimilarityArray(prevRoundClasses.toMappingArray());
			cOldVect.addAll(pVect);
			
			pVect = propertiesMatrix.toSimilarityArray(propertiesMatrix.toMappingArray());
			
			cNewVect = classesMatrix.toSimilarityArray(classesMatrix.toMappingArray());
			cNewVect.addAll(pVect);
			
			try {
				fw.append(cOldVect.size() + "\n");
				fw.append(cNewVect.size() + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			if( round == 1 )System.out.println(cOldVect.toString());
//			if( round == 1 )System.out.println(cNewVect.toString());
		} while(!checkStopCondition(round, cOldVect, cNewVect));
		progressDisplay.appendToReport("done.\n");
		// until delta less then value
		
		// phase 9: compute relative similarities (at the very end)
		progressDisplay.appendToReport("Computing Relative Similarities...");
		computeRelativeSimilarities(classesMatrix);
		computeRelativeSimilarities(propertiesMatrix);
		progressDisplay.appendToReport("done.\n");
		
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 }
	 
	private void executeRoundOperations(WrappingGraph s, WrappingGraph t){
		Iterator<WGraphVertex> sLocalItr = s.vertices();
		Iterator<WGraphVertex> tLocalItr = t.vertices();

		WGraphVertex sVertex = null, tVertex = null;
		// until all cells are covered
		int sInd = 0, tInd = 0, pcgSize = 0;
		while(sLocalItr.hasNext()){
			sVertex = sLocalItr.next();
			sInd++;
			while(tLocalItr.hasNext()){
				tInd++;
				tVertex = tLocalItr.next();
//				if( round == 1 ) System.out.println(sVertex + ": " + sVertex.getObject().getClass() + " "+ tVertex + ": " + tVertex.getObject().getClass());
//				if( round == 1 ) System.out.println(sVertex + ": " + sVertex.getNodeType() + " "+ tVertex + ": " + tVertex.getNodeType());

				if(sVertex.getNodeType().equals(tVertex.getNodeType())){
					
					// phase 1: get a pcg vertex and inserts it in the pcg
//					System.out.println("----------- PHASE:" + round + ".1." + sInd + "." + tInd + " ---------------");
					PCGVertex pcgV = getPCGVertex(sVertex, tVertex);
					pcg = new PairwiseConnectivityGraph(); // clean out old PCG
					if(round == 2 && pcg.numVertices() > 0) System.out.println();

					// phase 2: compute pcg graph on that vertex
//					System.out.println("----------- PHASE:" + round + ".2." + sInd + "." + tInd + " ---------------");
					boolean pcgCreated = createPairwiseConnectivityGraph(pcgV); 
						
					if(pcgCreated && pcg.numVertices() > 0){
						
						pcgSize += pcg.numVertices();
						System.out.print("-" + pcgSize + "-");
						try {
							fw.append("-" + pcgSize + "-");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(round == 1) {
							populateSimilarityMatrices(pcg, classesMatrix, propertiesMatrix);
							populateSimilarityMatrices(pcg, prevRoundClasses, prevRoundProperties);
						}
						
						// phase 3: grab matrix values
//						if( round == 1 )System.out.println("----------- PHASE:" + round + ".3." + sInd + "." + tInd + " ---------------");
						grabMatrixValues(pcg.vertices());
						createInducedPropagationGraph();

						// phase 4: one round of the fixpoint
//						if( round == 1 )System.out.println("----------- PHASE:" + round + ".4." + sInd + "." + tInd + " ---------------");
						computeFixpointRound(pcg.vertices());
						
						// phase 5: translate results in matrix
//						if( round == 1 )System.out.println("----------- PHASE:" + round + ".5." + sInd + "." + tInd + " ---------------");
						populateSimilarityMatrices(pcg, classesMatrix, propertiesMatrix);
					}
				}
			}
			tLocalItr = t.vertices();
		}
		 try {
				fw.append("----------------------------------------------> " + new Integer(pcgSize).toString()+ "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	 
	/* private void performEdgesLookup(PCGVertex pcgV, EdgeDirection ed){
	
		 // WGraphEdges iterators
		 Iterator<DirectedGraphEdge<String,RDFNode>> sourceIterator = null;
		 Iterator<DirectedGraphEdge<String,RDFNode>> targetIterator = null;
		 
		 // WGraphVertices for edges lookup
		 WGraphVertex s = pcgV.getObject().getStCouple().getLeft();
		 WGraphVertex t = pcgV.getObject().getStCouple().getRight();
		 
		 switch(ed){
		 case IN:
			 sourceIterator = s.edgesInIter();
			 targetIterator = t.edgesInIter();
			 break;
		 case OUT:
			 sourceIterator = s.edgesOutIter();
			 targetIterator = t.edgesOutIter();
			 break;
		 default:
			 try {
				throw new Exception("Should not be here. Make sure EdgeDirection is provided and not null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }

		 WGraphEdge sEdge = null, tEdge = null;
		 String edgeLabel = null;
		 
		 // Looking for nodes and edges to add to the PCG
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
			 targetIterator = t.edgesOutIter();
		 }
	 }*/
	 
/*	 private void addNewElementsToPCG(PCGVertex pcgV, WGraphVertex s, String edgeLabel, WGraphVertex t, EdgeDirection ed) {
			
			PCGVertex secondPCGVertex = getPCGVertex(s, t);
			PCGEdge edge = null;
//			System.out.println(pcgV + " ----- " + secondPCGVertex.toString());
			 
			// insertion
			switch(ed){
			 case IN:
				 if((edge = getEdge(secondPCGVertex, edgeLabel, pcgV)) != null){
					 pcg.insertEdge(secondPCGVertex, edge, pcgV);
				 }
				 break;
			 case OUT:
				 if((edge = getEdge(pcgV, edgeLabel, secondPCGVertex)) != null){
					 pcg.insertEdge(pcgV, edge, secondPCGVertex);
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
			createPairwiseConnectivityGraph(secondPCGVertex);
			
		}
	 
		protected PCGEdge getEdge(PCGVertex pcgV, String edgeLabel, PCGVertex pcgV2) {
			
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
		}*/
	 
		// PHASE 3: grab matrix values	//
		private void grabMatrixValues(Iterator<PCGVertex> iVert) {
			
			PCGVertex vert = null;
			RDFNode s, t;
			while(iVert.hasNext()){
				 
				// take the current vertex
				vert = iVert.next();
				
				// take the RDFNodes associated to vert
				s = vert.getObject().getStCouple().getLeft().getObject();
				t = vert.getObject().getStCouple().getRight().getObject();
				 
				// take both source and target ontResources (values can be null, means not possible to take resources
				 OntResource sourceRes = getOntResourceFromRDFNode(s);
				 OntResource targetRes = getOntResourceFromRDFNode(t);
				 if(sourceRes != null && targetRes != null){
					
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
			 Iterator<PCGVertex> iVert = pcg.vertices();
			 PCGVertex currentVert = null;
			 while(iVert.hasNext()){
				 currentVert = iVert.next();
				 
				 // take both source and target ontResources (values can be null, means not possible to take resources
				 OntResource sourceRes = getOntResourceFromRDFNode(currentVert.getObject().getStCouple().getLeft().getObject());
				 OntResource targetRes = getOntResourceFromRDFNode(currentVert.getObject().getStCouple().getRight().getObject());
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
							 if(pMatrix.get(sourceProperty.getIndex(), targetProperty.getIndex()) != null){
								 m = new Mapping(sourceProperty, targetProperty, currentVert.getObject().getNewSimilarityValue());
							 }
							 else{
								 m = new Mapping(sourceProperty, targetProperty, 1.0);
							 }
							 pMatrix.set(sourceProperty.getIndex(), targetProperty.getIndex(), m);
						 }
					 }
					 else{
						 // the necessary similarity value is stored in the newSimilarityValue var
						 if(cMatrix.get(sourceClass.getIndex(), targetClass.getIndex()) != null){
							 m = new Mapping(sourceClass, targetClass, currentVert.getObject().getNewSimilarityValue());
						 }
						 else{
							 m = new Mapping(sourceClass, targetClass, 1.0);
						 }
						 cMatrix.set(sourceClass.getIndex(), targetClass.getIndex(), m);
					 }
				 }
				 else{
					 continue;
				 }
			 }
		}
		 
		 // PHASE 6: get global max similarity		//
		 private double getGlobalMaxSimilarity(SimilarityMatrix c, SimilarityMatrix p){
			 return Math.max(c.getMaxValue(), p.getMaxValue());
		 }
		
		 // PHASE 7: normalize all values 			//
		 protected void normalizeSimilarities(SimilarityMatrix localMatrix, double roundMax) {
			 super.normalizeSimilarities(localMatrix, roundMax);
		 }	
		 
		 // INHERITED FUNCTIONS //
		 
		@Override
		protected void loadSimilarityMatrices() {
			// load classesMatrix
			classesMatrix = new ArraySimilarityMatrix(sourceOntology.getClassesList().size(),
					targetOntology.getClassesList().size(),
					alignType.aligningClasses);
//			classesMatrix.fillMatrix(1.0, sourceOntology.getClassesList(), targetOntology.getClassesList());
			prevRoundClasses = new ArraySimilarityMatrix(classesMatrix);
			// load propertiesMatrix
			propertiesMatrix = new ArraySimilarityMatrix(sourceOntology.getPropertiesList().size(),
					targetOntology.getPropertiesList().size(),
					alignType.aligningProperties);
//			propertiesMatrix.fillMatrix(1.0, sourceOntology.getPropertiesList(), targetOntology.getPropertiesList());
			prevRoundProperties = new ArraySimilarityMatrix(propertiesMatrix);
		}
		
		
	
		@Override
		protected PCGVertexData selectInput(Pair<RDFNode, RDFNode> pair) {
			// TODO Auto-generated method stub
			return null;
		}
}
