/**
 * 
 */
package am.app.mappingEngine.structuralMatchers.similarityFlooding;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.SimilarityMatrix;
import am.utility.DirectedGraph;
import am.utility.DirectedGraphEdge;
import am.utility.DirectedGraphVertex;
import am.utility.Pair;

/**
 * @author Michele Caci
 *
 */
public class SimilarityFloodingMatcher extends AbstractMatcher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3749229483504509029L;

	private boolean hasInput;
	private DirectedGraph< DirectedGraphEdge<Property>,
							DirectedGraphVertex< Pair<RDFNode, RDFNode> >
						  > pairwiseConnectivityGraph;
	
	/**
	 * 
	 */
	public SimilarityFloodingMatcher() {
		super();
		setHasInput(false);
		loadSimilarityMatrices();
		pairwiseConnectivityGraph = new DirectedGraph<DirectedGraphEdge<Property>, DirectedGraphVertex<Pair<RDFNode,RDFNode>>>();
	}

	/**
	 * @param params_new
	 */
	public SimilarityFloodingMatcher(AbstractParameters params_new) {
		super(params_new);
	}
	
	/**
	 * Overridden method 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * @author michele
	 * NOTE: we are using graphs instead of arrayList
	 */
	 protected void align() throws Exception {
		 createPairwiseConnectivityGraph();
    	/*
    	if( sourceOntology == null || targetOntology == null ) return;  // cannot align just one ontology 
    	
		if(alignClass && !this.isCancelled() ) {
			ArrayList<Node> sourceClassList = sourceOntology.getClassesList();
			ArrayList<Node> targetClassList = targetOntology.getClassesList();
			classesMatrix = alignClasses(sourceClassList,targetClassList );	
			//classesMatrix.show();
		}
		if(alignProp && !this.isCancelled() ) {
			ArrayList<Node> sourcePropList = sourceOntology.getPropertiesList();
			ArrayList<Node> targetPropList = targetOntology.getPropertiesList();
			propertiesMatrix = alignProperties(sourcePropList, targetPropList );					
		}
		*/
	 }
	 
	 protected void createPairwiseConnectivityGraph(){
		 OntModel localSource = sourceOntology.getModel();
		 OntModel localTarget = targetOntology.getModel();
		 
		 StmtIterator sStmtIterator = localSource.listStatements();
		 StmtIterator tStmtIterator = localTarget.listStatements();
		 
		 Statement sStmt = null;
		 Statement tStmt = null;
		 
		 while(sStmtIterator.hasNext()){
			 sStmt = sStmtIterator.next();
			 
			 while(tStmtIterator.hasNext()){
				 tStmt = tStmtIterator.next();
				 
				 // condition where we add a new element in the pairwise connectivity graph:
				 // comparison of predicates
				 if(sStmt.getPredicate().equals(tStmt.getPredicate())){
					 insertInPCG(new Pair<RDFNode, RDFNode>(sStmt.getSubject(), tStmt.getSubject()),
							 		sStmt.getPredicate(),
							 		new Pair<RDFNode, RDFNode>(sStmt.getObject(), tStmt.getObject())
							 	);
				 }
				 
			 }
			 tStmtIterator = localTarget.listStatements();
		 }
		 
		 pairwiseConnectivityGraph.vertices();
		 System.out.println(pairwiseConnectivityGraph.toString());
	 }
	 
	 /* *************************************************** */
	 /* 			   MATCHING STEP OPERATIONS				*/
	 /* *************************************************** */
	 
	 /**
	  * 
	  */
	 private void insertInPCG(Pair<RDFNode, RDFNode> sourcePair,
			 					Property linkingPred,
			 					Pair<RDFNode, RDFNode> targetPair){
		 
		 DirectedGraphVertex<Pair<RDFNode,RDFNode>> sourceVertex = new DirectedGraphVertex<Pair<RDFNode,RDFNode>>(sourcePair);
		 DirectedGraphVertex<Pair<RDFNode,RDFNode>> targetVertex = new DirectedGraphVertex<Pair<RDFNode,RDFNode>>(targetPair);
		 // problem in dealing with edges
		 DirectedGraphEdge<Property> pairEdge = null;
		 // TODO: new DirectedGraphEdge<Property>(sourcePair);
		 
		 // pairwiseConnectivityGraph is a field of the class so far
		 pairwiseConnectivityGraph.insertVertex(sourceVertex);
		 pairwiseConnectivityGraph.insertVertex(targetVertex);
		 pairwiseConnectivityGraph.insertEdge(pairEdge);
	 }

	/* *************************************************** */
	/* 					  SUPPORT FUNCTIONS 			   */
	/* *************************************************** */
	
	/**
	 * this function has to be called as soon as possible because this matcher needs some sort of input
	 */
	private void loadSimilarityMatrices(){
		if(this.hasInput){
			classesMatrix = inputMatchers.get(0).getClassesMatrix();
			propertiesMatrix = inputMatchers.get(0).getPropertiesMatrix();
		}
		else{
			// load classesMatrix
			if(classesMatrix != null){
				classesMatrix.fillMatrix(1.0);
			}
			else{
				classesMatrix = new SimilarityMatrix(getSourceOntology().getClassesList().size(),
						getTargetOntology().getClassesList().size(),
						alignType.aligningClasses,
						1.0);
			}
			// load propertiesMatrix
			if(propertiesMatrix != null){
				propertiesMatrix.fillMatrix(1.0);
			}
			else{
				classesMatrix = new SimilarityMatrix(getSourceOntology().getPropertiesList().size(),
						getTargetOntology().getPropertiesList().size(),
						alignType.aligningClasses,
						1.0);
			}
		}
	}

	/**
	 * @return the hasInput
	 */
	public boolean getHasInput() {
		return hasInput;
	}

	/**
	 * @param hasInput the hasInput value to set
	 */
	public void setHasInput(boolean hasInput) {
		this.hasInput = hasInput;
	}

}
